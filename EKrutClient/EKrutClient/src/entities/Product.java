package entities;

import java.io.Serializable;

import Enum.ProductCategory;


/**
 * This class is the Product entity which hold all the information of the Product
 *  entity with getters and setters for each attribute, 
 *  correspond to the 'product' table in database
 */
public class Product  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	// Attributes
	private int productCode;
	private String protductName;
	private float productPrice;
	private  byte[] image;
	private ProductCategory category;
	
	
	
	// Constructors
	public Product() {
	}

	public Product(int productCode) {
		this.productCode=productCode;
		}
		
	public Product(int productCode, String protductName, float productPrice,byte[] image ,ProductCategory category ) {
		super();
		this.productCode = productCode;
		this.protductName = protductName;
		this.productPrice = productPrice;
		this.image=image;
		this.category=category;
	}
	
	
	public Product(int productCode, String protductName) {
		this.productCode=productCode;
		this.protductName=protductName;
	}
	
	public Product(int productCode, String protductName, ProductCategory category) {
		this.productCode=productCode;
		this.protductName=protductName;
		this.category=category;
	}

	// Getters and setters
	public int getProductCode() {
		return productCode;
	}

	public void setProductCode(int productCode) {
		this.productCode = productCode;
	}

	public String getProtductName() {
		return protductName;
	}

	public void setProtductName(String protductName) {
		this.protductName = protductName;
	}

	public float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(float productPrice) {
		this.productPrice = productPrice;
	}


	public ProductCategory getCategory() {
		return category;
	}
	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public byte[] getPhoto() {
		return image;
	}

	public void setPhoto(byte[] image) {
		this.image = image;
	}
	
}