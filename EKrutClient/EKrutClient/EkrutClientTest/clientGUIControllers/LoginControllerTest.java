package clientGUIControllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Enum.Area;
import Enum.Role;
import Enum.UserStatus;
import clientGUIControllers.ILoginManager;
import clientGUIControllers.LoginController;
import entities.RegisteredCustomer;
import entities.ServerMessage;
import entities.Subscriber;
import entities.User;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
//import junit.framework.Assert;

class LoginControllerTest {
	
	
	boolean actualLoggedIn;
	boolean expectedLoggedIn;
	String actualPathToScreen;
	String expectedScreen;
	UserController userController;
	LoginController loginController;
	WorkerEntityController workerEntityController;
	ILoginManager stubLoginManager;
	User testUser;
	User user_test;
	User user_ceo1;
	User user_ceo1_loggedIn;
	User user_ceo1_accessDenied;
	User user_ceo1_notApproved;
	Subscriber subscriber;
	User user_registeredCustomer_not_approved;
	Role secondRole;
	String noRole_screen;
	String username;
	String password;
	String actualError;
	String expectedError;
	String subscriberSelected;
	
	
	public class StubLoginManager implements ILoginManager{

		@Override
		public void setUserLoggedIn(ServerMessage msg) {
			actualLoggedIn = (Boolean)msg.getData().get("loggedIn");
		}

		@Override
		public void userLoginDetails() {
			userController.setUser(testUser);
			workerEntityController.setSecondRole(secondRole);
		}

		@Override
		public void changeScreen(ActionEvent event, String pathToScreen) {
			actualPathToScreen = pathToScreen;
		}

		@Override
		public String getUsername() {
			return username;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public void showError(String error) {
			actualError = error;
			
		}

		@Override
		public void userLoginDetailsEKT(ServerMessage msg) {
			userController.setUser(testUser);
			workerEntityController.setSecondRole(secondRole);
		}

		@Override
		public void setSubscriberSelected(String value) {
		}

		@Override
		public String getSubscriberSelected() {
			return subscriberSelected;
		}
		
	}
	
	@BeforeEach
	void setUp(){
		userController = new UserController();
		userController.setUser(null);
		workerEntityController = new WorkerEntityController();
		stubLoginManager = new StubLoginManager();
		loginController = new LoginController();
		loginController.setServerLoginManager(stubLoginManager);
		actualLoggedIn = false;
		actualPathToScreen = null;
		actualError = null; 
		
		
		noRole_screen = "/clientGUIScreens/NoRoleScreen.fxml";
		user_ceo1 = new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.CEO, false, "CEO",
				UserStatus.Active, Area.North);
		user_ceo1_loggedIn = new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.CEO, true, "CEO",
				UserStatus.Active, Area.North);
		user_ceo1_accessDenied = new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.CEO, false, "CEO",
				UserStatus.Access_Denied, Area.North);
		user_ceo1_notApproved =new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.CEO, false, "CEO",
				UserStatus.Not_Approved, Area.North);
		user_test = new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", null, false, "CEO",
				UserStatus.Active, Area.North);
		subscriber = new Subscriber("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.Subscriber, false, "CEO",
				UserStatus.Active, Area.North, "1234123412341234", "3", "25", "333", 4, 34.5f ,true);
		user_registeredCustomer_not_approved=new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.RegisteredCustomer, false, "CEO",
				UserStatus.Not_Approved, Area.North);
	}
	
	
	/*---------------------------------------- Login Test-------------------------------------------*/
	
	
	/**
	 * checking : login with user success with every role
	 * input : "user", "1", user_test; (every role)
	 * expected: logged in true, change screen to correct screen, no errors
	 * @throws Exception 
	 */
	@Test
	void loginUserSuccessTest() throws Exception {
		testUser = user_test;
		
		for(Role role: Role.values()) {
			
			// set up for each screen test
			actualLoggedIn = false;
			testUser.setLoggedIn(false);
			testUser.setRole(role);
			username = "user";
			password = "1";
			// end setup
			
			loginController.loginPressed(new ActionEvent());
			expectedLoggedIn = true;
			expectedScreen = "/clientGUIScreens/" + role+ "Screen.fxml";
			expectedError = null;
			Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
			Assert.assertEquals(expectedScreen, actualPathToScreen);
			Assert.assertEquals(expectedError, actualError);
		}
	}
	
	/**
	 * checking : login with CEO already logged in
	 * input : "CEO", "1", user_ceo1_loggedIn
	 * expected: already logged in error , (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginUserAlreadyLoggedInTest() throws Exception {
		testUser = user_ceo1_loggedIn;
		username = "CEO";
		password = "1";
		loginController.loginPressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "The user is already logged in, please disconnect from the other device!";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	/**
	 * checking : login with empty fields
	 * input : "", "", user_ceo1
	 * expected: empty fields error , (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginUserEmptyFieldsTest() throws Exception {
		testUser = user_ceo1;
		username = "";
		password = "";
		loginController.loginPressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "Username OR password are empty, please try again!";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}

	/**
	 * checking : login with empty username
	 * input : "", "1", user_ceo1
	 * expected: empty fields error , (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginUserEmptyUsernameTest() throws Exception {
		testUser = user_ceo1;
		username = "";
		password = "1";
		loginController.loginPressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "Username OR password are empty, please try again!";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	/**
	 * checking : login with empty password
	 * input : "CEO", "", user_ceo1
	 * expected: empty fields error , (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginUserEmptyPasswordTest() throws Exception {
		testUser = user_ceo1;
		username = "CEO";
		password = "";
		loginController.loginPressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "Username OR password are empty, please try again!";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	
	/**
	 * checking : login with access denied
	 * input : "CEO", "1", user_ceo1_accessDenied
	 * expected: user access error , (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginUserAccessDeniedTest() throws Exception {
		testUser = user_ceo1_accessDenied;
		username = "CEO";
		password = "1";
		loginController.loginPressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "User access denied!";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	/**
	 * checking : login - user not found 
	 * input : "CEO", "1", null
	 * expected:  error wrong username or password, (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginUserNotFoundTest() throws Exception {
		testUser = null;
		username = "CEO";
		password = "1";
		loginController.loginPressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "Wrong username OR password, please try again!";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	/**
	 * checking : login - user not approved and registeredCustomer.
	 * input : "RegisteredCustomer1", "1", 
	 * expected: logged in true, change screen to no role screen, no errors
	 * @throws Exception 
	 */
	@Test
	void loginUserNotApprovedAndRegisteredCustomerTest() throws Exception {
		testUser = user_registeredCustomer_not_approved;
		username = "RegisteredCustomer1";
		password = "1";
		loginController.loginPressed(new ActionEvent());
		expectedLoggedIn = true;
		expectedScreen = noRole_screen;
		expectedError = null;
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	/**
	 * checking : login - user as worker not approved .
	 * input : "CEO", "1", 
	 * expected: logged in true, change screen to no role screen, no errors
	 * @throws Exception 
	 */
	@Test
	void loginUserAsWorkerNotApprovedTest() throws Exception {
		testUser = user_ceo1_notApproved;
		username = "CEO";
		password = "1";
		loginController.loginPressed(new ActionEvent());
		expectedLoggedIn = true;
		Role role=testUser.getRole();
		expectedScreen = "/clientGUIScreens/" + role+ "Screen.fxml";
		expectedError = null;
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	
	/*----------------------------------- Login via EKT Test---------------------------------------*/
	
	/**
	 * checking : login via EKT user success with every role that can use EKT
	 * input :"user", "1", testUser:subscriber(
	 * expected: logged in true, change screen to correct screen, no errors
	 * @throws Exception 
	 */
	@Test
	void loginEKTUserSuccessTest() throws Exception {
		testUser = subscriber;
		subscriberSelected = "user";
		
		for(Role role: Role.values()) {
			if(role.equals(Role.RegisteredCustomer) || role.equals(Role.NoRole))
				continue;
			
			// set up for each screen test
			actualLoggedIn = false;
			testUser.setLoggedIn(false);
			testUser.setRole(role);
			username = subscriberSelected;
			// end setup
			
			loginController.loginEKT_Pressed(new ActionEvent());
			expectedLoggedIn = true;
			expectedScreen = "/clientGUIScreens/" + role+ "Screen.fxml";
			expectedError = null;
			Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
			Assert.assertEquals(expectedScreen, actualPathToScreen);
			Assert.assertEquals(expectedError, actualError);
		}
	}
	
	/**
	 * checking : login EKT with CEO already logged in
	 * input : "CEO", "1", user_ceo1_loggedIn
	 * expected: already logged in error , (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginEKTUserAlreadyLoggedInTest() throws Exception {
		subscriberSelected = "CEO";
		testUser = user_ceo1_loggedIn;
		username = subscriberSelected;
		loginController.loginEKT_Pressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "The user is already logged in, please disconnect from the other device!";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	
	/**
	 * checking : login EKT with access denied
	 * input : "CEO", "1", user_ceo1_accessDenied
	 * expected: user access error , (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginEKTUserAccessDeniedTest() throws Exception {
		subscriberSelected = "CEO";
		testUser = user_ceo1_accessDenied;
		username = subscriberSelected;
		loginController.loginEKT_Pressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "User access denied!";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
	
	/**
	 * checking : login EKT - subscriber not selected from list
	 * input : null, "1", subscriber
	 * expected: subscriber not selected error , (without setting the user to be logged in or change the screen)
	 * @throws Exception 
	 */
	@Test
	void loginEKTSubscriberNotSelectedTest() throws Exception {
		subscriberSelected = null;
		testUser = subscriber;
		username = subscriberSelected ;
		loginController.loginEKT_Pressed(new ActionEvent());
		expectedLoggedIn = false;
		expectedScreen = null;
		expectedError = "You MUST select subscriber before pressing login";
		Assert.assertEquals(expectedLoggedIn, actualLoggedIn);
		Assert.assertEquals(expectedScreen, actualPathToScreen);
		Assert.assertEquals(expectedError, actualError);
	}
}