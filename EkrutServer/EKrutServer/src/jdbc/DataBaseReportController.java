package jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import Enum.Area;
import Enum.Instruction;
import Enum.ReportType;
import Server.EkrutServer;
import entities.CustomerReport;
import entities.Facility;
import entities.InventoryReport;
import entities.OrderReport;
import entities.ServerMessage;

/**
 * Controller to deal with reports . Get from DB and Set to DB monthley report
 * from all types
 * 
 *
 */
public class DataBaseReportController {

	/**
	 * @param data from client
	 * @return Server Messeege with the wanted report
	 */
	public static ServerMessage get_Report_Area_Manager(Hashtable<String, Object> data) {
		OrderReport order_report = new OrderReport();
		CustomerReport customer_report = new CustomerReport();
		InventoryReport inventory_report = new InventoryReport();
		String areaData = (String) data.get("Area");
		String yearString = (String) data.get("Year");
		String monthString = (String) data.get("Month");
		String typeString = (String) data.get("Type");
		String facilityId = "";

		Hashtable<String, Object> data_Report = new Hashtable<String, Object>();
		Statement stmt;

		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs;
			switch (typeString) {
			case "InventoryReport":
				// Query -> Search For Order Inventory
				facilityId = (String) data.get("Facility");
				rs = quarey_Inventory_Reports(stmt, areaData, yearString, monthString, facilityId);
				if (!rs.next())
					return new ServerMessage(Instruction.Report_Area_Manager_NotFound);
				inventory_report = create_Inventory_Report(rs, inventory_report);
				data_Report.put("Report", inventory_report);
				try {
					EkrutServer.getServer().getServerGui().printToConsole(
							"Found Inventory Report " + inventory_report.getMonth() + "/" + inventory_report.getYear());

				} catch (Exception e) {
					System.out.println("Tests : Found Inventory Report " + inventory_report.getMonth() + "/"
							+ inventory_report.getYear());
				}
				break;

			case "CustomerReport":
				// Query -> Search For Customer Report
				rs = quarey_Monthley_Customer_Reports(stmt, areaData, yearString, monthString);
				if (!rs.next())
					return new ServerMessage(Instruction.Report_Area_Manager_NotFound);
				customer_report = create_Coustomer_Report(rs, customer_report);
				data_Report.put("Report", customer_report);
				EkrutServer.getServer().getServerGui().printToConsole(
						"Found Customer Report " + customer_report.getMonth() + "/" + customer_report.getYear());
				break;
			case "OrderReport":
				order_report = new OrderReport();
				// Query -> Search For Order Report
				rs = quarey_Monthley_Order_Reports(stmt, areaData, yearString, monthString);
				if (!rs.next())
					return new ServerMessage(Instruction.Report_Area_Manager_NotFound);
				order_report = create_Order_Report(rs, order_report);
				data_Report.put("Report", order_report);
				EkrutServer.getServer().getServerGui()
						.printToConsole("Found Order Report " + order_report.getMonth() + "/" + order_report.getYear());
				break;

			default:
				break;
			}
			return new ServerMessage(Instruction.Report_Area_Manager_Found, data_Report);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query Get_Area_Manager_Details failed");
			return null;

		}
	}

	/**
	 * First Query -> Search For Order Report
	 * 
	 * @param stmt        used to execute the query and return the result set.
	 * @param area        from user combobox
	 * @param yearString  from user combobox
	 * @param monthString from user combobox
	 * @return ResultSet Of Report
	 * @throws SQLException
	 */
	private static ResultSet quarey_Monthley_Order_Reports(Statement stmt, String area, String yearString,
			String monthString) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				String.format("SELECT * FROM monthleyorderreports where year=\"%s\" and month=\"%s\" and area=\"%s\";",
						yearString, monthString, area));
		return rs;
	}

	/**
	 * Get Order Report
	 * 
	 * @param rs ResultSet Of Report
	 * @throws SQLException
	 */
	private static OrderReport create_Order_Report(ResultSet rs, OrderReport order_report) throws SQLException {
		order_report.setReportId(rs.getInt(1));
		order_report.setArea(getEnumUserArea(rs.getString(2)));
		order_report.settempFacilities(rs.getString(3));
		order_report.setNumOfTotalOrders(rs.getInt(4));
		order_report.setMonth(rs.getInt(5));
		order_report.setYear(rs.getInt(6));
		order_report.setTotalPrice(rs.getFloat(7));
		order_report.setSelfColletionTotal(rs.getInt(8));
		order_report.setLocalColletionTotal(rs.getInt(9));
		order_report.setReportType(ReportType.OrderReport);
		order_report.setShipmentColletionTotal(rs.getInt(10));
		return order_report;

	}

	/**
	 * Query -> Search For Customer Report
	 * 
	 * @param stmt        used to execute the query and return the result set.
	 * @param area        from user combobox
	 * @param yearString  from user combobox
	 * @param monthString from user combobox
	 * @return ResultSet Of Report
	 * @throws SQLException
	 */
	private static ResultSet quarey_Monthley_Customer_Reports(Statement stmt, String area, String yearString,
			String monthString) throws SQLException {
		ResultSet rs = stmt.executeQuery(String.format(
				"SELECT * FROM monthleycustomerreports where year=\"%s\" and month=\"%s\" and area=\"%s\";", yearString,
				monthString, area));
		return rs;
	}

	/**
	 * Get Customer Report
	 * 
	 * @param rs ResultSet Of Report
	 * @throws SQLException
	 */
	private static CustomerReport create_Coustomer_Report(ResultSet rs, CustomerReport customer_report)
			throws SQLException {
		customer_report.setReportId(rs.getInt(1));
		customer_report.setCustomerTempString(rs.getString(2));
		customer_report.setArea(getEnumUserArea(rs.getString(3)));
		customer_report.setBiggestOrderCountUser(rs.getString(4));
		customer_report.setMostExpenseUser(rs.getString(5));
		customer_report.setMonth(rs.getInt(6));
		customer_report.setYear(rs.getInt(7));
		customer_report.setReportType(ReportType.CustomerReport);
		return customer_report;

	}

	/**
	 * @param stmt        used to execute the query and return the result set.
	 * @param area        filter the results based on the area of the inventory
	 *                    report.
	 * @param yearString  filter the results based on the year of the inventory
	 *                    report.
	 * @param monthString filter the results based on the month of the inventory
	 *                    report
	 * @param facilityId  filter the results based on the facility ID of the
	 *                    inventory report.
	 * @return ALL Inventory_Customer_Reports
	 * @throws SQLException
	 */
	private static ResultSet quarey_Inventory_Reports(Statement stmt, String area, String yearString,
			String monthString, String facilityId) throws SQLException {
		ResultSet rs = stmt.executeQuery(String.format(
				"SELECT * FROM monthleyinventoryreports where year=\"%s\" and month=\"%s\" and area=\"%s\" and facilityID=\"%s\";",
				yearString, monthString, area, facilityId));
		return rs;
	}

	/**
	 * Get Inventory Report
	 * 
	 * @param rs ResultSet Of Report
	 * @throws SQLException
	 */
	private static InventoryReport create_Inventory_Report(ResultSet rs, InventoryReport inventory_report)
			throws SQLException {
		inventory_report = new InventoryReport(rs.getInt(1), getEnumUserArea(rs.getString(4)),
				ReportType.InventoryReport, rs.getInt(6), rs.getInt(5), rs.getString(2), rs.getString(3),
				rs.getString(7), rs.getInt(8), rs.getInt(9), rs.getString(10));
		return inventory_report;
	}

	/**
	 * @param userArea - area from quert
	 * @return Area and vertify we are using one of the Areas defined
	 */
	private static Area getEnumUserArea(String userArea) {
		for (Area i : Area.values()) {
			if (userArea.equals(i.name()))
				return i;
		}
		return null;
	}

	/*---------------------------------------------------------------Generate-Report-------------------------------------------------------*/

	/**
	 * generate_Report Create Report Every End of Month
	 */
	public static void generate_Report() {
		// check if this is our first time
		if (!existOrderReport()) {
			if (!existInventoryReport()) {
				if (!existCostumerReport()) {
					for (Area area : Area.values()) {
						// generate reports
						generate_Customer_Report(area);
						generate_Order_Report(area);
						generate_Inventory_Report(area);
						EkrutServer.getServer().getServerGui()
								.printToConsole("Shipment Report Of " + java.time.LocalDate.now().getMonth() + "/"
										+ java.time.LocalDate.now().getYear()
										+ "\nGet All Delayed Payments From Subscribers ");

						// resets the debtAmount of subscribers
						DataBaseUserController.resetsDebtAmount();
					}
				}
			}
		}
	}

	/**
	 * @return boolean true / false after checking if this report already generate
	 */
	private static boolean existCostumerReport() {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(
					String.format("SELECT * FROM ekrut.monthleycustomerreports WHERE year=\"%s\" and month=\"%s\";",
							java.time.LocalDate.now().getYear(), java.time.LocalDate.now().getMonthValue()));
			while (rs.next())
				return true;
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("Not Exist Costumer Report");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This function checking if this report already generate
	 * 
	 * @return boolean exist Inventory Report
	 */
	private static boolean existInventoryReport() {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(
					String.format("SELECT * FROM ekrut.monthleyinventoryreports WHERE year=\"%s\" and month=\"%s\";",
							java.time.LocalDate.now().getYear(), java.time.LocalDate.now().getMonthValue()));
			while (rs.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This function checking if this report already generate
	 * 
	 * @return boolean exist Order Report
	 */
	private static boolean existOrderReport() {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(
					String.format("SELECT * FROM ekrut.monthleyorderreports WHERE year=\"%s\" and month=\"%s\";",
							java.time.LocalDate.now().getYear(), java.time.LocalDate.now().getMonthValue()));
			while (rs.next())
				return true;
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("Not Exist Order Report");
			e.printStackTrace();
		}
		return false;
	}

	/*---------------------------------------------------------------Customer-Report-------------------------------------------------------*/

	/**
	 * @param area - Area To Genrate Report on Him Create Customer_Report and insert
	 *             To DB
	 * 
	 */
	public static void generate_Customer_Report(Area area) {
		ResultSet rs;
		Statement stmt;
		HashMap<String, Float[]> searchForMAXHashMap = new HashMap<String, Float[]>();
		float MaxTotalPrice = 0;
		String userNameMaxTotalPrice = "";
		float MaxTotalCount = 0;
		String userNameMaxTotalCount = "";
		boolean flag = false;
		int year = 0, month = 0, length = 0;

		try {
			stmt = DataBaseController.getConn().createStatement();
			rs = create_Monthley_Customer_Reports(stmt);
			while (rs.next()) {

				String tempData = rs.getString(6);
				String[] dateArray = tempData.split("-");
				year = Integer.parseInt(dateArray[0]);
				month = Integer.parseInt(dateArray[1]);

				if (java.time.LocalDate.now().getMonthValue() == month && java.time.LocalDate.now().getYear() == year) {

					if (checkAreaOfFacility(rs.getString(2), area.toString())) {
						// ordersCount totalPrice
						flag = true;
						Float[] array = { (float) 1, (float) 0 };

						String userName = rs.getString(3);// UserName
						if (searchForMAXHashMap.containsKey(userName)) {
							array = searchForMAXHashMap.get(userName);
							array[0] += 1; // Add order Count
							array[1] += rs.getFloat(5);// Add total Price
							searchForMAXHashMap.put(userName, array); // Add to Map

						} else {
							array[1] += rs.getFloat(5);// Add total Price
							searchForMAXHashMap.put(rs.getString(3), array); // Add to Map
						}
						// SearchForMax User Order The Most from all users: username
						if (array[0] > MaxTotalCount) {
							MaxTotalCount = array[0];
							userNameMaxTotalCount = userName;
						}

						// SearchForMax Price Total From all Users : username
						if (array[1] > MaxTotalPrice) {
							MaxTotalPrice = array[0];
							userNameMaxTotalPrice = userName;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String customerLiString = "";
		length = 0;
		if (flag == true) {
			//// | a | cohershir |
			//// |[1.0,300] | [2.0,170] |
			for (String key : searchForMAXHashMap.keySet()) {
				Float[] value = searchForMAXHashMap.get(key);
				float total = value[1];
				customerLiString += key + "," + String.valueOf((int) total) + ",";
				length = customerLiString.length();
			}

			customerLiString = customerLiString.substring(0, customerLiString.length() - 1);
		} else {
			EkrutServer.getServer().getServerGui()
					.printToConsole("No Orders For Area:" + area + " Customer Report Not Generated");
			return;
		}
		try {
			PreparedStatement stmt1;
			stmt1 = DataBaseController.getConn().prepareStatement(
					"INSERT INTO ekrut.monthleycustomerreports ( customerList, area, biggestOrderCountUser, mostExpenseUser, month, year) VALUES ( ?, ?, ?, ?, ?, ?);");
			stmt1.setString(1, customerLiString);
			stmt1.setString(2, area.toString());
			stmt1.setString(3, userNameMaxTotalCount);
			stmt1.setString(4, userNameMaxTotalPrice);
			stmt1.setInt(5, java.time.LocalDate.now().getMonthValue());
			stmt1.setInt(6, java.time.LocalDate.now().getYear());
			stmt1.executeUpdate();
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("Cant Insert To Data Base the report ");
		}

	}

	/**
	 * Function that calculte if a facility is in the requested area
	 * 
	 * @param currnt_facilityID
	 * @param currnt_area
	 * @return
	 */
	private static boolean checkAreaOfFacility(String currnt_facilityID, String currnt_area) {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(
					String.format("SELECT * FROM facilities WHERE facilityId = \"%s\" AND facilityArea= \"%s\";",
							currnt_facilityID, currnt_area));

			// query return empty ResultSet--> the username do not found in DB.
			if (!rs.next()) {
				return false;
			}
			return true;

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query order details failed");
			e.printStackTrace();
			return (Boolean) null;
		}

	}

	private static ResultSet create_Monthley_Customer_Reports(Statement stmt) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT * FROM ekrut.order");
		return rs;
	}

	/*---------------------------------------------------------------Order-Report-------------------------------------------------------*/

	/**
	 * @param area1 - Area To Genrate Report on Him Create Customer_Report and
	 *              insert To DB
	 * 
	 */
	public static void generate_Order_Report(Area area1) {
		String area = area1.toString();
		boolean flag_forOrders = false;
		int selfCollection_Total = 0, localOrder_Total = 0, shipment_Total = 0;
		int count_Orders = 0;
		float total_Price = 0;
		HashMap<String, Integer> orders_in_Facility = new HashMap<String, Integer>();
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM ekrut.order;"));
			while (rs.next()) {
				String tempData = rs.getString(6);
				// splitted the date to year, month and days
				String[] dateArray = tempData.split("-");
				String year = dateArray[0];
				String month = dateArray[1];
				String day = dateArray[2];
				int integerYear = Integer.parseInt(year);
				int integerMonth = Integer.parseInt(month);
				// cheack if the time report is in this month and year
				if (java.time.LocalDate.now().getMonthValue() == integerMonth
						&& java.time.LocalDate.now().getYear() == integerYear) {
					String currnt_facilityID = rs.getString(2); // facilityID
					// check if the facilityID from the area that we want
					if (checkAreaOfFacility(currnt_facilityID, area)) {
						flag_forOrders = true;
						if (orders_in_Facility.containsKey(currnt_facilityID)) {
							int countOrd = orders_in_Facility.get(currnt_facilityID);
							orders_in_Facility.put(currnt_facilityID, countOrd += 1); // add order of facility
						} else {
							orders_in_Facility.put(currnt_facilityID, 1); // first time there is order in this facility
						}
						count_Orders++;
						float curr_totalPrice = Float.parseFloat(rs.getString(5));
						total_Price += curr_totalPrice;
						String Curr_supplyMethod = rs.getString(7);
						if (Curr_supplyMethod.equals("SelfCollection"))
							selfCollection_Total++;
						else if (Curr_supplyMethod.equals("Shipment"))
							shipment_Total++;
						else if (Curr_supplyMethod.equals("LocalOrder"))
							localOrder_Total++;
					}
				}
			}
			// check if we got order for this area in this month
			if (flag_forOrders) {
				insertOrderReport(area, selfCollection_Total, localOrder_Total, shipment_Total, count_Orders,
						total_Price, orders_in_Facility);
				EkrutServer.getServer().getServerGui()
						.printToConsole("Generated Order Report For Area " + area + " Date "
								+ java.time.LocalDate.now().getMonthValue() + "/"
								+ java.time.LocalDate.now().getYear());

			} else {
				EkrutServer.getServer().getServerGui()
						.printToConsole("No Orders For Area:" + area + " Order Report Not Generated");
			}
			return;

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query generated Order Report details Failed");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * This Method get relevant orders details and create Order Report
	 * 
	 * @param area:                 a string representing the area of the order
	 * @param selfCollection_Total: an integer representing the total number of
	 *                              self-collection orders
	 * @param localOrder_Total:     an integer representing the total number of
	 *                              local orders
	 * @param shipment_Total:       an integer representing the total number of
	 *                              shipment orders
	 * @param count_Orders:         an integer representing the total number of
	 *                              orders
	 * @param total_Price:          a float representing the total price of all
	 *                              orders
	 * @param orders_in_Facility:   a HashMap that stores the number of orders for
	 *                              each facility.
	 */
	private static void insertOrderReport(String area, int selfCollection_Total, int localOrder_Total,
			int shipment_Total, int count_Orders, float total_Price, HashMap<String, Integer> orders_in_Facility) {

		String temp_facilitiesOrders = "";
		for (String key : orders_in_Facility.keySet()) {
			Integer value = orders_in_Facility.get(key);
			temp_facilitiesOrders += (key + "," + String.valueOf(value) + ",");
		}
		String facilitiesOrders = temp_facilitiesOrders.substring(0, temp_facilitiesOrders.length() - 1);
		PreparedStatement stmt;
		try {

			stmt = DataBaseController.getConn().prepareStatement(
					"INSERT INTO ekrut.monthleyorderreports (area, facilitiesOrders, numOfTotalOrders, month, year, totalPrice, selfCollection_Total, localCollection_Total, shipmentCollection_Total) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
			stmt.setString(1, area);
			stmt.setString(2, facilitiesOrders);
			stmt.setInt(3, count_Orders);
			stmt.setInt(4, java.time.LocalDate.now().getMonthValue());
			stmt.setInt(5, java.time.LocalDate.now().getYear());
			stmt.setFloat(6, total_Price);
			stmt.setInt(7, selfCollection_Total);
			stmt.setInt(8, localOrder_Total);
			stmt.setInt(9, shipment_Total);
			stmt.executeUpdate();
			return;
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get user details failed");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/*---------------------------------------------------------------Inventory-Report-----------------------------------------------------------*/

	/**
	 * @param area for generate inventory report function generate_Inventory_Report
	 */
	public static void generate_Inventory_Report(Area area) {
		ServerMessage sm;
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("Area", area);
		sm = DataBaseAreaManagerController.getFacilitiesInArea(data);
		ArrayList<Facility> facilitiesList = (ArrayList<Facility>) sm.getData().get("facilitiesInArea");
		for (Facility val : facilitiesList) {
			if (!(val.getFacilityId().equals("OperationCenterNorth"))
					&& !(val.getFacilityId().equals("OperationCenterUAE"))
					&& !(val.getFacilityId().equals("OperationCenterSouth"))) {

				if (val.getProductsInFacility() != null && !val.getProductsInFacility().isEmpty()) {
					// initialize the paramter to check for min and max
					int total_products = 0;
					int small_inventory = 0;
					int biggest_inentory = val.getProductsInFacility().get(0).getPaseedThreshold();
					String biggest_inentory_string = val.getProductsInFacility().get(0).getProtductName();
					String temp_invetorylist = "";
					String temp_passedThresholdList = "";
					for (int i = 0; i < val.getProductsInFacility().size(); i++) {

						temp_invetorylist += val.getProductsInFacility().get(i).getProtductName() + ","
								+ val.getProductsInFacility().get(i).getQuantity() + ",";
						if (val.getProductsInFacility().get(i).getPaseedThreshold() != 0) {
							temp_passedThresholdList += val.getProductsInFacility().get(i).getProtductName() + ","
									+ val.getProductsInFacility().get(i).getPaseedThreshold() + ",";
						}
						total_products += 1;

						// count of products that not passed threshold level
						if (0 == val.getProductsInFacility().get(i).getPaseedThreshold()) {
							small_inventory += 1;
						}
						// look for the product with the highest inventory
						if (biggest_inentory < val.getProductsInFacility().get(i).getPaseedThreshold()) {
							biggest_inentory = val.getProductsInFacility().get(i).getPaseedThreshold();
							biggest_inentory_string = val.getProductsInFacility().get(i).getProtductName();
						}

					}
					String invetorylist = "", passedThresholdList = "";
					if (!temp_invetorylist.equals("")) {
						invetorylist = temp_invetorylist.substring(0, temp_invetorylist.length() - 1);

					}
					if (!temp_passedThresholdList.equals("")) {
						passedThresholdList = temp_passedThresholdList.substring(0,
								temp_passedThresholdList.length() - 1);
					}

				try {
					insertInventoryReport(val.getFacilityId(), invetorylist, area, biggest_inentory_string,
							small_inventory, total_products, passedThresholdList);
				} catch (SQLException e) {
					System.out.println("tests");
				}	

				}
			}
		}
	}

	/**
	 * @param facilityId:              a string representing the ID of the facility
	 * @param invetorylist:            a string representing a list of inventory
	 *                                 items
	 * @param area:                    an enumeration type representing the area of
	 *                                 the facility
	 * @param biggest_inentory_string: a string representing the biggest inventory
	 *                                 item
	 * @param small_inventory:         an integer representing the number of small
	 *                                 inventory items
	 * @param total_products:          an integer representing the total number of
	 *                                 products
	 * @param passedThresoldList:      a string representing a list of products that
	 *                                 have passed a threshold. insert a new
	 *                                 inventory report into a table
	 *                                 monthleyinventoryreports
	 */
	public static void insertInventoryReport(String facilityId, String invetorylist, Area area,
			String biggest_inentory_string, int small_inventory, int total_products, String passedThresoldList) throws SQLException{
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn().prepareStatement(
					"INSERT INTO ekrut.monthleyinventoryreports (facilityID, facilityInventoryList, area, month, year, passThresholdMax, countNotPassed, numOfTotalProducts, passedThresHold) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?);");

			stmt.setString(1, facilityId);
			stmt.setString(2, invetorylist);
			stmt.setString(3, area.toString());
			stmt.setInt(4, java.time.LocalDate.now().getMonthValue());
			stmt.setInt(5, java.time.LocalDate.now().getYear());
			stmt.setString(6, biggest_inentory_string);
			stmt.setInt(7, small_inventory);
			stmt.setInt(8, total_products);
			stmt.setString(9, passedThresoldList);
			stmt.executeUpdate();
			return;
		} catch (SQLException e) {
			try {
				EkrutServer.getServer().getServerGui().printToConsole("The query create inventory report failed");
			} catch (Exception e2) {
				throw e;		
				
			}
		}
	}

	/*---------------------------------------------------------------Shipment-Report-----------------------------------------------------------*/
	/**
	 * The method uses two Hashmaps, shipmentDistrbutionCount and
	 * shipmentDistrbutionExpense, to store the count and expense of shipments for
	 * different areas (South, North, UAE). The method uses a query to retrieve all
	 * shipment order reports from the database, and iterates through the results
	 * using a ResultSet object
	 * 
	 * @param data
	 * @return the Shipment report
	 */
	public static ServerMessage generate_Shipment_Report(Hashtable<String, Object> data) {
		String yearString = (String) data.get("Year");
		String monthString = (String) data.get("Month");
		Date date;
		ResultSet rs;
		rs = quarey_Shipment_Order_Reports();
		HashMap<String, Integer> shipmentDistrbutionCount = new HashMap<>();
		HashMap<String, Float> shipmentDistrbutionExpense = new HashMap<>();
		Hashtable<String, Object> datashipment = new Hashtable<String, Object>();
		shipmentDistrbutionCount.put("South", 0);
		shipmentDistrbutionExpense.put("South", (float) 0);
		shipmentDistrbutionCount.put("North", 0);
		shipmentDistrbutionExpense.put("North", (float) 0);
		shipmentDistrbutionCount.put("UAE", 0);
		shipmentDistrbutionExpense.put("UAE", (float) 0);
		shipmentDistrbutionExpense.put("Year", (float) Integer.parseInt(yearString));
		shipmentDistrbutionExpense.put("Month", (float) Integer.parseInt(monthString));
		boolean flag = false;
		int yearInt = 0, monthInt = 0;
		try {
			while (rs.next()) {
				date = rs.getDate("orderDate");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				monthInt = calendar.get(calendar.MONTH) + 1;
				yearInt = calendar.get(calendar.YEAR);

				if (monthInt == Integer.parseInt(monthString) && yearInt == Integer.parseInt(yearString)) {
					flag = true;
					String operation = rs.getString("facilityID");
					String replacedString = operation.replaceFirst("OperationCenter", "");
					shipmentDistrbutionCount.put(replacedString, shipmentDistrbutionCount.get(replacedString) + 1);
					shipmentDistrbutionExpense.put(replacedString,
							(float) (shipmentDistrbutionExpense.get(replacedString) + rs.getFloat("totalPrice")));
				}
			}
			rs.close();

		} catch (NumberFormatException e) {
			datashipment.put("status", "NOT-OK");
			return new ServerMessage(Instruction.Shipment_Report_Return, datashipment);
		} catch (SQLException e) {
			datashipment.put("status", "NOT-OK");
			return new ServerMessage(Instruction.Shipment_Report_Return, datashipment);

		}
		if (flag == true)
			datashipment.put("status", "OK");
		else
			datashipment.put("status", "NOT-OK");

		datashipment.put("shipmentDistrbutionCount", shipmentDistrbutionCount);
		datashipment.put("shipmentDistrbutionExpense", shipmentDistrbutionExpense);
		return new ServerMessage(Instruction.Shipment_Report_Return, datashipment);
	}

	/**
	 * Create Shipment Report
	 * 
	 * @return ResultSet Of Shipment Report
	 * 
	 */
	private static ResultSet quarey_Shipment_Order_Reports() {
		Statement stmt;
		ResultSet rs = null;
		try {
			stmt = DataBaseController.getConn().createStatement();
			rs = stmt.executeQuery(String.format(
					"SELECT * FROM ekrut.order WHERE( facilityID='OperationCenterNorth' OR facilityID ='OperationCenterSouth' OR  facilityID='OperationCenterUAE' );"));
		} catch (SQLException e) {

			EkrutServer.getServer().getServerGui().printToConsole("Cant Access To Shipment Order");
			return null;
		}
		return rs;
	}

}