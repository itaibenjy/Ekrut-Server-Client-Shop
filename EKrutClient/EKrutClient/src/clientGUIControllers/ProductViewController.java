package clientGUIControllers;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;

import entities.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
* The ProductViewController class is used to handle the display of product information in the GUI.
* It consists of three FXML attributes: productImage, productName, and productPrice.
* The class has a single method setData which is used to populate the FXML attributes with information from a Product object.
*For each product in the catalog will be instance of this class.
*/
public class ProductViewController {

	/**
	 *FXML Attributes
	 */
	@FXML
	private ImageView productImage;

	@FXML
	private Label productName;

	@FXML
	private Label productPrice;
	
	/**
	* This method sets the ProductView FXML attributes with information from a Product object.
	* This method sets the text of the productName label to the name of the product,
	* formats and sets the text of the productPrice label to the price of the product, 
	* and sets the image of the productImage ImageView to the image of the product.
	* @param product the Product object containing the information to be displayed.
	*/
	public void setData(Product product) {
		this.productName.setText(product.getProtductName());
		DecimalFormat format = new DecimalFormat("0.#");
		this.productPrice.setText(format.format(product.getProductPrice()) + "₪");
		Image productImage = new Image(new ByteArrayInputStream(product.getPhoto()));
		this.productImage.setImage(productImage);
		
	}

}
