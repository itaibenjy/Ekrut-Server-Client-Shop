package jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import Enum.Instruction;
import Enum.ProductStatus;
import Server.EkrutServer;
import entities.ProductInFacility;
import entities.ServerMessage;
/**
 * DataBaseOpreatingWorker, in this class we will perform several queries in order to:
 * Update execution instruction.
 * 
 * Set execution instruction status to DONE.
 * 
 * Update the inventory amount of a product in the facility -> the update will depend on whether the product exists or not
 * and then it will be an income of a new product.
 * 
 * Get details on the regional manager by region in order to send him an email that the task has been completed
 */
public class DataBaseOpreatingWorker {

	private static ArrayList<ProductInFacility> productsInFacility = new ArrayList<ProductInFacility>();

	/**
	 * Get values and update products in executioninstructions table
	 * 
	 * @param data hashtable that holds: "idExecutionInstruction" (int): the id of the execution instruction to
	 *                                 be updated
	 * 			 "userName"               (String): the user name of the user
	 *                                 associated with the execution instruction
	 * 			"facilityId"             (String): the id of the facility associated
	 *                                 with the execution instruction
	 * 			"products"               (String): the updated products for the
	 *                                 execution instruction
	 * @return ServerMessage without information
	 */
	public static ServerMessage updateInventoryExecutionInstruction(Hashtable<String, Object> data) {

		int idExecutionInstruction = (int) data.get("idExecutionInstruction");
		String userName = (String) data.get("userName");
		String facilityId = (String) data.get("facilityId");
		String products = (String) data.get("products");
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn().prepareStatement("UPDATE executioninstructions" + " SET products = ?"
					+ " WHERE (idExecutionInstruction = ?) AND (userName = ?) AND (facilityId = ?)");

			stmt.setString(1, products);
			stmt.setInt(2, idExecutionInstruction);
			stmt.setString(3, userName);
			stmt.setString(4, facilityId);
			stmt.executeUpdate();
			return new ServerMessage(Instruction.Nothing_To_Get);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query Update ExecutionInstruction failed");
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Get values and update status execution instruction in executioninstructions
	 * table
	 * 
	 * @param data hashtable that holds: "idExecutionInstruction" (int): the ID of the execution instruction to
	 *                                 be updated
	 * 		  "userName"               (String): the username of the user who is
	 *                                 updating the execution instruction
	 *        "facilityId"             (String): the ID of the facility associated
	 *                                 with the execution instruction
	 *        "products"               (String): the updated products list for the
	 *                                 execution instruction
	 *         "status"                 (String): the updated status for the
	 *                                 execution instruction
	 * @return ServerMessage without information
	 */
	public static ServerMessage updateStatusExecutionInstruction(Hashtable<String, Object> data) {
		int idExecutionInstruction = (int) data.get("idExecutionInstruction");
		String userName = (String) data.get("userName");
		String facilityId = (String) data.get("facilityId");
		String products = (String) data.get("products");
		String status = (String) data.get("status");
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn()
					.prepareStatement("UPDATE executioninstructions" + " SET status = ?, products = ?"
							+ " WHERE (idExecutionInstruction = ?) AND (userName = ?) AND (facilityId = ?)");

			stmt.setString(1, status);
			stmt.setString(2, products);
			stmt.setInt(3, idExecutionInstruction);
			stmt.setString(4, userName);
			stmt.setString(5, facilityId);
			stmt.executeUpdate();
			EkrutServer.getServer().getServerGui().printToConsole("Update Execution Instruction Status to Done");
			return new ServerMessage(Instruction.Nothing_To_Get);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query Update ExecutionInstruction failed");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get values and update Product inventory in Product table
	 * 
	 * @param data "facilityId": a String representing the id of the facility where the
	 *                      products are located
	 * 						"Products":   a String containing information about the products and
	 *                      their updated inventory levels, in the format
	 *                      "ProductCode, ProductName,OldInventory, NewInventory"
	 *                      (e.g. "2,Cola,5,10")
	 * @return ServerMessage without information
	 */
	public static ServerMessage updateProductInventory(Hashtable<String, Object> data) {
		// values:
		String facilityId = (String) data.get("facilityId");
		String products = (String) data.get("Products");
		String productStatus = "";
		String[] values = products.split(",");
		int quantity = 0;
		int tresholdlevel = 0;
		int Productcode;
		getArrayProductsValues(facilityId);
		PreparedStatement stmt;
		try {

			// "ProductCode, ProductName,OldInventory, NewInventory"
			// "2,Cola,5,10"

			for (int i = 0; i < values.length; i += 4) {
				Productcode = Integer.parseInt(values[i]);
				quantity = Integer.parseInt(values[i + 3]);
				productStatus = "Avaliable";
				if (quantity == 0) {
					productStatus = "UnAvaliable";
				}

				// Product already exist in facility
				if (CheckIfProductExistInFacility(Productcode) == true) {
					stmt = DataBaseController.getConn().prepareStatement("UPDATE productinfacility SET"
							+ " quantity = ?, productStatus = ?" + " WHERE (facilityId = ?) AND (productCode = ?);");
					stmt.setInt(1, quantity);
					stmt.setString(2, productStatus);
					stmt.setString(3, facilityId);
					stmt.setInt(4, Productcode);
					stmt.executeUpdate();
				}
				// new product in facility
				else {
					stmt = DataBaseController.getConn()
							.prepareStatement("INSERT INTO productinfacility "
									+ "(facilityId, productCode, quantity, productStatus, tresholdlevel)"
									+ " VALUES (?, ?, ?, ?, ?);");
					stmt.setString(1, facilityId);
					stmt.setInt(2, Productcode);
					stmt.setInt(3, quantity);
					stmt.setString(4, productStatus);
					stmt.setInt(5, tresholdlevel);
					stmt.executeUpdate();
				}
			}
			return new ServerMessage(Instruction.Nothing_To_Get);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query Update Product Inventory failed");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get all productinfacility in specific facility to know if to update product
	 * or insert new one
	 */
	private static void getArrayProductsValues(String facilityId) {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt
					.executeQuery(String.format("SELECT * FROM productinfacility where facilityId = '%s'", facilityId));

			while (rs.next()) {
				ProductInFacility p = new ProductInFacility();
				p.setProductCode(rs.getInt("productCode"));
				p.setQuantity(Integer.parseInt(rs.getString("quantity")));
				p.setProductStatus(getEnumProductStatus(rs.getString("productStatus")));
				p.setTresholdLevel(Integer.parseInt(rs.getString("tresholdlevel")));
				productsInFacility.add(p);
			}
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get Get array products failed");
			e.printStackTrace();
		} 

	}

	/**
	 * Receives the details of the area manager by machine area in order to send him an email about performing an inventory update
	 * @param data hashtable "facilityId": the id of facility to get the area and the nanger in this area
	 * @return ServerMessage with information: firstName,lastName,email,telephone
	 */
	public static ServerMessage getFacilityArea(Hashtable<String, Object> data) {
		Statement stmt;
		String facilityId = (String) data.get("facilityId");
		Hashtable<String, Object> valuesTable = new Hashtable<String, Object>();

		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT distinct firstName , lastName, email , telephone\r\n"
					+ "FROM ekrut.facilities, ekrut.user\r\n" + "where ekrut.user.role ='AreaManager' and"
					+ " ekrut.facilities.facilityArea = ekrut.user.area and" + " ekrut.facilities.facilityId = '%s'",
					facilityId));

			while (rs.next()) {
				valuesTable.put("firstName", rs.getString("firstName"));
				valuesTable.put("lastName", rs.getString("lastName"));
				valuesTable.put("email", rs.getString("email"));
				valuesTable.put("telephone", rs.getString("telephone"));
			}

			return new ServerMessage(Instruction.Facility_Area_Value, valuesTable);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get Get rellevant area manager failed");
			e.printStackTrace();
			return null;
		} 
	}

	private static boolean CheckIfProductExistInFacility(int code) {
		for (ProductInFacility productInFacility : productsInFacility) {
			if (productInFacility.getProductCode() == code) {
				return true;
			}
		}
		return false;
	}
	
	/* ENUM getters */
	private static ProductStatus getEnumProductStatus(String productCategory) {
		if (productCategory == null) {
			return null;
		}
		for (ProductStatus i : ProductStatus.values()) {
			if (productCategory.equals(i.name())) {
				return i;
			}
		}
		return null;
	}

}
