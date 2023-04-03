package entities;

import java.io.Serializable;

import Enum.Area;
import Enum.ReportType;

/**
 * This class is the Report entity which hold all the information of the Report entity with 
 * getters and setters for each attribute
 */
public class Report implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// Attributes
	private static int lastId = 0;
	private int reportId;
	private Area area;
	private ReportType reportType;
	private int year;
	private int month;	

	// Constructors
	public Report(int reportId, Area area, ReportType reportType, int year, int month) {
		super();
		this.reportId = reportId;
		this.area = area;
		this.reportType = reportType;
		this.year = year;
		this.month = month;
	}
	
	public Report() {
		super();
		this.reportId = ++lastId;
	}
	
	// getters and setters
	
	public static int getLastId() {
		return lastId;
	}

	public static void setLastId(int lastId) {
		Report.lastId = lastId;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}


	
}
