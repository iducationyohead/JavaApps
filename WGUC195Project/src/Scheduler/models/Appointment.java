package Scheduler.models;

/**
 *
 * @author Jeremy H
 */
public class Appointment {
    
    private String user;

    private String appointmentDate;
    private String appointmentId;
    private String start;
    private String end;
    private String title;
    private String type;
    private Customer customer;
    private Customer customerName;
    private Customer customerId;
    private String createdBy;
      
    // Constructor
    public Appointment() {}
    
    public Appointment(String appointmentId) {
        this.appointmentId = appointmentId;
      
    }

    public Appointment(String user, String start, String end) {
        this.user = user;
        this.start = start;
        this.end = end;
    }

     public Appointment(String appointmentId, String appointmentTitle, String appointmentType, String start, String end, Customer customer, Customer customerName, Customer customerId, String user) {
        this.appointmentId = appointmentId;
        this.title = appointmentTitle;
        this.type = appointmentType;
        this.start = start;
        this.end = end;
        this.customer = customer;
        this.customerName = customerName;
        this.customerId = customerId;
        this.user = user;
    }

    public Appointment(String appointmentId, String title, String type, String createdBy, Customer customerName, String start, String end) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.type = type;
        this.createdBy = createdBy;
        this.customerName = customerName;
        this.start = start;
        this.end = end;
    }

    /****************************** Getters ***********************************/
    public String getUser() {
        return user;
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }
   
    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Customer getCustomerName() {
        return customerName;
    }

    public Customer getCustomerId() {
        return customerId;
    }
    
    public String getType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
    
    /****************************** Setters ***********************************/
    public void setUser(String user) {
        this.user = user;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
     public void setAppointmentDate(String date) {
        this.appointmentDate = appointmentDate;
    }
   
    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setCustomerName(Customer customerName) {
        this.customerName = customerName;
    }
    
    public void setCustomerId(Customer customerId) {
        this.customerId = customerId;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
       
}
