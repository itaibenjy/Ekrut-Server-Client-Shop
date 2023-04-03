package clientGUIControllers;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import Client.ClientConsole;
import Client.EkrutClientUI;
import Enum.Instruction;
import Enum.SupplyMethod;
import common.ChangeWindow;
import common.CommonActionsController;
import common.Observer;
import entities.Product;
import entities.ProductInFacility;
import entities.ServerMessage;
import entityControllers.OrderEntityController;
import entityControllers.OrderTimerEntityController;
import entityControllers.UserController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class display to the user his cart and let him have the option of cancel or confirm the order, also able to press back to MyOrderScreen
 * to continue and edit the order
 * 
 * This class makes user of this tables
 * 'productinfacility' or 'product', which contain the product details (for updating inventory according to user selection)
 */
public class CheckoutController implements Initializable, Observer {
	
	// JavaFx Elements
    @FXML
    private VBox cartVBox;

    @FXML
    private Label price;
    
    @FXML
    private Label timer;
    
    @FXML
    private Button supplyNavButton;
    
    // Attributes
	private OrderTimerEntityController orderTimerEntityController = new OrderTimerEntityController();
    private CommonActionsController commonActionsController = new CommonActionsController();
    private OrderEntityController orderEntityController = new OrderEntityController();
    private UserController userController = new UserController();
	private DecimalFormat format = new DecimalFormat("0.#");
    private ArrayList<Product> products;
    
    // Methods
	/**
	 *  Initialize the checkout with the necessary values and show the default 
	 *  information needed on the checkout screen
	 */ 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		orderTimerEntityController.getOrderTimer().add(this);
		products = orderEntityController.getOrder().getOrderProductList();
		CompletableFuture.runAsync(() -> {
			initLater();
		});
		price.setText(format.format(orderEntityController.getOrder().getTotalPrice()) +"₪");
		
		if(!ClientConsole.getConfiguration().equals("OL")) {
			supplyNavButton.setDisable(true);
		}
	}
	
	/**
	 * This method load all the product in your cart on the screen each in a new thread
	 */
	private void initLater() {
		for(Product p: products) {
			new Thread(()-> loadProductRow((ProductInFacility)p)).start();
		}
	}

	/**
	 * This method loads the scene of a product row in the cart and displays it on the screen
	 * 
	 * @param product the product row to display
	 */
	private void loadProductRow(ProductInFacility product) {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(this.getClass().getResource("/clientGUIScreens/CartScreen.fxml"));
		AnchorPane anchorPane;
		try {
			anchorPane = (AnchorPane)fxmlLoader.load();
			CartController cartController = (CartController)fxmlLoader.getController();
			cartController.setData(product);
			Platform.runLater(()->cartVBox.getChildren().add(anchorPane));
		} catch (IOException e) {
			EkrutClientUI.chat.display("Error while loading cart row " + product.getProtductName() +".");
		}
	}
	
	/**
	 * This method cancels the order, send a request to the server to return products to the inventory.
	 */
	private void cancelOrder() {
		Hashtable<String, Object> productOrdertable =  new Hashtable<>();
		productOrdertable.put("products", orderEntityController.getOrder().getOrderProductList());
		productOrdertable.put("facilityID", orderEntityController.getOrder().getFacilityId());
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Add_Order_To_Invetory, productOrdertable));
		while(!EkrutClientUI.chat.isServerMsgRecieved()) {} // waiting for sever response
	}

    /**
     * This method is called when the user pressed the return button,
     * the method will transfer the user to previous window
     * 
     * @param event ActionEvent of the click on the return button 
     */
    @FXML
    void backPressed(ActionEvent event) {
    	if(orderEntityController.getOrder().getSupplyMethod()!= SupplyMethod.Shipment) {
    		cancelOrder();
    	}
    	orderEntityController.getOrder().setOrderProductList(products);
    	orderTimerEntityController.getOrderTimer().remove(this);
    	commonActionsController.backOrNextPressed(event, "/clientGUIScreens/MyOrderScreen.fxml");
    }

    /**
     * This method gets called when the cancel button is pressed, cancel the order and return 
     * the user to the make order screen
     * 
     * @param event ActionEvent of the confirm button pressed
     */
    @FXML
    void cancelPressed(ActionEvent event) {
    	if(orderEntityController.getOrder().getSupplyMethod()!= SupplyMethod.Shipment) {
    		cancelOrder();
    	}
    	orderEntityController.setOrder(null);
    	orderTimerEntityController.getOrderTimer().remove(this);
		orderTimerEntityController.getOrderTimer().stop();
		orderTimerEntityController.setOrderTimer(null);
    	
    	commonActionsController.backOrNextPressed(event, "/clientGUIScreens/"+userController.getUser().getRole()+"Screen.fxml");
    }

    /**
     * This method is called when the confirm button is pressed and transfer the user to the 
     * payment screen 
     * 
     * @param event ActionEvent of the confirm button pressed
     */
    @FXML
    void confirmPressed(ActionEvent event) {
    	orderTimerEntityController.getOrderTimer().remove(this);
    	commonActionsController.backOrNextPressed(event, "/clientGUIScreens/PaymentScreen.fxml");
    }
    
	/**
	 * This method is called when the user pressed the logout button, 
	 * the method perform a logout and change to the login screen
	 * 
	 * @param event ActionEvent of the click on the logout button
	 */
    @FXML
    void logoutPressed(ActionEvent event) {
    	if(orderEntityController.getOrder().getSupplyMethod()!= SupplyMethod.Shipment) {
    		cancelOrder();
    	}
    	orderEntityController.setOrder(null);
		orderTimerEntityController.getOrderTimer().stop();
    	orderTimerEntityController.getOrderTimer().remove(this);
		orderTimerEntityController.setOrderTimer(null);
    	commonActionsController.logOutPressed(event);
    }

    /**
     * This method gets called when the x button is pressed and logout and quit the program
     * 
     * @param event ActionEvent of pressing the x button 
     */
    @FXML
    void xBtnPressed(ActionEvent event) {
    	if(orderEntityController.getOrder().getSupplyMethod()!= SupplyMethod.Shipment) {
    		cancelOrder();
    	}
    	commonActionsController.xPressed(event);
    }
    
    
	/**
	 * update method from observer interface
	 * gets called from timer which not run on main thread there for cannot 
	 * change the UI (runLater waiting and run the command on main thread)
	 * displaying the timer and calling the timeEndOrder when time is up
	 */
	@Override
	public void update(Object data) {
		if(((String)data).startsWith("-")) {
			Platform.runLater(()->timeEndOrder());
		}
		Platform.runLater(()->{timer.setText((String)data);});
	}

	/**
	 * This method handles the force close and cancel of the order when the
	 * order timer is up
	 */
	private void timeEndOrder() {
		orderTimerEntityController.getOrderTimer().stop();
		orderTimerEntityController.setOrderTimer(null);
		timer.getScene().getWindow().hide();
		ChangeWindow changeWindow = new ChangeWindow();
		changeWindow.changeScreen(new Stage(),  "/clientGUIScreens/" + (new UserController()).getUser().getRole() + "Screen.fxml");
		commonActionsController.popUp(new Stage(), "Out of Time!",
				"Each order needs to be completed within 15 minutes, you have passed the 15 minutes, the order is canceled and you return to the home screen.",
				"/myOrderScreenPic/stopwatch.gif", null, null);
	}

}
