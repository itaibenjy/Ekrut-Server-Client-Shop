package entities;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import Enum.SupplyMethod;

/**
 * This class is the Order entity which hold all the information of the Order
 * entity with getters and setters for each attribute, 
 *  correspond to the 'order' table in database
 */
public class Order  implements Serializable  {
	private static final long serialVersionUID = 1L;
	// Attributes
	private int orderNum;
	private HashMap<Product, Integer> orderProductList1;
	private ArrayList<Product> orderProductList;
	private String facilityId;
	private float totalPrice;
	private SupplyMethod supplyMethod;
	private Date orderDate;
	private String userName;

	
	// Constructors

	public Order() {
		super();
	}
	
	public Order(int orderNum,HashMap<Product, Integer> orderProductList1,String facilityId,float totalPrice,SupplyMethod supplyMethod,Date orderDate,String userName) {
		this.orderNum=orderNum;
		this.orderProductList1=orderProductList1;
		this.facilityId=facilityId;
		this.totalPrice=totalPrice;
		this.supplyMethod=supplyMethod;
		this.orderDate=orderDate;
		this.userName=userName;
	}
	
	// getters and setters
	public int getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	public HashMap<Product, Integer> getOrderProductList1() {
		return orderProductList1;
	}
	public void setOrderProductList1(HashMap<Product, Integer> orderProductList1) {
		this.orderProductList1 = orderProductList1;
	}
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public SupplyMethod getSupplyMethod() {
		return supplyMethod;
	}
	public void setSupplyMethod(SupplyMethod supplyMethod) {
		this.supplyMethod = supplyMethod;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setOrderProductList(ArrayList<Product> arrayList) {
		this.orderProductList =arrayList;
		
	}
	public ArrayList<Product> getOrderProductList() {
		return orderProductList;
	}

}
