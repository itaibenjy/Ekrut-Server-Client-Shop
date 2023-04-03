package clientGUIControllers;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.ResourceBundle;
import Client.ClientConsole;
import Client.EkrutClientUI;
import Enum.Instruction;
import Enum.Role;
import Enum.SupplyMethod;
import common.ChangeWindow;
import common.CommonActionsController;
import common.Observer;
import entities.RegisteredCustomer;
import entities.SelfCollection;
import entities.ServerMessage;
import entities.Shipment;
import entities.Subscriber;
import entities.User;
import entityControllers.OrderEntityController;
import entityControllers.OrderTimerEntityController;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * This class display to the user all the details of the order and his personal
 * details, gives the user payments option (credit cart and monthly payment if
 * the he is a subscriber) when the user press pay the order will be saved in
 * the server and a pop up with the order number will dispaly.
 * 
 * This class makes use of this tables: 'order', to create the order in the
 * database 'ordershipment', save shipment details when the order is type
 * shipment 'orderselfcollecton', save the self collection details when the
 * order is type self collection 'productinfacility' or 'product', update
 * inventory on quit or logout
 */
public class PaymentController implements Initializable, Observer {

	// JavaFx Elements
	@FXML
	private Label price;

	@FXML
	private Label nameValue;

	@FXML
	private Label phoneValue;

	@FXML
	private Label emailValue;

	@FXML
	private Label facilityValue;
	@FXML
	private Label FACILITY_NAME;

	@FXML
	private Label methodValue;

	@FXML
	private Label creditName;

	@FXML
	private Label expName;

	@FXML
	private Label cvvName;

	@FXML
	private Label creditNumber;

	@FXML
	private Label expValue;

	@FXML
	private Label cvvValue;

	@FXML
	private Label depthName;

	@FXML
	private Label depthValue;

	@FXML
	private Label timer;
	
    @FXML
    private Label errorLabel;

	@FXML
	private Button payNow;

	@FXML
	private Button supplyNavButton;

	@FXML
	private ComboBox<String> paymentCombo;

	// Attributes
	private OrderTimerEntityController orderTimerEntityController = new OrderTimerEntityController();
	private OrderEntityController orderEntityController = new OrderEntityController();
	private DecimalFormat format = new DecimalFormat("0.#");
	private CommonActionsController commonActionsController = new CommonActionsController();
	private ObservableList<String> paymentOptions = FXCollections.observableArrayList();
	private UserController userController = new UserController();
	private WorkerEntityController workerEntityController = new WorkerEntityController();
	private User user;

	// Methods

	/**
	 * Initialize the payment controller with the necessary values and show the
	 * default information needed on the payment screen
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		errorLabel.setVisible(true);
		orderTimerEntityController.getOrderTimer().add(this);
		payNow.setDisable(true);

		// get subscriber or register from database
		user = userController.getUser();
		hideAll();
		paymentOptions.add("Credit Card");

		if (userController.getUser().getRole().equals(Role.Subscriber)
				|| ((workerEntityController.getSecondRole() != null)
						&& workerEntityController.getSecondRole().equals(Role.Subscriber))) {
			paymentOptions.add("Monthly Payment");
			depthValue.setText(format.format(((Subscriber) user).getDebtAmount()) + "₪");
		}
		setUser(user);
		if (orderEntityController.getOrder().getFacilityId() == null) {
			facilityValue.setVisible(false);
			FACILITY_NAME.setVisible(false);
		}
		facilityValue.setText(orderEntityController.getOrder().getFacilityId());
		creditNumber.setText(((RegisteredCustomer) user).getCreditCardNumber());
		expValue.setText(
				((RegisteredCustomer) user).getExpireMonth() + "/" + ((RegisteredCustomer) user).getExpireYear());
		cvvValue.setText(((RegisteredCustomer) user).getCvv());
		price.setText(format.format(orderEntityController.getOrder().getTotalPrice()) + "₪");
		methodValue.setText(orderEntityController.getOrder().getSupplyMethod().name());
		paymentCombo.setItems(paymentOptions);

		if (!ClientConsole.getConfiguration().equals("OL")) {
			supplyNavButton.setDisable(true);
		}

	}

	/**
	 * This method set the user details
	 * 
	 * @param user the user to display
	 */
	private void setUser(User user) {
		nameValue.setText(user.getFirstName() + " " + user.getLastName());
		phoneValue.setText(user.getTelephone());
		emailValue.setText(user.getEmail());
	}

	/**
	 * This method gets called when user select a value in the payment combo box and
	 * call the method to set the correct labels
	 * 
	 * @param event ActionEvent of the select combo box
	 */
	@FXML
	void paymentSelect(ActionEvent event) {
		errorLabel.setVisible(false);
		payNow.setDisable(false);
		if (paymentCombo.getValue().equals("Credit Card")) {
			setCreditCardPayment();
			return;
		}
		setMonthlyPayment();
	}

	/**
	 * This method is called when the user pressed the return button, the method
	 * will transfer the user to previous window
	 * 
	 * @param event ActionEvent of the click on the return button
	 */
	@FXML
	void backPressed(ActionEvent event) {
		orderTimerEntityController.getOrderTimer().remove(this);
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/CheckoutScreen.fxml");
	}

	/**
	 * This method is called when the user pressed the logout button, the method
	 * perform a logout and change to the login screen
	 * 
	 * @param event ActionEvent of the click on the logout button
	 */
	@FXML
	void logoutPressed(ActionEvent event) {
		if (orderEntityController.getOrder().getSupplyMethod() != SupplyMethod.Shipment) {
			cancelOrder();
		}
		orderEntityController.setOrder(null);
		orderTimerEntityController.getOrderTimer().stop();
		orderTimerEntityController.getOrderTimer().remove(this);
		orderTimerEntityController.setOrderTimer(null);
		commonActionsController.logOutPressed(event);
	}

	/**
	 * This method is called when the pay button is pressed, simulate a payment and
	 * save the order and details in the server (saves the information for each
	 * order type and request the server to save it, wait for response with the
	 * order number to display, and displays it
	 * 
	 * @param event ActionEvent of the pay button pressed
	 */
	@FXML
	void payPressed(ActionEvent event) {

		Hashtable<String, Object> orderTable = new Hashtable<>();

		switch (orderEntityController.getOrder().getSupplyMethod().toString()) {
		case "Shipment":
			orderTable.clear();
			// Shipment Details
			Shipment shipment = new Shipment();
			shipment = (Shipment) orderEntityController.getOrder();
			// Shipment Details
			orderTable.put("getSupplyMethod", shipment.getSupplyMethod().toString());
			orderTable.put("getOrderProductList", shipment.getOrderProductList());
			orderTable.put("getTotalPrice", shipment.getTotalPrice());
			orderTable.put("getDeliveryAddress", shipment.getDeliveryAddress());
			orderTable.put("getShippmentCountry", shipment.getShippmentCountry().toString());
			break;
		case "LocalOrder":
			orderTable.clear();
			orderTable.put("getTotalPrice", orderEntityController.getOrder().getTotalPrice());
			orderTable.put("getFacilityId", orderEntityController.getOrder().getFacilityId());
			orderTable.put("getOrderProductList", orderEntityController.getOrder().getOrderProductList());
			orderTable.put("getSupplyMethod", orderEntityController.getOrder().getSupplyMethod().toString());
			break;
		case "SelfCollection":
			orderTable.clear();
			SelfCollection selfCollection = new SelfCollection();
			selfCollection = (SelfCollection) orderEntityController.getOrder();
			// SelfCollection Details
			orderTable.put("getEstimatePickUpDate", selfCollection.getEstimatePickUpDate());
			orderTable.put("getOrderFacilityId", orderEntityController.getOrder().getFacilityId());
			orderTable.put("getSupplyMethod", selfCollection.getSupplyMethod().toString());
			orderTable.put("getOrderProductList", selfCollection.getOrderProductList());
			orderTable.put("getTotalPrice", selfCollection.getTotalPrice());
			break;
		default:
			break;
		}
		orderTable.put("payment", paymentCombo.getValue());
		orderTable.put("user", user);
		RegisteredCustomer user = (RegisteredCustomer) orderTable.get("user");
		orderTable.put("payment", paymentCombo.getValue());
		ServerMessage msg = new ServerMessage(Instruction.Make_An_Order, orderTable);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {	}	;// waiting for sever response
		if(user instanceof Subscriber) {
			if(paymentCombo.getValue().equals("Monthly Payment")) {
				((Subscriber) user).setDebtAmount(	((Subscriber) user).getDebtAmount()+orderEntityController.getOrder().getTotalPrice());
			}
			((Subscriber) user).setFirstOrder(false);
		}
		commonActionsController.popUp(new Stage(), "New Order " + "#" + orderEntityController.getOrder().getOrderNum(),
				"New Order " + "#" + orderEntityController.getOrder().getOrderNum() + " was created successfuly\n"
						+ "The EKrut System sent a confirmation message to the \nEmail " + user.getEmail()
						+ "and SMS to phone number " + user.getTelephone() + ".",
				"/ReportPhotos/email.gif", null, null);
		// orderEntityController.setOrder(null);
		orderTimerEntityController.getOrderTimer().remove(this);
		orderTimerEntityController.getOrderTimer().stop();
		orderTimerEntityController.setOrderTimer(null);
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/" + user.getRole() + "Screen.fxml");
	}

	/**
	 * This method gets called when the x button is pressed and logout and quit the
	 * program
	 * 
	 * @param event ActionEvent of pressing the x button
	 */
	@FXML
	void xBtnPressed(ActionEvent event) {
		if (orderEntityController.getOrder().getSupplyMethod() != SupplyMethod.Shipment) {
			cancelOrder();
		}
		commonActionsController.xPressed(event);
	}

	/**
	 * This method cancels the order, send a request to the server to return
	 * products to the inventory.
	 */
	private void cancelOrder() {
		Hashtable<String, Object> productOrdertable = new Hashtable<>();
		productOrdertable.put("products", orderEntityController.getOrder().getOrderProductList());
		productOrdertable.put("facilityID", orderEntityController.getOrder().getFacilityId());
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Add_Order_To_Invetory, productOrdertable));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		} // waiting for sever response
	}

	/**
	 * This method hid the monthly payment labels and display the credit cart labels
	 */
	private void setCreditCardPayment() {
		depthName.setVisible(false);
		depthValue.setVisible(false);
		creditName.setVisible(true);
		creditNumber.setVisible(true);
		expName.setVisible(true);
		expValue.setVisible(true);
		cvvName.setVisible(true);
		cvvValue.setVisible(true);
	}

	/**
	 * This method hide he credit cart labels and display the monthly payment labels
	 */
	private void setMonthlyPayment() {
		depthName.setVisible(true);
		depthValue.setVisible(true);
		creditName.setVisible(false);
		creditNumber.setVisible(false);
		expName.setVisible(false);
		expValue.setVisible(false);
		cvvName.setVisible(false);
		cvvValue.setVisible(false);
	}

	/**
	 * This method hide all labels of the monthly and credit card payment
	 */
	private void hideAll() {
		depthName.setVisible(false);
		depthValue.setVisible(false);
		creditName.setVisible(false);
		creditNumber.setVisible(false);
		expName.setVisible(false);
		expValue.setVisible(false);
		cvvName.setVisible(false);
		cvvValue.setVisible(false);
	}

	/**
	 * update method from observer interface gets called from timer which not run on
	 * main thread there for cannot change the UI (runLater waiting and run the
	 * command on main thread) displaying the timer and calling the timeEndOrder
	 * when time is up
	 */
	@Override
	public void update(Object data) {
		if (((String) data).startsWith("-")) {
			Platform.runLater(() -> timeEndOrder());
		}
		Platform.runLater(() -> {
			timer.setText((String) data);
		});
	}

	/**
	 * This method handles the force close and cancel of the order when the order
	 * timer is up
	 */
	private void timeEndOrder() {
		orderTimerEntityController.getOrderTimer().stop();
		orderTimerEntityController.setOrderTimer(null);
		timer.getScene().getWindow().hide();
		ChangeWindow changeWindow = new ChangeWindow();
		changeWindow.changeScreen(new Stage(),
				"/clientGUIScreens/" + (new UserController()).getUser().getRole() + "Screen.fxml");
		commonActionsController.popUp(new Stage(), "Out of Time!",
				"Each order needs to be completed within 15 minutes, you have passed the 15 minutes, the order is canceled and you return to the home screen.",
				"/myOrderScreenPic/stopwatch.gif", null, null);
	}
	

}
