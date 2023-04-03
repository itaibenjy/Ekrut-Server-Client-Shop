package clientGUIControllers;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;

import entities.ProductInFacility;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class is the controller of a small scene that display a single product in cart, 
 * when in the checkout screen it displays the photo, name , amount and total price of 
 * a single product.
 */
public class CartController {
	
	// JavaFX Elements
    @FXML
    private ImageView productImage;

    @FXML
    private Label productUnits;

    @FXML
    private Label productName;

    @FXML
    private Label productPrice;
    
    // Attributes
	private DecimalFormat format = new DecimalFormat("0.#");
	
	// Elements
    public void setData(ProductInFacility product) {
    	Image img = new Image(new ByteArrayInputStream(product.getPhoto()));
    	Platform.runLater(()->{
			productImage.setImage(img);
			productUnits.setText(Integer.toString(product.getQuantity()));
			productName.setText(product.getProtductName());
			productPrice.setText(format.format(product.getProductPrice() * product.getQuantity()) + "₪");
    	});
    }
}
