package com.colibri.toread.auth;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.colibri.toread.ToReadBaseEntity;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;

@Entity
public class User extends ToReadBaseEntity {
	@Indexed(value=IndexDirection.ASC, name="userNameIndex", unique=true)
	private String userName;
	private String emailAddress;
	private Password password;
	@Embedded
	private ArrayList<Device> devices = new ArrayList<Device>(); //All of a users registered devices
	private String firstName;
	private String lastName;
	private Date dob;
	
	//Index list of book ids so we can retrieve the books as user has without holding
	//direct copies of them
	//Map of object ID to boolean flag indicating if the book has been read or not
	private HashMap<ObjectId, Boolean> bookMap = new HashMap<ObjectId, Boolean>();
	private HashSet<ObjectId> deletedBooks = new HashSet<ObjectId>();
	
	private static Logger logger = Logger.getLogger(User.class);
	
	public User() {	
	}
	
	public boolean userFromJSON(JSONObject json) {
		try {
			if(json.has("username") ) {
				this.setUserName(json.getString("username"));
			}
			else {
				logger.error("Username field not found in user info JSON");
				return false;
			}
			
			if(json.has("first_name") ) {
				this.setFirstName(json.getString("first_name"));
			}
			else {
				logger.error("First name field not found in user info JSON");
				return false;
			}
			
			if(json.has("last_name") ) {
				this.setLastName(json.getString("last_name"));
			}
			else {
				logger.error("Last name field not found in user info JSON");
				return false;
			}
			
			if(json.has("email_address") ) {
				this.setEmailAddress(json.getString("email_address"));
			}
			else {
				logger.error("Email address field not found in user info JSON");
				return false;
			}
			
			if(json.has("password") ) {
				this.setNewPassword(json.getString("password"));
			}
			else {
				logger.error("Password field not found in user info JSON");
				return false;
			}
						
			if(json.has("dob") ) {
				String dateString = json.getString("dob");
				//E.g "January 2, 2010"
				Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(dateString);
				this.setDob(date);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		
		return true;
	}
	
	
	public void setNewPassword(String password){
		try {
			this.password = new Password();
			this.password.encrypt(password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
	
	public boolean validatePassword(String password){
		if(password == null)
			return false;
		
		try {
			return this.password.validate(password);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void markAsDeleted(ObjectId bookId) {
		//Remove a book from the active map and move to the deleted set
		deletedBooks.add(bookId);
		bookMap.remove(bookId);
		
		logger.info("Book Id " + bookId.toString() + " was marked for deletion");
	}

	//Add a new device to this user object
	public void addDevice(Device newDevice){
		devices.add(newDevice);
	}
	
	public Device findDevice(String id){
		for(Device device : devices){
			if(device.getObjectId().toString().compareTo(id) == 0)
				return device;
		}

		return null;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public HashMap<ObjectId, Boolean> getBookIds(){
		return this.bookMap;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Password getPassword() {
		return password;
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<Device> devices) {
		this.devices = devices;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public HashMap<ObjectId, Boolean> getbookMap() {
		return bookMap;
	}
	
	public boolean hasBook(ObjectId bookId) {
		return bookMap.containsKey(bookId);
	}

	public void setbookMap(HashMap<ObjectId, Boolean> bookMap) {
		this.bookMap = bookMap;
	}

	public String getUserName() {
		return userName;
	}
	
}
	
