package clientGUIControllers;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;

import entities.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
/**
 * This class is a decorative class for the product view class, it takes the product view class, and adds information and functionality.
 * The functionality added is the add to cart button when pressed on the scene, and the inventory label to show last quantity of a product or
 * if it is out of stock.
 * 
 * This class makes use of the tables:
 * 'productinfacility' or 'product', which contain the product details
 */
public class CartProductController {
	
	// JavaFX Elements
	@FXML
	private AnchorPane anchor;
	  

	@FXML
    private ImageView productPhoto;

    @FXML
    private Label productName;

    @FXML
    private Label cartPrice;

    @FXML
    private Label quntity;
    
    @FXML
    private ImageView removePhoto;
    
	// Attributes
    private Product product;
    private int count;
    private float totalPrice;
    private MyOrderController myOrderController;
	private DecimalFormat format = new DecimalFormat("0.#");
	
	// Methods

    public Product getProduct() {
		return product;
	}
    
    public int getCount() {
		return count;
	}
    
    public AnchorPane getAnchor() {
		return anchor;
	}
    
    public float getTotalPrice() {
		return totalPrice;
	}

	/**
	 * This method set the amount of the product in the cart, and update the price accordingly
	 * @param count the amount
	 */
	public void setCartCount(int count) {
		this.count = count;
		this.quntity.setText(Integer.toString(count));
		if(count == 0) {
			this.removeFromCart(null);
			return;
		}
		totalPrice = count * product.getProductPrice();
    	cartPrice.setText(format.format(totalPrice)+ "₪");
	}
    
	/**
	 * This method adds one to the quantity of the product and update the price
	 */
	public void add() {
    	totalPrice += product.getProductPrice();
    	quntity.setText(Integer.toString(++count));
    	cartPrice.setText(format.format(totalPrice)+ "₪");
    }
    
    /**
     * This method set the data to this controller using the product (sets image, name, price and quantity)
     * 
     * @param product the product to set
     * @param myOrderController for updating the order on plus and minus
     */
    public void setData(Product product, MyOrderController myOrderController) {
    	this.myOrderController = myOrderController;
    	this.product = product;
    	this.totalPrice = product.getProductPrice();
    	this.count = 1;
    	quntity.setText(Integer.toString(count));
    	cartPrice.setText(format.format(totalPrice)+ "₪");
    	productName.setText(product.getProtductName());
       	Image photo = new Image(new ByteArrayInputStream(product.getPhoto()));
    	productPhoto.setImage(photo);
    }

	/**
	 * This method remove this entire scene from the cart view in the my order controller
	 * 
	 * @param event the MouseEvent click on the cart
	 */
	@FXML
    void removeFromCart(MouseEvent event) {
		myOrderController.removeFromCart(product, count);
    }
	
	/**
	 * This method is called when the plus image is pressed, adds one to cart using the 
	 * my order controller 
	 * 
	 * @param event the MouseEvent click on plus image
	 */
	@FXML
	void addOne(MouseEvent event) {
		
    	if(!myOrderController.updateCart(product.getProductPrice(), product, true)) {return;}
		quntity.setText(format.format(++count));
		totalPrice += product.getProductPrice();
    	cartPrice.setText(format.format(totalPrice)+ "₪");
	}


	/**
	 * This method is called when the minus image is pressed, if there is more than 1 on screen 
	 * sub one from cart and update the inventory using the my order controller 
	 * 
	 * @param event thee MouseEvent click on the minus image
	 */
	@FXML
	void removeOne(MouseEvent event) {
		
		if(count < 2) {
			return;
		}
		
		quntity.setText(format.format(--count));
		totalPrice -= product.getProductPrice();
    	cartPrice.setText(format.format(totalPrice)+ "₪");
    	myOrderController.updateCart(product.getProductPrice(), product, false);
	}

}
