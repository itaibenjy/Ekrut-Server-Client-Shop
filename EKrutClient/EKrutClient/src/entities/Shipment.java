package entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;

import Enum.Country;
import Enum.CustomerShippmentStatus;
import Enum.ProccessStatus;
import Enum.SupplyMethod;

/**
 * This class is the Shipment entity which hold all the information of the Shipment
 *  entity with getters and setters for each attribute, 
 *  correspond to the 'ordershipment' table in database
  * extends the Order entity (hold all the information of the Shipment order)
 */
public class Shipment extends Order implements Serializable  {
	
	// Attributes
	private static final long serialVersionUID = 1L;
	private String deliveryAddress;
	private Date estimateDeliveryDate;
	private ProccessStatus shippmentOrderStatus;
	private CustomerShippmentStatus customerShippmentStatus;
	private Country shippmentCountry;
	

	// Constructors
	public Shipment() {
		super();
	}
	
	public Shipment(int orderNum,HashMap<Product, Integer> orderProductList1,String facilityId,float totalPrice,SupplyMethod supplyMethod,Date orderDate,String userName,String deliveryAddress,Date estimateDeliveryDate,ProccessStatus shippmentOrderStatus,CustomerShippmentStatus customerShippmentStatus,Country shippmentCountry) {
		super(orderNum,orderProductList1,facilityId,totalPrice,supplyMethod,orderDate,userName);
		this.deliveryAddress=deliveryAddress;
		this.estimateDeliveryDate=estimateDeliveryDate;
		this.shippmentOrderStatus=shippmentOrderStatus;
		this.customerShippmentStatus=customerShippmentStatus;
		this.shippmentCountry=shippmentCountry;
	}
	
	public Shipment(String deliveryAddress, Date estimateDeliveryDate, ProccessStatus shippmentOrderStatus) {
		super();
		this.deliveryAddress = deliveryAddress;
		this.estimateDeliveryDate = estimateDeliveryDate;
		this.shippmentOrderStatus = shippmentOrderStatus;
	}

	// Getters and Setters
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public Date getEstimateDeliveryDate() {
		return estimateDeliveryDate;
	}
	public void setEstimateDeliveryDate(Date estimateDeliveryDate) {
		this.estimateDeliveryDate = estimateDeliveryDate;
	}
	public ProccessStatus getShippmentOrderStatus() {
		return shippmentOrderStatus;
	}
	public void setShippmentOrderStatus(ProccessStatus shippmentOrderStatus) {
		this.shippmentOrderStatus = shippmentOrderStatus;
	}
	public CustomerShippmentStatus getCustomerShippmentStatus() {
		return customerShippmentStatus;
	}

	public void setCustomerShippmentStatus(CustomerShippmentStatus customerShippmentStatus) {
		this.customerShippmentStatus = customerShippmentStatus;
	}
	
	public Country getShippmentCountry() {
		return shippmentCountry;
	}

	public void setShippmentCountry(Country shippmentCountry) {
		this.shippmentCountry = shippmentCountry;
	}

}
