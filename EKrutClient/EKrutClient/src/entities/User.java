package entities;

import java.io.Serializable;
import java.util.Objects;

import Enum.Area;
import Enum.Role;
import Enum.UserStatus;

/**
 * This class is the User entity which hold all the information of the User entity with 
 * getters and setters for each attribute, correspond to the 'user' table in database
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	// Attributes
	private String password;
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String telephone;
	private Role role;
	private Boolean loggedIn;
	private String userName;
	private UserStatus userStatus;
	private Area area;//Belonging to the area.
	

	// Constructors
	public User() {
	}
	public User(String password, int id, String firstName, String lastName, String email, String telephone, Role role,
			Boolean loggedIn, String userName, UserStatus userStatus, Area area) {
		super();
		this.password = password;
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.telephone = telephone;
		this.role = role;
		this.loggedIn = loggedIn;
		this.userName = userName;
		this.userStatus = userStatus;
		this.area = area;
	}

	public User(String username, String password, int id) {
		super();
		this.userName = username;
		this.password = password;
		this.id = id;
	}
	
	// hashCode , equals and toString for testing 
	
	@Override
	public int hashCode() {
		return Objects.hash(area, email, firstName, id, lastName, loggedIn, password, role, telephone, userName,
				userStatus);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return area == other.area && Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
				&& id == other.id && Objects.equals(lastName, other.lastName)
				&& Objects.equals(loggedIn, other.loggedIn) && Objects.equals(password, other.password)
				&& role == other.role && Objects.equals(telephone, other.telephone)
				&& Objects.equals(userName, other.userName) && userStatus == other.userStatus;
	}
	
	@Override
	public String toString() {
		return "User [password=" + password + ", id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", telephone=" + telephone + ", role=" + role + ", loggedIn=" + loggedIn
				+ ", userName=" + userName + ", userStatus=" + userStatus + ", area=" + area + "]";
	}

	// seeters and getters

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastname) {
		this.lastName = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Boolean getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStauts(UserStatus userStauts) {
		this.userStatus = userStauts;
	}
	
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

}