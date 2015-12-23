package com.ferrydev.ferry.antitextinganddriving;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SMSReceiver extends BroadcastReceiver {

    private SharedPreferences settings;

    String msg_from;

    public static final String ACTION_SMS_SENT = "com.techblogon.android.apis.os.SMS_SENT_ACTION";

    public SMSReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        Log.d("resulty", "Message From: " + msg_from + " and it says: " + msgBody);
                    }
                } catch (Exception e) {
                    Log.d("Exception caught", e.getMessage());
                }
            }
        }

        settings = context.getSharedPreferences(Constants.ACTION.SETTINGS_NAME, Context.MODE_PRIVATE);

        double currentSpeed = Double.parseDouble(settings.getString(Constants.ACTION.CURRENT_SPEED, "0"));

        double targetSpeed = Double.parseDouble(settings.getString(Constants.ACTION.CUSTOM_SPEED, "10"));
        boolean unitMPH = settings.getBoolean(Constants.ACTION.UNIT_MPH, true);
        boolean unitKMH = settings.getBoolean(Constants.ACTION.UNIT_KMH, false);

        String customMessage = settings.getString(Constants.ACTION.CUSTOM_MESSAGE, "Driving, will call you later.");

        if (settings.getBoolean(Constants.ACTION.APP_SWITCH, false)) {
            if (unitMPH){
                if (roundDecimal((currentSpeed * 2.23694), 2) >= targetSpeed) {

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(msg_from, null, customMessage,
                            PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0), null);

                    Toast.makeText(context, "SMS will be sent", Toast.LENGTH_LONG).show();
                }
            } else if (unitKMH){
                if (roundDecimal((currentSpeed * 3.6), 2) >= targetSpeed) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(msg_from, null, customMessage,
                            PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0), null);

                    Toast.makeText(context, "SMS will be sent ", Toast.LENGTH_LONG).show();
                }
            }
        }

        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String response = null;
                switch (getResultCode()) {
                    case Activity.RESULT_OK: response = "SMS was sent successfully"; break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE: response = "Failed to send SMS."; break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE: response = "Failed to send SMS."; break;
                    case SmsManager.RESULT_ERROR_NULL_PDU: response = "Failed to send SMS."; break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF: response = "Failed to send SMS."; break;
                }

                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter(ACTION_SMS_SENT));
    }

    private double roundDecimal(double value, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(value);

        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        value = bd.doubleValue();

        return value;
    }
}
