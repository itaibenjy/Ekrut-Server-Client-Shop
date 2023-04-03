package clientGUIControllers;

import java.util.HashMap;

import common.CommonActionsController;
import entityControllers.Inventory_ReportEntityController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

/**
 * 			 JavaFX controller * Inventory_ReportEntityController instance
 *         variable to get report details Inventory Report Based On Distribution Of
 *         of products in facilities inventory Area Presented as PIE 
 *         Count Of times passed threshold level for every product in Histogram
 */
public class InventoryReportController {
	
	/*-----------FXML----------*/
	@FXML
	private Label area_label;

	@FXML
	private Button backButton;

    @FXML
    private StackedBarChart<String, Integer> inventoryReportHistogram;
    
	@FXML
	private PieChart inventoryReportPie;

	@FXML
	private Button logOut;

	@FXML
	private Label month_label;


    @FXML
    private Text vaildNum;

	@FXML
	private Button xButton;

    @FXML
    private Text numOfTotalOrders_label;


	@FXML
	private Label year_label;
	  @FXML
	    private Label facilityID;
	/* Entities And Controller */
	private CommonActionsController commonActionsController = new CommonActionsController();
	private Inventory_ReportEntityController inventory_ReportEntityController = new Inventory_ReportEntityController();
	/* Data Lists */
	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	XYChart.Series histogramData = new XYChart.Series();

	/**
	 * The initialize method is used to set the values for the combo boxes and the
	 * 
	 */
	@FXML
	private void initialize() {
		month_label
				.setText(String.valueOf(inventory_ReportEntityController.getInventoryReport().getMonth()));
		facilityID.setText(inventory_ReportEntityController.getInventoryReport().getFacilityID());
		year_label.setText(String.valueOf(inventory_ReportEntityController.getInventoryReport().getYear()));
		numOfTotalOrders_label.setText( String.valueOf(inventory_ReportEntityController.getInventoryReport().getNumOfTotalProducts()));
		area_label.setText( inventory_ReportEntityController.getInventoryReport().getArea().toString());
		vaildNum.setText(String.valueOf( inventory_ReportEntityController.getInventoryReport().getSmallInventoroy()) );
		inventoryReportPie_intilize();
		inventoryReportHistogram_intilize();
	}


	/**
	 * Pie Chat Of Inventory Intilize
	 */
	private void inventoryReportPie_intilize() {
		inventoryReportPie.setTitle("Facilities Distribution");
		int sum = 0;
		for (int value : inventory_ReportEntityController.getInventoryReport()
				.getFacilitieInventory().values()) {
		  sum += value;
		}
		
		for (HashMap.Entry<String, Integer> entry : inventory_ReportEntityController.getInventoryReport()
				.getFacilitieInventory().entrySet()) {
			  int value = entry.getValue();
			  double percent = (double) entry.getValue() / sum * 100;
			  percent= (double) Math.round(percent * 100) / 100;
			  
			pieChartData.add(new PieChart.Data(entry.getKey()+" :\n"+percent+"%", percent));
		}
		inventoryReportPie.setData(pieChartData);
	}

	/**
	 *    Count Of times passed threshold level for every product in Histogram
	 */
	private void inventoryReportHistogram_intilize() {

		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		for (HashMap.Entry<String, Integer> entry : inventory_ReportEntityController.getInventoryReport()
				.getPassedThresHold().entrySet()) {
			
			series = new XYChart.Series<>();
			series.getData().add(new XYChart.Data<>(entry.getKey(),entry.getValue() ));
			series.setName(entry.getKey());
			inventoryReportHistogram.getData().add(series);

		}
		inventoryReportHistogram.setVerticalGridLinesVisible(false);
	}
	/**
	 * @param event Back Button Pressed , Back To ViewReportScreen.fxml
	 */
	@FXML
	void backButton_ButtonPressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/ViewReportScreen.fxml");
	}
	/**
	 * @param event log Out Button Pressed log out the user
	 */
	@FXML
	void logOut_ButtonPressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}
	/**
	 * @param event X Button Pressed method closes the window
	 */
	@FXML
	void x_ButtonPressed(ActionEvent event) {
		commonActionsController.xPressed(event);

	}
}
