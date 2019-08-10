package Scheduler.models;

/**
 *
 * @author Jeremy H
 */
public class Report {
    
    private String month;
    private String type;
    private String amount;
 
    public Report() {}

    public Report(String month, String type, String amount) {
        this.month = month;
        this.type = type;
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
