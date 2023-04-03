package entityControllers;

import entities.AreaManager;

/**
 *The AreaManagerEntityController class is a controller class for an AreaManager entity. 
 *An AreaManager is a type of user that is responsible for managing a specific area in the system. The class has a single instance variable,
 * areaManager, which stores a reference to an AreaManager object
 */
public class AreaManagerEntityController {
	private static AreaManager areaManager = null;

	public AreaManagerEntityController() {
	}

	public AreaManagerEntityController(AreaManager AreaManager) {
		AreaManagerEntityController.areaManager = AreaManager;
	}

	public void setAreaManager(AreaManager AreaManager) {
		AreaManagerEntityController.areaManager = AreaManager;
	}
	
	public AreaManager getAreaManager() {
		return AreaManagerEntityController.areaManager;
	}

	public boolean AreaManagerIsExist() {
		return AreaManagerEntityController.areaManager != null;
	}


}
