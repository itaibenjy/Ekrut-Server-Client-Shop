package entityControllers;

import entities.OrderReport;

/**
 *  *The Order_ReportEntityController class is a controller class for an Order_ReportEntityController entity. 
 *An Order_ReportEntityController is a type of Report that is responsible for order inventory report  in the system. 
 *The class has a single instance variable, orderReport, which stores a reference to an OrderReport object
 */
public class Order_ReportEntityController {
	private static OrderReport orderReport = null;

	public Order_ReportEntityController() {
	}

	public Order_ReportEntityController(OrderReport orderReport) {
		Order_ReportEntityController.orderReport = orderReport;
	}

	public void setReport(OrderReport orderReport) {
		Order_ReportEntityController.orderReport = orderReport;
	}
	
	public OrderReport getReport() {
		return Order_ReportEntityController.orderReport;
	}

	public boolean ReportIsExist() {
		return Order_ReportEntityController.orderReport != null;
	}


}
