package entities;

import java.io.Serializable;

import Enum.Area;
import Enum.Role;
import Enum.UserStatus;

/**
 * This class is the Subscriber entity which hold all the information of the Subscrigber entity with 
 * getters and setters for each attribute, correspond to the 'Subscriber' table in database, 
 * extends the Registered entity which extends the User entity (hold all the information of the 
 * Subscriber User)
 */
public class Subscriber extends RegisteredCustomer implements Serializable {

	private static final long serialVersionUID = 1L;
	// Attributes
	private int subscriberNumber;
	private float debtAmount;
	private Boolean firstOrder;
	

	// Constructors
	public Subscriber() {
		super();
	}

	public Subscriber(String username, String password, int id, int subscriberNumber,
			float debtAmount) {
		super(username, password, id);
		this.subscriberNumber = subscriberNumber;
		this.debtAmount = debtAmount;
	}
	
	
	public Subscriber(String password, int id, String firstName, String lastName, String email, String telephone,
			Role role, Boolean loggedIn, String userName, UserStatus userStatus, Area area, String creditCardNumber,
			String expireMonth, String expireYear, String cvv , int subscriberNumber,
			float debtAmount,boolean firstOrder) {
		super(password, id, firstName, lastName, email, telephone, role, loggedIn, userName, userStatus, area, creditCardNumber,
				expireMonth, expireYear, cvv);
		this.subscriberNumber = subscriberNumber;
		this.debtAmount = debtAmount;
		this.firstOrder=firstOrder;
	}
	
	
	
	public Subscriber( String creditCardNumber,String expireMonth, String expireYear, String cvv , int subscriberNumber,float debtAmount,boolean firstOrder) {
		super(creditCardNumber,expireMonth, expireYear, cvv);
		this.subscriberNumber = subscriberNumber;
		this.debtAmount = debtAmount;
		this.firstOrder=firstOrder;
	}
	
	// getters and seetters
	public int getSubscriberNumber() {
		return subscriberNumber;  
	}

	public void setSubscriberNumber(int subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
	}

	public float getDebtAmount() {
		return debtAmount;
	}

	public void setDebtAmount(float debtAmount) {
		this.debtAmount = debtAmount;
	}
	public boolean isFirstOrder() {
		return firstOrder;
	}

	
	public Boolean getFirstOrder() {
		return firstOrder;
	}

	public void setFirstOrder(Boolean firstOrder) {
		this.firstOrder = firstOrder;
	}
	
	
	
	
}
