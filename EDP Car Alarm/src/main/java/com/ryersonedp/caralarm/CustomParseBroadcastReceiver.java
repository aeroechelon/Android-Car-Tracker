package com.ryersonedp.caralarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class CustomParseBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "MyCustomReceiver";
    private static final int NOTIFICATION_ID = 1;
    public static int numMessages = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf(TAG, "onReceiver was invoked.");

        // Grabbing extra data from the intent
        String action = intent.getAction();

        if (action.equalsIgnoreCase(context.getResources().getString(R.string.broadcast_receiver_action_STATUS_ALARM_ON))) {
            String title = context.getResources().getString(R.string.notification_content_STATUS_ALARM_ON);
            String content = context.getResources().getString(R.string.notification_content_GENERIC);

            generateNotification(context, title, content);
        }else if(action.equals(context.getResources().getString(R.string.broadcast_receiver_action_STATUS_ALARM_OFF))){
            String title = context.getResources().getString(R.string.notification_content_STATUS_ALARM_OFF);
            String content = context.getResources().getString(R.string.notification_content_GENERIC);

            generateNotification(context, title, content);
//            }else if(action.equals(context.getResources().getString(R.string.broadcast_receiver_action_STATUS_ALARM_MANUAL_ON))){
//                String title = context.getResources().getString(R.string.notification_content_STATUS_ALARM_MANUAL_ON);
//                String content = context.getResources().getString(R.string.notification_title_GENERIC);
//
//                generateNotification(context, title, content);
//            }else if(action.equals(context.getResources().getString(R.string.broadcast_receiver_action_STATUS_ALARM_MANUAL_OFF))) {
//                String title = context.getResources().getString(R.string.notification_content_STATUS_ALARM_MANUAL_OFF);
//                String content = context.getResources().getString(R.string.notification_title_GENERIC);
//                generateNotification(context, title, content);
        }
    }

    private void generateNotification(Context context, String title, String content) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setSound(notificationSound)
                        .setNumber(++numMessages)
                        .setTicker("Car Alarm Status: " + title);

        mBuilder.setContentIntent(contentIntent);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}