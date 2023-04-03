package entities;

import java.io.Serializable;

/**
 * This class is the SalePattern entity which hold all the information of the SalePattern
 *  entity with getters and setters for each attribute, 
 *  correspond to the 'salepattern' table in database
 */
public class SalePattern implements Serializable{
	
	// Attributes
	private static final long serialVersionUID = 1L;
	private int salePatternId;
	private String description;
	private int discount_total;
	private int startHour;
	private int endHour;
	
	// Constructors
	public SalePattern() {
		
	}
	
	public SalePattern(int salePatternId, String description, int discount_total, int startHour, int endHour) {
		super();
		this.salePatternId = salePatternId;
		this.description = description;
		this.discount_total = discount_total;
		this.startHour = startHour;
		this.endHour = endHour;
	}

	// Getters and Setters
	public int getSalePatternId() {
		return salePatternId;
	}
	public void setSalePatternId(int salePatternId) {
		this.salePatternId = salePatternId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int getDiscount_total() {
		return discount_total;
	}

	public void setDiscount_total(int discount_total) {
		this.discount_total = discount_total;
	}
	public int getStartHour() {
		return startHour;
	}
	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}
	public int getEndHour() {
		return endHour;
	}
	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}


}
