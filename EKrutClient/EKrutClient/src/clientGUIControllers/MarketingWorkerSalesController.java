
package clientGUIControllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;

import Client.EkrutClientUI;
import Enum.Instruction;
import common.CommonActionsController;
import entities.Sale;
import entities.ServerMessage;
import entityControllers.UserController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * This class gives the option to let a marketing worker active sales 
 * that the marketing manager requested to run based on the start date.
 * 
 * In addition, he can change the status of sales that are currently active
 * and have run out of activity time.
 * 
 * The class will make use of information from the 'sales' table,
 * which contains information on all promotions and their status
 */
public class MarketingWorkerSalesController {

	/* Attributes */
	

	// controllers:
	private CommonActionsController commonActionsController = new CommonActionsController();
	private UserController userController = new UserController();

	// Variables:
	Sale selectedSale = null;
	private static ArrayList<Sale> ActiveSalesList = new ArrayList<Sale>();
	private static ArrayList<Sale> SalesToactiveList = new ArrayList<Sale>();
	// List for table
	private final ObservableList<Sale> ActiveSalesData = FXCollections.observableArrayList();
	private final ObservableList<Sale> SalesToactiveData = FXCollections.observableArrayList();

	/* FXML */
	// labels:
	@FXML
	private Label welcomeToMarketingWorkerLabel;
	@FXML
	private Label NoActiveSalesLabel;
	@FXML
	private Label NoSalesToActiveLabel;
	@FXML
	private Label chooseLineErrorTable1;
	@FXML
	private Label chooseLineErrorTable2;
	// Text
	@FXML
    private Text help_label;
	@FXML
    private Text help_label1;
	@FXML
    private Text help_label2;
	
	// Buttons:
	@FXML
	private Button Active_Button;
	@FXML
	private Button Finish_Button;
	@FXML
	private Button Exit_Button;
	@FXML
	private Button LogOut;
	@FXML
	private Button Back_Button;
	@FXML
	private Button help_button;

	// Active Sales Table(first table):
	@FXML
	private TableView<Sale> activeSales;
	@FXML
	private TableColumn<Sale, Integer> SaleIDCol_ActiveSales;
	@FXML
	private TableColumn<Sale, String> SalePatternID_ActiveSales;
	@FXML
	private TableColumn<Sale, Date> SaleStartDate_ActiveSales;
	@FXML
	private TableColumn<Sale, Date> SaleEndDate_ActiveSales;

	// Sales To Active Table(second table):
	@FXML
	private TableView<Sale> salesToActive;
	@FXML
	private TableColumn<Sale, Integer> SaleIDCol_SalesToActive;
	@FXML
	private TableColumn<Sale, String> SalePatternID_SalesToActive;
	@FXML
	private TableColumn<Sale, Date> SaleStartDate_SalesToActive;
	@FXML
	private TableColumn<Sale, Date> SaleEndDate_SalesToActive;
	// images
	@FXML
	private ImageView logoImage;
	@FXML
	private ImageView activeSalegif;
	
	// Events:
	/**
	 * Pressing this button will completely take us out of the screen.
	 * @param event
	 */
	@FXML
	void Exit_Pressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}
	/**
	 * Clicking this button will disconnect the user from the system and take him to the login screen.
	 * @param event
	 */
	@FXML
	void LogOut_Pressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}
	
	  /**
     * @param event Back button pressed, and the method will take us back to the area deliveries operator home page.
     * */
    @FXML
    void BackButton_Pressed(ActionEvent event) {
    	commonActionsController.backOrNextPressed(event, "/clientGUIScreens/MarketingWorkerScreen.fxml");
    }
    /**
	 * Clicking this button will open a help menu with the actions to be performed on this screen.
	 * @param event
	 */
	@FXML
    void helpButton_Pressed(ActionEvent event) {
    	help_button.setVisible(false);
    	help_label.setVisible(true);
    	help_label1.setVisible(true);
    	help_label2.setVisible(true);
    }
	
	/**
	 * initialize
	 */
	@FXML
	private void initialize() {
		
		setImages();
		// initialize active sales table
		getSales();
		if (ActiveSalesList.isEmpty() == true) {
			activeSales.setVisible(false);
			NoActiveSalesLabel.setVisible(true);
		} else {
			SetActiveSalesTable();
		}
		// initialize sales to active table
		if (SalesToactiveList.isEmpty() == true) {
			salesToActive.setVisible(false);
			NoSalesToActiveLabel.setVisible(true);
		} else {
			SetSalesToActiveTable();
		}
	}

	

	
	/**
	 * Finish Button Pressed
	 * @param event
	 */
	@FXML
	void FinishButton_Pressed(ActionEvent event) {
		selectedSale = activeSales.getSelectionModel().getSelectedItem();
		if (selectedSale == null) {
			chooseLineErrorTable1.setVisible(true);
		} else {
			chooseLineErrorTable1.setVisible(false);
			updateActiveSale(selectedSale.getSaleId());
			commonActionsController.popUp(new Stage(), "Sale Finished! ", " Sale number " +selectedSale.getSaleId() + " finished successfully !\n"   , "/myOrderScreenPic/minus-button.png", null, event);
			// refresh table
			ActiveSalesData.clear();
			getSales();
			if (ActiveSalesList.isEmpty() == true) {
				activeSales.setVisible(false);
				NoActiveSalesLabel.setVisible(true);
			} else {
				SetActiveSalesTable();
				selectedSale = null;
			}
		}

	}

	
	/**
	 * Active Button Pressed
	 * @param event
	 */
	@FXML
	void ActiveButton_Pressed(ActionEvent event) {
		selectedSale = salesToActive.getSelectionModel().getSelectedItem();
		if (selectedSale == null) {
			chooseLineErrorTable2.setVisible(true);
		} else {
			chooseLineErrorTable2.setVisible(false);
			updateSaleToActive(selectedSale.getSaleId());
			// refresh table
			SalesToactiveData.clear();
			getSales();
			if (SalesToactiveList.isEmpty() == true) {
				salesToActive.setVisible(false);
				NoSalesToActiveLabel.setVisible(true);
				// update active table after update sale to active so reload the values in
				// active sales table
				ActiveSalesData.clear();
				SetActiveSalesTable();
			} else {
				// update active table after update sale to active so reload the values in
				// active sales table
				ActiveSalesData.clear();
				SetActiveSalesTable();
				// update sale To Active table
				SetSalesToActiveTable();
				activeSales.setVisible(true);
				selectedSale = null;
			}
			commonActionsController.popUp(new Stage(), "Sale Activated ", " Sale number " +selectedSale.getSaleId() + " activated successfully !\n"   , "/myOrderScreenPic/sale.gif", "/clientGUIScreens/MarketingWorkerSalesScreen.fxml", event);

		}
	}

	/* End FXML */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		activeSalegif.setImage(new Image("/marketingManagerImages/activeSale.gif"));

	}

	public static void setActiveSalesList(ArrayList<Sale> activeSalesList) {
		ActiveSalesList = activeSalesList;
	}

	public static void setSalesToactiveList(ArrayList<Sale> salesToactiveList) {
		SalesToactiveList = salesToactiveList;
	}

	private void getSales() {
		// Go to DB and get all Active Sales
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("area", userController.getUser().getArea().toString());
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_Sales_By_Area, data));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		;
	}

	
	/**
	 * Set table ActiveSales values
	 */
	private void SetActiveSalesTable() {
		for (Sale sale : ActiveSalesList) {
			Sale salwToTable = new Sale(sale.getSaleId(), sale.getSalePatternId(), sale.getStartDate(),
					sale.getEndDate());
			SaleIDCol_ActiveSales.setCellValueFactory(new PropertyValueFactory<Sale, Integer>("saleId"));
			SalePatternID_ActiveSales.setCellValueFactory(new PropertyValueFactory<Sale, String>("salePatternId"));
			SaleStartDate_ActiveSales.setCellValueFactory(new PropertyValueFactory<Sale, Date>("startDate"));
			SaleEndDate_ActiveSales.setCellValueFactory(new PropertyValueFactory<Sale, Date>("endDate"));
			ActiveSalesData.add(salwToTable);
		}
		activeSales.setItems(ActiveSalesData);
	}

	
	/**
	 * Set table SalesToActive values
	 */
	private void SetSalesToActiveTable() {
		for (Sale sale : SalesToactiveList) {
			Sale salwToTable = new Sale(sale.getSaleId(), sale.getSalePatternId(), sale.getStartDate(),
					sale.getEndDate());
			SaleIDCol_SalesToActive.setCellValueFactory(new PropertyValueFactory<Sale, Integer>("saleId"));
			SalePatternID_SalesToActive.setCellValueFactory(new PropertyValueFactory<Sale, String>("salePatternId"));
			SaleStartDate_SalesToActive.setCellValueFactory(new PropertyValueFactory<Sale, Date>("startDate"));
			SaleEndDate_SalesToActive.setCellValueFactory(new PropertyValueFactory<Sale, Date>("endDate"));
			SalesToactiveData.add(salwToTable);
		}
		salesToActive.setItems(SalesToactiveData);
	}

	/**
	 * Go to DB and update Active Sales to Finish
	 * @param saleId
	 */
	private void updateActiveSale(int saleId) {
		
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("saleId", selectedSale.getSaleId());
		data.put("status", "Finish");
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Update_Sale_Status, data));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		;

	}

	/**
	 * Go to DB and update Active Sales to Finish
	 * @param saleId
	 */
	private void updateSaleToActive(int saleId) {
		
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("saleId", selectedSale.getSaleId());
		data.put("status", "Active");
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Update_Sale_Status, data));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		;
	}
}
