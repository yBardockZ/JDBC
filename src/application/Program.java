package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.impl.DepartmentDaoJDBC;
import model.entities.Department;

public class Program {

	public static void main(String[] args) {
		
		DepartmentDaoJDBC dep = (DepartmentDaoJDBC) DaoFactory.createDepartmentDAO();
		
		Department department = dep.findById(2);
		List<Department> list = dep.findAll();
		dep.update(new Department(10, "Programmers"));
		
		System.out.println("Find by id: " + department);
		System.out.println("Find all: ");
		list.forEach(System.out::println);
		
		
	}

}
