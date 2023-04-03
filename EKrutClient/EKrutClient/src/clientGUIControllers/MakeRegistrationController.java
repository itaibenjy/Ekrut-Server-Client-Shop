package clientGUIControllers;


import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Instruction;
import Enum.Role;
import Enum.UserStatus;
import common.CommonActionsController;
import entities.ServerMessage;
import entities.User;
import entityControllers.WorkerEntityController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 *
 *This class is used to handle the MakeRegistrationForm's GUI screen.
 *This class is responsible for checking the correctness of the input (username and password) and,
 * if necessary, displays an error message. 
 * In addition, this class communicates with the server and retrieves data from the database about the user who wants to register.
 * It provides methods that are called when the user interacts with the GUI, such as when the user presses the "Get User Details" button. 
 * The class checks the input fields, retrieves the user's details from the database, and determines the user's role and registration type. 
 * Based on these values, different actions are taken, such as displaying an error message if the user is already a registered customer. 
 *The class also contains methods that handle the display of images and the visibility of labels in the GUI.
 *
 */
public class MakeRegistrationController {


	/**
	 *FXML Attributes
	 */
	@FXML
	private Button backBtn;

	@FXML
	private Button getUserDetails;

	@FXML
	private Button helpBtn;

	@FXML
	private Label helpLabel;

	@FXML
	private Button logOutBtn;

	@FXML
	private ComboBox<Role> registerationTypeCombo;


	@FXML
	private TextField userPassword;

	@FXML
	private TextField userUsername;

	@FXML
	private Button xBtn;

	@FXML
	private Label errorLabel;


	@FXML
	private ImageView requesticon;

	@FXML
	private ImageView logoImage;

	@FXML
	private ImageView regisrationReqImage;



	/**
	 * Entities,variables And Controllers 
	 */
	private CommonActionsController commonActionsController = new CommonActionsController();
	private ObservableList<Role> registerationType = FXCollections.observableArrayList(Role.RegisteredCustomer,Role.Subscriber);
	private static User  userForRegisteretion=null;
	private static Role secondRoleForRegistration=null;
	private static boolean  isSubscriberRegisteration=false;



	/**
	 *The initialize() method is called when the class is first loaded.
	 * It sets the items for the registerationTypeCombo, 
	 * sets the helpLabel and errorLabel to invisible, 
	 * and sets the images for the requesticon, logoImage, and regisrationReqImage.
	 */
	@FXML
	public void initialize() {
		this.registerationTypeCombo.setItems(registerationType);
		this.helpLabel.setVisible(false);
		this.errorLabel.setVisible(false);
		setImages();
	}



	/**
	 *The getUserDetailsPressed(ActionEvent event) method is called when the "Get User Details" button is pressed.
	 *This method checks if user is exist in DB and checks the possibility of registration for that user and display error messages accordingly.
	 *It first checks the input fields with the checkInputBeforeGetUserDetails() method, 
	 *then attempts to get the user's details from the database using a ServerMessage object.
	 *If the user is not found in the database, the errorLabel is set to "User not found, please try again".
	 *If the user is found, the realRole and tempRole of the user are determined, 
	 *and the user's registration type is checked against the value selected in the registerationTypeCombo by summing RegisterationTypeOptions the method.
	 *@param event The ActionEvent that triggers the method
	 */
	@FXML
	void getUserDetailsPressed(ActionEvent event) {

		if(checkInputBeforeGetUserDetails()) {

			/**
			 *Get the user's details from DB
			 *
			 */
			Hashtable<String,Object> data = new Hashtable<>();
			data.put("username",userUsername.getText().trim());
			data.put("password",userPassword.getText().trim());
			data.put("isForRegistration", true);
			ServerMessage msg = new ServerMessage(Instruction.Get_user_details,data);
			EkrutClientUI.chat.accept(msg);
			while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update

			/**
			 * If user for registration does not found in BD.
			 */
			if(userForRegisteretion==null) {
				this.errorLabel.setText("User not found,please try again");
				this.errorLabel.setVisible(true);

			}else{

				Role realRole=userForRegisteretion.getRole();
				Role tempRole=Role.NoRole;

				/**
				 * If user for registration is not worker.
				 */
				if((realRole.equals(Role.RegisteredCustomer)||realRole.equals(Role.Subscriber)||realRole.equals(Role.NoRole))) {
					tempRole=userForRegisteretion.getRole();

				}

				/**
				 * If user for registration is worker check is second role and set him on temp role.
				 */
				else {
					if(secondRoleForRegistration==null)
						tempRole=Role.NoRole;
					else if(secondRoleForRegistration.equals(Role.RegisteredCustomer)||secondRoleForRegistration.equals(Role.Subscriber)) {
						if(secondRoleForRegistration.equals(Role.Subscriber)) 
							tempRole=Role.Subscriber;
						else
							tempRole=Role.RegisteredCustomer;	
					}
				}
				
				RegisterationTypeOptions(tempRole,event);
			}
		}
	}

	
	/**
	*This method is used to handle the registration type options selected by the user.
	*It checks the current role of the user and the registration type selected by the user and performs the appropriate actions.
	*If the user is already registered as a customer or a subscriber, it displays an error message.
	*If the user is not approved by the area manager, it displays an error message.
	*If the user meets the requirements, it navigates the user to the registration form screen.
	*@param tempRole The current role of the user.
	*@param event The ActionEvent that triggers this method.
	*/
	private void RegisterationTypeOptions(Role tempRole,ActionEvent event) {
		switch (this.registerationTypeCombo.getValue()) {
		case RegisteredCustomer: 

			isSubscriberRegisteration=false;
			if(tempRole.equals(Role.RegisteredCustomer) &&(userForRegisteretion.getUserStatus().equals(UserStatus.Not_Approved))) {
				this.errorLabel.setVisible(true);
				this.errorLabel.setText("User is already waiting for aprroval of area manager to be registered customber.");
				break;
			}
			else if(tempRole.equals(Role.RegisteredCustomer)|| tempRole.equals(Role.Subscriber)) {
				this.errorLabel.setVisible(true);
				this.errorLabel.setText("User is already registered customer.");
				break;
			}					
			else
			{
				commonActionsController.backOrNextPressed(event, "/clientGUIScreens/RegistrationFormScreen.fxml");
				break;
			}


		case Subscriber:

			isSubscriberRegisteration=true;
			if(tempRole.equals(Role.Subscriber)) {
				this.errorLabel.setVisible(true);
				this.errorLabel.setText("User is already subscriber.");
				break;
			}
			else if(tempRole.equals(Role.RegisteredCustomer) &&(userForRegisteretion.getUserStatus().equals(UserStatus.Not_Approved))) {
				this.errorLabel.setVisible(true);
				this.errorLabel.setText("User needs the aprroval of area manager to be registered customber before he cans to register as a subscriber.");
				break;
			}else if(!tempRole.equals(Role.RegisteredCustomer)) {
				this.errorLabel.setVisible(true);
				this.errorLabel.setText("User have to be registered customber before he cans to register as a subscriber.");
				break;
			}
			else {
				commonActionsController.backOrNextPressed(event,  "/clientGUIScreens/RegistrationFormScreen.fxml");
				break;
			}


		default:
			break;
		}
	}

	/**
	 *The checkInputBeforeGetUserDetails() method is used to check the correctness of the input values before getting the user's details from the database.
	 *It checks if the registerationTypeCombo has a selected value, and if the userUsername and userPassword text fields are empty.
	 *If the registerationTypeCombo has no selected value, an error message is displayed stating "You MUST select registration type!".
	 *If the userUsername or userPassword text fields are empty, an error message is displayed stating "You Must enter user's username and user's password!".
	 *@return A boolean value indicating if the input values are correct (true) or not (false)
	 */
	private boolean checkInputBeforeGetUserDetails(){
		if(this.registerationTypeCombo.getValue()==null) {
			this.errorLabel.setVisible(true);
			this.errorLabel.setText("You MUST select registration type!");
			return false;
		}else if(this.userUsername.getText().isEmpty() || this.userPassword.getText().isEmpty()) {
			this.errorLabel.setVisible(true);
			this.errorLabel.setText("You Must enter user's usename ans user's password!");
			return false;
		}else
			return true;
	}


	/**
	 *The helpPressed(ActionEvent event) method is called when the "Help" button is pressed.
	 *It sets the helpLabel to visible and displays text explaining how to use the registration form.
	 *@param event The ActionEvent that triggers the method
	 */
	@FXML
	void helpPressed(ActionEvent event) {
		this.helpLabel.setVisible(true);
		this.helpLabel.setText("First you need to enter a username and password for the user you want to make registeration,\n"
				+ "\nThe Registeration types you can choose are:"
				+ "\n1)Registered costumer"
				+ "\n2)Subscriber"
				+ "\nFor each of the options, the appropriate registration form will open,"
				+ "\nIf the registration is for a registered customer, "
				+ "\n the registration request will sent to area manager for approval!");
	}


	/**
	 *The backPressed(ActionEvent event) method is called when the "Back" button is pressed.
	 *It calls the backOrNextPressed method of the commonActionsController class and redirects to the ServiceRepresentiveEmployeeScreen.fxml.
	 *@param event The ActionEvent that triggers the method
	 */
	@FXML
	void backPressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/ServiceRepresentiveEmployeeScreen.fxml");

	}

	/**
	 *The logOutPressed(ActionEvent event) method is called when the "Log Out" button is pressed.
	 *It calls the logOutPressed method of the commonActionsController class and this method logout the user.
	 *@param event The ActionEvent that triggers the method
	 */
	@FXML
	void logOutPressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}

	/**
	 *The xPressed(ActionEvent event) method is called when the "X" button is pressed.
	 *It calls the xPressed method of the commonActionsController class.
	 *@param event The ActionEvent that triggers the method
	 */
	@FXML
	void xPressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}


	/**
	 *The getUserForRegisteretion() method is used to get the user for registration.
	 *@return The user object that needs to be registered.
	 */
	public static User getUserForRegisteretion() {
		return userForRegisteretion;
	}


	/**
	 *The setUserForRegisteretion(User userForRegisteretion) method is used to set the user for registration.
	 *@param userForRegisteretion The user object that needs to be registered
	 */
	public static void setUserForRegisteretion(User userForRegisteretion) {
		MakeRegistrationController.userForRegisteretion = userForRegisteretion;
	}


	/**
	 *The getIsSubscriberRegisteration() method is used to get the registration type (subscriber or registered customer).
	 *@return A boolean value indicating if the registration type is subscriber (true) or registered customer (false)
	 */
	public static boolean getIsSubscriberRegisteration() {
		return isSubscriberRegisteration;
	}

	/**
	 *The setSubscriberRegisteration(boolean isSubscriberRegisteration) method is used to set the registration type (subscriber or registered customer).
	 *@param isSubscriberRegisteration A boolean value indicating if the registration type is subscriber (true) or registered customer (false)
	 */
	public static void setSubscriberRegisteration(boolean isSubscriberRegisteration) {
		MakeRegistrationController.isSubscriberRegisteration = isSubscriberRegisteration;
	}

	/**
	 *The getSecondRoleForRegistration() method is used to get the secondary role for worker's registration.
	 *@return The secondary role for registration.
	 */
	public static Role getSecondRoleForRegistration() {
		return secondRoleForRegistration;
	}


	/**
	 *The setSecondRoleForRegistration(Role secondRoleForRegistration) method is used to set the secondary role for worker's registration.
	 *@param secondRoleForRegistration The secondary role for registration.
	 */
	public static void setSecondRoleForRegistration(Role secondRoleForRegistration) {
		MakeRegistrationController.secondRoleForRegistration = secondRoleForRegistration;
	}

	
	/**
	 * Set images
	 */
	private void setImages() {
		requesticon.setImage(new Image("/registrationIcon.png"));
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		regisrationReqImage.setImage(new Image("/serviceRepresentiveEmployeePics/search.gif"));
	}




}