package com.example.nursify.model;

/**
 * Created by vassilis on 5/14/16.
 */
public class Incident {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("latitude")
    private double latitude;
    @com.google.gson.annotations.SerializedName("longitude")
    private double longitude;
    @com.google.gson.annotations.SerializedName("incidentTime")
    private String incidentTime;

    public Incident() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIncidentTime() {
        return incidentTime;
    }

    public void setIncidentTime(String incidentTime) {
        this.incidentTime = incidentTime;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Incident && ((Incident) o).id.equals(id);
    }
}
