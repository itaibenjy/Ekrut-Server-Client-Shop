
package clientGUIControllers;

import Enum.Role;
import Enum.UserStatus;
import common.ChangeWindow;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This page includes the home screen of the operations worker,
 *  he can place an order according to the privileges he has
 *  or go to the page for updating inventory in the facilities
 */

public class OpreatingWorkerController {
	/* Attributes */
	// controllers:
	private CommonActionsController commonActionsController = new CommonActionsController();
	private UserController userController = new UserController();
	private WorkerEntityController workerEntityController = new WorkerEntityController();
	private ChangeWindow changeWindow = new ChangeWindow();
	private boolean isInfoBtnPressed = false;
	/* End Attributes */

	/* FXML */
	@FXML
	private Text help_label;
	// labels:
	@FXML
	private Label MarketingMangerName;
	@FXML
	private Label welcomeToOperatingWorkerLabel;
	// Buttons:
	@FXML
	private Button Exit_Button;
	@FXML
	private Button InventoryUpdate;
	@FXML
	private Button LogOut;
	@FXML
	private Button additionalPermissionBtn;
	@FXML
	private Button help_button;
	// Images:
	@FXML
	private ImageView logoImage;
	@FXML
	private ImageView orderImage;
	@FXML
	private ImageView InventoryUpdateImage;

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
	 * initialize
	 */
	@FXML
	private void initialize() {
		if (workerEntityController.getSecondRole() == null || userController.getUser().getUserStatus().equals(UserStatus.Not_Approved)) {
			this.additionalPermissionBtn.setVisible(false);
			orderImage.setVisible(false);
		}
		additionalPermissionBtn.setText(workerEntityController.getSecondRole() + " View");

		this.welcomeToOperatingWorkerLabel.setText(
				"Welcome back " + userController.getUser().getFirstName() + " " + userController.getUser().getLastName());
		setImages();
		setUserInformation();
	}

	// Events:
	/**
	 * Pressing this button will completely take us out of the screen.
	 * @param event
	 */
	@FXML
	void Exit_Pressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}
	/**
	 * Clicking this button will disconnect the user from the system and take him to the login screen.
	 * @param event
	 */
	@FXML
	void LogOut_Pressed(ActionEvent event) {
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
			infoBtn.setText("Hide user Deatils");
			info.setVisible(true);
			isInfoBtnPressed =true;
		}
	}
	
	/**
	 * Clicking this button will open a help menu with the actions to be performed on this screen.
	 * @param event
	 */
	@FXML
	void helpButton_Pressed(ActionEvent event) {
		help_button.setVisible(false);
		help_label.setVisible(true);
	}

	/**
	 * Clicking this button will take us to the inventory update screen
	 * @param event
	 */
	@FXML
	void InventoryUpdate_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/UpdateInventoryScreen.fxml");

	}
	/**
	 * Clicking this button will take me to the screen according to the client's permissions
	 * @param event
	 */
	@FXML
	void additionalPermissionPressed(ActionEvent event) {

		if ((workerEntityController.getSecondRole().equals(Role.Subscriber)))
			this.changeWindow.changeScreen(new Stage(), "/clientGUIScreens/SubscriberScreen.fxml");
		else
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/RegisteredCustomerScreen.fxml");
	}

	/* End FXML */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		orderImage.setImage(new Image("/ReportPhotos/buy.png"));
		InventoryUpdateImage.setImage(new Image("/product.png"));
	}
	
	/**
	 * Clicking this button will open me information about the user
	 */
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
