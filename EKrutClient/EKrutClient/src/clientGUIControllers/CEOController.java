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
 * CEO Controller
 * This class is used to handle the CEO's GUI screen.
 * This controller inculde all the actions that CEO can do.
 * The class has several methods that handle the different button presses, such view reports (like AreaManager, but with permission to all areas),
 * or Shipment report that is unique action to CEO
 * It is also show to CEO if he has another role, if yes he can also do the actions of the second role if he want to.
 * 	JavaFX controller
 */


public class CEOController {

	@FXML
	private Button LogOut;

	@FXML
	private Button viewReports;

	@FXML
	private Button shipmentInformation;

	@FXML
	private Button Exit_Button;

	@FXML
	private Label ceoLabel;

	@FXML
	private ImageView orderImage;

	@FXML
	private ImageView viewReportImage;

	@FXML
	private ImageView logoImage;

	@FXML
	private ImageView shipmentImage;

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
	
	private UserController userController = new UserController();
	private CommonActionsController commonActionsController = new CommonActionsController();
	private WorkerEntityController workerEntityController = new WorkerEntityController();
	private boolean isInfoBtnPressed = false;

	@FXML
	public void initialize() {

		ceoLabel.setText("Welcome Back " + userController.getUser().getFirstName() + " "
				+ userController.getUser().getLastName());
		if (workerEntityController.getSecondRole() == null || userController.getUser().getUserStatus().equals(UserStatus.Not_Approved)) {
			this.additionalPermissionBtn.setVisible(false);
			orderImage.setVisible(false); // set image to extra premissions
		}
		additionalPermissionBtn.setText(workerEntityController.getSecondRole() + " View");
		setImages();
		setUserInformation();
	}

	/**
	 * @param event to the actions he can do in his second role
	 */
	@FXML
	void additionalPermissionPressed(ActionEvent event) {
		if ((workerEntityController.getSecondRole().equals(Role.Subscriber)))
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/SubscriberScreen.fxml");
		else
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/RegisteredCustomerScreen.fxml");
	}

	/**
	 * @param event X Button Pressed method closes the window
	 */
	@FXML
	void Exit_Pressed(ActionEvent event) {
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
	 * @param event log Out Button Pressed log out the user 
	 */
	@FXML
	void LogOut_Pressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}

	 /**
     * @param event choose the view shipment report action
     */
	@FXML
	void shipmentInformation_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/CheckShipmentReportScreen.fxml");
	}

	 /**
     * @param event choose the view report action
     */
	@FXML
	void viewReports_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/ViewReportScreen.fxml");
	}

	/**
	 * Set images
	 */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		viewReportImage.setImage(new Image("/ReportPhotos/report.png"));
		shipmentImage.setImage(new Image("/manageOrders/approval.png"));
		orderImage.setImage(new Image("/ReportPhotos/buy.png"));

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
