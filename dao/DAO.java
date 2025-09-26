package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {
	public static Connection con;

	public DAO() {
		if (con == null) {
			String dbUrl = "jdbc:mysql://localhost:3306/hotel_case_study?autoReconnect=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
			String dbClass = "com.mysql.cj.jdbc.Driver";
			String username = "root";
			String password = "123456";

			try {
				Class.forName(dbClass);
				con = DriverManager.getConnection(dbUrl, username, password);
				System.out.println("Database connected successfully.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeConnection() {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
