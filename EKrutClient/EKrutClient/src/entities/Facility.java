package entities;

import java.io.Serializable;
import java.util.ArrayList;

import Enum.Area;

	/**
	 * The Facility class represents a facility that stores products.
	 * It has a unique identifier (facilityId), an area (facilityArea),
	 * and a list of products that are stored in the facility (productsInFacility).
	 */
public class Facility  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// Attributes
	private String facilityId;
	private Area facilityArea;
	private ArrayList<ProductInFacility> productsInFacility;
	
	// Constructors
	public Facility(String facilityId, Area facilityArea) {
		super();
		this.facilityId = facilityId;
		this.facilityArea = facilityArea;
	}
	public Facility() {
		super();
	}
	
	// Methods
	public String getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(String facilityId2) {
		this.facilityId = facilityId2;
	}
	public Area getFacilityArea() {
		return facilityArea;
	}
	public void setFacilityArea(Area facilityArea) {
		this.facilityArea = facilityArea;
	}
	public ArrayList<ProductInFacility> getProductsInFacility() {
		return productsInFacility;
	}
	public void setProductsInFacility(ArrayList<ProductInFacility> productsInFacility) {
		this.productsInFacility = productsInFacility;
	}

	
}
