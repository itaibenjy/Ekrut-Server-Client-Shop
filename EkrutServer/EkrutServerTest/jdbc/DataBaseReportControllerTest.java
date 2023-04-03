package jdbc;

import java.sql.SQLException;
import java.util.Hashtable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Enum.Area;
import Enum.Instruction;
import Enum.ReportType;
import Server.ServerScreenController;
import entities.DataBaseConnectionDetails;
import entities.InventoryReport;
import entities.ServerMessage;
import junit.framework.Assert;


/**
 *Junit Test for inventory report
 */
class DataBaseReportControllerTest {
	/*---------------------------------------Inventory Report Test--------------------------------------*/
		//inventory
	   InventoryReport month_test;
		//Instruction
	   Instruction report_Not_Found;
	   
	   //General
	   	ServerMessage sm ; 
		Hashtable<String, Object> data;
	   
	   

	@BeforeEach
	void setUp() throws Exception {
		/*---------------------------------------Inventory Report Test--------------------------------------*/
		data = new Hashtable<>();

	     DataBaseController.connectToDataBase(new DataBaseConnectionDetails("jdbc:mysql://{IP}/ekrut?serverTimezone=IST","localhost","root","43941077Rom"),new ServerScreenController());
		report_Not_Found = Instruction.Report_Area_Manager_NotFound; 
		month_test=new InventoryReport(0, Area.South, ReportType.InventoryReport, java.time.LocalDate.now().getYear(),java.time.LocalDate.now().getMonthValue(),"Ashkelon","Cola,5,Fanta,8,Sprite,12,Monster,20", "Monster",2, 4,"Monster,10,Cola,2");

		

	}

	/*---------------------------------------Inventory Report Test--------------------------------------*/
	
	/**checking : insert new report and search for it
	 *  input : { InventoryReport , South , LocalDate.now().getYear()  , LocalDate.now().getMonthValue()  , South } 
	 *  expected : month_test
	 */
	@Test
	void generate_monthlyInventory_Report_Report_Exist() {
		try {
			DataBaseReportController.insertInventoryReport("Ashkelon","Cola,5,Fanta,8,Sprite,12,Monster,20",Area.South,"Monster",2,4,"Monster,10,Cola,2"); 
			data.put("Type", "InventoryReport");
			data.put("Area", "South");		
			data.put("Year", String.valueOf(java.time.LocalDate.now().getYear()));
			data.put("Month", String.valueOf(java.time.LocalDate.now().getMonthValue()));
			data.put("Facility", "Ashkelon");
			sm = DataBaseReportController.get_Report_Area_Manager(data);
			InventoryReport expected =month_test;
		    InventoryReport result = (InventoryReport) sm.getData().get("Report");
		    Assert.assertEquals(result, expected);	 
		} catch (SQLException e) {
			Assert.fail();
		}
	    
	}

	/**checking : search for not existing report
	 *	input : { InventoryReport , UAE , 2022 , 02 , Dubai } 
	 *	expected: report_Not_Found
	 */
	@Test
	void monthlyInventoryReport_Report_Not_Exist() {
		data.put("Type", "InventoryReport");
		data.put("Area", "UAE");
		data.put("Year", "2022");
		data.put("Month", "02");
		data.put("Facility", "Dubai");
		sm = DataBaseReportController.get_Report_Area_Manager(data);
		Instruction expected=report_Not_Found;
		Instruction result = (Instruction) sm.getInstruction();
		Assert.assertEquals(expected, result);	    
	}
	

	/**checking : insert new report wrong format check not exist ,  invalid facility id 
	 *  input : { InventoryReport , South , LocalDate.now().getYear()  , LocalDate.now().getMonthValue()  , South } 
	 *  expected : SQLException -> The function catchs sql exception , facillity id have to be  foriegn key 
	 */
	@Test
	void generate_monthlyInventory_Report_Report_Not_Exist() {
		
			try {
				DataBaseReportController.insertInventoryReport("#","Cola,5,Fanta,8,Sprite,12,Monster,20",Area.South,"Monster",2,4,"Monster,10,Cola,2"); 
				Assert.fail();

			} catch (SQLException e) {
				Assert.assertTrue(true);

			}	
	}
	
	/**checking : insert new report wrong format check not exist ,facility id == NULL 
	 *  input : { InventoryReport , South , LocalDate.now().getYear()  , LocalDate.now().getMonthValue()  , South } 
	 *  expected : NullPointerException ->  The function catchs sql exception , facillity id cant be null
	 */
	@Test
	void generate_monthlyInventory_Report_Report_Not_Exist_Null() {
		try {
			DataBaseReportController.insertInventoryReport(null,"Cola,5,Fanta,8,Sprite,12,Monster,20",Area.South,"Monster",2,4,"Monster,10,Cola,2"); 
			Assert.fail();
		} catch (SQLException e) {
			Assert.assertTrue(true);
		}
		
	}
	
	
	
	/**checking : insert null - > no  information to search for report
	 *  input : null
	 *  expected : NullPointerException
	 */
	@Test
	void generate_monthlyInventory_Report_Report_Null() {
		data=null;
		try {
			sm = DataBaseReportController.get_Report_Area_Manager(data);
			Assert.fail();
		} catch (NullPointerException nullPointerException) {
			Assert.assertTrue(true);
		}
	}
	

	
	

}
