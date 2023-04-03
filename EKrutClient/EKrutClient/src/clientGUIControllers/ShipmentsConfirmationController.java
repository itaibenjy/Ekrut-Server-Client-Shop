package clientGUIControllers;


import java.time.LocalDateTime;
import java.sql.Date;
import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Country;
import Enum.Instruction;
import Enum.ProccessStatus;
import common.CommonActionsController;
import entities.Order;
import entities.ServerMessage;
import entities.Shipment;
import entities.User;
import entityControllers.UserController;
import entityControllers.UserOrdersEntityController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Shipments confirmation controller for area deliveries employee to confirm all of the shipment in his area
 * that in status "Waiting for confirmation" to start their shipment process.
 * */
public class ShipmentsConfirmationController {

	/*----FXML Attributes----*/
    @FXML
    private Button confirmBtn;
    
    @FXML
    private Button helpBtn;
    
    @FXML
    private TableColumn<Shipment, String> deliveryAddress;

    @FXML
    private TableColumn<Shipment, Integer> orderNum;

    @FXML
    private ComboBox<Integer> orders;

    @FXML
    private TableColumn<Shipment, Float> price;
    
    @FXML
    private TableColumn<Shipment, Country> deliveryCountry;

    @FXML
    private TableColumn<Shipment, String> customerName;

    @FXML
    private ComboBox<String> status;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label selectOrderLabel;
    
    @FXML
    private Label help2Label;

    @FXML
    private Label helpLabel;
    
    @FXML
    private ImageView deliveries;
    
    @FXML
    private ImageView logoIMG;

    @FXML
    private TableView<Shipment> table;
    
    /* -----Entities And Controllers----- */
    private UserController userController =new UserController();
	private CommonActionsController commonActionsController = new CommonActionsController();
	private UserOrdersEntityController userOrdersEntityController = new UserOrdersEntityController();
	private ObservableList<Shipment> tableData = FXCollections.observableArrayList();
	private ObservableList<String> shipmentStatus =  FXCollections.observableArrayList("Waiting For Confirmation","In Proccess");
	private static User userForSMSAndEmail = null;
	private  static String customerArea;
	private Integer chosenOrder;
    
    /**
	 * The initialize method is used to set visibility of the buttons ,text fields, combo box, images and labels.
	 * In addition, this method gets all of the shipments that in status "Waiting for confirmation" 
	 * and from the same area of the area deliveries operator area.
	 * and displays a small window showing how many shipments it has to approve.
	 **/
	@FXML
	public void initialize() {
		logoIMG.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		deliveries.setImage(new Image("/manageOrders/deliverylist.gif"));
		setLabels();
		Hashtable<String,Object> data = new Hashtable<>();
		data.put("userArea",userController.getUser().getArea().toString());
		ServerMessage msg = new ServerMessage(Instruction.Get_shipments_of_area,data);
		EkrutClientUI.chat.accept(msg);
		while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
		if(userOrdersEntityController.getShipmentList().size() == 0) {
			selectOrderLabel.setText("There are no new shipments that you need to confirm !!");
			selectOrderLabel.setVisible(true);
		}
		else {
			commonActionsController.popUp(new Stage(),"New shipments to confirm", "You have "+userOrdersEntityController.getShipmentList().size()+" shipments to confirm!","/manageOrders/list.gif",null,null);
			selectOrderLabel.setVisible(true);
			orders.setVisible(true);	
			ObservableList<Integer> ordersNumbers =  FXCollections.observableArrayList();
			for(int i=0; i<userOrdersEntityController.getShipmentList().size(); i++) {
				ordersNumbers.add(userOrdersEntityController.getShipmentList().get(i).getOrderNum());
			}
			orders.setItems(ordersNumbers);
		}
	}
	
	
	/**
	 * @param event An order is selected from the orders combo box.
	 * According to the selected order, 
	 * the method will show the deliveries operator employee all the details about it and the content of the order, 
	 * and show to the worker a combo box for confirm the order.
	 * */
    @FXML
    void selectOrder(ActionEvent event) {
    	tableData.clear();
    	chosenOrder = orders.getValue();
    	Shipment s =  (Shipment) findOrder(chosenOrder);
    	tableData.add(s);
    	initTableView();
    	table.setVisible(true);
		status.setValue("Waiting For Confirmation");
		statusLabel.setVisible(true);
		status.setVisible(true);
		confirmBtn.setVisible(true);
    	
    }
    
    /**
     * @param event An status is selected from the status combo box.
     * This method will cause the confirm button to disable according to the selected status
     * just when the worker will choose "In Proccess" status he can click on confirm.
     * */
    @FXML
    void selectStatus(ActionEvent event) {
    	if(status.getValue()=="Waiting For Confirmation") {
    		confirmBtn.setDisable(true);
    	}
    	if(status.getValue()=="In Proccess") {
    		confirmBtn.setDisable(false);
    	}
    	
    }
    
    /**
     * @param event Confirm button pressed,
     * When the worker clicks on the confirm button, 
     * the method will update the status of the order and the calculate the estimate delivery date of the order,
     * In addition, a small screen will pop up that informs that the delivery process has been started successfully
     * and sent e-mail and SMS to the customer.
     * */
    @FXML
    void confirmPressed(ActionEvent event) {
    	Shipment s =  (Shipment) findOrder(chosenOrder);
    	s.setShippmentOrderStatus(ProccessStatus.InProccess);
    	Hashtable<String,Object> data = new Hashtable<>();
		data.put("shipmentToUpdate",s);
		data.put("userForEmailAndSMS", s.getUserName());
		ServerMessage msg = new ServerMessage(Instruction.Update_shipment_status,data);
    	EkrutClientUI.chat.accept(msg);
    	while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
    	calculateDeliveryTime(s);
    	ServerMessage msg1 = new ServerMessage(Instruction.Get_user_details_for_send_email_and_sms,data);
    	EkrutClientUI.chat.accept(msg1);
    	while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
    	commonActionsController.popUp(new Stage(),"Order confirmed !", "Order number: "+chosenOrder+" has been confirmed and started the shipping process,"
    			+ " the estimated delivery date is:  "+s.getEstimateDeliveryDate()+" . \nA message was sent to:  "+getUserForSMSAndEmail().getFirstName()+" "+getUserForSMSAndEmail().getLastName()
    			+ " .\nto email:  "+getUserForSMSAndEmail().getEmail()+" .\nand SMS to:  "+getUserForSMSAndEmail().getTelephone()+" .","/manageOrders/message.gif","/clientGUIScreens/ShipmentsConfirmationScreen.fxml",event);
    	
    }
    
    /**
     * @param event Help button pressed 
     * the method will show the help label and set the help button to disable.
     * */
    @FXML
    void helpPressed(ActionEvent event) {
    	helpLabel.setVisible(true);
    	help2Label.setVisible(true);
    	helpBtn.setVisible(false);
    	
    }
    
    /**
     * @param event Back button pressed, and the method will take us back to the area deliveries operator home page.
     * */
    @FXML
    void backPressed(ActionEvent event) {
    	commonActionsController.backOrNextPressed(event, "/clientGUIScreens/AreaDeliveriesOperatorScreen.fxml");
    }

    /**
	 * @param event log Out Button Pressed 
	 * log out the user. 
	 */
    @FXML
    void logOutPressed(ActionEvent event) {
    	commonActionsController.logOutPressed(event);
    }

    /**
     * @param event X button pressed.
     * close the program and log out the user.
     * */
    @FXML
    void xPressed(ActionEvent event) {
    	commonActionsController.xPressed(event);
    }
    
    /**
	 * Finds an Order object based on the order number.
	 * @param orderNum The order number to search for.
	 * @return An Order object that matches the given order number, or null if no match is found.
	 */
    private Order findOrder(Integer orderNum) {
    	for(int i=0; i<userOrdersEntityController.getShipmentList().size(); i++) {
    		if(userOrdersEntityController.getShipmentList().get(i).getOrderNum()==orderNum)
    			return userOrdersEntityController.getShipmentList().get(i);
    	}
    	return null;
    }
    
    /**
     * This method set labels for the initialize screen.
     * */
    private void setLabels() {
		table.setVisible(false);
		statusLabel.setVisible(false);
		status.setVisible(false);
		confirmBtn.setVisible(false);   
		confirmBtn.setDisable(true); 
		status.setItems(shipmentStatus);
		selectOrderLabel.setVisible(false);
		orders.setVisible(false);
    }
    
    /**
     * method to set the wanted order's details into the table.
     * */
	private void initTableView() {
		orderNum.setCellValueFactory(new PropertyValueFactory<Shipment, Integer>("orderNum"));
		customerName.setCellValueFactory(new PropertyValueFactory<Shipment,String>("userName"));
		deliveryAddress.setCellValueFactory(new PropertyValueFactory<Shipment, String>("deliveryAddress"));
		deliveryCountry.setCellValueFactory(new PropertyValueFactory<Shipment,Country>("shippmentCountry"));
		price.setCellValueFactory(new PropertyValueFactory<Shipment,Float>("totalPrice"));
		table.setItems(tableData);
	}
	
	/**
	 * @param s - Shipment that confirmed 
	 * This method will calculate the estimate delivery date of the shipment
	 *  considering the country to which the shipment is made.
	 * */
	private void calculateDeliveryTime(Shipment s) {
    	Hashtable<String,Object> data = new Hashtable<>();
		data.put("customerAreaToShipment",s.getUserName());
		ServerMessage msg = new ServerMessage(Instruction.Calculate_delivery_time,data);
    	EkrutClientUI.chat.accept(msg);
    	while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
    	LocalDateTime currentDate = LocalDateTime.now();
    	LocalDateTime estimateDate;
    	Date estimateDeliveryDate;
    	if(s.getShippmentCountry() == Country.ISRAEL) {
    		if(customerArea!="UAE") {
    			estimateDate = currentDate.plusWeeks(1);
    			estimateDeliveryDate = localDateToDate(estimateDate);
    			s.setEstimateDeliveryDate(estimateDeliveryDate);
    		}
    		else {
    			estimateDate = currentDate.plusWeeks(2);
    			estimateDeliveryDate = localDateToDate(estimateDate);
    			s.setEstimateDeliveryDate(estimateDeliveryDate);
    		}
    	}
    	else {
    		if(customerArea=="UAE") {
    			estimateDate = currentDate.plusWeeks(1);
    			estimateDeliveryDate = localDateToDate(estimateDate);
    			s.setEstimateDeliveryDate(estimateDeliveryDate);
    		}
    		else {
    			estimateDate = currentDate.plusWeeks(2);
    			estimateDeliveryDate = localDateToDate(estimateDate);
    			s.setEstimateDeliveryDate(estimateDeliveryDate);
    		}
    	}
    	updateEstimateDate(estimateDeliveryDate);
	}
	
	/**
	 * @param estimateDeliveryDate from type Date and go to the DB to update the delivery date.
	 * */
	private void updateEstimateDate(Date estimateDeliveryDate) {
		Hashtable<String,Object> data = new Hashtable<>();
		data.put("estimateDateForShipment",estimateDeliveryDate);
		data.put("orderNumForUpdateEstimateDate", chosenOrder);
		ServerMessage msg = new ServerMessage(Instruction.Update_shipment_estimate_date,data);
    	EkrutClientUI.chat.accept(msg);
    	while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
	}

	 /**
     * Converts a LocalDateTime object to a Date object.
     * @param date The LocalDateTime object to convert.
     * @return A Date object that represents the same date and time as the input LocalDateTime object.
     */
	private Date localDateToDate(LocalDateTime date) {
		Date newDate = Date.valueOf(date.toLocalDate()) ;
		return newDate;
	}
	
	/**
	 * Get method for get the customer area for calculating the delivery date to him.
	 * @return String object of the customer area for calculating the delivery date to him.
	 * */
	public static String getCustomerArea() {
		return customerArea;
	}

	/**
	 * Set method for set customer area when we got this information from DB.
	 * */
	public static void setCustomerArea(String customerArea) {
		ShipmentsConfirmationController.customerArea = customerArea;
	}
	
	/**
	 * Get method for get the customer that we want to send him SMS and E-mail.
	 * @return User object of the user that we want to send him SMS and E-mail.
	 * */
	public static User getUserForSMSAndEmail() {
		return userForSMSAndEmail;
	}

	/**
	 * Set method to set the customer when we go him from DB.
	 * */
	public static void setUserForSMSAndEmail(User userForSMSAndEmail) {
		ShipmentsConfirmationController.userForSMSAndEmail = userForSMSAndEmail;
	}

}
