package com.example.matthew.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class MyUtility {
    final static String SERVER_LINK = "http://mattodonnell.com/";

    // Download an image from online
    public static Bitmap downloadImage(String url) {
        Bitmap bitmap = null;

        InputStream stream = getHttpConnection(url);
        if (stream != null) {
            bitmap = BitmapFactory.decodeStream(stream);
            try {
                stream.close();
            } catch (IOException e1) {
                Log.d("MyDebugMsg", "IOException in downloadImage()");
                e1.printStackTrace();
            }
        }

        return bitmap;
    }

    // Download a Json file from online
    public static String downloadJSON(String url) {
        String json = null, line;

        InputStream stream = getHttpConnection(url);
        if (stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder out = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
                json = out.toString();
            } catch (IOException ex) {
                Log.d("MyDebugMsg", "IOException in downloadJSON()");
                ex.printStackTrace();
            }
        }
        return json;
    }


    // Makes HttpURLConnection and returns InputStream
    public static InputStream getHttpConnection(String urlString) {
        InputStream stream = null;
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (UnknownHostException e1) {
            Log.d("MyDebugMsg", "UnknownHostexception in getHttpConnection()");
            e1.printStackTrace();
        } catch (Exception ex) {
            Log.d("MyDebugMsg", "Exception in getHttpConnection()");
            ex.printStackTrace();
        }
        return stream;
    }

    public static class MyGetFamilyMembersAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            MainActivity.familyMembers.clear();
            String familyEventsJson = MyUtility.downloadJSON(MyUtility.SERVER_LINK + "getfamilymembers/" + urls[0]);
            Log.d("familyMembersJson", familyEventsJson);

            try {
                JSONArray userJsonArray = new JSONArray(familyEventsJson);
                for (int i = 0; i < userJsonArray.length(); i++) {
                    JSONObject userObject = userJsonArray.getJSONObject(i);
                    if (userObject != null) {
                        String familyMemberUsername = userObject.getString("username");
                        String familyMemberName = userObject.getString("name");
                        int isParent = userObject.getInt("parent");
                        FamilyMem familyMem = new FamilyMem(familyMemberUsername, familyMemberName, isParent);
                        MainActivity.familyMembers.add(familyMem);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {


        }
    }


}
