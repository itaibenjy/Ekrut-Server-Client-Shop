package clientGUIControllers;

import java.util.ArrayList;

import Enum.ProductCategory;
import common.CommonActionsController;
import entities.Facility;
import entities.ProductInFacility;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * This class gives the option to Area Manager to see the inventory of each
 * facility in his area that have not been updated and will still remain open.
 * 
 * This department makes use of the tables: 'facility', where we see which
 * facility in his area. 'productinfacility', where we can see inventory of each
 * product according to the relevant facility.  
 */

public class ViewUpdatedFacilityInventoryController {
	@FXML
	private Label errLabel;

	@FXML
	private Button Back_Button;

	@FXML
	private Button Exit_Button;

	@FXML
	private ComboBox<String> ChoseFacility_ComboBox;

	@FXML
	private Label area;

	@FXML
	private Button LogOut;

	@FXML
	private Label errFacility;

	@FXML
	private TableView<ProductInFacility> UpdatedProductsInventory;

	@FXML
	private TableColumn<ProductInFacility, Integer> productCode;

	@FXML
	private TableColumn<ProductInFacility, String> protductName;

	@FXML
	private TableColumn<ProductInFacility, Integer> quantity;

	@FXML
	private TableColumn<ProductInFacility, ProductCategory> category;

	@FXML
	private Text descriptionPage;

	@FXML
	private ImageView ekrut;

	@FXML
	private ImageView areaImage;

	@FXML
	private ImageView facilityImage;

	@FXML
	private Button help;
	

	// Entities Controllers
	private AreaManagerEntityController entity_areaManagerController = new AreaManagerEntityController();
	private CommonActionsController commonActionsController = new CommonActionsController();

	private ArrayList<Facility> facilities = entity_areaManagerController.getAreaManager().getFacilities();
	private final ObservableList<String> facilityList = FXCollections.observableArrayList();
	private Facility chosenFacility;
	private String facilityNameStringSelected;
	private final ObservableList<ProductInFacility> dataOfProducts = FXCollections.observableArrayList();

	@FXML
	private void initialize() {
		setImages();
		UpdatedProductsInventory.setVisible(false);
		this.area.setText(entity_areaManagerController.getAreaManager().getArea().toString());

		setListOfFacilities();

	}

	/**
	 * Set images
	 */
	private void setImages() {
		areaImage.setImage(new Image("/location.png"));
		ekrut.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		facilityImage.setImage(new Image("/areaManagerImages/building.png"));

	}

	/**
	 * set relevant facilities in comboBox
	 */
	private void setListOfFacilities() {
		for (Facility Val : facilities) {
			if (!(Val.getFacilityId().equals("OperationCenterNorth"))
					&& !(Val.getFacilityId().equals("OperationCenterUAE"))
					&& !(Val.getFacilityId().equals("OperationCenterSouth")))
				facilityList.add(Val.getFacilityId());
		}

		if (facilityList.isEmpty()) {
			ChoseFacility_ComboBox.setVisible(false);
			errFacility.setVisible(true);
		}
			
		else
			ChoseFacility_ComboBox.setItems(facilityList);
	}

	/**
	 * @param event get the requested facility from the area manager
	 */
	@FXML
	void ChoseFacilityComboBox_Pressed(ActionEvent event) {
		errLabel.setVisible(false);
		UpdatedProductsInventory.setVisible(true);
		facilityNameStringSelected = ChoseFacility_ComboBox.getValue();
		setProductsOfChosenFacility();
	}

	/**
	 * intilize the data of products that the facility has
	 */
	private void setProductsOfChosenFacility() {
		// get the chosen facility from the area manger
		for (Facility facility : facilities) {
			if (facility.getFacilityId().equals(facilityNameStringSelected)) {
				chosenFacility = facility;
				break;
			}
		} // arrayList != null && !arrayList.isEmpty()
		dataOfProducts.clear(); // delete the current values in TableView
		// check if there is products in the facility
		if (chosenFacility.getProductsInFacility().isEmpty()) {
			errLabel.setVisible(true);
			UpdatedProductsInventory.setVisible(false); // there is no products to show
		}

		else {
			UpdatedProductsInventory.setVisible(true);
			// run over all products in chosen facility
			for (ProductInFacility productInFaciliy : chosenFacility.getProductsInFacility()) {
				dataOfProducts.add(
						new ProductInFacility(productInFaciliy.getProductCode(), productInFaciliy.getProtductName(),
								productInFaciliy.getCategory(), productInFaciliy.getQuantity()));
			}
			initTableView(); // we got a choose of facility and init the table
		}
	}

	/**
	 * initiate the table view to look at the products inventory at facility
	 */
	private void initTableView() {
		productCode.setCellValueFactory(new PropertyValueFactory<ProductInFacility, Integer>("productCode"));
		protductName.setCellValueFactory(new PropertyValueFactory<ProductInFacility, String>("protductName"));
		category.setCellValueFactory(new PropertyValueFactory<ProductInFacility, ProductCategory>("category"));
		quantity.setCellValueFactory(new PropertyValueFactory<ProductInFacility, Integer>("quantity"));

		UpdatedProductsInventory.setItems(dataOfProducts);
	}

	/**
	 * 
	 * @param event this method will help the user to use this page
	 */
	@FXML
	void helpPress(ActionEvent event) {
		descriptionPage.setVisible(true);
		help.setVisible(false);
	}

	/**
	 * @param event back to pervious page
	 */
	@FXML
	void BackButton_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/AreaManagerScreen.fxml");
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

}