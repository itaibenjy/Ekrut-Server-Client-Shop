package clientGUIControllers;

import entities.InventoryReport;
import entities.ServerMessage;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

/**
 * This Interface is to be able to use dependency injection in the testing for login
 * controller class (this interface remove the dependency of the ui elements and the server 
 * when use with stub class)
 */

public interface IReportManager {
	
	
	public void showErrLabel();
	public void showErrYear();
	public void showErrMonth();
	public void showErrType();
	public void showErrArea();
	public void showErrFacility();
	public void ReportDetails();
	public void changeScreen(ActionEvent event, String pathToScreen);
	public void makeLabelsUnVisible();
	public void getValues();

}