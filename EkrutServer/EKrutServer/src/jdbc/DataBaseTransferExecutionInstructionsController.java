package jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import Enum.Instruction;
import Enum.ProccessStatus;
import Enum.Role;
import Enum.UserStatus;
import Server.EkrutServer;
import entities.ExecutionInstructions;
import entities.ServerMessage;
import entities.User;
/**
 * DataBase Transfer Execution Instructions Controller In this class, queries will be made for the inventory update request process
 * Queries:
 * Get all operations workers from the database.
 * Get all existing tasks from the database.
 * Create a new execution task.
 * Update an existing execution task.
 */
public class DataBaseTransferExecutionInstructionsController {
	
	/**
	 * Get all operating workers
	 * @param data
	 * @return ServerMessage with User list with all information about Opreating Workers
	 */
	public static ServerMessage getoperatingWorkerList(Hashtable<String, Object> data) {
		Statement stmt;
		ArrayList<User> OpreatingWorkerList = new ArrayList<User>();
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM user where role = 'OpreatingWorker'"));

			while (rs.next()) {
				User ow = new User();
				ow.setUserName(rs.getString(1));
				ow.setPassword(rs.getString(2));
				ow.setId(rs.getInt(3));
				ow.setFirstName(rs.getString(4));
				ow.setLastName(rs.getString(5));
				ow.setEmail(rs.getString(6));
				ow.setTelephone(rs.getString(7));
				ow.setRole(getEnumRole(rs.getString(8)));
				ow.setLoggedIn(rs.getBoolean(9));
				ow.setUserStauts(getEnumUserStatus(rs.getString(10)));
				OpreatingWorkerList.add(ow);

			}
			Hashtable<String, Object> productTable = new Hashtable<String, Object>();
			productTable.put("operatingWorker", OpreatingWorkerList);
			return new ServerMessage(Instruction.All_Operating_Workers, productTable);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get operatingWorkerList failed");
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Get all execution Instructions table
	 * @param data
	 * @return ServerMessage with ExecutionInstructions list with all information about execution Instructions table
	 */
	public static ServerMessage getexecutionInstructionsList(Hashtable<String, Object> data) {
		Statement stmt;
		ArrayList<ExecutionInstructions> executionInstructions = new ArrayList<ExecutionInstructions>();
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM executioninstructions"));

			while (rs.next()) {
				ExecutionInstructions ei = new ExecutionInstructions();
				ei.setIdExecutionInstruction(rs.getInt(1));
				ei.setUserName(rs.getString(2));
				ei.setFacilityId(rs.getString(3));
				ei.setProducts(rs.getString(4));
				ei.setStatus(getProccessStatus(rs.getString(5)));
				executionInstructions.add(ei);
			}
			Hashtable<String, Object> productTable = new Hashtable<String, Object>();
			productTable.put("executionInstructions", executionInstructions);
			return new ServerMessage(Instruction.Set_Execution_Instructions_List, productTable);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get executionInstructions failed");
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Get values and set new line in executioninstructions table
	 * @param data
	 * @return ServerMessage without information
	 */
	public static ServerMessage setExecutionInstruction(Hashtable<String, Object> data) {
		String facilityId = (String) data.get("facilityId");
		String userName = (String) data.get("userName");
		String products = (String) data.get("products");
		String status = (String) data.get("status");
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn().prepareStatement("INSERT INTO executioninstructions "
					+ "(userName, facilityId, products, status)" + " VALUES ( ?, ?, ?, ?);");
			stmt.setString(1, userName);
			stmt.setString(2, facilityId);
			stmt.setString(3, products);
			stmt.setString(4, status);
			stmt.executeUpdate();
			EkrutServer.getServer().getServerGui().printToConsole("Add new Execution Instruction");
			return new ServerMessage(Instruction.Nothing_To_Get);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query SET ExecutionInstruction failed");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get values and update old line in executioninstructions table
	 * @param data
	 * @return ServerMessage without information
	 */
	public static ServerMessage updateExecutionInstruction(Hashtable<String, Object> data) {

		int idExecutionInstruction = (int) data.get("idExecutionInstruction");
		String facilityId = (String) data.get("facilityId");
		String oldUserName = (String) data.get("oldUserName");
		String newUserName = (String) data.get("newUserName");
		String products = (String) data.get("products");
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn()
					.prepareStatement("UPDATE executioninstructions" + " SET userName = ?, products = ?"
							+ " WHERE (idExecutionInstruction = ?) AND (userName = ?) AND (facilityId = ?)");
			stmt.setString(1, newUserName);
			stmt.setString(2, products);
			stmt.setInt(3, idExecutionInstruction);
			stmt.setString(4, oldUserName);
			stmt.setString(5, facilityId);
			stmt.executeUpdate();
			EkrutServer.getServer().getServerGui().printToConsole("Update Execution Instruction");
			return new ServerMessage(Instruction.Nothing_To_Get);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query Update ExecutionInstruction failed");
			e.printStackTrace();
			return null;
		} 
	}
	
	/* ENUM getters */
	/**
	  *This method is used to get the appropriate Role ENUM from a string.
	  *@param role the string representation of the Role ENUM.
	  *@return the appropriate Role ENUM if found, or null if not found.
	  */
	private static Role getEnumRole(String role) {
		if (role == null) {
			return null;
		}
		for (Role i : Role.values()) {
			if (role.equals(i.name())) {
				return i;
			}
		}
		return null;
	}
	/**
	  *This method is used to get the appropriate UserStatus ENUM from a string.
	  *@param userStatus the string representation of the UserStatus ENUM.
	  *@return the appropriate UserStatus ENUM if found, or null if not found.
	  */
	private static UserStatus getEnumUserStatus(String userStatus) {
		if (userStatus == null) {
			return null;
		}
		for (UserStatus i : UserStatus.values()) {
			if (userStatus.equals(i.name())) {
				return i;
			}
		}
		return null;
	}
	/**
	  *This method is used to get the appropriate ProccessStatus ENUM from a string.
	  *@param proccessStatus the string representation of the ProccessStatus ENUM.
	  *@return the appropriate ProccessStatus ENUM if found, or null if not found.
	  */
	private static ProccessStatus getProccessStatus(String proccessStatus) {
		if (proccessStatus == null) {
			return null;
		}
		for (ProccessStatus i : ProccessStatus.values()) {
			if (proccessStatus.equals(i.name())) {
				return i;
			}
		}
		return null;
	}
}
