package jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import Enum.Area;
import Enum.Instruction;
import Enum.SaleStatus;
import Server.EkrutServer;
import entities.Sale;
import entities.SalePattern;
import entities.ServerMessage;
import entities.User;

/**
 * DataBaseSalesController is a class that is responsible for sales,
 * The sales are handled by the marketing manager and the marketing worker.
 * The department performs queries from and to the database.
 * 
 * The queries:
 * Receiving all Sales according to a certain area and their availability is 'active' or 'Requested'.
 * Update products in executioninstructions table by saleId.
 * Get All Sales Patterns.
 * Set new Sales Pattern.
 * Set new sale that marketing worker need to active.
 * Get all Active sales by date and time.
 * Check if first order.
 */
public class DataBaseSalesController {
	/**
	 * Get all Sales according to a certain area and their availability is 'active'
	 * or 'Requested'
	 * 
	 * @param data
	 * @return active Sales List -> saleStatus = 'Active' . AND Sales To active List
	 *         -> saleStatus = 'Requested'
	 */
	public static ServerMessage getSales(Hashtable<String, Object> data) {
		Statement stmt;
		String area = (String) data.get("area");
		ArrayList<Sale> activeSalesList = new ArrayList<Sale>();
		ArrayList<Sale> SalesToactiveList = new ArrayList<Sale>();
		try {
			// activeSalesList
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(
					String.format("SELECT * FROM sales where saleStatus = 'Active' AND area = '%s'", area));

			while (rs.next()) {
				Sale s = new Sale();
				s.setSaleId(rs.getInt("saleId"));
				s.setArea(getArea(rs.getString("area")));
				s.setSalePatternId(rs.getString("salePatternId"));
				s.setStartDate(fixDateDifference(rs.getDate("startDate")));
				s.setEndDate(fixDateDifference(rs.getDate("endDate")));
				s.setSaleStatus(getsaleStatus(rs.getString("saleStatus")));
				activeSalesList.add(s);

			}

			Hashtable<String, Object> productTable = new Hashtable<String, Object>();
			productTable.put("activeSalesList", activeSalesList);

			// SalesToactiveList
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs2 = stmt.executeQuery(
					String.format("SELECT * FROM sales WHERE saleStatus = 'Requested' AND area = '%s'", area));

			while (rs2.next()) {
				Sale s2 = new Sale();
				s2.setSaleId(rs2.getInt("saleId"));
				s2.setArea(getArea(rs2.getString("area")));
				s2.setSalePatternId(rs2.getString("salePatternId"));
				s2.setStartDate(fixDateDifference(rs2.getDate("startDate")));
				s2.setEndDate(fixDateDifference(rs2.getDate("endDate")));
				s2.setSaleStatus(getsaleStatus(rs2.getString("saleStatus")));
				SalesToactiveList.add(s2);

			}
			productTable.put("SalesToactiveList", SalesToactiveList);
			return new ServerMessage(Instruction.Get_Sales_By_Area_Success, productTable);
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get sales details failed");
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Update products in executioninstructions table by saleId
	 * 
	 * @param data
	 * @return ServerMessage without information
	 */
	public static ServerMessage updateSaleStatus(Hashtable<String, Object> data) {

		int saleId = (int) data.get("saleId");
		String status = (String) data.get("status");
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn()
					.prepareStatement("UPDATE sales" + " SET saleStatus = ?" + " WHERE saleId = ? ");
			stmt.setString(1, status);
			stmt.setInt(2, saleId);
			stmt.executeUpdate();
			EkrutServer.getServer().getServerGui().printToConsole("Marketing worker update sale status");
			return new ServerMessage(Instruction.Nothing_To_Get);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query Update SaleStatus failed");
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Get All Sales Patterns
	 * 
	 * @return ServerMessage with all SalePattern list
	 */
	public static ServerMessage getAllSalesPatterns() {
		Statement stmt;
		ArrayList<SalePattern> salesPatternsList = new ArrayList<SalePattern>();
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM ekrut.salepattern;"));
			while (rs.next()) {
				SalePattern salesPattern = new SalePattern();
				salesPattern.setSalePatternId(rs.getInt(1));
				salesPattern.setDescription(rs.getString(2));
				salesPattern.setDiscount_total(rs.getInt(3));
				salesPattern.setStartHour(rs.getInt(4));
				salesPattern.setEndHour(rs.getInt(5));
				salesPatternsList.add(salesPattern);
			}
			Hashtable<String, Object> salesPatternsTable = new Hashtable<String, Object>();
			salesPatternsTable.put("AllSalesPatterns", salesPatternsList);
			return new ServerMessage(Instruction.All_salesPatterns_list, salesPatternsTable);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query getAllSalesPatterns failed");
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Get data of sale pattern and insert him to DB - Set new Sales Pattern
	 * 
	 * @param data
	 * @return ServerMessage without information
	 */
	public static ServerMessage setSalesPattern(Hashtable<String, Object> data) {
		String descriptionInput = (String) data.get("description");
		int discountInput = (Integer) data.get("discount");
		int starthourInput = (Integer) data.get("starthour");
		int endhourInput = (Integer) data.get("endhour");

		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn()
					.prepareStatement("INSERT INTO ekrut.salepattern (description, discount, startHour, endHour) "
							+ "VALUES (?, ?, ?, ?);");
			stmt.setString(1, descriptionInput);
			stmt.setInt(2, discountInput);
			stmt.setInt(3, starthourInput);
			stmt.setInt(4, endhourInput);
			stmt.executeUpdate();
			EkrutServer.getServer().getServerGui().printToConsole("Marketing Manager add new sale pattern");
			return new ServerMessage(Instruction.Nothing_To_Get);
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query setSalesPattern failed");
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Set new sale that marketing worker need to active
	 * @param data
	 * @return ServerMessage without information
	 */
	public static ServerMessage insertNewSale(Hashtable<String, Object> data) {
		String areaInput = (String) data.get("area");
		int salePatternIDInput = (Integer) data.get("salePatternID");
		String startDateInput = (String) data.get("startDate");
		String endDateInput = (String) data.get("endDate");

		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn()
					.prepareStatement("INSERT INTO ekrut.sales (area, salePatternId, startDate, endDate, SaleStatus) "
							+ "VALUES (?, ?, ?, ?, ?);");
			stmt.setString(1, areaInput);
			stmt.setInt(2, salePatternIDInput);
			stmt.setString(3, startDateInput);
			stmt.setString(4, endDateInput);
			stmt.setString(5, "Requested");
			stmt.executeUpdate();
			EkrutServer.getServer().getServerGui().printToConsole("Marketing Manager of " + areaInput + " ask for new sale");
			return new ServerMessage(Instruction.Nothing_To_Get);
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query insertNewSale failed");
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Get all Active sales by date and time
	 * @param data include facilityID , to search for facility Discount
	 * @return discount
	 */
	public static ServerMessage getActiveDiscount(Hashtable<String, Object> data) {
		Statement stmt;
		String facilityID = (String) data.get("facility");
		User user = (User) data.get("user");
		ResultSet rs;
		int discount = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String hour = sdf.format(new Date()).substring(0, 2);
		Hashtable<String, Object> discountTable = new Hashtable<String, Object>();
		try {
			if (isSubscriberFirstOrder(user.getUserName())) {
				discountTable.put("isSubFirst", true);
			} else {
				discountTable.put("isSubFirst", false);
			}
			stmt = DataBaseController.getConn().createStatement();
			rs = stmt.executeQuery(String.format("	SELECT distinct\r\n" + "		ekrut.salepattern.discount\r\n"
					+ "	FROM\r\n" + "	    ekrut.sales,\r\n" + "	    ekrut.facilities,\r\n"
					+ "	    ekrut.salepattern\r\n" + "	WHERE \r\n"
					+ "		ekrut.sales.area = ekrut.facilities.facilityArea AND"
					+ "     ekrut.sales.salePatternId = ekrut.salepattern.salePatternId AND"
					+ "     ekrut.sales.saleStatus='Active' AND ekrut.sales.startDate <= CURDATE() AND"
					+ "     ekrut.sales.endDate >= CURDATE() AND" + "     ekrut.facilities.facilityId = '%s' AND"
					+ "     ekrut.salepattern.startHour  <= '%s' AND" + "     ekrut.salepattern.endHour > '%s';",
					facilityID, hour, hour));
			if (rs.next())
				discount = rs.getInt("discount");
			rs.close();
			discountTable.put("discount", discount);
			return new ServerMessage(Instruction.Get_Sale_Facility, discountTable);
		} catch (SQLException e) {
			e.printStackTrace();
			EkrutServer.getServer().getServerGui().printToConsole("Cant Access To Discount");
		}
		return new ServerMessage(Instruction.Get_Sale_Facility, discountTable);
	}
	
	/**
	 * Check if first order
	 * @param userName
	 * @return True if first order(1). False if it's not first order(0)
	 * @throws SQLException
	 */
	private static boolean isSubscriberFirstOrder(String userName) throws SQLException {
		Statement stmt = DataBaseController.getConn().createStatement();
		ResultSet rs = stmt
				.executeQuery(String.format("SELECT firstorder from ekrut.subscriber where username = '%s'", userName));
		int isFirst = 0;
		if (rs.next()) {
			isFirst = rs.getInt("firstOrder");
		} else {
			EkrutServer.getServer().getServerGui()
					.printToConsole("Subscriber " + userName + " not found when checking for first order sale.");
		}
		rs.close();
		return isFirst == 1 ? true : false;
	}

	// get area enum
	private static Area getArea(String area) {
		if (area == null) {
			return null;
		}
		for (Area i : Area.values()) {
			if (area.equals(i.name())) {
				return i;
			}
		}
		return null;
	}

	// get saleStatues enum
	private static SaleStatus getsaleStatus(String saleStatus) {
		if (saleStatus == null) {
			return null;
		}
		for (SaleStatus i : SaleStatus.values()) {
			if (saleStatus.equals(i.name())) {
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
