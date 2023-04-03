package entities;

import java.io.Serializable;
import java.util.Date;

import Enum.Area;
import Enum.SaleStatus;

/**
 * This class is the Sale entity which hold all the information of the Sale
 *  entity with getters and setters for each attribute, 
 *  correspond to the 'sales' table in database
 */
public class Sale implements Serializable{
	
	// Attributes
	private static final long serialVersionUID = 1L;
	private static int lastSaleId = 0;
	private int saleId;
	private Area area;
	private String salePatternId;
	private Date startDate;
	private Date endDate;
	private SaleStatus saleStatus;


	// Constructors
	public Sale(Area area, String salePatternId, Date startDate, Date endDate,SaleStatus saleStatus) {
		super();
		this.saleId = ++lastSaleId;
		this.area = area;
		this.salePatternId = salePatternId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.saleStatus =saleStatus;
	}
	public Sale(int saleId, String salePatternId, Date startDate, Date endDate) {
		super();
		this.saleId = saleId;
		this.salePatternId = salePatternId;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Sale() {
		super();
		this.saleId = ++lastSaleId;
	}

	// Getters and Setters
	
	public static int getLastSaleId() {
		return lastSaleId;
	}

	public static void setLastSaleId(int lastSaleId) {
		Sale.lastSaleId = lastSaleId;
	}

	public int getSaleId() {
		return saleId;
	}

	public void setSaleId(int saleId) {
		this.saleId = saleId;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getSalePatternId() {
		return salePatternId;
	}

	public void setSalePatternId(String salePatternId) {
		this.salePatternId = salePatternId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
	
	public SaleStatus getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(SaleStatus saleStatus) {
		this.saleStatus = saleStatus;
	}
}
