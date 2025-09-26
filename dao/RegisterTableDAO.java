package dao;

import java.sql.*;
import java.util.*;

public class RegisterTableDAO extends DAO {

    // Lấy lịch làm việc tuần
    public Map<String, Map<String, Boolean>> getWeeklySchedule(int staffId, String startDate, String endDate) {
        String sql = "SELECT "
                + "d.name AS WorkDate, "
                + "MAX(CASE WHEN sh.name = 'Ca 1' THEN CASE WHEN sh.isCheck = 1 THEN 1 ELSE 0 END END) AS Ca1, "
                + "MAX(CASE WHEN sh.name = 'Ca 2' THEN CASE WHEN sh.isCheck = 1 THEN 1 ELSE 0 END END) AS Ca2 "
                + "FROM tblstaff s "
                + "JOIN tblregistertable rt ON s.ID = rt.staffID "
                + "JOIN tbldate d ON d.registertableID = rt.ID "
                + "JOIN tblshift sh ON sh.dateID = d.ID "
                + "WHERE s.ID = ? "
                + "AND d.name BETWEEN ? AND ? "
                + "GROUP BY d.name "
                + "ORDER BY d.name;";

        Map<String, Map<String, Boolean>> schedule = new TreeMap<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setString(2, startDate);
            ps.setString(3, endDate);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String date = rs.getString("WorkDate");
                boolean ca1 = rs.getInt("Ca1") == 1;
                boolean ca2 = rs.getInt("Ca2") == 1;

                Map<String, Boolean> shifts = new HashMap<>();
                shifts.put("Ca 1", ca1);
                shifts.put("Ca 2", ca2);

                schedule.put(date, shifts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedule;
    }

    // Cập nhật isCheck, có kiểm tra và thêm mới nếu cần
    public boolean updateShiftCheck(int staffId, String date, String shiftName, boolean isCheck) {
        System.out.println("Start updateShiftCheck: staffId=" + staffId + ", date=" + date + ", shiftName=" + shiftName
                + ", isCheck=" + isCheck);

        try {
            con.setAutoCommit(false);
            System.out.println("Step 1: getOrCreateRegisterTableId");
            int registerTableID = getOrCreateRegisterTableId(staffId);
            System.out.println("✓ Got registerTableID = " + registerTableID);

            System.out.println("Step 2: getOrCreateDateId");
            int dateId = getOrCreateDateId(registerTableID, date);
            System.out.println("✓ Got dateId = " + dateId);

            System.out.println("Step 3: getOrCreateShiftId");
            int shiftId = getOrCreateShiftId(dateId, shiftName);
            System.out.println("✓ Got shiftId = " + shiftId);

            System.out.println("Step 4: Updating isCheck...");
            String updateSql = "UPDATE tblshift SET isCheck = ? WHERE ID = ?";
            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setBoolean(1, isCheck);
                ps.setInt(2, shiftId);
                ps.executeUpdate();
                System.out.println("✓ Update success.");
            }

            con.commit();
            return true;
        } catch (Exception e) {
            System.err.println(" Exception occurred:");
            e.printStackTrace();
            try {
                con.rollback();
                System.err.println("→ Rolled back.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Tạo hoặc lấy registertable
    private int getOrCreateRegisterTableId(int staffId) throws SQLException {
        String sql = "SELECT ID FROM tblregistertable WHERE staffID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("ID");
        }

        sql = "INSERT INTO tblregistertable (staffID, name, des) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, staffId);
            ps.setString(2, "Lịch tự động");
            ps.setString(3, "Sinh từ EditScheduleFrm");
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
        }

        throw new SQLException("Không thể tạo registertable.");
    }

    // Tạo hoặc lấy dateId (tbldate)
    private int getOrCreateDateId(int registerTableId, String date) throws SQLException {
        String sql = "SELECT ID FROM tbldate WHERE registertableID = ? AND name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, registerTableId);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("ID");
        }

        sql = "INSERT INTO tbldate (registertableID, name, des) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, registerTableId);
            ps.setString(2, date);
            ps.setString(3, "Ngày tự tạo");
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
        }

        throw new SQLException("Không thể tạo ngày.");
    }

    // Tạo hoặc lấy shiftId (tblshift)
    private int getOrCreateShiftId(int dateId, String shiftName) throws SQLException {
        String sql = "SELECT ID FROM tblshift WHERE dateID = ? AND name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dateId);
            ps.setString(2, shiftName);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("ID");
        }

        sql = "INSERT INTO tblshift (dateID, name, isCheck) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, dateId);
            ps.setString(2, shiftName);
            ps.setBoolean(3, false);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
        }

        throw new SQLException("Không thể tạo ca làm.");
    }
}
