package com.example.matthew.finalproject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;


import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class Fragment_Location extends DialogFragment {
    Date mDate;
    final static public String LAT_ARGS = "lat";
    final static public String LON_ARGS = "lon";
    final static public String EVENT_ARGS = "event";


    Context context;

    public Fragment_Location() {
    }

    public static Fragment_Location newInstance(String[] cords, String msg) {
        Fragment_Location fragment = new Fragment_Location();
        Bundle args = new Bundle();
        args.putStringArray(LAT_ARGS, cords);
        args.putString(EVENT_ARGS, msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_location, null);
        final String[] cords = getArguments().getStringArray(LAT_ARGS);
        String msg = getArguments().getString(EVENT_ARGS);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(v)
                .setTitle("Locate Your Child")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("cords", Double.valueOf(cords[0].substring(0,cords[0].length()))+":"+Double.valueOf(cords[1]));
                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f", Double.valueOf(cords[0]), Double.valueOf(cords[1]), Double.valueOf(cords[0]), Double.valueOf(cords[1]));
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }
                        }).setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return alertDialogBuilder.create();
    }

}