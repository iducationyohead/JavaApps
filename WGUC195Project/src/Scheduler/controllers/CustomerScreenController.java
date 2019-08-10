package Scheduler.controllers;

import Scheduler.models.DB_Manager;
import Scheduler.SchedulerMain;
import Scheduler.models.City;
import Scheduler.models.Customer;
import Scheduler.models.User;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Jeremy H
 */
public class CustomerScreenController {
   
    SchedulerMain main;
    MainScreenController msController;
    private User currentUser;
    private Stage alertWindow;
    private boolean editEnabled = false; 

    @FXML
    private TableView<Customer> customerListTable;

    @FXML
    private TableColumn<Customer, String> customerScreenIdColumn;

    @FXML
    private TableColumn<Customer, String> customerScreenNameColumn;

    @FXML
    private TableColumn<Customer, String> customerScreenPhoneColumn;

    @FXML
    private TableColumn<Customer, String> customerScreenAddressColumn;

    @FXML
    private TableColumn<Customer, String> customerScreenAddress2Column;

    @FXML
    private TableColumn<Customer, String> customerScreenCityColumn;

    @FXML
    private TableColumn<Customer, String> customerScreenCountryColumn;

    @FXML
    private TableColumn<Customer, String> customerScreenPostalCodeColumn;

    @FXML
    private TextField customerScreenIDField;

    @FXML
    private TextField customerScreenNameField;

    @FXML
    private TextField customerScreenAddressField;

    @FXML
    private TextField customerScreenAddressField2;

    @FXML
    private ComboBox<City> customerScreenCityDropDownBox;

    @FXML
    private TextField customerScreenCountryField;

    @FXML
    private TextField customerScreenPostalCodeField;

    @FXML
    private TextField customerScreenPhoneField;

    @FXML
    private Button customerScreenSaveButton;

    @FXML
    private Button customerScreenDeleteButton;

    // Constructor
    public CustomerScreenController() {
    }
    
    /************************* Button Actions ****************************/
            
    @FXML
    private void customerScreenSaveButtonAction(ActionEvent event) throws SQLException, IOException { 
        customerListTable.isDisabled();
        if(isValidCustomer()) {
            if (editEnabled == true) { // Edit button was clicked so its a current customer record being updated
                System.out.println("Attempting to update customer info");
                updateCustomer(); 
            } else { // Edit button wasn't clicked, so were adding a new customer record
                System.out.println("Attempting to save customer info");
                saveCustomer();
            }       
        }      
        main.loadCustomerScreen(currentUser);
    }  
    
    @FXML
    private void customerScreenNewButtonAction(ActionEvent event) { 
        editEnabled = false; // Edit button wasn't clicked its a new customer
        enableFieldEdits();
    }    

    @FXML
    private void customerScreenEditButtonAction(ActionEvent event) {
        Customer customer = customerListTable.getSelectionModel().getSelectedItem();
        editEnabled = true;
        if(customer != null) {
            //editEnabled = true;
            customerListTable.setDisable(true);
            customerScreenDeleteButton.isDisabled();
            enableFieldEdits(); 
            
        }
        else {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Customer Not Selected");
            alert.setHeaderText("Error no customer was selected");
            alert.setContentText("Please select a customer to edit");
            alert.showAndWait();
        }
    }

    @FXML
    private void customerScreenDeleteButtonAction(ActionEvent event) {
        Customer deleteSelectCustomer = customerListTable.getSelectionModel().getSelectedItem();

        if (deleteSelectCustomer != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("You're about to Delete a customer");
            alert.setHeaderText("Please confirm you would like to delete " + deleteSelectCustomer.getCustomerName() + "?");
            alert.setContentText("Are you sure you want to delete this?");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
         
                            deleteCustomer(deleteSelectCustomer);
                try {
                    main.loadCustomerScreen(currentUser);
                } catch (SQLException ex) {
                    Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
                        
                    });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Deletion Error");
            alert.setHeaderText("There was an error selecting a customer to delete.");
            alert.setContentText("Please select a customer to delete");
            alert.showAndWait();
        }
    }

    @FXML
    private void customerScreenCancelButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Please confirm you wish cancel?");
        alert.showAndWait().filter(response -> response == ButtonType.OK).
                ifPresent((ButtonType response) -> {

                    customerScreenIDField.clear();
                    customerScreenNameField.clear();
                    customerScreenAddressField.clear();
                    customerScreenAddressField2.clear();
                    customerScreenPostalCodeField.clear();
                    customerScreenPhoneField.clear();
                }
                );
    }
       
    /************************ Field edit controls *************************/
    
    private void enableFieldEdits() {   
        customerScreenNameField.setEditable(true);
        customerScreenAddressField.setEditable(true); 
        customerScreenAddressField2.setEditable(true);
        customerScreenCountryField.setEditable(true);        
        customerScreenPostalCodeField.setEditable(true);        
        customerScreenPhoneField.setEditable(true);  
    }
    
    public void disableFieldEdits() {
        customerScreenIDField.setEditable(false);
        customerScreenNameField.setEditable(false);
        customerScreenAddressField.setEditable(false); 
        customerScreenAddressField2.setEditable(false);
        customerScreenCountryField.setEditable(false);        
        customerScreenPostalCodeField.setEditable(false);        
        customerScreenPhoneField.setEditable(false); 
    }
    
    /****************** Customer Data Manipulation controls ********************/
    // Save, Update, Delete, Validate
    /*
    *Saves Data to Customer table. Customer table has appointmentId as a PK, all fields are Not Null
    *    and a FK as address from Address table. AddressId is a primary key but is also Auto Incremented.
    *    Address table has all Fields as Non-Null, PK is addressId and foreign key is cityId to cityid in City Table.
    *    City Table has a FK of countryId in country table.
    *    Data first inserted into address table. Data then inserted into customer Table.
    */
    private void saveCustomer() { 
        try {
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(
                    "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, "
                            + "createdBy, lastUpdate, lastUpdateBy) "
                            + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)",
                            Statement.RETURN_GENERATED_KEYS); // addressId is auto generated
            
            
            ps.setString(1, customerScreenAddressField.getText()); // address  
            ps.setString(2, customerScreenAddressField2.getText()); // address2
            ps.setInt(3, customerScreenCityDropDownBox.getValue().getCityId()); // cityId
            ps.setString(4, customerScreenPostalCodeField.getText()); //postalCode
            ps.setString(5, customerScreenPhoneField.getText()); // phone
            ps.setString(6,currentUser.getUserName()); //createdBy
            ps.setString(7, currentUser.getUserName()); //lastUpdatedBy
            
            boolean hasResult = ps.execute();
            int autoAddressId = -1;
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                autoAddressId = rs.getInt(1);
                // System.out.println("New customer Address Id created: " + autoAddressId); debugging
            }

            PreparedStatement ps2 = DB_Manager.getConnection().prepareStatement("INSERT INTO customer "
                    + "(customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");
            
        
            ps2.setString(1, customerScreenNameField.getText()); //customerName
            ps2.setInt(2, autoAddressId); // addressId 
            ps2.setInt(3, 1); // 1 indicates active, 0 inactive
            ps2.setString(4, currentUser.getUserName()); // createdBy
            ps2.setString(5, currentUser.getUserName()); // lastUpdateBy
            int result = ps2.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /*
    *Updates Data to Customer table. Customer table has appointmentId as a PK, all fields are Not Null
    *    and a FK as address from Address table. AddressId is a primary key but is also Auto Incremented.
    *    Address table has all Fields as Non-Null, PK is addressId and foreign key is cityId to cityid in City Table.
    *    City Table has a FK of countryId in country table.
    *    Data first inserted into address table. Data then inserted into customer Table.
    */
    private void updateCustomer() { 
        String query = "UPDATE customer, address, city, country "
                        + "SET address=?, address2=?, address.cityId=?, postalCode=?, phone=?, address.lastUpdate=CURRENT_TIMESTAMP, address.lastUpdateBy=? "
                        + "WHERE customer.customerId=? AND customer.addressId=address.addressId AND address.cityId=city.cityId AND city.countryId=country.countryId";
        
        String query2 = "UPDATE customer, address, city "
                    + "SET customerName=?, customer.lastUpdate=CURRENT_TIMESTAMP, customer.lastUpdateBy=? "
                    + "WHERE customer.customerId=? AND customer.addressId=address.addressId AND address.cityId=city.cityId";
        try {
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
            
            ps.setString(1, customerScreenAddressField.getText());
            ps.setString(2, customerScreenAddressField2.getText());
            ps.setInt(3, customerScreenCityDropDownBox.getValue().getCityId());
            ps.setString(4, customerScreenPostalCodeField.getText());
            ps.setString(5, customerScreenPhoneField.getText());
            ps.setString(6,currentUser.getUserName());
            ps.setString(7, customerScreenIDField.getText());

            int result = ps.executeUpdate();
            
            PreparedStatement ps2 = DB_Manager.getConnection().prepareStatement(query2);
            
            ps2.setString(1, customerScreenNameField.getText());
            ps2.setString(2, currentUser.getUserName());
            ps2.setString(3, customerScreenIDField.getText());
            int result2 = ps2.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void deleteCustomer(Customer customer) {
            String query = "DELETE customer.*, address.* from customer, address WHERE customer.customerId = ? AND customer.addressId = address.addressId";
        
            try {
                PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
                ps.setString(1, customer.getCustomerId());
                ps.executeUpdate();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Deletion Error");
                alert.setHeaderText("Current customer has an appointment");
                alert.setContentText("Please check customer appointments");
                alert.showAndWait();
            }

    }
     
    
     /********************* Customer Screen set-up controls ********************/
    
    // Initial setup of Customer Screen
    public void configureCustomerScreen(SchedulerMain main, User currentUser) throws SQLException { // sets up customer screen when initially loading
        this.currentUser = currentUser;
        this.main = main;
        
        disableFieldEdits();
        
            // System.out.println("Customer screen user is " + currentUser); debugging
        
        customerScreenIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerScreenNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerScreenPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        customerScreenAddressColumn.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        customerScreenAddress2Column.setCellValueFactory(new PropertyValueFactory<>("customerAddress2"));
        customerScreenCityColumn.setCellValueFactory(new PropertyValueFactory<>("customerCity"));
        customerScreenCountryColumn.setCellValueFactory(new PropertyValueFactory<>("customerCountry"));
        customerScreenPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("customerPostalCode"));
        
            // System.out.println("Setting cell values finished...");debugging
        
        populateCityList(); 
            // System.out.println("populate City List method completed"); debugging
        
        customerScreenCityDropDownBox.setConverter(new StringConverter<City>() { 
            @Override
            public String toString(City object) {
                return object.getCity();
            }

            @Override
            public City fromString(String string) {
                return customerScreenCityDropDownBox.getItems().stream().filter
                    (f -> f.getCity().equals(string)).findFirst().orElse(null);
            }
        });
       
        customerScreenCityDropDownBox.valueProperty().addListener((newV) -> {
            if(newV != null) {    
            populateCountry(newV.toString());
            }    
        });

        customerListTable.getItems().addAll(populateCustomerList());
        
        customerListTable.getSelectionModel().selectedItemProperty().addListener(
                (obsV, oldV, newV) -> populateCustomerFields(newV));
      
    }

    
    // retrieves list from db
    protected List<Customer> populateCustomerList() { // loads customer list onto table
            // System.out.println("Now to populate the customer list..."); debugging
        
        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        String customerID;
        String customerName;
        String customerPhone;
        String customerAddress;
        String customerAddress2;
        City customerCity;
        String customerCountry;
        String customerPostalCode;
        
        // System.out.println("Attempting to execute populate customer list Query"); debugging
        
        String query = "SELECT customer.customerId, customer.customerName, address.address, "
                + "address.address2, address.postalCode, address.phone, city.cityId, city.city,"
                + "country.country " +
                "FROM customer, address, city, country " +
                "WHERE customer.addressId=address.addressId AND address.cityId=city.cityId AND city.countryId = country.countryId " +
                "ORDER BY customer.customerName";
        
        try (
            
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
            
            ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                customerID = rs.getString("customer.customerId");
                customerName = rs.getString("customer.customerName");
                customerAddress = rs.getString("address.address");
                customerAddress2 = rs.getString("address.address2");
                customerPostalCode = rs.getString("address.postalCode");
                customerPhone = rs.getString("address.phone");
                customerCity = new City(rs.getInt("city.cityId"), rs.getString("city.city"));
                customerCountry = rs.getString("country.country");
                
                customerList.add(new Customer(customerID, customerName, customerAddress, customerAddress2, customerPostalCode, customerPhone, customerCity, customerCountry));
            }
             
        } catch (SQLException e) {
            e.printStackTrace();
        }
            // System.out.println("DB Query finished..."); debugging
        return customerList;
    }
    
    @FXML
    private void populateCustomerFields(Customer customer) {

        customerScreenNameField.setText(customer.getCustomerName());
        customerScreenPhoneField.setText(customer.getCustomerPhone());
        customerScreenAddressField.setText(customer.getCustomerAddress());
        customerScreenAddressField2.setText(customer.getCustomerAddress2());
        customerScreenCityDropDownBox.setValue(customer.getCustomerCity());
        customerScreenPostalCodeField.setText(customer.getCustomerPostalCode());
        customerScreenCountryField.setText(customer.getCustomerCountry());
        customerScreenIDField.setText(customer.getCustomerId());
    }

    protected void populateCityList() { // sets up drop down selection of cities from db

        ObservableList<City> cityList = FXCollections.observableArrayList();
        String query = "SELECT cityId, city FROM city LIMIT 200;";
        try (
            PreparedStatement ps = DB_Manager.getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();) {

            while(rs.next()) {
                cityList.add(new City(rs.getInt("city.cityId"), rs.getString("city.city")));
            }
        } catch (SQLException ex) {
                // System.out.println("Theres a problem with the populate city list in Customer Screencontroller"); debugging
            ex.printStackTrace();
        }
        customerScreenCityDropDownBox.setItems(cityList);
            // System.out.println("City List Populate finished..."); debugging
    }

    private void populateCountry(String selectedCity) { // country self populates on city selection
        if (selectedCity.equals("Phoenix") || selectedCity.equals("New York")) {
            customerScreenCountryField.setText("United States");
        } else if (selectedCity.equals("London")) {
            customerScreenCountryField.setText("England");
        }
             // System.out.println("Country List Populate finished..."); debugging
    }

     /************************** Validation checks ***************************/
    private boolean isValidCustomer() {
            System.out.println("Attempting to validate customer fields"); 
        String vName = customerScreenNameField.getText().toLowerCase();
        String vAddress = customerScreenAddressField.getText();
        String vAddress2 = customerScreenAddressField2.getText();
        City vCity = customerScreenCityDropDownBox.getValue();
        String vState = customerScreenCountryField.getText();
        String vPostalCode = customerScreenPostalCodeField.getText();
        String vPhone = customerScreenPhoneField.getText();
        String errorMessage = "";

        if (vName == null || vName.length() == 0) {
            errorMessage += "Please enter a valid Customer Name.\n";
            System.out.println("Attempting to validate customer name"); 
        }
        if (vAddress == null || vAddress.length() == 0) {
            errorMessage += "Please enter a valid Customer Address.\n";
            System.out.println("Attempting to validate address");
        }
        if (vAddress2 == null || vAddress2.length() == 0) {
            errorMessage += "Please enter a valid Address for Address box 2 or NA if not applicable.\n";
            System.out.println("Attempting to validate address2");
        }
        if (vCity == null) {
            errorMessage += "Please enter a valid Customer City.\n";
            System.out.println("Attempting to validate city");
        }
        if (vState == null || vState.length() == 0) {
            errorMessage += "Please enter a valid Customer State.\n";
            System.out.println("Attempting to validate state");
        }
        if (vPostalCode == null || vPostalCode.length() == 0) {
            errorMessage += "Please enter a valid Customer Postal Code.\n";
            System.out.println("Attempting to validate zip code");
        }
        if (vPhone.length() < 7 || vPhone.length() > 15) {
            errorMessage += "Please enter a valid Customer Phone Number.\n";
            System.out.println("Attempting to validate customer phone");
        }
        if (errorMessage.length() == 0) {
            System.out.println("Error message length 0");
            return true; //customer is valid    
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Customer Validation Error");
            alert.setHeaderText("There was an error saving customer");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

}
