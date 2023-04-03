package entityControllers;

import java.util.ArrayList;

import entities.Order;
import entities.SelfCollection;
import entities.Shipment;


/**
 * @author Yotam Aharon 
 * UserOrdersEntityController class is a controller class that handles the order, shipment and self collection objects. 
 * It uses array list data structure to store the order, shipment, and self-collection objects and send them from 
 * Client to Server and vice versa..
 */
public class UserOrdersEntityController {
	
	/* -----Array lists for the orders----- */
	private static ArrayList<Order> ordersList = new ArrayList<Order>();
	private static ArrayList<Shipment> shipmentList = new ArrayList<Shipment>();
	private static ArrayList<SelfCollection> selfCollectionList = new ArrayList<SelfCollection>();
	
	/* --Constructors--*/
	public UserOrdersEntityController() {
		
	}

	/*----Getters and Setters----*/
	public static ArrayList<Order> getOrdersList() {
		return ordersList;
	}

	public static void setOrdersList(ArrayList<Order> ordersList) {
		UserOrdersEntityController.ordersList = ordersList;
	}
	public static ArrayList<Shipment> getShipmentList() {
		return shipmentList;
	}

	public static void setShipmentList(ArrayList<Shipment> shipmentList) {
		UserOrdersEntityController.shipmentList = shipmentList;
	}
	
	public static ArrayList<SelfCollection> getSelfCollectionList() {
		return selfCollectionList;
	}

	public static void setSelfCollectionList(ArrayList<SelfCollection> selfCollectionList) {
		UserOrdersEntityController.selfCollectionList = selfCollectionList;
	}

}
