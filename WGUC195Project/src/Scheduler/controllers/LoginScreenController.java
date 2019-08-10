package Scheduler.controllers;

import Scheduler.useLogger;
import Scheduler.SchedulerMain;
import Scheduler.models.Appointment;
import Scheduler.models.Customer;
import Scheduler.models.DB_Manager;
import Scheduler.models.User;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author Jeremy H
 */
public class LoginScreenController {
    
    Locale locale; 
    User user = new User();
    
    private SchedulerMain main;
    private static Logger LOG = Logger.getLogger(useLogger.class.getName());
    
    private final ZoneId newZID = ZoneId.systemDefault();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    ObservableList<Appointment> alertList;
    ResourceBundle rb = ResourceBundle.getBundle("Scheduler/resources/Language", Locale.getDefault()); 
        // ResourceBundle rb = ResourceBundle.getBundle("Scheduler/resources/Language", Locale.CHINA); // Testing login screen for Chinese
        // ResourceBundle rb = ResourceBundle.getBundle("Scheduler/resources/Language", Locale.FRANCE); // Testing login screen in French
    
    @FXML    
    private Label loginScreenStatusLabel;

    @FXML
    private Label loginScreenLowerStatusLabel;

    @FXML
    private Label loginScreenUsernameLabel;

    @FXML
    private Label loginScreenPasswordLabel;

    @FXML
    private Button loginScreenLoginButtonLabel;

    @FXML
    private TextField loginScreenUserNameField;

    @FXML
    private PasswordField loginScreenPasswordField;

    // Constructor
    public LoginScreenController() {
    }

    /*
    * On-Click action activated once button clicked on login screen 
    * Language should change with location to either French or Chinese (traditional)
    */
    @FXML
    void loginScreenButtonAction(ActionEvent event) throws IOException {

        String loginUserName = loginScreenUserNameField.getText();
        String loginPassword = loginScreenPasswordField.getText();

        if (loginUserName.length() == 0 || loginPassword.length() == 0) {
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(rb.getString("loginError"));
                alert.setHeaderText(rb.getString("loginError"));
                alert.setContentText(rb.getString("loginMessage"));
                alert.showAndWait();
           
                 loginScreenPasswordField.setText("");
            return;
            
        } else {
            User user = validateLogin(loginUserName, loginPassword);
            if (user == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(rb.getString("loginError"));
                alert.setHeaderText(rb.getString("loginError"));
                alert.setContentText(rb.getString("loginMessage"));
                alert.showAndWait();
           
                 loginScreenPasswordField.setText("");
            }
            else {
                    // System.out.println("isValid: " + user); debugging
                LOG.log(Level.INFO, (user.getUserName() + " logged in."));
                    // System.out.println("Logger has started"); debugging
                
                populateAlertList();
                getLoginAppointmentAlert();
                main.loadMainScreen(user);
                main.loadAppointmentListScreen(user);
            }
        }
    }
    
    /*
    * Login validation method checks username and password in DB
    */
    User validateLogin(String username, String password) {
            // System.out.println("validating login"); debugging
        try {
            String userQuery = "SELECT * FROM user WHERE userName=? AND password=?";
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(userQuery);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user.setUserID(rs.getInt("userId"));
                user.setUserName(rs.getString("userName"));
                user.setPassword(rs.getString("password"));
                    // System.out.println("username is " + username); debugging
                    // System.out.println("password is " + password); debugging
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
    
    /*
    * Login screen setup. 
    */
    public void configureLoginScreen(SchedulerMain main) {
            // System.out.println("loading login screen"); debugging
        this.main = main;
       
        alertList = FXCollections.observableArrayList();
        
        loginScreenStatusLabel.setText(rb.getString("loginmainheader"));
        loginScreenLowerStatusLabel.setText(rb.getString("subheader"));
        loginScreenUsernameLabel.setText(rb.getString("username"));
        loginScreenPasswordLabel.setText(rb.getString("password"));
        loginScreenLoginButtonLabel.setText(rb.getString("login"));

    }
    
    /*
    * Login alert check. When user logs in should give alert if an appointment 
    * is found to be within 15 minutes of login time.
    */
    private void getLoginAppointmentAlert() {
       int count = 0;
       if (count < 1) {
           // configureAppointmentList();
                // System.out.println("checking appointment alerts for: " + user); debugging
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowP15 = now.plusMinutes(15);
          
            FilteredList<Appointment> filteredList = new FilteredList<>(alertList);
            filteredList.setPredicate(row -> { 
                LocalDateTime LDT = LocalDateTime.parse(row.getStart(), timeDTF);
                return LDT.isAfter(now.minusMinutes(1)) && LDT.isBefore(nowP15);
            });
            
            if(filteredList.isEmpty()) {
                    // System.out.println("No upcoming appointments within 15 minutes."); debugging
            }
            
            else {
                    // System.out.println("You do have an upcoming appointment."); debugging
                String customer = filteredList.get(0).getCustomer().getCustomerName();
                String title = filteredList.get(0).getTitle();
                String startTime = filteredList.get(0).getStart();
                    
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming Appointment Alert");
                alert.setHeaderText("You have an appointment within 15 minutes.");
                alert.setContentText("Your " + title + " appointment with " + customer + " starts at " + startTime);//************************************finish this part for upcoming appointments
                alert.showAndWait();
            }
       }
        count++;
    }
    
    /*
    * DB connection to populate alert list (used above) with appointments so they can be checked.
    */    
    void populateAlertList() {
       
                // System.out.println("Configuring appointment list"); debugging
        String query = "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, appointment.`start`, appointment.`end`, "
                + "customer.customerId, customer.customerName, appointment.createdBy "
                + "FROM appointment, customer "
                + "WHERE appointment.customerId = customer.customerId "
                + "ORDER BY `start` ";
        
        try (
                PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
                
                ResultSet rs = ps.executeQuery();) {
            
            while (rs.next()) {
                Timestamp startTime = rs.getTimestamp("appointment.start");
                ZonedDateTime newZDTStart = startTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localST = newZDTStart.withZoneSameInstant(newZID);

                Timestamp endTime = rs.getTimestamp("appointment.end");
                ZonedDateTime newZDTEnd = endTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localET = newZDTEnd.withZoneSameInstant(newZID);

                String tappointmentId = rs.getString("appointment.appointmentId");
                String tappointmentTitle = rs.getString("appointment.title");
                String tappointmentType = rs.getString("appointment.description");
               
                String tuser = rs.getString("appointment.createdBy");
                Customer tcustomer = new Customer(rs.getString("customer.customerId"), rs.getString("customer.customerName"));
                Customer tcustomerName = new Customer(rs.getString("customerName"));
                Customer tcustomerId = new Customer(rs.getString("customer.customerId"));
                
                alertList.add(new Appointment(tappointmentId, tappointmentTitle, tappointmentType, localST.format(timeDTF), localET.format(timeDTF), tcustomer, tcustomerName, tcustomerId, tuser));

                        //System.out.println("data added to appointment lists"); debugging
                        //System.out.println("heres the list: " + alertList); debugging
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
 

}
