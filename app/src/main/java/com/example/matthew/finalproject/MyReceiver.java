package com.example.matthew.finalproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyReceiver extends BroadcastReceiver {
    Context context;
    AppLocationService appLocationService;

    @Override
    public void onReceive(Context c, Intent intent) {
        Log.d("BroadcastReceiver", "in onReceive:");
        context = c;
        Event event;
        event = (Event) intent.getSerializableExtra("event");
        boolean isParent = false;
        SharedPreferences prefs = context.getSharedPreferences("SyncItUserData", 0);
        String uname = prefs.getString("username", "");
        String name = prefs.getString("name", "");
        for (FamilyMem familyMem: MainActivity.familyMembers){
            Log.d("IsParent", "Family Member:" + familyMem.getUsername() + ", my Username" + uname);
            if (uname.equals(familyMem.getUsername())){
                Log.d("IsParent", "Family Member: " + familyMem.getParent() + " guests: " + event.getGuestsToString() + ", my name: " +name);
                if (familyMem.getParent()==1 && event.getGuestsToString().contains(name)){
                    isParent = true;
                }
                break;
            }
        }
        if (!isParent) {
            appLocationService = new AppLocationService(context);
            double[] coods = new double[2];
            Location gpsLocation = appLocationService
                    .getLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation != null) {
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                coods[0] = latitude;
                coods[1] = longitude;
                String result = "Latitude: " + gpsLocation.getLatitude() +
                        " Longitude: " + gpsLocation.getLongitude();
                Log.d("BroadcastReceiver", "GPS:" + result);

            }


            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;
                addresses = geocoder.getFromLocationName(event.getLocation(), 1);
                if (addresses.size() > 0) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    double dist = distance(coods[0], coods[1], latitude, longitude);
                    Log.d("BroadcastReceiver", "UserLatitude:" + coods[0] + " UserLongitude:" + coods[1] + " AddressLatitude:" + latitude + " AddressLongitude:" + longitude + " Distance:" + dist);
                    if (dist > 1) {
                        String familyUsername = prefs.getString("familyusername", "");
                        name = prefs.getString("name", "");
                        String[] urls = new String[6];
                        urls[0] = familyUsername;
                        urls[1] = name;
                        urls[2] = event.getName();
                        double distRounded = Math.round(dist * 10) / 10;
                        urls[3] = distRounded + "";
                        urls[4] = coods[0] + "";
                        urls[5] = coods[1] + "";
                        MyNotifyParentsAsyncTask myNotifyParentsAsyncTask = new MyNotifyParentsAsyncTask();
                        myNotifyParentsAsyncTask.execute(urls);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent i = new Intent(context, DetailViewActivity.class);
        intent.putExtra("event", event);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                i, 0);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.tiny_logo)
                .setContentTitle(event.getName())
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Event is starting"))
                .setContentText(event.getDescription())
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(2, mBuilder.build());
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
    private class MyNotifyParentsAsyncTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... urls) {
            String url = MyUtility.SERVER_LINK + "notifyparents/" + urls[0] + "/" + urls[1]
                    + "/" + urls[2] + "/" + urls[3] + "/" + urls[4] + "/"+urls[5];
            url = url.replaceAll(" ", "%20");
            MyUtility.downloadJSON(url);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {

        }
    }
}

