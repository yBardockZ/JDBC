package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDAO;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		SellerDAO sel = DaoFactory.createSellerDAO();
		
		List<Seller> list = sel.findAll();
		
		list.forEach(System.out::println);
		
		
		
		
		
		
	}

}
