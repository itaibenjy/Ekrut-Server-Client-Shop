package entities;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;

import Enum.SupplyMethod;

/**
 * This class is the SelfCollection entity which hold all the information of the SelfCollection
 *  entity with getters and setters for each attribute, 
 *  correspond to the 'orderselfcollection' table in database
  * extends the Order entity (hold all the information of the Self Collection order)
 */
public class SelfCollection extends Order implements Serializable{

	// Attributes
	private static final long serialVersionUID = 1L;
	private LocalDate estimatePickUpDate;
	private LocalDate actualPickUpDate=null;


	// Constructors

	public SelfCollection() {
		super();
	}
	
	public SelfCollection(int orderNum,HashMap<Product, Integer> orderProductList1,String facilityId,float totalPrice,SupplyMethod supplyMethod,Date orderDate,String userName,LocalDate estimatePickUpDate,LocalDate actualPickUpDate) {
		super(orderNum,orderProductList1,facilityId,totalPrice,supplyMethod,orderDate,userName);
		this.estimatePickUpDate=estimatePickUpDate;
		this.actualPickUpDate=actualPickUpDate;
	}
	
	// Getters and Setters

	public LocalDate getEstimatePickUpDate() {
		return estimatePickUpDate;
	}


	public void setEstimatePickUpDate(LocalDate estimatePickUpDate) {
		this.estimatePickUpDate = estimatePickUpDate;
	}

	public LocalDate getActualPickUpDate() {
		return actualPickUpDate;
	}

	public void setActualPickUpDate(LocalDate actualPickUpDate) {
		this.actualPickUpDate = actualPickUpDate;
	}


}
