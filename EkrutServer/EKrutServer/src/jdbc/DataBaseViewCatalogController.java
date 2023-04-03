package jdbc;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

//import Client.EkrutClient;
import Enum.Instruction;
import Enum.ProductCategory;
import Enum.ProductStatus;
import Server.EkrutServer;
import entities.Product;
import entities.ProductInFacility;
import entities.ServerMessage;

public class DataBaseViewCatalogController {

	/**
	 * Returns a ServerMessage object containing a list of all products stored in the database.
	 * @return the ServerMessage object containing the list of products and instruction,
	 *  or null if there was an error.
	 */
	public static ServerMessage getAllProducts() {
		Statement stmt;
	    ArrayList<Product> productsList = new ArrayList<Product>();
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM product;"));
			while (rs.next())
			{
				Blob blob =rs.getBlob("photo");
				Product p=new Product(rs.getInt("productCode"),rs.getString("productName"),rs.getFloat("productPrice"),blob.getBytes(1, (int)blob.length()),getEnumProductCategory(rs.getString("category")));
				productsList.add(p);
				
			}
			Hashtable<String,Object> productTable = new Hashtable<String,Object>(); 
			productTable.put("allProductList", productsList);
			return new ServerMessage(Instruction.All_Product_list,productTable);
			
		}catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get user details failed");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns a ServerMessage object containing a list of all products stored in a specific facility in the database.
	 * @param data a Hashtable object that contains a key "facilityID" with the ID of the facility.
	 * @return the ServerMessage object containing the list of products and instruction,
	 *  or null if there was an error.
	 */
	public static ServerMessage getFacilityProducts(Hashtable<String, Object> data) {
		Statement stmt;
	    ArrayList<Product> productsList = new ArrayList<Product>();
	    String facilityID = (String) data.get("facilityID");
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format(
					"SELECT * FROM productinfacility, product"
				  + " Where productinfacility.productCode = product.productCode"
				  + " AND productinfacility.facilityId = '%s'"
				  + " AND productinfacility.productStatus = 'Avaliable';", facilityID));

			while (rs.next())
			{
				Blob blob =rs.getBlob("photo");
				ProductInFacility p=new ProductInFacility(rs.getInt("productCode"),rs.getString("productName"),rs.getFloat("productPrice"),blob.getBytes(1, (int)blob.length()),getEnumProductCategory(rs.getString("category")),rs.getInt("quantity"),getEnumProductStatus(rs.getString("productStatus")),rs.getInt("tresholdlevel"));
				productsList.add(p);
				
			}
			Hashtable<String,Object> productTable = new Hashtable<String,Object>(); 
			productTable.put("allProductList", productsList);
			return new ServerMessage(Instruction.All_Product_list,productTable);
			
		}catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get facility products failed");
			return null;

		}
	}
		

	/**
	 * Returns the corresponding ProductCategory enum for the given string.
	 * @param productCategory the string representation of the product category.
	 * @return the corresponding ProductCategory enum, or null if no match is found or the input is null.
	 */
	private static ProductCategory getEnumProductCategory(String productCategory) {
		if(productCategory == null) {return null;}
		for(ProductCategory i : ProductCategory.values()){
			if(productCategory.equals(i.name())) {
				return i;
			}
		}
		return null;
	}
	
	/**
	 * Returns the corresponding ProductStatus enum for the given string.
	 * @param productStatus the string representation of the product status.
	 * @return the corresponding ProductStatus enum, or null if no match is found or the input is null.
	 */
	private static ProductStatus getEnumProductStatus(String productStatus) {
		if(productStatus== null) {return null;}
		for(ProductStatus i: ProductStatus.values()){
			if(productStatus.equals(i.name())) {
				return i;
			}
		}
		return null;
	}


}


