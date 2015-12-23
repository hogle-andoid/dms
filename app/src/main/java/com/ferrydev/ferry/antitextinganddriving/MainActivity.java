package com.ferrydev.ferry.antitextinganddriving;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView speedView;
    private TextView speedUnit;

    private SwitchCompat switchService;

    private BroadcastReceiver broadcastReceiver;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        settings = getSharedPreferences(Constants.ACTION.SETTINGS_NAME, MODE_PRIVATE);

        speedView = (TextView) findViewById(R.id.speed_view);
        speedUnit = (TextView) findViewById(R.id.unit_text_view);
        switchService = (SwitchCompat) findViewById(R.id.switch_service);

        if (settings.getBoolean(Constants.ACTION.UNIT_MPH, false)) {
            speedUnit.setText("Mph");
        } else if (settings.getBoolean(Constants.ACTION.UNIT_KMH, true)) {
            speedUnit.setText("Km/h");
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String speed = intent.getStringExtra(SpeedTestService.BROADCAST_MSG_TAG);
                speedView.setText(speed);
            }
        };

        switchService.setChecked(settings.getBoolean(Constants.ACTION.APP_SWITCH, false));
        switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.ACTION.APP_SWITCH, checked);
                    editor.commit();

                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    }

                    Intent intent = new Intent(MainActivity.this, SpeedTestService.class);
                    intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(intent);
                } else {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.ACTION.APP_SWITCH, checked);
                    editor.commit();

                    Intent stopIntent = new Intent(MainActivity.this, SpeedTestService.class);
                    stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    stopService(new Intent(MainActivity.this, SpeedTestService.class));
                }
            }
        });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(SpeedTestService.BROADCAST_NAME));

        if (settings.getBoolean(Constants.ACTION.UNIT_MPH, true)) {
            speedUnit.setText("Mph");
        } else if (settings.getBoolean(Constants.ACTION.UNIT_KMH, false)) {
            speedUnit.setText("Km/h");
        }

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (settings.getBoolean(Constants.ACTION.APP_SWITCH, false) && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
