package clientGUIControllers;

import javafx.scene.control.Button;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import Client.ClientConsole;
import Client.EkrutClientUI;
import Enum.Instruction;
import Enum.Role;
import Enum.SupplyMethod;
import common.ChangeWindow;
import common.CommonActionsController;
import common.Observer;
import common.OrderTimer;
import entities.Product;
import entities.ProductInFacility;
import entities.ServerMessage;
import entityControllers.OrderEntityController;
import entityControllers.OrderTimerEntityController;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * This class gives the user the option to select product he want to buy and
 * adds them to a shopping cart: displaying the total price and enable a get
 * button to move to the checkout screen to confirm and pay for the order.
 * Displaying the products and the last quantity or out of stock labels, and
 * click to add to cart using the ProductViewController. Displaying the products
 * in cart and enabling to add, sub and remove the product using the
 * CartProductScreen. using the OrderTimer from the OrderTimerEntityController
 * this class display the time left before the order automatically ends. . This
 * class makes use of the tables: 'productinfacility' or 'product', which
 * contain the product details (getting the product to display and updating
 * inventory accordingly)
 */
public class MyOrderController implements Initializable, Observer {
	
	// JavaFX Elements
	
	@FXML
	private VBox myCartVBox;

	@FXML
	private Label totalPrice;
	@FXML
	private Label CartEmpty;

	@FXML
	private GridPane grid;

	@FXML
	private ComboBox<String> category;

	@FXML
	private Label timer;

	@FXML
	private Label discountLabel;

	@FXML
	private Label discountPrice;

	@FXML
	private Line discountLine;

	@FXML
	private Label saleLabel;

	@FXML
	private ImageView activeSale;
	
    @FXML
    private Label noProducts;

    @FXML
    private Button supplyNavButton;
	
	// Attributes
	private static int discount;
	private static boolean isThereSale = false;
	private static boolean isSubFirst = false;

	private float totalCartPrice = 0;
	private float totalCartPriceDiscount = 0;
	private SupplyMethod supplyMethod;
	private String facilityID;
	private DecimalFormat format = new DecimalFormat("0.#");

	private WorkerEntityController workerEntityController = new WorkerEntityController();
	private OrderTimerEntityController orderTimerEntityController = new OrderTimerEntityController();
	private UserController userController = new UserController();
	private CommonActionsController commonActionsController = new CommonActionsController();
	private ArrayList<Product> products = new ArrayList<Product>();
	private ArrayList<CartProductController> cartControllers = new ArrayList<CartProductController>();
	private ArrayList<ProductCartViewController> productCartViewControllers = new ArrayList<ProductCartViewController>();
	private ViewCatalogController viewCatalogController = new ViewCatalogController();
	private ObservableList<String> categories = FXCollections.observableArrayList();
	private OrderEntityController orderEntityController = new OrderEntityController();
	
	// Methods
	
	/**
	 * Setting the discount of the sale that is running at the moment, when set
	 * the flag is there sale is set to true
	 * @param discount the discount value
	 */
	public void setDiscount(int discount) {
		if (discount == 0) {
			return;
		}
		isThereSale = true;
		MyOrderController.discount = discount;
	}


	/**
	 * Setting the boolean attribute that shows if the current user should have a first order sale
	 * @param isSubFirst the boolean to set
	 */
	public void setIsSubFirst(boolean isSubFirst) {
		MyOrderController.isSubFirst = isSubFirst;
	}

	/**
	 * Initialize the order controller with the necessary values and show the
	 * default information needed on the My Order screen
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setOrderTimer();
		getOrderDetails();
		categories.addAll(Arrays.asList("Drinks", "Snacks", "Show All"));
		category.setItems(categories);
		CompletableFuture.runAsync(() -> {
			initLater();
		});
		if(!ClientConsole.getConfiguration().equals("OL")) {
			supplyNavButton.setDisable(true);
		}

	}

	/*
	 *  This method initiate all the necessary UI elements to view the order screen, like 
	 *  products and photos of product after the page has loaded (for better response time)
	 */
	private void initLater() {
		if (!supplyMethod.equals(SupplyMethod.Shipment)) {
			requestFacilityProducts();
		} else {
			requestAllProducts();
		}
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		} // wait for server update
		products = viewCatalogController.getProductsList();
		if (products.isEmpty()) {
			Platform.runLater(()->noProducts.setVisible(true));
		}
		else {
			Platform.runLater(()->noProducts.setVisible(false));
		}
		for (int i = 0; i < this.products.size(); i++) {
			threadToSetOnGrid(i, products.get(i));
		}
		// returning from checkout - adding all products to cart
		
		if (orderEntityController.getOrder().getOrderProductList() != null) {
			try {
				addPreviousData(orderEntityController.getOrder().getOrderProductList());
			} catch (IOException e) {
				EkrutClientUI.chat.display("Error while readding to cart after back pressed from checkout screen");
			}
		}
		setSales();
	}

	/**
	 *	This method display one product on the grid of selection using a thread, that 
	 *	way each thread load one product and the loading time is decreased 
	 * @param index the index of the product in the array to know where to display it on the grid
	 * @param productToDisplay the product itself to know the name, photo and price
	 */
	private void threadToSetOnGrid(int index, Product productToDisplay) {
		new Thread((new Runnable() {
			int index;
			Product product;

			public void run() {
				try {
					setProductOnGrid(index, product);
				} catch (IOException e) {
					EkrutClientUI.chat.display("Error while trying to show product " + product.getProtductName() + " on grid with thread.");
				}
			}

			public Runnable pass(int index, Product product) {
				this.index = index;
				this.product = product;
				return this;
			}
		}).pass(index, productToDisplay)).start();
	}

	/**
	 * This function display the current sale, and update UI for sale
	 */
	private void setSales() {
		isThereSale = false;
		isSubFirst = false;
		

		if (userController.getUser().getRole().equals(Role.Subscriber)
				|| workerEntityController.getSecondRole().equals(Role.Subscriber)) {

			Hashtable<String, Object> table = new Hashtable<>();
			if (orderEntityController.getOrder().getFacilityId() != null) {
				table.put("facility", orderEntityController.getOrder().getFacilityId());
			}

			table.put("user", userController.getUser());
			EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_Sale_Facility, table));
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
			; // wait for server update
			
			if ((isThereSale || isSubFirst)) {
				if ((isSubFirst && !isThereSale) || (isThereSale && discount < 20)) {
					
					Platform.runLater(
							() -> commonActionsController.popUp(new Stage(), "This is your first order as subscriber!",
									"This is your first order as subscriber!\n You get 20% off!",
									"/myOrderScreenPic/sale.gif", null, null));
					discount = 20;
					isThereSale = true;
					Platform.runLater(() -> saleLabel.setText("First order, you get " + discount + "% off!"));
				} else {
					
					Platform.runLater(() -> commonActionsController.popUp(new Stage(), "There is a Sale!",
							"There is a Sale for " + discount + "% off!", "/myOrderScreenPic/sale.gif", null, null));

					activeSale.setImage(new Image("/sales.gif"));
					Platform.runLater(() -> saleLabel.setText("There is a Sale for " + discount + "% off!"));
				}

				discountLine.setVisible(true);
				discountLabel.setVisible(true);
				discountPrice.setVisible(true);
			}
		}
	}

	/**
	 * Set and show the cart price before and after discount
	 */
	private void setTotalPrice() {
		totalPrice.setText(format.format(totalCartPrice) + "₪");

		if (isThereSale) {
			totalCartPriceDiscount = (float) (totalCartPrice - (totalCartPrice * (discount * 0.01)));
			discountPrice.setText(format.format(totalCartPriceDiscount) + "₪");
		}
	}

	/**
	 * This method add to the observable object OrderTimer this controller, start
	 * new order timer if necessary
	 */
	private void setOrderTimer() {
		if (orderTimerEntityController.getOrderTimer() == null) {
			orderTimerEntityController.setOrderTimer(new OrderTimer());
		}
		orderTimerEntityController.getOrderTimer().add(this);
	}

	/**
	 * This method save the order details from the order entity controller
	 */
	private void getOrderDetails() {
		supplyMethod = orderEntityController.getOrder().getSupplyMethod();
		facilityID = orderEntityController.getOrder().getFacilityId();
	}

	/**
	 * This method create the scene for a product in store and display in on the
	 * grid
	 * 
	 * @param index   the index of the product in the array
	 * @param product the product
	 * @throws IOException
	 */
	private void setProductOnGrid(int index, Product product) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(this.getClass().getResource("/clientGUIScreens/ProductCartViewScreen.fxml"));
		StackPane stackPane = (StackPane) fxmlLoader.load();
		ProductCartViewController productCartViewController = (ProductCartViewController) fxmlLoader.getController();
		productCartViewController.setData(product, "/clientGUIScreens/ProductViewScreen.fxml", this);
		if (!productCartViewControllers.contains(productCartViewController)) {
			productCartViewControllers.add(productCartViewController);
		}
		Platform.runLater(() -> this.grid.add(stackPane, index % 3, index / 3));

	}

	/**
	 * This method gets a specific facility products form the server
	 */
	public void requestFacilityProducts() {
		Hashtable<String, Object> facilityTable = new Hashtable<String, Object>();
		facilityTable.put("facilityID", facilityID);
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_Facility_Products, facilityTable));
	}

	/**
	 * This method gets all the product from the server
	 */
	public void requestAllProducts() {
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_all_Product_list, null));
	}

	/**
	 * This method gets called when the x button is pressed and logout and quit the
	 * program
	 * 
	 * @param event ActionEvent of the pressing the x button
	 */
	@FXML
	void XBtn(ActionEvent event) {
		commonActionsController.xPressed(event);
	}

	/**
	 * This method gets called when the combo box for category is changes and
	 * display the correct products
	 * 
	 * @param event ActionEvent of the click on the select category combo box
	 * @throws IOException
	 */
	@FXML
	void selectCategory(ActionEvent event) throws IOException {
		grid.getChildren().clear();
		String categorySelected = category.getValue();
		int categoryIndex = 0;
		for (int i = 0; i < this.products.size(); i++) {
			if (categorySelected.equals("Show All")
					|| categorySelected.equals(this.products.get(i).getCategory().name())) {
				threadToSetOnGrid(categoryIndex++, products.get(i));
			}
		}

	}

	/**
	 * This method gets called when the get button is pressed in the screen, checks
	 * to see if all products are in up to date inventory , shows pop up and update
	 * cart if not and transfer to checkout screen otherwise
	 * 
	 * @param event ActionEvent of the click on the get button
	 * @throws IOException
	 */
	@FXML
	void getProducts(ActionEvent event) throws IOException {
		if (cartControllers.size() == 0) {
			CartEmpty.setVisible(true);
			return;
		}

		ArrayList<Product> orderProducts = new ArrayList<>();
		for (CartProductController c : cartControllers) {
			orderProducts.add(cloneProductAddQuantity(c.getProduct(), c.getCount()));
		}

		if (!supplyMethod.equals(SupplyMethod.Shipment)) {
			String msg = "Some of the product in your cart become unavaliable or out of stock while you shopped\n";
			totalCartPrice = 0;
			requestFacilityProducts();
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {
			}
			; // wait for server update
			grid.getChildren().clear();
			this.products = viewCatalogController.getProductsList();
			Iterator<CartProductController> iterator = cartControllers.iterator();
			while (iterator.hasNext()) {
				CartProductController c = iterator.next();
				
				msg = updateStockProducts(c, msg, iterator);
				totalCartPrice += c.getTotalPrice();
			}
			setTotalPrice();
			for (int i = 0; i < this.products.size(); i++) {
				setProductOnGrid(i, this.products.get(i));
			}
			if (!msg.equals("Some of the product in your cart become unavaliable or out of stock while you shopped\n")) {
				commonActionsController.popUp(new Stage(), "Product out of stock!",
						msg + "\n Updating your cart according to inventory.",
						"/myOrderScreenPic/warehouse.gif", null, null);
				return;
			}
			updateOrderServer(orderProducts);
			if (orderEntityController.getOrder().getOrderProductList() == null) {
				commonActionsController.popUp(new Stage(), "Error", "Error while updating inventory in server", null,
						null, null);
				return;
			}
		} else {
			orderEntityController.getOrder().setOrderProductList(orderProducts);
		}

		if (isThereSale) {
			orderEntityController.getOrder().setTotalPrice(totalCartPriceDiscount);
		} else {
			orderEntityController.getOrder().setTotalPrice(totalCartPrice);
		}
		orderTimerEntityController.getOrderTimer().remove(this);
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/CheckoutScreen.fxml");
	}

	/**
	 * This method send a request to update the product inventory in the server and
	 * waits for response
	 * 
	 * @param orderProducts the products to update
	 */
	private void updateOrderServer(ArrayList<Product> orderProducts) {

		Hashtable<String, Object> productsTable = new Hashtable<>();
		productsTable.put("products", orderProducts);
		productsTable.put("facilityID", orderEntityController.getOrder().getFacilityId());
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Order_Get_Products, productsTable));
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		; // wait for server update

	}

	/**
	 * This method make a product in facility to represent a product in order
	 * 
	 * @param product  the product
	 * @param quantity the quantity in the order
	 * @return a ProductInFacilty type with the quantity as oder quantity
	 */
	private ProductInFacility cloneProductAddQuantity(Product product, int quantity) {
		ProductInFacility temp = new ProductInFacility();
		temp.setProtductName(product.getProtductName());
		temp.setPhoto(product.getPhoto());
		temp.setCategory(product.getCategory());
		temp.setProductCode(product.getProductCode());
		temp.setProductPrice(product.getProductPrice());
		if (product instanceof ProductInFacility) {
			((ProductInFacility) temp).setProductStatus(((ProductInFacility) product).getProductStatus());
			((ProductInFacility) temp).setTresholdLevel(((ProductInFacility) product).getTresholdLevel());
		}
		((ProductInFacility) temp).setQuantity(quantity);
		return temp;
	}

	/**
	 * This method update the cart and a string message after checking the inventory
	 * and the products in cart, the cart will be updated to the available stock and
	 * to the message string we log the update we made
	 * 
	 * @param c   the product cart controller
	 * @param msg the string message
	 * @return
	 */
	private String updateStockProducts(CartProductController c, String msg, Iterator<CartProductController> iterator) {
		int quantity;
		boolean found = false;
		for (Product p : this.products) {
			if (p.getProductCode() == c.getProduct().getProductCode()) {
				found = true;
				quantity = ((ProductInFacility) p).getQuantity() - c.getCount();
				if (quantity < 0) {
					msg += p.getProtductName() + ": " + -1 * quantity + " more than in stock. \n";
					c.setCartCount(c.getCount() + quantity);
					quantity = 0;
				}
				((ProductInFacility) p).setQuantity(quantity);
			}
		}
		if(!found) {
			myCartVBox.getChildren().remove(c.getAnchor());
			iterator.remove();
			totalCartPrice-=c.getTotalPrice();
			setTotalPrice();
			msg += c.getProduct().getProtductName() + ": " + c.getCount() + " more than in stock. \n";
		}
		return msg;
	}

	/**
	 * This method is called when the user pressed the logout button, the method
	 * perform a logout and change to the login screen
	 * 
	 * @param event ActionEvent of the click on the logout button
	 */
	@FXML
	void logout(ActionEvent event) {
    	orderTimerEntityController.getOrderTimer().remove(this);
		orderTimerEntityController.getOrderTimer().stop();
		orderTimerEntityController.setOrderTimer(null);
		commonActionsController.logOutPressed(event);
	}

	/**
	 * This method is called when the user pressed the return button, the method
	 * will transfer the user to previous window
	 * 
	 * @param event ActionEvent of the click on the return button
	 */
	@FXML
	void returnBtn(ActionEvent event) {
		String screen;
		if (supplyMethod.equals(SupplyMethod.Shipment) || supplyMethod.equals(SupplyMethod.SelfCollection)) {
			screen = "/clientGUIScreens/RemoteOrderScreen.fxml";
		} else {
			screen = "/clientGUIScreens/" + userController.getUser().getRole().toString() + "Screen.fxml";
		}
		orderTimerEntityController.getOrderTimer().stop();
		orderTimerEntityController.setOrderTimer(null);
		commonActionsController.backOrNextPressed(event, screen);
	}

	/**
	 * This method add to cart a single unit of a wanted product
	 * 
	 * @param product the product we want to add
	 * @throws IOException
	 */
	public void addToCart(Product product) throws IOException {
		CartEmpty.setVisible(false);
		if (supplyMethod != SupplyMethod.Shipment) {
			quantityUpdate((ProductInFacility) product, -1);
		}
		for (CartProductController c : cartControllers) {
			if (c.getProduct().getProductCode() == product.getProductCode()) {
				c.add();
				totalCartPrice += product.getProductPrice();
				setTotalPrice();
				return;
			}
		}
		addProductCartView(product);
	}

	/**
	 * This method update the product inventory according to the amount given and
	 * notify the other scenes like product dart view of the changes
	 * 
	 * @param product the product to update
	 * @param amount  the amount to update
	 */
	public void quantityUpdate(ProductInFacility product, int amount) {
		int quantity = 0;
		for (Product p : products) {
			if (p.getProductCode() == product.getProductCode()) {
				((ProductInFacility) p).setQuantity(((ProductInFacility) p).getQuantity() + amount);
				quantity = ((ProductInFacility) p).getQuantity();
			}
		}
		for (ProductCartViewController pcvc : productCartViewControllers) {
			if (pcvc.getProduct().getProductCode() == product.getProductCode()) {
				pcvc.updateQuantity(quantity);
			}
		}
	}

	/**
	 * This method add the scene of a single product in cart and updated total price
	 * 
	 * @param product the product to add
	 * @throws IOException
	 */
	private void addProductCartView(Product product) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(this.getClass().getResource("/clientGUIScreens/CartProductScreen.fxml"));
		AnchorPane anchorPane = (AnchorPane) fxmlLoader.load();
		CartProductController CartProductController = (CartProductController) fxmlLoader.getController();
		CartProductController.setData(product, this);
		cartControllers.add(CartProductController);
		myCartVBox.getChildren().add(anchorPane);
		totalCartPrice += product.getProductPrice();
		setTotalPrice();
	}

	/**
	 * This method remove from the cart a product, remove the visual line of the
	 * product in the cart and remove the details saves for the product in the cart
	 * controller
	 * 
	 * @param product  the product to the remove
	 * @param quantity the quantity of the product
	 */
	public void removeFromCart(Product product, int quantity) {

		if (product instanceof ProductInFacility && !orderEntityController.getOrder().getSupplyMethod().equals(supplyMethod.Shipment)) {
			quantityUpdate((ProductInFacility) product, quantity);
		}
		
		Iterator<CartProductController> iterator = cartControllers.iterator();
		while(iterator.hasNext()) {
			CartProductController c = iterator.next();
			if (c.getProduct().getProductCode() == product.getProductCode()) {
				totalCartPrice -= c.getTotalPrice();
				setTotalPrice();
				myCartVBox.getChildren().remove(c.getAnchor());
				iterator.remove();
			}
		}

	}

	/**
	 * This method update the cart details when a product is added from another
	 * scene, uses the quantityUpdate to update the inventory.
	 *
	 * @param productPrice the product price
	 * @param product      product object
	 * @param isAdder      true if we want to add the product and false if we remove
	 *                     the product
	 * @return true if we successfully updated and false if there is no inventory to
	 *         preform this action
	 */
	public boolean updateCart(float productPrice, Product product, boolean isAdder) {
		if (!supplyMethod.equals(SupplyMethod.Shipment)) {
			for (Product p : products) {
				if (p.getProductCode() == product.getProductCode()) {
					product = p;
				}
			}
			if (((ProductInFacility) product).getQuantity() < 1 && isAdder) {
				notInStock(product);
				return false;
			}
			quantityUpdate((ProductInFacility) product, isAdder ? -1 : 1);
		}
		totalCartPrice += isAdder ? productPrice : productPrice * -1;
		setTotalPrice();
		return true;
	}

	/**
	 * This method pop up a window to say a product is out of stock
	 * 
	 * @param product the product that is out of stock
	 */
	public void notInStock(Product product) {
		commonActionsController.popUp(new Stage(), "Out of Stock!",
				product.getProtductName()
						+ " is out of stock,\n or you just added all our avaliable stock to your cart.",
				"/myOrderScreenPic/warehouse.gif", null, null);
	}

	/**
	 * The method add all previous chosen items to the cart with the correct amount
	 * gets called when user go back from checkout screen
	 * 
	 * @param oldProducts old products and there amounts
	 * @throws IOException
	 */
	public void addPreviousData(ArrayList<Product> oldProducts) throws IOException {
		
		for (Product product : oldProducts) {
			ProductInFacility p = (ProductInFacility) product;
			for (int i = 0; i < p.getQuantity(); i++) {
				
				Platform.runLater(() -> {
					try {
						addToCart(product);
					} catch (IOException e) {
						EkrutClientUI.chat.display("Error while trying to add previous selected products to cart.");
					}
				});
			}
		}
	}

	/**
	 * update method from observer interface gets called from timer which not run on
	 * main thread there for cannot change the UI (runLater waiting and run the
	 * command on main thread) displaying the timer and calling the timeEndOrder
	 * when time is up
	 */
	@Override
	public void update(Object data) {
		if (((String) data).startsWith("-")) {
			Platform.runLater(() -> timeEndOrder());
		}
		Platform.runLater(() -> {
			timer.setText((String) data);
		});
	}

	/**
	 * This method handles the force close and cancel of the order when the order
	 * timer is up
	 */
	private void timeEndOrder() {
		orderTimerEntityController.getOrderTimer().stop();
		orderTimerEntityController.setOrderTimer(null);
		timer.getScene().getWindow().hide();
		ChangeWindow changeWindow = new ChangeWindow();
		changeWindow.changeScreen(new Stage(),
				"/clientGUIScreens/" + (new UserController()).getUser().getRole() + "Screen.fxml");
		commonActionsController.popUp(new Stage(), "Out of Time!",
				"Each order needs to be completed within 15 minutes, you have passed the 15 minutes, the order is canceled and you return to the home screen.",
				"/myOrderScreenPic/stopwatch.gif", null, null);
	}
	
	
    @FXML
    void pressHelp(ActionEvent event) {
		commonActionsController.popUp(new Stage(), "How to order",
				"Click on the product you want to add to the cart.\n"
				+ "You can view your cart on the left side of the screen, add and subtract with the plus and minus button, and remove from cart with the bin button.\n\n"
				+ "After adding all the product you want to the cart, press the \"Get\" button to move the checkout screen and continue the order process.\n\n"
				+ "Please be aware that our products may contain or come into contact with common allergens, such as dairy, eggs, wheat, tree nuts and peanuts.",
				"/information.gif", null, null);
    }

}