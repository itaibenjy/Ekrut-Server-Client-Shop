package clientGUIControllers;

/**
 * This class gives the option to Marketing Manger to add sale patten.
 * He also can view all current sale patterns
 * If he updated sale pattern in our company, he must update in the system.
 *
 * 
 * This department makes use of the tables:
 * * 'SalesPatterns', where we will have to update a new sale pattern if the Marketing Manger  will add one.
 */

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class SalesPatternsController {

	@FXML
	private Button Back_Button;

	@FXML
	private Button LogOut;

	@FXML
	private Button Exit_Button;

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
	private TextField description_field;

	@FXML
	private TextField discount_field;

	@FXML
	private Button addPattterm;

	@FXML
	private Label errTime;

	@FXML
	private Label ErorMessage;

	@FXML
	private Label errDiscription;

	@FXML
	private Label errHour;

	@FXML
	private ComboBox<Integer> startHour_ComboBox;

	@FXML
	private ComboBox<Integer> endHour_ComboBox;
	
    @FXML
    private Text des1;
    
    @FXML
    private Text des2;

	@FXML
	private ImageView logoImage;

	@FXML
	private Button help;
	

    @FXML
    private ImageView salePatterngif;

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

	int startHourSelected = 0, endHourSelected = 0;
	String discount_select;
	private CommonActionsController commonActionsController = new CommonActionsController();
	private static ArrayList<SalePattern> salesPatternArray = new ArrayList<SalePattern>();

	// hours of sales that can be
	private ObservableList<Integer> strtTime = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
	private ObservableList<Integer> endTime = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
			13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24);

	/**
	 * init the values in the ComboBoxs and page`s images
	 */
	@FXML
	private void initialize() {
		setImages();
		// intial the data in combo box
		startHour_ComboBox.setItems(strtTime);
		endHour_ComboBox.setItems(endTime);
		createSalePatternTable();

	}

	/**
	 * Set images
	 */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		salePatterngif.setImage(new Image("/marketingManagerImages/salePattern.gif"));
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

	/**
	 * @param event when the Marketing Manger choose to add new sale patten
	 */
	@FXML
	void addPattterm_Pressed(ActionEvent event) {
		boolean vaildData = true; // check if the inputs data correct
		resetsLabels();

		if (startHourSelected == 0 || endHourSelected == 0) {
			errHour.setVisible(true);
		} else { // check that the end hour of the sale wont be earlier than the start hour
			if (endHour_ComboBox.getValue() < startHour_ComboBox.getValue()) {
				errTime.setVisible(true);
				vaildData = false;
			}
		}

		discount_select = discount_field.getText();
		// Check correct spell
		if (!checkIfContainsOnlyNumbers(discount_select))
			vaildData = false;

		if (description_field.getText().isEmpty()) {
			errDiscription.setVisible(true);
			vaildData = false;
		}

		if (vaildData) // the inputs its ok
			insertSalePattern();
		// update The Table
		createSalePatternTable();

	}

	/**
	 * resets the labels
	 */
	private void resetsLabels() {
		errTime.setVisible(false);
		ErorMessage.setVisible(false);
		errDiscription.setVisible(false);
		errHour.setVisible(false);
		
	}

	/**
	 * this function insert to DataBase new sale patten that the marketing manager
	 * want
	 */
	private void insertSalePattern() {
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("description", description_field.getText());
		data.put("discount", Integer.parseInt(discount_select));
		data.put("starthour", startHour_ComboBox.getValue());
		data.put("endhour", endHour_ComboBox.getValue());
		ServerMessage msg = new ServerMessage(Instruction.set_Sales_Pattern, data);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		; // wait for server update
	}

	/**
	 * Spell check input for discount value Selected return yes if its correct,
	 * otherwise, false
	 */
	private boolean checkIfContainsOnlyNumbers(String discount) {
		// Empty value
		if (this.discount_field.getText().isEmpty()) {
			this.ErorMessage.setVisible(true);
			this.ErorMessage.setText("Must enter a value!");
			return false;
		}

		// Check that the field contain only digits
		for (int i = 0; i < discount.length(); i++) {
			// there is char in the field
			if (!Character.isDigit(discount.charAt(i))) {
				this.ErorMessage.setVisible(true);
				this.ErorMessage.setText("Discount must consist of only numbers!");
				return false;
			}
		}

		// the sale have to be between 0 to 100
		int val = Integer.parseInt(discount);
		if (val >= 100 || val <= 0) {
			this.ErorMessage.setVisible(true);
			this.ErorMessage.setText("Discount cannot be greater than 100 and less than 0!");
			return false;
		}

		return true;
	}

	/**
	 * @param event insert the end hour of the sale
	 */
	@FXML
	void endHour_ComboBox_Pressed(ActionEvent event) {
		endHourSelected = endHour_ComboBox.getValue();
	}

	/**
	 * @param event insert the start hour of the sale
	 */
	@FXML
	void startHour_ComboBox_Pressed(ActionEvent event) {
		startHourSelected = startHour_ComboBox.getValue();
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
	 * @return ArrayList of type SalePattern 
	 */
	public static ArrayList<SalePattern> getSalesPatternArray() {
		return salesPatternArray;
	}

	/**
	 * @param salesPatternArray
	 * set ArrayList of type SalePattern
	 */
	public static void setSalesPatternArray(ArrayList<SalePattern> salesPatternArray) {
		SalesPatternsController.salesPatternArray = salesPatternArray;
	}

}
