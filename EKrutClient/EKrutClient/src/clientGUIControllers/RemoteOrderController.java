package clientGUIControllers;

import common.CommonActionsController;
import entities.Order;
import entities.SelfCollection;
import entities.Shipment;
import entityControllers.OrderEntityController;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Date;

import Enum.SupplyMethod;


/**
 *This class is responsible for handling the remote order functionality. 
 *It includes methods for handling the supply method selection, 
 *displaying the appropriate details for the selected supply method (shipment or self collection), 
 *and processing the order once the user has provided all necessary information.
 */
public class RemoteOrderController {


	/**
	 *FXML Attributes
	 */
	@FXML
	private Button backBtn;

	@FXML
	private Button continueBtn;

	@FXML
	private Label errorlabel;

	@FXML
	private Button helpBtn;

	@FXML
	private Label helpLabel;

	@FXML
	private Button logOutBtn;

	@FXML
	private BorderPane mainPane;

	@FXML
	private Button xBtn;

	@FXML
	private ComboBox<SupplyMethod> supplyMethodCombo;

	@FXML
	private ImageView methodPhoto;

	@FXML
	private ImageView logoIMG;


	/**
	 * Entities,variables And Controllers 
	 */
    private WorkerEntityController workerEntityController=new WorkerEntityController();
	private OrderEntityController orderEntityController = new OrderEntityController();
	private ShipmentDetailsController shipmentDetailsController= null;
	private SelfCollectionDetailsController selfCollectionDetailsController=null;
	private CommonActionsController commonActionsController= new CommonActionsController();
	private OrderEntityController newOrder= null;
	private ObservableList<SupplyMethod> supplyMethods = FXCollections.observableArrayList(SupplyMethod.Shipment,SupplyMethod.SelfCollection);
	private UserController userController=new UserController();



	/**
	 *The initialize() method is called when the class is first loaded.
	 *The method sets the image of logoIMG, set supply methods in the supplyMethodCombo, hide mainPane, helpLabel and continueBtn.
	 */
	@FXML
	public void initialize() {
		logoIMG.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		this.supplyMethodCombo.setItems(this.supplyMethods);
		this.mainPane.setVisible(false);
		this.helpLabel.setVisible(false);
		continueBtn.setVisible(false);
	}



	/**
	*This method handles the selection of a supply method by the user.
	*It sets the main pane to visible, and based on the value selected in the supplyMethodCombo,
	*it sets an appropriate image and loads the corresponding FXML screen for either shipment or self collection details.
	*The continue button is made visible upon selection of a supply method.
	*@param event The ActionEvent that triggered the method call.
	*/
	@FXML
	void selectSupplyMethodsupplyMethod(ActionEvent event) {
		this.mainPane.setVisible(true);
		switch (this.supplyMethodCombo.getValue()) {
		case Shipment: 
			methodPhoto.setImage(new Image("/viewCatalogScreenPic/drone.gif"));
			try {
				FXMLLoader fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(this.getClass().getResource("/clientGUIScreens/ShipmentDetailsScreen.fxml"));
				AnchorPane anchorPane = (AnchorPane)fxmlLoader.load();
				this.shipmentDetailsController = (ShipmentDetailsController)fxmlLoader.getController();
				this.mainPane.setCenter(anchorPane);
				continueBtn.setVisible(true);
				break;

			}catch (IOException e) {
				e.printStackTrace();
			}

		case SelfCollection:
			methodPhoto.setImage(new Image("/myOrderScreenPic/robotic-arm.gif"));
			try {
				FXMLLoader fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(this.getClass().getResource("/clientGUIScreens/SelfCollectionDetailsScreen.fxml"));
				AnchorPane anchorPane = (AnchorPane)fxmlLoader.load();
				this.selfCollectionDetailsController = (SelfCollectionDetailsController)fxmlLoader.getController();
				this.mainPane.setCenter(anchorPane);
				continueBtn.setVisible(true);
				break;

			}catch (IOException e) {
				e.printStackTrace();
			}
		default:
			break;


		}
	}



	/**
	* Handles the action event when the "continue" button is pressed.
	* Retrieves the selected value from the "supplyMethodCombo" combo box and checks if it is not null.
	* If the selected value is "Shipment", it checks if the "shipmentDetailsController" has all the necessary information.
	* If it does, it sets the delivery address, creates a new Shipment object, sets its supply method, delivery address, and shipment country, 
	* creates a new OrderEntityController, sets the order, and sets the facilityId. 
	* Then it moves to the next screen by calling the "backOrNextPressed()" method.
	* If the selected value is "SelfCollection", it checks if the "selfCollectionDetailsController" has all the necessary information.
	* If it does, it creates a new SelfCollection object,
	*  sets its supply method, facilityId and estimated pick-up date, 
	*  creates a new OrderEntityController, sets the order, 
	*  and moves to the next screen by calling the "backOrNextPressed()" method for.
	*
	* @param event the ActionEvent object generated when the button is pressed
	*/
	@FXML
	void continuePressed(ActionEvent event) {
		if (this.supplyMethodCombo.getValue() != null) {
			switch (supplyMethodCombo.getValue()) {
			case Shipment:
				if(!this.shipmentDetailsController.checkFieldsInfo())
					break;
				this.shipmentDetailsController.setAddress();
				Shipment newShipmentOrder=new Shipment();
				newShipmentOrder.setDeliveryAddress(this.shipmentDetailsController.getFullAddress());
				newShipmentOrder.setSupplyMethod(SupplyMethod.Shipment);
				newShipmentOrder.setShippmentCountry(this.shipmentDetailsController.getSelectedCountry());
				newOrder=new OrderEntityController();
				this.newOrder.setOrder(newShipmentOrder);
				newOrder.getOrder().setFacilityId("OperationCenter"+userController.getUser().getArea());
				commonActionsController.backOrNextPressed(event, "/clientGUIScreens/MyOrderScreen.fxml");
				break;
			case SelfCollection:
				if(!this.selfCollectionDetailsController.checkIfValidInput())
					break;
				SelfCollection newSelfCollectionOrder=new SelfCollection();
				newSelfCollectionOrder.setSupplyMethod(SupplyMethod.SelfCollection);
				newSelfCollectionOrder.setFacilityId(selfCollectionDetailsController.getFacilityId());
				newSelfCollectionOrder.setEstimatePickUpDate(selfCollectionDetailsController.getEstmatePickUpDate());
				newOrder=new OrderEntityController();
				this.newOrder.setOrder(newSelfCollectionOrder);
				commonActionsController.backOrNextPressed(event, "/clientGUIScreens/MyOrderScreen.fxml");
				break;
			default:
				break;

			}
		}
	}



	//This method display help message with instructions to user.
	
	/**
	* Displays a help message with instructions to the user.
	* When the method is called, it sets the text of a "helpLabel" label to a predefined message with instructions 
	* on the available supply methods and any necessary information that the user needs to provide.
	* The "helpLabel" is set to be visible and the "helpBtn" button is set to visible false. 
	* @param event the ActionEvent object generated when the button is pressed
	*/
	@FXML
	void helpPreesed(ActionEvent event) {
		this.helpLabel.setVisible(true);
		this.helpLabel.setText("The supply methods you can choose are:"
				+ "\n1)Self Collection"
				+ "\n2)Shipment"
				+ "\nIf you choose a shipment option you MUST provide  Shipping Address, City,Street and House number!"
				+ "\nIf you choose a self collectin option you MUST select Area,Facility and wanted pick up date!");
		helpBtn.setVisible(false);
	}


	/**
	* Handles the action event when the "back" button is pressed.
	* Resets the "newOrder" object to null and creates new instances of UserController and WorkerEntityController objects.
	* If the WorkerEntityController object's getSecondRole() method returns null, it navigates to the screen corresponding to the role of 
	* the user, otherwise it navigates to the screen corresponding to the second role of the worker.
	* @param event the ActionEvent object generated when the button is pressed
	*/
	@FXML
	void backPressed(ActionEvent event) {
		if(newOrder!=null)
			newOrder=null;
		userController = new UserController();
		workerEntityController = new WorkerEntityController();
		if(workerEntityController.getSecondRole() == null) {
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/"+userController.getUser().getRole()+"Screen.fxml");
		}
		else {
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/"+workerEntityController.getSecondRole()+"Screen.fxml");
		}
	}

	
	/**
	* Handles the action event when the logOut button is pressed.
	* Calls the logOutPressed() method of the commonActionsController object to logout the user.
	* @param event the ActionEvent object generated when the button is pressed.
	*/
	@FXML
	void logOutPressed(ActionEvent event) {
		workerEntityController.setSecondRole(null);
		commonActionsController.logOutPressed(event);
	}
	
	/**
	* Handles the action event when the x button is pressed.
	* Calls the xPressed() method of the commonActionsController object.
	* @param event the ActionEvent object generated when the button is pressed.
	*/
	@FXML
	void xPressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}


}







