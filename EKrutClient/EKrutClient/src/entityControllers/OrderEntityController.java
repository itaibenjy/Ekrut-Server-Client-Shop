package entityControllers;

import entities.Order;

/**
 * General Entity order Controller , Save general information about report
 */
public class OrderEntityController {
	
	private static Order order=null;
	
	public OrderEntityController () {
		
	}

	public OrderEntityController (Order order) {
		OrderEntityController.order=order;
		
	}
	
	public void setOrder(Order order) {
		OrderEntityController.order=order;
	}
	
	public Order getOrder() {
		return OrderEntityController.order;
	}
	
	
}
