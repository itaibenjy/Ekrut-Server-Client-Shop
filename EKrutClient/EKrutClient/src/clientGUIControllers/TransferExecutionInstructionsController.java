/**
 * This Class gives the option to let the Area manager add a new task
 * and selected operation employee will have to go and update the actual inventory in the facility.
 * 
 * The area manager selects a facility where he wants to perform an inventory update,
 * and selects from all the products that exist in the company,
 * the new inventory he wants to have in the facility.
 * 
 * If the product does not exist in the facility, we will re-add it to the facility.
 * 
 * We will note that the system will no longer allow one execution request per facility for one employee.
 * That is, if the operations employee has an execution request to update inventory,
 * and the Area manager requests to change inventory to new products in this facility,
 * the request will be updated accordingly and a new request will not be opened for that facility.
 * 
 * The department uses the following tables:
 * 
 * 'product', in order to view all the products in the company and from which to choose which product to update inventory in the facility.
 * 'user', in order to view all the operating employees existing in the company and from them to choose one employee who will perform the task.
 * Information is used for the area manager which includes machines in his area, and products in each machine
 */
package clientGUIControllers;

import java.util.ArrayList;
import java.util.Hashtable;

import Client.EkrutClientUI;
import Enum.Instruction;
import common.CommonActionsController;
import entities.ExecutionInstructions;
import entities.Facility;
import entities.OpreatingWorkerTableInAreaManger;
import entities.Product;
import entities.ProductInFacility;
import entities.ServerMessage;
import entities.User;
import entityControllers.AreaManagerEntityController;
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
import javafx.stage.Stage;

public class TransferExecutionInstructionsController {

	/* Attributes */

	// controllers:
	private CommonActionsController commonActionsController = new CommonActionsController();
	private AreaManagerEntityController entity_areaManagerController = new AreaManagerEntityController();
	private ViewCatalogController catalogController = new ViewCatalogController();
	// Variables:
	private String facilityNameStringSelected;
	private String productNameSelected;
	private String newInventoryChoose;
	private String workerUsername;
	private String workerNamwWithIDSelected;
	private String productsStringToDB = "";
	private int workerID;
	private Facility chosenFacility;
	private static ArrayList<Product> productsList = new ArrayList<Product>();
	private static ArrayList<User> operatingWorkerList = new ArrayList<User>();
	private static ArrayList<ExecutionInstructions> executionInstructionsList = new ArrayList<ExecutionInstructions>();
	private int currectInventory;
	private int plusMinusCounter = 0;
	boolean firstChooseFcility = false;
	boolean firstChooseWorker = false;
	// Lists for combBox:
	private ObservableList<String> facilityList = FXCollections.observableArrayList();
	private ObservableList<String> productList = FXCollections.observableArrayList();
	private ObservableList<String> operatingWorkers = FXCollections.observableArrayList();
	// List for table
	private final ObservableList<OpreatingWorkerTableInAreaManger> dataToTable = FXCollections.observableArrayList();
	/* End Attributes */

	/* FXML */

	// TextField
	@FXML
	private TextField EnterQuantity_TextBox;
	
	// Images
	@FXML
    private ImageView logoImage;
	
	// labels
	@FXML
	private Label ChooseInventoryValue_Label;
	@FXML
	private Label ChoseProduct_Label;
	@FXML
	private Label ErorMessage;
	@FXML
	private Label ErorMessage_line;
	@FXML
	private Label currectInventoryValue;
	@FXML
	private Label NoFacilityLabel;
	@FXML
	private Label NoProductsLabel;
	@FXML
	private Label NoWorkersLabel;
	
	@FXML
    private Text help_label;

	// Buttons
	@FXML
	private Button Exit_Button;
	@FXML
	private Button LogOut;
	@FXML
	private Button SendRequestUpdate_Button;
	@FXML
	private Button addValueToTable_Button;
	@FXML
	private Button removeValueFromTable_Button;
	@FXML
	private Button Back_Button;
	@FXML
	private Button help_button;

	// comboBoxes
	@FXML
	private ComboBox<String> ChoseFacility_ComboBox;
	@FXML
	private ComboBox<String> selectProduct_ComboBox;
	@FXML
	private ComboBox<String> ChooseWorkerComboBox;

	// Table
	@FXML
	private TableView<OpreatingWorkerTableInAreaManger> tableShow;
	@FXML
	private TableColumn<OpreatingWorkerTableInAreaManger, String> faclitityName;
	@FXML
	private TableColumn<OpreatingWorkerTableInAreaManger, String> productName;
	@FXML
	private TableColumn<OpreatingWorkerTableInAreaManger, Integer> newInventory;
	@FXML
	private TableColumn<OpreatingWorkerTableInAreaManger, Integer> oldInventory;
	@FXML
	private TableColumn<OpreatingWorkerTableInAreaManger, String> worker;

	// Events
	/**
	 * Clicking on this button will return us to the previous page from which we arrived.
	 * @param event
	 */
	@FXML
	void BackButton_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/AreaManagerScreen.fxml");
	}
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
	 * Clicking this button will open a help menu with the actions to be performed on this screen.
	 * @param event
	 */
	 @FXML
	    void helpButton_Pressed(ActionEvent event) {
	    	help_button.setVisible(false);
	    	help_label.setVisible(true);
	    }
	
	/**
	 * Initialize the comboBoxes
	 * @throws InterruptedException
	 */
	@FXML
	private void initialize() throws InterruptedException {
		setImages();
		// Go to DB and get values of products
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_all_Product_list, null));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		productsList = catalogController.getProductsList();

		setFacilitiesInComboBox();
		setProductsInComboBox();

		// check if comboBoxes empty
		if (entity_areaManagerController.getAreaManager().getFacilities().isEmpty()) {
			NoFacilityLabel.setVisible(true);
		}
		if (productsList.isEmpty()) {
			NoProductsLabel.setVisible(true);
		}
		// set the comboBoxes drop down levels to be 5
		selectProduct_ComboBox.setVisibleRowCount(5);
		ChoseFacility_ComboBox.setVisibleRowCount(5);
		ChooseWorkerComboBox.setVisibleRowCount(5);
	}

	/**
	 * Facilities comboBox Pressed
	 * @param event
	 */
	@FXML
	void ChoseFacilityComboBox_Pressed(ActionEvent event) {
		// Go to DB and get all workers
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_Operating_Workers, null));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		setoperatingWorkerInComboBox();
		if (operatingWorkerList.isEmpty()) {
			NoWorkersLabel.setVisible(true);
		}
		
		facilityNameStringSelected = ChoseFacility_ComboBox.getValue();
		selectProduct_ComboBox.setDisable(false);
		// get the chosen facility from the area manger
		for (Facility facility : entity_areaManagerController.getAreaManager().getFacilities()) {
			if (facility.getFacilityId().equals(facilityNameStringSelected)) {
				chosenFacility = facility;
				break;
			}

			// change the currectInventoryValue only after 1 time that he get at least one
			// product
			if (firstChooseFcility == true) {
				getProductNameCodeQuantity();
				currectInventoryValue.setText(String.valueOf(currectInventory));
			}
		}
	}


	/**
	 * Products comboBox Pressed
	 * @param event
	 */
	@FXML
	void selectProductComboBox_Pressed(ActionEvent event) {
		// Go to DB and get all ExecutionInstructions
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_Execution_Instructions_List, null));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		
		productNameSelected = selectProduct_ComboBox.getValue();
		EnterQuantity_TextBox.setDisable(false);
		firstChooseFcility = true;
		// first time to choose worker
		if (firstChooseWorker == false) {
			ChooseWorkerComboBox.setDisable(false);
		}
		firstChooseWorker = true;
		getProductNameCodeQuantity();
		currectInventoryValue.setText(String.valueOf(currectInventory));
		currectInventoryValue.setVisible(true);
	}

	
	/**
	 * Operation workers comboBox Pressed
	 * @param event
	 */
	@FXML
	void ChooseWorkerComboBox_Pressed(ActionEvent event) {

		addValueToTable_Button.setDisable(false);
		workerNamwWithIDSelected = ChooseWorkerComboBox.getValue();
		// to get user name we need to run over operatingWorkerList and get the username
		// by ID that we split from the chosen worker in the comboBox
		String[] sentences = workerNamwWithIDSelected.split(", ID: ");
		workerID = Integer.parseInt(sentences[1]);
		for (User worker : operatingWorkerList) {
			if (workerID == worker.getId()) {
				workerUsername = worker.getUserName();
				break;
			}
		}
	}

	
	/**
	 * Add button action
	 * @param event
	 */
	@FXML
	void addValueToTable_pressed(ActionEvent event) {
		newInventoryChoose = EnterQuantity_TextBox.getText();
		// Check correct spell
		if (checkIfQuantityContainsOnlyNumbers(newInventoryChoose) == true) {
			plusMinusCounter += 1;
			this.ErorMessage.setVisible(false);
			addLineToTable();
			removeValueFromTable_Button.setDisable(false);
			SendRequestUpdate_Button.setDisable(false);

		}

	}

	/**
	 * Clicking this button will download a product from the table.
	 * @param event
	 */
	@FXML
	void removeValueFromTable_Pressed(ActionEvent event) {
		removeLineFromTable();
		// No lines in table
		if (plusMinusCounter == 0) {
			removeValueFromTable_Button.setDisable(true);
			ChooseWorkerComboBox.setDisable(false);
			ChoseFacility_ComboBox.setDisable(false);
			selectProduct_ComboBox.setPromptText("Choose Product");
		}
	}

	/**
	 * Clicking this button will send a new request to perform an inventory update.
	 * @param event
	 * @throws Exception
	 */
	@FXML
	void SendRequestUpdateButton_Pressed(ActionEvent event) throws Exception {
		if (plusMinusCounter == 0) {
			this.ErorMessage_line.setVisible(true);
			this.ErorMessage_line.setText("You neet to add 1 request at least");
		}
		// get the values and update it to DB
		else {
			for (OpreatingWorkerTableInAreaManger opreatingWorkerTable : dataToTable) {
				setStringProducts(opreatingWorkerTable);
			}
			// remove last ','
			StringBuilder sb = new StringBuilder(productsStringToDB);
			sb.deleteCharAt(sb.length() - 1);
			productsStringToDB = sb.toString();
			// Update DB execution Instructions
			if (checkLocationAndProcessUpdaeIt() == true) {
				commonActionsController.popUp(new Stage(), "The updatde succeed", "New Execution Instructions added ",
						null, "/clientGUIScreens/AreaManagerScreen.fxml", event);

			} else {
				// Set new execution Instructions line in DB
				setRequestInDB();
				commonActionsController.popUp(new Stage(), "The updatde succeed", "New Execution Instructions added ",
						null, "/clientGUIScreens/AreaManagerScreen.fxml", event);
			}
		}
	}

	/* End FXML */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));	
	}
	// set operatingWorkerList from DB
	public void setOperatingWorkerList(ArrayList<User> OperatingWorkerList) {
		operatingWorkerList = OperatingWorkerList;

	}

	// set executionInstructionsList from DB
	public void setExecutionInstructionsList(ArrayList<ExecutionInstructions> ExecutionInstructionsList) {
		executionInstructionsList = ExecutionInstructionsList;

	}

	// get executionInstructionsList
	public ArrayList<ExecutionInstructions> getExecutionInstructionsList() {
		return executionInstructionsList;
	}

	
	/**
	 * set the facility comboBox list
	 */
	private void setFacilitiesInComboBox() {
		// get the AreaManger facilities
		ArrayList<Facility> facilities = entity_areaManagerController.getAreaManager().getFacilities();
		// Add all facilities name to Observe arr
		for (Facility facility : facilities) {
			if (!(facility.getFacilityId().equals("OperationCenterNorth"))
					&& !(facility.getFacilityId().equals("OperationCenterUAE"))
					&& !(facility.getFacilityId().equals("OperationCenterSouth"))) {
				facilityList.add(facility.getFacilityId());
			}
		}
		// add observe arr and set the items
		ChoseFacility_ComboBox.setItems(facilityList);
	}

	
	/**
	 * Set the product comboBox list
	 */
	private void setProductsInComboBox() {

		// run over all products in facility an add there names to Observe arr
		for (Product product : productsList) {
			productList.add(product.getProtductName());
		}
		// add observe arr and set the items
		selectProduct_ComboBox.setItems(productList);
	}

	
	/**
	 * Get product code and quantity from the name that selected
	 */
	private void getProductNameCodeQuantity() {
		// product exist in facility
		for (ProductInFacility product : chosenFacility.getProductsInFacility()) {
			if (productNameSelected.equals(product.getProtductName())) {
				currectInventory = product.getQuantity();
				return;
			}
		}
		// product didn't found in facility so we need to set quantity to 0
		currectInventory = 0;
	}

	
	/**
	 * Spell check input for inventory value Selected
	 * @param newInventory
	 * @return
	 */
	private boolean checkIfQuantityContainsOnlyNumbers(String newInventory) {
		// Empty value
		if (this.EnterQuantity_TextBox.getText().isEmpty()) {
			this.ErorMessage.setVisible(true);
			this.ErorMessage.setText("Must enter a value!");
			return false;
		}

		// Check that the field contain only digits
		for (int i = 0; i < newInventory.length(); i++) {
			// there is char in the field
			if (!Character.isDigit(newInventory.charAt(i))) {
				this.ErorMessage.setVisible(true);
				this.ErorMessage.setText("Quantity must consist of only numbers!");
				return false;
			}
		}

		// Quantity cannot be greater than 100
		int val = Integer.parseInt(newInventory);
		if (val > 100) {
			this.ErorMessage.setVisible(true);
			this.ErorMessage.setText("Quantity cannot be greater than 100!");
			return false;
		}

		return true;
	}

	
	/**
	 * Set the operating Worker comboBox list
	 */
	private void setoperatingWorkerInComboBox() {
		String nameToShow;
		// Add all facilities name to Observe arr
		for (User opWorker : operatingWorkerList) {
			nameToShow = opWorker.getFirstName() + ", ID: " + opWorker.getId();
			operatingWorkers.add(nameToShow);
		}
		// add observe arr and set the items
		ChooseWorkerComboBox.setItems(operatingWorkers);
	}

	
	/**
	 * Add new line to table
	 */
	private void addLineToTable() {
		OpreatingWorkerTableInAreaManger addOW = new OpreatingWorkerTableInAreaManger(chosenFacility.getFacilityId(),
				productNameSelected, workerNamwWithIDSelected, currectInventory, Integer.parseInt(newInventoryChoose));
		// case the table contain the line, can't add line:
		if (dataToTable.contains(addOW)) {
			this.ErorMessage_line.setVisible(true);
			this.ErorMessage_line.setText("The product already exists, please choose another product!");
			plusMinusCounter--;

			return;
		}

		// case the table success to update the line:
		// set items to observe list and add them to the table

		dataToTable.add(addOW);
		faclitityName.setCellValueFactory(
				new PropertyValueFactory<OpreatingWorkerTableInAreaManger, String>("faclitityName"));
		productName
				.setCellValueFactory(new PropertyValueFactory<OpreatingWorkerTableInAreaManger, String>("productName"));
		oldInventory.setCellValueFactory(
				new PropertyValueFactory<OpreatingWorkerTableInAreaManger, Integer>("oldInventory"));
		newInventory.setCellValueFactory(
				new PropertyValueFactory<OpreatingWorkerTableInAreaManger, Integer>("newInventory"));
		worker.setCellValueFactory(new PropertyValueFactory<OpreatingWorkerTableInAreaManger, String>("Worker"));
		tableShow.setItems(dataToTable);
		this.ErorMessage_line.setVisible(false);
		// Products can only be added to one facility
		ChoseFacility_ComboBox.setDisable(true);
		// Can't choose more than 1 worker to job
		ChooseWorkerComboBox.setDisable(true);
	}

	/**
	 * remove Line FromTable
	 */
	private void removeLineFromTable() {
		OpreatingWorkerTableInAreaManger lineToDelete = tableShow.getSelectionModel().getSelectedItem();
		tableShow.getItems().remove(lineToDelete);
		if (lineToDelete != null)
			plusMinusCounter--;
	}

	
	/**
	 * Get the product code
	 * @param productName
	 * @return
	 */
	private String getProductCode(String productName) {
		for (Product product : productsList) {
			if (product.getProtductName().equals(productName)) {
				return String.valueOf(product.getProductCode());

			}
		}
		return "";
	}

	
	/**
	 * Set new string to products list in DB -> "ProductCode, ProductName,
	 * OldInventory, NewInventory" -> "2,Cola,5,10"
	 * @param op
	 */
	private void setStringProducts(OpreatingWorkerTableInAreaManger op) {
		productsStringToDB += this.getProductCode(op.getProductName()) + "," + op.getProductName() + ","
				+ String.valueOf(op.getOldInventory() + ",") + String.valueOf(op.getNewInventory() + ",");
	}

	
	/**
	 * Check if Execution Instructions with same operation worker, in specific
	 * facility, and steel in process so need to update it and not add another one
	 * @return
	 */
	private boolean checkLocationAndProcessUpdaeIt() {
		for (ExecutionInstructions ei : executionInstructionsList) {
			if (ei.getFacilityId().equals(facilityNameStringSelected)
					&& String.valueOf(ei.getStatus()).equals("InProccess")) {
				try {
					Hashtable<String, Object> data = new Hashtable<>();
					data.put("idExecutionInstruction", ei.getIdExecutionInstruction());
					data.put("oldUserName", ei.getUserName());
					data.put("newUserName", workerUsername);
					data.put("facilityId", ei.getFacilityId());
					data.put("products", productsStringToDB);
					ServerMessage msg = new ServerMessage(Instruction.Update_Execution_Instructions, data);
					EkrutClientUI.chat.accept(msg);
					while (!EkrutClientUI.chat.isServerMsgRecieved()) {
					}
					;
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	
	/**
	 * Press set -> set new ExecutionInstructions and save it in DB pressing send
	 * @throws Exception
	 */
	private void setRequestInDB() throws Exception {
		try {
			Hashtable<String, Object> data = new Hashtable<>();
			data.put("userName", workerUsername);
			data.put("facilityId", facilityNameStringSelected);
			data.put("products", productsStringToDB);
			data.put("status", "InProccess");
			ServerMessage msg = new ServerMessage(Instruction.Set_New_Execution_Instructions, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
