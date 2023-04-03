package clientGUIControllers;
import Enum.Area;
import java.util.ArrayList;
import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Instruction;
import common.CommonActionsController;
import entities.CustomerReport;
import entities.Facility;
import entities.InventoryReport;
import entities.OrderReport;
import entities.ServerMessage;
import entityControllers.AreaManagerEntityController;
import entityControllers.Customer_ReportEntityController;
import entityControllers.Inventory_ReportEntityController;
import entityControllers.Order_ReportEntityController;
import entityControllers.UserController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * View Report Controller
 * Screen for selecting reports, for managers.
 * according to a certain year and type of report
 */
public class ViewReportController {

	/*-----------FXML----------*/
	@FXML
	private ComboBox<String> monthComboBox;

	@FXML
	private ComboBox<String> yearComboBox;

	@FXML
	private ComboBox<String> typeComboBox;

	@FXML
	private ComboBox<String> selectFacility_ComboBox;

	@FXML
	private Button backButton;

	@FXML
	private Button logOut;

	@FXML
	private Button xButton;

	@FXML
	private Button help;

	@FXML
	private Button generateReportButton;
	@FXML
	private Label errorlabel;

	@FXML
	private Label selectFacility;

	@FXML
	private Label areaLabel;


	@FXML
	private Label areaLabelUnderline;


	@FXML
	private Text explains;

	@FXML
	private ImageView logoImage;

	@FXML
	private ImageView reportGif;


	@FXML
	private ImageView areaLogo;

	@FXML
	private ComboBox<String> areaComboBox;

	@FXML
	private Label msgForArea;

	@FXML
	private Label selectAreaLabel;

	@FXML
	private Label errYear;
	@FXML
	private Label errMonth;
	@FXML
	private Label errType;

	@FXML
	private Label errArea;

	@FXML
	private Label errFacility;

	/* Entities And Controller */
	Image logoI, reportI, managerI, locationI;

	private UserController userController = new UserController();
	private AreaManagerEntityController entity_areaManagerController = new AreaManagerEntityController();
	private CommonActionsController commonActionsController = new CommonActionsController();
	private String typeReportSelected, yearSelected, monthSelected, modifiedString, facilitySelected = null,
			area_Selected = null;
	private Order_ReportEntityController order_ReportEntityController = new Order_ReportEntityController();
	private Customer_ReportEntityController customer_ReportEntityController = new Customer_ReportEntityController();
	private Inventory_ReportEntityController inventory_ReportEntityController = new Inventory_ReportEntityController();

	/* Data */
	private ArrayList<Facility> facilities;
	private ObservableList<String> facilityList = FXCollections.observableArrayList();
	private ObservableList<String> areaOptions = FXCollections.observableArrayList("North", "South", "UAE");
	private ObservableList<String> month_ComboBox_Values;
	private ObservableList<String> year_ComboBox_Values;
	private ObservableList<String> report_ComboBox_Values;
	private static ArrayList<Facility> facilityList_CEO = null;
	private IReportManager reportManager=new ReportManager();

	/** 
	 * The initialize method is used to set the values for the combo boxes and the
	 * text
	 */
	@FXML
	private void initialize() {
		reportGif.setImage(new Image("/ReportPhotos/search.gif"));
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		areaLogo.setImage(new Image("/location.png"));
		month_ComboBox_Values = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
				"11", "12");
		monthComboBox.setItems(month_ComboBox_Values);
		year_ComboBox_Values = FXCollections.observableArrayList("2020", "2021", "2022", "2023","2024");
		yearComboBox.setItems(year_ComboBox_Values);
		report_ComboBox_Values = FXCollections.observableArrayList("Inventory Report", "Customer Report",
				"Order Report");
		typeComboBox.setItems(report_ComboBox_Values);
		explains.setText(" On this page :\nManagers can view reports and analyze information from them. \nOnly after you choose all relevant paramrters from Combo-Box\nSystem will search for report.");

		if (userController.getUser().getRole().toString().equals("AreaManager")) {
			areaManagerScreen();
			facilities = entity_areaManagerController.getAreaManager().getFacilities();
			areaLabel.setVisible(true);
			areaLabel.setText(userController.getUser().getArea().toString());
		} else {
			areaLabel.setVisible(false);
			areaLabelUnderline.setVisible(false);
			areaComboBox.setItems(areaOptions);
			areaLogo.setVisible(false);
		}
	}

	/**
	 * This method will show the page relevant to Area Manager role
	 */
	private void areaManagerScreen() {
		selectAreaLabel.setVisible(false);
		areaComboBox.setVisible(false);
	}

	/**
	 * @param event - press on help display explenation
	 */
	@FXML
	void helpPress(ActionEvent event) {
		explains.setVisible(true);
		help.setVisible(false);
	}
	/**
	 * @param event Choose Year Combo Box
	 */
	@FXML
	void yearComboBoxPressed(ActionEvent event) {
		reportManager.makeLabelsUnVisible();
		yearSelected = yearComboBox.getValue();
	}

	/**
	 * @param event Choose Month Combo Box
	 */
	@FXML
	void monthComboBoxPressed(ActionEvent event) {
		reportManager.makeLabelsUnVisible();
		monthSelected = monthComboBox.getValue();
	}

	// Type
	/**
	 * @param event Choose Type Combo Box
	 */
	@FXML
	void selectTypeComboBoxPressed(ActionEvent event) {
		reportManager.makeLabelsUnVisible();
		typeReportSelected = typeComboBox.getValue();
		modifiedString = typeReportSelected.replace(" ", "");

		if (typeReportSelected == "Inventory Report") {

			selectFacility_ComboBox.setVisible(true);
			selectFacility.setVisible(true);

			// CEO
			if (!(userController.getUser().getRole().toString().equals("AreaManager"))) {
				if (area_Selected == null)
					msgForArea.setVisible(true);
				else {
					setListOfFacilities();
					selectFacility_ComboBox.setDisable(false);
				}
			}
			// AreaManager
			else {
				setListOfFacilities();
				selectFacility_ComboBox.setDisable(false);
			}

		} else {
			selectFacility_ComboBox.setVisible(false);
			selectFacility.setVisible(false);
		}
	}

	public String getTypeReportSelected() {
		return typeReportSelected;
	}

	/**
	 * @param event Choose Facility Combo Box
	 */
	@FXML
	void selectFacility_ComboBox_Pressed(ActionEvent event) {
		reportManager.makeLabelsUnVisible();
		facilitySelected = selectFacility_ComboBox.getValue();
	}

	/**
	 * Set Facility In Combo Box
	 */
	private void setListOfFacilities() {
		facilityList.clear();
		if (userController.getUser().getRole().toString().equals("AreaManager")) {
			for (Facility Val : facilities) {
				if (!(Val.getFacilityId().equals("OperationCenterNorth"))
						&& !(Val.getFacilityId().equals("OperationCenterUAE"))
						&& !(Val.getFacilityId().equals("OperationCenterSouth")))
					facilityList.add(Val.getFacilityId());
			}
			selectFacility_ComboBox.setItems(facilityList);
		} else
			addByRequestArea();

	}

	/**
	 * Add tArea to facility list
	 */
	private void addByRequestArea() {
		facilityList.clear();
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("Area", getEnumUserArea(area_Selected));
		ServerMessage msg = new ServerMessage(Instruction.Get_facilities_in_Area, data);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		; // wait for server update
		for (Facility Val : facilityList_CEO) {
			if (!(Val.getFacilityId().equals("OperationCenterNorth"))
					&& !(Val.getFacilityId().equals("OperationCenterUAE"))
					&& !(Val.getFacilityId().equals("OperationCenterSouth")))
				facilityList.add(Val.getFacilityId());
		}
		selectFacility_ComboBox.setItems(facilityList);
	}

	/**
	 * @param event Generate Report Button Pressed , move to wanted screen
	 */
	@FXML
	void generateReportButton_Pressed(ActionEvent event) {
		reportManager.getValues();
		reportManager.makeLabelsUnVisible();
		if (checkIfCorrectInputs()) {
			
			reportManager.ReportDetails();
			if (modifiedString.equals("CustomerReport") && customer_ReportEntityController.customerReportIsExist()) {
				reportManager.changeScreen(event, "/clientGUIScreens/CustomerReportScreen.fxml");
			} else if (modifiedString.equals("OrderReport") && order_ReportEntityController.ReportIsExist()) {
				reportManager.changeScreen(event, "/clientGUIScreens/OrderReportScreen.fxml");
			} else if ((modifiedString.equals("InventoryReport") && inventory_ReportEntityController.ReportIsExist())) {
				reportManager.changeScreen(event, "/clientGUIScreens/InventoryReportScreen.fxml");
			} else {
				reportManager.showErrLabel();
			}
		}

	}

	/**
	 * this method goes over the inputs in this page and check if its ok to generate
	 * a report
	 */
	private boolean checkIfCorrectInputs() {
		boolean vaildInputs = true;
		// private String typeReportSelected, yearSelected, monthSelected,
		// modifiedString, facilitySelected = null,
		// area_Selected = null;
		if (checkComboBox(yearSelected, errYear)) {
			
			reportManager.showErrYear();
			vaildInputs = false;
		}
		if (checkComboBox(monthSelected, errMonth)) {
			
			reportManager.showErrMonth();
			vaildInputs = false;
		}
		if (checkComboBox(typeReportSelected, errType)) {
			
			reportManager.showErrType();
			vaildInputs = false;
		}
		if (typeReportSelected != null && typeReportSelected.equals("Inventory Report")){
			if (checkComboBox(facilitySelected, errFacility)) {
				
				reportManager.showErrFacility();
				vaildInputs = false;
			}
		}
		if (userController.getUser().getRole().toString().equals("CEO")) {
			if (checkComboBox(area_Selected, errArea)) {
				reportManager.showErrArea();
				vaildInputs = false;
			}
		}
		return vaildInputs;
	}



	/**
	 * Check ComboBox
	 */
	private boolean checkComboBox(String comboBoxSelected, Label errLabel) {
		if (comboBoxSelected == null) {
			return true;
		}
		return false;
	}

	public static ArrayList<Facility> getFacilityList_CEO() {
		return facilityList_CEO;
	}

	public static void setFacilityList_CEO(ArrayList<Facility> facilityList_CEO) {
		ViewReportController.facilityList_CEO = facilityList_CEO;
	}

	/**
	 * @param event Area Combo Box Pressed
	 */
	@FXML
	void areaComboBoxPressed(ActionEvent event) {
		reportManager.makeLabelsUnVisible();
		msgForArea.setVisible(false);
		area_Selected = areaComboBox.getValue();
		selectFacility_ComboBox.setDisable(false);
		if (typeReportSelected == "Inventory Report")
			addByRequestArea();

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
		if (userController.getUser().getRole().toString().equals("AreaManager"))
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/AreaManagerScreen.fxml");
		else
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/CEOScreen.fxml");

	}


	/**
	 * 
	 *  Run and vertify we are using one of the Areas define
	 */
	private static Area getEnumUserArea(String userArea) {

		for (Area i : Area.values()) {
			if (userArea.equals(i.name())) {
				return i;
			}
		}
		return null;
	}


	//---------------------------------for tests--------------------------------------

	/** 
	 * Class for testing with property injection
	 */
	public class ReportManager implements IReportManager{

		/**
		 *This method displays the given error message on the error label.
		 *@param error - The error message to be displayed.
		 */
		public void showError(Label errLabelName,boolean val) {
			errLabelName.setVisible(val);
		}

		public void showErrLabel() {
			errorlabel.setVisible(true);
		}
		
		public void showErrYear() {
			errYear.setVisible(true);
		}
		
		public void showErrMonth() {
			errMonth.setVisible(true);
		}
		
		public void showErrType() {
			errType.setVisible(true);
		}
		
		public void showErrArea() {
			errArea.setVisible(true);
		}
		
		public void showErrFacility() {
			errFacility.setVisible(true);
		}

		/***
		 * 
		 *Get report details from DB according the user selected values (year,month,type,area and facility).
		 * 
		 */
		public void ReportDetails(){
			Hashtable<String, Object> data = new Hashtable<>();
			
			if (userController.getUser().getRole().toString().equals("AreaManager"))
				data.put("Area", userController.getUser().getArea().toString());
			else
				data.put("Area", area_Selected);
			data.put("Year", yearSelected);
			data.put("Month", monthSelected);
			data.put("Type", modifiedString);
			if (facilitySelected != null) {
				data.put("Facility", facilitySelected);
			}
			ServerMessage msg = new ServerMessage(Instruction.Get_Report_Area_Manager, data);
			EkrutClientUI.chat.accept(msg);
			while (!EkrutClientUI.chat.isServerMsgRecieved()) {}; // wait for server update

		}


		/**
		 *This method changes the current screen to the one specified by pathToScreen.
		 *@param event - The ActionEvent that triggers the screen change.
		 *@param pathToScreen - The path to the desired screen.
		 */
		public void changeScreen(ActionEvent event, String pathToScreen) {
			commonActionsController.backOrNextPressed(event, pathToScreen);
		}
		
		
		/**
		 * resets all labels
		 */
		public void makeLabelsUnVisible() {
			errorlabel.setVisible(false);
			errYear.setVisible(false);
			errMonth.setVisible(false);
			errType.setVisible(false);
			errFacility.setVisible(false);
			errArea.setVisible(false);
		}
		
		// for testing purpose - setting all chosen combo box values
		@Override
		public void getValues() {
			return;
		}



	
	}
	
	/**
	 *This method sets property for property injection, set the report manager to the given IReportManager object.
	 *It is used for property injection.
	 *@param reportManager - the IReportManager object to be set as the report manager.
	 */
	public void setReportManager(IReportManager reportManager) {
		this.reportManager = reportManager;
	}
	
	//  setters 
	public void setTypeReportSelected(String typeReportSelected) {
		this.typeReportSelected = typeReportSelected;
	}

	public void setYearSelected(String yearSelected) {
		this.yearSelected = yearSelected;
	}

	public void setMonthSelected(String monthSelected) {
		this.monthSelected = monthSelected;
	}

	public void setFacilitySelected(String facilitySelected) {
		this.facilitySelected = facilitySelected;
	}

	public void setArea_Selected(String area_Selected) {
		this.area_Selected = area_Selected;
	}
	
	public void setModifiedString(String modifiedString) {
		this.modifiedString = modifiedString;
	}



}