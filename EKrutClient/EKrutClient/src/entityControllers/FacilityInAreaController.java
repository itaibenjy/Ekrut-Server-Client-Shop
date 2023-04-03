package entityControllers;

import java.util.ArrayList;

import entities.Facility;


/**
 * @author Yotam Aharon 
 * FacilityInAreaController saves an ArrayList of all the facilities in some area.
 */
public class FacilityInAreaController {
	
	//Attributes
	private static ArrayList<Facility> facilityList=null;
	
	//Constructors
	public FacilityInAreaController() {
	}

	public FacilityInAreaController(ArrayList<Facility> facilityList) {
		FacilityInAreaController.facilityList = facilityList;
	}

	//Getters and Setters
	public void setFacilityInArea(ArrayList<Facility> facilityList) {
		FacilityInAreaController.facilityList = facilityList;
	}
	
	public ArrayList<Facility>  getFacilityInArea() {
		return FacilityInAreaController.facilityList;
	}
	

}
