package Client;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import Enum.Role;
import clientGUIControllers.ActivateSalesController;
import clientGUIControllers.ClientConnectScreenController;
import clientGUIControllers.ConfirmRegistretionRequestController;
import clientGUIControllers.LoginController;
import clientGUIControllers.MakeRegistrationController;
import clientGUIControllers.RegistrationFormController;
import clientGUIControllers.MyOrderController;
import clientGUIControllers.MarketingWorkerSalesController;
import clientGUIControllers.UpdateInventoryController;
import clientGUIControllers.PickUpOrderController;
import clientGUIControllers.SalesPatternsController;
import clientGUIControllers.SelfCollectionDetailsController;
import clientGUIControllers.ShipmentReportController;
import clientGUIControllers.ShipmentsConfirmationController;
import clientGUIControllers.CheckShipmentReportController;
import clientGUIControllers.TransferExecutionInstructionsController;
import clientGUIControllers.ViewCatalogController;
import clientGUIControllers.ViewReportController;
import entities.AreaManager;
import entities.CustomerReport;
import entities.ExecutionInstructions;
import entities.Facility;
import entities.InventoryReport;
import entities.Order;
import entities.Product;
import entities.Sale;
import entities.OrderReport;
import entities.SalePattern;
import entities.SelfCollection;
import entities.ServerMessage;
import entities.Shipment;

import entities.User;
import entityControllers.AreaManagerEntityController;
import entityControllers.Customer_ReportEntityController;
import entityControllers.Inventory_ReportEntityController;
import entityControllers.NotificationController;
import entityControllers.OrderEntityController;
import entityControllers.Order_ReportEntityController;
import entityControllers.UserController;
import entityControllers.UserOrdersEntityController;
import entityControllers.WorkerEntityController;
import javafx.event.ActionEvent;
import ocsf.AbstractClient;

/**
 * This class extends the AbstractClient class from the OCSF framework which
 * means this is our client which communicate with our server, the purpose of
 * this class is to maintain connection with the server ,send and receive
 * messages from the server and transferring the information from the UI
 * controllers to the server and back to the UI controllers.
 */
public class EkrutClient extends AbstractClient {

	// Attributes
	private static ClientConnectScreenController clientConnectScreenController = new ClientConnectScreenController();
	public volatile boolean msgReturn = true;

	// Entities Controllers
	private ClientConsole clientUI;
	private UserController userController;
	private ViewCatalogController viewCatalogController = new ViewCatalogController();
	private ConfirmRegistretionRequestController confirmRegistretionRequestController;
	private SalesPatternsController salesPatternsController;
	private ActivateSalesController activateSalesController;
	private SelfCollectionDetailsController selfCollectionDetailsController;
	private ViewReportController viewReportController;
	private TransferExecutionInstructionsController transferExecutionInstructionsController = new TransferExecutionInstructionsController();
	private UpdateInventoryController updateInventoryController;
	private MakeRegistrationController makeRegistrationController = null;
	private RegistrationFormController registrationFormController = null;
	private WorkerEntityController workerEntityController = null;
	private MyOrderController myOrderController = new MyOrderController();
	private MarketingWorkerSalesController marketingWorkerSalesController;
	private LoginController loginController;
	private ShipmentReportController shipmentReportController;
	private CheckShipmentReportController checkShipmentReportController;
	private ShipmentsConfirmationController shipmentsConfirmationController;

	// Entities Controllers
	private AreaManagerEntityController entity_areaManagerController;
	private Order_ReportEntityController order_ReportEntityController = new Order_ReportEntityController();
	private Customer_ReportEntityController customer_ReportEntityController = new Customer_ReportEntityController();
	private Inventory_ReportEntityController inventory_ReportEntityController = new Inventory_ReportEntityController();
	private NotificationController notification = new NotificationController();
	private OrderEntityController orderEntityController = new OrderEntityController();
	private UserOrdersEntityController userOrdersEntityController;
	private PickUpOrderController pickUpOrderController;

	// Constructor
	public EkrutClient(String host, int port, ClientConsole clientUI) throws IOException {
		super(host, port);
		this.clientUI = clientUI;
		openConnection();
	}

	/**
	 * This method is an override method from the abstract client of OCSF , this
	 * method gets invoked every time a message (instance of ServerMessage) is sent
	 * from the server, it main purpose is to deal with each message according to
	 * their Instruction in the ServerMessage using the data the message carries.
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	protected void handleMessageFromServer(Object msg) {
		ServerMessage sm = (ServerMessage) msg;
		switch (sm.getInstruction()) {
		case Nothing_To_Get:
			break;
		case User_not_found:
			boolean isForRegistration1 = (Boolean) sm.getData().get("isForRegistration");
			if (!isForRegistration1) {
				userController = new UserController();
				userController.setUser(null);
			} else {
				makeRegistrationController = new MakeRegistrationController();
				makeRegistrationController.setUserForRegisteretion(null);
				makeRegistrationController.setSecondRoleForRegistration(null);
			}
			break;
		case User_found:
			boolean isForRegistration = (Boolean) sm.getData().get("isForRegistration");
			if (!isForRegistration) {
				userController = new UserController();
				User user = (User) sm.getData().get("user");
				userController.setUser(user);
				Role role = (Role) sm.getData().get("secondRole");
				if (!role.equals(Role.NoRole)) {
					workerEntityController = new WorkerEntityController();
					workerEntityController.setSecondRole(role);
				}
				resetController();
			} else {
				makeRegistrationController = new MakeRegistrationController();
				User tempUser = (User) sm.getData().get("user");
				makeRegistrationController.setUserForRegisteretion(tempUser);
				Role role = (Role) sm.getData().get("secondRole");
				if (!role.equals(Role.NoRole)) {
					makeRegistrationController.setSecondRoleForRegistration(role);
				} else
					makeRegistrationController.setSecondRoleForRegistration(null);
			}
			break;
		case Area_Manager_Details_Found:
			entity_areaManagerController = new AreaManagerEntityController();
			notification = new NotificationController();
			notification.setContext((String) sm.getData().get("Notification"));
			entity_areaManagerController.setAreaManager((AreaManager) sm.getData().get("AreaManager"));
			break;
		case Report_Area_Manager_Found:
			if (sm.getData().get("Report") instanceof OrderReport) {
				order_ReportEntityController = new Order_ReportEntityController();
				order_ReportEntityController.setReport((OrderReport) sm.getData().get("Report"));
			}
			if (sm.getData().get("Report") instanceof CustomerReport) {
				customer_ReportEntityController = new Customer_ReportEntityController();
				customer_ReportEntityController.setCustomerReport((CustomerReport) sm.getData().get("Report"));
			}
			if (sm.getData().get("Report") instanceof InventoryReport) {

				inventory_ReportEntityController = new Inventory_ReportEntityController();
				inventory_ReportEntityController.setInventoryReport((InventoryReport) sm.getData().get("Report"));
			}
			break;
		case Report_Area_Manager_NotFound:
			order_ReportEntityController.setReport(null);
			inventory_ReportEntityController.setInventoryReport(null);
			customer_ReportEntityController.setCustomerReport(null);
			break;
		case All_Product_list:
			ArrayList<Product> productsList = (ArrayList<Product>) sm.getData().get("allProductList");
			viewCatalogController.setProductsList(productsList);
			break;
		case All_NotApprovedUser_list:
			confirmRegistretionRequestController = new ConfirmRegistretionRequestController();
			ArrayList<User> usersList = (ArrayList<User>) sm.getData().get("AllNotApprovedUsers");
			confirmRegistretionRequestController.setUsersList(usersList);
			break;
		case All_Operating_Workers:
			ArrayList<User> operatingWorkerList = (ArrayList<User>) sm.getData().get("operatingWorker");
			transferExecutionInstructionsController.setOperatingWorkerList(operatingWorkerList);
			break;
		case Set_Execution_Instructions_List:
			ArrayList<ExecutionInstructions> executionInstructionsList = (ArrayList<ExecutionInstructions>) sm.getData()
					.get("executionInstructions");
			transferExecutionInstructionsController.setExecutionInstructionsList(executionInstructionsList);
			break;
		case Facilities_In_Area_List:
			viewReportController = new ViewReportController();
			selfCollectionDetailsController = new SelfCollectionDetailsController();
			selfCollectionDetailsController = new SelfCollectionDetailsController();
			viewReportController = new ViewReportController();
			ArrayList<Facility> facilitiesList = (ArrayList<Facility>) sm.getData().get("facilitiesInArea");
			selfCollectionDetailsController.setFacilityList(facilitiesList);
			viewReportController.setFacilityList_CEO(facilitiesList);
			break;
		case Order_Get_Products:
			orderEntityController.getOrder().setOrderProductList((ArrayList<Product>) sm.getData().get("products"));
			break;
		case Add_Order_To_Invetory:
			orderEntityController.getOrder().setOrderProductList(null);
			break;
		case Order_Update:
			orderEntityController.setOrder((Order) sm.getData().get("order"));
			break;
		case New_Notification:
			notification = new NotificationController();
			notification.setContext((String) sm.getData().get("Notification"));
			break;
		case Subscriber_number_max:
			registrationFormController = new RegistrationFormController();
			int max = (Integer) sm.getData().get("subscriberNum");
			registrationFormController.setSubscriberMax(max);
			break;
		case Get_Sale_Facility:
			myOrderController.setDiscount((int) sm.getData().get("discount"));
			myOrderController.setIsSubFirst((boolean) sm.getData().get("isSubFirst"));
			break;
		case Get_Sales_By_Area_Success:
			marketingWorkerSalesController = new MarketingWorkerSalesController();
			ArrayList<Sale> activeSalesList = (ArrayList<Sale>) sm.getData().get("activeSalesList");
			marketingWorkerSalesController.setActiveSalesList(activeSalesList);
			ArrayList<Sale> SalesToactiveList = (ArrayList<Sale>) sm.getData().get("SalesToactiveList");
			marketingWorkerSalesController.setSalesToactiveList(SalesToactiveList);
			break;
		case All_salesPatterns_list:
			salesPatternsController = new SalesPatternsController();
			activateSalesController = new ActivateSalesController();
			ArrayList<SalePattern> salesPatterns = (ArrayList<SalePattern>) sm.getData().get("AllSalesPatterns");
			salesPatternsController.setSalesPatternArray(salesPatterns);
			activateSalesController.setSalesPatternArray(salesPatterns);
			break;
		case Shipment_Report_Return:
			if (((String) sm.getData().get("status")).equals("OK")) {
				shipmentReportController = new ShipmentReportController();
				shipmentReportController.setShipmentDistrbutionCount(
						(HashMap<String, Integer>) sm.getData().get("shipmentDistrbutionCount"));
				shipmentReportController.setShipmentDistrbutionExpense(
						(HashMap<String, Float>) sm.getData().get("shipmentDistrbutionExpense"));
				checkShipmentReportController.setReportexist(true);
			} else {
				checkShipmentReportController.setReportexist(false);
			}
			break;
		case Facility_Area_Value:
			updateInventoryController = new UpdateInventoryController();
			updateInventoryController.setValuesTable((Hashtable<String, Object>) sm.getData());
			break;
		case User_Orders:
			ArrayList<Order> ordersList = (ArrayList<Order>) sm.getData().get("localOrSelfOrders");
			ArrayList<Shipment> shipmentList = (ArrayList<Shipment>) sm.getData().get("shipmentOrders");
			ArrayList<SelfCollection> selfCollectionList = (ArrayList<SelfCollection>) sm.getData()
					.get("selfCollectionOrders");
			userOrdersEntityController = new UserOrdersEntityController();
			userOrdersEntityController.setOrdersList(ordersList);
			userOrdersEntityController.setShipmentList(shipmentList);
			userOrdersEntityController.setSelfCollectionList(selfCollectionList);
			break;
		case Shipments_by_area:
			ArrayList<Shipment> shipmentByAreaList = (ArrayList<Shipment>) sm.getData().get("shipmentByArea");
			userOrdersEntityController = new UserOrdersEntityController();
			userOrdersEntityController.setShipmentList(shipmentByAreaList);
			break;
		case Received_shipments_by_area:
			ArrayList<Shipment> receivedShipmentList = (ArrayList<Shipment>) sm.getData()
					.get("receivedShipmentsForConfirmation");
			userOrdersEntityController = new UserOrdersEntityController();
			userOrdersEntityController.setShipmentList(receivedShipmentList);
			break;
		case Customer_area_for_calculate:
			String customerArea = (String) sm.getData().get("customerArea");
			ShipmentsConfirmationController shipmentsConfirmController = new ShipmentsConfirmationController();
			shipmentsConfirmController.setCustomerArea(customerArea);
			break;
		case Get_Subscriber_Data:
			loginController = new LoginController();
			ArrayList<String> sub_array = (ArrayList<String>) sm.getData().get("subscribers");
			loginController.setSubscriber_userNamesArr(sub_array);
			break;
		case PickUp_Order_Values:
			pickUpOrderController = new PickUpOrderController();
			if (((String) sm.getData().get("Exists")).equals("Yes")) {
				if (((String) sm.getData().get("null")).equals("yes")) {
					pickUpOrderController.setActualPickUpDate(null);
				} else {
					pickUpOrderController.setActualPickUpDate((Date) sm.getData().get("actualPickUpDate"));
				}
				pickUpOrderController.setFacilityID((String) sm.getData().get("facilityID"));
				pickUpOrderController.setUserNameDB((String) sm.getData().get("userName"));
			} else {
				pickUpOrderController.setFacilityID("");
			}
			break;
		case User_for_SMS_and_email_found:
			shipmentsConfirmationController = new ShipmentsConfirmationController();
			User userForSMS = (User) sm.getData().get("userForSendEmailAndSMS");
			shipmentsConfirmationController.setUserForSMSAndEmail(userForSMS);
			break;
		default:
			break;
		}
		msgReturn = true;

	}

	/**
	 * This method reset the wanted controllers (mostly used on logout)
	 */
	private void resetController() {
		entity_areaManagerController = null;
	}

	/**
	 * This method start an instance o the EkrutClient and establish a connection to
	 * the server
	 * 
	 * @param serverIp the server IP to connect to
	 * @param port     the port to connect to on the server IP
	 * @param event    the ActionEvent click on connect in the client connect screen
	 */
	public static void connectToServer(String serverIp, String port, ActionEvent event) {

		// open only one instance of client
		if (ClientConsole.ekrutClient != null) {
			return;
		}
		try {
			ClientConsole.ekrutClient = new EkrutClient(serverIp, Integer.parseInt(port), new ClientConsole());
			clientConnectScreenController.connectionSuccessfull(event);
		} catch (IOException e) {
			EkrutClientUI.chat.display("Error while connecting to server");
		}
	}

	/**
	 * This method sends requests to the server with an instance of ServerMessage
	 * 
	 * @param msg the ServerMessage we want to send
	 */
	public void handleMessageFromClientUI(ServerMessage msg) {
		if (msgReturn = false) {
			clientUI.display("Wait for server to respond!");
		}
		try {
			msgReturn = false;
			sendToServer(msg);
		} catch (Exception e) {
			clientUI.display("Could not send message to server.");
		}
	}

}