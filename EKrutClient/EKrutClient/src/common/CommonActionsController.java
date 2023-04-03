package common;

import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Instruction;
import entities.ServerMessage;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;


/**
 * This class implements all the methods that every or a lot of different screens uses,
 * like logout, pressing the X , Back, or Next button in the screen and use of a PopUp
 * window. (The goal is to reduce the duplicate code in individuals screens).
 * 
 * This class uses the tables:
 * 'user' - for logout users when logout or X pressed
 */
public class CommonActionsController {
	private UserController userController = new UserController();

	/**
	 * This method is called when logout is pressed in one of the screens controllers
	 * change the window to the login window and call the changeUserLoggedIn method to 
	 * disconnect the logged in user from the database
	 * @param event the ActionEvent click on the logout button
	 */
	public void logOutPressed(ActionEvent event) {
		changeUserLoggedIn();
		((Node)event.getSource()).getScene().getWindow().hide();
		ChangeWindow changeWindow = new ChangeWindow();
		changeWindow.changeScreen(new Stage(),"/clientGUIScreens/LoginScreen.fxml");
	}

	/** 
	 * This method is called when the X button is pressed in one of the screens controllers
	 * it uses the changeUserLogedIn method disconnect the logged in user from the database
	 * and notify the server that this client is disconnecting.
	 * @param event the ActionEvent click on the X button 
	 */
	public void xPressed(ActionEvent event) {
		changeUserLoggedIn();
		ServerMessage msg = new ServerMessage(Instruction.Disconnect_From_Server, null);
		EkrutClientUI.chat.accept(msg);
		System.exit(0);
	}

	/**
	 * This method is called when the back button or a button that is used to transfer the 
	 * user to another screen is pressed from on of the screens controllers and uses the changeScreen
	 * class to transfer to the wanted screen
	 * @param event the ActionEvent click on the back or other button used to transfer to another screen
	 * @param newPage the path to the FXML file (Screen) we want to transfer to
	 */
	public void backOrNextPressed(ActionEvent event, String newPage) {
		((Node)event.getSource()).getScene().getWindow().hide();
		ChangeWindow changeWindow = new ChangeWindow();
		changeWindow.changeScreen(new Stage(),newPage);
	}


	/**
	 * This method is used to disconnect the current user from the database, reset all
	 * the needed controllers and their fields , and send request to the server to
	 * disconnect from the data base
	 */
	private void changeUserLoggedIn() {
		WorkerEntityController workerEntityController = new WorkerEntityController();
		workerEntityController.setSecondRole(null);
		userController.getUser().setLoggedIn(false);
		Hashtable<String,Object> data = new Hashtable<>();
		data.put("user",userController.getUser());
		data.put("loggedIn",false);
		ServerMessage msg = new ServerMessage(Instruction.Set_User_loggedIn,data);
		EkrutClientUI.chat.accept(msg);
	}

	/**
	 * This method hide the current screen (closes the screen before opening the new one)
	 * @param event the ActionEvent click on the screen to be able to hide the screen
	 */
	public void hideCurrent(ActionEvent event) {
		((Node)event.getSource()).getScene().getWindow().hide();
	}
	
	/**
	 * This method uses the showPopUp method of class ChangeWindow to display a pop up window on screen,
	 * it also can transfer user to a new screen when pressing OK or X on the pop screen.
	 * @param primaryStage the stage for the new pop up screen
	 * @param popUpTitle the title of the pop up screen
	 * @param popUpMessage the message shown in the pop up screen
	 * @param imagePath the image to show in the pop up screen (null if don't want to show image)
	 * @param linkToString the FXML path to a new screen if the wanted action when pressed OK or X on pop up is to transfer user to a new screen (null if want to stay on same screen)
	 * @param eventToClose the ActionEvent from the current screen to close it before transferring to a new screen (null if want to stay on same screen) 
	 */
	public void popUp(Stage primaryStage, String popUpTitle,String popUpMessage, String imagePath,String linkToString,ActionEvent eventToClose) {
		ChangeWindow changeWindow = new ChangeWindow();
		changeWindow.showPopUp(primaryStage, popUpTitle, popUpMessage, imagePath,linkToString,eventToClose);
	}


}