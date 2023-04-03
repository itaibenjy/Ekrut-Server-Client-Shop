package Server;

import java.util.Calendar;
import entities.ClientConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jdbc.DataBaseReportController;
import jdbc.DataBaseUserController;
import entities.DataBaseConnectionDetails;
import ocsf.ConnectionToClient;

public class ServerScreenController {

	/**
	 *  This class is the controller for the server screen, shows the connected clients in a table view
	 *  and shows important information happening in the server through a text area ( all the informaiton 
	 *  is being printed to the text area).
	 */
	public ServerScreenController() {
		super();

	}

	// javaFx elements
	@FXML
	private Button connect;
	@FXML
	private Button disconnect;
	@FXML
	private TextField ip;
	@FXML
	private TextField port;
	@FXML
	private TextField dbname;
	@FXML
	private TextField dbusername;
	@FXML
	private TextField dbpass;
	@FXML
	private TextArea console;
	@FXML
	private TableView<ClientConnection> clientstable;
	@FXML
	private TableColumn<ClientConnection, String> ipcolumn;
	@FXML
	private TableColumn<ClientConnection, String> hostcolumn;
	@FXML
	private TableColumn<ClientConnection, String> statuscolumn;
    @FXML
    private ImageView logo;
	@FXML
	private Button importData;

	// Regular attributes
	private static ObservableList<ClientConnection> data =  FXCollections.observableArrayList();
    
	
 
	
	// Simple setters and getters
	public static ObservableList<ClientConnection> getData() {
		return data;
	}
	public static void setData(ObservableList<ClientConnection> data) {
		ServerScreenController.data = data;
	}
	
	// Methods
	
	/**
	 *  This method initialize the screen
	 */
	@FXML
	public void initialize() {
		logo.setImage(new Image("/EKRUTLOGONOCIRCLE.png"));
	}

	
	/**
	 * method called when the connect button was pressed in the GUI save the data
	 * from the GUI in a DataBaseConnectionDetails instance and initiate the
	 * connected clients table
	 */
	public void connectPressed() {
		DataBaseConnectionDetails database = new DataBaseConnectionDetails();

		Integer portNum;
		try {
			portNum = Integer.parseInt(port.getText());

		} catch (Exception e) {
			printToConsole("Port must be a number");
			return;
		}

		database.setIp(ip.getText());
		database.setName(dbname.getText());
		database.setPassword(dbpass.getText());
		database.setUsername(dbusername.getText());

		EkrutServer.startServer(database, portNum, this);
		initTableView();
		try {
	// checking if we in the end of the month
			Calendar calendar = Calendar.getInstance();
			if (java.time.LocalDate.now().getDayOfYear() == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				DataBaseReportController.generate_Report(); // generate report and resets the subscriber debt amount
			}
		} catch (NullPointerException e) {
		printToConsole("Cant generate report");
		}
			
	}

	/**
	 * method called when the disconnect button was pressed in the GUI call the stop
	 * server function in the server and enable the connect form for reconnect
	 */
	public void disconnectPressed() {
		EkrutServer.stopServer();
		disconnect.setDisable(true);
		disableFields(false);
		data.clear();
	}

	/**
	 * This method prints to the console on the GUI and the regular console
	 * 
	 * @param msg the message that will get printed
	 */
	public void printToConsole(String msg) {
		console.appendText(msg + "\n");
	}

	/**
	 * This method add client to the connected client table in the GUI
	 * 
	 * @param client ConnectionToClient we want to add
	 */
	public void addToConnected(ConnectionToClient client) {
		data.add(new ClientConnection(client.getInetAddress().getHostAddress(), client.getInetAddress().getHostName(),
				"Connected"));
	}

	/**
	 * This method remove client from the connected clients list
	 * 
	 * @param client ConnectionToClient we want to remove
	 */
	public void removeFromConnected(ConnectionToClient client) {
		for (ClientConnection cc : data) {
			if (cc.getHostIp().equals(client.getInetAddress().getHostAddress())
					&& cc.getHostName().equals(client.getInetAddress().getHostName())) {
				data.remove(cc);
				return;
			}
		}
	}

	/**
	 * This method enable the disconnect button and disable the connection form (get
	 * called after successful connection)
	 */
	public void connectionSuccessfull() {
		disconnect.setDisable(false);
		disableFields(true);
	}

	// Private helping methods

	/**
	 * initiate the table view to look at the data in data
	 */
	private void initTableView() {
		ipcolumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("hostIp"));
		hostcolumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("hostName"));
		statuscolumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("status"));
		clientstable.setItems(data);
	}

	/**
	 * disable or enable all fields except disconnect
	 * 
	 * @param bool true to disable false to enable
	 */
	private void disableFields(boolean bool) {
		connect.setDisable(bool);
		ip.setDisable(bool);
		port.setDisable(bool);
		dbname.setDisable(bool);
		dbusername.setDisable(bool);
		dbpass.setDisable(bool);

	}

	/**
	 * import data from csv files
	 * @param event clicking on button import data
	 */
	@FXML
	void importDataPress(ActionEvent event) {
		if (DataBaseUserController.importData()) {
			printToConsole("Import Data Successfully");
			return;
		}
		printToConsole("Cant Import Data");
	}

}
