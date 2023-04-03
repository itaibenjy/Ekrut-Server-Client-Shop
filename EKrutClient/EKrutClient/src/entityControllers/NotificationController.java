package entityControllers;

/**
 * The NotificationController class is a controller class for displaying notifications 
 * in the system. It has a single static instance variable, context, which stores the text of the notification.
 *
 */
public class NotificationController {
	private static String context="";
	
	public NotificationController() {
		context="";
	}

	public static String getContext() {
		return context;
	}

	public static void setContext(String context) {
		NotificationController.context = context;
	}
	

}
