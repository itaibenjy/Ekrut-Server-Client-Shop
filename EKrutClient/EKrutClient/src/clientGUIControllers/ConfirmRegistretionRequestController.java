package clientGUIControllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import Client.EkrutClientUI;
import Enum.Instruction;
import common.CommonActionsController;
import entities.ServerMessage;
import entities.User;
import entityControllers.AreaManagerEntityController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class gives the option to Area Manager to see whose the users who
 * registered to his area and waiting to approve. He can choose who he want to
 * confirm. If he confirms any user, he must update in the system. If he will
 * confirm all of the users or he have no users to approve, its will show him
 * relevant message.
 * 
 * This department makes use of the tables: 'user', which contains all data of
 * ours users. We will search for the users that their `UserStatus` is
 * "Not_Approved"
 * 
 *  
 */

public class ConfirmRegistretionRequestController {

	@FXML
	private Button Back_Button;

	@FXML
	private Button LogOut;

	@FXML
	private Button Exit_Button;

	@FXML
	private Text description;

	@FXML
	private Button help;

	@FXML
	private TableView<UserNotApprovedTable> usersData;

	@FXML
	private TableColumn<UserNotApprovedTable, String> userName;

	@FXML
	private TableColumn<UserNotApprovedTable, String> firstName;

	@FXML
	private TableColumn<UserNotApprovedTable, String> lastName;

	@FXML
	private TableColumn<UserNotApprovedTable, ComboBox<String>> userStatusCheck;

	@FXML
	private Button Confirm;
	
	@FXML
	private Label noUsersLabel;

	@FXML
	private ImageView logoImage;

	@FXML
	private ImageView customerIm;

	// entities controllers
	private AreaManagerEntityController entity_areaManagerController = new AreaManagerEntityController();
	private static ArrayList<User> usersList = new ArrayList<User>();
	private CommonActionsController commonActionsController = new CommonActionsController();
	private ObservableList<String> checkingStatus;

	@FXML
	private void initialize() {
		setImages();
		createTableViewUsers();

	}

	/**
	 * Set images
	 */
	private void setImages() {
		logoImage.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
		customerIm.setImage(new Image("/areaManagerImages/customer.png"));
	}

	/**
	 * this function create the tableview that shows the users AreaManager need to
	 * approve
	 */
	private void createTableViewUsers() {
		usersData.getItems().clear(); // First clear the table
		checkingStatus = FXCollections.observableArrayList();
		checkingStatus.add("Active");
		checkingStatus.add("Not_Approved");
		// get area and return from the DB the users that "Not_Approved"
		Hashtable<String, Object> data = new Hashtable<>();
		String area_str = entity_areaManagerController.getAreaManager().getArea().toString();
		data.put("area", area_str);
		ServerMessage msg = new ServerMessage(Instruction.Get_all_Users_NotApproved_list, data);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		; // wait for server update
		if (usersList.isEmpty()) { // there is no users that need to approve
			usersData.setVisible(false);
			noUsersLabel.setVisible(true);
			Confirm.setVisible(false);

		} else {
			for (int i = 0; i < usersList.size(); i++) {
				usersData.getItems()
						.add(new UserNotApprovedTable(usersList.get(i).getUserName(), usersList.get(i).getFirstName(),
								usersList.get(i).getLastName(),
								FXCollections.observableArrayList("Active", "Not_Approved")));

			}

			initTableView(); // we got a choose of users and init the table
		}

	}

	/**
	 * init the table view to show the users Area Manager can approve
	 */
	private void initTableView() {
		userName.setCellValueFactory(new PropertyValueFactory<UserNotApprovedTable, String>("userName"));
		firstName.setCellValueFactory(new PropertyValueFactory<UserNotApprovedTable, String>("firstName"));
		lastName.setCellValueFactory(new PropertyValueFactory<UserNotApprovedTable, String>("lastName"));
		userStatusCheck.setCellValueFactory(
				new PropertyValueFactory<UserNotApprovedTable, ComboBox<String>>("userStatusCheck"));

	}

	/**
	 * @param event when confirm press we will goes over the tableview data and we
	 *              will check if we need to updata user`s UserStatus
	 */
	@FXML
	void Confirm_Pressed(ActionEvent event) {

		ArrayList<String> tempUserArray = new ArrayList<String>();
		for (UserNotApprovedTable val : usersData.getItems()) {
			// check if who is the users need to updata user`s UserStatus
			if (val.getUserStatusCheck().getValue().equals("Active")) {
				tempUserArray.add(val.getUserName()); // adding to array the users who needs updated

			}
		}
		// update te users the area manager confirmed
		Hashtable<String, Object> data = new Hashtable<>();
		data.put("users", tempUserArray);
		ServerMessage msg = new ServerMessage(Instruction.Update_User_userStatus, data);
		EkrutClientUI.chat.accept(msg);
		while (!EkrutClientUI.chat.isServerMsgRecieved()) {
		}
		; // wait for server update
		// update the TableView
		
		String popUpMsg="An SMS and an Email was sent to all users that were approved.";
		commonActionsController.popUp(new Stage(), "Registeration request",popUpMsg,"/serviceRepresentiveEmployeePics/user.gif",null,null);
		createTableViewUsers();
	}

	/**
	 * return ArrayList with User Objects
	 */
	public ArrayList<User> getUsersList() {
		return usersList;
	}

	/**
	 * @param UsersList
	 */
	public void setUsersList(ArrayList<User> UsersList) {
		usersList = UsersList;
	}

	/**
	 * @param
	 *  event this method will help the user to use this page
	 */
	@FXML
	void helpPress(ActionEvent event) {
		description.setVisible(true);
		help.setVisible(false);
	}

	/**
	 * @param event back to pervious page
	 */
	@FXML
	void BackButton_Pressed(ActionEvent event) {
		commonActionsController.backOrNextPressed(event, "/clientGUIScreens/AreaManagerScreen.fxml");
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
	 * This class will save data for the users we will represent in the TableView
	 *
	 */
	public class UserNotApprovedTable implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String userName;
		private String firstName;
		private String lastName;
		private ComboBox userStatusCheck;
		private String userStatusCheck_string;

		public UserNotApprovedTable(String userName, String firstName, String lastName, ObservableList data) {
			this.userName = userName;
			this.firstName = firstName;
			this.lastName = lastName;
			this.userStatusCheck = new ComboBox(data);
			userStatusCheck.setValue("Not_Approved");
		}

		public UserNotApprovedTable(String userName, String firstName, String lastName, String userStatusCheck_string) {
			this.userName = userName;
			this.firstName = firstName;
			this.lastName = lastName;
			this.userStatusCheck_string = userStatusCheck_string;
		}

		public String getUserStatusCheck_string() {
			return userStatusCheck_string;
		}

		public void setUserStatusCheck_string(String userStatusCheck_string) {
			this.userStatusCheck_string = userStatusCheck_string;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public ComboBox getUserStatusCheck() {
			return userStatusCheck;
		}

		public void setUserStatusCheck(ComboBox userStatusCheck) {
			this.userStatusCheck = userStatusCheck;
		}
	}
}
