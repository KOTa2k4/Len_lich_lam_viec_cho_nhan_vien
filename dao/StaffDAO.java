package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Staff;

public class StaffDAO extends DAO {

	public StaffDAO() {
		super();
	}

	/**
	 * search all rooms in the tblRoom whose name contains the @key
	 * 
	 * @param key
	 * @return list of room whose name contains the @key
	 */
	public ArrayList<Staff> searchRoom(String key) {
		ArrayList<Staff> result = new ArrayList<Staff>();
		String sql = "SELECT * FROM tblstaff WHERE name LIKE ?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, "%" + key + "%");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Staff rm = new Staff();
				rm.setId(rs.getInt("id"));
				rm.setName(rs.getString("name"));
				rm.setType(rs.getString("email"));
				rm.setDes(rs.getString("phoneNumber"));
				result.add(rm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * update the @Staff
	 * 
	 * @param rm
	 */
	public boolean updateRoom(Staff rm) {
		String sql = "UPDATE tblstaff SET name=?, email=?, phoneNumber=? WHERE id=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, rm.getName());
			ps.setString(2, rm.getType());
			ps.setString(3, rm.getDes());
			ps.setInt(4, rm.getId());

			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Search available rooms in the period from @checkin to @checkout
	 * 
	 * @param checkin
	 * @param checkout
	 * @return
	 */
	{
		/*
		 * public ArrayList<Room> searchFreeRoom(Date checkin, Date checkout) {
		 * ArrayList<Room> result = new ArrayList<Room>();
		 * String sql =
		 * "SELECT * FROM tblstaff WHERE id NOT IN (SELECT idroom FROM tblBookedRoom WHERE checkout > ? AND checkin < ?)"
		 * ;
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * try {
		 * PreparedStatement ps = con.prepareStatement(sql);
		 * ps.setString(1, sdf.format(checkin));
		 * ps.setString(2, sdf.format(checkout));
		 * ResultSet rs = ps.executeQuery();
		 * 
		 * while (rs.next()) {
		 * Room rm = new Room();
		 * rm.setId(rs.getInt("id"));
		 * rm.setName(rs.getString("name"));
		 * rm.setType(rs.getString("type"));
		 * rm.setPrice(rs.getFloat("price"));
		 * rm.setDes(rs.getString("des"));
		 * result.add(rm);
		 * }
		 * } catch (Exception e) {
		 * e.printStackTrace();
		 * }
		 * return result;
		 * }
		 */}
}
