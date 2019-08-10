package Scheduler.controllers;

import Scheduler.SchedulerMain;
import Scheduler.models.Appointment;
import Scheduler.models.Customer;
import Scheduler.models.DB_Manager;
import Scheduler.models.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 * @author Jeremy H
 */
public class AddEditAppointmentScreenController {

    Locale local = Locale.getDefault();
    SchedulerMain main;
    private Stage stage;
    private User currentUser;
    boolean editClicked;
    
    private static Connection conn;
    private Appointment selectAppointment;
    
    CustomerScreenController controller = new CustomerScreenController();
    AppointmentListScreenController alcontroller = new AppointmentListScreenController();
    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    
    private ObservableList<Customer> customerList;
    private final ObservableList<String> startingTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endingTimes = FXCollections.observableArrayList();
    
    private final ZoneId newZID = ZoneId.systemDefault();
    private final DateTimeFormatter dateDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    
    @FXML
    private Label addEditAppointmentScreenLabel;
    
    @FXML
    private TextField appointmentScreenCustomerIdField;
    
    @FXML
    private TextField appointmentScreenCustomerNameField;
    
    @FXML
    private TextField appointmentScreenAppointmentSearchField;

    @FXML
    private TextField appointmentScreenAppointmentNameField;
    
    @FXML
    private TextField appointmentScreenAppointmentIdField;

    @FXML
    private DatePicker appointmentScreenDatePicker;

    @FXML
    private ComboBox<String> appointmentScreenStartTimeBox;

    @FXML
    private ComboBox<String> appointmentScreenEndTimeBox;

    @FXML
    private ComboBox<String> appointmentScreenAppointmentTypeBox;

    @FXML
    private TableView<Customer> appointmentScreenCustomerTable;

    @FXML
    private TableColumn<Customer, String> appointmentScreenAddCustomerNameColumn;
    
    @FXML
    private TableColumn<Appointment, Customer> appointmentScreenAddCustomerIdColumn;
    
    @FXML
    private TableColumn<Appointment, Customer> appointmentScreenAddCustomerAppointmentIdColumn;
    
    /**************************** Button Actions ******************************/
    /*
    * Save Button activation. Decyphers between current appointments being edited and new appointments being saved.
    */
    @FXML
    private void appointmentScreenSaveButtonAction(ActionEvent event) throws IOException, SQLException {
       if (isValidAppointment()) {
           if(editClicked) {
               System.out.println("Edit button clicked!" );
               updateAppointment();
           }
           else {
               saveAppointment(); 
           }  
       }
       stage.close(); 
    }
    
    /************************* Appointment DB Actions ***************************/
    /*
    * Save appointment method. Save appointment to Db
    */
    private void saveAppointment() throws IOException { 
        
        LocalDate localDate = appointmentScreenDatePicker.getValue();
	LocalTime startTime = LocalTime.parse(appointmentScreenStartTimeBox.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(appointmentScreenEndTimeBox.getSelectionModel().getSelectedItem(), timeDTF);
            // System.out.println("Local Date and times set " + startTime + " + " + endTime); debugging
        
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(newZID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(newZID).withZoneSameInstant(ZoneId.of("UTC"));            
	
	Timestamp startTS = Timestamp.valueOf(startUTC.toLocalDateTime()); 
        Timestamp endTS = Timestamp.valueOf(endUTC.toLocalDateTime());        
            //ystem.out.println("Timestamps configured set");debugging
        
        String query = "INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?); ";
      
        try {
            
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
                    //System.out.println("Setting data"); debugging
                  
                ps.setString(1, appointmentScreenCustomerTable.getSelectionModel().getSelectedItem().getCustomerId());
                ps.setInt(2, currentUser.getUserID());
                ps.setString(3, appointmentScreenAppointmentNameField.getText()); // title
                ps.setString(4, "not needed"); // description
                ps.setString(5, "not needed"); // location
                ps.setString(6, "not needed"); //contact
                ps.setString(7, appointmentScreenAppointmentTypeBox.getValue()); //type
                ps.setString(8, "not needed");
                ps.setTimestamp(9, startTS); //start
                ps.setTimestamp(10, endTS); //end
                    // System.out.println("made it to usename section1 with: " + currentUser); debugging
                ps.setObject(11, currentUser.getUserName()); 
                    // System.out.println("made it to usename section2 with " + currentUser);debugging
                ps.setObject(12, currentUser.getUserName());
                ps.executeUpdate(); 
                    // System.out.println("ps Query executed to save info"); debugging
                
        } catch (SQLException e) {
            e.printStackTrace();
        }   
    }

    /*
    *Updates appointment Table. Appointment table has a PK of addressId and all fields are Not Null.
    *   Appointment table also has 2 FK's for customer Table customer Id and User Table userId.
    *   AppointmentId is auto-incremented.
    */
    private void updateAppointment() { 

        LocalDate localDate = appointmentScreenDatePicker.getValue();
	LocalTime startTime = LocalTime.parse(appointmentScreenStartTimeBox.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(appointmentScreenEndTimeBox.getSelectionModel().getSelectedItem(), timeDTF);
            // System.out.println("Local Date and times set " + startTime + " + " + endTime); debugging
        
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(newZID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(newZID).withZoneSameInstant(ZoneId.of("UTC"));            
	
	Timestamp startTS = Timestamp.valueOf(startUTC.toLocalDateTime()); 
        Timestamp endTS = Timestamp.valueOf(endUTC.toLocalDateTime());        
           // System.out.println("Timestamp configuration set"); debugging
         
        String query = "UPDATE appointment "
                + "SET customerId=?, title=?, type=?, start=?, end=?, lastUpdate=CURRENT_TIMESTAMP, lastUpdateBy=? "
                        + "WHERE appointmentId=?";   
            
        try {
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
                    // System.out.println("Setting data" ); debugging
         
                ps.setString(1, appointmentScreenCustomerIdField.getText());
                ps.setString(2, appointmentScreenAppointmentNameField.getText()); // appointment title 
                ps.setString(3, appointmentScreenAppointmentTypeBox.getValue()); // appointment type
                ps.setTimestamp(4, startTS); //start
                ps.setTimestamp(5, endTS); //end
                ps.setString(6, currentUser.getUserName());
                ps.setString(7, appointmentScreenAppointmentIdField.getText());
        
                ps.executeUpdate();
                //System.out.println("Update was made to appointment " + selectAppointment.getAppointmentId()); debugging
     
        } catch (SQLException e) {
            e.printStackTrace();
        }   
    }

    /********************* Add Edit Screen Configuration ***********************/
    /*
    * Initial screen configuration. Called when loading screen. Decyphers between edit clicked and add clicked
    *   for screen configruation.
    */
    @FXML
    public void configureAddEditAppointmentScreen(Stage apptStage, User currentUser) { // Initial screen setup when adding (not editing) appointment
        this.stage = apptStage;
        this.currentUser = currentUser;
        customerList = FXCollections.observableArrayList();
        
        if(editClicked) {
            addEditAppointmentScreenLabel.setText("Edit Appointment Screen");
            editClicked = true;
        }
        else {
            addEditAppointmentScreenLabel.setText("Add AppointmentScreen");
            editClicked = false;
        }
    
        populateAppointmentTypeBox();
        populateAppointmentScreenTimeBoxes();
        configureAppointmentScreenDatePicker(); 
        customerList = populateCustomerNameTable();
        
        // lambda expression to actively filter customer names while entering text in search box. Used to 
        // help eliminate code bloat.
        // Wrap Oberservable list in a filtered list (here it still displays all data, not filtered yet)
        FilteredList<Customer> fList = new FilteredList<>(customerList, f -> true); 
        // Listener implemented for when the search field criteria changes
        appointmentScreenAppointmentSearchField.textProperty().addListener((obsV, oldV, newV) -> { 
            fList.setPredicate(customer -> {
                    // If search box is empty display all data(names) and filter out names as text is entered
                    if(newV == null || newV.isEmpty()) {
                        return true;
                    }
                    // compare filtered text to current list of customer names
                    String caseFilter = newV.toLowerCase();
                    if(customer.getCustomerName().toLowerCase().contains(caseFilter)) {
                        // True returned for first matching name
                        return true;
                    }
                    // False returned if no matching names found on list
                    return false;
            });
        });
        // Filtered list wrapped in a sorted list
        SortedList<Customer> sList = new SortedList<>(fList);
        // Filtered and now sorted list added to the appoitment screen customer table
        appointmentScreenCustomerTable.setItems(sList.sorted());
        appointmentScreenCustomerTable.getSelectionModel().selectedItemProperty().addListener((obsV, oldV, newV) -> populateCustomerNameField(newV)); 
        appointmentScreenAddCustomerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        appointmentScreenAddCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
   
    }
    
   
    /********************* Methods for populating screen Table and choice boxes ******************/
     protected ObservableList<Customer> populateCustomerNameTable() { // populates customer Name table
        String lCustomerId;
        String lCustomerName;
        String query = "SELECT customer.customerName, customer.customerId " +
            "FROM customer, address, city, country " +
                "WHERE customer.addressId=address.addressId AND address.cityId=city.cityId AND city.countryId=country.countryId; ";            
        ObservableList<Customer> customerNameList = FXCollections.observableArrayList();
        
        try {
   
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
        
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lCustomerId = rs.getString("customer.customerId");
                lCustomerName = rs.getString("customer.customerName");
 
                customerNameList.add(new Customer(lCustomerId, lCustomerName));         
            } 
        } catch (SQLException s) {
            System.out.println("Sorry SQL query error"); 
            s.printStackTrace();
        } catch (Exception e) {
           System.out.println("Error not associated with SQL occurred."); 
        }      
        return customerNameList;
    }
     
    public void AddEditAppointmentScreenPrepopulateEndTime() {
        String start = appointmentScreenStartTimeBox.getSelectionModel().getSelectedItem();
        appointmentScreenEndTimeBox.setValue(start);  
    }
    
    public void populateAppointmentTypeBox() { //populates appointment choice box with appointment types
        ObservableList<String> types = FXCollections.observableArrayList();
        types.addAll("Presentation", "Scrum", "Initial", "Followup", "Consult", "Other" );
        appointmentScreenAppointmentTypeBox.setItems(types);
    }
                
    public void populateCustomerNameField (Customer customer) { // When customer name clicked in Customer name table fields to left populated with info
        appointmentScreenCustomerNameField.setText(customer.getCustomerName());
        appointmentScreenCustomerIdField.setText(customer.getCustomerId());
        appointmentScreenAppointmentIdField.setText(customer.getAppointmentId());
    }
    
    public void populateAppointmentFields(Appointment currentAppointment) {  // populates appointment info fields for selected appointment from appointment screen    
        selectAppointment = currentAppointment;
        editClicked = true;
            
        String startTime = selectAppointment.getStart();
        LocalDateTime startTimeLDT = LocalDateTime.parse(startTime, dateDTF);
        String endTime = selectAppointment.getEnd();
        LocalDateTime endTimeLDT = LocalDateTime.parse(endTime, dateDTF);        
        
        appointmentScreenCustomerNameField.setText(selectAppointment.getCustomerName().toString());
        appointmentScreenAppointmentNameField.setText(selectAppointment.getTitle());
        appointmentScreenDatePicker.setValue(LocalDate.parse(selectAppointment.getStart(), dateDTF));
        appointmentScreenStartTimeBox.getSelectionModel().select(startTimeLDT.toLocalTime().format(timeDTF));
        appointmentScreenEndTimeBox.getSelectionModel().select(endTimeLDT.toLocalTime().format(timeDTF));
        appointmentScreenAppointmentTypeBox.setValue(selectAppointment.getType()); 
        appointmentScreenCustomerTable.getSelectionModel().select(selectAppointment.getCustomer());
        appointmentScreenCustomerIdField.setText(selectAppointment.getCustomerId().toString());
        appointmentScreenAppointmentIdField.setText(selectAppointment.getAppointmentId());

    }

    public void populateAppointmentScreenTimeBoxes() { //populates Start/End time appointment Drop down boxes
        LocalTime time = LocalTime.of(7, 0);  // time set for 7 am (normal business hours)
        
	do {
            startingTimes.add(time.format(timeDTF));
            endingTimes.add(time.format(timeDTF));                                                  
            time = time.plusMinutes(15);
        }
        while(!time.equals(LocalTime.of(18, 15))); // time set for 6pm at 15 minute intervals (normal business hours)
            startingTimes.remove(startingTimes.size() - 1);
            endingTimes.remove(0);

            appointmentScreenStartTimeBox.setItems(startingTimes);
            appointmentScreenEndTimeBox.setItems(endingTimes);
    }
    
    public void configureAppointmentScreenDatePicker() { // populates date picker box with local date
        appointmentScreenDatePicker.setValue(LocalDate.now());
    }

    /****************** Appointment Validation checks **********************/
    /*
    *Check to verify appointment is valid, if not valid alert pops up.
    */
    private boolean isValidAppointment() throws SQLException {
            // System.out.println("Checking appointments valitidity"); debugging
           
        String type = appointmentScreenAppointmentTypeBox.getSelectionModel().getSelectedItem();
        String title = appointmentScreenAppointmentNameField.getSelectedText();
        String errorMessage = "";
           //  System.out.println("Initial fields valid"); debugging
            
        LocalDate localDate = appointmentScreenDatePicker.getValue();
        LocalTime startTime = LocalTime.parse(appointmentScreenStartTimeBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime endTime = LocalTime.parse(appointmentScreenEndTimeBox.getSelectionModel().getSelectedItem(), timeDTF);
  
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(newZID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(newZID).withZoneSameInstant(ZoneId.of("UTC"));   
           // System.out.println("Time fields are valid"); debugging
   
        if(startUTC == null) {
            errorMessage += "Please select a valid start time.\n";
        }
        if(endUTC == null) {
            errorMessage += "Please select a valid end time.\n";
            
        } else if(endUTC.equals(startUTC) || startUTC.isAfter(endUTC)) {
                  errorMessage += "Error start time must be before end time.\n"; 
                  
        } else if(appointmentTimeCheck(startUTC, endUTC)) {
            errorMessage += "Sorry but the appointment times conflict with another appointment.\n";
        }
        if(errorMessage.length() == 0) {
            System.out.println("Returning True - valid");
            return true;
        }
        if(type == null) {
            errorMessage += "Please select a valid appointment type.\n";
        }
        if(title == null) {
            errorMessage += "Please select a valid appointment title.\n";
        }
        if(errorMessage.length() == 0) {
            return true;
        }
        
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error saving appointment, please try again.\n");
            alert.setHeaderText("Sorry but there was a problem saving appointment.");
            alert.setContentText(errorMessage);
            alert.showAndWait();
                 // System.out.println("Returning False - not valid"); debugging
            return false;
        }
    }
    
    /*
    * On-Click action activated once button clicked on login screen 
    * Language should change with location to either French or Chinese (traditional)
    */
    private boolean appointmentTimeCheck(ZonedDateTime start, ZonedDateTime end) throws SQLException {
        String user;
        String appointmentId;
        String query = "SELECT * FROM appointment "
                + "WHERE (? BETWEEN start AND end OR ? BETWEEN start AND end OR ? < start AND ? > end) "
                + "AND (createdBy=? AND appointmentId!=?)";
        
        if(editClicked) {
            appointmentId = appointmentScreenAppointmentIdField.getText();
            user = selectAppointment.getUser();
        }
        else {
            appointmentId = "";
            user = currentUser.getUserName();
        }
    
        PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
        
        ps.setTimestamp(1, Timestamp.valueOf(start.toLocalDateTime()));
        ps.setTimestamp(2, Timestamp.valueOf(end.toLocalDateTime()));
        ps.setTimestamp(3, Timestamp.valueOf(start.toLocalDateTime()));
        ps.setTimestamp(4, Timestamp.valueOf(end.toLocalDateTime()));
       
        ps.setString(5, user);
        ps.setString(6, appointmentId);
    
        
        ResultSet rs = ps.executeQuery();
        
        if(rs.next()) {
            return true;
        } else {
        return false;
        }
    
    }
    
    /****************************** Not Currently Used *********************************/
    /*
     private void enableFieldEdit() { // currently not used
        appointmentScreenAddCustomerNameColumn.setEditable(true);
    }
    
    private void disableFieldEdit() { // currently not used
        appointmentScreenAddCustomerNameColumn.setEditable(false);
    }

    protected ObservableList<Appointment> populateAppointmentList() { // configures appointment list without a selected appointment
        editClicked = false;
        System.out.println("Configuring appointment list"); //////////////////////////
            System.out.println("This is under the <Appiontment> populateappointment list");
            
            
        String query = "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.type, appointment.`start`, appointment.`end`, "
                + "customer.customerId, customer.customerName, appointment.createdBy " +
            "FROM appointment, customer " +
            "WHERE appointment.customerId = customer.customerId " +
            "ORDER BY `start`; ";
        
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
                
                String appointmentId = rs.getString("appointmentId");
                String appointmentTitle = rs.getString("appointment.title");
                String appointmentType = rs.getString("appointment.type");
                String user = rs.getString("appointment.createdBy");
                Customer customer = new Customer(rs.getString("customerId"), rs.getString("customerName"));
                Customer customerName = new Customer(rs.getString("customerName"));
                Customer customerId = new Customer(rs.getString("customerId"));
                
                appointmentList.add(new Appointment(appointmentId, appointmentTitle, appointmentType, localST.format(timeDTF), localET.format(timeDTF), customer, customerName, customerId, user));
                
            }
             
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentList;
    }

    */
}
