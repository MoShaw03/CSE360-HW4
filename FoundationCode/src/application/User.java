package application;
import java.util.List;		
import java.util.ArrayList; //added import for lists

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private List<String> roles = new ArrayList<String>();
    private String email;		// Added an email variable
    
    // Constructor to initialize a new User object with userName, password, and role.
    public User(String userName, String password, String role, String email) {
        this.userName = userName;
        if(password != "") {this.password = password;}
        // Changed the role to be a list to accomodate users withmultiple roles
        // The if statement is to prevent errors with the way the lists work
        if(role != "") {this.roles.add(role);}	
        if(email!= "") {this.email = email;}
    }
    
    // Added some mutator methods to accomodate changing usernames or passwords
    public void setUserName(String userName) {this.userName = userName;}
    public void setPassword(String password) {this.password = password;}
    public void setEmail(String email) {this.email = email;}
    
    // Sets the role of the user.
    // Changed the setRole method to allow for adding multiple roles to a single user
    public void setRole(String role) {									
    	// Checks if the user already has this role. If they do, do nothing
    	if(roles.contains(role)) {return;}    	
    	// Checks if multiple roles are being adding at once.
    	else if(role.contains(",")){
    		String[] rolearray = role.split(",");
    		for(int i = 0; i<rolearray.length ; i++) {
    			roles.add(rolearray[i].trim());
    		}   		    		
    	}
    	// Otherwise add the role normally
    	else {roles.add(role);}
}
    
    public void removeRole(String role) {
    	roles.remove(role);
    }

    public String getUserName() 	{return userName;}
    public String getPassword() 	{return password;}
    public String getEmail()		{return email;}
    public String getRole()	{ 		// Returns the list as a string to preserve functionality of the original code.
    	if(roles.size() == 1) {return roles.getFirst();}
    	else {return roles.toString().substring(1, roles.toString().length() - 1);}// The substring part of the code removes the brackets of the list
    }						
    public List<String> getRoleList(){return roles;} // Returns the list as a list
    
    /**
     * Returns true if this user holds the given role. Only checks for role at a time. 
     * @param role role name.
     * @return whether the role is present.
     */
    public boolean hasRole(String role) {
        if (role == null) return false;
        for (String r : roles) {
            if (role.equalsIgnoreCase(r)) return true; // why: centralizes checks; avoids string CSV parsing
        }
        return false;
    }
    
    public String toString() {
    	return userName;
    }
    	
    
}
    
