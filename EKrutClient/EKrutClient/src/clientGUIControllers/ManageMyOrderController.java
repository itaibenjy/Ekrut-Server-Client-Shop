package clientGUIControllers;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
//import java.util.Date;
import java.util.Hashtable;
import java.util.Set;
import Client.EkrutClientUI;
import Enum.CustomerShippmentStatus;
import Enum.Instruction;
import common.CommonActionsController;
import entities.Order;
import entities.Product;
import entities.ProductInUserOrder;
import entities.SelfCollection;
import entities.ServerMessage;
import entities.Shipment;
import entityControllers.UserController;
import entityControllers.UserOrdersEntityController;
import entityControllers.WorkerEntityController;
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
 * Manage my order controller for see details about all user's orders history and confirmation receipt of shipments.
 * This class created to allow each user to see his order history,
 * all the details on each of these orders, the order status (if he received it or not).
 * In addition, on this screen, in cases of orders made by deliveries,
 * the user will be able to indicate that he received the shipment.
 * 
 * */

public class ManageMyOrderController {

	/*----FXML Attributes----*/
    @FXML
    private Button backBtn;

    @FXML
    private Button helpBtn;

    @FXML
    private Label helpLabel;
    
    @FXML
    private TableColumn<ProductInUserOrder, String> c1;

    @FXML
    private TableColumn<ProductInUserOrder, Integer> c2;

    @FXML
    private TableColumn<ProductInUserOrder, String> c3;
    
    @FXML
    private Button logOutBtn;

    @FXML
    private ComboBox<Integer> orderBox;

    @FXML
    private Label orderDate;

    @FXML
    private Label orderStatus;

    @FXML
    private TableView<ProductInUserOrder> orderTable;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private Label supplyMethod;

    @FXML
    private Label supplyAddress;
    
    @FXML
    private Label totalPrice;

    @FXML
    private Button updateBtn;

    @FXML
    private Label dateLabel;
    
    @FXML
    private Label priceLabel;
    
    @FXML
    private Label sAddress;

    @FXML
    private Label sMethod;

    @FXML
    private Label estimateDate;

    @FXML
    private Label estimateDateHead;
    
    @FXML
    private Label chooseOrderLabel;

    @FXML
    private Label waitingLabel;
    
    @FXML
    private ImageView manageOrdersIMG;
    
    @FXML
    private ImageView statusIMG;
    
    @FXML
    private ImageView logoIMG;
    
    @FXML
    private Button xBtn;
    
    /* -----Entities And Controllers----- */
    private UserController userController = new UserController();
    private WorkerEntityController workerEntityController=new WorkerEntityController();
    private CommonActionsController commonActionsController = new CommonActionsController();
    private UserOrdersEntityController userOrdersEntityController = new UserOrdersEntityController();
    private ObservableList<ProductInUserOrder> tableData = FXCollections.observableArrayList();
    DecimalFormat format = new DecimalFormat("0.#");
    private Integer selectedOrder;

    
    /**
	 * The initialize method is used to set visibility of the buttons ,text fields, combo box, images and labels.
	 * In addition, this method gets all of the user's orders and insert their order numbers to the combo box.
	 */
	@FXML
	public void initialize() {
		logoIMG.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		manageOrdersIMG.setImage(new Image("/manageOrders/orders.png"));
		setLabels();
    	Hashtable<String,Object> data = new Hashtable<>();
		data.put("username",userController.getUser().getUserName());
    	EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_user_orders,data));
    	while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
    	ObservableList<Integer> orders =  FXCollections.observableArrayList();
    	for(int i=0; i<userOrdersEntityController.getOrdersList().size(); i++) {
    		orders.add(userOrdersEntityController.getOrdersList().get(i).getOrderNum());
    	}
    	for(int i=0; i<userOrdersEntityController.getShipmentList().size(); i++) {
    		orders.add(userOrdersEntityController.getShipmentList().get(i).getOrderNum());
    	}
    	for(int i=0; i<userOrdersEntityController.getSelfCollectionList().size(); i++) {
    		orders.add(userOrdersEntityController.getSelfCollectionList().get(i).getOrderNum());
    	}
    	if(orders.size()== 0) {
    		orderBox.setVisible(false);
    		chooseOrderLabel.setText("You have not made previous orders.");
    	}
    	orderBox.setItems(orders);
    }

	/**
	 * @param event An order is selected from the orders combo box.
	 * According to the selected order, 
	 * the method will show the user all the details about it and the content of the order, 
	 * and only if it is a delivery type order that has been approved by the operations
	 *  employee and has not yet reached the customer, the customer will be able to confirm its receipt.
	 *  
	 * */
    @FXML
    void selectOrder(ActionEvent event) {
    	tableData.clear();
    	Integer chosenOrder = orderBox.getValue();
    	selectedOrder=chosenOrder;
    	Order order =  findOrder(chosenOrder);
    	Set<Product> products = order.getOrderProductList1().keySet();
    	for (int i=0; i<products.size(); i++) {
    		Product[] productArr = products.toArray(new Product[i]);
    		String productName = productArr[i].getProtductName();
    		Integer quantity = order.getOrderProductList1().get(productArr[i]);
    		Float price = productArr[i].getProductPrice() * order.getOrderProductList1().get(productArr[i]);
    		ProductInUserOrder p= new ProductInUserOrder(productName,quantity,format.format(price));
    		tableData.add(p);
    	}
    	initTableView();
    	orderTable.setVisible(true);
    	statusIMG.setVisible(true);
    	dateLabel.setText(String.format("%s", order.getOrderDate().toString()));
    	supplyMethod.setVisible(true);
    	orderDate.setVisible(true);
    	dateLabel.setVisible(true);
    	orderStatus.setVisible(true);
    	statusCombo.setValue("Not Received");
    	sMethod.setVisible(true);
    	sAddress.setVisible(true);
    	updateBtn.setVisible(false);
    	waitingLabel.setVisible(false);
    	if(order.getSupplyMethod().toString().equals("Shipment")) {
    		Shipment ship = (Shipment) order;
			sMethod.setText("Shipment");
    		sAddress.setText(String.format("%s",ship.getDeliveryAddress()));
    		supplyAddress.setText("Delivery address:");
    		supplyAddress.setVisible(true);
    		totalPrice.setVisible(true);
    		priceLabel.setText(String.format("%s ₪", format.format(ship.getTotalPrice())));
    		priceLabel.setVisible(true);
    		if(ship.getShippmentOrderStatus().toString().equals("WaitingForConfirmation")) {
    			waitingLabel.setText("Your order is awaiting confirmation and has not yet started the shipping process.");
        		waitingLabel.setVisible(true);
    			estimateDateHead.setVisible(false);
        		estimateDate.setVisible(false);
        		statusCombo.setValue("Not Received");
        		statusCombo.setDisable(true);
        		statusCombo.setVisible(true);
        		statusIMG.setImage(new Image("/manageOrders/incomplete.gif"));
    		}
    		else if(ship.getShippmentOrderStatus().toString().equals("InProccess") && ship.getCustomerShippmentStatus().toString().equals("NotRecived")) {
    			ObservableList<String> orderStauts =  FXCollections.observableArrayList("Not Received","Received");
    			estimateDateHead.setText("Estimate delivery date:");
    			estimateDateHead.setVisible(true);
        		estimateDate.setText(String.format("%s",ship.getEstimateDeliveryDate().toString()));
        		estimateDate.setVisible(true);
        		statusCombo.setItems(orderStauts);
        		statusCombo.setDisable(false);
        		statusCombo.setVisible(true);
        		statusCombo.setValue("Not Received");
        		updateBtn.setVisible(true);
        		statusIMG.setVisible(false);
    		}
    		else {
        		estimateDateHead.setVisible(true);
        		estimateDate.setVisible(true);
        		estimateDateHead.setText("The shipment delivered at:");
        		estimateDate.setText(String.format("%s", ship.getEstimateDeliveryDate()));
        		statusCombo.setValue("Received");
        		statusCombo.setDisable(true);
        		statusCombo.setVisible(true);
        		updateBtn.setVisible(false);
        		statusIMG.setImage(new Image("/manageOrders/complete.gif"));
    		}
    	}
    	else if(order.getSupplyMethod().toString().equals("SelfCollection")) {
    		SelfCollection self = (SelfCollection) order;
    		sMethod.setText("Self collection");
    		sAddress.setText(String.format("%s",order.getFacilityId()));
    		supplyAddress.setText("FacilityID:");
    		supplyAddress.setVisible(true);
			totalPrice.setVisible(true);
			priceLabel.setText(String.format("%s ₪", format.format(self.getTotalPrice())));
			priceLabel.setVisible(true);
    		if(self.getActualPickUpDate()==null) {
				estimateDate.setText(String.format("%s", self.getEstimatePickUpDate()));
				estimateDateHead.setText("Estimate pick up date:");
				estimateDate.setVisible(true);
				estimateDateHead.setVisible(true);
        		statusCombo.setValue("Not Received");
            	statusCombo.setVisible(true);
            	statusCombo.setDisable(true);
            	updateBtn.setVisible(false);
            	statusIMG.setImage(new Image("/manageOrders/incomplete.gif"));
    		}
    		else {
				estimateDate.setText(String.format("%s", self.getActualPickUpDate()));
				estimateDateHead.setText("Picked up at:");
				estimateDate.setVisible(true);
				estimateDateHead.setVisible(true);
            	statusCombo.setValue("Received");
            	statusCombo.setVisible(true);
            	statusCombo.setDisable(true);
            	updateBtn.setVisible(false);
            	statusIMG.setImage(new Image("/manageOrders/complete.gif"));
    		}
    	}
    	else if(order.getSupplyMethod().toString().equals("LocalOrder")) {
    		sMethod.setText("Local order");
    		sAddress.setText(String.format("%s",order.getFacilityId()));
    		supplyAddress.setText("FacilityID:");
    		supplyAddress.setVisible(true);
    		priceLabel.setText(String.format("%s ₪", format.format(order.getTotalPrice())));
    		estimateDate.setVisible(false);
			estimateDateHead.setVisible(false);
        	statusCombo.setValue("Received");
        	statusCombo.setDisable(true);
        	statusCombo.setVisible(true);
        	priceLabel.setVisible(true);
        	totalPrice.setVisible(true);
        	updateBtn.setVisible(false);
        	statusIMG.setImage(new Image("/manageOrders/complete.gif"));
    	}    
    }
    
    /**
     * @param event Help button pressed 
     * the method will show the help label and set the help button to disable.
     * */
    @FXML
    void helpPressed(ActionEvent event) {
    	helpLabel.setVisible(true);
    	helpBtn.setVisible(false);
    	
    }
    
    /**
     * @param event Update button pressed,
     * When the customer clicks on the update button, 
     * the method will update the status of the order and the date of receipt of the order,
     * In addition, a small screen will pop up that informs that the delivery process has been completed successfully.
     * */
    @FXML
    void updatePressed(ActionEvent event) {
    	Order o =  findOrder(selectedOrder);
    	if(o.getSupplyMethod().toString().equals("Shipment")) {
    		Shipment ship = (Shipment) o;
    		ship.setCustomerShippmentStatus(CustomerShippmentStatus.Received);
    		LocalDateTime currentDate = LocalDateTime.now();
    		Date receivedDate = localDateToDate(currentDate);
    		if(ship.getEstimateDeliveryDate().after(receivedDate)) {
    			ship.setEstimateDeliveryDate(receivedDate);
    			updateEstimateDate(receivedDate);
    		}
    		Hashtable<String,Object> data = new Hashtable<>();
    		data.put("orderToUpdate",ship);
        	EkrutClientUI.chat.accept(new ServerMessage(Instruction.Update_order,data));
        	commonActionsController.popUp(new Stage(),"Order received !", "Your order number: "+selectedOrder+" has arrived successfully,\n"
        			+ "Thank you for shopping with us.","/manageOrders/verified.gif","/clientGUIScreens/ManageMyOrderScreen.fxml",event);
    	}
    }
    
    /**
     * @param event An status is selected from the status combo box.
     * This method will cause the update button to disable according to the selected status
     * just when the customer will choose "Received" status he can click on update.
     * */
    @FXML
    void selectStatus(ActionEvent event) {
    	if(statusCombo.getValue()=="Received") {
    		updateBtn.setDisable(false);
    	}
    	if(statusCombo.getValue()=="Not Received") {
    		updateBtn.setDisable(true);
    	}
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
     * @param event Back button pressed.
     * This method will take us to the appropriate screen according to the type of customer.
     * */
    @FXML
    void backPressed(ActionEvent event) {
		if(workerEntityController.getSecondRole() == null) {
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/"+userController.getUser().getRole()+"Screen.fxml");
		}
		else {
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/"+workerEntityController.getSecondRole()+"Screen.fxml");
		}
		
    }
    
    /**
	 * @param event log Out Button Pressed 
	 * log out the user. 
	 */
    @FXML
    void logOutPressed(ActionEvent event) {
    	workerEntityController.setSecondRole(null);
    	commonActionsController.logOutPressed(event);
    }
    
    /**
     * method to set the wanted order's details into the table.
     * */
	private void initTableView() {
		c1.setCellValueFactory(new PropertyValueFactory<ProductInUserOrder, String>("productName"));
		c2.setCellValueFactory(new PropertyValueFactory<ProductInUserOrder, Integer>("quantity"));
		c3.setCellValueFactory(new PropertyValueFactory<ProductInUserOrder, String>("price"));
		orderTable.setItems(tableData);
	}
	
	/**
	 * Finds an Order object based on the order number.
	 * @param orderNum The order number to search for.
	 * @return An Order object that matches the given order number, or null if no match is found.
	 */
    private Order findOrder(Integer orderNum) {
    	for(int i=0; i<userOrdersEntityController.getOrdersList().size(); i++) {
    		if(userOrdersEntityController.getOrdersList().get(i).getOrderNum()==orderNum)
    			return userOrdersEntityController.getOrdersList().get(i);
    	}
    	for(int i=0; i<userOrdersEntityController.getShipmentList().size(); i++) {
    		if(userOrdersEntityController.getShipmentList().get(i).getOrderNum()==orderNum)
    			return userOrdersEntityController.getShipmentList().get(i);
    	}
    	for(int i=0; i<userOrdersEntityController.getSelfCollectionList().size(); i++) {
    		if(userOrdersEntityController.getSelfCollectionList().get(i).getOrderNum()==orderNum)
    			return userOrdersEntityController.getSelfCollectionList().get(i);
    	}
    	return null;
    }


    /**
     * This method set labels for the initialize screen.
     * */
    private void setLabels() {
    	helpLabel.setVisible(false);
		orderTable.setVisible(false);
    	statusCombo.setVisible(false);
    	supplyMethod.setVisible(false);
    	updateBtn.setVisible(false);
    	totalPrice.setVisible(false);
    	orderDate.setVisible(false);
    	supplyAddress.setVisible(false);
    	orderStatus.setVisible(false);
    	sMethod.setVisible(false);
    	dateLabel.setVisible(false);
    	sAddress.setVisible(false);
    	priceLabel.setVisible(false);
    	estimateDate.setVisible(false);
    	estimateDateHead.setVisible(false);
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
	 * @param estimateDeliveryDate from type Date and go to the DB to update the delivery date.
	 * */
	private void updateEstimateDate(Date estimateDeliveryDate) {
		Hashtable<String,Object> data = new Hashtable<>();
		data.put("estimateDateForShipment",estimateDeliveryDate);
		data.put("orderNumForUpdateEstimateDate", selectedOrder);
		ServerMessage msg = new ServerMessage(Instruction.Update_shipment_estimate_date,data);
    	EkrutClientUI.chat.accept(msg);
    	while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
	}

}
