package com.example.nursify.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.nursify.model.DeletedIncident;
import com.example.nursify.notification.MyHandler;
import com.example.nursify.R;
import com.example.nursify.model.Incident;
import com.example.nursify.utils.ProgressFilter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Mobile Service Client reference
    public static MobileServiceClient client;

    // Mobile Service Table used to access data
    private MobileServiceTable<Incident> incidentTable;
    private MobileServiceTable<DeletedIncident> deletedIncidentTable;

    private GoogleApiClient googleApiClient;

    public static Location location;

    private static final String TAG = MainActivity.class.getName();

    private Incident item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            client = new MobileServiceClient(
                    "https://nursify.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            client.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            NotificationsManager.handleNotifications(this, getString(R.string.sender_id), MyHandler.class);

            // Get the Mobile Service Table instance to use
            incidentTable = client.getTable(Incident.class);
            deletedIncidentTable = client.getTable(DeletedIncident.class);

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }

        //Init googleApiClient
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_call_ambulance) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + getString(R.string.ambulance_number)));
            startActivity(callIntent);
        }

        return true;
    }

    public void addItem(View view) {
        if (client == null) {
            return;
        }

        // Create a new item
        item = new Incident();

        item.setIncidentTime(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()));

        item.setLatitude(location.getLatitude());
        item.setLongitude(location.getLongitude());

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    item = addItemInTable(item);
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);
    }

    public void removeItem(View view) {
        if (client == null || item == null) {
            return;
        }
        showCancelDialog();
    }

    private void cancelIncident() {
        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    removeItemInTable(item);
                } catch (final Exception e) {
                    Log.e("error", e.getMessage());
                    e.printStackTrace();
                    //createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);
    }

    private void showCancelDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getSupportActionBar().getThemedContext());

        adb.setTitle(getString(R.string.cancel_message));

        adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cancelIncident();
            }
        });

        adb.setNegativeButton(android.R.string.no, null);

        Dialog d = adb.show();
    }

    public void register(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item The item to Add
     */
    public Incident addItemInTable(Incident item) throws ExecutionException, InterruptedException {
        return incidentTable.insert(item).get();
    }

    public void removeItemInTable(Incident item) throws ExecutionException, InterruptedException {
        DeletedIncident deletedIncident = new DeletedIncident();
        deletedIncident.setId(item.getId());
        deletedIncident.setIncidentTime(item.getIncidentTime());
        deletedIncident.setLatitude(item.getLatitude());
        deletedIncident.setLongitude(item.getLongitude());

        deletedIncidentTable.insert(deletedIncident).get();
        incidentTable.delete(item).get();
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception The exception to show in the dialog
     * @param title     The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        createAndShowDialog(exception, "Error");
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception The exception to show in the dialog
     * @param title     The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if (exception.getCause() != null) {
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message The dialog message
     * @param title   The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     *
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            Log.i(TAG, "onConnected: Longitude is " + location.getLongitude() + " Latitude is: " + location.getLatitude());
        } else {
            startLocationUpdates();
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: Longitude is " + location.getLongitude() + " Latitude is: " + location.getLatitude());

    }
}
