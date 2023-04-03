package clientGUIControllers;

import java.io.IOException;
import entities.Product;
import entities.ProductInFacility;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * This class is like a decorator for the product view controller it uses the product view
 * controller as the base and adds the add to cart button and a label shows the amount left
 * in stock .
 */
public class ProductCartViewController {
	
	// JavaFX Elements
    @FXML
    private StackPane stack;

    @FXML
    private ImageView addToCart;
    
    @FXML
    private Label lastProduct;
    
    // Attributes
    
	// included and under threshold level to show the user the quantity left 
	private final int SHOWQUNTINTYLEVEL = 3;
    
    private Product product;
	private MyOrderController myOrderController;
	
	public Product getProduct() {
		return product;
	}

    
    /**
     * This method set the correct scene from product catalog as the background and display the add to cart and inventory on it
     * 
     * @param product the product to of the controller
     * @param catalogProductPath the path to the product catalog view scene
     * @param myOrderController the my order controller to access it when add to cart pressed
     * @throws IOException
     */
    public void setData(Product product, String catalogProductPath, MyOrderController myOrderController) throws IOException {
    	this.product = product;
    	this.myOrderController = myOrderController;
    	
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(this.getClass().getResource(catalogProductPath));
		AnchorPane cartProduct = (AnchorPane)fxmlLoader.load();
		ProductViewController productViewController = (ProductViewController)fxmlLoader.getController();
		productViewController.setData(product);
		
		stack.getChildren().add(0,cartProduct);
		addToCart.setTranslateX(65);
		addToCart.setTranslateY(65);
		
		lastProduct.setTranslateX(-47);
		lastProduct.setTranslateY(60);
		
		addToCart.setVisible(false);
		setHoverCart();
		
		if(product instanceof ProductInFacility) {
			int unitsLeft = ((ProductInFacility)product).getQuantity();
			updateQuantity(unitsLeft);
		}
    }
    
    
    /**
     * This method sets the add to cart image to display only when the cursor is hovering above the scene
     */
    private void setHoverCart() {
    	stack.setOnMouseEntered(new EventHandler<MouseEvent>() {

    	    @Override
    	    public void handle(MouseEvent t) {
    	    	addToCart.setVisible(true);
    	    }
    	});

    	stack.setOnMouseExited(new EventHandler<MouseEvent>() {

    	    @Override
    	    public void handle(MouseEvent t) {
    	    	addToCart.setVisible(false);
    	    }
    	});
	}

	/**
	 * This method called when this scene has been clicked on and adding the product to cart through the 
	 * my order controller
	 * 
	 * @param event ActionEvent of the clicking on the scnen
	 * @throws IOException
	 */
	@FXML
    public void addToCartClicked(MouseEvent event) throws IOException {
		if(product instanceof ProductInFacility) {
			
			if(((ProductInFacility)product).getQuantity() < 1){
				myOrderController.notInStock(product);
				return;
			}
		}
		
		myOrderController.addToCart(product);
    }


	/**
	 * This method update the label showing on the bottom left of the product view to 
	 * show the amount left in inventory or to show not in stock (reposition the label accordingly)
	 * 
	 * @param quantity
	 */
	public void updateQuantity(int quantity) {
		((ProductInFacility)product).setQuantity(quantity);
		
		if(quantity == 0) {
			lastProduct.setText("Not In Stock");
			lastProduct.setTranslateX(-30);
		}
		else if(quantity <= SHOWQUNTINTYLEVEL) { 
			lastProduct.setText(Integer.toString(quantity) + " left");
			lastProduct.setTranslateX(-47);
		}
		else {
			lastProduct.setText("");
			lastProduct.setTranslateX(-47);
		}
	}

}
