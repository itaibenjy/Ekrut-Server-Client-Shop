package jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Server.EkrutServer;
import Server.ServerScreenController;
import entities.DataBaseConnectionDetails;


/**
 * The DataBaseController class appears to be responsible for creating and managing a connection to a MySQL database
 *
 */
public class DataBaseController {
	private static Connection dbConn;

	public static Connection getConn() {
		return dbConn;
	}

	/**
	 * Initial Connection to data base
	 * 
	 * @param database holds the necessary data base information for connection
	 */

	public static void connectToDataBase(DataBaseConnectionDetails database , ServerScreenController serverScreenController) {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");// .newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}

		try {
			Connection conn = DriverManager.getConnection(database.getName().replace("{IP}", database.getIp()),
					database.getUsername(), database.getPassword());
			dbConn = conn;

		} catch (SQLException ex) {
			try {
				serverScreenController.printToConsole("SQLException: " + ex.getMessage());
				serverScreenController.printToConsole("SQLState: " + ex.getSQLState());
				serverScreenController.printToConsole("VendorError: " + ex.getErrorCode());
			} catch (Exception e) {
				System.out.println("For tests");
			}
		}

	}
}