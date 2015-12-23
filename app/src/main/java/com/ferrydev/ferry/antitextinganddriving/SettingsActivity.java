package com.ferrydev.ferry.antitextinganddriving;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private SharedPreferences settings;

    private EditText customMessage;
    private EditText customSpeed;

    private RadioButton mphBtn;
    private RadioButton kmBtn;

    private Button saveBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings = getSharedPreferences(Constants.ACTION.SETTINGS_NAME, Context.MODE_PRIVATE);

        customMessage = (EditText) findViewById(R.id.custom_msg);
        customSpeed = (EditText) findViewById(R.id.speed_text);
        mphBtn = (RadioButton) findViewById(R.id.mph_radio);
        kmBtn = (RadioButton) findViewById(R.id.km_radio);
        saveBtn = (Button) findViewById(R.id.save_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        customMessage.setText(settings.getString(Constants.ACTION.CUSTOM_MESSAGE, "Driving, will call you later."));
        customSpeed.setText(settings.getString(Constants.ACTION.CUSTOM_SPEED, "10"));
        mphBtn.setChecked(settings.getBoolean(Constants.ACTION.UNIT_MPH, true));
        kmBtn.setChecked(settings.getBoolean(Constants.ACTION.UNIT_KMH, false));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settings.edit();

                editor.putString(Constants.ACTION.CUSTOM_MESSAGE, customMessage.getText().toString());
                editor.putString(Constants.ACTION.CUSTOM_SPEED, customSpeed.getText().toString());
                editor.putBoolean(Constants.ACTION.UNIT_KMH, kmBtn.isChecked());
                editor.putBoolean(Constants.ACTION.UNIT_MPH, mphBtn.isChecked());

                editor.commit();

                Toast.makeText(getApplicationContext(), "Settings saved !", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
