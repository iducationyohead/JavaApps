package Scheduler.models;

/**
 *
 * @author Jeremy H
 */
public class City {
    
    private int cityId;
    private int countryId;
    
    private String city;
    
    //Constructors
    public City() {}
    
    public City(int cityId) {
        this.cityId = cityId;
    }
    
    public City(int cityId, String city) {
        this.cityId = cityId;
        this.city = city;
    }
    
    public City(int cityId, int countryId, String city) {
        this.cityId = cityId;
        this.countryId = countryId;
        this.city = city;
    }

    /******************** Getters  **********************/
    public int getCityId() {
        return this.cityId;
    }

    public int getCountryId() {
        return this.countryId;
    }
    
     public String getCity() {
        return this.city;
    }

    /******************** Setters  **********************/
    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    /////////////////////////////////////////////////////
    @Override
    public String toString() {
        return city;
    }
    
    
}
