package entities;

import java.io.Serializable;

import Enum.ProductCategory;
import Enum.ProductStatus;

/**
 * This class is the ProductInFacility entity which hold all the information of the ProductInFacility
 *  entity with getters and setters for each attribute, 
 *  correspond to the 'productinfacility' table in database
 */
public class ProductInFacility extends Product  implements Serializable{
	
	// Attributes
	private static final long serialVersionUID = 1L;
	private int quantity;
	private ProductStatus productStatus;
	private int tresholdLevel;
	private int paseedThreshold;



	// Constructors
	public ProductInFacility() {
		super();
	}
	
	public ProductInFacility(int productCode , String protductName , float productPrice , byte[] photo,ProductCategory category,int quantity, ProductStatus productStatus,int tresholdLevel) {
		super(productCode,protductName,productPrice,photo,category);
		this.quantity = quantity;
		this.productStatus = productStatus;
		this.tresholdLevel=tresholdLevel;
	}

	public ProductInFacility(int productCode , String protductName , int quantity) {
		super(productCode , protductName);
		this.quantity = quantity;
	}
	
	
	public ProductInFacility(int productCode, String protductName, ProductCategory category, int quantity2) {
		super(productCode,protductName,category);
		this.quantity=quantity2;
	}

	// Getters and setters
	public int getPaseedThreshold() {
		return paseedThreshold;
	}

	public void setPaseedThreshold(int paseedThreshold) {
		this.paseedThreshold = paseedThreshold;
	}

	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public ProductStatus getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(ProductStatus productStatus) {
		this.productStatus = productStatus;
	}
	
	public int getTresholdLevel() {
		return tresholdLevel;
	}

	public void setTresholdLevel(int tresholdLevel) {
		this.tresholdLevel = tresholdLevel;
	}
	
	// To string
	@Override
	public String toString() {
		return super.toString()+"ProductInFacility [quantity=" + quantity + ", productStatus=" + productStatus + ", tresholdLevel="
				+ tresholdLevel + "]";
	}


	
}