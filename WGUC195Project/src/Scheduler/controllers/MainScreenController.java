package Scheduler.controllers;

import Scheduler.SchedulerMain;
import Scheduler.models.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;

/**
 * FXML Controller class
 *
 * @author Jeremy H
 */
public class MainScreenController {

    private SchedulerMain main;
    private User currentUser;
    Locale local = Locale.getDefault();
    ResourceBundle rb; 
     
    public MainScreenController() {}
    
    @FXML
    private MenuItem mainScreenAppointmentsItem;
    
    @FXML
    private MenuItem mainScreenCustomersItem;
    
    @FXML
    private MenuItem mainScreenReportsItem;
    
    
    @FXML
    public void showMainScreen(SchedulerMain main, User currentUser) {
        this.main = main;
        this.currentUser = currentUser;
    }
 
    @FXML
    void SHOW_APPOINTMENTS(ActionEvent event) throws IOException { 
            // System.out.println("main screen appointment button clicked"); debugging
            // System.out.println("current user is: " + currentUser);debugging
       main.loadAppointmentListScreen(currentUser);
    }

    @FXML
    void SHOW_CUSTOMERS(ActionEvent event) throws IOException {
        try {
            main.loadCustomerScreen(currentUser);
            
        } catch (SQLException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void SHOW_REPORTS(ActionEvent event) throws IOException, SQLException {
        try {
            main.loadReportScreen(currentUser);
    
        } catch (IOException e) {
            System.out.println(e.getMessage() + "Report Screen load failed");
        }  
    }

    @FXML
    void EXIT_HANDLER(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Please confirm you wish to Exit the application?");
        
// Alert using lambda approach
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK).
                ifPresent((ButtonType response) -> {
                    System.exit(0);
                            // System.out.println("Logout"); debugging
                });
    }

    @FXML
    void LOGOUT_HANDLER(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Please confirm you wish to Logout of application?");
        
        // Alert using lambda approach
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK).
                ifPresent((ButtonType response) -> {

                    try {
                        main.loadLoginScreen();
                    } catch (IOException ex) {
                        
                        Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
    }  
}
