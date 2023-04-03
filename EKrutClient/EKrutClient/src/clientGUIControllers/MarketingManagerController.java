package clientGUIControllers;


import Enum.Role;
import Enum.UserStatus;
import common.CommonActionsController;
import entityControllers.UserController;
import entityControllers.WorkerEntityController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;



/**
 * Maketing Manager Controller
 * This class is used to handle the MaketingManager's GUI screen.
 * This controller inculde all the actions that marketing manager can do.
 * The class has several methods that handle the different button presses on the RegisteredCustomer screen, such make new sale pattern,
 * or ask for new sale in his area.
 * It is also show to Maketing Manager if he has another role, if yes he can also do the actions of the second role if he want to.
 * 	JavaFX controller
 */

public class MarketingManagerController {

    @FXML
    private Button LogOut;

    @FXML
    private Button Exit_Button;

    @FXML
    private Button salesPatten;

    @FXML
    private Button activeSales;
	// user details
    @FXML
	private AnchorPane info;
	@FXML
	private Button infoBtn;

	@FXML
	private Label Fname;
	@FXML
	private Label Lname;
	@FXML
	private Label Email;
	@FXML
	private Label phone;
	@FXML
	private Label userStatus;
    
    @FXML
    private Label MarketingMangerName;
    
	@FXML
	private Button additionalPermissionBtn;
	
    @FXML
    private ImageView logoImage;

    @FXML
    private ImageView salePat;

    @FXML
    private ImageView activeSale;
    
    @FXML
    private ImageView orderImage;
    
    // entities controllers
    private UserController userController = new UserController();
    private CommonActionsController commonActionsController = new CommonActionsController();
	private WorkerEntityController workerEntityController=new WorkerEntityController();
	private boolean isInfoBtnPressed = false;
	
	/**
	 * Intilize Screen of Marketing Manager
	 */
	@FXML
	public void initialize() {
		if (workerEntityController.getSecondRole()==null || userController.getUser().getUserStatus().equals(UserStatus.Not_Approved))
		{
			this.additionalPermissionBtn.setVisible(false);
			orderImage.setVisible(false);
		}
		setImages();
		setUserInformation();
		additionalPermissionBtn.setText(workerEntityController.getSecondRole()+ " View");
		MarketingMangerName.setText("Welcome back " + userController.getUser().getFirstName() + " " + userController.getUser().getLastName());

			}

	/**
	 * Set images
	 */
	private void setImages() {
		orderImage.setImage(new Image("/ReportPhotos/buy.png"));	
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		salePat.setImage(new Image("/marketingManagerImages/patterns.png"));
		activeSale.setImage(new Image("/marketingManagerImages/active.png"));
	
		
	}
	
	@FXML
	void additionalPermissionPressed(ActionEvent event) {
		if((workerEntityController.getSecondRole().equals(Role.Subscriber)))	
			commonActionsController.backOrNextPressed(event, "/clientGUIScreens/SubscriberScreen.fxml");
		else
			commonActionsController.backOrNextPressed(event,"/clientGUIScreens/RegisteredCustomerScreen.fxml");
	}
	
	/**
	 * @param event X Button Pressed method closes the window
	 */
	@FXML
	void Exit_Pressed(ActionEvent event) {
		commonActionsController.xPressed(event);
	}

	/**
	 * @param event log Out Button Pressed log out the user 
	 */
	@FXML
	void LogOut_Pressed(ActionEvent event) {
		commonActionsController.logOutPressed(event);
	}
	/**
	 * A button when clicked it displays information about the user.
	 * Another click will hide the information
	 * @param event
	 */
	@FXML
	void infoBtn_Pressed(ActionEvent event) {
		
		if (isInfoBtnPressed) {
			infoBtn.setText("Show user Deatils");
			info.setVisible(false);
			isInfoBtnPressed =false;
		}
		else {
			infoBtn.setText("Hide user Deatils  ");
			info.setVisible(true);
			isInfoBtnPressed =true;
		}
	}

    /**
     * @param event choose the "Sale Pattern" action
     */
    @FXML
    void salesPatten_pressed(ActionEvent event) {
    	commonActionsController.backOrNextPressed(event, "/clientGUIScreens/SalesPatternsScreen.fxml");
    }
   
    /**
     * @param event choose the "Active Sale" action
     */
    @FXML
    void activeSales_pressed(ActionEvent event) {
    	commonActionsController.backOrNextPressed(event, "/clientGUIScreens/ActivateSalesScreen.fxml");
    }
    
    private void setUserInformation() {
		Fname.setText(userController.getUser().getFirstName());
		Lname.setText(userController.getUser().getLastName());
		Email.setText(userController.getUser().getEmail());
		phone.setText(userController.getUser().getTelephone());
		userStatus.setText(userController.getUser().getUserStatus().toString());
		if (userController.getUser().getUserStatus().toString().equals("Active")) {
			userStatus.setId("LabelActive");
			
		} else {
			userStatus.setId("LabelNoActive");
		}

	}
}
