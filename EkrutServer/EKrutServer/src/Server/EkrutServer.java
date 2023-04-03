package Server;

import static org.junit.jupiter.api.Assumptions.abort;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import Enum.Instruction;
import jdbc.DataBaseController;
import entities.DataBaseConnectionDetails;
import entities.ServerMessage;
import ocsf.AbstractServer;
import ocsf.ConnectionToClient;

/**
 * This class extends the abstract server class from the OCSF framework, starts the 
 * server instance and implements function from the abstract server to run and 
 * maintain the server, deal with clients connections and requests. (receive and 
 * send messages)
 */
public class EkrutServer extends AbstractServer {
	
	// Attributes
	private static EkrutServer server = null;
	private ServerScreenController serverGui; 
	private static ClientConnectionController clientConnectionController;
	
		
	 //Constructors 
	/**
	   * Constructs an instance of the prototype server.
	   *
	   * @param port The port number to connect on.
	   * @param serverGui ServerScreenController to send information to the Gui
	   */
	public EkrutServer(int port, ServerScreenController serverGui) {
		super(port);
		this.serverGui = serverGui;
		clientConnectionController = new ClientConnectionController();
	}
	
	// Methods
	
	
	/**
	   * This method handle messages from client and sends them to the correct method (which handle a specific message)
	   * 
	   * @param msg object that the client sent
	   * @param client Connection to client with all details and ability to sent message back
	   */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		InetAddress clientDet = client.getInetAddress();
		if(!(msg instanceof ServerMessage)) {
			serverGui.printToConsole(String.format("Unknown message format was sent to server from ", clientDet.getHostName()));
			return;
		}
		ServerMessage sm = (ServerMessage)msg;
		MessageHandler.handleMessage(sm,client);
		
	}
	

	/**
	   *  Method called when the server starts listening for connections.
	   */
	@Override
	protected void serverStarted()
	{
			serverGui.printToConsole("Server listening for connections on port " + getPort() + " Server IP: " + getServerIpAddress());
	}
	
	/**
	 * The method enumerate through the network interface eliminate local host and virtual mechines to 
	 * return the right IP;
	 * @return a String containing the correct IP
	 */
	private String getServerIpAddress() {
		String ip;
		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements())
				{
					InetAddress i = (InetAddress) ee.nextElement();
					ip = i.getHostAddress();
					if(ip.contains(".") && !ip.equals("127.0.0.1") && !n.getDisplayName().toLowerCase().contains("virtual")) {
						return  i.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			serverGui.printToConsole("Error while getting the server ip.");
		}
		return "Not found use ipconfig in command line prompt";
	}
	
	/**
	   * Method called when the server stops accepting connections.
	   */
	@Override
	protected void serverStopped()
	{
		clientConnectionController.stop();
		serverGui.printToConsole("Server has stopped listening for connections.");
	}
	
	/**
	   * method called when the server is closed
	   */
	@Override
	protected void serverClosed() {
		serverGui.printToConsole("Server has been closed.");
	}
	
	/**
	   * Method called when a new client connect to the server
	   * 
	   * @param client the connection to the client hold details about client and use to send and receive messages
	   */
	@Override
	protected void clientConnected(ConnectionToClient client) {
		InetAddress details = client.getInetAddress();
		serverGui.printToConsole("Client " + details.getHostName() + " with IP: " + details.getHostAddress() + " Connected.");
		serverGui.addToConnected(client);
	}
	
	/**
	   * Method called when a client disconnect from the server
	   * 
	   * @param client the connection to the client hold details about client and use to send and receive messages
	   */
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		InetAddress details = client.getInetAddress();
		serverGui.printToConsole("Client " + details.getHostName() + " with IP: " + details.getHostAddress() + " Disonnected.");
		serverGui.removeFromConnected(client);
	}
	
	/**
	   * This method stop the server to listen on a specific port and close it
	   */
	public static void stopServer() {
		if(server == null) {
			return;
		}
		try {
			server.sendToAllClients(new ServerMessage(Instruction.Disconnect_From_Server));
			server.stopListening();
			server.close();
			server = null;
		} catch (IOException e) {
			server.serverGui.printToConsole("Error while disconnecting from all clients");
			e.printStackTrace();
		}
	}
	
	
	 /**
	   * This method start the server to listen on a specific port and connect to database via database controller
	   *
	   * @param database contain the database connection details
	   * @param serverGui ServerScreenController to send information to the Gui
	   */
	public static void startServer(DataBaseConnectionDetails database, Integer port, ServerScreenController serverGui) {
		
			DataBaseController.connectToDataBase(database,serverGui);
			if(DataBaseController.getConn() == null) {
				serverGui.printToConsole("Connection to database falied!");
				return;
			}
	
			
		serverGui.printToConsole("Connection to database Successfull.");
		
		// single server allowed to be open
		if(server != null) {
			serverGui.printToConsole("There is already a server connected");
			return;
		}
			
		server = new EkrutServer(port, serverGui);
		
	
		try
		{
			server.listen();
			serverGui.connectionSuccessfull();
		}
		catch (Exception ex)
		{
			serverGui.printToConsole("ERROR - could not listen for clients!");
			server = null;
		}
	}
	
	/**
	 * This function send to client an object of ServerMessage
	 * @param msg the ServerMessage to send
	 * @param client the client to send to
	 */
	public void sendToClient(ServerMessage msg, ConnectionToClient client) {
		try {
			
			client.sendToClient(msg);
		} catch (IOException e) {
			serverGui.printToConsole("Error, failed to send instruction: "+msg.getInstruction().name()+" to client.");;
		}
	}

	// getters and setters
	public static EkrutServer getServer() {
		return server;
	}
	
	  public ServerScreenController getServerGui() {
		return serverGui;
	}
	public static ClientConnectionController getClientConnectionController() {
		return clientConnectionController;
	}

	public static void setClientConnectionController(ClientConnectionController clientConnectionController) {
		EkrutServer.clientConnectionController = clientConnectionController;
	}
}