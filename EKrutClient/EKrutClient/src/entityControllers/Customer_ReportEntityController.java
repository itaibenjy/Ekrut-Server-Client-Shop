package entityControllers;
import entities.CustomerReport;
/**
 *  *The Customer_ReportEntityController class is a controller class for an CustomerReport entity. 
 *An CustomerReport is a type of Report that is responsible for display customer report  in the system. 
 *The class has a single instance variable, orderReport, which stores a reference to an CustomerReport object
 */

public class Customer_ReportEntityController {
	private static CustomerReport customerReport = null;

	public Customer_ReportEntityController() {
	}

	public Customer_ReportEntityController(CustomerReport user) {
		Customer_ReportEntityController.customerReport = user;
	}

	public void setCustomerReport(CustomerReport user) {
		Customer_ReportEntityController.customerReport = user;
	}
	
	public CustomerReport getCustomerReport() {
		return Customer_ReportEntityController.customerReport;
	}

	public boolean customerReportIsExist() {
		return Customer_ReportEntityController.customerReport != null;
	}
}
