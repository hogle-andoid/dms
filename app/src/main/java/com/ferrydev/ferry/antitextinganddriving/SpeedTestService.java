package com.ferrydev.ferry.antitextinganddriving;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SpeedTestService extends Service {

    static final public String BROADCAST_NAME = "com.ferrydev.antitext.stack.BROADCAST_NAME";

    static final public String BROADCAST_MSG_TAG = "com.ferrydev.antitext.stack.BROADCAST_MSG_TAG";

    private static final String LOG_TAG = "ForegroundService";


    private LocationManager locationManager;
    private LocationListener locationListener;

    private LocalBroadcastManager broadcastManager;

    private SharedPreferences settings;

    public SpeedTestService() {
    }

    public void sendResult(String message) {
        Intent intent = new Intent(BROADCAST_NAME);
        if (message != null)
            intent.putExtra(BROADCAST_MSG_TAG, message);
        broadcastManager.sendBroadcast(intent);
    }

    private double roundDecimal(double value, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(value);

        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        value = bd.doubleValue();

        return value;
    }

    @Override
    public void onCreate() {
        broadcastManager = LocalBroadcastManager.getInstance(this);

        settings = getSharedPreferences(Constants.ACTION.SETTINGS_NAME, MODE_PRIVATE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");


            locationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double speed = location.getSpeed();

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.ACTION.CURRENT_SPEED, String.valueOf(roundDecimal(speed, 2)));

                    editor.commit();

                    if (settings.getBoolean(Constants.ACTION.UNIT_MPH, true)) {
                        sendResult("" + roundDecimal(speed * 2.23694, 2));
                    } else if (settings.getBoolean(Constants.ACTION.UNIT_KMH, false)) {
                        sendResult("" + roundDecimal(speed * 3.6, 2));
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    //Toast.makeText(getApplicationContext(), "Status Changed: " + s, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderEnabled(String s) {
                    Toast.makeText(getApplicationContext(), "GPS Enabled: " + s, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String s) {
                    Toast.makeText(getApplicationContext(), "GPS Disabled: " + s, Toast.LENGTH_SHORT).show();
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_no_text);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("No Text Zone")
                    .setTicker("Don't text while driving")
                    .setContentText("We help you reply.")
                    .setSmallIcon(R.drawable.ic_directions_car_white_24dp).setColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setLargeIcon(icon)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }

        locationManager = null;

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
