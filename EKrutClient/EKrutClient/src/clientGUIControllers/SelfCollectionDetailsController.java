package clientGUIControllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Area;
import Enum.Instruction;
import entities.Facility;
import entities.ServerMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * The SelfCollectionDetailsController class is a controller class that handles
 * the logic and data of the self collection of the user's remote order. The
 * class has methods for handling user interactions with the pressed buttons ,
 * ComboBoxes, such as the areaComboPressed() method which gets a list of
 * facilities in a certain area from the server and populates the facility
 * ComboBox, a checkIfValidInput() method which checks that the user has
 * selected an area and facility, and that the date input is valid.
 * 
 */

public class SelfCollectionDetailsController {

	/**
	 * FXML Attributes
	 */
	@FXML
	private ComboBox<Area> areaCombo;

	@FXML
	private DatePicker datePicker;

	@FXML
	private Label errorLabel;

	@FXML
	private ComboBox<String> facilityCombo;

	@FXML
	private AnchorPane shipmentPane;

	@FXML
	void facilityComboPressed(ActionEvent event) {

	}

	/**
	 * Entities And Controllers.
	 */
	private ObservableList<Area> area = FXCollections.observableArrayList(Area.North, Area.South, Area.UAE);
	private ObservableList<String> facilitesObservableList = FXCollections.observableArrayList();
	private static ArrayList<Facility> facilityList = null;

	/**
	 * The initialize method is called automatically when the FXML file is loaded.
	 * Sets the area ComboBox with a list of areas.
	 */
	@FXML
	public void initialize() {
		this.areaCombo.setItems(this.area);
	}

	/**
	 * Handles the event when the area ComboBox is pressed. Gets a list of
	 * facilities in the selected area from the DB and sets the facility ComboBox.
	 * If there are no facility in this area for self collection order display error
	 * message.
	 * 
	 * @param event the ActionEvent
	 */
	@FXML
	void areaComboPressed(ActionEvent event) {
		this.errorLabel.setVisible(false);
		// Get list of facilities in a certain area
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("Area", this.areaCombo.getValue());
		ServerMessage msg = new ServerMessage(Instruction.Get_facilities_in_Area, data);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		;

		if ((facilityList == null) || (facilityList.get(0).getFacilityId().equals("OperationCenterNorth"))
				|| (facilityList.get(0).getFacilityId().equals("OperationCenterUAE"))
				|| (facilityList.get(0).getFacilityId().equals("OperationCenterSouth"))) {
			this.facilitesObservableList.clear();
			this.errorLabel.setVisible(true);
			this.errorLabel.setText("Threr is no facility in the selected area, Please select diffrent area.");
		}

		else {
			facilitesObservableList.clear();
			for (int i = 0; i < facilityList.size(); i++) {
				if (!(facilityList.get(i).getFacilityId().equals("OperationCenterNorth"))
						&& !(facilityList.get(i).getFacilityId().equals("OperationCenterUAE"))
						&& !(facilityList.get(i).getFacilityId().equals("OperationCenterSouth")))
					facilitesObservableList.add(facilityList.get(i).getFacilityId());
			}
			this.facilityCombo.setItems(this.facilitesObservableList);

		}
	}

	/**
	 * This method checks that area and facility id were selected.
	 * 
	 * @return true if the area and facility are selected, false otherwise
	 */
	public boolean checkIfValidInput() {
		if (this.areaCombo.getValue() == null) {
			this.errorLabel.setVisible(true);
			this.errorLabel.setText("You have to select area!");
			return false;

		} else if (this.facilityCombo.getValue() == null) {
			this.errorLabel.setVisible(true);
			this.errorLabel.setText("You have to select facility!");
			return false;

		} else
			return checkDate();
	}

	/**
	 * This method checks the validity of date input.
	 * 
	 * @return true if the date input is valid, false otherwise
	 */
	public boolean checkDate() {
		if (this.datePicker.getValue() != null) {
			errorLabel.setVisible(true);
			LocalDate DateNow = LocalDate.now();
			if (DateNow.getYear() > this.datePicker.getValue().getYear()) {
				this.errorLabel.setText("The Year MUST be higher (Or equals) then " + DateNow.getYear() + " !");
				return false;
			} else if ((DateNow.getYear() == this.datePicker.getValue().getYear())
					&& (DateNow.getMonthValue() > this.datePicker.getValue().getMonthValue())) {
				this.errorLabel.setText("The Month MUST be higher (Or equals) then " + DateNow.getMonthValue() + " !");
				return false;
			} else if ((DateNow.getMonthValue() == this.datePicker.getValue().getMonthValue())
					&& (DateNow.getDayOfMonth() > this.datePicker.getValue().getDayOfMonth())) {
				this.errorLabel
						.setText("Day of the month MUST be higher (Or equals) then " + DateNow.getDayOfMonth() + " !");
				return false;
			} else {
				return true;
			}
		} else {
			this.errorLabel.setText("You Must select date of picking up your order!");
			return false;
		}
	}

	/**
	 * This method returns facilities list of specified area.
	 * 
	 * @return ArrayList of type Facility is the facilities list of specified area.
	 */
	public static ArrayList<Facility> getFacilityList() {
		return facilityList;
	}

	/**
	 * This method set facilities list of specified area.
	 * 
	 * @param facilityList ArrayList of type Facility is the facilities list of specified area.
	 */
	public static void setFacilityList(ArrayList<Facility> facilityList) {
		SelfCollectionDetailsController.facilityList = facilityList;
	}

	/**
	 * This method returns the selected date.
	 * 
	 * @return LocalDate the selected date.
	 */
	public LocalDate getEstmatePickUpDate() {
		return datePicker.getValue();
	}

	// This method returns the selected facility id from the facilities list.
	/**
	 * This method returns the selected facility id.
	 * 
	 * @return String the selected facility id.
	 */
	public String getFacilityId() {
		return facilityCombo.getValue();
	}

}
