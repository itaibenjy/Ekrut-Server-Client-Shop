package clientGUIControllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Enum.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


/**
 *The ShipmentDetailsController class is a controller class that handles the logic and data of the shipment of the user's remote order.
 *It is responsible for initializing the countryCombo with a list of countries, 
 *validating the input fields and setting and getting the address details.
 */

public class ShipmentDetailsController {


	/**
	 *FXML Attributes
	 */
	@FXML
	private TextField houseNumTextField;

	@FXML
	private TextField cityTextField;

	@FXML
	private Label errorLabel;

	@FXML
	private AnchorPane shipmentPane;

	@FXML
	private TextField streetTextField;

	@FXML
	private ComboBox<Country> countryCombo;


	/**
	 *Variables.
	 */
	private String fullAddress;
	private ObservableList<Country> country = FXCollections.observableArrayList(Country.ISRAEL,Country.UAE);


	/**
	*The initialize method is called automatically when the FXML file is loaded.
	*It sets the error label to be not visible and sets the items for the countryCombo to the country list.
	*/
	@FXML
	public void initialize() {

		this.errorLabel.setVisible(false);
		this.countryCombo.setItems(this.country);

	}

	
	
	/**
	*The setAddress method is used to set the full address by concatenating the text from the cityTextField, streetTextField, and houseNumTextField.
	*The full address is stored in the fullAddress variable.
	*This method creates one string with all the address details.
	*/
	public void setAddress() {
		fullAddress=cityTextField.getText()+" "+streetTextField.getText()+" "+houseNumTextField.getText();

	}


	/**
	*The getFullAddress method returns the address input as a one string with all the address details.
	*@return a string representing the full address.
	*/
	public String getFullAddress() {
		return fullAddress;
	}

	/**
	*The setFullAddress method is used to set the full address.
	*@param fullAddress a string representing the full address.
	*/
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}


	
	
	/**
	*The AbstractTest method is a private method that checks the integrity of the input for text fields.
	*It verifies that the input is valid according to the type of characters and possible input length.
	*If the input is invalid, the errorLabel is set to be visible and the error message is displayed.
	*@param testFieldInput the input to be checked.
	*@param maxBound the maximum allowed length for the input.
	*@param TextFieldName the name of the text field the input belongs to.
	*@param charBound a regular expression defining the acceptable characters for the input.
	*@param ErrorMsg the error message to be displayed if the input is invalid.
	*@return a boolean indicating whether the input is valid or not.
	*/
	private boolean AbstractTest(String testFieldInput, int maxBound, String TextFieldName, String charBound, String ErrorMsg) {
		if (testFieldInput.length() != 0 && testFieldInput.length() <= maxBound) {
			if (!this.checkString(charBound, testFieldInput)) {
				this.errorLabel.setVisible(true);
				this.errorLabel.setText(ErrorMsg);
				return false;
			} else {
				return true;
			}
		} else {
			this.errorLabel.setVisible(true);
			this.errorLabel.setText(TextFieldName + " cannot be empty and max input length is " + maxBound);
			return false;
		}
	}


	/**
	*The checkFieldsInfo method is used to check the correctness of the input for all fields.
	*It checks if the countryCombo has a selected value, and 
	*also checks the integrity of the input for cityTextField, streetTextField and houseNumTextField using the AbstractTest method.
	*If any of the input is invalid, the errorLabel is set to be visible and the error message is displayed.
	*@return a boolean indicating whether all the input is valid or not.
	*/
	public boolean checkFieldsInfo() {
		if(this.countryCombo.getValue()==null) {
			this.errorLabel.setVisible(true);
			this.errorLabel.setText("You have to select country!");
			return false;
		}else if (!this.AbstractTest(this.cityTextField.getText(), 20, "City", "^[ A-Za-z]+$", "City can only contain English letters!")) {
			return false;
		} else if (!this.AbstractTest(this.streetTextField.getText(), 20, "Street", "^[ A-Za-z]+$", "Street can only contain English letters!")) {
			return false;
		} else return(this.AbstractTest(this.houseNumTextField.getText(),5, "House Number", "[0-9]+$", "House number can only contain Numbers!")); 
	}

	
	/**
	*The checkString method is used to check if a string matches a certain pattern.
	*The pattern is defined using a regular expression and is passed as an argument to the method.
	*The method uses the Pattern and Matcher classes to perform the check.
	*Pattern Class - Defines a pattern (to be used in a search).
	*Matcher Class - Used to search for the pattern.
	*@param matchingChars a regular expression defining the pattern to match.
	*@param toCheck the string to check against the patter.
	*@return a boolean indicating whether the string matches the pattern or not.
	*/
	boolean checkString(String matchingChars, String toCheck) {
		Pattern p = Pattern.compile(matchingChars);
		Matcher m = p.matcher(toCheck);
		return m.matches();
	}



	/**
	*The getSelectedCountry method is used to return the selected country from the countryCombo.
	*@return a Country object representing the selected country.
	*/
	public Country getSelectedCountry() {
		return countryCombo.getValue();
	}


}
