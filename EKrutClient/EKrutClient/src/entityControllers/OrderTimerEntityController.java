package entityControllers;

import common.OrderTimer;

/**
 * General Entity order Controller , Save a static instance of the OrderTimer
 */
public class OrderTimerEntityController {
	
	// the static instance of the OrderTimer for the whole program
	private static OrderTimer orderTimer;
	
	// Constructor
	public OrderTimerEntityController() {
	}
	
	// Getters and setters
	public OrderTimer getOrderTimer() {
		return orderTimer;
	}

	public void setOrderTimer(OrderTimer orderTimerVal) {
		orderTimer = orderTimerVal;
	}
	
	
}
