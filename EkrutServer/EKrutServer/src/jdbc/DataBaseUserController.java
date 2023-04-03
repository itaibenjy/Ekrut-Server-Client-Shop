package jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import Enum.Area;
import Enum.Instruction;
import Enum.Role;
import Enum.UserStatus;
import Server.EkrutServer;
import entities.RegisteredCustomer;
import entities.ServerMessage;
import entities.Subscriber;
import entities.User;

/**
 * Controller to deal with user's details . Get from DB and Set to DB or update
 * in DB user's details.
 */
public class DataBaseUserController {

	/**
	 * This method is used to get the user as User or RegisteredCustomer or
	 * Subscriber.
	 * 
	 * @param data a Hashtable containing the following keys: "username", "password"
	 *             and "isForRegistration"
	 * @return a ServerMessage object containing the user details if found, or an
	 *         instruction indicating that the user was not found.
	 */
	public static ServerMessage getUser(Hashtable<String, Object> data) {
		String username = (String) data.get("username");
		String password = (String) data.get("password");
		boolean isForRegistration = (Boolean) data.get("isForRegistration");
		Hashtable<String, Object> userTypeTable = getUserTypeDetails(username, password);
		if (userTypeTable == null) {
//		   EkrutServer.getServer().getServerGui().printToConsole("The user: "+username+" with password: "+password+" not found!");
			Hashtable<String, Object> userNotFoundTable = new Hashtable<String, Object>();
			userNotFoundTable.put("isForRegistration", isForRegistration);
			return new ServerMessage(Instruction.User_not_found, userNotFoundTable);
		}
		userTypeTable.put("isForRegistration", isForRegistration);
		return new ServerMessage(Instruction.User_found, userTypeTable);
	}

	/**
	 * This method is used to get the user as a subscriber.
	 * 
	 * @param data a Hashtable containing the following keys: "username" and
	 *             "isForRegistration"
	 * @return a ServerMessage object containing the subscriber details if found, or
	 *         an instruction indicating that the subscriber was not found
	 */
	public static ServerMessage getSubscriber(Hashtable<String, Object> data) {
		String username = (String) data.get("username");
		boolean isForRegistration = (Boolean) data.get("isForRegistration");
		Statement stmt;
		try {

			/**
			 * Get the password of the subscriber.
			 */
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt
					.executeQuery(String.format("SELECT password FROM user WHERE userName = \"%s\";", username));

			if (!rs.next()) {
				EkrutServer.getServer().getServerGui().printToConsole("The subscriber: " + username + " not found!");
				return new ServerMessage(Instruction.User_not_found, null);
			}
			String password = rs.getString(1);

			/**
			 * 
			 * Get all the details of the subscriber.
			 */
			Hashtable<String, Object> userTypeTable = getUserTypeDetails(username, password);
			if (userTypeTable == null) {
				EkrutServer.getServer().getServerGui()
						.printToConsole("The subscriber: " + username + "with password: " + password + " not found!");
				return new ServerMessage(Instruction.User_not_found, null);
			}
			userTypeTable.put("isForRegistration", isForRegistration);
			return new ServerMessage(Instruction.User_found, userTypeTable);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get user details failed");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to get the user as user or registered customer or
	 * subscriber details.
	 * 
	 * @param username the username of the user
	 * @param password the password of the user
	 * @return a Hashtable containing the user details if found, or null if the user
	 *         was not found
	 */

	public static Hashtable<String, Object> getUserTypeDetails(String username, String password) {
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		User retUserType;

		/**
		 * Get general user's details.
		 */
		User user = getUserDetalis(username, password);
		if (user == null) {
			return null;
		} else if (user.getRole().equals(Role.RegisteredCustomer)) {

			/**
			 * Get user as registeredCustomer.
			 */
			RegisteredCustomer retRegisteredCustomer = getRegisteredCustomerDetalis(user.getUserName());
			retUserType = setuserDetailsAccordingToType(user, retRegisteredCustomer, Role.RegisteredCustomer);
			data.put("user", retUserType);
			data.put("secondRole", Role.NoRole);
			return data;

			/**
			 * Get user as subscriber.
			 */
		} else if (user.getRole().equals(Role.Subscriber)) {
			Subscriber retSubscriber = getSubscriberDetalis(user.getUserName());
			retUserType = setuserDetailsAccordingToType(user, retSubscriber, Role.Subscriber);
			data.put("user", retUserType);
			data.put("secondRole", Role.NoRole);
			return data;

			/**
			 * user.
			 */
		} else if (user.getRole().equals(Role.NoRole)) {
			data.put("user", user);
			data.put("secondRole", Role.NoRole);
			return data;

			/**
			 * get worker.
			 */
		} else {
			return getWorkerPermissions(user);
		}

	}

	/**
	 * This method is used to get the general user details.
	 * 
	 * @param username the username of the user
	 * @param password the password of the user
	 * @return a User object containing the user details if found, or null if the
	 *         user was not found
	 */
	public static User getUserDetalis(String username, String password) {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String
					.format("SELECT * FROM user WHERE userName = \"%s\" AND password= \"%s\";", username, password));

			/**
			 * If the username do not found in DB return null because the user not found.
			 */
			if (!rs.next()) {
				return null;
			}

			/**
			 * setting all the information of the user we got from the query using the user
			 * entity constructor.
			 */
			User retUser = new User(rs.getString("password"), rs.getInt("id"), rs.getString("firstName"),
					rs.getString("lastName"), rs.getString("email"), rs.getString("telephone"),
					getEnumRole(rs.getString("role")), rs.getBoolean("loggedIn"), rs.getString("userName"),
					getEnumUserStatus(rs.getString("UserStatus")));
			if (!(rs.getString("area") == null))
				retUser.setArea(getEnumArea(rs.getString("area")));
			return retUser;

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get user details failed");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to set the user details according to the user type.
	 * 
	 * @param generalUserdetails the general user details
	 * @param extraUserDetails   the extra user details for Subscriber or
	 *                           RegisteredCustomer.
	 * @param userRole           the role of the user, such as Subscriber or
	 *                           RegisteredCustomer.
	 * @return a User object with all the user details set according to the user
	 *         type.
	 */
	private static User setuserDetailsAccordingToType(User generalUserdetails, User extraUserDetails, Role userRole) {

		Subscriber subscriber;
		RegisteredCustomer registeredCustomer;
		if (userRole.equals(Role.Subscriber)) {

			Subscriber tempSub = (Subscriber) extraUserDetails;
			subscriber = new Subscriber(generalUserdetails.getPassword(), generalUserdetails.getId(),
					generalUserdetails.getFirstName(), generalUserdetails.getLastName(), generalUserdetails.getEmail(),
					generalUserdetails.getTelephone(), generalUserdetails.getRole(), generalUserdetails.getLoggedIn(),
					generalUserdetails.getUserName(), generalUserdetails.getUserStatus(), generalUserdetails.getArea(),
					tempSub.getCreditCardNumber(), tempSub.getExpireMonth(), tempSub.getExpireYear(), tempSub.getCvv(),
					tempSub.getSubscriberNumber(), tempSub.getDebtAmount(), tempSub.getFirstOrder());
			return subscriber;

		} else {

			RegisteredCustomer tempRegi = (RegisteredCustomer) extraUserDetails;
			registeredCustomer = new RegisteredCustomer(generalUserdetails.getPassword(), generalUserdetails.getId(),
					generalUserdetails.getFirstName(), generalUserdetails.getLastName(), generalUserdetails.getEmail(),
					generalUserdetails.getTelephone(), generalUserdetails.getRole(), generalUserdetails.getLoggedIn(),
					generalUserdetails.getUserName(), generalUserdetails.getUserStatus(), generalUserdetails.getArea(),
					tempRegi.getCreditCardNumber(), tempRegi.getExpireMonth(), tempRegi.getExpireYear(),
					tempRegi.getCvv());
			return registeredCustomer;
		}
	}

	/**
	 * This method is used to set the loggedIn attribute of a user in the database.
	 * 
	 * @param data a Hashtable containing the user object and the loggedIn
	 *             attribute.
	 * @return a ServerMessage indicating that the update is done.
	 */
	public static ServerMessage setUserLoggedIn(Hashtable<String, Object> data) {
		User user = (User) data.get("user");
		Boolean userLoggedIn = (Boolean) data.get("loggedIn");
		String username = user.getUserName();
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn().prepareStatement("UPDATE user SET loggedIn = ? WHERE userName = ?");
			stmt.setBoolean(1, userLoggedIn);
			stmt.setString(2, username);
			stmt.executeUpdate();
			try {
				if (userLoggedIn)
					EkrutServer.getServer().getServerGui()
							.printToConsole("" + user.getRole() + " With User Name " + username + "  Logged In");
				else
					EkrutServer.getServer().getServerGui()
							.printToConsole("" + user.getRole() + " With User Name " + username + "  Logged Out");
			} catch (Exception e) {
				if (userLoggedIn)
					System.out.println("Tests : " + user.getRole() + " With User Name " + username + "  Logged In");
				else
					System.out.println("Tests : " + user.getRole() + " With User Name " + username + "  Logged Out");
			}
			return new ServerMessage(Instruction.Nothing_To_Get);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query changed user loggedIn failed");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to get extra details about a user of type registered
	 * customer from the database.
	 * 
	 * @param username the username of the registered customer.
	 * @return a RegisteredCustomer object containing the additional details, or
	 *         null if the user is not found.
	 */
	private static RegisteredCustomer getRegisteredCustomerDetalis(String username) {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt
					.executeQuery(String.format("SELECT * FROM registeredcustomer WHERE userName = \"%s\";", username));

			if (!rs.next()) {
				return null;
			}
			/**
			 * setting all the information of the registered customer we got from the query.
			 */
			RegisteredCustomer retRegisteredCustomer = new RegisteredCustomer(rs.getString("creditCardNumber"),
					rs.getString("expireMonth"), rs.getString("expireYear"), rs.getString("cvv"));
			return retRegisteredCustomer;

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get registered customer details failed");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to get extra details about a user of type subscriber from
	 * the database.
	 * 
	 * @param username the username of the subscriber.
	 * @return a Subscriber object containing the additional details, or null if the
	 *         user is not found.
	 */
	private static Subscriber getSubscriberDetalis(String username) {

		/**
		 * Get extra details as registered customer.
		 */
		RegisteredCustomer retRegisteredCustomer = getRegisteredCustomerDetalis(username);
		if (retRegisteredCustomer == null) {
			return null;
		}

		/**
		 * Get extra details as Subscriber.
		 */
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt
					.executeQuery(String.format("SELECT * FROM subscriber WHERE userName = \"%s\";", username));

			if (!rs.next()) {
				return null;
			}
			Subscriber retSubscriber = new Subscriber(retRegisteredCustomer.getCreditCardNumber(),
					retRegisteredCustomer.getExpireMonth(), retRegisteredCustomer.getExpireYear(),
					retRegisteredCustomer.getCvv(), rs.getInt("subscriberNum"), rs.getFloat("debtAmount"),
					rs.getBoolean("firstOrder"));
			return retSubscriber;

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get user details failed");
			e.printStackTrace();
			return null;

		}
	}

	/**
	 * @param data hashtable that include the area that we want to seach the users
	 *             that not approved in it.
	 * @return array of relevant users
	 */
	public static ServerMessage getAllNotApprovedUsers(Hashtable<String, Object> data) {
		String area = (String) data.get("area");
		Statement stmt;
		ArrayList<User> userList = new ArrayList<User>();
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(
					String.format("SELECT * FROM user WHERE UserStatus='Not_Approved' AND area = \"%s\";", area));
			while (rs.next()) {
				User retUser = new User(rs.getString("password"), rs.getInt("id"), rs.getString("firstName"),
						rs.getString("lastName"), rs.getString("email"), rs.getString("telephone"),
						getEnumRole(rs.getString("role")), rs.getBoolean("loggedIn"), rs.getString("userName"),
						getEnumUserStatus(rs.getString("UserStatus")));
				userList.add(retUser);
			}
			// save the array of users in array.
			Hashtable<String, Object> userTable = new Hashtable<String, Object>();
			userTable.put("AllNotApprovedUsers", userList);
			EkrutServer.getServer().getServerGui().printToConsole("The query get All Not Approved Users succeeded");
			return new ServerMessage(Instruction.All_NotApprovedUser_list, userTable);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get All Not Approved Users failed");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to update users's status.
	 * 
	 * @param data a hashtable that include arraylist with usersname of users that
	 *             the Area Manager Confirm
	 * @return ServerMessage
	 */
	@SuppressWarnings("unchecked")
	public static ServerMessage updateUserStatus(Hashtable<String, Object> data) {
		ArrayList<String> users_Array = (ArrayList<String>) data.get("users");
		PreparedStatement stmt;
		try {

			// goes over each user and change is UserStatus to "Active"
			for (String val_users_Array : users_Array) {
				EkrutServer.getServer().getServerGui()
						.printToConsole("Activated the status of the user: " + val_users_Array);
				stmt = DataBaseController.getConn()
						.prepareStatement("UPDATE user SET UserStatus = 'Active' WHERE userName = ?");
				stmt.setString(1, val_users_Array);
				stmt.executeUpdate();
			}
			EkrutServer.getServer().getServerGui().printToConsole("Users status updated succsessfully.");
			return new ServerMessage(Instruction.Nothing_To_Get);
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query update User Status failed");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to get all the usernames of the subscribers. this
	 * function goes over subscribers and return their userName
	 * 
	 * @return the names of all the subscribers we have in array
	 */
	public static ServerMessage getSubscriberUserName() {
		Statement stmt;
		ArrayList<String> userList = new ArrayList<String>();
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM ekrut.subscriber;"));
			while (rs.next()) {
				userList.add(rs.getString(1));
			}
			Hashtable<String, Object> userTable = new Hashtable<String, Object>();
			userTable.put("subscribers", userList);
			return new ServerMessage(Instruction.Get_Subscriber_Data, userTable);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get Subscriber Details failed");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method gets the maximum subscriber number from the subscriber table in
	 * the database.
	 * 
	 * @param data a hashtable containing the data to be used in the method
	 * @return ServerMessage containing the maximum subscriber number in the
	 *         subscriber table.
	 */
	public static ServerMessage getSubscriberNumber(Hashtable<String, Object> data) {
		Statement stmt;
		Integer max = 0;
		try {
			stmt = DataBaseController.getConn().createStatement();
			Hashtable<String, Object> subscriberNumTable = new Hashtable<String, Object>();
			ResultSet rs = stmt.executeQuery(String.format("SELECT MAX(subscriberNum) as max FROM ekrut.subscriber"));
			if (rs.next()) {
				max = rs.getInt(1);
			}
			subscriberNumTable.put("subscriberNum", max);
			return new ServerMessage(Instruction.Subscriber_number_max, subscriberNumTable);
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get Subscriber Number failed");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * This method updates the details of a registered customer in the database. It
	 * updates the user's row in the user's table and also updates or adds a new row
	 * in the registered customer's table.
	 * 
	 * @param data A Hashtable containing the updated user details in the form of a
	 *             RegisteredCustomer object.
	 * @return A ServerMessage object indicating the success of the update
	 *         operation.
	 */
	public static ServerMessage updateRegisteredCustomerAfterRegistration(Hashtable<String, Object> data) {
		RegisteredCustomer registereCustomer = (RegisteredCustomer) data.get("updatedUser");
		PreparedStatement stmt, stmt1, stmt2;

		try {
			/** Update user's row in user's table. */
			String updateDetailsSql = "UPDATE user SET password=?,id=?,firstName=?,lastName=?,email=?,telephone=?,role=?,loggedIn=?,UserStatus=?,area=? WHERE userName=?";

			stmt = DataBaseController.getConn().prepareStatement(updateDetailsSql);

			stmt.setString(1, registereCustomer.getPassword());
			stmt.setInt(2, registereCustomer.getId());
			stmt.setString(3, registereCustomer.getFirstName());
			stmt.setString(4, registereCustomer.getLastName());
			stmt.setString(5, registereCustomer.getEmail());
			stmt.setString(6, registereCustomer.getTelephone());
			stmt.setString(7, registereCustomer.getRole().toString());
			stmt.setBoolean(8, false);
			stmt.setString(9, registereCustomer.getUserStatus().toString());
			stmt.setString(10, registereCustomer.getArea().toString());
			stmt.setString(11, registereCustomer.getUserName());
			stmt.executeUpdate();

			if (registereCustomer.getRole().equals(Role.Subscriber)) {
				/** Update user's row in reisteredCustomer's table. */
				String updateDetailsSql1 = "UPDATE registeredcustomer SET creditCardNumber=?,cvv=?,expireMonth=?,expireYear=? WHERE userName=?";

				stmt2 = DataBaseController.getConn().prepareStatement(updateDetailsSql1);
				stmt2.setString(1, registereCustomer.getCreditCardNumber());
				stmt2.setString(2, registereCustomer.getCvv());
				stmt2.setString(3, registereCustomer.getExpireMonth());
				stmt2.setString(4, registereCustomer.getExpireYear());
				stmt2.setString(5, registereCustomer.getUserName());
				stmt2.executeUpdate();

			} else {
				/** Adding new row in registered customer's table. */
				String updateDetailsSql1 = "INSERT  IGNORE INTO registeredcustomer (userName, creditCardNumber,cvv,expireMonth,expireYear) VALUES ( ?,?,?,?,?)";
				stmt1 = DataBaseController.getConn().prepareStatement(updateDetailsSql1);
				stmt1.setString(1, registereCustomer.getUserName());
				stmt1.setString(2, registereCustomer.getCreditCardNumber());
				stmt1.setString(3, registereCustomer.getCvv());
				stmt1.setString(4, registereCustomer.getExpireMonth());
				stmt1.setString(5, registereCustomer.getExpireYear());
				stmt1.executeUpdate();

			}

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui()
					.printToConsole("The query update user details to registered customer in user's table failed");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		EkrutServer.getServer().getServerGui().printToConsole("Update user: " + registereCustomer.getUserName()
				+ " details to full details of registered customer in DB succeded.");
		return new ServerMessage(Instruction.Nothing_To_Get);

	}

	/**
	 * This method updates subscriber's details in the database after a registration
	 * process. The method receives a Hashtable that contains the updated subscriber
	 * details. The method update the subscriber's details in the subscriber's table
	 * in the database.
	 * 
	 * @param data A Hashtable that contains the updated subscriber details.
	 * @return ServerMessage.
	 */
	public static ServerMessage updateSubscriberAfterRegistration(Hashtable<String, Object> data) {
		Subscriber subscriber = (Subscriber) data.get("updatedSubscriber");
		PreparedStatement stmt;
		try {
			String updateDetailsSql = "INSERT INTO subscriber (userName, debtAmount,firstOrder) VALUES ( ?,?,?)";
			stmt = DataBaseController.getConn().prepareStatement(updateDetailsSql);
			stmt.setString(1, subscriber.getUserName());
			stmt.setFloat(2, subscriber.getDebtAmount());
			stmt.setBoolean(3, subscriber.isFirstOrder());
			stmt.executeUpdate();

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query update subsriber details in DB failed");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		EkrutServer.getServer().getServerGui()
				.printToConsole("Update subscriber: " + subscriber.getUserName() + " details in DB succeded.");
		return new ServerMessage(Instruction.Nothing_To_Get);
	}

	/**
	 * This method is used to get worker's permissions. It takes a user object as
	 * input and returns a Hashtable containing the user object and the second role
	 * of this worker.
	 * 
	 * @param worker User object containing the worker's details.
	 * @return Hashtable String, Object containing the user object and second role
	 *         of the worker.
	 */
	public static Hashtable<String, Object> getWorkerPermissions(User worker) {
		User user = worker;
		Subscriber subscriber = getSubscriberDetalis(worker.getUserName());
		RegisteredCustomer registeredCustomer = getRegisteredCustomerDetalis(worker.getUserName());
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		if (subscriber == null) {
			if (registeredCustomer == null) {
				data.put("user", user);
				data.put("secondRole", Role.NoRole);
				return data;
			} else {
				user = setuserDetailsAccordingToType(worker, registeredCustomer, Role.RegisteredCustomer);
				data.put("user", user);
				data.put("secondRole", Role.RegisteredCustomer);
				return data;
			}

		} else {
			user = setuserDetailsAccordingToType(worker, subscriber, Role.Subscriber);
			data.put("user", user);
			data.put("secondRole", Role.Subscriber);
			return data;
		}

	}

	// ----------------------------------Get user details for send sms and
	// email---------------------------------------------

	/**
	 * This method is used to retrieve the contact information (username, first
	 * name, last name, email, and telephone) of a user from the database. The
	 * information is used to send email and SMS to the user(pop up window message).
	 * 
	 * @param data a Hashtable containing the username of the user in the key
	 *             userForEmailAndSMS.
	 * @return a ServerMessage object containing the user's contact information in
	 *         the key "userForSendEmailAndSMS", or null if the user is not found.
	 */
	public static ServerMessage getUserDetailsForSendEmailAndSMS(Hashtable<String, Object> data) {
		String username = (String) data.get("userForEmailAndSMS");
		User retUser = new User();
		String sql = "SELECT userName,firstName,lastName,email,telephone FROM ekrut.user WHERE userName = ?;";
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn().prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				retUser.setUserName(rs.getString("userName"));
				retUser.setFirstName(rs.getString("firstName"));
				retUser.setLastName(rs.getString("lastName"));
				retUser.setEmail(rs.getString("email"));
				retUser.setTelephone(rs.getString("telephone"));
			}
			data.put("userForSendEmailAndSMS", retUser);
			return new ServerMessage(Instruction.User_for_SMS_and_email_found, data);
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get user details failed");
			e.printStackTrace();
		}
		return null;
	}

	/*------------------------------------------------------------------*/

	/**
	 * This function will happen when their is click on "Import Data" in the server
	 * screen This method will put data of users from locl csv file to the DB
	 * 
	 * @return true if the importData success. Otherwise, false.
	 */
	public static boolean importData() {
		Statement stmt;

		try {
			stmt = DataBaseController.getConn().createStatement();
			// call to 'extracted' function
			extracted(stmt, "Ekrut_User.csv", "ekrut.user");
			extracted(stmt, "Ekrut_Registercustomer.csv", "ekrut.registeredcustomer");
			extracted(stmt, "Ekrut_Subscriber.csv", "ekrut.subscriber");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param stmt
	 * @param fileName
	 * @param tableName This method get data from fileName and put it in the table
	 *                  (=tableName) in our DB.
	 */
	private static void extracted(Statement stmt, String fileName, String tableName) throws SQLException {
		String queryImportUser = String
				.format("LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/%s' IGNORE INTO TABLE %s\n"
						+ "FIELDS TERMINATED BY ',' " + "LINES TERMINATED BY '\r\n' ;", fileName, tableName);
		stmt.executeQuery(queryImportUser);
	}

	/**
	 * This method set the debt amount of subscribers to 0 in the end of the month
	 */
	public static void resetsDebtAmount() {
		int numberOfSubscriber = countDebtAmount();
		if (numberOfSubscriber == 0)
			return;
		PreparedStatement stmt;
		try {
			int zero = 0;
			stmt = DataBaseController.getConn()
					.prepareStatement("UPDATE subscriber SET debtAmount = ? WHERE debtAmount != ? LIMIT ?");
			stmt.setInt(1, zero);
			stmt.setInt(2, zero);
			stmt.setInt(3, numberOfSubscriber);
			stmt.executeUpdate();

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query resetsDebtAmount failed");
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * @return the number of subscriber that need to reset to them the debtAmount
	 */
	private static int countDebtAmount() {
		int countDebtAmount;
		int zero = 0;
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt
					.executeQuery(String.format("SELECT COUNT(*) FROM subscriber WHERE debtAmount != \"%s\";", zero));
			if (rs.next()) {
				countDebtAmount = rs.getInt("COUNT(*)");
				return countDebtAmount; // the numbers of subscribers we need to reset their debtAmount
			}
			return 0; // there is no subscribers with debtAmount != 0

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query countDebtAmount failed");
			e.printStackTrace();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * This method is used to get the appropriate Area ENUM from a string.
	 * 
	 * @param area the string representation of the Area ENUM
	 * @return the appropriate Area ENUM if found, or null if not found.
	 */
	private static Area getEnumArea(String area) {
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

	/**
	 * This method is used to get the appropriate Role ENUM from a string.
	 * 
	 * @param role the string representation of the Role ENUM.
	 * @return the appropriate Role ENUM if found, or null if not found.
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
	 * This method is used to get the appropriate UserStatus ENUM from a string.
	 * 
	 * @param userStatus the string representation of the UserStatus ENUM.
	 * @return the appropriate UserStatus ENUM if found, or null if not found.
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

}
