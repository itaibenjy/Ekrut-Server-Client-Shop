package clientGUIControllers;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map.Entry;

import common.CommonActionsController;
import entityControllers.Order_ReportEntityController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;  



import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
		   JavaFX controller * Order_ReportEntityController instance
 *         variable to get report details Order Report Based On Distribution Of
 *         Facilities by order number in Area Presented as PIE Count Of Orders
 *         displayed in Bar Chart - Histogram
 */
public class OrderReportController {
	/*-----------FXML----------*/
	@FXML
	private Label area_label;

	@FXML
	private Button backButton;

	@FXML
	private Button logOut;

	@FXML
	private Label month_label;

	@FXML
	private Label numOfTotalOrders_label;

	@FXML
	private PieChart pieChartFacilities;

	@FXML
	private BarChart<String, Integer> histogramChartOrderMethod;

	@FXML
	private Button xButton;

	@FXML
	private Label year_label;

	@FXML
	private Label bestSeller;

	@FXML
	private Label profit;

	/* Entities And Controller */
	private CommonActionsController commonActionsController = new CommonActionsController();
	private Order_ReportEntityController order_ReportEntityController = new Order_ReportEntityController();
	/* Data Lists */
	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	XYChart.Series<String, Integer> series = new XYChart.Series<>();
	private DecimalFormat format = new DecimalFormat("0.#");

	/**
	 * The initialize method is used to set the values for the combo boxes and the
	 * 
	 */
	@FXML
	private void initialize() {
		profit.setText(format.format(order_ReportEntityController.getReport().getTotalPrice()) + "₪");
		// bestSeller.setText(null);
		month_label.setText(String.valueOf(order_ReportEntityController.getReport().getMonth()));
		year_label.setText(String.valueOf(order_ReportEntityController.getReport().getYear()));
		numOfTotalOrders_label.setText(String.valueOf(order_ReportEntityController.getReport().getNumOfTotalOrders()));
		area_label.setText(order_ReportEntityController.getReport().getArea().toString());
		pieChartFacilitiesIntilize();
		pieChartOrderMethodIntilize();

	}

	/**
	 * Pie Chat For Order Method Intilize
	 */
	private void pieChartOrderMethodIntilize() {
		histogramChartOrderMethod.setTitle("Order Method Distribution");
		series.getData().add(
				new XYChart.Data<>("Self-Colletion", order_ReportEntityController.getReport().getSelfColletionTotal()));
		series.getData()
				.add(new XYChart.Data<>("Local", order_ReportEntityController.getReport().getLocalColletionTotal()));
		series.getData().add(
				new XYChart.Data<>("Shipment", order_ReportEntityController.getReport().getShipmentColletionTotal()));
		histogramChartOrderMethod.getData().add(series);
		histogramChartOrderMethod.setBarGap(10);
	}

	/**
	 * Pie Chat For Facilities Intilize Distribution Of Facilities by order number
	 * in Area BY PRECENETAGE
	 */
	private void pieChartFacilitiesIntilize() {
		String maxString = "";
		int maxvalue = 0;
		pieChartFacilities.setTitle("Facilities Distribution");
		int sum = 0;
		for (Entry<String, Integer> value : order_ReportEntityController.getReport().getFacilitiesHashMap()
				.entrySet()) {

			if (value.getValue() > maxvalue) {
				maxvalue = value.getValue();
				maxString = value.getKey();
			}
			sum += value.getValue();
		}
		
		bestSeller.setText(maxString);
	
		for (HashMap.Entry<String, Integer> entry : order_ReportEntityController.getReport().getFacilitiesHashMap()
				.entrySet()) {
			double percent = (double) entry.getValue() / sum * 100;
			percent = (double) Math.round(percent * 100) / 100;
			pieChartData.add(new PieChart.Data(entry.getKey() + ":\n" + percent + "%", percent));

		}
		pieChartFacilities.setData(pieChartData);
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
