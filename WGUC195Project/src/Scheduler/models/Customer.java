package Scheduler.models;

/**
 *
 * @author Jeremy H
 */
public class Customer {
    
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerAddress2;
    private City customerCity;
    private String customerCountry;
    private String customerPostalCode;
    private String appointmentId;
    

    /******************* Constructors *************************/
    public Customer(){}
    
    public Customer(String customerName) {
        this.customerName = customerName;
    }
    
     public Customer(String customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }
   
    public Customer(String customerId, String customerName, String appointmentId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.appointmentId = appointmentId;
    }
    
    public Customer(String  customerId, String customerName, String customerAddress, String customerAddress2, String customerPostalCode, String customerPhone, City customerCity,
            String customerCountry) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.customerAddress2 = customerAddress2;
        this.customerCity = customerCity;
        this.customerCountry = customerCountry;
        this.customerPostalCode = customerPostalCode;
    }

    /********************** Getters *************************/
    
    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }
    
    public String getCustomerAddress2() {
        return customerAddress2;
    }

    public City getCustomerCity() {
        return customerCity;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public String getCustomerPostalCode() {
        return customerPostalCode;
    }
    
    public String getAppointmentId() {
        return appointmentId;
    }

    /********************** Setters *************************/
     public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setCustomerAddress2(String customerAddress2) {
        this.customerAddress2 = customerAddress2;
    }
    
    public void setcustomerCity(City city) {
        this.customerCity = city;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    public void setCustomerPostalCode(String customerPostalCode) {
        this.customerPostalCode = customerPostalCode;
        
    }
    
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    @Override
    public String toString() {
        return customerName;
    }
    
}
