package Client;

import common.ChatIF;
import entities.ServerMessage;

/**
 * This class implements the chatIF interface, holds a EkrutClient instance 
 * and its purpose is to and communicate between the client UI and the EkrutClient, 
 * 
 */
public class ClientConsole implements ChatIF {
	
	private static String configuration ;  
	  
	//Getters and Setters for Configuration 
	  public static String getConfiguration() {
		return configuration;
	}

	public static void setConfiguration(String configuration) {
		ClientConsole.configuration = configuration;
	}

	/**
	   * The instance of the client that created this ConsoleChat.
	   */
	static EkrutClient ekrutClient;
	
	  //Constructors ****************************************************

	  /**
	   * Constructs an instance of the ClientConsole UI.
	   */
	public ClientConsole() {
	}
	
	  //Instance methods ************************************************
	  
	/**
	 * This method transfer a ServerMessage instance from the UI to the EKrutClient 
	 * to be able to send to server
	 * @param msg the ServerMessage we want to send
	 */
	public void accept(ServerMessage msg) {
		ekrutClient.handleMessageFromClientUI(msg);
	}
	
	  /**
	   * This method overrides the method in the ChatIF interface.  It
	   * displays a message onto the screen.
	   *
	   * @param message The string to be displayed.
	   * */
	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	public boolean isServerMsgRecieved() {
		return ekrutClient.msgReturn;
	}
	
}

