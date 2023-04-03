package clientGUIControllers;

import java.util.HashMap;

import common.CommonActionsController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *   JavaFX controller  Shipment Report Based On Distribution
 *         Of Shipments Count in Area Presented as PIE Profit from shipment in
 *         every area displayed in Bar Chart Presented as Histogram
 */
public class ShipmentReportController {

	/* FXML */
	@FXML
	private Button backButton;

	@FXML
	private Button logOut;

	@FXML
	private Button xButton;

	@FXML
	private Label month_label;

	@FXML
	private Label numOfTotalOrders_label;

	@FXML
	private Label year_label;

	@FXML
	private PieChart pieChartFacilities;


    @FXML
    private StackedBarChart<String, Float>histogramProfitable;
    
	/* Entities And Controller */
	 private CommonActionsController commonActionsController = new CommonActionsController();
	/* Atrributes */
	private static  HashMap<String, Integer> shipmentDistrbutionCount = new HashMap<>();
	private  static HashMap<String, Float> shipmentDistrbutionExpense = new HashMap<>();
	/* Data Lists */
	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	int totalOrdersCount=0;
	/**
	 * The initialize method is used to set the values for javafx
	 */
	@FXML
	private void initialize() {
		histogramProfitable_initialize();
		totalOrdersCount =  shipmentDistrbutionCount.get("South")+shipmentDistrbutionCount.get("North")+shipmentDistrbutionCount.get("UAE");
		pieChartFacilities_initialize();
		month_label.setText(String.valueOf( Math.round(shipmentDistrbutionExpense.get("Month"))));
		year_label.setText(String.valueOf(Math.round(shipmentDistrbutionExpense.get("Year"))));
		float totalProfit = shipmentDistrbutionExpense.get("South")+shipmentDistrbutionExpense.get("North")+shipmentDistrbutionExpense.get("UAE");
		numOfTotalOrders_label.setText(String.valueOf(totalProfit)+" ₪");

	}

	/**
	 * 	 * method is used to set the values to pie chart 
	 */
	private void pieChartFacilities_initialize() {

		pieChartFacilities.setTitle("Quantity of orders : "+String.valueOf(totalOrdersCount));
		int sum = 0;
		for (int value : shipmentDistrbutionCount.values()) {
		  sum += value;
		}
		for (HashMap.Entry<String, Integer> entry : shipmentDistrbutionCount.entrySet()) {
			  double percent = (double) entry.getValue() / sum * 100;
			  percent= (double) Math.round(percent * 100) / 100;
			pieChartData.add(new PieChart.Data(entry.getKey()+" : "+percent+"%", percent));

		}
		pieChartFacilities.setData(pieChartData);

	}

	/**
	 * 	 * method is used to set the values bar chart - histogram
	 */
	private void histogramProfitable_initialize() {
		XYChart.Series<String, Float>dataOperationCenterSouth = new XYChart.Series<>();
		XYChart.Series<String, Float> dataOperationCenterNorth = new XYChart.Series<>();
		XYChart.Series<String, Float> dataOperationCenterUAE = new XYChart.Series<>();
		//Names
		dataOperationCenterSouth.setName("South");
		dataOperationCenterNorth.setName("North");
		dataOperationCenterUAE.setName("UAE");
		//add data to series
		dataOperationCenterSouth.getData().add(new XYChart.Data<>("South",shipmentDistrbutionExpense.get("South")));
		dataOperationCenterUAE.getData().add(new XYChart.Data<>("UAE",shipmentDistrbutionExpense.get("UAE")));
		dataOperationCenterNorth.getData().add(new XYChart.Data<>("North",shipmentDistrbutionExpense.get("North")));
		

		//add data
		histogramProfitable.setTitle("Shipping Profits By Areas  ");
		//histogramProfitable.getData().addAll(dataOperationCenterSouth,dataOperationCenterUAE,dataOperationCenterNorth);	
		histogramProfitable.getData().add(dataOperationCenterSouth);
		histogramProfitable.getData().add(dataOperationCenterUAE);
		histogramProfitable.getData().add(dataOperationCenterNorth);
		histogramProfitable.setVerticalGridLinesVisible(false);

	}

	/**
	 * @param event Back Button Pressed , Back To ViewReportScreen.fxml
	 */
	@FXML
	void backButton_ButtonPressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/CheckShipmentReportScreen.fxml");
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
	public  void setShipmentDistrbutionCount(HashMap<String, Integer> shipmentDistrbutionCount) {
		ShipmentReportController.shipmentDistrbutionCount = shipmentDistrbutionCount;
	}

	public  void setShipmentDistrbutionExpense(HashMap<String, Float> shipmentDistrbutionExpense) {
		ShipmentReportController.shipmentDistrbutionExpense = shipmentDistrbutionExpense;
	}

}