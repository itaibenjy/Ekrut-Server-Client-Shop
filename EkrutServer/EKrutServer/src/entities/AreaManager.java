package entities;

import java.io.Serializable;
import java.util.ArrayList;

import Enum.Area;

/**
 *  AreaManager class is a subclass of the User class and represents an area manager in the system. 
 *  It has two instance variables: area, which stores the area that the area manager is responsible for, 
 *  and facilities, which stores a list of facilities that the area manager is responsible for.
 */
public class AreaManager extends User implements Serializable{
	

	private static final long serialVersionUID = 1L;
	// Attributes
	private Area area;
	private ArrayList<Facility> facilities;
	
	// Constructors
	public AreaManager() {
		super();
	}

	public AreaManager(AreaManager areaManager) {
		this.area=areaManager.getArea();
		this.facilities=areaManager.getFacilities();
	}

	public AreaManager(String username, String password, int id) {
		super(username, password, id);
	}

	public AreaManager(String username, String password, int id, Area area) {
		super(username, password, id);
		this.area = area;
	}

	// Methods
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public ArrayList<Facility> getFacilities() {
		return facilities;
	}

	public void setFacilities(ArrayList<Facility> facilities) {
		this.facilities = facilities;
	}
}
