package Scheduler;

import Scheduler.controllers.AddEditAppointmentScreenController;
import Scheduler.controllers.AppointmentListScreenController;
import Scheduler.controllers.CustomerScreenController;
import Scheduler.models.User;
import Scheduler.controllers.LoginScreenController;
import Scheduler.controllers.MainScreenController;
import Scheduler.controllers.ReportScreenController;
import Scheduler.models.Appointment;
import Scheduler.models.DB_Manager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

/**
 *
 * @author Jeremy H
 */
public class SchedulerMain extends Application {
 
    ResourceBundle rb;
    User currentUser;
    
    boolean editClicked = false;
    private Stage primaryStage;
    private BorderPane mainMenu;
    Locale locale = Locale.FRENCH;
    
    private static Connection conn;
    AppointmentListScreenController alsController = new AppointmentListScreenController();
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Scheduler Application");
        loadLoginScreen();
    }
    
    /************************** Application screen setups *********************************/
    public void loadLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulerMain.class.getResource("/Scheduler/views/LoginScreen.fxml"));
        AnchorPane loginScreen = (AnchorPane) loader.load();
        LoginScreenController controller = loader.getController();
        controller.configureLoginScreen(this);
        Scene scene = new Scene(loginScreen);
        primaryStage.setScene(scene);
        primaryStage.show();
 
    }
    
    public void loadMainScreen(User currentUser) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulerMain.class.getResource("/Scheduler/views/MainScreen.fxml")); 
        mainMenu = (BorderPane) loader.load();
        Scene scene = new Scene(mainMenu);
        primaryStage.setScene(scene);
        MainScreenController controller = loader.getController();
        controller.showMainScreen(this, currentUser);
        primaryStage.show();
       
    }
    
    public void loadCustomerScreen(User currentUser) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulerMain.class.getResource("/Scheduler/views/CustomerScreen.fxml"));
        AnchorPane customerPane = (AnchorPane) loader.load();
        mainMenu.setCenter(customerPane);
        CustomerScreenController controller = loader.getController();
        controller.configureCustomerScreen(this, currentUser);
    }
    
    public void loadAppointmentListScreen(User currentUser) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulerMain.class.getResource("/Scheduler/views/AppointmentListScreen.fxml"));
        AnchorPane appointmentScreen = (AnchorPane) loader.load();
        mainMenu.setCenter(appointmentScreen);
        AppointmentListScreenController controller = loader.getController();
        controller.configureAppointmentListScreen(this, currentUser);
    }
    
    public boolean loadAddEditAppointmentScreen(Appointment appointment, User currentUser) throws IOException { // Used to load edit screen if appoinment was selected        
        editClicked = true;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulerMain.class.getResource("/Scheduler/views/AddEditAppointmentScreen.fxml"));
        AnchorPane aeAppointmentScreen = (AnchorPane) loader.load();    
        Stage addEditStage = new Stage();
        addEditStage.setTitle("Edit Appointments");
        addEditStage.initModality(Modality.WINDOW_MODAL);
        addEditStage.initOwner(primaryStage);
        AddEditAppointmentScreenController controller = loader.getController();
        Scene scene = new Scene(aeAppointmentScreen);
        addEditStage.setScene(scene);
        controller.populateAppointmentFields(appointment);
        System.out.println("current appointment id " + appointment.getAppointmentId());
        controller.configureAddEditAppointmentScreen(addEditStage, currentUser);
        addEditStage.showAndWait();
        return isLoaded();
    }
    
    public boolean loadAddAppointmentScreen(User currentUser) throws IOException { // loads add/edit screen with no selected appointment
        editClicked = false;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulerMain.class.getResource("/Scheduler/views/AddEditAppointmentScreen.fxml"));
        AnchorPane aeAppointmentScreen = (AnchorPane) loader.load();
        Stage addStage = new Stage();
        addStage.setTitle("Add Appointments");
        addStage.initModality(Modality.WINDOW_MODAL);
        addStage.initOwner(primaryStage);        
        AddEditAppointmentScreenController controller = loader.getController();       
        Scene scene = new Scene(aeAppointmentScreen);
        addStage.setScene(scene);
        controller.configureAddEditAppointmentScreen(addStage, currentUser);
        addStage.showAndWait();
        return isLoaded();
    }
       
    public void loadReportScreen(User currentUser) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SchedulerMain.class.getResource("/Scheduler/views/ReportScreen.fxml"));
        TabPane reportTab = (TabPane) loader.load();
        mainMenu.setCenter(reportTab);
        ReportScreenController controller = loader.getController();
        controller.configureReportScreen(this, currentUser);
    }
    
    public static void main(String[] args) throws SQLException, IOException {
      
        DB_Manager.initialize();
        conn = DB_Manager.getConnection();
        useLogger.startLog();
        launch(args);
        DB_Manager.closeDBConnection();
    }

    private boolean isLoaded() {
        return true;
    }

}
