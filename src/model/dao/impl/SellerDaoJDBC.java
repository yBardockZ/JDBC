package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbIntegrityException;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDAO {
	
	private Connection conn;
	
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller seller) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement("INSERT INTO seller (name, email, birthdate, basesalary, iddepartment) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, seller.getName());
			ps.setString(2, seller.getEmail());
			ps.setDate(3, Date.valueOf(seller.getBirthDate()));
			ps.setDouble(4, seller.getBaseSalary());
			ps.setInt(5, seller.getDepartment().getId());
			
			int rowsAffected = ps.executeUpdate();
			
			if (rowsAffected > 0) {
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					System.out.println("Done!, rowsAffected: " + rowsAffected + ", generated keys: " + rs.getInt(1));
				}
			}
	
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
			
		}
	}

	@Override
	public void update(Seller seller) {	
		PreparedStatement ps = null;
		
		try {
			conn.setAutoCommit(false);
			
			ps = conn.prepareStatement("UPDATE seller"
					+ " SET name = ?, email = ?, birthdate = ?, basesalary = ?, iddepartment = ?"
					+ " WHERE id = ?;");
			
			ps.setString(1, seller.getName());
			ps.setString(2, seller.getName());
			ps.setDate(3, Date.valueOf(seller.getBirthDate()));
			ps.setDouble(4, seller.getBaseSalary());
			ps.setInt(5, seller.getDepartment().getId());
			ps.setInt(6, seller.getId());
			
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
			throw new DbIntegrityException("Rolled back, caused by: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			DB.closeStatement(ps);
			
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement("DELETE FROM seller"
					+ " WHERE id = ?;");
			
			ps.setInt(1, id);
			
			int rowsAffected = ps.executeUpdate();
			
			if (rowsAffected > 0) {
				System.out.println("Done!, rows affected: " + rowsAffected);
			}
			else {
				System.out.println("No seller with id: " + id + " in database.");
			}
			
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			DB.closeStatement(ps);
			
		}
		
	}

	@Override
	public Seller findById(Integer id) {
		
		Seller seller = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			 ps = conn.prepareStatement("SELECT s.*, d.name AS DEPARTMENT"
					+ " FROM seller s"
					+ " INNER JOIN department d"
					+ "	ON s.iddepartment = d.id"
					+ " WHERE s.id = ?");
			
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				seller = new Seller(rs.getInt("id"), rs.getString("name"), rs.getString("email"),
						rs.getDate("birthdate").toLocalDate(), rs.getDouble("basesalary"), 
						new Department(rs.findColumn("iddepartment"), rs.getString("DEPARTMENT")));
			}
			
			
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
			
		}
		return seller;
	}

	@Override
	public List<Seller> findAll() {
		
		List<Seller> list = new ArrayList<>();
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement("SELECT * FROM seller_department");
			
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				throw new DbIntegrityException("No elements in database.");
			}
			
			Map<Integer, Department> map = new HashMap<>();
			
			do {
				Department dep = map.get(rs.getInt("iddepartment"));
				
				if (dep == null) {
					dep = instanciateDepartment(rs);
					map.put(rs.getInt("iddepartment"), dep);
				}

				list.add(instanciateSeller(rs, dep));
			} while (rs.next());
			
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
		return list;
	}
	
	@Override
	public List<Seller> findByDepartment(Department department) {
		
		List<Seller> list = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			ps = conn.prepareStatement("SELECT * FROM seller_department"
					+ " WHERE iddepartment = ?;");
			
			ps.setInt(1, department.getId());
			
			rs = ps.executeQuery();
			
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("iddepartment"));
				
				if (dep == null) {
					dep = instanciateDepartment(rs);
					map.put(rs.getInt("iddepartment"), dep);
				}
				
				
				list.add(instanciateSeller(rs, department));
			}
			
			
			
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
		
		return list;
	
	}
	
	private Seller instanciateSeller(ResultSet rs, Department department) {
		Seller seller = new Seller();
		try {
			
			seller.setId(rs.getInt("id"));
			seller.setName(rs.getString("name"));
			seller.setEmail(rs.getString("email"));
			seller.setBirthDate(rs.getDate("birthdate").toLocalDate());
			seller.setBaseSalary(rs.getDouble("basesalary"));
			seller.setDepartment(instanciateDepartment(rs));
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return seller;
	}
	
	private Department instanciateDepartment(ResultSet rs) {
		Department department = new Department();
		try {
			
			department.setId(rs.getInt("iddepartment"));
			department.setName(rs.getString("department"));
			
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return department;
	}
	
}
