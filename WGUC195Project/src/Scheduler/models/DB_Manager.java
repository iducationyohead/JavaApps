package Scheduler.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Jeremy H
 */
public class DB_Manager {
    
    public static String DB_NAME = "U056MG";
    public static String DB_URL = "jdbc:mysql://52.206.157.109/U056MG";
    public static String USERNAME = "U056MG";
    public static String PASSWORD = "53688428694";
    public static String DRIVER = "com.mysql.jdbc.Driver";
    private static Connection connection;

    
    // Constructor
    public DB_Manager(){}

    /******************************* Methods **********************************/
    
    public static Connection getConnection() {
        System.out.println("DB Manager is getting DB Connection...");
        return connection;
    }
    
    public static void initialize() throws SQLException{
        System.out.println("DB Manager is Initializing DB Connection...");
        connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);      
    }  

    public static void closeDBConnection() throws SQLException {
        System.out.println("DB Manager is Closing DB Connection...");
        connection.close();
    }
    
    
}
