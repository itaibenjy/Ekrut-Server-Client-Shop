package jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.processor.BeanWriterProcessor;

import Enum.Area;
import Enum.Instruction;
import Enum.Role;
import Enum.UserStatus;
import Server.ServerScreenController;
import entities.DataBaseConnectionDetails;
import entities.RegisteredCustomer;
import entities.ServerMessage;
import entities.Subscriber;
import entities.User;
import junit.framework.Assert;

/**
 * Junit Test for login
 *
 */
class DataBaseUserControllerTest {
	/*---------------------------------------- Login Test-------------------------------------------*/
	User user_ceo1;
	User user_not_exist;
	User user_logged_in;
	User user_logged_out;
	RegisteredCustomer registeredCustomer;
	RegisteredCustomer workeRegisteredCustomer;
	
	Subscriber subscriber;
	Subscriber workerSubscriber;
	User no_role;
	
	   Instruction user_not_found;
	
	// General
	ServerMessage sm;
	Hashtable<String, Object> data;

	@BeforeEach
	void setUp() throws Exception {
		data = new Hashtable<>();
		DataBaseController.connectToDataBase(new DataBaseConnectionDetails("jdbc:mysql://{IP}/ekrut?serverTimezone=IST", 
				"localhost", "root", "Aa123456"),new ServerScreenController());

		/*----------------------General Test -------------------------- */

		// General
		ServerMessage sm;
		Hashtable<String, Object> data;
		user_ceo1 = new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.CEO, false, "CEO",
				UserStatus.Active, Area.North);
		registeredCustomer = new RegisteredCustomer("1", 123456789, "Kroskal", "Chris", "Kroskal@gmail.com",
				"0525381648", Role.RegisteredCustomer, false, "RegisteredCustomer", UserStatus.Active, Area.North,
				"1234123412341234", "1", "2027", "123");
		subscriber = new Subscriber("1", 123456789, "David", "Prim", "David@gmail.com", "0525381648", Role.Subscriber,
				false, "Subscriber", UserStatus.Active, Area.North, "1234123412341234", "1", "2027", "123", 1, 10,
				false);
		no_role = new User("1", 123456789, "Shlomo", "Sixt", "Shlomo@gmail.com", "0525381648", Role.NoRole, false,
				"NoRole", UserStatus.Active, Area.North);
		workeRegisteredCustomer = new RegisteredCustomer("1", 123456789, "Dana", "Shadmi", "Dana@gmail.com",
				"0525381648", Role.MarketingManager, false, "WorkerRegistered", UserStatus.Active, Area.North,
				"1234123412341234", "1", "2027", "123");
		workerSubscriber = new Subscriber("1", 123456789, "Shoul", "Cat", "Shoul@gmail.com", "0525381648", Role.MarketingWorker,
				false, "WorkerSubscriber", UserStatus.Active, Area.North, "1234123412341234", "1", "2027", "123", 2, 12,
				false);
		user_not_exist = null;
		user_not_found=Instruction.User_not_found;
		user_logged_in = new User("1", 123456789, "Dave", "Royt", "Dave@gmail.com", "0525381648", Role.AreaManager, true, "Me",
				UserStatus.Active, Area.North);
		user_logged_out = new User("1", 123456789, "Dave", "Royt", "Dave@gmail.com", "0525381648", Role.AreaManager, false, "Me",
				UserStatus.Active, Area.North);


	}

	/*---------------------------------------- Login Test-------------------------------------------*/

	/*-----------------getUserDetalis------------------- */

	/**
	 * checking : get from DB the details on correct username password
	 *  input : { "ceo" , 1 } 
	 * expected: user_ceo1
	 */
	@Test
	void getUserDetalis_Success() {
		User result = DataBaseUserController.getUserDetalis("CEO", "1");
		User expected = user_ceo1;
		Assert.assertEquals(expected, result);

	}

	/**
	 * checking : get from DB the details on wrong username password
	 *  input : { "ceo", 2 }
	 *   expected: user_not_exist
	 */
	@Test
	void getUserDetalis_UnSuccess() {
		User result = DataBaseUserController.getUserDetalis("ceo", "2");
		User expected = user_not_exist;
		Assert.assertEquals(expected, result);
	}

	/**
	 * checking : get from DB the details on null username input : { null , "1" }
	 * expected: user_not_exist
	 */
	@Test
	void getUserDetalis_Null_UserName() {
		User result = DataBaseUserController.getUserDetalis(null, "1");
		User expected = user_not_exist;
		Assert.assertEquals(expected, result);
	}

	/**
	 * checking : get from DB the details on null username input : { "ceo" , null }
	 * expected: user_not_exist
	 */
	@Test
	void getUserDetalis_Null_Password() {
		User result = DataBaseUserController.getUserDetalis("ceo", null);
		User expected = user_not_exist;
		Assert.assertEquals(expected, result);
	}
	/*-----------------getUserTypeDetails------------------- */

	/**
	 * checking : get from null user if the details username not correct input : {
	 * "ceo" , null } expected: NullPointerException -> if user not exist , the
	 * return data (HashMap <String,Object>) is defined to be null ,cant use get()
	 * function
	 */
	@Test
	void getUserTypeDetails_Not_Exist_User() {
		try {
			User result = (User) DataBaseUserController.getUserTypeDetails("ceo", null).get("user");
			Assert.fail();

		} catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
	}

	/**
	 * checking :get from DB the Register Customer input : { "RegisteredCustomer" ,
	 * "1" } expected: registeredCustomer
	 */
	@Test
	void getUserTypeDetails_RegisterCustomer_Exist_User() {
		RegisteredCustomer result = (RegisteredCustomer) DataBaseUserController
				.getUserTypeDetails("RegisteredCustomer", "1").get("user");
		RegisteredCustomer expected = registeredCustomer;
		Assert.assertEquals(expected, result);
	}

	/**
	 * checking :get from DB the Subscriber input : { "Subscriber" , "1" } expected:
	 * subscriber
	 */
	@Test
	void getUserTypeDetails_Subscriber_Exist_User() {
		Subscriber result = (Subscriber) DataBaseUserController.getUserTypeDetails("Subscriber", "1").get("user");
		Subscriber expected = subscriber;
		Assert.assertEquals(expected, result);
	}

	/**
	 * checking :get from DB the Subscriber input : { "Subscriber" , "1" } expected:
	 * subscriber
	 */
	@Test
	void getUserTypeDetails_NoRole_Exist_User() {
		User result = (User) DataBaseUserController.getUserTypeDetails("NoRole", "1").get("user");
		User expected = no_role;
		Assert.assertEquals(expected, result);
	}

	/*-----------------getUser------------------- */
	/**
	 * checking :get from DB the Worker input : { "CEO" , "1" } expected: user_ceo1
	 */
	@Test
	void getUser_Exist_User() {
		data.put("username", "CEO");
		data.put("password", "1");
		data.put("isForRegistration", false);
		User result = (User) DataBaseUserController.getUser(data).getData().get("user");
		User expected = user_ceo1;
		Assert.assertEquals(expected, result);
	}
	/**
	 * checking : get from DB the details on wrong username password 
	 *  input : { "CEO" , 2 } 
	 * expected: user_not_found 
	 */
	@Test
	void getUser_Not_Exist_User() {
		data.put("username", "CEO");
		data.put("password", "2");
		data.put("isForRegistration", false);
		Instruction result = (Instruction) DataBaseUserController.getUser(data).getInstruction();
		Instruction expected = user_not_found;
		Assert.assertEquals(expected, result);

	}
	
	
	/**
	 * checking : get from User of Worker is Registerd Customer
	 * input : HashMap :  {"WorkerRegistered" , "1" , "false" }
	 *  expected: workeRegisteredCustomer
	 */

	@Test
	void getUser_Worker_RegisterdCustomer_User() {
		data.put("username", "WorkerRegistered");
		data.put("password", "1");
		data.put("isForRegistration", false);
		RegisteredCustomer result =	 (RegisteredCustomer) DataBaseUserController.getUser(data).getData().get("user");
		RegisteredCustomer expected = workeRegisteredCustomer;
		Assert.assertEquals(expected, result);
		}
	
	/**
	 * checking : get from User of Worker is Subscriber
	 * input : HashMap :  {"WorkerSubscriber" , "1" , "false" }
	 *  expected: workerSubscriber
	 */
	@Test
	void getUser_Worker_Subscriber_User() {
		data.put("username", "WorkerSubscriber");
		data.put("password", "1");
		data.put("isForRegistration", false);
		Subscriber result =	 (Subscriber) DataBaseUserController.getUser(data).getData().get("user");
		Subscriber expected = workerSubscriber;
		Assert.assertEquals(expected, result);
	}
	
	/*-----------------setUserLoggedIn------------------- */

	/**
	 * checking :For User : Me that is logged out ->  set User to logged in true.
	 * input : For setUserLoggedIn getUser HashMap { user_logged_out ,  true }   ,  For getUser HashMap :  {"Me" , "1" , "false" }
	 *  expected: user_logged_in.
	 */
	@Test
	void setUserLoggedIn_True() {
		data.put("user", user_logged_out);
		data.put("loggedIn", true);
		DataBaseUserController.setUserLoggedIn(data);
		data.put("username", "Me");
		data.put("password", "1");
		data.put("isForRegistration", false);
		User result = (User) DataBaseUserController.getUser(data).getData().get("user");
		User expected = user_logged_in;
		Assert.assertEquals(expected, result);
		
	}
	/**
	 * checking : set User to logged in true.
	 * input : For setUserLoggedIn getUser HashMap { user_logged_in ,  false}   ,  For getUser HashMap :  {"Me" , "1" , "false" }
	 *  expected: user_logged_out.
	 */
	@Test
	void setUserLoggedIn_False() {
		data.put("user", user_logged_in);
		data.put("loggedIn", false);
		DataBaseUserController.setUserLoggedIn(data);
		data.put("username", "Me");
		data.put("password", "1");
		data.put("isForRegistration", false);
		User result = (User) DataBaseUserController.getUser(data).getData().get("user");
		User expected = user_logged_out;
		Assert.assertEquals(expected, result);
		
	}
	

	} 
	
	

