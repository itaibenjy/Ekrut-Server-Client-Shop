package entities;
import java.io.Serializable;
import Enum.ProccessStatus;
/**
 * This class is the ExecutionInstructoin entity which hold all the information of the ExcectionInstruction
 *  entity with getters and setters for each attribute, 
 *  correspond to the 'exectioninstruction' table in database
 */
public class ExecutionInstructions implements Serializable {
	
	// Attributes
	private static final long serialVersionUID = 1L;
	private int idExecutionInstruction;
	private String userName;
	private String facilityId;
	private String products;
	private ProccessStatus status;
	
	// Constructors
	public ExecutionInstructions() {
		super();
	}

	public ExecutionInstructions(int idExecutionInstruction, String userName, String facilityId, String products,
			ProccessStatus status) {
		super();
		this.idExecutionInstruction = idExecutionInstruction;
		this.userName = userName;
		this.facilityId = facilityId;
		this.products = products;
		this.status = status;
	}
	public ExecutionInstructions(int idExecutionInstruction,String facilityId, String products) {
		super();
		this.idExecutionInstruction = idExecutionInstruction;
		this.facilityId = facilityId;
		this.products = products;
	}
	
	// getters and setters
	public int getIdExecutionInstruction() {
		return idExecutionInstruction;
	}

	public void setIdExecutionInstruction(int idExecutionInstruction) {
		this.idExecutionInstruction = idExecutionInstruction;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public ProccessStatus getStatus() {
		return status;
	}

	public void setStatus(ProccessStatus status) {
		this.status = status;
	}
	
}
