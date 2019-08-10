package Scheduler.models;

import java.util.Objects;

/**
 *
 * @author Jeremy H
 */
public class Country {
    
    public Integer countryId;
    public String country;

    
    // Constructors
    public Country () {}
    
    public Country(Integer countryId) {
        this.countryId = countryId;
    }

    public Country(String country) {
        this.country = country;
    }
    
    public Country(Integer countryId, String country) {
        this.countryId = countryId;
        this.country = country;
    }

    /************ Getters ****************/
    public Integer getCountryId() {
        return countryId;
    }

    public String getCountry() {
        return country;
    }

    /************* Setters ***************/
    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    @Override
    public String toString() {
        return "Country{" + "countryId=" + countryId + ", country=" + country + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.countryId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Country other = (Country) obj;
        if (!Objects.equals(this.country, other.country)) {
            return false;
        }
        if (!Objects.equals(this.countryId, other.countryId)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
    
    
    
}
