package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbIntegrityException;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDAO {
	
	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
		
	}

	@Override
	public void insert(Department department) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement("INSERT INTO department (name) VALUES (?);", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, department.getName());
			
			int rowsAffected = ps.executeUpdate();
			
			if (rowsAffected > 0) {
				rs = ps.getGeneratedKeys();
				rs.first();
				System.out.println("Done!, rowsAffected : " + rowsAffected + ", Generated key: " + rs.getInt(1));
			}
			
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
		
		
	}

	@Override
	public void update(Department department) {
		PreparedStatement ps = null;
		
		try {
			
			conn.setAutoCommit(false);
			
			ps = conn.prepareStatement("UPDATE department"
					+ " SET id = ?, name = ?"
					+ " WHERE id = ?;");
			
			ps.setInt(1, department.getId());
			ps.setString(2, department.getName());
			ps.setInt(3, department.getId());
			
			int rowsAffected = ps.executeUpdate();
			
			conn.commit();
			
			if (rowsAffected > 0) {
				System.out.println("Done!, rows affected: " + rowsAffected);
			}
			
			
			
		} catch (SQLException e) {
			
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			throw new DbIntegrityException("Rolled back!, cause: " + e.getMessage());
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			DB.closeStatement(ps);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
		
		try {
			
		ps = conn.prepareStatement("DELETE FROM department WHERE id = ?;");
		
		ps.setInt(1, id);
		
		int rowsAffected = ps.executeUpdate();
		
		if (rowsAffected > 0) {
			System.out.println("Done!, rows affected: " + rowsAffected);
		}
		
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public Department findById(Integer id) {
		
		Department department = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			ps = conn.prepareStatement("SELECT * FROM department WHERE id = ?;");
			
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				department = new Department(rs.getInt("id"), rs.getString("name"));
			} 
			else {
				throw new DbIntegrityException("No elements in database with id: " + id);
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbIntegrityException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
		
		return department;
		
	}

	@Override
	public List<Department> findAll() {
		
		List<Department> list = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			ps = conn.prepareStatement("SELECT * FROM department;");
			
			rs = ps.executeQuery();
			
			if (!rs.next()) {
	            throw new DbIntegrityException("No elements in the query");
	        }

	        do {
	            list.add(new Department(rs.getInt("id"), rs.getString("name")));
	        } while (rs.next());
	        
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
		return list;
	}

}
