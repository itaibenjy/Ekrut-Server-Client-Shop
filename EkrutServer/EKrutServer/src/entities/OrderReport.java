package entities;

import java.io.Serializable;
import java.util.HashMap;

import Enum.Area;
import Enum.ReportType;

/**
 * OrderReport Class to present Order Report For Managers
 *
 */
public class OrderReport extends Report implements Serializable {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 1L;

	// Attributes
	private String tempFacilities;
	private int numOfTotalOrders;
	private float totalPrice;
	private int selfColletionTotal;
	private int localColletionTotal;
	private int shipmentColletionTotal;
	
	// Hash Map -> KEY=FacilityId Value =Number Of Orders
	private HashMap<String, Integer> facilitiesHashMap = new HashMap<String, Integer>();

	/* Contractor */
	public OrderReport(int reportId, Area area, ReportType reportType, int year, int month, String tempFacilities,
			int numOfTotalOrders, float totalPrice, int selfColletionTotal, int localColletionTotal, int shipmentColletionTotal) {

		super(reportId, area, reportType, year, month);
		this.tempFacilities = tempFacilities;
		this.numOfTotalOrders = numOfTotalOrders;
		this.totalPrice = totalPrice;
		this.selfColletionTotal = selfColletionTotal;
		this.localColletionTotal = localColletionTotal;
		this.shipmentColletionTotal = shipmentColletionTotal;
		
	}

	/* Empty Contractor */
	public OrderReport() {
		super();
	}

	/* Getters And Setters */

	public String getTempFacilities() {
		return tempFacilities;
	}

	/**
	 * @param tempFacilities is a string from the type = "Braude,5,Haifa,4" we
	 *                       create from this string by parse HashMap -> KEY =
	 *                       FacilityId Value = Number Of Orders
	 */
	public void settempFacilities(String tempFacilities) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String[] items = tempFacilities.split(",");
		for (int i = 0; i < items.length - 1; i += 2) {
			String key = items[i];
			int value = Integer.parseInt(items[i + 1]);
			map.put(key, value);
		}
		this.facilitiesHashMap = map;
	}

	public int getNumOfTotalOrders() {
		return numOfTotalOrders;
	}

	public void setNumOfTotalOrders(int numOfTotalOrders) {
		this.numOfTotalOrders = numOfTotalOrders;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getSelfColletionTotal() {
		return selfColletionTotal;
	}

	public void setSelfColletionTotal(int selfColletionTotal) {
		this.selfColletionTotal = selfColletionTotal;
	}

	public int getLocalColletionTotal() {
		return localColletionTotal;
	}

	public void setLocalColletionTotal(int localColletionTotal) {
		this.localColletionTotal = localColletionTotal;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public HashMap<String, Integer> getFacilitiesHashMap() {
		return facilitiesHashMap;
	}

	public void setFacilitiesHashMap(HashMap<String, Integer> facilitiesHashMap) {
		this.facilitiesHashMap = facilitiesHashMap;
	}
	public int getShipmentColletionTotal() {
		return shipmentColletionTotal;
	}

	public void setShipmentColletionTotal(int shipmentColletionTotal) {
		this.shipmentColletionTotal = shipmentColletionTotal;
	}

}
