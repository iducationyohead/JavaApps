package Scheduler.models;

/**
 *
 * @author Jeremy H
 */
public class User {

    private int userID;
    private String user;
    private String userName;
    private String password;
    
    // Constructor I
    public User() {}
    
    // Constructor II
    public User(int userId, String username, String password) {
        this.userID = userId;
        this.userName = username;
        this.password = password;
        
    }
    
    /************************** Getters ***************************************/
    public int getUserID() {
        return userID;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    /************************** Setters ***************************************/
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

}
