package entities;

import Enum.Area;
import Enum.Role;
import Enum.UserStatus;

	/**
	 * This class is the Registered entity which hold all the information of the Registered entity with 
	 * getters and setters for each attribute, correspond to the 'RegisteredCusotmer' table in database, 
	 * extends the User entity (hold all the information of the RegisteredCustomer)
	 */
public class RegisteredCustomer extends User {

	private static final long serialVersionUID = 1L;
	// Attributes
	private String creditCardNumber;
	private String expireMonth;
	private String expireYear;
	private String cvv;

	// Constructors
	
	
	public RegisteredCustomer() {
		super();
	}
	
	public RegisteredCustomer(String username, String password, int id) {
		super(username, password, id);		
	}
	
	public RegisteredCustomer(String creditCardNumber, String expireMonth, String expireYear, String cvv) {
		super();
		this.creditCardNumber = creditCardNumber;
		this.expireMonth = expireMonth;
		this.expireYear = expireYear;
		this.cvv = cvv;
	}

	
	/**
	 *Constructor Using super(password, id, firstName, lastName, email, telephone, role, loggedIn, userName, userStatus, area);
	 */
	public RegisteredCustomer(String password, int id, String firstName, String lastName, String email,
			String telephone, Role role, Boolean loggedIn, String userName, UserStatus userStatus, Area area , String creditCardNumber, String expireMonth, String expireYear, String cvv) {
		super(password, id, firstName, lastName, email, telephone, role, loggedIn, userName, userStatus, area);
		this.creditCardNumber = creditCardNumber;
		this.expireMonth = expireMonth;
		this.expireYear = expireYear;
		this.cvv = cvv;
	}
	
	


	// Methods

	public String getCreditCardNumber() {
		return creditCardNumber;
	}
 
	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}
	

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}
	
	public String getExpireMonth() {
		return expireMonth;
	}

	public void setExpireMonth(String expireMonth) {
		this.expireMonth = expireMonth;
	}

	public String getExpireYear() {
		return expireYear;
	}

	public void setExpireYear(String expireYear) {
		this.expireYear = expireYear;
	}


}
