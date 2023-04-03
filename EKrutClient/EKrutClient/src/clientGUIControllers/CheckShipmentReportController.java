package clientGUIControllers;

import java.util.Hashtable;

import Client.EkrutClientUI;
import Enum.Instruction;
import common.CommonActionsController;
import entities.ServerMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class CheckShipmentReportController {

    @FXML
    private Button backButton;

    @FXML
    private Button logOut;

    @FXML
    private ImageView logo;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private Button xButton;

    @FXML
    private Button generateReportButton;

    @FXML
    private Label errorlabel;
    
    @FXML
    private Label errMonth;

    @FXML
    private Label errYear;

    @FXML
    private ImageView droneImage;
    
    
    private static boolean reportexist=false;
  
    String monthSelected=null,yearSelected=null;
    private CommonActionsController commonActionsController = new CommonActionsController();
    private ObservableList<String> yearOptions = FXCollections.observableArrayList("2021", "2022", "2023","2024", "2025", "2026");
    private ObservableList<String> monthOptions = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
			"11", "12");
    
 

    @FXML
	private void initialize() {
		monthComboBox.setItems(monthOptions);
		yearComboBox.setItems(yearOptions);
		//droneImage.setImage(new Image("/viewCatalogScreenPic/drone.gif"));
	
	}
    
    
    
    @FXML
    void monthComboBoxPressed(ActionEvent event) {
    	monthSelected=monthComboBox.getValue();
    }

 

    @FXML
    void yearComboBoxPressed(ActionEvent event) {
    	yearSelected=yearComboBox.getValue();
    }
    
    
    @FXML
    void generateReportButton_Pressed(ActionEvent event) {
    	errMonth.setVisible(false);
    	errYear.setVisible(false);
    	boolean vaildData=true;
    	if (yearSelected==null) {
    		errYear.setVisible(true);
    		vaildData=false;
    	}
    	if (monthSelected==null) {
    		errMonth.setVisible(true);
    		vaildData=false;
    	}
    	if(vaildData)
    		generateShipmentReport(event);
    	
    }

 
    private void generateShipmentReport(ActionEvent event) {
		ServerMessage msg;
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("Year", yearSelected);
		data.put("Month", monthSelected);
		msg = new ServerMessage(Instruction.Shipment_Report_Search, data);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {	}; // wait for server update
		if( reportexist)
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/ShipmentReportScreen.fxml");
		else {
			errorlabel.setVisible(true);

		}
	}



	/**
	 * @param event log Out Button Pressed log out the user
	 */
	@FXML
	void logOut_ButtonPressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}

	/**
	 * @param event X Button Pressed method closes the window
	 */
	@FXML
	void x_ButtonPressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}

	/**
	 * @param event Back Button Pressed , Back To AreaManagerScreen.fxml
	 */
	@FXML
	void backButton_ButtonPressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/CEOScreen.fxml");
	}



	public static void setReportexist(boolean reportexist) {
		CheckShipmentReportController.reportexist = reportexist;
	}

}
