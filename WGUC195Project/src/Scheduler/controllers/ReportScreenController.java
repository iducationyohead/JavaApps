package Scheduler.controllers;

import Scheduler.SchedulerMain;
import Scheduler.models.Appointment;
import Scheduler.models.Customer;
import Scheduler.models.DB_Manager;
import Scheduler.models.Report;
import Scheduler.models.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Jeremy H
 */
public class ReportScreenController {
    
    SchedulerMain main;
    private User currentUser;
    Locale locale = Locale.getDefault();
    
    private ObservableList<Report> appointmentList;
    private ObservableList<Appointment> scheduleList;
    private ObservableList<XYChart.Data> chartInfo;
    
    private final ObservableList<String> startingTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endingTimes = FXCollections.observableArrayList();
    
    private final ZoneId newZID = ZoneId.systemDefault();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    
    public ReportScreenController() {}
    
    /**********************User Schedule tab ******************/
    @FXML
    private TableView<Appointment> reportScreenUserTableView;
    
    @FXML
    private TableColumn<Appointment, ZonedDateTime> reportsUserScheduleStartColumn;
    
    @FXML
    private TableColumn<Appointment, LocalDateTime> reportsUserScheduleEndColumn;
    
    @FXML
    private TableColumn<Appointment, Customer> reportsUserScheduleCustomerColumn;
    
    @FXML
    private TableColumn<Appointment, String> reportsUserScheduleTitleColumn;  
    
    @FXML
    private TableColumn<Appointment, String> reportsUserScheduleTypeColumn;
    
    @FXML
    private TableColumn<Appointment, String> reportsUserScheduleCreatedByColumn;
    
    /********************** Location tab ******************/
    
    @FXML
    private BarChart reportScreenBarChart; 
    
    @FXML
    private NumberAxis reportScreenLNyAxis;
    
    @FXML
    private CategoryAxis reportScreenLCxAxis;
    
    /**********************Appointments by type and month tab ******************/
    @FXML
    private TableView<Report> reportScreenTypeMonthView;
     
    @FXML
    private TableColumn<Report, String> reportsTypeMonthColumn;
    
    @FXML
    private TableColumn<Report, String>  reportsTypeAmountColumn;
            
    @FXML
    private TableColumn<Report, String>  reportsAppointmentTypesColumn;
    
    
     /****************** Report Tab Configurations *********************/
    private void createUserSchedule() { 
        PreparedStatement ps;
        scheduleList = FXCollections.observableArrayList();
        String query = "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.type, "
                + "appointment.`start`, appointment.`end`, customer.customerId, customer.customerName, appointment.createdBy "
                + "FROM appointment, customer "
                + "WHERE appointment.customerId = customer.customerId AND appointment.`start` >= CURRENT_DATE AND appointment.createdBy = ?"
                + "ORDER BY `start`";
   
        try {
            ps = DB_Manager.getConnection().prepareStatement(query);
            ps.setString(1, currentUser.getUserName());
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                
                String appointmentId = rs.getString("appointment.appointmentId");
                String title = rs.getString("appointment.title");
                String type = rs.getString("appointment.type");
                String createdBy = rs.getString("appointment.createdBy");
                Customer customerName = new Customer(rs.getString("customerName"));
  
                Timestamp startTS = rs.getTimestamp("appointment.start");
                ZonedDateTime start = startTS.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newStart = start.withZoneSameInstant(newZID);
                Timestamp endTS = rs.getTimestamp("appointment.end"); 
                ZonedDateTime end = endTS.toLocalDateTime().atZone(ZoneId.of("UTC"));          
                ZonedDateTime newEnd = end.withZoneSameInstant(newZID);
                
                scheduleList.add(new Appointment(appointmentId, title, type, createdBy, customerName, newStart.format(timeDTF), newEnd.format(timeDTF)));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ReportScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        reportScreenUserTableView.getItems().setAll(scheduleList);
    }
    
    private void createAppointmentTypeMonthReport() {
        appointmentList = FXCollections.observableArrayList();
        
        String query = "SELECT MONTH(`start`) AS \"month\", type AS \"type\", COUNT(*) as \"amount\" "
                + "FROM appointment "
                + "GROUP BY MONTH(`start`), description";       
        try {
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int intMonth = rs.getInt("month");
                String letterMonth = formatMonth(intMonth, locale);
                String type = rs.getString("type");
                String amount = rs.getString("amount");
                
                appointmentList.add(new Report(letterMonth, type, amount));
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        
        reportScreenTypeMonthView.getItems().setAll(appointmentList);
    }
   
    private void createLocationChart() throws SQLException {
        
        ObservableList<XYChart.Data<String, Integer>> chartInfo = FXCollections.observableArrayList();
        XYChart.Series<String, Integer> seriesChart = new XYChart.Series<>();
        String query = "SELECT city.city, COUNT(city) "
                + "FROM customer, address, city "
                + "WHERE customer.addressId = address.addressId "
                + "AND address.cityId = city.cityId "
                + "GROUP BY city ";
        
        PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            Integer num = rs.getInt("COUNT(city)");
            String city = rs.getString("city");
            chartInfo.add(new Data<>(city, num));
        }
        
        seriesChart.getData().addAll(chartInfo);
        reportScreenBarChart.getData().add(seriesChart);
    }
      
    /********************** Initial Report screen setup ****************/
    public void configureReportScreen(SchedulerMain main, User currentUser) throws SQLException {
        this.main = main;
                // System.out.println("Made it past main" + main); debugging
        this.currentUser = currentUser;
                // System.out.println("Made it past currentUser" + currentUser); debugging
        
        // populate tables
        createLocationChart();
                // System.out.println("Made it past the location chart population"); debugging
        createUserSchedule();
            // System.out.println("Made it past the user schedule population"); debugging
        createAppointmentTypeMonthReport();
            // System.out.println("Made it past the initial appointment report population"); debugging
        
            // System.out.println("Made it past the initial table loading");debugging
        // initialize monthly report table columns
        reportsTypeMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        reportsTypeAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        reportsAppointmentTypesColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            // System.out.println("Made it past the monthly report cell initializations"); debugging
        // initialize user schedule
        reportsUserScheduleStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        reportsUserScheduleEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        reportsUserScheduleCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        reportsUserScheduleTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));  
        reportsUserScheduleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        reportsUserScheduleCreatedByColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
            System.out.println("Made it past the scheule report cell initializations"); //debugging
    }    
    
    /************************ Other Methods **********************/
    // Coverts months from numbers to Names 
     public String formatMonth(int month, Locale locale) {
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] monthNames = symbols.getMonths();
        return monthNames[month - 1];
    }

}
