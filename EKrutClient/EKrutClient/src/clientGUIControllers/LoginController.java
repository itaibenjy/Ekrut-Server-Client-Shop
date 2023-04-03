package clientGUIControllers;

import java.util.ArrayList;
import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Instruction;
import Enum.Role;
import Enum.UserStatus;
import common.CommonActionsController;
import entities.ServerMessage;
import entityControllers.UserController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/**
 * 
 * Login controller for a login GUI.
 * This class is used to identify the user and redirect him to the appropriate next page.
 * This class defines methods and variables for handling user login interactions,
 * such as taking input from username and password text fields,
 * handling button clicks, and displaying error messages.
 * The class uses other classes such as UserController to set the received user details from DB, 
 * CommonActionsController to redirect the user to the appropriate next page. 
 * and WorkerEntityController to handle with worker with second role(RegisteredCustomer/subscriber).
 * 
 */
public class LoginController {


	/**
	 * FXML Attributes
	 */

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private ImageView logoImage;

	@FXML
	private ImageView login;

	@FXML
	private ComboBox<String> subscriberComboBox;

	@FXML
	private Button loginButton;

	@FXML
	private Button xBtn;

	@FXML
	private Button loginEKT;

	@FXML
	private Button loginViaUsernameAndPasswordBtn;

	@FXML
	private Button loginviaEktBtn;

	@FXML
	private SplitPane splitpane;

	@FXML
	private Label errorLabel;

	@FXML
	private Label usernameLabel;

	@FXML
	private Label passwordLabel;


	/**
	 *  Entities And Controller
	 */
	private UserController userController = new UserController();
	private CommonActionsController commonActionsController = new CommonActionsController();
	private static ArrayList<String> subscriber_userNamesArr = new ArrayList<String>();
	private final ObservableList<String> subList = FXCollections.observableArrayList();
	private ILoginManager loginManager = new LoginManager();



	/**
	 * The initialize method is used to set visibility of the buttons ,textfields, combobox, images and labels.
	 */
	@FXML
	public void initialize() {
		this.errorLabel.setVisible(false);
		setImages();
		showView(false,true);
		insertDataOfSubscriber();

	}
	
	
	/**
	 *  @param event loginViaUsernameAndPassword Button Pressed
	 * If user click on login via username and password change the visibility of 
	 * buttons ,TextFields, ComboBox, images and labels.
	 * */

	@FXML
	void loginViaUsernameAndPasswordPressed(ActionEvent event) {
		showView(false,true);
		login.setImage(new Image("/login-.gif"));
	}



	/**
	 *  @param event loginviaEktBtn Button Pressed
	 * If user click on login via EKT app change the visibility of 
	 * buttons ,TextFields, ComboBox, images and labels.
	 * */
	@FXML
	void loginviaEktBtn(ActionEvent event) {
		showView(true,false);
		login.setImage(new Image("/face-scan.gif"));
	}


	/**
	 * This method is called when the login button is pressed
	 * @param event The action event that triggered the login button being pressed
	 * @throws Exception 
	 */
	@FXML
	public void loginPressed(ActionEvent event) throws Exception {
		this.login(event);
	}

	/**
	 * This method is used to handle the login process
	 * @param event The action event that triggered the login process
	 */
	public void login(ActionEvent event) {
		if (loginManager.getUsername().isEmpty() || loginManager.getPassword().isEmpty()) {
			this.emptyFields();
		} 
		else {
			this.loginManager.userLoginDetails();
			if (this.userController.getUser() == null) {
				this.userNotFound();
			} else if (this.userController.getUser().getLoggedIn()) {
				this.userLoggedIn();

			} else if (this.userController.getUser().getUserStatus().equals(UserStatus.Access_Denied)) {
				userAccessDenied();
			} else {
				loggedIn(event);

			}
		}
	}


	/**
	 * This class handles the actions that occur when buttons are pressed in the subscriber login via Ekt app.
	 * This method is called when the subscriber combo box is pressed.
	 * @param event The action event that triggered the combo box being pressed.
	 */
	@FXML
	void subscriberComboBox_Pressed(ActionEvent event) {
		loginManager.setSubscriberSelected(subscriberComboBox.getValue());
		errorLabel.setVisible(false);
	}


	/**
	 * This method is called when the "X" button is pressed-->exit from login screen.
	 * @param event The action event that triggered the "X" button being pressed
	 */
	@FXML
	void xPressed(ActionEvent event) {
		System.exit(0);
	}


	/**
	 * This method sets the user to logged in.
	 * @param event The action event that triggered the login process
	 */
	public void loggedIn(ActionEvent event) {

		userController.getUser().setLoggedIn(true);
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("user", this.userController.getUser());
		data.put("loggedIn", true);
		ServerMessage msg = new ServerMessage(Instruction.Set_User_loggedIn, data);
		loginManager.setUserLoggedIn(msg);
		this.userFound(event);
	}
	

	/**
	 *This method handles the login process for a subscriber using the EKT app.
	 *Get the selected subscriber details fromDB.
	 * @param event The action event that triggered the EKT login button being pressed.
	 */
	@FXML
	void loginEKT_Pressed(ActionEvent event) {
		if (loginManager.getSubscriberSelected() == null) {
			loginManager.showError("You MUST select subscriber before pressing login");
		}
		else {

			Hashtable<String, Object> data = new Hashtable<>();
			data.put("username", loginManager.getSubscriberSelected());
			data.put("isForRegistration", false);
			ServerMessage msg = new ServerMessage(Instruction.Get_Subscriber_EKT_Login, data);
			loginManager.userLoginDetailsEKT(msg);
			if (this.userController.getUser().getLoggedIn()) {
				this.userLoggedIn();

			} else if (this.userController.getUser().getUserStatus().equals(UserStatus.Access_Denied)) {
				userAccessDenied();
			} else {
				loggedIn(event);
			}
		}
	}

	/**
	 * Set images
	 */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		login.setImage(new Image("/login-.gif"));
	}

	/**
	 * 
	 * Get all the subscribers details from DB and ass  them to the ObservableList subList.
	 * Set ObservableList subList in the subscriberComboBox.
	 *  
	 * */
	private void insertDataOfSubscriber() {
		ServerMessage msg = new ServerMessage(Instruction.Get_Subscriber_Details, null);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		; // wait for server update
		for (String subUserName : subscriber_userNamesArr) {
			subList.add(subUserName);
		}
		subscriberComboBox.setItems(subList);
	}


	/**
	 * If user status is access denied display to the user error message. 
	 */
	public void userAccessDenied() {
		loginManager.showError("User access denied!");
	}


	/**
	 * If user not found display to the user error message.
	 */
	public void userNotFound() {

		loginManager.showError("Wrong username OR password, please try again!");
	}


	/**
	 * If user did not enter username or password display to the user error message..
	 */
	public void emptyFields() {
		loginManager.showError("Username OR password are empty, please try again!");
	}


	/**
	 * If user already logged in display to the user error message.
	 */
	private void userLoggedIn() {
		loginManager.showError("The user is already logged in, please disconnect from the other device!");
	}

	/**
	 * This class handles the actions that occur when a user is found.
	 * @param event The action event that triggered the user being found
	 * This method check if the user's status is Not_Approved,
	 * if the user is not approved, the user will be redirected to the home page of a user with no role
	 * otherwise the user will be redirected to the home page according to his role. 
	 */
	public void userFound(ActionEvent event) {
		if(this.userController.getUser().getUserStatus().equals(UserStatus.Not_Approved)&&this.userController.getUser().getRole().equals(Role.RegisteredCustomer)) {
			this.loginManager.changeScreen(event, "/clientGUIScreens/NoRoleScreen.fxml");
		}
		else
			this.loginManager.changeScreen(event, "/clientGUIScreens/" + this.userController.getUser().getRole() + "Screen.fxml");
	}

	/**
	 * Returns the ArrayList of subscriber usernames
	 * @return ArrayList of subscriber usernames
	 */
	public static ArrayList<String> getSubscriber_userNamesArr() {
		return subscriber_userNamesArr;
	}


	/**
	 * Sets the ArrayList of subscriber usernames
	 * @param subscriber_userNamesArr ArrayList of subscriber usernames
	 */
	public static void setSubscriber_userNamesArr(ArrayList<String> subscriber_userNamesArr) {
		LoginController.subscriber_userNamesArr = subscriber_userNamesArr;
	}


	/**
	 *Set visble/not visble buttons,labels,text fields according to login type.
	 *Sets the visibility of certain elements based on the values of the input parameters.
	 *@param partA boolean value that sets the visibility of loginViaUsernameAndPasswordBtn, loginEKT, and subscriberComboBox.
	 *@param partB boolean value that sets the visibility of loginviaEktBtn, loginButton, password, username, usernameLabel, and passwordLabel.
	 */
	private void showView(boolean partA, boolean partB) {
		loginViaUsernameAndPasswordBtn.setVisible(partA);
		loginEKT.setVisible(partA);
		subscriberComboBox.setVisible(partA);
		loginviaEktBtn.setVisible(partB);
		loginButton.setVisible(partB);
		password.setVisible(partB);
		username.setVisible(partB);
		usernameLabel.setVisible(partB);
		passwordLabel.setVisible(partB);

	}
	
/**---------------------------------For Testing----------------------------------*/	
	
	
	
	/** 
	 * Class for testing with property injection
	 */
	public class LoginManager implements ILoginManager{

		String subscriberSelected = null;
		
		
		/**
		*This method accepts a ServerMessage object and sets the user as logged in.
		*It also waits for a response from the server after sending the message.
		*@param msg - The ServerMessage object that contains login information for the user.
		*/
		public void setUserLoggedIn(ServerMessage msg) {
			EkrutClientUI.chat.accept(msg);
			while(EkrutClientUI.chat.isServerMsgRecieved()) {};
		}
		
		
		/**
		*This method returns the text entered in the username field.
		*@return - the text entered in the username field.
		*/
		public String getUsername() {
			return username.getText();
		}
		
		
		/**
		*This method returns the text entered in the password field.
		*@return - the text entered in the password field.
		*/
		public String getPassword() {
			return password.getText();
		}
		
		/***
		 * 
		 *Get user details from DB according the username and the password.
		 * 
		 */
		public void userLoginDetails(){
			Hashtable<String,Object> data = new Hashtable<>();
			data.put("username",loginManager.getUsername().trim());
			data.put("password",loginManager.getPassword().trim());
			data.put("isForRegistration", false);
			ServerMessage msg = new ServerMessage(Instruction.Get_user_details,data);
			EkrutClientUI.chat.accept(msg);
			while(!EkrutClientUI.chat.isServerMsgRecieved()) {};
		}
		
		
		/**
		*This method accepts a ServerMessage object and uses it to fetch user details.
		*It also waits for a response from the server after sending the message.
		*@param msg - The ServerMessage object that contains user login information.
		*/
		public void userLoginDetailsEKT(ServerMessage msg) {
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
		}
		
		/**
		*This method changes the current screen to the one specified by pathToScreen.
		*@param event - The ActionEvent that triggers the screen change.
		*@param pathToScreen - The path to the desired screen.
		*/
		public void changeScreen(ActionEvent event, String pathToScreen) {
			commonActionsController.backOrNextPressed(event, pathToScreen);
		}
		
		
		/**
		*This method displays the given error message on the error label.
		*@param error - The error message to be displayed.
		*/
		public void showError(String error) {
			errorLabel.setVisible(true);
			errorLabel.setText(error);
		}

		/**
		*This method sets the selected subscriber to the given value.
		*@param value - The value to set the selected subscriber to.
		*/
		@Override
		public void setSubscriberSelected(String value) {
			subscriberSelected = value;
		}

		/**
		*This method returns the currently selected subscriber.
		*@return - The currently selected subscriber.
		*/
		@Override
		public String getSubscriberSelected() {
			return subscriberSelected;
		}
		
	}
	
	/**
	*This method sets property for property injection, set the login manager to the given ILoginManager object.
	*It is used for property injection.
	*@param loginManager - the ILoginManager object to be set as the login manager.
	*/
	public void setServerLoginManager(ILoginManager loginManager) {
		this.loginManager = loginManager;
	}


}