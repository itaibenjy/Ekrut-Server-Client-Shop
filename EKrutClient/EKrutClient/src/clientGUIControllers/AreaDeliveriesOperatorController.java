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
 *  Area Deliveries Operator Controller Contains all the
 *         functionality to move the employee to the screen required to perform
 *         the various actions possible.
 */
public class AreaDeliveriesOperatorController {
	
	/*----FXML Attributes----*/
	@FXML
	private Label areaLabel;

	@FXML
	private Label welcomeLabel;

	@FXML
	private ImageView logoIMG;

	@FXML
	private ImageView areaIMG;

	@FXML
	private ImageView completeIMG;

	@FXML
	private ImageView confirmIMG;

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
	/* -----Entities And Controllers----- */
	private UserController userController = new UserController();
	private CommonActionsController commonActionsController = new CommonActionsController();
	private WorkerEntityController workerEntityController = new WorkerEntityController();
	private boolean isInfoBtnPressed = false;
	/**
	 * The initialize method is used to set visibility of the buttons , images and
	 * labels. In addition, this method checks if to this worker have a second role
	 * as a customer, and according to his second role shows the appropriate button.
	 **/
	@FXML
	public void initialize() {
		if (workerEntityController.getSecondRole() == null || userController.getUser().getUserStatus().equals(UserStatus.Not_Approved)) {
			this.additionalPermissionBtn.setVisible(false);
			orderImage.setVisible(false);
		}
		setImages();
		setUserInformation();
		welcomeLabel.setText("Welcome back " + userController.getUser().getFirstName() + " "
				+ userController.getUser().getLastName());
		areaLabel.setText(userController.getUser().getArea().toString());

		additionalPermissionBtn.setText(workerEntityController.getSecondRole() + " View");
	}

	/**
	 * @param event - additional permission button pressed. The method checks if the
	 *              worker has second role as customer and if he have take him to
	 *              the appropriate screen.
	 */
	@FXML
	void additionalPermissionPressed(ActionEvent event) {
		if ((workerEntityController.getSecondRole().equals(Role.Subscriber)))
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/SubscriberScreen.fxml");
		else
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/RegisteredCustomerScreen.fxml");
	}

	/**
	 * @param event - confirm shipments button pressed. The method takes him to the
	 *              ShipmentsConfirmationScreen.
	 */
	@FXML
	void confirmShipments(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/ShipmentsConfirmationScreen.fxml");
	}

	/**
	 * @param event - confirm shipments button pressed. The method takes him to the
	 *              CompleteShipmentsConfirmationScreen.
	 */
	@FXML
	void completeShipments(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/CompleteShipmentsConfirmationScreen.fxml");
	}

	/**
	 * @param event X button pressed. close the program and log out the user.
	 */
	@FXML
	void xPressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}

	/**
	 * @param event log Out Button Pressed log out the user.
	 */
	@FXML
	void logOutPressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
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
	 * Set images for all of the screen's images.
	 */
	private void setImages() {
		logoIMG.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		confirmIMG.setImage(new Image("/manageOrders/order-delivery.png"));
		completeIMG.setImage(new Image("/manageOrders/booking.png"));
		areaIMG.setImage(new Image("/location.png"));
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
