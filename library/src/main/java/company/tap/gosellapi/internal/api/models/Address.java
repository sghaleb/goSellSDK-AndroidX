package company.tap.gosellapi.internal.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import company.tap.gosellapi.internal.api.enums.AddressFormat;
import company.tap.gosellapi.internal.api.enums.AddressType;

/**
 * The type Address.
 */
public final class Address implements Serializable {

    @SerializedName("format")
    @Expose
    private AddressFormat format;

    @SerializedName("type")
    @Expose
    private AddressType type;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("line1")
    @Expose
    private String line1;

    @SerializedName("line2")
    @Expose
    private String line2;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("zip_code")
    @Expose
    private String zipCode;

    @SerializedName("country_governorate")
    @Expose
    private String countryGovernorate;

    @SerializedName("area")
    @Expose
    private String area;

    @SerializedName("block")
    @Expose
    private String block;

    @SerializedName("avenue")
    @Expose
    private String avenue;

    @SerializedName("street")
    @Expose
    private String street;

    @SerializedName("building_house")
    @Expose
    private String buildingHouse;

    @SerializedName("floor")
    @Expose
    private String floor;

    @SerializedName("office")
    @Expose
    private String office;

    @SerializedName("po_box")
    @Expose
    private String poBox;

    @SerializedName("postal_code")
    @Expose
    private String postalCode;

    public AddressFormat getFormat() {
        return this.format;
    }

    public AddressType getType() {
        return this.type;
    }

    public String getCountry() {
        return this.country;
    }

    public String getLine1() {
        return this.line1;
    }

    public String getLine2() {
        return this.line2;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public String getCountryGovernorate() {
        return this.countryGovernorate;
    }

    public String getArea() {
        return this.area;
    }

    public String getBlock() {
        return this.block;
    }

    public String getAvenue() {
        return this.avenue;
    }

    public String getStreet() {
        return this.street;
    }

    public String getBuildingHouse() {
        return this.buildingHouse;
    }

    public String getFloor() {
        return this.floor;
    }

    public String getOffice() {
        return this.office;
    }

    public String getPoBox() {
        return this.poBox;
    }

    public String getPostalCode() {
        return this.postalCode;
    }
}
