package clientGUIControllers;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.AreaAveragingScaleFilter;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Enum.Area;
import Enum.ReportType;
import Enum.Role;
import Enum.UserStatus;
import entities.InventoryReport;
import entities.User;
import entityControllers.Inventory_ReportEntityController;
import entityControllers.UserController;
import javafx.event.ActionEvent;


class InventoryReportControllerTest {

	User ceo;
	User areaManager;
	String actualPathToScreen;
	String expectedScreen;
	UserController userController;
	IReportManager stubReportManager;
	InventoryReport report;
	InventoryReport inventoryReportTest;
	Inventory_ReportEntityController inventoryReportEntity;
	ViewReportController viewReportController;
	String area;
	String type;
	String month;
	String year;
	String facility;
	boolean isErrLabelShown;
	boolean isErrYearShown;
	boolean isErrMonthShown;
	boolean isErrTypeShown;
	boolean isErrAreaShown;
	boolean isErrFacilityShown;


	public class StubReportManager implements IReportManager{

		@Override
		public void showErrLabel() {
			isErrLabelShown = true;
		}

		@Override
		public void showErrYear() {
			isErrYearShown = true;
		}

		@Override
		public void showErrMonth() {
			isErrMonthShown = true;
		}

		@Override
		public void showErrType() {
			isErrTypeShown = true;
		}

		@Override
		public void showErrArea() {
			isErrAreaShown = true;
		}

		@Override
		public void showErrFacility() {
			isErrFacilityShown = true;
		}

		@Override
		public void ReportDetails() {
			inventoryReportEntity.setInventoryReport(inventoryReportTest);
		}

		@Override
		public void changeScreen(ActionEvent event, String pathToScreen) {
			actualPathToScreen = pathToScreen;
		}

		@Override
		public void makeLabelsUnVisible() {
		 return;	
		}

		@Override
		public void getValues() {
			viewReportController.setArea_Selected(area);
			viewReportController.setFacilitySelected(facility);
			viewReportController.setMonthSelected(month);
			viewReportController.setTypeReportSelected(type);
			viewReportController.setYearSelected(year);
			if(type != null) {
				viewReportController.setModifiedString(type.replace(" ", ""));
			}
		}

	}


		@BeforeEach
		void setUp(){
			ceo=new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.CEO, false, "CEO",
					UserStatus.Active, Area.North);
			areaManager=new User("1", 123456789, "Dvora", "Shabat", "Dvora@gmail.com", "0525381648", Role.AreaManager, false, "areaManager",
					UserStatus.Active, Area.North);;

			stubReportManager = new StubReportManager();
			viewReportController=new ViewReportController();
			viewReportController.setReportManager(stubReportManager);
			inventoryReportEntity = new Inventory_ReportEntityController();
			inventoryReportEntity.setInventoryReport(null);
			userController = new UserController();
			userController.setUser(null);
			actualPathToScreen = null;

			report=new InventoryReport(50, Area.North, ReportType.InventoryReport, 2022, 12, "Braude",
					"Cola,5,Fanta,8,Sprite,12,Monster,20", "Monster", 2,4,"Monster,10,Cola,2");

			isErrLabelShown = false;
			isErrYearShown = false;
			isErrMonthShown = false;
			isErrTypeShown = false;
			isErrAreaShown = false;
			isErrFacilityShown = false;
			
			month = null;
			year = null;
			facility = null;
			area = null;
			type = null;
		}
		

		/**
		 * checking : successfully change the screen to view report as Area Manager
		 * input : "12", "2022", "Braude", "North", "InventoryReport", (Simulate combo box selected), areaManager user.
		 * expected:  change screen to inventory report, with no errors shown.
		 */
		@Test
		void generateInventoryReportSuccessAreaManagerTest() {
			userController.setUser(areaManager);
			month = "12";
			year = "2022";
			facility = "Braude";
			area = "North";
			type = "InventoryReport";
			inventoryReportTest = report;
			viewReportController.generateReportButton_Pressed(new ActionEvent());
			expectedScreen = "/clientGUIScreens/InventoryReportScreen.fxml";
			Assert.assertFalse(isErrAreaShown);
			Assert.assertFalse(isErrMonthShown);
			Assert.assertFalse(isErrYearShown);
			Assert.assertFalse(isErrTypeShown);
			Assert.assertFalse(isErrFacilityShown);
			Assert.assertFalse(isErrLabelShown);
			Assert.assertEquals(expectedScreen, actualPathToScreen);

		}
		
		/**
		 * checking : successfully change the screen to view report as CEO
		 * input : "12", "2022", "Braude", "North", "InventoryReport", (Simulate combo box selected), ceo user.
		 * expected:  change screen to inventory report, with no errors shown.
		 */
		@Test
		void generateInventoryReportSuccessCEOTest() {
			userController.setUser(ceo);
			month = "12";
			year = "2022";
			facility = "Braude";
			area = "North";
			type = "InventoryReport";
			inventoryReportTest = report;
			viewReportController.generateReportButton_Pressed(new ActionEvent());
			expectedScreen = "/clientGUIScreens/InventoryReportScreen.fxml";
			Assert.assertFalse(isErrAreaShown);
			Assert.assertFalse(isErrMonthShown);
			Assert.assertFalse(isErrYearShown);
			Assert.assertFalse(isErrTypeShown);
			Assert.assertFalse(isErrFacilityShown);
			Assert.assertFalse(isErrLabelShown);
			Assert.assertEquals(expectedScreen, actualPathToScreen);

		}
		
		/**
		 * checking : no month selected try to generate report
		 * input : "2022", "Braude", "North", "InventoryReport", (Simulate combo box selected), areaManger user
		 * expected:  Month error shows, and screen don't change (null).
		 */
		@Test
		void generateInventoryReportNoMonthTest() {
			userController.setUser(areaManager);
			year = "2022";
			facility = "Braude";
			area = "North";
			type = "InventoryReport";
			inventoryReportTest = report;
			viewReportController.generateReportButton_Pressed(new ActionEvent());
			expectedScreen = null;
			Assert.assertFalse(isErrAreaShown);
			Assert.assertTrue(isErrMonthShown);
			Assert.assertFalse(isErrYearShown);
			Assert.assertFalse(isErrTypeShown);
			Assert.assertFalse(isErrFacilityShown);
			Assert.assertFalse(isErrLabelShown);
			Assert.assertEquals(expectedScreen, actualPathToScreen);

		}
		
		/**
		 * checking : no year selected try generate report
		 * input : "12", "Braude", "North", "InventoryReport", (Simulate combo box selected), areaManger user.
		 * expected:  Year error shows, and screen don't change (null).
		 */
		@Test
		void generateInventoryReportNoYearTest() {
			userController.setUser(areaManager);
			month = "12";
			facility = "Braude";
			area = "North";
			type = "InventoryReport";
			inventoryReportTest = report;
			viewReportController.generateReportButton_Pressed(new ActionEvent());
			expectedScreen = null;
			Assert.assertFalse(isErrAreaShown);
			Assert.assertFalse(isErrMonthShown);
			Assert.assertTrue(isErrYearShown);
			Assert.assertFalse(isErrTypeShown);
			Assert.assertFalse(isErrFacilityShown);
			Assert.assertFalse(isErrLabelShown);
			Assert.assertEquals(expectedScreen, actualPathToScreen);

		}
		
		/**
		 * checking : no facility selected try generate report
		 * input : "12", "2022", "North", "InventoryReport" (Simulate combo box selected), areaManger user
		 * expected:  Month error shows, and screen don't change (null).
		 */
		@Test
		void generateInventoryReportNoFacilityTest() {
			userController.setUser(areaManager);
			month = "12";
			year = "2022";
			area = "North";
			type = "InventoryReport";
			inventoryReportTest = report;
			viewReportController.generateReportButton_Pressed(new ActionEvent());
			expectedScreen = null;
			Assert.assertFalse(isErrAreaShown);
			Assert.assertFalse(isErrMonthShown);
			Assert.assertFalse(isErrYearShown);
			Assert.assertFalse(isErrTypeShown);
			Assert.assertTrue(isErrFacilityShown);
			Assert.assertFalse(isErrLabelShown);
			Assert.assertEquals(expectedScreen, actualPathToScreen);

		}
		
		/**
		 * checking : no area selected try generate report as ceo
		 * input : "12", "2022", "Braude", "InventoryReport" (Simulate combo box selected), ceo user.
		 * expected:  Month error shows, and screen don't change (null).
		 */
		@Test
		void generateInventoryReportNoAreaCEOTest() {
			userController.setUser(ceo);
			month = "12";
			year = "2022";
			facility = "Braude";
			type = "InventoryReport";
			inventoryReportTest = report;
			viewReportController.generateReportButton_Pressed(new ActionEvent());
			expectedScreen = null;
			Assert.assertTrue(isErrAreaShown);
			Assert.assertFalse(isErrMonthShown);
			Assert.assertFalse(isErrYearShown);
			Assert.assertFalse(isErrTypeShown);
			Assert.assertFalse(isErrFacilityShown);
			Assert.assertFalse(isErrLabelShown);
			Assert.assertEquals(expectedScreen, actualPathToScreen);

		}
		
		/**
		 * checking : successfully change the screen to view report
		 * input : "12", "2022", "Braude", "North", "InventoryReport". (Simulate combo box selected).
		 * expected:  Month error shows, and screen don't change (null).
		 */
		@Test
		void generateInventoryReportNoTypeTest(){
			userController.setUser(areaManager);
			month = "12";
			year = "2022";
			facility = "Braude";
			area = "North";
			inventoryReportTest = report;
			viewReportController.generateReportButton_Pressed(new ActionEvent());
			expectedScreen = null;
			Assert.assertFalse(isErrAreaShown);
			Assert.assertFalse(isErrMonthShown);
			Assert.assertFalse(isErrYearShown);
			Assert.assertTrue(isErrTypeShown);
			Assert.assertFalse(isErrFacilityShown);
			Assert.assertFalse(isErrLabelShown);
			Assert.assertEquals(expectedScreen, actualPathToScreen);

		}
		
		/**
		 * checking : no report try to generate report( simulate no report found in database)
		 * input : "12", "2022", "Braude", "North", "InventoryReport". (Simulate combo box selected).
		 * expected:  Month error shows, and screen don't change (null).
		 */
		@Test
		void generateInventoryReportNoReport(){
			userController.setUser(areaManager);
			month = "12";
			year = "2022";
			facility = "Braude";
			area = "North";
			type = "InventoryReport";
			viewReportController.generateReportButton_Pressed(new ActionEvent());
			expectedScreen = null;
			Assert.assertFalse(isErrAreaShown);
			Assert.assertFalse(isErrMonthShown);
			Assert.assertFalse(isErrYearShown);
			Assert.assertFalse(isErrTypeShown);
			Assert.assertFalse(isErrFacilityShown);
			Assert.assertTrue(isErrLabelShown);
			Assert.assertEquals(expectedScreen, actualPathToScreen);

		}

}