
package clientGUIControllers;

import java.util.ArrayList;
import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Instruction;
import common.CommonActionsController;
import entities.Facility;
import entities.ProductInFacility;
import entities.ServerMessage;
import entityControllers.AreaManagerEntityController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This department gives the option to let the area manager change the threshold
 * level of each product in the facility located in the area he is responsible
 * for.
 * 
 * It will use the details of the area manager - which facilities exist for his
 * area, and information about all the products in each facility.
 */
public class DefineThresholdLevelController {

	/* Attributes */
	// controllers:
	private AreaManagerEntityController entity_areaManagerController = new AreaManagerEntityController();
	private CommonActionsController commonActionsController = new CommonActionsController();
	// Variables:
	private String ThresholdLevelSelected;
	private String facilityNameStringSelected;
	private String productNameSelected;
	private int ProductCodeSelected;
	private int productThresholdLevelSelected;
	private Facility chosenFacility;
	// Lists for combBox:
	private ObservableList<String> facilityList = FXCollections.observableArrayList();
	private ObservableList<String> productList = FXCollections.observableArrayList();
	/* End Attributes */

	/* FXML */
	// Images
	@FXML
	private ImageView logoImage;

	// TextField:
	@FXML
	private TextField EnterQuantity_TextBox;
	// labels:
	@FXML
	private Label noProductsLabel;
	@FXML
	private Label NoFacilitiesAvailable;
	@FXML
	private Label currectLabel;
	@FXML
	private Label ChooseThresholdLevel_Label;
	@FXML
	private Label currectThresholdLevel;
	@FXML
	private Label ErorMessage;
	@FXML
	private Label ChoseProduct_Label;

	@FXML
	private Text help_label;

	// Buttons
	@FXML
	private Button Back_Button;
	@FXML
	private Button LogOut;
	@FXML
	private Button Exit_Button;
	@FXML
	private Button SetThresholdLevel_Button;
	@FXML
	private Button help_button;

	// comboBoxes
	@FXML
	private ComboBox<String> ChoseFacility_ComboBox;
	@FXML
	private ComboBox<String> selectProduct_ComboBox;

	/**
	 * Clicking on this button will return us to the previous page from which we
	 * arrived.
	 * 
	 * @param event
	 */
	@FXML
	void BackButton_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/AreaManagerScreen.fxml");
	}

	/**
	 * Pressing this button will completely take us out of the screen.
	 * 
	 * @param event
	 */
	@FXML
	void Exit_Pressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}

	/**
	 * Clicking this button will disconnect the user from the system and take him to
	 * the login screen.
	 * 
	 * @param event
	 */
	@FXML
	void LogOut_Pressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}

	/**
	 * Clicking this button will open a help menu with the actions to be performed
	 * on this screen.
	 * 
	 * @param event
	 */
	@FXML
	void helpButton_Pressed(ActionEvent event) {
		help_button.setVisible(false);
		help_label.setVisible(true);
	}

	/**
	 * Initialize the comboBoxes
	 * 
	 * @throws InterruptedException
	 */
	@FXML
	private void initialize() throws InterruptedException {
		setImages();
		if (entity_areaManagerController.getAreaManager().getFacilities().isEmpty()) {
			NoFacilitiesAvailable.setVisible(true);
		}
		setFacilitiesInComboBox();
		// set the drop down levels
		selectProduct_ComboBox.setVisibleRowCount(5);
		ChoseFacility_ComboBox.setVisibleRowCount(5);
	}

	
	/**
	 * choose facility pressed
	 * @param event
	 */
	@FXML
	void ChoseFacilityComboBox_Pressed(ActionEvent event) {
		facilityNameStringSelected = ChoseFacility_ComboBox.getValue();
		selectProduct_ComboBox.setDisable(false);
		productEmpty(true);
		noProductsLabel.setVisible(false);
		setProductsInComboBox();
	}

	
	/**
	 * choose product pressed
	 * @param event
	 */
	@FXML
	void selectProductComboBox_Pressed(ActionEvent event) {
		productNameSelected = selectProduct_ComboBox.getValue();
		EnterQuantity_TextBox.setDisable(false);
		SetThresholdLevel_Button.setDisable(false);

		getProductNameAndThresholdLevel();
		currectThresholdLevel.setText(String.valueOf(productThresholdLevelSelected));
		currectThresholdLevel.setVisible(true);
	}

	
	/**
	 * Set Threshold Level Pressed
	 * @param event
	 * @throws Exception
	 */
	@FXML
	void SetThresholdLevelButton_Pressed(ActionEvent event) throws Exception {
		ThresholdLevelSelected = EnterQuantity_TextBox.getText();
		// check field value
		if (checkIfQuantityContainsOnlyNumbers(ThresholdLevelSelected) == true) {
			setThresholdLevel();
			this.ErorMessage.setVisible(false);
			commonActionsController.popUp(new Stage(), "The updatde succeed", "New Threshold Level Update ", null,
					"/clientGUIScreens/AreaManagerScreen.fxml", event);

		}
	}

	/* End FXML */

	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));

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
	 * set the product comboBox list
	 */
	private void setProductsInComboBox() {

		// get the chosen facility from the area manger
		for (Facility facility : entity_areaManagerController.getAreaManager().getFacilities()) {
			if (facility.getFacilityId().equals(facilityNameStringSelected)) {
				chosenFacility = facility;
				break;
			}
		}
		// run over all products in facility an add there names to Observe arr
		for (ProductInFacility productsInFacilities : chosenFacility.getProductsInFacility()) {
			productList.add(productsInFacilities.getProtductName());
		}
		if (productList.isEmpty()) {
			productEmpty(false);
			noProductsLabel.setVisible(true);
		} else {
			// add observe arr and set the items
			selectProduct_ComboBox.setItems(productList);
		}

	}

	
	/**
	 * in case product list is empty -> no access to rest of the screen
	 * @param toSet
	 */
	private void productEmpty(boolean toSet) {
		ChoseProduct_Label.setVisible(toSet);
		ChooseThresholdLevel_Label.setVisible(toSet);
		currectLabel.setVisible(toSet);
		EnterQuantity_TextBox.setVisible(toSet);
		selectProduct_ComboBox.setVisible(toSet);
		currectThresholdLevel.setVisible(toSet);
		SetThresholdLevel_Button.setVisible(toSet);

	}

	
	/**
	 *  press set -> set ThresholdLevel and save it in DB
	 *  A function that will define the raising of a threshold in a database after pressing SET
	 * @throws Exception
	 */
	private void setThresholdLevel() throws Exception {
		try {
			Hashtable<String, Object> data = new Hashtable<>();
			data.put("facilityName", this.facilityNameStringSelected);
			data.put("ProductCode", this.ProductCodeSelected);
			data.put("ThresholdLevel", this.ThresholdLevelSelected);
			ServerMessage msg = new ServerMessage(Instruction.Set_Product_ThresholdLevel, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * get product code and ThresholdLevelSelected from the name that selected
	 */
	private void getProductNameAndThresholdLevel() {
		for (ProductInFacility product : chosenFacility.getProductsInFacility()) {
			if (productNameSelected.equals(product.getProtductName())) {
				ProductCodeSelected = product.getProductCode();
				productThresholdLevelSelected = product.getTresholdLevel();
				break;
			}
		}
	}

	
	/**
	 * Spell check input for Threshold Level Selected
	 * @param EnterQuantity_TextBox
	 * @return
	 */
	private boolean checkIfQuantityContainsOnlyNumbers(String EnterQuantity_TextBox) {
		// Empty value
		if (this.EnterQuantity_TextBox.getText().isEmpty()) {
			this.ErorMessage.setVisible(true);
			this.ErorMessage.setText("Must enter a value!");
			return false;
		}

		// Check that the field contain only digits
		for (int i = 0; i < EnterQuantity_TextBox.length(); i++) {
			// there is char in the field
			if (!Character.isDigit(EnterQuantity_TextBox.charAt(i))) {
				this.ErorMessage.setVisible(true);
				this.ErorMessage.setText("Quantity must consist of only numbers!");
				return false;
			}
		}

		// Quantity cannot be greater than 100
		int val = Integer.parseInt(EnterQuantity_TextBox);
		if (val > 100) {
			this.ErorMessage.setVisible(true);
			this.ErorMessage.setText("Quantity cannot be greater than 100!");
			return false;
		}
		return true;
	}
}