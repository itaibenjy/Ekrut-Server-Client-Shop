package Server;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import entities.ClientConnection;
import entities.User;
import javafx.collections.ObservableList;
import jdbc.DataBaseUserController;
import ocsf.ConnectionToClient;

/**
 *  This class manages the connection to the clients, disconnect clients that unexpectedly disconnected
 *  from the server and if they where logged in to disconnect them from the database so they can login again.
 *  It manages to this by using a Timer and a TimerTask objects to run the code that check whether a client
 *  disconnected every 5 seconds in new Thread, and every time a client connect the method save to connection
 *  is called to save in hash table which client connected of disconnected to which user.
 *  
 *  This class uses tables:
 * 	'user' - for update the user login status 
 */
public class ClientConnectionController {
	// Attributes
	private Timer timer;
	private CheckConnection checkConnection;
	public static Hashtable<String, User > usersConnected = new Hashtable<>();
	
	/**
	 * This class extends TimerTask to be able to be scheduled to run from timer object
	 * uses the run method which search for disconnected clients and remove them from the UI 
	 */
	public class CheckConnection extends TimerTask{
		
		/**
		 * This method run through every client in the connected table of the server and search 
		 * the to see if there is an open connection to this client if not it will remove it 
		 * from the client connection and update the user is was connected to to be disconnected
		 */
		public void run() {
			Thread[] clientThreadList = EkrutServer.getServer().getClientConnections();
			ObservableList<ClientConnection> data = ServerScreenController.getData();
			boolean found = false;
			
			Iterator<ClientConnection> iterator = data.iterator();
			while (iterator.hasNext()) {
				ClientConnection cc = iterator.next();
				for (int i=0; i<clientThreadList.length; i++) {
					ConnectionToClient client = ((ConnectionToClient)clientThreadList[i]);
					if (cc.getHostIp().equals(client.getInetAddress().getHostAddress())
							&& cc.getHostName().equals(client.getInetAddress().getHostName())) {
						found = true;
						break;
					}
				}
				// not found in open connection so disconnect if necessary
				if(!found) {
					EkrutServer.getServer().getServerGui().printToConsole("Found that " + cc.getHostName() + " disconnected unexpectedly!");
					if(usersConnected.get(cc.getHostIp()+cc.getHostName()) != null) {
						Hashtable<String, Object> table = new Hashtable<>();
						table.put("user",usersConnected.get(cc.getHostIp()+cc.getHostName()));
						table.put("loggedIn",false);
						DataBaseUserController.setUserLoggedIn(table);
					}
					iterator.remove();
				}
				found = false;
			}	
		}
		
	}
	
	
	/**
	 *  A constructor to initiate the timer and task and schedule the task CheckConnnection  to run every 5 seconds
	 */
	public ClientConnectionController() {
		timer = new Timer();
		checkConnection = new CheckConnection();
		timer.schedule(checkConnection, 1000, 5000);
	}
	
	/**
	 * This method stop the timer object and by that stop the schedule task from running
	 */
	public void stop() {
		timer.cancel();
		timer.purge();
	}
	
	/**
	 *  This method start the timer to run again with the task CheckConnnection every 5 seconds
	 */
	public void start() {
		checkConnection = new CheckConnection();
		timer.schedule(checkConnection, 1000, 5000);
	}

	/**
	 * This method saves or remove user connected to client from the hash table usersConnected
	 * 
	 * @param data The hash table that holds the login or logout data in "loggedIn" key
	 * @param client the client data in ConnectionToClient instance
	 */
	public static void saveToConnection(Hashtable<String, Object> data, ConnectionToClient client) {
		Boolean isLogged = (Boolean) data.get("loggedIn");
		User user = (User)data.get("user");
		String key = client.getInetAddress().getHostAddress() + client.getInetAddress().getHostName();
		if(isLogged) {
			usersConnected.put(key, user);
		}
		else {
			usersConnected.remove(key, user);
		}
	}
}
