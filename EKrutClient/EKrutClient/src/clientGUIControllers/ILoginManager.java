package clientGUIControllers;

import entities.ServerMessage;
import javafx.event.ActionEvent;

/**
 * This Interface is to be able to use dependency injection in the testing for login
 * controller class (this interface remove the dependency of the ui elements and the server 
 * when use with stub class)
 */
public interface ILoginManager {
	public void setUserLoggedIn(ServerMessage msg);
	public void userLoginDetails();
	public void changeScreen(ActionEvent event, String pathToScreen);
	public String getUsername();	
	public String getPassword();
	public void showError(String error);
	public void userLoginDetailsEKT(ServerMessage msg);
	public void setSubscriberSelected(String value);
	public String getSubscriberSelected();
}
