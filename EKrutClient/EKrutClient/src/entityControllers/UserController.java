package entityControllers;

import entities.User;


/**
 *This class is responsible for storing and managing a User object.
 *It also provides various methods to check the user's existence.
 */
public class UserController {

	
	/**
	 *  Entity
	 */
	private static User user = null;


	/**
	 * Constructor for creating a new UserController object.
	 */
	public UserController() {
	}

	
	/**
	 * Constructor for creating a new UserController object with a User object.
	 *@param user - The User object that will be stored in the UserController's user.
	 */
	public UserController(User user) {
		UserController.user = user;
	}

	
	/**
	 *Sets the User object to the given user.
	 *@param user - The User object that will be stored in the UserController's user.
	 */
	public void setUser(User user) {
		UserController.user = user;
	}
	
	
	/**
	 * Returns the User object stored in the UserController.
	 *@return - The User object stored in the UserControllerr.
	 */
	public User getUser() {
		return UserController.user;
	}

	/**
	 *Returns true if a User object is stored in the UserController.
	 *@return - true if a User object is stored in the UserController, false otherwise.
	 */
	public boolean userIsExist() {
		return UserController.user != null;
	}
	
}

