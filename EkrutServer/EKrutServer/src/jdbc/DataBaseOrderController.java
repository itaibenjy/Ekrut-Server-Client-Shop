package jdbc;

import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
//import java.util.Date;
import java.util.HashMap;

import java.util.Hashtable;
import java.util.Set;


import Enum.Country;
import Enum.CustomerShippmentStatus;
import Enum.Instruction;
import Enum.ProccessStatus;
import Enum.ProductCategory;
import Enum.SupplyMethod;
import Server.EkrutServer;
import entities.Order;
import entities.Product;
import entities.ProductInFacility;
import entities.RegisteredCustomer;
import entities.SelfCollection;
import entities.ServerMessage;
import entities.Shipment;
import entities.Subscriber;

public class DataBaseOrderController {

	/**
	 * This method is used to update the products of an order in the database.
	 * The products can either be added or removed from the order, depending on the value of the 'isAdd' parameter.
	 *
	 * @param data  A Hashtable that contains data for the method, including the products to be updated and the facility ID.
	 * @param isAdd A boolean value indicating whether the products should be added or removed from the order.
	 * @param inst  A Instruction enum value indicating the instruction of the method
	 * @return A ServerMessage object containing information about the updated order.
	 */

	public static ServerMessage updateOrderProducts(Hashtable<String, Object> data, boolean isAdd, Instruction inst) {
		ArrayList<Product> products = (ArrayList<Product>) data.get("products");
		String facilityID = ((String) data.get("facilityID"));
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			for (Product product : products) {
				ProductInFacility p = (ProductInFacility) product;
				updateProduct(facilityID, stmt, isAdd ? p.getQuantity() : -1 * p.getQuantity(), p.getProductCode());
				updateStatusProductInFacility(facilityID, stmt);
			}

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui()
					.printToConsole("Error While excecuting updateOrderProducts query with isAdd = ." + isAdd);
			e.printStackTrace();
			return new ServerMessage(Instruction.Nothing_To_Get);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ServerMessage(inst, data);
	}
 
	/**
	This method updates the quantity of a product in a specific facility.
	@param facilityID The id of the facility where the product is located.
	@param stmt The statement object used to execute the update query.
	@param amount The amount to update the quantity by. Can be positive or negative.
	@param productCode The code of the product to update the quantity for.
	@throws SQLException if an error occurs while executing the update query.
	*/
	private static void updateProduct(String facilityID, Statement stmt, int amount, int productCode)
			throws SQLException {
		stmt.executeUpdate(String.format(
				"UPDATE `ekrut`.`productinfacility` SET `quantity` = `quantity` + %d  WHERE (`facilityId` = '%s') and (`productCode` = '%d');",
				amount, facilityID, productCode));
	}

	/**
	This method updates the status of products in a specific facility.
	@param facilityID The id of the facility to update the product status for.
	@param stmt The statement object used to execute the update query.
	@throws SQLException if an error occurs while executing the update query.
	*/
	private static void updateStatusProductInFacility(String facilityID, Statement stmt) throws SQLException {
		stmt.executeUpdate(String.format(
				"UPDATE `ekrut`.`productinfacility` SET `productStatus` = 'Avaliable' WHERE (`facilityId` = '%s') and (`quantity` > '0');",
				facilityID));
		stmt.executeUpdate(String.format(
				"UPDATE `ekrut`.`productinfacility` SET `productStatus` = 'UnAvaliable' WHERE (`facilityId` = '%s') and (`quantity` = '0');",
				facilityID));
	}
	
	/**

	This method updates the passed threshold level of a product in a specific facility.
	@param facilityID The id of the facility where the product is located.
	@param quantity The amount of product to be added or subtracted.
	@param code The code of the product to update the threshold level for.
	@param stmt The statement object used to execute the update query.
	 */
	private static void passing_Threshold_Level(String facilityID,int quantity , int code, Statement stmt) throws SQLException {
		stmt.executeUpdate(String.format(
				"UPDATE productinfacility SET paseedThreshold = paseedThreshold + (CASE WHEN ( quantity < tresholdlevel  and quantity +'%s' >= tresholdlevel ) THEN 1 ELSE 0 END)  WHERE facilityId = '%s' AND productCode = '%s' ;",
				quantity,facilityID,code));
		EkrutServer.getServer().getServerGui().printToConsole("Product Code "+code +" In Facility "+ facilityID +" Passed Threshold Level ");

	}

	/**
	 * @param data type Hashtable , with all information on the oreder from client
	 *	@return A ServerMessage object containing an instruction of type Order_Update and a Hashtable containing the created order, depend on whice oreder created
	 *
	 */
	@SuppressWarnings("unchecked")
	public static ServerMessage makeAnOrder(Hashtable<String, Object> data) {
		
		String supplyMethod=(String) data.get("getSupplyMethod");
		ArrayList<Product> orderProductList =(ArrayList<Product>) data.get("getOrderProductList");
		float totalPrice = (float) data.get("getTotalPrice");
		Order order=new Order();
		RegisteredCustomer user = (RegisteredCustomer) data.get("user");

		switch (supplyMethod) {
		case "Shipment":
			 order=new Shipment();
			 ((Shipment)order).setDeliveryAddress((String) data.get("getDeliveryAddress"));
			 ((Shipment)order).setShippmentCountry(Country.valueOf((String) data.get("getShippmentCountry")));
			order.setTotalPrice(totalPrice);
			order.setSupplyMethod(SupplyMethod.Shipment);
			order.setFacilityId("OperationCenter"+user.getArea().toString());
			break;
		case "SelfCollection":
			order=new SelfCollection();
			((SelfCollection)order).setEstimatePickUpDate(  (LocalDate) data.get("getEstimatePickUpDate"));
			order.setFacilityId((String)  data.get("getOrderFacilityId"));
			order.setTotalPrice(totalPrice);
			order.setSupplyMethod(SupplyMethod.SelfCollection);
			break;
		case "LocalOrder":
			order.setTotalPrice(totalPrice);
			order.setSupplyMethod(SupplyMethod.LocalOrder);
			order.setFacilityId((String)data.get("getFacilityId"));
			break;
		default:
			break;
		}
		order.setOrderProductList(orderProductList);
		String orderProductListString=stringfyOrder(orderProductList);
		String payment = (String) data.get("payment");		
		Statement stmt;
		


		try {
			stmt = DataBaseController.getConn().createStatement();
			
			//passed threshold level ?
			if( !order.getSupplyMethod().equals(SupplyMethod.Shipment))
			{
			for (Product product : 	order.getOrderProductList()) {
				ProductInFacility productInFacility = (ProductInFacility) product;
					passing_Threshold_Level(order.getFacilityId(), 	productInFacility.getQuantity(), productInFacility.getProductCode(), stmt);
			}
			}
			makeOrder(stmt, orderProductListString, order, user);

			if (supplyMethod.equals("SelfCollection")) {
				makeSelfCollection((SelfCollection) order, user);
			} else if (supplyMethod.equals("Shipment") ){
				makeShippment((Shipment) order, user);
			}
			
			if(payment.equals("Monthly Payment")) {
				updateDebt(user, totalPrice);
			}
			
			if(user instanceof Subscriber) {
				updateFirstOrder(user);
			}

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("Cant Make An Order");
			
		} catch (Exception e) {
			EkrutServer.getServer().getServerGui().printToConsole("Cant Make An Order");
		}

		Hashtable<String, Object> orderTable = new Hashtable<>();
		orderTable.put("order", order);
		EkrutServer.getServer().getServerGui().printToConsole("New Order Number : "+order.getOrderNum()+" To User "+ user.getUserName() +" Supply Method :" +order.getSupplyMethod());
		return new ServerMessage(Instruction.Order_Update, orderTable);
	}

	/**

	This method creates an order in the database and sets the order number for the order object.
	@param stmt The statement object used to execute the query.
	@param orderProducts A String representing the products in the order, in the format suitable for the database.
	@param order The Order object to be created in the database.
	@param registeredCustomer The RegisteredCustomer object associated with the order.
	@throws SQLException if an error occurs while executing the query.
	*/
	private static void makeOrder(Statement stmt, String orderProducts, Order order,
			RegisteredCustomer registeredCustomer) throws SQLException, Exception {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String dateInString = now.format(formatter);

		stmt.executeUpdate(String.format(
				"INSERT INTO `ekrut`.`order` (`facilityID`, `userName`, `products`, `totalPrice`, `orderDate`, `supplyMethod`)"
						+ " VALUES ('%s', '%s', '%s', '%f', '%s','%s')",
					order.getFacilityId(), registeredCustomer.getUserName().toString(), orderProducts, order.getTotalPrice(),
				dateInString, order.getSupplyMethod().toString()));
		ResultSet rs = stmt.executeQuery(String.format(
				"SELECT MAX(orderNum) as max FROM ekrut.order WHERE userName='%s'", registeredCustomer.getUserName()));
		if (rs.next()) {
			order.setOrderNum(rs.getInt(1));
		}
	}

	
	
	/**
	This method insert a shipment order in the database
	@param order The Shipment object containing the order information.
	@param user The RegisteredCustomer object associated with the order.
	@throws SQLException if an error occurs while executing the query.
	*/	 
	private static void makeShippment(Shipment order, RegisteredCustomer user) throws SQLException {
		PreparedStatement stmt1;
		stmt1 = DataBaseController.getConn().prepareStatement(
				"INSERT INTO ekrut.ordershipment ( orderNum, deliveryAddress, shipmentOrderStatus, customerShipmentStatus, deliveryCountry) VALUES ( ?, ?, ?, ?, ?);");
		stmt1.setInt(1, order.getOrderNum());
		stmt1.setString(2, order.getDeliveryAddress());
		stmt1.setString(3, "WaitingForConfirmation");
		stmt1.setString(4, "NotRecived");
		stmt1.setString(5,  ((Shipment)order).getShippmentCountry().toString());
		stmt1.executeUpdate();
	}
	/**
	 * 
	 * This method is used to create a self-collection order in the database. It takes in two parameters, an object of the SelfCollection class and an object of the RegisteredCustomer class. 
	 *
	 * @param order  An object of the SelfCollection class that contains the order details.
	 * @param user  An object of the RegisteredCustomer class that contains the user details.
	 * @throws SQLException  If an exception occurs when interacting with the database.
	 */
	private static void makeSelfCollection(SelfCollection order, RegisteredCustomer user) throws SQLException {

		PreparedStatement stmt1;
		stmt1 = DataBaseController.getConn()
				.prepareStatement("INSERT INTO ekrut.orderselfcollection (orderNum, estimatePickUpDate) VALUES ( ?, ?);");
		stmt1.setInt(1, order.getOrderNum());
		Date date = Date.valueOf(order.getEstimatePickUpDate());
		stmt1.setDate(2, date);
		stmt1.executeUpdate();

	}
	/**
	 * 
	 * This method is used to update the debt of a registered customer in the database.
	 *
	 * @param user  An object of the RegisteredCustomer class that contains the user details.
	 * @param amount  A float value representing the amount to be added to the user's debt.
	 * @throws SQLException  If an exception occurs when interacting with the database.
	 */

	private static void updateDebt(RegisteredCustomer user, float amount) throws SQLException {
		PreparedStatement stmt1;
		stmt1 = DataBaseController.getConn()
				.prepareStatement("UPDATE `ekrut`.`subscriber` SET `ekrut`.`subscriber`.`debtAmount` = `ekrut`.`subscriber`.`debtAmount` + ?  WHERE `userName` = ? ;");
		stmt1.setFloat(1, amount);
		stmt1.setString(2, user.getUserName());
		stmt1.execute();
	}
	
	/**
	 * 
	 * This method is used to update the first order status of a registered customer in the database.
	 *
	 * @param user  An object of the RegisteredCustomer class that contains the user details.
	 * @throws SQLException  If an exception occurs when interacting with the database.
	 */

	private static void updateFirstOrder(RegisteredCustomer user) throws SQLException {
		PreparedStatement stmt1;
		stmt1 = DataBaseController.getConn()
				.prepareStatement("UPDATE `ekrut`.`subscriber` SET `firstOrder` = 0  WHERE `userName` = ? ;");
		stmt1.setString(1, user.getUserName());
		stmt1.execute();
	}

	/**
	 * This method is used to convert an ArrayList of Product objects into a string representation.
	 * Each product will be represented as a string in the format: "productName,quantity"
	 * @param productaArrayList  An ArrayList of Product objects to be converted into a string.
	 * @return  A string representation of the ArrayList of products.
	 */

	private static String stringfyOrder(ArrayList<Product> productaArrayList) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Product product : productaArrayList) {
			ProductInFacility productInFacility = (ProductInFacility) product;
			stringBuilder.append(productInFacility.getProtductName());
			stringBuilder.append(",");
			stringBuilder.append(productInFacility.getQuantity());
			stringBuilder.append(",");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}

	
	
	/**
	 * Retrieves orders associated with a specific user from the database.
	 * @param data A hashtable that contains the "username" key, the value is a user's username (String) ,
	 * and will be used to store the results of the query. The following keys will be added:
	 * "localOrSelfOrders" (an ArrayList of Order objects)
	 * "shipmentOrders" (an ArrayList of Shipment objects),
	 * "selfCollectionOrders" (an ArrayList of SelfCollection objects)
	 * @return ServerMessage An object that encapsulates the result of the method.
	 */
	public static ServerMessage getUserOrders(Hashtable<String, Object> data) {
		String username = (String) data.get("username");
		String sql = "SELECT * FROM ekrut.order WHERE userName = ? AND supplyMethod = ?;";
		
		
		ArrayList<Order> ordersList = new ArrayList<Order>();
		ArrayList<Shipment> shipmentList = new ArrayList<Shipment>();
		ArrayList<SelfCollection> selfCollectionList = new ArrayList<SelfCollection>();
		HashMap<String, Integer> productMap  = new HashMap<String, Integer>();
		ArrayList<Product> products  = new ArrayList<Product>();
		HashMap<Product, Integer> orderContent  = new HashMap<Product, Integer>();
		
		
		PreparedStatement pstmt;
		try {
			String sm = SupplyMethod.LocalOrder.toString();
			pstmt = DataBaseController.getConn().prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, sm);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				Order o = new Order( rs.getInt("orderNum"),makeOrderMap(rs.getString("products")),rs.getString("facilityID"),rs.getFloat("totalPrice"),getEnumSupplyMethod(rs.getString("supplyMethod")),fixDateDifference(rs.getDate("orderDate")),rs.getString("userName"));
				ordersList.add(o);
			}
			data.put("localOrSelfOrders", ordersList);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		String sql1 = "SELECT *\r\n"
				+ "FROM ekrut.order as O ,ekrut.ordershipment as S\r\n"
				+ "WHERE O.orderNum=S.orderNum AND userName=?;";
		PreparedStatement pstmt1;
		try {
			pstmt1 = DataBaseController.getConn().prepareStatement(sql1);
			pstmt1.setString(1, username);
			ResultSet rs1 = pstmt1.executeQuery();
			while(rs1.next()) {
				Shipment s = new Shipment(rs1.getInt("orderNum"),makeOrderMap(rs1.getString("products")),rs1.getString("facilityID"),rs1.getFloat("totalPrice"),getEnumSupplyMethod(rs1.getString("supplyMethod")),fixDateDifference(rs1.getDate("orderDate")),rs1.getString("userName"),rs1.getString("deliveryAddress"),fixDateDifference(rs1.getDate("estimateDeliveryTime")),getEnumShipmentStatus(rs1.getString("shipmentOrderStatus")),getEnumCustomerShippmentStatus(rs1.getString("customerShipmentStatus")),getEnumCountry(rs1.getString("deliveryCountry")));
				shipmentList.add(s);
			}
			data.put("shipmentOrders", shipmentList);
		}catch (SQLException e) {
			e.printStackTrace(); 
		}catch(Exception e) {
			e.printStackTrace();
		}
		String sql2 = "SELECT *\r\n"
				+ "FROM ekrut.order as O ,ekrut.orderselfcollection as S\r\n"
				+ "WHERE O.orderNum=S.orderNum AND userName=?;";
		
		PreparedStatement pstmt2;
		try {
			pstmt2 = DataBaseController.getConn().prepareStatement(sql2);
			pstmt2.setString(1, username);
			ResultSet rs2 = pstmt2.executeQuery();
			while(rs2.next()) {
				SelfCollection sc = new SelfCollection(rs2.getInt("orderNum"),makeOrderMap(rs2.getString("products")),rs2.getString("facilityID"),rs2.getFloat("totalPrice"),getEnumSupplyMethod(rs2.getString("supplyMethod")),fixDateDifference(rs2.getDate("orderDate")),rs2.getString("userName"),dateToLocalDate(fixDateDifference(rs2.getDate("estimatePickUpDate"))),dateToLocalDate(fixDateDifference(rs2.getDate("actualPickUpDate"))));
				selfCollectionList.add(sc);
			}
			data.put("selfCollectionOrders", selfCollectionList);
			return new ServerMessage(Instruction.User_Orders,data);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	/**
	 * Updates the status of a specific shipment order in the database.
	 * @param data A hashtable that contains the "orderToUpdate" key,
	 * the value is a Shipment object that contains the updated status.
	 * @return ServerMessage An object with "Nothing to get" instruction to the client because its update the DB 
	 * and don't get any result.
	 */
	public static ServerMessage updateOrder(Hashtable<String, Object> data) {
		Shipment shipmentOrder = (Shipment) data.get("orderToUpdate");
		Integer orderNum = shipmentOrder.getOrderNum();
		String status = CustomerShippmentStatus.Received.toString();
		String sql = "UPDATE ekrut.ordershipment SET customerShipmentStatus = ? WHERE orderNum = ?;";
		PreparedStatement pstmt;
		try {
			pstmt = DataBaseController.getConn().prepareStatement(sql);
			pstmt.setString(1, status);
			pstmt.setInt(2, orderNum);
			pstmt.executeUpdate();
			return new ServerMessage(Instruction.Nothing_To_Get);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Retrieves all shipment orders with a specific area and with "Waiting for confirmation" status from the DB.
	 * @param data A hashtable that contains the "userArea" key, the value is a string that represents the wanted area.
	 * @return ServerMessage An object that encapsulates the result of the method. 
	 * It contains "shipmentByArea" key, the value is an ArrayList of Shipment object that match the area and status.
	 */
	public static ServerMessage getShipmentByArea(Hashtable<String, Object> data) {
		String area = (String)data.get("userArea");
		String status =ProccessStatus.WaitingForConfirmation.toString();
		ArrayList<Shipment> shipmentList = new ArrayList<Shipment>();
		String method = SupplyMethod.Shipment.toString();
		String sql = "SELECT *\r\n"
				+ "FROM ekrut.order JOIN ekrut.ordershipment\r\n"
				+ "ON ekrut.order.orderNum = ekrut.ordershipment.orderNum\r\n"
				+ "WHERE facilityID=ANY(SELECT facilityID\r\n"
				+ "				  FROM ekrut.facilities\r\n"
				+ "				  WHERE facilityArea=?) AND supplyMethod=? AND ekrut.order.orderNum = ANY(SELECT ekrut.ordershipment.orderNum\r\n"
				+ "																						  FROM ekrut.ordershipment\r\n"
				+ "																						  WHERE shipmentOrderStatus=?);";
		PreparedStatement pstmt;
		try {
			pstmt = DataBaseController.getConn().prepareStatement(sql);
			pstmt.setString(1, area);
			pstmt.setString(2, method); 
			pstmt.setString(3, status);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				Shipment s = new Shipment(rs.getInt("orderNum"),makeOrderMap(rs.getString("products")),rs.getString("facilityID"),rs.getFloat("totalPrice"),getEnumSupplyMethod(rs.getString("supplyMethod")),fixDateDifference(rs.getDate("orderDate")),rs.getString("userName"),rs.getString("deliveryAddress"),fixDateDifference(rs.getDate("estimateDeliveryTime")),getEnumShipmentStatus(rs.getString("shipmentOrderStatus")),getEnumCustomerShippmentStatus(rs.getString("customerShipmentStatus")),getEnumCountry(rs.getString("deliveryCountry")));
				shipmentList.add(s);
			}
			data.put("shipmentByArea", shipmentList);
			return new ServerMessage(Instruction.Shipments_by_area,data);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrieves all shipment orders with a specific area and with "In Proccess" status
	 * and already received by the customer from the DB.
	 * @param data A hashtable that contains the "recivedOrderUserArea" key,
	 * the value is a string that represents the wanted area.
	 * @return ServerMessage An object that encapsulates the result of the method. 
	 * It contains "receivedShipmentsForConfirmation" key,
	 * the value is an ArrayList of Shipment object that match the area and status.
	 */
	public static ServerMessage getReceviedShipmentForConfirmation(Hashtable<String, Object> data) {
		String area = (String)data.get("recivedOrderUserArea");
		String status = CustomerShippmentStatus.Received.toString();
		String shipmentStatus = ProccessStatus.InProccess.toString();
		ArrayList<Shipment> receivedShipmentList = new ArrayList<Shipment>();
		String sql = "SELECT *\r\n"
				+ "FROM ekrut.order JOIN ekrut.ordershipment\r\n"
				+ "ON ekrut.order.orderNum = ekrut.ordershipment.orderNum\r\n"
				+ "WHERE facilityID=ANY(SELECT facilityID\r\n"
				+ "				  FROM ekrut.facilities\r\n"
				+ "				  WHERE facilityArea=?) AND shipmentOrderStatus=? AND ekrut.order.orderNum = ANY(SELECT ekrut.ordershipment.orderNum\r\n"
				+ "																						  FROM ekrut.ordershipment\r\n"
				+ "																						  WHERE customerShipmentStatus=?);";
		PreparedStatement pstmt;
		try {
			pstmt = DataBaseController.getConn().prepareStatement(sql);
			pstmt.setString(1, area);
			pstmt.setString(2, shipmentStatus);
			pstmt.setString(3, status);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				Shipment s = new Shipment(rs.getInt("orderNum"),makeOrderMap(rs.getString("products")),rs.getString("facilityID"),rs.getFloat("totalPrice"),getEnumSupplyMethod(rs.getString("supplyMethod")),fixDateDifference(rs.getDate("orderDate")),rs.getString("userName"),rs.getString("deliveryAddress"),fixDateDifference(rs.getDate("estimateDeliveryTime")),getEnumShipmentStatus(rs.getString("shipmentOrderStatus")),getEnumCustomerShippmentStatus(rs.getString("customerShipmentStatus")),getEnumCountry(rs.getString("deliveryCountry")));
				receivedShipmentList.add(s);
			}
			data.put("receivedShipmentsForConfirmation", receivedShipmentList);
			return new ServerMessage(Instruction.Received_shipments_by_area,data);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Updates the status of a specific shipment order in the database.
	 * @param data A hashtable that contains the "shipmentToUpdate" key,
	 * the value is a Shipment object that contains the updated status.
	 * @return ServerMessage An object with "Nothing to get" instruction to the client because its update the DB 
	 * and don't get any result.
	 */
	public static ServerMessage updateShipmentStatus(Hashtable<String, Object> data) {
		String status = "";
		Shipment s = (Shipment)data.get("shipmentToUpdate");
		if(s.getShippmentOrderStatus() == ProccessStatus.InProccess) {
			status = ProccessStatus.InProccess.toString();
		}
		if(s.getShippmentOrderStatus() == ProccessStatus.Done) {
			status = ProccessStatus.Done.toString();
		}
		Integer orderNum =  s.getOrderNum();
		String sql= "UPDATE ekrut.ordershipment SET shipmentOrderStatus=? WHERE orderNum=?;";
		PreparedStatement pstmt;
		try {
			pstmt = DataBaseController.getConn().prepareStatement(sql);
			pstmt.setString(1, status);
			pstmt.setInt(2, orderNum);
			pstmt.executeUpdate();
			EkrutServer.getServer().getServerGui()
			.printToConsole("Order status number: "+orderNum+", has been successfully updated to "+status+".");
			return new ServerMessage(Instruction.Nothing_To_Get);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	* Update the estimate delivery time of an order in the database.
	* @param data - A Hashtable containing the following key-value pairs:
	*                "orderNumForUpdateEstimateDate" - The order number of the order being updated.
	*                "estimateDateForShipment" - The new estimate delivery date.
	* @return A ServerMessage An object with "Nothing to get" instruction to the client because its update the DB 
	 * and don't get any result.
	*/
	public static ServerMessage updateEstimateDate(Hashtable<String, Object> data) {
		Integer orderNum = (Integer)data.get("orderNumForUpdateEstimateDate");
		Date estimateDate = (Date)data.get("estimateDateForShipment");
		String sql = "UPDATE ekrut.ordershipment SET estimateDeliveryTime = ? WHERE orderNum = ?;";
		PreparedStatement pstmt;
		try {
			pstmt = DataBaseController.getConn().prepareStatement(sql);
			pstmt.setDate(1, estimateDate);
			pstmt.setInt(2, orderNum);
			pstmt.executeUpdate();
			
			return new ServerMessage(Instruction.Nothing_To_Get);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	* Creates a Product object from productName.
	* @param productName - a string representing the name of the product.
	* @return Product object that contains the product details or null if there is no product found.
	*/
	private static Product makeProduct(String productName) {
		String sql = "SELECT * FROM ekrut.product WHERE productName = ?;";
		PreparedStatement pstmt;
		try {
			pstmt = DataBaseController.getConn().prepareStatement(sql);	
			pstmt.setString(1, productName);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()) {
				return null;
			}
			Product p = new Product();
			p.setProductCode(rs.getInt("productCode"));
			p.setProtductName(productName);
			p.setProductPrice(rs.getFloat("productPrice"));
			Blob blob =rs.getBlob("photo");
			p.setPhoto (blob.getBytes(1, (int)blob.length()));
			p.setCategory(getEnumProductCategory(rs.getString("category")));
			return p;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrieves the area of a specific customer from the DB.
	 * @param data A hashtable that contains the "customerAreaToShipment" key,
	 * the value is a String object that contains the customer username.
	 * @return ServerMessage An object that encapsulates the result of the method. 
	 * It contains "customerArea" key,
	 * the value is a String object that contains the customer area.
	 */
	public static ServerMessage getCustomerArea(Hashtable<String, Object> data) {
		String username = (String) data.get("customerAreaToShipment");
		String userArea = "";
		String sql = "SELECT area FROM ekrut.user WHERE userName=?;";
		PreparedStatement pstmt;
		try {
			pstmt = DataBaseController.getConn().prepareStatement(sql);	
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				userArea = rs.getString("area");
			}
			data.put("customerArea", userArea);
			return new ServerMessage(Instruction.Customer_area_for_calculate, data);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return null;
	}
	
	/**
	* Creates a HashMap of Product and Integer for the quantity, objects from a string of products.
	* @param productsString - a string representation of the products in the order and their quantity separated by commas..
	* @return a HashMap containing the products as keys and their quantities as values.
	*/
	public static HashMap<Product, Integer> makeOrderMap(String productsString){
		HashMap<Product, Integer> orderContent  = new HashMap<Product, Integer>();
		HashMap<String, Integer> tempList = new HashMap<String, Integer>();
		tempList = setContent(productsString);
		Set<String> listOfProduct = tempList.keySet();
		for(int i=0;i<listOfProduct.size();i++) {
			try {
				String[] listProduct = listOfProduct.toArray(new String[i]);
				Product p = makeProduct(listProduct[i]);
				orderContent.put(p, tempList.get(p.getProtductName()));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		return orderContent;
	}
	
	/**
	* Creates a HashMap of String and Integer objects from a string of products and quantities.
	* @param stringContent - a string containing a list of items in the form of :
	* "item1, quantity1, item2, quantity2, ... "
	* @return a HashMap containing the items as keys and their quantities as values.
	*/
	public static HashMap<String, Integer> setContent(String stringContent) {
		HashMap<String, Integer> map  = new HashMap<String, Integer>();
		String[] items = stringContent.split(",");
		for (int i = 0; i < items.length-1; i += 2) {
			String key = items[i];
			int value = Integer.parseInt(items[i + 1]);
			map.put(key, value);
		}
		return map;
	}

	public static ArrayList<String> setProducts(String orderString) {
		ArrayList<String> productList  = new ArrayList<String>();
		String[] substrings = orderString.split("[^a-zA-Z]");
		for (String substring : substrings) {
			if (!substring.isEmpty()) {
				productList.add(substring);
			}
		}
		return productList;
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
	 * Returns the corresponding SupplyMethod enum for the given string.
	 * @param supplyMethod the string representation of the supply method.
	 * @return the corresponding SupplyMethod enum, or null if no match is found or the input is null.
	 */
	private static SupplyMethod getEnumSupplyMethod(String supplyMethod) {
		if(supplyMethod == null) {return null;}
		for(SupplyMethod i : SupplyMethod.values()){
			if(supplyMethod.equals(i.name())) {
				return i;
			}
		}
		return null;
	}
	
	/**
	 * Returns the corresponding CustomerShippmentStatus enum for the given string.
	 * @param customerShipmentStatus the string representation of the customer shipment status.
	 * @return the corresponding CustomerShippmentStatus enum, or null if no match is found or the input is null.
	 */
	private static CustomerShippmentStatus getEnumCustomerShippmentStatus(String customerShipmentStatus) {
		if(customerShipmentStatus == null) {return null;}
		for(CustomerShippmentStatus i : CustomerShippmentStatus.values()){
			if(customerShipmentStatus.equals(i.name())) {
				return i;
			}
		}
		return null;
	}
	
	/**
	 * Returns the corresponding ProccessStatus enum for the given string.
	 * @param shipmentStatus the string representation of the shipment status.
	 * @return the corresponding ProccessStatus enum, or null if no match is found or the input is null.
	 */
	private static ProccessStatus getEnumShipmentStatus(String shipmentStatus) {
		if(shipmentStatus == null) {return null;}
		for(ProccessStatus i : ProccessStatus.values()){
			if(shipmentStatus.equals(i.name())) {
				return i;
			}
		}
		return null;
	}
	
	/**
	 * Returns the corresponding Country enum for the given string.
	 * @param country the string representation of the country.
	 * @return the corresponding Country enum, or null if no match is found or the input is null.
	 */
	private static Country getEnumCountry(String country) {
		if(country == null) {return null;}
		for(Country i : Country.values()){
			if(country.equals(i.name())) {
				return i;
			}
		}
		return null;
	}
	
	/**
	 * Returns the LocalDate representation of the given Date object.
	 * @param date the Date object to be converted.
	 * @return the corresponding LocalDate object, or null if the input is null.
	 */
	private static LocalDate dateToLocalDate(Date date) {
		if(date == null) {return null;}
		else
			return ((java.sql.Date) date).toLocalDate();
	}
	
	/**
	 * Returns a Date object that is one day ahead of the given Date object.
	 * Because when you get a Date object from the DB is get one day early.
	 * @param date the Date object to be fixed.
	 * @return the corresponding Date object that is one day ahead of the input, or null if the input is null.
	 */
	private static Date fixDateDifference(Date date) {
		if(date==null) {return null;}
		LocalDate localDate = dateToLocalDate(date);
		localDate = localDate.plusDays(1);
		Date newDate = java.sql.Date.valueOf(localDate);
		return newDate;
	}
}

