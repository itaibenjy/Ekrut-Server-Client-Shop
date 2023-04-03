package clientGUIControllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import common.CommonActionsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import Client.EkrutClientUI;
import Enum.Instruction;
import Enum.ProductCategory;
import entities.Product;
import entities.ServerMessage;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;


/**

ViewCatalogController is a JavaFX controller class that handles the user interface of the view catalog screen in the client application.
It displays a grid of products and allows the user to select a category to view,
log out, and navigate back to the previous screen.
 */
public class ViewCatalogController {

	/**
	 *FXML Attributes
	 */
	@FXML
	private Button XBtn;

	@FXML
	private Button backBtn;

	@FXML
	private Button logOutBtn;

	@FXML
	private ImageView catalogIMG;

	@FXML
	private ImageView logoIMG;

	@FXML
	private ImageView drinksIMG;

	@FXML
	private ImageView snackIMG;

	@FXML
	private ComboBox<String> selectCategoryComboBox;
	ObservableList<String> items = (ObservableList<String>) FXCollections.observableArrayList(ProductCategory.Snacks.toString(),ProductCategory.Drinks.toString());

	@FXML
	private GridPane grid;



	/**
	 * Entities,variables And Controllers 
	 */
	private UserController userController = new UserController();
	private WorkerEntityController workerEntityController=new WorkerEntityController();
	private static ArrayList<Product> productsList = new ArrayList<Product>();
	private  CommonActionsController commonActionsController = new CommonActionsController();
	private boolean firstTimeComboBox =true;



	/**
	 * This method is called when the class is initialized, it sets the images for the different elements of the screen,
	 * sets the items for the select category combo box, and retrieves the list of products from the server.
	 */
	@FXML
	public void initialize() {
		snackIMG.setVisible(false);
		drinksIMG.setVisible(false);
		selectCategoryComboBox.setItems(items);
		setImages();
		CompletableFuture.runAsync(() -> {
			initLater();
		});
	}

	
	
	/**
	 * This method retrieves the list of products from the DB and sets them on the grid.
	 */
	private void initLater() {
		EkrutClientUI.chat.accept(new ServerMessage(Instruction.Get_all_Product_list,null));
		while(!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update
		for( int i=0; i<productsList.size();i++) {
			threadToSetOnGrid(i, productsList.get(i));		
		}

	}

	/**
	 * This method sets a product on the grid using a FXMLLoader, it loads the ProductViewScreen.fxml file, 
	 * sets the data for the product and adds it to the grid.
	 * @param i the index of the product in the list.
	 * @param product the product to be set on the grid.
	 * @throws IOException
	 */
	private void setProductOnGrid(int i , Product product) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(this.getClass().getResource("/clientGUIScreens/ProductViewScreen.fxml"));
		AnchorPane anchorPane = (AnchorPane)fxmlLoader.load();
		ProductViewController productViewController = (ProductViewController)fxmlLoader.getController();
		productViewController.setData(product);
		Platform.runLater(()->this.grid.add(anchorPane, i%3, i/3));


	}

	/**
	 * This method creates a new thread that calls the setProductOnGrid method for a specific product and index.
	 * @param i the index of the product in the list.
	 * @param p the product to be set on the grid.
	 */
	private void threadToSetOnGrid(int i,Product p) {
		new Thread((new Runnable() {
			int i ;
			Product product;
			public void run() {
				try {
					setProductOnGrid(i,product );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			public Runnable pass(int i,Product product) {
				this.i = i;
				this.product=product;
				return this;
			}
		}).pass(i,p)).start();
	}


	/**
	 * This method filters the products in the grid based on the selected category.
	 * and sets the corresponding image to be visible.
	 * @param event the event that triggered the method call.
	 */
	@FXML
	public void selectFilter(ActionEvent event) {
		this.grid.getChildren().clear();
		if (firstTimeComboBox) {
			items.add("Show All");
			selectCategoryComboBox.setItems(items);
			firstTimeComboBox=false;
			snackIMG.setVisible(false);
			drinksIMG.setVisible(false);
		}
		String filter = selectCategoryComboBox.getValue();
		if(filter.equals(ProductCategory.Snacks.toString())) {
			snackIMG.setVisible(true);
			drinksIMG.setVisible(false);
		}else if(filter.equals(ProductCategory.Drinks.toString())) {
			snackIMG.setVisible(false);
			drinksIMG.setVisible(true);
		}else {
			snackIMG.setVisible(false);
			drinksIMG.setVisible(false);
		}
		int count = 0 ;
		for( int i=0; i<productsList.size();i++) {
			if( productsList.get(i).getCategory().toString().equals(filter) || filter.equals("Show All")) {
				threadToSetOnGrid(count, productsList.get(i));
				count++;
			}
		}
	}


	/**
	 *This method returns the current list of products being displayed in the view catalog screen.
	 *@return an ArrayList of products.
	 */
	public ArrayList<Product> getProductsList() {
		return productsList;
	}

	/**
	 *This method sets the current list of products to be displayed in the view catalog screen.
	 * @param ProductsList
	 */
	public void setProductsList(ArrayList<Product> ProductsList) {
		productsList = ProductsList;
	}

	/**
	 *This method handles the event when the log out button is pressed. 
	 *It calls the logOutPressed method of the CommonActionsController.
	 *@param event the event that triggered the method call
	 */
	@FXML
	void logOutPressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}

	/**
	 *This method handles the event when the X button is pressed.
	 * It calls the xPressed method of the CommonActionsController.
	 *@param event the event that triggered the method call.
	 */
	@FXML
	void xPressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}


	/**
	 *This method handles the event when the back button is pressed. 
	 *It calls the backOrNextPressed method of the CommonActionsController with the appropriate fxml file location according to the appropriate role.
	 *@param event the event that triggered the method call.
	 */
	@FXML
	void backPressed(ActionEvent event) {
		if(workerEntityController.getSecondRole() == null) {
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/"+userController.getUser().getRole()+"Screen.fxml");
		}
		else {
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/"+workerEntityController.getSecondRole()+"Screen.fxml");
		}

	}
	
	/**
	 * Set images
	 */
	private void setImages() {
		logoIMG.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		drinksIMG.setImage(new Image("/viewCatalogScreenPic/drinks.png"));
		snackIMG.setImage(new Image("/viewCatalogScreenPic/snacks.png"));
		catalogIMG.setImage(new Image("/viewCatalogScreenPic/catalog.gif"));
	} 
}
