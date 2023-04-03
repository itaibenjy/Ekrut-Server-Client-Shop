package jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import Enum.Instruction;
import Server.EkrutServer;
import entities.ServerMessage;

/**
 * This Class makes use of a database and searches for a customer whether his order exists
 * and whether it is correct for the facility where he is.
 * 
 * This class uses queries:
 * Accepting orders according to the customer's order number and USERNAME.
 * Collected delivery status update
 *
 */
public class DataBasePickUpOrderController {

	/**
	 * This function receives orderNum, userName
	 * and checks whether a self-pickup order exists in the system,
	 * provided that it has not already been picked up
	 * @param data
	 * @return ServerMessage with facilityID, userName and yes or no if the order exist
	 */
	public static ServerMessage getPickUpOrder(Hashtable<String, Object> data) {
		Statement stmt;
		String orderNum = (String) data.get("orderNum");
		String userName = (String) data.get("userName");
		Hashtable<String, Object> valuesTable = new Hashtable<String, Object>();

		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT distinct actualPickUpDate,facilityID,userName\r\n"
					+ "FROM ekrut.orderselfcollection, ekrut.order\r\n"
					+ "where ekrut.orderselfcollection.orderNum = ekrut.order.orderNum and"
					+ " ekrut.orderselfcollection.orderNum = '%s' and ekrut.order.userName = '%s' ",orderNum,userName));
			
			// No order
			if (rs.next()== false) {
				valuesTable.put("Exists","No");
			}
			else {
				// order exists, put values in table
				valuesTable.put("Exists","Yes");
				if (rs.getDate("actualPickUpDate") == null) {
					valuesTable.put("null", "yes");
				}
				else {
					valuesTable.put("null", "no");
					valuesTable.put("actualPickUpDate", rs.getDate("actualPickUpDate"));
				}	
				valuesTable.put("facilityID", rs.getString("facilityID"));
				valuesTable.put("userName", rs.getString("userName"));

			}
			
			return new ServerMessage(Instruction.PickUp_Order_Values, valuesTable);
			
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get getPickUpOrder failed");
			e.printStackTrace();
			return null;
		} 
		
	}
	
	/**
	 * This function gets orderNum and updates its order pick date -> current date
	 * @param data
	 * @return ServerMessage without info
	 */
	
	public static ServerMessage setPickUpDate(Hashtable<String, Object> data) {

		String orderNum = (String) data.get("orderNum");
		String actualPickUpDate = "";
		// get the current date
		actualPickUpDate = actualPickUpDate + java.time.LocalDate.now();
		

		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn().prepareStatement("UPDATE orderselfcollection" +
																" SET actualPickUpDate = ?" +
																" WHERE (orderNum = ?)");

			stmt.setString(1, actualPickUpDate);
			stmt.setString(2, orderNum);
			stmt.executeUpdate();
			return new ServerMessage(Instruction.Nothing_To_Get);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query Update set PickUpDate failed");
			e.printStackTrace();
			return null;
		} 
	}

	

}
