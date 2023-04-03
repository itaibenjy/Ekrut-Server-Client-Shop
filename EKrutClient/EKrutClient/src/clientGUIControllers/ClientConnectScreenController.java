package clientGUIControllers;

import Client.EkrutClient;
import common.ChangeWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This class is the controller of the client connect screen allow to enter IP of the
 * server you want to connect and a port of the server you want to connect to, 
 * when connected transfer to the login screen.
 */
public class ClientConnectScreenController {
	
	// JavaFX Elements
    @FXML
    private Button connect;

    @FXML
    private TextField port;

    @FXML
    private TextField serverIp;

    // Methods
    
    /**
     *  This method gets called when the connect button pressed, call the connect method in 
     *  ekrut client to start the client and connect to the server
     * @param event the AcitonEvent press on the connect button 
     */
    @FXML
    void connectPressed(ActionEvent event) {
    	EkrutClient.connectToServer(serverIp.getText(), port.getText(), event);
    }

	/**
	 * This method is called from the client if the connect to the server was successful and 
	 * transfer to the login screen
     * @param event the AcitonEvent press on the connect button 
	 */
	public void connectionSuccessfull(ActionEvent event) {
		((Node)event.getSource()).getScene().getWindow().hide();
		ChangeWindow changeWindow = new ChangeWindow();
		changeWindow.changeScreen(new Stage(),"/clientGUIScreens/LoginScreen.fxml");
	}
}
