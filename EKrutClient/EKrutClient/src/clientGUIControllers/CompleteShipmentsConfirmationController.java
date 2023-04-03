package clientGUIControllers;

import java.text.DecimalFormat;
import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Country;
import Enum.Instruction;
import Enum.ProccessStatus;
import common.CommonActionsController;
import entities.Order;
import entities.ServerMessage;
import entities.Shipment;
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
 *
 * Complete shipments confirmation controller for area deliveries employee to confirm all of the shipment in his area
 * that received by the customer and in status "In Proccess" and change them to "Done" for complete the process.
 * */
public class CompleteShipmentsConfirmationController {
	
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
    private TableColumn<Shipment, Country> deliveryCountry;

    @FXML
    private TableColumn<Shipment, String> customerUsername;
    
    @FXML
    private TableColumn<Shipment, Float> price;

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
	ObservableList<String> shipmentStatus =  FXCollections.observableArrayList("In Proccess","Done");
	DecimalFormat format = new DecimalFormat("0.#");
	private Integer chosenOrder;
    
	/**
	 * The initialize method is used to set visibility of the buttons ,text fields, combo box, images and labels.
	 * In addition, this method gets all of the shipments that received by the customer and in status "In Proccess" 
	 * and from the same area of the area deliveries operator area.
	 * and displays a small window showing how many shipments it has to approve.
	 **/
	@FXML
	public void initialize() {
		logoIMG.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		deliveries.setImage(new Image("/manageOrders/deliverylist.gif"));
		setLabels();
		Hashtable<String,Object> data = new Hashtable<>();
		data.put("recivedOrderUserArea",userController.getUser().getArea().toString());
		ServerMessage msg = new ServerMessage(Instruction.Get_recived_shipments_for_confirmation,data);
		EkrutClientUI.chat.accept(msg);
		while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
		if(userOrdersEntityController.getShipmentList().size() == 0) {
			selectOrderLabel.setText("There are no new shipments that you need to complete !!");
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
		status.setValue("In Proccess");
		statusLabel.setVisible(true);
		status.setVisible(true);
		confirmBtn.setVisible(true);
    	
    }

    /**
     * @param event An status is selected from the status combo box.
     * This method will cause the confirm button to disable according to the selected status
     * just when the worker will choose "Done" status he can click on confirm.
     * */
    @FXML
    void selectStatus(ActionEvent event) {
    	if(status.getValue()=="Done") {
    		confirmBtn.setDisable(false);
    	}
    	if(status.getValue()=="In Proccess") {
    		confirmBtn.setDisable(true);
    	}
    	
    }
    
    /**
     * @param event Confirm button pressed,
     * When the worker clicks on the confirm button, 
     * the method will update the status of the order,
     * In addition, a small screen will pop up that informs that the delivery process has been finished successfully.
     * */
    @FXML
    void confirmPressed(ActionEvent event) {
    	Shipment s =  (Shipment) findOrder(chosenOrder);
    	s.setShippmentOrderStatus(ProccessStatus.Done);
    	Hashtable<String,Object> data = new Hashtable<>();
		data.put("shipmentToUpdate",s);
		ServerMessage msg = new ServerMessage(Instruction.Update_shipment_status,data);
    	EkrutClientUI.chat.accept(msg);
    	commonActionsController.popUp(new Stage(),"Order complete !", "The order process for order number: "+chosenOrder+" has been completed successfully!","/manageOrders/verified.gif","/clientGUIScreens/CompleteShipmentsConfirmationScreen.fxml",event);
    	
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
     * method to set the wanted order's details into the table.
     * */
	private void initTableView() {
		orderNum.setCellValueFactory(new PropertyValueFactory<Shipment, Integer>("orderNum"));
		customerUsername.setCellValueFactory(new PropertyValueFactory<Shipment,String>("userName"));
		deliveryAddress.setCellValueFactory(new PropertyValueFactory<Shipment, String>("deliveryAddress"));
		deliveryCountry.setCellValueFactory(new PropertyValueFactory<Shipment,Country>("shippmentCountry"));
		price.setCellValueFactory(new PropertyValueFactory<Shipment,Float>("totalPrice"));
		table.setItems(tableData);
	}

}
