package clientGUIControllers;

import Enum.Role;
import Enum.UserStatus;
import common.CommonActionsController;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;



/**
*This class is a controller class for the ServiceRepresentiveEmployee FXML file.
*It is responsible for handling the actions and events that occur on the ServiceRepresentiveEmployee screen.
*If the ServiceRepresentiveEmployee have seconder role he has all the permissions of registeredCustomer or Subscriber according to his second role.
*It set the user options: make new registration or extra permission button as a registerdCustomer or as a Subscriber(if the user have second role).
log out and XBtn and redirects the user to the next page according to the button he clicked.
*/
public class ServiceRepresentiveEmployeeController {

	/**
	 *FXML Attributes
	 */
	@FXML
	private ImageView imageView;

	@FXML
	private Button logOutBtn;

	@FXML
	private Button makeNewRegisterationRequest;


	@FXML
	private Label welcomeBackLabel;

	@FXML
	private Button xBtn;

	@FXML
	private ImageView servicerepresentiveEmployeePic;
	
    @FXML
    private ImageView logoImage;
    
    @FXML
    private ImageView makeRegImage;
    
    @FXML
    private ImageView orderImage;
   
	@FXML
	private Button additionalPermissionBtn;
	 // user details
    @FXML
	private AnchorPane info;
	@FXML
	private Button infoBtn;
 	@FXML
 	private Label Fname;
 	@FXML
 	private Label Lname;
 	@FXML
 	private Label Email;
 	@FXML
 	private Label phone;
 	@FXML
 	private Label userStatus;
	
	/**
	 * Entities And Controllers. 
	 */
	private CommonActionsController commonActionsController = new CommonActionsController();
	private UserController userController=new UserController();
	private WorkerEntityController workerEntityController=new WorkerEntityController();
	private boolean isInfoBtnPressed = false;


	
	/**
	*The initialize method is called automatically when the FXML file is loaded.
	*It sets the text of the welcomeBackLabel to "Welcome back" and the user's first name.
	*It checks if the workerEntityController's second role is null, if it is then the additionalPermissionBtn and orderImage are set to be not visible
	*else to worker service representative employee have extra permissions as registeredCustomer or as a subscriber.
	*and then the additionalPermissionBtn is set to the value of the second role and "View" is added to it.
	*It sets the servicerepresentiveEmployeePic, makeRegImage, orderImage, and logoImage to the specified images.
	*/
	@FXML
	public void initialize() {
		setUserInformation();
		this.welcomeBackLabel.setText("Welcome back " + userController.getUser().getFirstName() + " "
				+ userController.getUser().getLastName());
		if (workerEntityController.getSecondRole()==null  || userController.getUser().getUserStatus().equals(UserStatus.Not_Approved))
		{
			this.additionalPermissionBtn.setVisible(false);
			orderImage.setVisible(false);
		}
		additionalPermissionBtn.setText(workerEntityController.getSecondRole()+ " View");
		setImages();
	}
	/**
	 * A button when clicked it displays information about the user.
	 * Another click will hide the information
	 * @param event
	 */
	@FXML
	void infoBtn_Pressed(ActionEvent event) {
		
		if (isInfoBtnPressed) {
			infoBtn.setText("Show user Deatils");
			info.setVisible(false);
			isInfoBtnPressed =false;
		}
		else {
			infoBtn.setText("Hide user Deatils  ");
			info.setVisible(true);
			isInfoBtnPressed =true;
		}
	}

	/**
	*The additionalPermissionPressed method is called when the additionalPermissionBtn is pressed.
	*It checks if the workerEntityController's second role is equal to "Subscriber" and if so, it navigates to the SubscriberScreen.fxml.
	*If the workerEntityController's second role is not "Subscriber" it navigates to the RegisteredCustomerScreen.fxml.
	*After the user navigates to the appropriate screen he can do all the action as registeredCustomer or as a subscriber.
	*@param event the action event that occurred, in this case, the button press.
	*/
	@FXML
	void additionalPermissionPressed(ActionEvent event) {
		if((workerEntityController.getSecondRole().equals(Role.Subscriber)))	
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/SubscriberScreen.fxml");
		else
			commonActionsController.backOrNextPressed(event,"/clientGUIScreens/RegisteredCustomerScreen.fxml");
	}

	
	/**
	*The makeNewRegisterationRequestPresserd method is called when the makeNewRegisterationRequest button is pressed.
	*it navigates the user to the MakeRegistrationScreen.fxml for getting user'd details for registration.
	*@param event the action event that occurred, in this case, the button press.
	*/
	@FXML
	void makeNewRegisterationRequestPresserd(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/MakeRegistrationScreen.fxml");
	}

	
	/**
	*The xPressed method is called when the x button is pressed.
	*It calls the xPressed method of the commonActionsController to handle the action.
	*@param event the action event that occurred, in this case, the button press.
	*/
	@FXML
	void xPressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}

	/**
	*The logOutPressed method is called when the logOut button is pressed.
	*It calls the logOutPressed method of the commonActionsController to handle the action.
	*@param event the action event that occurred, in this case, the button press.
	*/
	@FXML
	void logOutPressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);

	}
	
	/**
	 * Set images
	 */
	private void setImages() {
		servicerepresentiveEmployeePic.setImage(new Image("/customerService.png"));
		makeRegImage.setImage(new Image("/serviceRepresentiveEmployeePics/registration-2.png"));
		orderImage.setImage(new Image("/ReportPhotos/buy.png"));
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));	
	} 
	private void setUserInformation() {
		Fname.setText(userController.getUser().getFirstName());
		Lname.setText(userController.getUser().getLastName());
		Email.setText(userController.getUser().getEmail());
		phone.setText(userController.getUser().getTelephone());
		userStatus.setText(userController.getUser().getUserStatus().toString());
		if (userController.getUser().getUserStatus().toString().equals("Active")) {
			userStatus.setId("LabelActive");
		} else {
			userStatus.setId("LabelNoActive");
		}
	}
}



