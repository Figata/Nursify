package com.example.nursify.model;

/**
 * Created by vassilis on 5/11/16.
 * <p/>
 * Represents an item in Helper table
 */
public class Helper {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("id")
    private String deviceId;
    @com.google.gson.annotations.SerializedName("id")
    private String lastName;
    @com.google.gson.annotations.SerializedName("id")
    private String firstName;
    @com.google.gson.annotations.SerializedName("id")
    private String email;
    @com.google.gson.annotations.SerializedName("id")
    private String qualifications;

    public Helper() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Helper && ((Helper) o).id.equals(id);
    }
}
