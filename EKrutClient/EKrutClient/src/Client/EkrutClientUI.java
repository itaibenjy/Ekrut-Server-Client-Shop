package Client;

import javafx.application.Application;
import javafx.stage.Stage;
import common.ChangeWindow;


/**
 * This class extend the javaFx application class to start our javaFX application
 */
public class EkrutClientUI extends Application {
	public static ClientConsole chat;
	/**
	 * This method starts the application and call the change screen method to display the 
	 * client connect screen
	 */
	@Override
	public void start(Stage primaryStage) {
		ChangeWindow changeWindow = new ChangeWindow();
		changeWindow.changeScreen(primaryStage, "/clientGUIScreens/clientConnectScreen.fxml");
	}

	 
	/**
	 * This main method start our program 
	 * @param args the variables we send as we start our program
	 */
	public static void main(String[] args) {
		chat=new ClientConsole();

		if ( args.length>0)
			ClientConsole.setConfiguration(args[0]);		
		else 
			ClientConsole.setConfiguration("OL");
		launch(args);
	}
	
}