package entities;
import java.io.Serializable;
import java.util.HashMap;
import Enum.Area;
import Enum.ReportType;

/**
 * 			 CustomerReport  Include data to create Histogram Chart Of
 *         Customers The Graph will show 10%,20%.....100% Distbution , Based On
 *         Money Customer Spent In Area In specific Month.
 *         Also store Biggest Order Count UserName,Most Expense User
 */
public class CustomerReport extends Report implements Serializable {
	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 1L;

	// Attributes
	private String customerTempString;
	private String biggestOrderCountUser;
	private String mostExpenseUser;

	// Hash Map -> KEY=FacilityId Value =Number Of Orders
	private HashMap<String, Integer> customerHashMap = new HashMap<String, Integer>();

	/* Contractor */
	public CustomerReport(int reportId, Area area, ReportType reportType, int year, int month,
			String customerTempString, String biggestOrderCountUser, String mostExpenseUser) {
		super(reportId, area, reportType, year, month);
		this.customerTempString = customerTempString;
		this.biggestOrderCountUser = biggestOrderCountUser;
		this.mostExpenseUser = mostExpenseUser;
	}

	/* Empty Contractor */
	public CustomerReport() {
		super();
	}

	/* Getters And Setters */

	public String getTempFacilities() {
		return customerTempString;
	}

	/**
	 * @param tempFacilities is a string from the type = "Braude,5,Haifa,4" we
	 *                       create from this string by parse HashMap -> KEY =
	 *                       FacilityId Value = Number Of Orders
	 */
	public void setCustomerTempString(String tempFacilities) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String[] items = tempFacilities.split(",");
		for (int i = 0; i < items.length - 1; i += 2) {
			String key = items[i];
			int value = Integer.parseInt(items[i + 1]);
			map.put(key, value);
		}
		this.customerHashMap = map;
	}
	public String getBiggestOrderCountUser() {
		return biggestOrderCountUser;
	}

	public void setBiggestOrderCountUser(String biggestOrderCountUser) {
		this.biggestOrderCountUser = biggestOrderCountUser;
	}

	public String getMostExpenseUser() {
		return mostExpenseUser;
	}

	public void setMostExpenseUser(String mostExpenseUser) {
		this.mostExpenseUser = mostExpenseUser;
	}

	public HashMap<String, Integer> getCustomerHashMap() {
		return customerHashMap;
	}

	public void setCustomerHashMap(HashMap<String, Integer> customerHashMap) {
		this.customerHashMap = customerHashMap;
	}


}
