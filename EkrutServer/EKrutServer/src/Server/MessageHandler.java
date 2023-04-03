package Server;

import entities.ServerMessage;
import jdbc.DataBaseAreaManagerController;
import jdbc.DataBaseOpreatingWorker;
import jdbc.DataBaseOrderController;
import jdbc.DataBasePickUpOrderController;
import jdbc.DataBaseReportController;
import jdbc.DataBaseSalesController;
import jdbc.DataBaseTransferExecutionInstructionsController;
import jdbc.DataBaseUserController;
import jdbc.DataBaseViewCatalogController;
import ocsf.ConnectionToClient;

/**
 * This class manages all incoming messages from clients and divert them to the correct database 
 * controller.
 */
public class MessageHandler {

	/**
	 *  This function manages all incoming messages from clients and divert them to the 
	 *  correct methods in the correct data base controller with a switch case on the Enum 
	 *  Instruction (which say which instruction this message want to accomplish);
	 */
	public static void handleMessage(Object msg, ConnectionToClient client) {
		ServerMessage sm = (ServerMessage) msg;
		switch (sm.getInstruction()) {
		case Get_user_details:
			sm = DataBaseUserController.getUser(sm.getData());
			break;
		case Get_Subscriber_EKT_Login:
			sm = DataBaseUserController.getSubscriber(sm.getData());
			break;
		case Set_User_loggedIn:
			ClientConnectionController.saveToConnection(sm.getData(), client);
			sm = DataBaseUserController.setUserLoggedIn(sm.getData());
			break; 
		case Update_user_details_After_RegisteredCustomer_Registration:
			sm=DataBaseUserController.updateRegisteredCustomerAfterRegistration(sm.getData());
			break;
		case Update_subscriber_details_After_Subscriber_Registration:
			sm=DataBaseUserController.updateSubscriberAfterRegistration(sm.getData());
			break;
		case Get_max_subscriber_number: 
			sm=DataBaseUserController.getSubscriberNumber(sm.getData());
			break; 
		case Get_all_Product_list: 
			sm = DataBaseViewCatalogController.getAllProducts();
			break;
		case Get_all_Users_NotApproved_list:
			sm = DataBaseUserController.getAllNotApprovedUsers(sm.getData());
			break;
		case Update_User_userStatus:
			sm = DataBaseUserController.updateUserStatus(sm.getData());
			break;
		case Get_Area_Manager_Details:
			sm = DataBaseAreaManagerController.getAreaManagerDetails(sm.getData());
			break; 
		case Set_Product_ThresholdLevel:
			sm = DataBaseAreaManagerController.setThresholdLevel(sm.getData());
			break;
		case Get_Report_Area_Manager:
			sm = DataBaseReportController.get_Report_Area_Manager(sm.getData());
			break;
		case Get_Facility_Products:
			sm = DataBaseViewCatalogController.getFacilityProducts(sm.getData());
			break;
		case Get_Operating_Workers:
			sm = DataBaseTransferExecutionInstructionsController.getoperatingWorkerList(sm.getData());
			break;
		case Get_Execution_Instructions_List:
			sm = DataBaseTransferExecutionInstructionsController.getexecutionInstructionsList(sm.getData());
			break;
		case Update_Execution_Instructions:
			sm = DataBaseTransferExecutionInstructionsController.updateExecutionInstruction(sm.getData());
			break;
		case Set_New_Execution_Instructions:
			sm = DataBaseTransferExecutionInstructionsController.setExecutionInstruction(sm.getData());
			break;
		case Update_Inventory_Execution_Instructions:
			sm = DataBaseOpreatingWorker.updateInventoryExecutionInstruction(sm.getData());
			break;
		case Update_Status_Execution_Instructions:
			sm = DataBaseOpreatingWorker.updateStatusExecutionInstruction(sm.getData());
			break;	
		case Update_Product_Inventory:
			sm = DataBaseOpreatingWorker.updateProductInventory(sm.getData());
			break;	
		case Get_facilities_in_Area:
			sm = DataBaseAreaManagerController.getFacilitiesInArea(sm.getData());
			break;
		case Order_Get_Products:
			sm = DataBaseOrderController.updateOrderProducts(sm.getData(), false, sm.getInstruction());
			break;
		case Add_Order_To_Invetory:
			sm = DataBaseOrderController.updateOrderProducts(sm.getData(), true, sm.getInstruction());
			break;
		case Make_An_Order:
			sm = DataBaseOrderController.makeAnOrder(sm.getData());
			break;
		case Get_Sales_By_Area:
			sm= DataBaseSalesController.getSales(sm.getData());
			break;	
		case Update_Sale_Status:
			sm= DataBaseSalesController.updateSaleStatus(sm.getData());
			break;	
		case Get_all_Sales_Pattern_list:
			sm = DataBaseSalesController.getAllSalesPatterns();
			break;
		case set_Sales_Pattern:
			sm = DataBaseSalesController.setSalesPattern(sm.getData());
			break;
		case add_New_Sale:
			sm = DataBaseSalesController.insertNewSale(sm.getData());
			break;
		case Get_Subscriber_Details:
			sm = DataBaseUserController.getSubscriberUserName();
			break;
		case Get_Facility_Area:
			sm = DataBaseOpreatingWorker.getFacilityArea(sm.getData());
			break;
		case Shipment_Report_Search:
			sm = DataBaseReportController.generate_Shipment_Report(sm.getData());
			break;
		case Get_Sale_Facility:
			sm = DataBaseSalesController.getActiveDiscount(sm.getData());
			break;
		case Get_user_orders:
			sm = DataBaseOrderController.getUserOrders(sm.getData());
			break;
		case Update_order:
			sm = DataBaseOrderController.updateOrder(sm.getData());
			break;
		case Get_shipments_of_area:
			sm = DataBaseOrderController.getShipmentByArea(sm.getData());
			break;
		case Update_shipment_status:
			sm = DataBaseOrderController.updateShipmentStatus(sm.getData());
			break;
		case Get_recived_shipments_for_confirmation:
			sm = DataBaseOrderController.getReceviedShipmentForConfirmation(sm.getData());
			break;
		case Calculate_delivery_time:
			sm = DataBaseOrderController.getCustomerArea(sm.getData());
			break;
		case Update_shipment_estimate_date:
			sm = DataBaseOrderController.updateEstimateDate(sm.getData());
			break;
		case Get_PickUp_Order:
			sm= DataBasePickUpOrderController.getPickUpOrder(sm.getData());
			break;
		case SET_PickUp_Date:
			sm= DataBasePickUpOrderController.setPickUpDate(sm.getData());
			break;
		case Disconnect_From_Server:
			
			EkrutServer.getServer().clientDisconnected(client);
			return;
		case Get_user_details_for_send_email_and_sms:
			sm=DataBaseUserController.getUserDetailsForSendEmailAndSMS(sm.getData());
			break;
		default:
			break;
		}
		EkrutServer.getServer().sendToClient(sm, client);
	}

}
