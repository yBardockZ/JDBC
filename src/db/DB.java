package db;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {
	
	private static Connection conn = null;
	
	public static Connection getConnection() {
		if (conn != null) {
			return conn;
		}
		
		else {
			
			try {
				
				Properties props = new Properties();
				FileInputStream fileInputStream = new FileInputStream(new File("db.properties"));
				
				props.load(fileInputStream);
				
				conn = DriverManager.getConnection(props.getProperty("dburl"), props.getProperty("user"),
						props.getProperty("password"));
				
			} catch (SQLException e) {
				throw new DBException(e.getMessage());
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
			
			return conn;
			
		}
		
	}
	
	public static void closeConnection() {
		try {
			conn.close();
			
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	public static void closeStatement(Statement statement) {
		try {
			statement.close();
			
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void closeResultSet(ResultSet resultSet) {
		try {
			resultSet.close();
			
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
