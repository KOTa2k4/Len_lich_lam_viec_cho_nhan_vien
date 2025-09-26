package model;

public class Shift {
    private int staffId;
    private String date; // yyyy-MM-dd
    private String shiftName; // "Ca 1", "Ca 2"
    private boolean isCheck;

    public Shift(int staffId, String date, String shiftName, boolean isCheck) {
        this.staffId = staffId;
        this.date = date;
        this.shiftName = shiftName;
        this.isCheck = isCheck;
    }

    // Getters & Setters
    public int getStaffId() {
        return staffId;
    }

    public String getDate() {
        return date;
    }

    public String getShiftName() {
        return shiftName;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
