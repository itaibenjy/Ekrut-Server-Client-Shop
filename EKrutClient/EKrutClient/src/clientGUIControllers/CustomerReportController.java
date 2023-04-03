package clientGUIControllers;

import java.util.HashMap;
import javafx.scene.chart.BarChart;
import common.CommonActionsController;
import entityControllers.Customer_ReportEntityController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *		   Customer Report JavaFX controller CustomerReportController
 *         instance variable to get report details Include Histogram Chart Of
 *         Customers The Graph will show 10%,20%.....100% Distbution , Based On
 *         Money Customer Spent In Area In specific Month
 *
 */
public class CustomerReportController {
	/*-----------FXML----------*/
	@FXML
	private Label area_label;

	@FXML
	private Button backButton;

	@FXML
	private BarChart<String, Number> histogram;

	@FXML
	private Button logOut;

	@FXML
	private Label month_label;

	@FXML
	private Label mostExpenseUser_label;

	@FXML
	private Label numOfTotalUsers_label;

	@FXML
	private Label userOrderdMost;

	@FXML
	private Button xButton;

	@FXML
	private Label year_label;
	 
	@FXML  
	 private Label totalProfit;
	
	@FXML
	private ImageView logo;

    @FXML
    private ImageView productImage;
    
    @FXML
    private ImageView profit;
    
    @FXML
    private ImageView buyMost;

    @FXML
    private ImageView expenceMost;
    
    @FXML
    private ImageView totalUsers;
	
	
	/* Entities And Controller */
	private CommonActionsController commonActionsController = new CommonActionsController();
	private Customer_ReportEntityController entity_Customer_ReportEntityController = new Customer_ReportEntityController();
	private int totalprofit=0;
	
	/**
	 * The initialize method is used to set the values for the combo boxes and the
	 */
	@FXML
	private void initialize() {
		setImages();
		
		month_label.setText(String.valueOf(entity_Customer_ReportEntityController.getCustomerReport().getMonth()));
		year_label.setText(String.valueOf(entity_Customer_ReportEntityController.getCustomerReport().getYear()));
		numOfTotalUsers_label.setText(
				String.valueOf(entity_Customer_ReportEntityController.getCustomerReport().getCustomerHashMap().size()));
		area_label.setText(entity_Customer_ReportEntityController.getCustomerReport().getArea().toString());
		userOrderdMost.setText(entity_Customer_ReportEntityController.getCustomerReport().getBiggestOrderCountUser());
		mostExpenseUser_label.setText(entity_Customer_ReportEntityController.getCustomerReport().getMostExpenseUser() + "\nSpent : "+entity_Customer_ReportEntityController.getCustomerReport().getCustomerHashMap().get(entity_Customer_ReportEntityController.getCustomerReport().getMostExpenseUser() )+"₪");
		intilizeHistogramChart();
		totalProfit.setText(String.valueOf(totalprofit)+" ₪");

	}

	/**
	 * set Images
	 */
	private void setImages() {
		profit.setImage(new Image("/ReportPhotos/arrow-trend-up.png"));
		buyMost.setImage(new Image("/ReportPhotos/shopping-cart.png"));
		expenceMost.setImage(new Image("/ReportPhotos/payment-method.png"));
		totalUsers.setImage(new Image("/ReportPhotos/group.png"));
	}

	/**
	 * Intilize Histogram Chart Of Customers The Graph will show 10%,20%.....100%
	 * Distbution , Based On Money Customer Spent In Area In specific Month
	 */
	private void intilizeHistogramChart() {
		int maxValue = 0;
		for (HashMap.Entry<String, Integer> entry : entity_Customer_ReportEntityController.getCustomerReport()
				.getCustomerHashMap().entrySet()) {
			totalprofit+=entry.getValue();
			if (entry.getValue() > maxValue) {
				maxValue = entry.getValue();
			}
		}

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		String range = "";
		histogram.setTitle("Distribution Of Customers According To Financial Expenses");
		for (int i = 0; i <= 100; i += 10) {

			int count = 0;
			for (HashMap.Entry<String, Integer> entry : entity_Customer_ReportEntityController.getCustomerReport()
					.getCustomerHashMap().entrySet()) {
				double calc = (entry.getValue() / (double) maxValue) * 100;
				if (calc >= i && calc < (i + 10)) {
					count++;
				}

			}
			if (i < 100) {
				range = String.valueOf(i + 10) + "%";
			}
			series.getData().add(new XYChart.Data<>(range, count));
			series.setName("Count of users");

		}
		histogram.setVerticalGridLinesVisible(false);
		histogram.getData().add(series);
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
//crane
}
