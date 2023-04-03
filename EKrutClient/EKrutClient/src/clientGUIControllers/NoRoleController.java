package clientGUIControllers;

import Enum.Role;
import Enum.UserStatus;
import common.CommonActionsController;
import entityControllers.UserController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


/**
* This class is used to handle the display of a screen for users who do not have a role.
* It has several methods that handle button press events and initialize the screen.
* The logOutPressed method handles the event when the logout button is pressed and calls the logOutPressed method of the commonActionsController.
* The wantToBeRegisteredPressed method handles the event when the "want to be registered" button is pressed 
* and opens a pop-up window with information on how to register as a subscriber.
* 
* */
public class NoRoleController {

	/**
	 *FXML Attributes
	 */
	@FXML
	private Button logOutBtn;

	@FXML
	private Button wantToBeRegisteredCustomerBtn;

	@FXML
	private Label welcomeBackLabel;

	@FXML
	private ImageView logoIMG;

	@FXML
	private ImageView registerIMG;

	@FXML
	private ImageView joinIMG;

	@FXML
	private Label instructionLabel;

	@FXML
	private Button xBtn;

	/**
	 * Entities,variables And Controllers 
	 */
	private CommonActionsController commonActionsController = new CommonActionsController();
	private UserController userController=new UserController();

	
	/**
	 * The initialize method is called when the NoRoleController class is loaded. 
	 * It sets the logoIMG to the specified image, sets the instructionLabel and joinIMG based on
	 *  the user's role and status, and sets the welcomeBackLabel to display a welcome message to the user.
	 * If the user's status is Not_Approved and role is RegisteredCustomer, it sets the instructionLabel to display a message
	 * indicating that the registration request is awaiting approval and sets the wantToBeRegisteredCustomerBtn to be invisible.
	 * Otherwise, it sets the instructionLabel to display a message indicating that the user must be a registered customer to
	 * gain access to all areas of the system, sets images to specified images, 
	 * sets the wantToBeRegisteredCustomerBtn to be visible, and sets the welcomeBackLabel to display a welcome message to the user.
	 */
	@FXML
	public void initialize() {
		logoIMG.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		if(userController.getUser().getUserStatus().equals(UserStatus.Not_Approved) && userController.getUser().getRole().equals(Role.RegisteredCustomer)) {
			instructionLabel.setText("Your registration request has not yet been approved and is awaiting approval from the regional manager.");
			joinIMG.setImage(new Image("/RegisteredCustomerScreenPic/hourglass.gif"));
			wantToBeRegisteredCustomerBtn.setVisible(false);
		}
		else {
			instructionLabel.setText("In order to gain access to all areas of the system \n(such as make an order, view the product catalog, etc). \nyou must be a registered customer!");
			registerIMG.setImage(new Image("/RegisteredCustomerScreenPic/user.gif"));
			joinIMG.setImage(new Image("/RegisteredCustomerScreenPic/signup.png"));
			this.welcomeBackLabel.setText("Welcome back " + userController.getUser().getFirstName() + " " + userController.getUser().getLastName());
			wantToBeRegisteredCustomerBtn.setVisible(true);
		}
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
	 * The wantToBeRegisteredPressed method is called when the wantToBeRegisteredCustomerBtn is pressed.
	 * It opens a pop-up window with the title "Registered Customer registration request",
	 * displaying a message asking the user to contact a service representative at the provided number
	 * to register as a Registered Customer.
	 * @param event The ActionEvent that triggers the method.
	 */
	@FXML
	void wantToBeRegisteredPressed(ActionEvent event) {
		commonActionsController.popUp(new Stage(),
				"Sign Up",
				"To register as a registered customer,\n please contact a service representative at the number:\n 0525381648","/RegisteredCustomerScreenPic/userPopUp.gif",null,null);
	}

	/**
	 *The xPressed(ActionEvent event) method is called when the "X" button is pressed.
	 *It calls the xPressed method of the commonActionsController class.
	 *@param event The ActionEvent that triggers the method.
	 */
	@FXML
	void xPressed(ActionEvent event) {
		commonActionsController.xPressed(event);

	}
}
