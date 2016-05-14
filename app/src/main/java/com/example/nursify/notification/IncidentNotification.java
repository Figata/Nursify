package com.example.nursify.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.nursify.R;

import java.util.Locale;

/**
 * Created by vassilis on 5/8/16.
 */
public class IncidentNotification {
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private Context context;
    private String message;
    private final String markerLabel;

    int NOTIFICATION_ID = 1;

    private Double latitude;
    private Double longitude;
    private String time;

    public IncidentNotification(Context context, String message, double latitude, double longitude, String time) {
        this.message = message;
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;

        if (message.equals("help"))
            markerLabel = context.getString(R.string.help_message);

        else
            markerLabel = context.getString(R.string.help_canceled);
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void showNotification() {

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(markerLabel)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.time_asked) + " " + time))
                .setContentText(context.getString(R.string.time_asked) + " " + time)
                .setSmallIcon(R.drawable.ic_launcher);

        Intent intent;
        if (message.equals("help")) {
            String uri = String.format(Locale.ENGLISH, "geo:?q=(%s)@%f,%f", context.getString(R.string.patient_location), latitude, longitude);

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        } else {
            mBuilder.setAutoCancel(true);
            intent = new Intent();
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}