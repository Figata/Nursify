package com.example.nursify.notification;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.nursify.view.MainActivity;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class MyHandler extends NotificationsHandler {

    @Override
    public void onRegistered(Context context, final String gcmRegistrationId) {
        super.onRegistered(context, gcmRegistrationId);

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void... params) {
                try {

                    MainActivity.client.getPush().register(gcmRegistrationId);
                    return null;
                } catch (Exception e) {
                    // handle error
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        String message = bundle.getString("message");
        double latitude = Double.parseDouble(bundle.getString("latitude"));
        double longitude = Double.parseDouble(bundle.getString("longitude"));
        String time = bundle.getString("time");

        Location receivedLocation = new Location("");
        receivedLocation.setLatitude(latitude);
        receivedLocation.setLongitude(longitude);

        LocationCalculator locationCalculator = new LocationCalculator();

        if (locationCalculator.isInDistanceToTriggerNotification(MainActivity.location, receivedLocation)) {
            new IncidentNotification(context, message, latitude, longitude, time).showNotification();
        }
    }
}
