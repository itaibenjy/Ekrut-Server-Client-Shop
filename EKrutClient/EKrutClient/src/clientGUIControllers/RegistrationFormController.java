package clientGUIControllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Client.EkrutClientUI;
import java.util.Hashtable;
import Enum.Area;
import Enum.Instruction;
import Enum.Role;
import Enum.UserStatus;
import common.CommonActionsController;
import entities.RegisteredCustomer;
import entities.ServerMessage;
import entities.Subscriber;
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
import javafx.stage.Stage;

/**
 * 
 *
 *This class is a JavaFX controller class for a registration form GUI
 * to create new registration as registeredCustomer or as a Subscriber for user and 
 * add the user all the permissions as registeredCustomer or as a Subscriber. 
 *It uses the JavaFX API to set up and initialize the form's various components, such as text fields, images, and labels,
 *and sets their properties and visibility according to the registration type.
 *The class also handle logic for determining whether the form is for registering a new subscriber or a new registered customer,
 *and sets form fields and visibility accordingly. 
 *Additionally, it handles communication with a server to retrieve and update data, such as user , registeredCustomer and subscriber details.  
 *this class sets error and help labels, as well as defaulting user-specific fields with user-specific data.  * 
 * 
 */
public class RegistrationFormController {


	/**
	 *FXML Attributes
	 */
	@FXML
	private ComboBox<Area> areaCombo;

	@FXML
	private Button backBtn;

	@FXML
	private TextField creditCardNumTextField;

	@FXML
	private TextField cvvTextField;

	@FXML
	private TextField emailTextField;

	@FXML
	private Label errorlabel;

	@FXML
	private TextField firstnameTextField;

	@FXML
	private Button helpBtn;

	@FXML
	private Label helpLabel;

	@FXML
	private TextField lastNameTextField;

	@FXML
	private Button logOutBtn;

	@FXML
	private ComboBox<String> monthCombo;

	@FXML
	private TextField passwordTextField;

	@FXML
	private Button sendrequestBtn;

	@FXML
	private ComboBox<UserStatus> userStatusCombo;

	@FXML
	private TextField usernameTextField;

	@FXML
	private TextField idTextField;

	@FXML
	private Button xBtn;

	@FXML
	private ComboBox<String> yearCombo;

	@FXML
	private TextField telephoneTextField;

	@FXML
	private Button makeRegistrationBtn;

	@FXML
	private TextField subscriberNumTextField;

	@FXML
	private Label subscriberNumberLabel;

	@FXML
	private Label titleLabel;

	@FXML
	private Label subscriberDetailsLabel;

	@FXML
	private Button viewBtn;

	@FXML
	private Button hideBtn;

	@FXML
	private Label usernamelbl;

	@FXML
	private Label firstnamelbl;

	@FXML
	private Label lastnamelbl;

	@FXML
	private Label idlbl;

	@FXML
	private Label emaillbl;

	@FXML
	private Label passwordlbl;

	@FXML
	private Label telephonelbl;

	@FXML
	private Label arealbl;

	@FXML
	private Label statuslbl;

	@FXML
	private Label userGeneralSetailsLabel;

	@FXML
	private ImageView logoImage;

	@FXML
	private ImageView creditcardImage;

	@FXML
	private ImageView subscriberImage;

	@FXML
	private ImageView usersDetailsImage;


	/**
	 * Entities And Controllers. 
	 */
	private ObservableList<Area> area = FXCollections.observableArrayList(Area.North,Area.South,Area.UAE);
	private ObservableList<String> month = FXCollections.observableArrayList("1","2","3","4","5","6","7","8","9","10","11","12");
	private ObservableList<String> year = FXCollections.observableArrayList();
	private CommonActionsController commonActionsController = new CommonActionsController();
	private User user=MakeRegistrationController.getUserForRegisteretion();
	private static RegisteredCustomer registeredCustomerDetails=null;
	private static int subscriberMax=0;
	private WorkerEntityController workerEntityController;
	private RegisteredCustomer registeredCustomerDtails; //This variable is for subscriber registration of registered customer.



	/**
	 * The initialize method is called when the fxml file is loaded.
	 * sets images,buttons,text fields according to the registration type..
	 * sets general user details, and hides the those extra details.
	 * sets the title label and subscriber details label based on whether the registration is for subscriber or registered customer.
	 * sets the credit card number, CVV, month and year based on the registered customer details.
	 * sets the max subscriber number based on the max subscriber number in the database if the registration for subscriber.
	 * This method organizes the registration form according to the type of registration.
	 */
	@FXML
	public void initialize() {
		setImages();

		/**
		 * 
		 * set visible/not visible general user's details buttons,images and labels.
		 * 
		 */
		this.hideBtn.setVisible(false);
		this.viewBtn.setVisible(true);
		extreDetailsIsVisible(false);
		this.userGeneralSetailsLabel.setVisible(true);
		this.usersDetailsImage.setVisible(true);
		this.creditcardImage.setVisible(true);
		this.subscriberImage.setVisible(false);



		/**
		 * 
		 * Subscriber registration.
		 * set visible/not visible general subscriber's details buttons,images and labels.
		 * 
		 */
		if(MakeRegistrationController.getIsSubscriberRegisteration()) {

			this.titleLabel.setText("Subsriber registration");
			this.titleLabel.setVisible(true);
			this.subscriberImage.setVisible(true);
			this.subscriberDetailsLabel.setVisible(true);
			this.subscriberNumberLabel.setVisible(true);
			this.subscriberNumTextField.setVisible(true);
			this.subscriberImage.setVisible(true);


			/**
			 * Set user status to Active.
			 * */
			this.userStatusCombo.setValue(UserStatus.Active);
			this.userStatusCombo.setDisable(true);

			/**
			 * Set user extra details of registeredCustomer.
			 */
			registeredCustomerDtails = (RegisteredCustomer) MakeRegistrationController.getUserForRegisteretion();
			this.creditCardNumTextField.setText(registeredCustomerDtails.getCreditCardNumber());
			this.cvvTextField.setText(registeredCustomerDtails.getCvv());
			this.monthCombo.setValue(registeredCustomerDtails.getExpireMonth());
			this.yearCombo.setValue(registeredCustomerDtails.getExpireYear());

			/**
			 * Get the last max subscriber number from DB and 
			 * set the subscriberNumTextField text field with the next subscriber number.
			 */
			ServerMessage msg = new ServerMessage(Instruction.Get_max_subscriber_number,null);
			EkrutClientUI.chat.accept(msg);
			while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
			this.subscriberNumTextField.setText(Integer.toString(subscriberMax+1));
			this.subscriberNumTextField.setDisable(true);

		}


		/**
		 * 
		 * RegisteredCustomer registration.
		 * set visible/not visible general registeredCustomer's details buttons,images and labels.
		 * 
		 */
		else {
			this.titleLabel.setText("Registered Customer registration");
			this.subscriberDetailsLabel.setVisible(false);
			this.subscriberNumberLabel.setVisible(false);
			this.subscriberNumTextField.setVisible(false);	
			/**
			 * Set user status to NotApproved.
			 */
			this.userStatusCombo.setValue(UserStatus.Not_Approved);
			this.userStatusCombo.setDisable(true);
		}

		/**
		 * set visible/not visible buttons,images,combobox and labels.
		 * 
		 */
		this.errorlabel.setVisible(false);
		this.helpLabel.setVisible(false);
		this.areaCombo.setItems(this.area);
		this.monthCombo.setItems(this.month);
		addYearsToYearList();
		this.yearCombo.setItems(this.year);



		/**
		 * 
		 * Set details on the registration form.
		 */
		this.usernameTextField.setText(user.getUserName());
		this.idTextField.setText(Integer.toString(user.getId()));
		this.usernameTextField.setDisable(true); //Can't edit user's username.
		this.passwordTextField.setText(user.getPassword());
		if(user.getFirstName()!=null)
			this.firstnameTextField.setText(user.getFirstName());
		if(user.getLastName()!=null)
			this.lastNameTextField.setText(user.getLastName());
		if(user.getEmail()!=null)
			this.emailTextField.setText(user.getEmail());
		if(user.getTelephone()!=null)
			this.telephoneTextField.setText(user.getTelephone());
		if(user.getArea()!=null)
			this.areaCombo.setValue(user.getArea());

	}


	/**
	 * 
	 *This method add to expire year list more years.
	 */
	private void addYearsToYearList() {
		for(int i=2024;i<2050;i++) {
			year.add(String.valueOf(i));
		}
	}

	

	/**
	 * Handles the event when the "View" button is pressed.
	 * Makes the extra details visible and shows the "Hide" button while hiding the "View" button.
	 * The user pressed on this button to see all general user's for registration details.
	 * @param event The ActionEvent object associated with the button press.
	 */
	@FXML
	void viewPressed(ActionEvent event) {
		extreDetailsIsVisible(true);
		this.hideBtn.setVisible(true);
		this.viewBtn.setVisible(false);
	}

	/**
	 * Handles the event when the "hide" button is pressed.
	 * Hides the extra details and shows the "View" button while hiding the "Hide" button.
	 * The user pressed on this button to hide all general user's for registration details.
	 * @param event The ActionEvent object associated with the button press.
	 */
	@FXML
	void hidePressed(ActionEvent event) {
		extreDetailsIsVisible(false);
		this.hideBtn.setVisible(false);
		this.viewBtn.setVisible(true);
	}


	/**
	 * Sets the visibility of the general user's details fields.
	 * @param val  boolean value that indicates whether the details fields should be visible (true) or not (false)
	 */
	private void extreDetailsIsVisible(boolean val) {
		this.usernameTextField.setVisible(val);
		this.idTextField.setVisible(val);
		this.usernameTextField.setVisible(val);
		this.passwordTextField.setVisible(val);
		this.firstnameTextField.setVisible(val);
		this.lastNameTextField.setVisible(val);
		this.emailTextField.setVisible(val);
		this.telephoneTextField.setVisible(val);
		this.areaCombo.setVisible(val);
		this.userStatusCombo.setVisible(val);
		this.usernamelbl.setVisible(val);
		this.firstnamelbl.setVisible(val);
		this.lastnamelbl.setVisible(val);
		this.idlbl.setVisible(val);
		this.emaillbl.setVisible(val);
		this.passwordlbl.setVisible(val);
		this.telephonelbl.setVisible(val);
		this.arealbl.setVisible(val);
		this.statuslbl.setVisible(val);
		if(idTextField.getText().equals(null))
			this.idTextField.setDisable(false);
		else
			this.idTextField.setDisable(true);
	}

	/**
	 * Handles the event when the "Make Registration" button is pressed.
	 * Creates a new RegisteredCustomer object and checks the user's input fields.
	 * If the input fields are valid, it updates the worker's second role in the workerPermission's table 
	 * and update the Subscriber details in the Subscriber's table and in the RegisteredCustomer's table
	 * also sends a SMS to the phone number and an email to the email address,
	 * And finally, it sets the user status to "Active" for subscriber or "Not Approved" for Registered customer.
	 * @param event The ActionEvent object associated with the button press.
	 */
	@FXML
	void makeregistrationPressed(ActionEvent event) {
		RegisteredCustomer registeredCustomer=new RegisteredCustomer();
		String popUpMsg;

		if(checkFieldsInfo()) {

			/**
			 * 
			 * Setting all details of user's as registeredCustomer.
			 * 
			 */
			registeredCustomer= new RegisteredCustomer(this.passwordTextField.getText(), Integer.parseInt(this.idTextField.getText()), this.firstnameTextField.getText(), this.lastNameTextField.getText(),this.emailTextField.getText(),
					this.telephoneTextField.getText(),user.getRole() ,false, this.usernameTextField.getText(), UserStatus.Not_Approved, this.areaCombo.getValue(), this.creditCardNumTextField.getText(),this.monthCombo.getValue(), this.yearCombo.getValue(),this.cvvTextField.getText());


			/**
			 * 
			 * If this is Subscriber registration.
			 * 
			 */
			if(MakeRegistrationController.getIsSubscriberRegisteration()) {

				/**
				 * 
				 * Set extra details of user of type Subscriber in subscriber entity.
				 */
				Subscriber subscriber=new  Subscriber();
				registeredCustomer.setUserStauts(UserStatus.Active);
				if(user.getRole().equals(Role.RegisteredCustomer))
					registeredCustomer.setRole(Role.Subscriber);
				else
					registeredCustomer.setRole(user.getRole());

				subscriber.setUserName(this.usernameTextField.getText());
				subscriber.setSubscriberNumber(subscriberMax);
				subscriber.setDebtAmount(0);
				subscriber.setFirstOrder(true);


				/**
				 * 
				 * Set user's details in DB in subscriber's table.
				 */
				Hashtable<String,Object> updatedData = new Hashtable<>();
				updatedData.put("updatedSubscriber",subscriber);
				ServerMessage msg = new ServerMessage(Instruction.Update_subscriber_details_After_Subscriber_Registration,updatedData);
				EkrutClientUI.chat.accept(msg);
				while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update

				popUpMsg="The registeration for user: "+user.getUserName()+
						"\nto register as a suscriber Done successfully\n"
						+"and SMS was sent to the phone number: "+this.telephoneTextField.getText()
						+"\nand an email to the email address: "+ this.emailTextField.getText()+"\nwith the subscriber number: #"+(subscriberMax+1);
			}

			/**
			 * 
			 * If this is RegisteredCustomer registration.
			 * 
			 */
			else {
				registeredCustomer.setUserStauts(UserStatus.Not_Approved);
				if(user.getRole().equals(Role.NoRole))
					registeredCustomer.setRole(Role.RegisteredCustomer);
				else
					registeredCustomer.setRole(user.getRole());

				popUpMsg="Request for user: "+user.getUserName()+
						"\nto register as a Registered customer sent for approval to the Area manager " + registeredCustomer.getArea() + ".";
			}

			/**
			 * 
			 * Set user's details in DB in user's table and registeredCustomer's table.
			 */
			Hashtable<String,Object> updatedData = new Hashtable<>();
			updatedData.put("updatedUser",registeredCustomer);
			ServerMessage msg = new ServerMessage(Instruction.Update_user_details_After_RegisteredCustomer_Registration,updatedData);
			EkrutClientUI.chat.accept(msg);
			while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update

			commonActionsController.popUp(new Stage(),
					"Registeration request",popUpMsg,"/serviceRepresentiveEmployeePics/user.gif","/clientGUIScreens/ServiceRepresentiveEmployeeScreen.fxml",event);

		}

	}



	/**
	 * Performs an input integrity check on the given text field input.
	 * The method checks that the input is valid according to the type of characters and possible input length.
	 * @param testFieldInput The input to be checked.
	 * @param minBound The minimum allowed length of the input.
	 * @param maxBound The maximum allowed length of the input.
	 * @param TextFieldName The name of the text field that the input belongs to.
	 * @param charBound The regular expression pattern that the input must match.
	 * @param ErrorMsg The error message to be displayed if the input is invalid.
	 * @return true if the input is valid, false otherwise.
	 */
	private boolean AbstractTest(String testFieldInput, int minBound,int maxBound, String TextFieldName, String charBound, String ErrorMsg) {
		if (testFieldInput.length() != 0 &&testFieldInput.length() >= minBound && testFieldInput.length() <= maxBound) {
			if (!this.checkString(charBound, testFieldInput)) {
				this.errorlabel.setVisible(true);
				this.errorlabel.setText(ErrorMsg);
				return false;
			} else {
				return true;
			}
		} else {
			this.errorlabel.setVisible(true);
			this.errorlabel.setText(TextFieldName + " cannot be empty and the min input length is "+minBound+" and the max input length is " + maxBound);
			return false;
		}
	}


	/**
	 * Checks the validity of the input in all fields.
	 * @return true if all fields have valid input, false otherwise.
	 */
	public boolean checkFieldsInfo() {
		if(this.areaCombo.getValue()==null) {
			this.errorlabel.setVisible(true);
			this.errorlabel.setText("You have to select Area!");
			return false;
		}else if (!this.AbstractTest(this.firstnameTextField.getText(),1, 20, "First name", "^[ A-Za-z]+$", "first name can only contain English letters!")) {
			return false;
		}else if (!this.AbstractTest(this.lastNameTextField.getText(),1,20, "Last name", "^[ A-Za-z]+$", "last name can only contain English letters!")) {
			return false;
		} else if (!this.AbstractTest(idTextField.getText(),9,9, "Id", "[0-9]+$", "id can only contain Numbers and Exactly 9 digits!")) {
			return false;
		} else if (!this.AbstractTest(passwordTextField.getText(),1,20, "Password", "[a-zA-Z0-9]+$", "password can contain Numbers and English letters!")) {
			return false;
		}else if(!emailValidityInput()) {
			return false;
		}else if (!this.AbstractTest(this.creditCardNumTextField.getText(),16,16, "Credit card number", "[0-9]+$", "Credit card number can only contain Numbers and Exactly 16 digits!")) {
			return false;
		} else if (!this.AbstractTest(this.cvvTextField.getText(),3,3, "CVV", "[0-9]+$", "CVV number can only contain Numbers and Exactly 3 digits!")) {
			return false;
		} else if (!this.AbstractTest(this.telephoneTextField.getText(),10,10, "Telephone", "[0-9]+$", "Telephone number can only contain Numbers and Exactly 10 digits!")) {
			return false;
		} else if (this.monthCombo.getValue()==null) {
			this.errorlabel.setText("You must enter expiry month");
			return false;
		} else if(this.yearCombo.getValue()==null) {
			this.errorlabel.setText("You must enter expiry year");
			return false;
		}else return true;
		
	}


	/**
	 * checks the given string against the given regular expression pattern.
	 *Pattern Class - Defines a pattern (to be used in a search).
	 *Matcher Class - Used to search for the pattern.
	 * @param matchingChars The regular expression pattern to match against.
	 * @param toCheck The string to be checked.
	 * @return true if the string matches the pattern, false otherwise.
	 */
	boolean checkString(String matchingChars, String toCheck) {
		Pattern p = Pattern.compile(matchingChars);
		Matcher m = p.matcher(toCheck);
		return m.matches();
	}


	/**
	 * Checks the validity of the input in the email field.
	 * The email must contain a @ symbol, a dot (.) symbol, the dot must be at least one character after the @ symbol,
	 * the string must not contain any spaces.
	 * @return true if the input is valid, false otherwise.
	 */
	private boolean emailValidityInput() {
		boolean isValid = EmailValidator.validateEmail(emailTextField.getText());
		if (!isValid) {
			this.errorlabel.setVisible(true);
			this.errorlabel.setText("The email is not a valid email address,the format email is:\n "
					+ "name@aa.com");
			return false;
		} 
		return true;

	}


	/**
	 * A utility class that provides email validation functionality.
	 * The class uses a regular expression pattern to check the email's validity.
	 */
	private static class EmailValidator {
		private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
		private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

		/**
		 * Validates the given email using a regular expression pattern.
		 * @param email The email to be validated.
		 * @return true if the email is valid, false otherwise.
		 */
		public static boolean validateEmail(String email) {
			Matcher matcher = PATTERN.matcher(email);
			return matcher.matches();
		}
	}



	/**
	 * Handles the event when the back button is pressed.
	 * Clears the user for registration and navigates to the MakeRegistrationScreen(make registration start page).
	 * @param event The ActionEvent object that represents the button press event.
	 */
	@FXML
	void backPressed(ActionEvent event) {
		MakeRegistrationController.setUserForRegisteretion(null);
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/MakeRegistrationScreen.fxml");
	}

	/**
	 * Handles the event when the help button is pressed.
	 * Shows a help message to guide the user in filling in the registration form details.
	 * @param event The ActionEvent object that represents the button press event.
	 */
	@FXML
	void helpPreesed(ActionEvent event) {
		this.helpLabel.setVisible(true);
		this.helpLabel.setText("You must fill in the details about the customer\n when finished click on the"
				+ " \"Send request\" button which will send the registration request to area manager.");
	}


	/**
	 * Handles the event when the log out button is pressed.
	 * Clears the user for registration(set null) and navigates to the login screen.
	 * @param event The ActionEvent object that represents the button press event.
	 */
	@FXML
	void logOutPressed(ActionEvent event) {
		MakeRegistrationController.setUserForRegisteretion(null);
		commonActionsController.logOutPressed(event);


	}

	/**
	 * Handles the event when the X button is pressed.
	 * Clears the user for registration(set null) and closes the current window.
	 * @param event The ActionEvent object that represents the button press event.
	 */
	@FXML
	void xPressed(ActionEvent event) {
		MakeRegistrationController.setUserForRegisteretion(null);
		commonActionsController.xPressed(event);
	}



	/**
	 * Gets the maximum subscriber number.
	 * @return the maximum subscriber number.
	 */
	public static int getSubscriberMax() {
		return subscriberMax;
	}


	/**
	 * Sets the maximum subscriber number.
	 * @param subscriberMax the new maximum subscriber number.
	 */
	public void setSubscriberMax(int subscriberMax) {
		RegistrationFormController.subscriberMax = subscriberMax;
	}
	
	/**
	 * Set images
	 */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		creditcardImage.setImage(new Image("/serviceRepresentiveEmployeePics/registeredCustomer.png"));
		subscriberImage.setImage(new Image("/serviceRepresentiveEmployeePics/subscriber.png"));
		usersDetailsImage.setImage(new Image("/serviceRepresentiveEmployeePics/user.png"));
	} 

}

