package clientGUIControllers;
import common.CommonActionsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;


/**
 * This class is used to control the behavior of a pop-up window.
 * It includes methods to set the title and message of the pop-up, 
 * as well as an ImageView and a link to the next screen.
 * It also includes methods to handle the 'X' and 'OK' buttons being pressed,
 *  which will close the pop-up window and navigate to the specified screen.
 *  
 */
public class PopUpController {


	/**
	 *FXML Attributes
	 */
	@FXML
	private Text messageLabel;

	@FXML
	private Button okBtn;

	@FXML
	private Label titleLabel;

	@FXML
	private Button xBtn;

	@FXML
	private FlowPane flowPane;

	@FXML
	private ImageView imageView;

	/**
	 * Entities,variables And Controllers 
	 */
	private  String linkToScreen="";
	private ActionEvent eventToClose; 
	private CommonActionsController commonActionsController= new CommonActionsController();


	/**
	 *  This method is called when the 'X' button is pressed.
	 *  It closes the current window and navigates to the screen specified in the linkToScreen field.
	 *  @param event The ActionEvent that triggers the method.
	 */
	@FXML
	void Xpressed(ActionEvent event) {
		commonActionsController.hideCurrent(event);
		if (!linkToScreen.equals("")) {
			commonActionsController.backOrNextPressed(event, linkToScreen);
			((Node)eventToClose.getSource()).getScene().getWindow().hide();
			linkToScreen = "";
		}
	}

	/**
	 *  This method is called when the 'OK' button is pressed.
	 *  It closes the current window and navigates to the screen specified in the linkToScreen field.
	 *  @param event The ActionEvent that triggers the method.
	 */
	@FXML
	void okPressed(ActionEvent event) {
		commonActionsController.hideCurrent(event);
		if (!linkToScreen.equals("")) {
			commonActionsController.backOrNextPressed(event, linkToScreen);
			((Node)eventToClose.getSource()).getScene().getWindow().hide();
			linkToScreen = "";
		}

	}


	/**
	 *  This method sets the title and message of the pop-up window.
	 *@param title The title of the pop-up window.
	 *@param message The message of the pop-up window.
	 */
	public void setTitleAndMessage(String title,String message) {
		titleLabel.setText(title);
		messageLabel.setText(message);
	}


	/**
	 *  This method sets the image in the imageView of the pop-up window.
	 *@param image The image that should be displayed in the pop-up window.
	 */
	public void seImageView(Image image) {
		imageView.setImage(image);
		imageView.setFitHeight(100.0D);
		imageView.setFitWidth(100.0D);
	}

	
	/**
	*The method setLinkToScreen sets the screen that the user should be directed to
	*after closing the pop-up window or pressing OK.
	*@param linkToScreen The FXML file path of the screen that the user should be directed to after closing the pop-up window or pressing OK.
	*/
	public  void setLinkToScreen(String linkToScreen) {
		this.linkToScreen = linkToScreen;
	}
	
	
	/**
	*The method setEventToClose sets the event that should be used to close the current pop-up window.
	*@param eventToClose The event that should be used to close the current pop-up window.
	*/
	public void setEventToClose(ActionEvent eventToClose) {
		this.eventToClose = eventToClose;
	}

}