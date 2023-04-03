package entityControllers;

import entities.InventoryReport;

/**
 *  *The Inventory_ReportEntityController class is a controller class for an Inventory_ReportEntityController entity. 
 *An inventoryReport is a type of Report that is responsible for display inventory report  in the system. 
 *The class has a single instance variable, inventoryReport, which stores a reference to an InventoryReport object
 */
public class Inventory_ReportEntityController {
	private static InventoryReport inventoryReport = null;

	public Inventory_ReportEntityController() {
	}

	public Inventory_ReportEntityController(InventoryReport inventoryReport) {
		Inventory_ReportEntityController.inventoryReport = inventoryReport;
	}

	public void setInventoryReport(InventoryReport inventoryReport) {
		Inventory_ReportEntityController.inventoryReport = inventoryReport;
	}
	
	public InventoryReport getInventoryReport() {
		return Inventory_ReportEntityController.inventoryReport;
	}

	public boolean ReportIsExist() {
		return Inventory_ReportEntityController.inventoryReport != null;
	}


}
