package Scheduler.models;

import java.util.Objects;

/**
 *
 * @author Jeremy H
 */
public class Address {
    
    private int cityId;
    private Integer addressId;
    
    private String address;
    private String address2;
    private String phone;
    private String postalCode;
    
    
    //Constructors
    public Address(){}

    public Address(Integer addressId) {
        this.addressId = addressId;
    }

    public Address(int cityId, Integer addressId, String address, String address2, String phone, String postalCode) {
        this.cityId = cityId;
        this.addressId = addressId;
        this.address = address;
        this.address2 = address2;
        this.phone = phone;
        this.postalCode = postalCode;
    }

    /******************** Getters **********************/
    public int getCityId() {
        return cityId;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }

    public String getPhone() {
        return phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    /******************** Setters **********************/
    
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final Address other = (Address) obj;
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        return Objects.equals(this.addressId, other.addressId);
    }

    
    
    
    
    
    
    
}
