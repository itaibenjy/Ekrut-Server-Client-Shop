
package clientGUIControllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import Client.EkrutClientUI;
import Enum.Instruction;
import Enum.ProccessStatus;
import common.CommonActionsController;
import entities.ExecutionInstructions;
import entities.ServerMessage;
import entityControllers.UserController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class gives the option to let the operations worker watch the tasks that are open for him.
 * If he updated inventory in one of the machines, he must update in the system which products he updated inventory.
 * If he has updated all the relevant products in the task, its status will change to 'DONE'.
 * If he updated only some of the products, the task will change to the amount of products,
 * that have not been updated and will still remain open.
 * 
 * This department makes use of the tables:
 * 'executioninstructions', which contains all the tasks to be executed.
 * 'productinfacility', where we will have to update the inventory of each product according to the relevant facility.
 */
public class UpdateInventoryController implements Serializable {
	private static final long serialVersionUID = 1L;

	/* Attributes */

	// controllers:
	private CommonActionsController commonActionsController = new CommonActionsController();
	private UserController userController = new UserController();
	private TransferExecutionInstructionsController transferExecutionInstructionsController = new TransferExecutionInstructionsController();
	// Variables:
	private String selectedProductsToSplit;
	private String updatedProductsToSplit = "";
	private String listProductsToUpdate = "";
	private String facilityName;
	private int taskIDSelecterd;
	private static ArrayList<ExecutionInstructions> executionInstructionsList = new ArrayList<ExecutionInstructions>();
	private static Hashtable<String, Object> valuesTable = new Hashtable<String, Object>();

	boolean isSelectProduct = false;
	// List for table
	private final ObservableList<ExecutionInstructions> tasksTableData = FXCollections.observableArrayList();
	private final ObservableList<OpreatingWorkerTable> productTablesData = FXCollections.observableArrayList();

	/* End Attributes */

	/* FXML */
	// Images
	@FXML
	private ImageView logoImage;
	@FXML
	private ImageView orderImage;
	// labels:
	@FXML
	private Label welcomeToOperatingWorkerLabel;
	@FXML
	private Label ErorMessage;
	@FXML
	private Label OpenTasksLabel;
	@FXML
	private Label prodactsLabel;
	@FXML
	private Label selectLabel;

	@FXML
	private Text help_label;
	// Buttons:
	@FXML
	private Button Exit_Button;
	@FXML
	private Button LogOut;
	@FXML
	private Button UpdateTask;
	@FXML
	private Button UpdateAll;
	@FXML
	private Button Show_Button;
	@FXML
	private Button help_button;
	@FXML
	private Button Back_Button;

	// Task Table(first table):
	// ExecutionInstructions
	@FXML
	private TableView<ExecutionInstructions> tasksTable;
	@FXML
	private TableColumn<ExecutionInstructions, Integer> idExecutionInstruction;
	@FXML
	private TableColumn<ExecutionInstructions, String> facilityId;

	// Product Table(second table):
	// OpreatingWorkerTable
	@FXML
	private TableView<OpreatingWorkerTable> productTables;
	@FXML
	private TableColumn<OpreatingWorkerTable, Integer> ProductID;
	@FXML
	private TableColumn<OpreatingWorkerTable, String> ProductName;
	@FXML
	private TableColumn<OpreatingWorkerTable, CheckBox> checkboxStatus;
	@FXML
	private TableColumn<OpreatingWorkerTable, Integer> newInventory;
	@FXML
	private TableColumn<OpreatingWorkerTable, Integer> oldInventory;

	/**
	 * Initialize
	 */
	@FXML
	private void initialize() {
		setImages();
		getExecutionInstructionsFromDB();
		if (checkexecutionInstructionsListNull() == true) {
			tasksTable.setVisible(false);
			OpenTasksLabel.setVisible(false);
			Show_Button.setVisible(false);
			UpdateAll.setVisible(false);
			UpdateTask.setVisible(false);
			prodactsLabel.setVisible(false);
			OpenTasksLabel.setVisible(true);

		} else {
			setTasksTable();
		}
	}


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
	 * Clicking this button will open a help menu with the actions to be performed on this screen.
	 * @param event
	 */
	@FXML
	void helpButton_Pressed(ActionEvent event) {
		help_button.setVisible(false);
		help_label.setVisible(true);
	}
	/**
	 * Clicking on this button will return us to the previous page from which we arrived.
	 * @param event
	 */
	@FXML
	void BackButton_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/OpreatingWorkerScreen.fxml");
	}

	// Show Button Pressed
	@FXML
	void ShowButton_Pressed(ActionEvent event) {

		// no line select
		if (tasksTable.getSelectionModel().getSelectedItem() == null) {
			this.ErorMessage.setVisible(true);
			this.ErorMessage.setText("You need to select task!");
		} else {
			this.ErorMessage.setVisible(false);
			productTables.setVisible(true);
			UpdateAll.setVisible(true);
			UpdateTask.setVisible(true);
			taskIDSelecterd = tasksTable.getSelectionModel().getSelectedItem().getIdExecutionInstruction();
			facilityName = tasksTable.getSelectionModel().getSelectedItem().getFacilityId();
			selectedProductsToSplit = tasksTable.getSelectionModel().getSelectedItem().getProducts();
			productTablesData.clear();
			showProducts();
		}

	}

	
	/**
	 * Update all tasks
	 * @param event
	 */
	@FXML
	void UpdateAllTasks_Pressed(ActionEvent event) {
		// change status to done
		fullUpdate();
		listProductsToUpdate = selectedProductsToSplit;
		updateProductsInventoryDB();
		refreshScreen();
		productTables.setVisible(false);
		UpdateAll.setVisible(false);
		UpdateTask.setVisible(false);
		isSelectProduct = false;
		sendMailToAreaManger();
	}

	/**
	 * Update specific tasks
	 * @param event
	 */
	@FXML
	void UpdateTask_Pressed(ActionEvent event) {
		// change status to done
		if (allChecked()) {
			fullUpdate();
			listProductsToUpdate = selectedProductsToSplit;
			updateProductsInventoryDB();
			refreshScreen();
			productTables.setVisible(false);
			UpdateAll.setVisible(false);
			UpdateTask.setVisible(false);
			isSelectProduct = false;
			sendMailToAreaManger();

		} else {
			halfUpdate();
			if (isSelectProduct) {
				selectLabel.setVisible(false);
				updateProductsInventoryDB();
				refreshScreen();
				isSelectProduct = false;
			} else {
				selectLabel.setVisible(true);
			}

		}
	}

	/* End FXML */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
	}

	
	/**
	 * Get Execution Instructions From DB
	 */
	private void getExecutionInstructionsFromDB() {
		// Go to DB and get all ExecutionInstructions
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_Execution_Instructions_List, null));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		;
		executionInstructionsList = transferExecutionInstructionsController.getExecutionInstructionsList();
	}

	// set executionInstructionsList from DB
	public void setExecutionInstructionsList(ArrayList<ExecutionInstructions> ExecutionInstructionsList) {
		executionInstructionsList = ExecutionInstructionsList;

	}

	// set values about area manger details from DB
	public static void setValuesTable(Hashtable<String, Object> valuesTable) {
		UpdateInventoryController.valuesTable = valuesTable;
	}

	
	/**
	 * Check if there is relevant missions open to user
	 * @return
	 */
	private boolean checkexecutionInstructionsListNull() {
		for (ExecutionInstructions eInstruction : executionInstructionsList) {
			// mission relevant to this operation worker
			if (userController.getUser().getUserName().equals(eInstruction.getUserName())
					&& eInstruction.getStatus().equals(ProccessStatus.InProccess)) {
				return false;
			}
		}
		return true;
	}

	
	/**
	 * Set the values in TasksTable
	 */
	private void setTasksTable() {
		for (ExecutionInstructions eInstruction : executionInstructionsList) {
			// mission relevant to this operation worker
			if (userController.getUser().getUserName().equals(eInstruction.getUserName())
					&& eInstruction.getStatus().equals(ProccessStatus.InProccess)) {
				ExecutionInstructions addOpLineOpreatingWorkerTable = new ExecutionInstructions(
						eInstruction.getIdExecutionInstruction(), eInstruction.getFacilityId(),
						eInstruction.getProducts());
				// add new object
				tasksTableData.add(addOpLineOpreatingWorkerTable);
				// show relevant details in table
				idExecutionInstruction.setCellValueFactory(
						new PropertyValueFactory<ExecutionInstructions, Integer>("idExecutionInstruction"));
				facilityId.setCellValueFactory(new PropertyValueFactory<ExecutionInstructions, String>("facilityId"));
			}
			tasksTable.setItems(tasksTableData);
		}
	}

	
	/**
	 * Set the values in ProductsTable
	 */
	private void showProducts() {
		// split products string list like this-> "ProductCode, ProductName,
		// OldInventory, NewInventory" -> "2,Cola,5,10"
		// evrey 4 jumps / values its new object
		String[] values = selectedProductsToSplit.split(",");
		for (int i = 0; i < values.length; i += 4) {
			OpreatingWorkerTable olineOpreatingWorkerTable = new OpreatingWorkerTable(Integer.parseInt(values[i]),
					values[i + 1], Integer.parseInt(values[i + 2]), Integer.parseInt(values[i + 3]), new CheckBox());

			ProductID.setCellValueFactory(new PropertyValueFactory<OpreatingWorkerTable, Integer>("ProductID"));
			ProductName.setCellValueFactory(new PropertyValueFactory<OpreatingWorkerTable, String>("ProductName"));
			oldInventory.setCellValueFactory(new PropertyValueFactory<OpreatingWorkerTable, Integer>("oldInventory"));
			newInventory.setCellValueFactory(new PropertyValueFactory<OpreatingWorkerTable, Integer>("newInventory"));
			checkboxStatus
					.setCellValueFactory(new PropertyValueFactory<OpreatingWorkerTable, CheckBox>("checkboxStatus"));
			productTablesData.add(olineOpreatingWorkerTable);
		}
		productTables.setItems(productTablesData);
	}

	
	/**
	 * Run over all checkBox values and return true if all the checkBoxeses selected
	 * @return
	 */
	private boolean allChecked() {
		for (int i = 0; i < productTables.getItems().size(); i++) {
			if (productTables.getItems().get(i).getCheckboxStatus().isSelected() == false) {
				return false;
			}
		}
		return true;
	}

	// Create the half update
	private void halfUpdate() {
		updateProductsList();
		updateExecutionInstruction();
	}

	// Update products field to be with the products that the Operating worker
	// didn't update.
	// create the string that enter to DB
	private void updateProductsList() {
		updatedProductsToSplit = "";
		listProductsToUpdate = "";
		for (OpreatingWorkerTable product : productTablesData) {
			if (product.getCheckboxStatus().isSelected() == false) {
				updatedProductsToSplit += String.valueOf(product.getProductID()) + "," + product.getProductName() + ","
						+ String.valueOf(product.getOldInventory()) + "," + String.valueOf(product.getNewInventory())
						+ ",";
			} else {
				listProductsToUpdate += String.valueOf(product.getProductID()) + "," + product.getProductName() + ","
						+ String.valueOf(product.getOldInventory()) + "," + String.valueOf(product.getNewInventory())
						+ ",";
				isSelectProduct = true;
			}
		}

		// remove last ','
		StringBuilder sb = new StringBuilder(updatedProductsToSplit);
		sb.deleteCharAt(sb.length() - 1);
		updatedProductsToSplit = sb.toString();
		selectedProductsToSplit = updatedProductsToSplit;
		if (isSelectProduct) {
			StringBuilder ls = new StringBuilder(listProductsToUpdate);
			ls.deleteCharAt(ls.length() - 1);
			listProductsToUpdate = ls.toString();
		}
	}

	// Update products field in mysql to be with the products that the Operating
	// worker didn't update
	private void updateExecutionInstruction() {
		try {
			Hashtable<String, Object> data = new Hashtable<>();
			data.put("idExecutionInstruction", taskIDSelecterd);
			data.put("userName", userController.getUser().getUserName());
			data.put("facilityId", facilityName);
			data.put("products", updatedProductsToSplit);
			ServerMessage msg = new ServerMessage(Instruction.Update_Inventory_Execution_Instructions, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Full update,
	// for all products the inventory has been updated,
	// so the status of the task must be changed to done.
	private void fullUpdate() {
		try {
			Hashtable<String, Object> data = new Hashtable<>();
			data.put("idExecutionInstruction", taskIDSelecterd);
			data.put("userName", userController.getUser().getUserName());
			data.put("facilityId", facilityName);
			data.put("products", selectedProductsToSplit);
			data.put("status", "Done");
			ServerMessage msg = new ServerMessage(Instruction.Update_Status_Execution_Instructions, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Partial update,
	// which means that some of the products are selected and therefore
	// we need to change the remaining products to be updated in the task
	private void updateProductsInventoryDB() {
		try {
			Hashtable<String, Object> data = new Hashtable<>();
			data.put("facilityId", facilityName);
			data.put("Products", listProductsToUpdate);
			ServerMessage msg = new ServerMessage(Instruction.Update_Product_Inventory, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshScreen() {
		// refresh the screen
		productTablesData.clear();
		tasksTableData.clear();
		// update theExecutionInstructions
		getExecutionInstructionsFromDB();
		updatedProductsToSplitShownProducts();
		if (checkexecutionInstructionsListNull() == true) {
			tasksTable.setVisible(false);
			OpenTasksLabel.setVisible(false);
			Show_Button.setVisible(false);
			UpdateAll.setVisible(false);
			UpdateTask.setVisible(false);
			prodactsLabel.setVisible(false);
			OpenTasksLabel.setVisible(true);
		} else {
			// set values in tables
			setTasksTable();
			showProducts();
		}

	}

	// Update the 'selectedProductsToSplit' to show the updated list in table
	private void updatedProductsToSplitShownProducts() {
		for (ExecutionInstructions executionInstructions : executionInstructionsList) {
			if (executionInstructions.getIdExecutionInstruction() == taskIDSelecterd) {
				selectedProductsToSplit = executionInstructions.getProducts();
				break;
			}
		}
	}

	/**
	 * 	create "mail" to area manger with the facility area
	 */
	private void sendMailToAreaManger() {
	
		// get facility area from DB
		try {
			Hashtable<String, Object> data = new Hashtable<>();
			data.put("facilityId", facilityName);
			ServerMessage msg = new ServerMessage(Instruction.Get_Facility_Area, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {};
		} catch (Exception e) {
			e.printStackTrace();
		}

		// create "mail" to area manger with the facility area
		commonActionsController.popUp(new Stage(), "Mail send for update",
				"We sent message to Area Manger: " + "Name: " + valuesTable.get("firstName") + " "
						+ valuesTable.get("lastName") + "." + "\nTo mail: " + valuesTable.get("email")
						+ "\nAnd message sent yo his phone: " + valuesTable.get("telephone") + "."
						+ "\nabout the inventory updates.",
				null, null, null);

	}

	/**
	 *	 new class for table in OperatingWorkerController
	 */
	public class OpreatingWorkerTable implements Serializable {

		private static final long serialVersionUID = 1L;
		private int ProductID;
		private String ProductName;
		private int oldInventory;
		private int newInventory;
		private CheckBox checkboxStatus;

		public OpreatingWorkerTable(int productID, String productName, int oldInventory, int newInventory,
				CheckBox checkboxStatus) {
			super();
			ProductID = productID;
			ProductName = productName;
			this.oldInventory = oldInventory;
			this.newInventory = newInventory;
			this.checkboxStatus = checkboxStatus;
		}

		public int getProductID() {
			return ProductID;
		}

		public void setProductID(int productID) {
			ProductID = productID;
		}

		public String getProductName() {
			return ProductName;
		}

		public void setProductName(String productName) {
			ProductName = productName;
		}

		public int getOldInventory() {
			return oldInventory;
		}

		public void setOldInventory(int oldInventory) {
			this.oldInventory = oldInventory;
		}

		public int getNewInventory() {
			return newInventory;
		}

		public void setNewInventory(int newInventory) {
			this.newInventory = newInventory;
		}

		public CheckBox getCheckboxStatus() {
			return checkboxStatus;
		}

		public void setCheckboxStatus(CheckBox checkboxStatus) {
			this.checkboxStatus = checkboxStatus;
		}

	}

}
