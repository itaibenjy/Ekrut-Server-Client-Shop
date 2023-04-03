package entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class is the OpreatingWorkerTableInAreaManger entity which hold all the information of the OpreatingWorkerTableInAreaManger   
 *  entity with getters and setters for each attribute, the purpose is to be able to display this 
 *  information in a table view.
 */
public class OpreatingWorkerTableInAreaManger implements Serializable {
	// Attributes
	private static final long serialVersionUID = 1L;
	private String faclitityName;
	private String productName;
	private String worker;
	private int oldInventory;
	private int newInventory;
	
	// Constructors
	public OpreatingWorkerTableInAreaManger(String faclitityName, String productName, String worker, int oldInventory, int newInventory) {
		this.faclitityName = faclitityName;
		this.productName = productName;
		this.worker = worker;
		this.oldInventory = oldInventory;
		this.newInventory = newInventory;
	}
	
	// getters and setters
	
	public String getFaclitityName() {
		return faclitityName;
	}
	public void setFaclitityName(String faclitityName) {
		this.faclitityName = faclitityName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getWorker() {
		return worker;
	}
	public void setWorker(String worker) {
		this.worker = worker;
	}
	public int getOldInventory() {
		return oldInventory;
	}
	public void setOldInventory(int oldInventory) {
		this.oldInventory = oldInventory;
	}
	public int getNewInventory() {
		return newInventory;
	}
	public void setNewInventory(int newInventory) {
		this.newInventory = newInventory;
	}
	
	// equals an hashcode
	@Override
	public int hashCode() {
		return Objects.hash(faclitityName, productName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OpreatingWorkerTableInAreaManger other = (OpreatingWorkerTableInAreaManger) obj;
		return Objects.equals(faclitityName, other.faclitityName) && Objects.equals(productName, other.productName);
	}
	
}
