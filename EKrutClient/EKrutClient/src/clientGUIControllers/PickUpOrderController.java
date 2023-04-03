/**
 * A class that represents the option to collect the shipment at a specific facility in the OK configuration.
 * The customer will enter an order code and collect his order unless:
 * The code does not exist in the system.
 * The order has already been collected.
 * The customer tries to pick up an order from an inappropriate facility.
 * Messages went out accordingly.
 * If the order exists, an execution order will be sent to EK-OP for the robot to create the order,
 * and the order collection date will be updated to the current collection date.
 * 
 * Tables are used:
 * order - in order to take the information about the location of the facility.
 * orderselfcollection - you will know which orders are waiting for collection
 */
package clientGUIControllers;

import java.sql.Date;
import java.util.Hashtable;

import Client.ClientConsole;
import Client.EkrutClientUI;
import Enum.Instruction;
import common.CommonActionsController;
import entities.ServerMessage;
import entityControllers.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class PickUpOrderController {
	/* Attributes */
	// controllers:
	private CommonActionsController commonActionsController = new CommonActionsController();
	private UserController userController = new UserController();

	// Variables:

	private String realFacility = ClientConsole.getConfiguration(); // TODO change this Variable to get it from argument
																	// in EK method
	private String val_fieldBox;
	private static String facilityID;
	private static String userNameDB;
	private static Date actualPickUpDate;

	/* End Attributes */

	/* Start FXML */
	// Images
	@FXML
	private ImageView logoImage;
	@FXML
	private ImageView userPhoto;
	@FXML
	private ImageView complete;
	@FXML
	private TextField entrerOrderField;

    @FXML
    private Text help_label;
	
	// labels
	@FXML
	private Label errorLabel;
	@FXML
	private Label orderFoundLabel;
	@FXML
	private Label labelCollected;
	@FXML
	private Label labelWrong;

	// Buttons
	@FXML
	private Button Back_Button;
	@FXML
	private Button Exit_Button;
	@FXML
	private Button LogOut;
	@FXML
	private Button pickButton;
	@FXML
	private Button search_Button;
	@FXML
	private Button help_button;
	// Events
	
	/**
	 * Clicking on this button will return us to the previous page from which we arrived.
	 * @param event
	 */
	@FXML
	void BackButton_Pressed(ActionEvent event) {
		// TODO change back location
		commonActionsController.backOrNextPressed(event,
				"/clientGUIScreens/" + userController.getUser().getRole() + "Screen.fxml");
	}
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
	 * Clicking this button will open a help menu with the actions to be performed on this screen.
	 * @param event
	 */
    @FXML
    void helpButton_Pressed(ActionEvent event) {
    	help_button.setVisible(false);
    	help_label.setVisible(true);
    }
	@FXML
	private void initialize() {
		setImages();
	}
	// Search button pressed
	@FXML
	void searchButton_Pressed(ActionEvent event) {
		// rest window
		orderFoundLabel.setVisible(false);
		pickButton.setVisible(false);
		errorLabel.setVisible(false);
		labelCollected.setVisible(false);
		labelWrong.setVisible(false);

		val_fieldBox = entrerOrderField.getText();
		if (checkIfQuantityContainsOnlyNumbers(val_fieldBox) == true) {
			getOrderFromDB(val_fieldBox);
			// found order to pick up in DB
			if (!(facilityID.equals(""))) {
				if (checkValidValues() == true) {
					orderFoundLabel.setVisible(true);
					pickButton.setVisible(true);
				}
			}
			// didn't found order id on DB
			else {
				labelWrong.setText("Sorry, but we couldn't find a self collection order with this ID");
				labelWrong.setVisible(true);
			}
		}
	}

	/**
	 * Clicking this button will update the status of the order to be collected.
	 * @param event
	 */
	@FXML
	void pickButton_Pressed(ActionEvent event) {
		updatePickUpDate();
		commonActionsController.popUp(new Stage(), "The updatde succeed",
				"The order was sent for execution by the EK-OP robot.\nThank you and goodbye",
				"file:../../clientGuiAssets/ROBOT.gif", "/clientGUIScreens/PickUpOrderScreen.fxml", event);
	}
	/* END FXML */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		complete.setImage(new Image("/complete.gif"));
		
	}
	public void setFacilityID(String FacilityID) {
		facilityID = FacilityID;
	}

	public void setActualPickUpDate(Date ActualPickUpDate) {
		actualPickUpDate = ActualPickUpDate;
	}

	public void setUserNameDB(String UserNameDB) {
		userNameDB = UserNameDB;
	}

	
	/**
	 * Spell check input for Threshold Level Selected
	 * @param val_fieldBox
	 * @return
	 */
	private boolean checkIfQuantityContainsOnlyNumbers(String val_fieldBox) {
		// Empty value
		if (val_fieldBox.isEmpty()) {
			errorLabel.setVisible(true);
			errorLabel.setText("Must enter a value!");
			return false;
		}

		// Check that the field contain only digits
		for (int i = 0; i < val_fieldBox.length(); i++) {
			// there is char in the field
			if (!Character.isDigit(val_fieldBox.charAt(i))) {
				errorLabel.setVisible(true);
				errorLabel.setText("Order ID must consist of only numbers!");
				return false;
			}
		}

		return true;
	}

	
	/**
	 * Trying to get order actualPickUpDate and facilityID to self collection order
	 * @param val_fieldBox
	 */
	private void getOrderFromDB(String val_fieldBox) {
		try {
			Hashtable<String, Object> data = new Hashtable<>();
			data.put("orderNum", val_fieldBox);
			data.put("userName", userController.getUser().getUserName());
			ServerMessage msg = new ServerMessage(Instruction.Get_PickUp_Order, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Check if the order valid
	 * @return
	 */
	private boolean checkValidValues() {
		boolean vaildInputs = true;
		// username on the order not same the correct username
		if (!(userNameDB.equals(userController.getUser().getUserName()))) {
			labelWrong.setText("Sorry but the username on this order does not match this account");
			labelWrong.setVisible(true);

			vaildInputs = false;
		}

		// Checking that the order has not already been collected
		// already picked up
		if (actualPickUpDate != null) {
			labelCollected.setVisible(true);
			vaildInputs = false;
		}
		// Checking that the order is taken from the correct facility
		else if (!facilityID.equals(realFacility)) {
			labelWrong.setText("Sorry but this is the wrong facility, you will need to collect from: " + facilityID);
			labelWrong.setVisible(true);
			vaildInputs = false;
		}
		return vaildInputs;
	}

	
	/**
	 * Update pick up date in DB
	 */
	private void updatePickUpDate() {
		try {
			Hashtable<String, Object> data = new Hashtable<>();
			data.put("orderNum", val_fieldBox);
			ServerMessage msg = new ServerMessage(Instruction.SET_PickUp_Date, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
