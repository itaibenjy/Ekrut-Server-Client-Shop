package clientGUIControllers;

import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Instruction;
import Enum.Role;
import Enum.UserStatus;
import common.ChangeWindow;
import common.CommonActionsController;
import entities.ServerMessage;
import entityControllers.AreaManagerEntityController;
import entityControllers.NotificationController;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *  Area Manager Controller
 * 	Get All Area Manager Information
 * 	AreaManagerEntityController instance variable to get area manager details
 * 	JavaFX controller
 */
public class AreaManagerController {

	/*FXML Attributes*/
	@FXML
	private Button ConfirmRegistrationRequestButton;

	@FXML
	private Button defineTresholdLevelButton;

	@FXML
	private Button logOut;

	@FXML
	private Button transferExecutionInsturctions;

	@FXML
	private Button viewReportsButton;

	@FXML
	private Button viewUpdatedFacilityInventory;

	@FXML
	private Label welcomeToAreaManagerLabel;

	@FXML
	private Button additionalPermissionBtn;
	
    @FXML
    private Label areaLabel;
    @FXML
    private ImageView logoImage;
    @FXML
    private ImageView areaPhoto;
    
    @FXML
    private ImageView viewReportImage;
    
    @FXML
    private ImageView inventoryImage;

    @FXML
    private ImageView tresholdImage;
    
    @FXML
    private ImageView confirmImage;

    @FXML
    private ImageView instructionImage;
    
    @FXML
    private ImageView orderImage;
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

	/* Entities And Controller */
	private UserController userController = new UserController();
	private ChangeWindow changeWindow = new ChangeWindow();
	private AreaManagerEntityController entity_areaManagerController;
	private static CommonActionsController commonActionsController = new CommonActionsController();
	private NotificationController notification = new NotificationController();
	private static boolean setNotification = true;
	private WorkerEntityController workerEntityController=new WorkerEntityController();
	private boolean isInfoBtnPressed = false;
	


	/**
	 * The initialize method is used to set the values for the combo boxes and the text
	 */

	@FXML
	public void initialize() {


		if (workerEntityController.getSecondRole()==null || userController.getUser().getUserStatus().equals(UserStatus.Not_Approved))
		{
		this.additionalPermissionBtn.setVisible(false);
		orderImage.setVisible(false);
		}
		areaLabel.setText(userController.getUser().getArea().toString());
		additionalPermissionBtn.setText(workerEntityController.getSecondRole()+ " View");
		setImages();
		setUserInformation();
		entity_areaManagerController = new AreaManagerEntityController();
		this.welcomeToAreaManagerLabel.setText("Welcome back " + userController.getUser().getFirstName() + " "
				+ userController.getUser().getLastName());
		requestAreaManagerDetails();
	



	}



	/**
	 * @param event View Reports Button Pressed
	 */
	@FXML
	void viewReportsButton_Pressed(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		this.changeWindow.changeScreen(new Stage(), "/clientGUIScreens/ViewReportScreen.fxml");
	}

	/**
	 * @param event View Updated Facility Inventory Button Pressed
	 */
	@FXML
	void viewUpdatedFacilityInventory_ButtonPressed(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		this.changeWindow.changeScreen(new Stage(), "/clientGUIScreens/ViewUpdatedFacilityInventoryScreen.fxml");
	}

	/**
	 * @param event Define Treshold Level Button Pressed
	 */
	@FXML
	void defineTresholdLevelButton_Pressed(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		this.changeWindow.changeScreen(new Stage(), "/clientGUIScreens/DefineThresholdLevelScreen.fxml");

	}

	/**
	 * @param event Confirm Registration Request Button Button Pressed
	 */
	@FXML
	void ConfirmRegistrationRequestButton_ButtonPressed(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		this.changeWindow.changeScreen(new Stage(), "/clientGUIScreens/ConfirmRegistrationRequestScreen.fxml");
	}

	/**
	 * @param event Transfer Execution Insturctions Button Pressed
	 */
	@FXML
	void transferExecutionInsturctions_ButtonPressed(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		this.changeWindow.changeScreen(new Stage(), "/clientGUIScreens/TransferExecutionInstructionsScreen.fxml");
	}

	/**
	 * @param event log Out Button Pressed log out the user 
	 */
	@FXML
	void logOut_ButtonPressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
		setNotification = true;
	}

	/**
	 * @param event X Button Pressed method closes the window
	 */
	@FXML
	void x_ButtonPressed(ActionEvent event) {
		commonActionsController.xPressed(event);
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
	 * @param event to the actions he can do in his second role
	 */
	@FXML
	void additionalPermissionPressed(ActionEvent event) {
		if((workerEntityController.getSecondRole().equals(Role.Subscriber)))	
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/SubscriberScreen.fxml");
		else
			commonActionsController.backOrNextPressed(event,"/clientGUIScreens/RegisteredCustomerScreen.fxml");
	}
	
	/**
	 * Set images
	 */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		viewReportImage.setImage(new Image("/ReportPhotos/report.png"));
		inventoryImage.setImage(new Image("/ReportPhotos/inventory.png"));
		tresholdImage.setImage(new Image("/ReportPhotos/return.png"));
		confirmImage.setImage(new Image("/ReportPhotos/online-registration.png"));
		instructionImage.setImage(new Image("/ReportPhotos/manufacturing.png"));
		orderImage.setImage(new Image("/ReportPhotos/buy.png"));		
		areaPhoto.setImage( new Image("/location.png"));
	}

	/**
	 * get area manager details from DB
	 * Pop Notification
	 */
	private void requestAreaManagerDetails() {

		ServerMessage msg;
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("user", userController.getUser());
		msg = new ServerMessage(Instruction.Get_Area_Manager_Details, data);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {	}; // wait for server update
		if (setNotification == true) {
			if (!notification.getContext().equals("")) {
				commonActionsController.popUp(new Stage(), "Notification", notification.getContext(), "/ReportPhotos/email.gif",null,null);
				setNotification = false;
			}

		}

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