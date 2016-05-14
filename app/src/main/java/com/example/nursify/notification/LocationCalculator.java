package com.example.nursify.notification;

import android.location.Location;

public class LocationCalculator {
    public static final float MAX_HELPER_DISTANCE = 2000;

    public boolean isInDistanceToTriggerNotification(Location currentLocation, Location locationFromServer) {
        //distance is in meters

        float distance = currentLocation.distanceTo(locationFromServer);

        if (distance < MAX_HELPER_DISTANCE && distance != 0.0f) {
            return true;
        }
        return false;
    }
}