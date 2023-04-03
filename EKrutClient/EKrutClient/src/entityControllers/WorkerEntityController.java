package entityControllers;


import Enum.Role;
import entities.User;

/**
 *This class is responsible for storing and managing a User object for worker and the worker second role.
 *It also provides various methods to check the user's existence, and worker second role.
 */
public class WorkerEntityController {


	/**
	 *  Entities And Controller
	 */
	private static User user = null;
	private static Role secondRole=null;


	/**
	 * Constructor for creating a new WorkerEntityController object.
	 */
	public WorkerEntityController() {
	}

	/**
	 * Constructor for creating a new WorkerEntityController object with a User object.
	 *@param user - The User object that will be stored in the WorkerEntityController's user.
	 */
	public WorkerEntityController(User user) {
		WorkerEntityController.user = user;
	}

	/**
	 *Sets the User object to the given user.
	 *@param user - The User object that will be stored in the WorkerEntityController's user.
	 */
	public void setUser(User user) {
		WorkerEntityController.user = user;
	}

	/**
	 * Returns the User object stored in the WorkerEntityController.
	 *@return - The User object stored in the WorkerEntityController.
	 */
	public User getUser() {
		return WorkerEntityController.user;
	}

	/**
	 *Returns true if a User object is stored in the WorkerEntityController.
	 *@return - true if a User object is stored in the WorkerEntityController, false otherwise.
	 */
	public boolean userIsExist() {
		return WorkerEntityController.user != null;
	}

	/**
	 *Returns the additional role of the user.
	 *@return - The second role of the user.
	 */
	public Role getSecondRole() {
		return WorkerEntityController.secondRole;
	}

	/**
	 *Sets the additional role of the user to the given role.
	 *@param role - The role to be set as the second role for the user.
	 */
	public void setSecondRole(Role role) {
		WorkerEntityController.secondRole = role;
	}

	/**
	*Returns true if the worker(user) has an second role.
	*@return - true if the worker(user) has an second role, false otherwise.
	 */
	public boolean isExtraPermission() {
		if(secondRole==null)
			return false;
		return true;
	}
}


