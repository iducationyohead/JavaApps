package Scheduler.controllers;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Jeremy H
 */
public class AppointmentListScreenController {

    boolean editClicked = false;
    
    private User currentUser;
    private SchedulerMain main;
    
    private final ZoneId newZID = ZoneId.systemDefault();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    
    @FXML
    private RadioButton appointmentWeeklyViewRadio;
          
    @FXML
    private RadioButton appointmentMonthlyViewRadio;
     
    @FXML
    private ToggleGroup appointmentViewToggle;
     
    @FXML
    private TableView<Appointment> appointmentViewTable;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> appointmentViewStartColumn;
    
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentViewEndColumn;

    @FXML
    private TableColumn<Appointment, Customer> appointmentViewCustomerColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentViewTypeColumn;
    
    @FXML
    private TableColumn<Appointment, String> appointmentViewTitleColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentViewAuthorColumn;
    
    @FXML
    private TableColumn<Appointment, Customer> appointmentViewCustomerIdColumn;
    
    @FXML
    private TableColumn<Appointment, Customer> appointmentViewAppointmentIdColumn;
    
    /**************************** Button Actions ******************************/
    @FXML
    void appointmentListAddButtonAction(ActionEvent event) throws IOException {
        try {
            editClicked = main.loadAddAppointmentScreen(currentUser);
            
        } catch (IOException ex) {
            Logger.getLogger(AppointmentListScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        main.loadAppointmentListScreen(currentUser);
    }
   
    @FXML
    void appointmentListEditButtonAction(ActionEvent event) throws IOException { 
        Appointment selectAppointment = appointmentViewTable.getSelectionModel().getSelectedItem();
       
        if (selectAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error Nothing Selected");
            alert.setHeaderText("No Appointment was selected to Edit");
            alert.setContentText("Please select an Appointment to Edit");
            alert.showAndWait();    
        }    
        else {
            boolean editClicked = main.loadAddEditAppointmentScreen(selectAppointment, currentUser);
        }
        main.loadAppointmentListScreen(currentUser);
    }
     
    @FXML
    void appointmentListDeleteButtonAction(ActionEvent event) {
         Appointment selectAppointment = appointmentViewTable.getSelectionModel().getSelectedItem();
       
        if (selectAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error Nothing Selected");
            alert.setHeaderText("No Appointment was selected to Edit");
            alert.setContentText("Please select an Appointment to Edit");
            alert.showAndWait();    
        }    
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delete Appointment");
            alert.setHeaderText("You're about to delete an appointment");
            alert.setContentText("Please confirm you'd like to delte select appointment.");
            
            // Alert using lambda filter approach to help with code readabilty. 
            alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    deleteSelection(selectAppointment);
                    try {
                        main.loadAppointmentListScreen(currentUser);
                    } catch (IOException ex) {
                        Logger.getLogger(AppointmentListScreenController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            );
        }
    }
    
     @FXML
    void appointmentWeeklyViewRadioButtonSelected(ActionEvent event) { //Appear to work cant test until Add customer works!!
        LocalDate now = LocalDate.now();
        LocalDate nowP7 = now.plusDays(7);
        
        FilteredList<Appointment> filteredList = new FilteredList<>(appointmentList); //
        // lambda with predicate used to help with code readabilty.
        filteredList.setPredicate(row -> {
            LocalDate rowDate = LocalDate.parse(row.getStart(), timeDTF);
            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowP7);
        });
        
        appointmentViewTable.getItems().setAll(filteredList);
    }
    
    @FXML
    void appointmentMonthlyViewRadioButtonSelected(ActionEvent event) {  //Appear to work cant test until Add customer works!!
        LocalDate now = LocalDate.now();
        LocalDate nowP1M = now.plusMonths(1);
        
        FilteredList<Appointment> filteredList = new FilteredList<>(appointmentList); 
        // lambda with predicate used to help with code readabilty.
        filteredList.setPredicate(row -> {
            LocalDate rowDate = LocalDate.parse(row.getStart(), timeDTF);
            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowP1M);
        });
        appointmentViewTable.getItems().setAll(filteredList);
    }
    
    /***************** Appointment Screen Configuration ***********************/
    public void configureAppointmentListScreen(SchedulerMain main, User currentUser) { // sets up appointment screen when initially loading
        this.currentUser = currentUser;
        this.main = main;
        
            // System.out.println("configuring monthly screen in progress...");debugging
        appointmentViewToggle = new ToggleGroup();
        appointmentWeeklyViewRadio.setToggleGroup(appointmentViewToggle);
        appointmentMonthlyViewRadio.setToggleGroup(appointmentViewToggle);
     
            // System.out.println("Toggle groups configured...");debugging
        populateAppointmentList();
        
        appointmentViewStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentViewEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentViewCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        appointmentViewTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentViewTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentViewAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        appointmentViewCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentViewAppointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            //System.out.println("Setting cell values finished..."); debugging
        appointmentViewTable.getItems().setAll(appointmentList);
            //System.out.println("populating in progress..."); debugging
    }
   
    void populateAppointmentList() {
        // System.out.println("Configuring appointment list"); debugging
      
        String query = "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.type, appointment.`start`, appointment.`end`, "
                + "customer.customerId, customer.customerName, appointment.createdBy " +
            "FROM appointment, customer " +
            "WHERE appointment.customerId = customer.customerId " +
            "ORDER BY `start`; ";
                
        try (
            
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
       
            ResultSet rs = ps.executeQuery();) {
            while(rs.next()) {
                
                Timestamp startTime = rs.getTimestamp("appointment.start");
                ZonedDateTime newZDTStart = startTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localST = newZDTStart.withZoneSameInstant(newZID);
                
                Timestamp endTime = rs.getTimestamp("appointment.end");
                ZonedDateTime newZDTEnd = endTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localET = newZDTEnd.withZoneSameInstant(newZID);
                
                String tappointmentId = rs.getString("appointment.appointmentId");
                String tappointmentTitle = rs.getString("title");
                String tappointmentType = rs.getString("type"); 
                String tuser = rs.getString("appointment.createdBy");
                Customer tcustomer = new Customer(rs.getString("appointment.customerId"), rs.getString("customer.customerName"));
                Customer tcustomerId = new Customer(rs.getString("appointment.customerId"));
                Customer tcustomerName = new Customer(rs.getString("customer.customerName"));
            
                System.out.println("data added to appointment lists");
                System.out.println("heres the list: " + appointmentList);
                
                appointmentList.add(new Appointment(tappointmentId, tappointmentTitle, tappointmentType, localST.format(timeDTF), localET.format(timeDTF), tcustomer, tcustomerName, tcustomerId, tuser));
            }
             
        } catch (SQLException e) {
            e.printStackTrace();
        }      
    }
   
   
    /*********************** Data Manipulation **********************/
    private void deleteSelection(Appointment appointment) {
        String query = "DELETE appointment.* FROM appointment WHERE appointment.appointmentId = ?";
        try{           
            PreparedStatement pst = DB_Manager.getConnection().prepareStatement(query);
            pst.setString(1, appointment.getAppointmentId()); 
            pst.executeUpdate();  
                
        } catch(SQLException e){
            e.printStackTrace();
        }       
    }

}
