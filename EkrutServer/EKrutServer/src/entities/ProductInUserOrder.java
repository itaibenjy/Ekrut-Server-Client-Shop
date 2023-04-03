package entities;

/**
 * This class is the ProductInUserOrder entity which hold all the information of the ProductInUserOrder
 *  entity with getters and setters for each attribute, the purpose is to be able to display this 
 *  information in a table view.
 */
public class ProductInUserOrder {
	// Attributes
	private String productName;
	private Integer quantity;
	private String price;
	
	// Constructors
	public ProductInUserOrder() {
		
	}
	public ProductInUserOrder(String productName) {
		this.productName=productName;
	}
	public ProductInUserOrder(String productName,Integer quantity,String price) {
		this.productName=productName;
		this.quantity=quantity;
		this.price=price;
	}
	
	// Getters and Setters
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
}
