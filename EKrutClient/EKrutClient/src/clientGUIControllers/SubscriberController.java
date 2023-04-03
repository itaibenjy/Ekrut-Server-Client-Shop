package clientGUIControllers;

import Client.ClientConsole;
import Enum.Role;
import Enum.SupplyMethod;
import Enum.UserStatus;
import common.CommonActionsController;
import entities.Order;
import entities.User;
import entityControllers.OrderEntityController;
import entityControllers.UserController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 *This class is used to handle the subscriber's GUI screen.
 * It allows the subscriber to make an order, view catalog, manage his orders and pick up an order.
 * It handles the actions performed by the subscriber.
 *The class has several methods that handle the different button presses on the Subscriber screen, such as the logOutPressed,
 *manageMyOrderPressed, pickUpMyOrderPressed,viewCatalogPressed and makeAnOrderPressed methods.
 */
public class SubscriberController {

	/**
	 * FXML Attributes
	 */
	@FXML
	private Button logOutBtn;

	@FXML
	private Button makeAnOrderBtn;

	@FXML
	private Button manageMyOrderBtn;

	@FXML
	private Button pickUpMyOrderBtn;

	@FXML
	private Button viewcatalogBtn;

	@FXML
	private Label welcomeBackLabel;

	@FXML
	private Button xBtn;

	@FXML
	private Button backBtn;

	@FXML
	private ImageView pickupPhoto;
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
	 * Entities,variables And Controllers
	 */
	Scene popUpScene;
	private UserController userController = new UserController();
	private User customer;
	private CommonActionsController commonActionsController = new CommonActionsController();
	private OrderEntityController orderEntityController;
	private ClientConsole clientConsole = new ClientConsole();
	private boolean isInfoBtnPressed = false;

	/**
	 *The initialize method is called when the fxml file is loaded.
	 *It initializes the pickUpMyOrderBtn and pickupPhoto as invisible, sets the customer variable to the current user,
	 *sets the welcome back label text, disables the make an order button if the user's status is not Active,
	 *sets the pickUpMyOrderBtn and pickupPhoto as visible if the client configuration is not "OL",
	 *and sets the back button as visible if the user's role is not Subscriber.
	 */
	@FXML
	public void initialize() {
		setUserInformation();
		pickUpMyOrderBtn.setVisible(false);
		pickupPhoto.setVisible(false);
		customer = userController.getUser();
		this.welcomeBackLabel.setText("Welcome back " + customer.getFirstName() + " " + customer.getLastName());
		if (!(userController.getUser().getUserStatus().equals(UserStatus.Active)))
			this.makeAnOrderBtn.setDisable(true);
		if (!clientConsole.getConfiguration().equals("OL")) {
			pickUpMyOrderBtn.setVisible(true);
			pickupPhoto.setVisible(true);
		}
		if (!(userController.getUser().getRole().equals(Role.Subscriber))) {
			this.backBtn.setVisible(true);
		} else {
			this.backBtn.setVisible(false);
		}
	}

	/**
	 * This method handles the action event when the back button is pressed. It
	 * calls the backOrNextPressed(ActionEvent, String) method with the event and
	 * the appropriate screen according to the current user's role.
	 * 
	 * @param event the event that triggered this method, in this case the back
	 *              button being pressed.
	 */
	@FXML
	void backPressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event,
				"/clientGUIScreens/" + userController.getUser().getRole() + "Screen.fxml");

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
	 * @param event the event that triggered the action. This method is triggered
	 *              when the user presses the 'X' button. It calls the xPressed
	 *              method from the CommonActionsController class. This method is
	 *              responsible for closing the current window.
	 */
	@FXML
	void xPressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}

	/**
	 * Handles the logout button press event. It calls the logOutPressed method in
	 * the CommonActionsController class.
	 * 
	 * @param event the logout button press event
	 */
	@FXML
	void logOutPressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}

	/**
	 * This method is used to handle the event when the user clicks on the 'Manage
	 * My Order' button. It navigates the user to the 'ManageMyOrderScreen.fxml'
	 * screen. 'ManageMyOrderScreen.fxml' screen the user can view his order history
	 * and confirm receipt of deliveries.
	 * 
	 * @param event ActionEvent - The event that triggers this method.
	 */
	@FXML
	void manageMyOrderPressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/ManageMyOrderScreen.fxml");
	}

	/**
	 * This method handles the event when the "pickUpMyOrderBtn" button is pressed.
	 * It navigates the user to the "PickUpOrderScreen.fxml" screen, which allows
	 * the user to pick up their order.
	 * 
	 * @param event the event of pressing the "pickUpMyOrderBtn" button.
	 */
	@FXML
	void pickUpMyOrderPressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/PickUpOrderScreen.fxml");
	}

	/**
	 * The method handles the event of pressing the view catalog button. It calls
	 * the backOrNextPressed(ActionEvent, String)} method from the
	 * CommonActionsController class, the user will be transferred to the view
	 * catalog screen to see the all products.
	 * 
	 * @param event The event of pressing the view catalog button.
	 */
	@FXML
	void viewCatalogPressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/ViewCatalogScreen.fxml");

	}

	/**
	 * This method is executed when the "Make an Order" button is pressed. It checks
	 * the current configuration of the client console and either navigates to the
	 * Remote Order Screen for remote order(OLbconfiguration) or the My Order Screen
	 * for local order(EK configuration).
	 * 
	 * @param event the ActionEvent that triggered this method.
	 */
	@FXML
	void makeAnOrderPressed(ActionEvent event) {
		Order newOrder = new Order();
		if (clientConsole.getConfiguration().equals("OL")) {
			orderEntityController = new OrderEntityController();
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/RemoteOrderScreen.fxml");
		} else {
			orderEntityController = new OrderEntityController();
			
			newOrder.setFacilityId(clientConsole.getConfiguration());
			newOrder.setSupplyMethod(SupplyMethod.LocalOrder);
			orderEntityController.setOrder(newOrder);
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/MyOrderScreen.fxml");
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