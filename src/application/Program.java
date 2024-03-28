package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDAO;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		SellerDAO sel = DaoFactory.createSellerDAO();
		
		DepartmentDAO dep = DaoFactory.createDepartmentDAO();
		
		System.out.println("Test1=====findByid======\n");
		
		Seller s1 = sel.findById(1);
		System.out.println(s1);
		
		System.out.println("\nTest2=====findAll======\n");
		
		List<Seller> list = sel.findAll();
		list.forEach(System.out::println);
		
		System.out.println("\nTest3=====findByDepartment======\n");
		
		List<Seller> list1 = sel.findByDepartment(new Department(1, "Computers"));
		list1.forEach(x -> System.out.println(x));
		
		System.out.println("\nTest4=====findById======\n");
		
		Department d = dep.findById(1);
		System.out.println(d);
		
		System.out.println("\nTest5=====findAll======\n");
		
		List<Department> list2 = dep.findAll();
		list2.forEach(x -> System.out.println(x));
		
	}

}
