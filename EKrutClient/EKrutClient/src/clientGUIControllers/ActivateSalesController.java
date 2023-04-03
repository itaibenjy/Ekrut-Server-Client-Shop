package clientGUIControllers;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Instruction;
import common.CommonActionsController;
import entities.SalePattern;
import entities.ServerMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class gives the option to Marketing Manger to ask from the Marketing
 * Worker to active one of the sale pattern. He also can view all current sale
 * patterns He can choose which sale pattern he want to active. He can choose
 * the dates the sale will be.
 *
 * This department makes use of the tables: * 'SalesPatterns', where we will
 * show the current sale patten and choose one of them. * 'Sales', where we will
 * add the requests for the marketing worker.  
 */

public class ActivateSalesController {

	@FXML
	private Button LogOut;

	@FXML
	private Button Back_Button;

	@FXML
	private Button Exit_Button;

	@FXML
	private DatePicker endDate;

	@FXML
	private TableView<SalePattern> SalesPattensView;

	@FXML
	private TableColumn<SalePattern, Integer> salePatternId;

	@FXML
	private TableColumn<SalePattern, String> description;

	@FXML
	private TableColumn<SalePattern, Integer> startHour;

	@FXML
	private TableColumn<SalePattern, Integer> endHour;

	@FXML
	private TableColumn<SalePattern, Integer> discount_total;

	@FXML
	private ComboBox<String> areaComboBox;

	@FXML
	private DatePicker startDate;

	@FXML
	private Label area;

	@FXML
	private Button ActiveSale;

	@FXML
	private Label errDate;

	@FXML
	private Label errMarkPattern;

	@FXML
	private Label errSatrtDate;

	@FXML
	private Label errEndDate;

	@FXML
	private Label msgSucc;

	@FXML
	private ImageView areaImage;

	@FXML
	private ImageView logoImage;
	
    @FXML
    private ImageView activeSalegif;


	@FXML
	private Text des2;

	@FXML
	private Text des1;

	@FXML
	private Button help;
	
    @FXML
    private ImageView calendar;

	private SalePattern salePatternTemp = null;
	LocalDate selected_startDate = null, selected_endDate = null;
	String areaStringSelected = null, endDateString, startDateString;
	private CommonActionsController commonActionsController = new CommonActionsController();
	private static ArrayList<SalePattern> salesPatternArray = new ArrayList<SalePattern>();
	private ObservableList<String> areaOption = FXCollections.observableArrayList("South", "North", "UAE");

	// intial the data in area combo box
	@FXML
	private void initialize() {
		setImages();
		areaComboBox.setItems(areaOption);
		createSalePatternTable();

	}

	/**
	 * Set images
	 */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		areaImage.setImage(new Image("/location.png"));
		calendar.setImage(new Image("/marketingManagerImages/calendar.png"));
		activeSalegif.setImage(new Image("/marketingManagerImages/activeSale.gif"));
	}

	/**
	 * This function get data of sales patterns from DB and put that on table view
	 */
	private void createSalePatternTable() {
		SalesPattensView.getItems().clear(); // First clear the table
		ServerMessage msg = new ServerMessage(Instruction.Get_all_Sales_Pattern_list, null);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		; // wait for server update
		for (int i = 0; i < salesPatternArray.size(); i++) {
			// put the data on the table view
			SalesPattensView.getItems()
					.add(new SalePattern(salesPatternArray.get(i).getSalePatternId(),
							salesPatternArray.get(i).getDescription(), salesPatternArray.get(i).getDiscount_total(),
							salesPatternArray.get(i).getStartHour(), salesPatternArray.get(i).getEndHour()));
		}
		initTableView(); // we got a choose of users and init the table
	}

	/**
	 * intial the table with the data of sale patterns
	 */
	private void initTableView() {
		salePatternId.setCellValueFactory(new PropertyValueFactory<SalePattern, Integer>("salePatternId"));
		description.setCellValueFactory(new PropertyValueFactory<SalePattern, String>("description"));
		discount_total.setCellValueFactory(new PropertyValueFactory<SalePattern, Integer>("discount_total"));
		startHour.setCellValueFactory(new PropertyValueFactory<SalePattern, Integer>("startHour"));
		endHour.setCellValueFactory(new PropertyValueFactory<SalePattern, Integer>("endHour"));

	}

	//
	/**
	 * @param event Get the area where the marketing manager wants to run sale
	 */
	@FXML
	void areaComboBox_Pressed(ActionEvent event) {
		msgSucc.setVisible(false);
		areaStringSelected = areaComboBox.getValue();

	}

	/**
	 * @param event Get the date when the marketing manager wants to end sale
	 */
	@FXML
	void endDate_Pressed(ActionEvent event) {
		msgSucc.setVisible(false);
		selected_endDate = endDate.getValue();
		endDateString = selected_endDate.toString();

	}

	/**
	 * @param event Get the date when the marketing manager wants to start sale
	 */
	@FXML
	void startDate_Pressed(ActionEvent event) {
		msgSucc.setVisible(false);
		selected_startDate = startDate.getValue();
		startDateString = selected_startDate.toString();

	}

	/**
	 * @param event This mehod will check if all the inputs are corret, if yes, will
	 *              insert the data to DB. else, will show releavant messages.
	 */
	@FXML
	void ActiveSale_Pressed(ActionEvent event) {
		resetsLabels();
		// the inputs are correct
		if (vaildInputs()) {
			insertNewSale(event); // marketing manager is ask for new sale, with correct inputs
		}

	}

	
	/**
	 * resets the labels
	 */
	private void resetsLabels() {
		msgSucc.setVisible(false);
		errEndDate.setVisible(false);
		errSatrtDate.setVisible(false);
		area.setVisible(false);
		errDate.setVisible(false);
		errMarkPattern.setVisible(false);
		
	}

	/**
	 * This function will insert to DB the sale that the marketing manger ask from
	 * the marketing worker.
	 */
	private void insertNewSale(ActionEvent event) {
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("area", areaStringSelected);
		data.put("salePatternID", salePatternTemp.getSalePatternId());
		data.put("startDate", startDateString);
		data.put("endDate", endDateString);
		ServerMessage msg = new ServerMessage(Instruction.add_New_Sale, data);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		; // wait for server update
		commonActionsController.popUp(new Stage(), "New Sale Requested ", "New sale is sent to activation \nIn area : "+areaStringSelected.toString() , "/marketingManagerImages/activeSale.gif", null, event);
		msgSucc.setVisible(true);
	}

	/**
	 * This function goes over the inputs in Active Sale Page, check them, and
	 * return true if corret. otherwise, false
	 */
	private boolean vaildInputs() {
		boolean vaildActiveSale = true;
		// check if choose area
		if (areaStringSelected == null) {
			area.setVisible(true);
			vaildActiveSale = false;
		}

		// checking if the selected dates are relevance
		if (selected_endDate == null || selected_startDate == null) {
			errDate.setVisible(true);
			vaildActiveSale = false;
		} else {
			// check if the start date is earlier than the current date
			if (!(checkTime(selected_startDate, java.time.LocalDate.now(), errSatrtDate)))
				vaildActiveSale = false;

			// check if the end date is earlier than the start date
			if (!(checkTime(selected_endDate, selected_startDate, errEndDate)))
				vaildActiveSale = false;

		}

		// checking if choose one of the sale patterns from the table
		salePatternTemp = SalesPattensView.getSelectionModel().getSelectedItem();
		if (salePatternTemp == null) {
			errMarkPattern.setVisible(true);
			vaildActiveSale = false;
		}
		return vaildActiveSale;
	}

	// This fucntion check if "selected_Date" earlier than "now"
	/**
	 * @param selected_Date
	 * @param now
	 * @param errLabelofDate This fucntion check if "selected_Date" earlier than
	 *                       "now"
	 */
	private boolean checkTime(LocalDate selected_Date, LocalDate now, Label errLabelofDate) {
		boolean vaildFlag = true;
		// check if the start date is earlier than the current date
		if (selected_Date.getYear() < now.getYear()) {
			errLabelofDate.setVisible(true);
			vaildFlag = false;
		}
		if (selected_Date.getYear() == now.getYear()) {
			if (selected_Date.getMonthValue() < now.getMonthValue()) {
				errLabelofDate.setVisible(true);
				vaildFlag = false;
			}
			if (selected_Date.getMonthValue() == now.getMonthValue()) {
				if (selected_Date.getDayOfMonth() < now.getDayOfMonth()) {
					errLabelofDate.setVisible(true);
					vaildFlag = false;
				}
			}
		}
		// otherwise -> correct
		return vaildFlag;
	}

	/**
	 * this method will help the user to use this page
	 * 
	 * @param event
	 */
	@FXML
	void helpPress(ActionEvent event) {
		des1.setVisible(true);
		des2.setVisible(true);
		help.setVisible(false);
	}

	/**
	 * @param event back to pervious page
	 */
	@FXML
	void BackButton_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/MarketingManagerScreen.fxml");
	}

	/**
	 * @param event X Button Pressed method closes the window
	 */
	@FXML
	void Exit_Pressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}

	/**
	 * @param event log Out Button Pressed log out the user 
	 */
	@FXML
	void LogOut_Pressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}

	/**
	 * return ArrayList with SalePattern Objects
	 */
	public static ArrayList<SalePattern> getSalesPatternArray() {
		return salesPatternArray;
	}

	/**
	 * @param salesPatternArray
	 */
	public static void setSalesPatternArray(ArrayList<SalePattern> salesPatternArray) {
		ActivateSalesController.salesPatternArray = salesPatternArray;
	}

}
