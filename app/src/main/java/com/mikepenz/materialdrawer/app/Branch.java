package com.mikepenz.materialdrawer.app;

/**
 * Created by chathuranga on 1/21/2016.
 */
public class Branch {

    int branchCode;
    String branchName;
    double latitude;
    double longitude;
    String address;
    String tel;
    String fax;

    public Branch() {
    }

    public Branch(int branchCode, String branchName, double latitude, double longitude, String address, String tel, String fax) {
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.tel = tel;
        this.fax = fax;
    }

    public int getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(int branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
}
