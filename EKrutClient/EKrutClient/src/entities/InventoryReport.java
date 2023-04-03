package entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

import Enum.Area;
import Enum.ReportType;

/**
 * InventoryReport Class to present Inventory Report For Managers
 */
public class InventoryReport extends Report implements Serializable {
	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 1L;

	// Attributes
	private String facilityID;
	private String tempFacilitieInventory;
	private String passThresholdMax;
	private int countNotPassed;
	private int numOfTotalProducts;

	// Hash Map -> KEY=FacilityId Value =Number Of Orders
	private HashMap<String, Integer> facilitieInventory = new HashMap<String, Integer>();
	private HashMap<String, Integer> passedThresHold = new HashMap<String, Integer>();

	/* Contractor */
	public InventoryReport(int reportId, Area area, ReportType reportType, int year, int month, String facilityID,
			String tempFacilitieInventory, String passThresholdMax, int countNotPassed, int numOfTotalProducts,String tempPassedThresHold) {
		super(reportId, area, reportType, year, month);
		this.facilityID = facilityID;
		this.passThresholdMax = passThresholdMax;
		this.countNotPassed = countNotPassed;
		this.numOfTotalProducts = numOfTotalProducts;
		setfacilitieInventory(tempFacilitieInventory);
		setPassedThresHold(tempPassedThresHold);
	}

	public InventoryReport(int reportId, Area area, ReportType reportType, int year, int month) {
		super(reportId, area, reportType, year, month);
	}


	public InventoryReport() {
		// TODO Auto-generated constructor stub
	}
	


	/**
	 * @param tempFacilities is a string from the type = "cola,5,fanta,4" we
	 *                       create from this string by parse HashMap -> KEY =
	 *                       product Value = quantity in facility
	 */
	public void setfacilitieInventory(String tempFacilities) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String[] items = tempFacilities.split(",");
		for (int i = 0; i < items.length - 1; i += 2) {
			String key = items[i];
			int value = Integer.parseInt(items[i + 1]);
			map.put(key, value);
		}
		this.facilitieInventory = map;
	}
	/**
	 * @param passedThresHoldString is a string from the type = "cola,5,fanta,4" we
	 *                       create from this string by parse HashMap -> KEY =
	 *                       Product Value = Number of times passed thershold level
	 */
	public void setPassedThresHold(String passedThresHoldString) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String[] items = passedThresHoldString.split(",");
		for (int i = 0; i < items.length - 1; i += 2) {
			String key = items[i];
			int value = Integer.parseInt(items[i + 1]);
			map.put(key, value);
		}
		this.passedThresHold = map;
	}
	
	// Simple getters and setter
	public String getFacilityID() {
		return facilityID;
	}

	public void setFacilityID(String facilityID) {
		this.facilityID = facilityID;
	}

	public String getTempFacilitieInventory() {
		return tempFacilitieInventory;
	}

	public void setTempFacilitieInventory(String tempFacilitieInventory) {
		this.tempFacilitieInventory = tempFacilitieInventory;
	}

	public String getBiggestInventory() {
		return passThresholdMax;
	}

	public void setPassThresholdMax(String biggestInventory) {
		this.passThresholdMax = biggestInventory;
	}

	public int getSmallInventoroy() {
		return countNotPassed;
	}

	public void setSmallInventoroy(int countNotPassed) {
		this.countNotPassed = countNotPassed;
	}

	public int getNumOfTotalProducts() {
		return numOfTotalProducts;
	}

	public void setNumOfTotalProducts(int numOfTotalProducts) {
		this.numOfTotalProducts = numOfTotalProducts;
	}

	public HashMap<String, Integer> getFacilitieInventory() {
		return facilitieInventory;
	}

	public HashMap<String, Integer> getPassedThresHold() {
		return passedThresHold;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// equals and hashcode
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		InventoryReport other = (InventoryReport) obj;
		return Objects.equals(passThresholdMax, other.passThresholdMax)
				&& Objects.equals(facilitieInventory, other.facilitieInventory)
				&& numOfTotalProducts == other.numOfTotalProducts
				&& Objects.equals(countNotPassed, other.countNotPassed)
				&& Objects.equals(tempFacilitieInventory, other.tempFacilitieInventory);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(passThresholdMax, facilitieInventory, numOfTotalProducts,
				countNotPassed, tempFacilitieInventory);
		return result;
	}


}
