package smikhlevskiy.uafinance.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by SMikhlevskiy on 07-Nov-15.
 * class describes organizatation and it exchange rates
 */
public class Organization implements Parcelable {
    private String id;
    private int oldId;
    private int orgType;
    private String title;
    private String regionId;
    private String cityId;
    private String phone;
    private String address;



    private ArrayList<Organization> organizationBrunches = null;


    private String link;
    public Map<String, Currencie> currencies;


    private Double currentCurrencyVal = 0.0;


    private LatLng latLong = null;



    private double distance=Double.MAX_VALUE;

    public Organization() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOldId() {
        return oldId;
    }

    public void setOldId(int oldId) {
        this.oldId = oldId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getOrgType() {
        return orgType;
    }

    public void setOrgType(int orgType) {
        this.orgType = orgType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Map<String, Currencie> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Map<String, Currencie> currencies) {
        this.currencies = currencies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(oldId);
        dest.writeInt(orgType);
        dest.writeString(title);
        dest.writeString(regionId);
        dest.writeString(cityId);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(link);
        dest.writeDouble(currentCurrencyVal);


        if (organizationBrunches != null) {
            dest.writeInt(organizationBrunches.size());
            for (Organization organization : organizationBrunches)
                dest.writeParcelable(organization, flags);
        } else
            dest.writeInt(0);

    }

    protected Organization(Parcel in) {
        id = in.readString();
        oldId = in.readInt();
        orgType = in.readInt();
        title = in.readString();
        regionId = in.readString();
        cityId = in.readString();
        phone = in.readString();
        address = in.readString();
        link = in.readString();
        currentCurrencyVal = in.readDouble();


        int sizeBrunch = in.readInt();
        if (sizeBrunch > 0) {
            organizationBrunches = new ArrayList<Organization>();
            for (int i = 0; i < sizeBrunch; i++)
                organizationBrunches.add((Organization) in.readParcelable(Organization.class.getClassLoader()));
            Log.i("Organization", "Ok Brunch");
        }
    }

    public static final Creator<Organization> CREATOR = new Creator<Organization>() {
        @Override
        public Organization createFromParcel(Parcel in) {
            return new Organization(in);
        }

        @Override
        public Organization[] newArray(int size) {
            return new Organization[size];
        }
    };

    public Double getCurrentCurrencyVal() {
        return currentCurrencyVal;
    }

    public void setCurrentCurrencyVal(Double currentCurrencyVal) {
        this.currentCurrencyVal = currentCurrencyVal;
    }


    public ArrayList<Organization> getOrganizationBrunches() {
        return organizationBrunches;
    }

    public void setOrganizationBrunches(ArrayList<Organization> organizationBrunches) {
        this.organizationBrunches = organizationBrunches;
    }

    public LatLng getLatLong() {
        return latLong;
    }

    public void setLatLong(LatLng latLong) {
        this.latLong = latLong;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}

