package com.example.matthew.finalproject;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static final String MESSAGE_KEY = "message";
    static String incomingMessage;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCMNotificationIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

                for (int i = 0; i < 3; i++) {
                    Log.i(TAG,
                            "Working... " + (i + 1) + "/3 @ "
                                    + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }

                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                incomingMessage = (String) extras.get(MESSAGE_KEY);
                sendNotification("Message Received from Google GCM Server: "
                        + extras.get(MESSAGE_KEY));
                Log.i(TAG, "Received: " + extras.toString());

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (!msg.contains("|")) {
            String eventMessage = msg;
            for (int i = 0; i < msg.length(); i++) {
                char c = msg.charAt(i);
                if (c != '[') {
                    eventMessage = msg.substring(i + 1, msg.length());
                } else {
                    break;
                }
                Log.d("GCMNotificationIntentService.sendNotification", "updated Message: " + eventMessage);
                //Process char
            }
            String eventName = "", eventDescription = "", eventStart = "", eventEnd = "", eventLocation = "";
            String[] eventGuests;
            Event event = new Event();
            SharedPreferences prefs = getSharedPreferences("SyncItUserData", 0);
            String username = prefs.getString("username", "");
            String createdby = "";
            try {
                JSONArray eventJsonArray = new JSONArray(eventMessage);
                for (int i = eventJsonArray.length() - 1; i >= 0; i--) {
                    JSONObject userObject = eventJsonArray.getJSONObject(i);
                    if (userObject != null) {
                        eventName = userObject.getString("name");
                        eventDescription = userObject.getString("description");
                        eventStart = userObject.getString("start");
                        eventEnd = userObject.getString("end");
                        eventLocation = userObject.getString("location");
                        String guests = userObject.getString("guests");
                        createdby = userObject.getString("createdby");
                        eventGuests = guests.split(",");
                        Log.d("GCMNotificationIntentService.sendNotification", "Received Event: " + eventName + ":" + eventStart);
                        event = new Event(eventName, eventDescription, eventStart, eventEnd, eventLocation, eventGuests);

                        if (!username.equals(createdby)) {
                            PendingIntent pendingIntent;
                            Intent myIntent = new Intent(this, MyReceiver.class);
                            myIntent.putExtra("event", event);
                            pendingIntent = PendingIntent.getBroadcast(this, event.getId(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                            try {
                                cal.setTime(sdf.parse(event.getStartDate()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.d("GCMNotificationIntentService Cal", "cal = " + cal.getTime().toString());
                            alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
                        }
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("GCMNotificationIntentService.sendNotification", "well that didnt work");
            }
            if (!username.equals(createdby)) {
                String notificationMsg = eventDescription;
                if (eventDescription.length() > 40) {
                    notificationMsg = eventDescription.substring(0, 40) + "...";
                }

                Intent intent = new Intent(this, DetailViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("event", event);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this).setSmallIcon(R.drawable.tiny_logo)
                        .setContentTitle("Event Added: " + eventName)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMsg))
                        .setContentText(notificationMsg)
                        .setAutoCancel(true);

                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                Log.d(TAG, "Notification sent successfully.");
            }else{
                Log.d(TAG, "This is the event creator, do not send update");
            }
        }else{
            String coordinates = "";
            String msgOnly = "";
            for (int i = 0; i < incomingMessage.length(); i++) {
                char c = incomingMessage.charAt(i);
                if (c != '|') {
                    coordinates = incomingMessage.substring(i + 2, incomingMessage.length());
                    msgOnly = incomingMessage.substring(0,i+1);

                } else {
                    break;
                }
            }
            Log.d("GCMNotificationIntentService", "coordinates: " + coordinates);
            Log.d("GCMNotificationIntentService", "message: " + msgOnly);

            String[] strCords;

            strCords = coordinates.trim().split(",");
            Log.d("GCMNotificationIntentService", "coordinates: " + strCords[0] + "---" + strCords[1]);
            Intent intent = new Intent(this, LocationActivity.class);
            intent.putExtra("cords", strCords);
            intent.putExtra("msg",msgOnly);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this).setSmallIcon(R.drawable.tiny_logo)
                    .setContentTitle("Child Alert!")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msgOnly))
                    .setContentText(msgOnly)
                    .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(0, mBuilder.build());
            Log.d(TAG, "Notification alert sent successfully.");

        }
    }
}
