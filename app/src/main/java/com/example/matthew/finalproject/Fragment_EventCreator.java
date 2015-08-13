package com.example.matthew.finalproject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

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

public class Fragment_EventCreator extends DialogFragment {
    Date mDate;
    final static public String DATE_ARGS = "date";

    static public EditText eventName, startDate, endDate, description;
    static AutoCompleteTextView location;
    static MultiAutoCompleteTextView guests;

    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    //------------ make your specific key ------------
    private static final String API_KEY = "AIzaSyAMYQLfBt5Ttm7WVeLvwuG2ZcoeFrwd5pU";

    public Handler handler;// for grabbing some event values for showing the dot
    Context context;

    public Fragment_EventCreator() {
    }

    public static Fragment_EventCreator newInstance(Date date) {
        Fragment_EventCreator fragment = new Fragment_EventCreator();
        Bundle args = new Bundle();
        args.putSerializable(DATE_ARGS, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        context = getActivity();
        mDate = (Date) getArguments().getSerializable(DATE_ARGS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View v = getActivity().getLayoutInflater().inflate(R.layout.event_creator, null);
        eventName = (EditText) v.findViewById(R.id.eventEdit);
        description = (EditText) v.findViewById(R.id.eventCreatorDescription);

        guests = (MultiAutoCompleteTextView) v.findViewById(R.id.guests);
        final String[] guestList = new String[MainActivity.familyMembers.size()];
        int count = 0;
        for (FamilyMem familyMem : MainActivity.familyMembers) {
            guestList[count] = familyMem.getName();
            count++;
        }
        final ArrayAdapter guestAdapter = new ArrayAdapter
                (getActivity(), android.R.layout.simple_list_item_1, guestList);
        guests.setAdapter(guestAdapter);
        guests.setThreshold(1);
        guests.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        guests.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {

            }
        });

        location = (AutoCompleteTextView) v.findViewById(R.id.locationEdit);

        location.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity().getApplicationContext(), R.layout.list_item));
        location.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Toast.makeText(getActivity().getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            }
        });


        Button okay = (Button) v.findViewById(R.id.okayButton);
        Button cancel = (Button) v.findViewById(R.id.cancelButton);
        startDate = (EditText) v.findViewById(R.id.startDateBtn);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_DateDialog dialog = null;
                if (startDate.getText().length() > 0) {
                    try {
                        dialog = Fragment_DateDialog.newInstance(new SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(startDate.getText().toString()), startDate.getHint().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    dialog = Fragment_DateDialog.newInstance(mDate, startDate.getHint().toString());
                }
                dialog.show(getFragmentManager(), "DatePicker Dialog: Get Result");
            }
        });
        endDate = (EditText) v.findViewById(R.id.endDateBtn);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date eDate = new Date();
                if (startDate.getText().length() > 0) {
                    try {
                        eDate = new SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(startDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    eDate = mDate;
                }
                Fragment_DateDialog dialog = Fragment_DateDialog.newInstance(eDate, endDate.getHint().toString());
                dialog.show(getFragmentManager(), "DatePicker Dialog: Get Result");
            }
        });
        okay.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(v)
                .setTitle("Create Event")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences prefs = getActivity().getSharedPreferences("SyncItUserData", 0);
                                String username = prefs.getString("username", "");
                                String familyusername = prefs.getString("familyusername", "");
                                String[] urls = new String[8];
                                urls[0] = eventName.getText().toString();
                                urls[1] = description.getText().toString();
                                urls[2] = startDate.getText().toString();
                                urls[3] = endDate.getText().toString();
                                urls[4] = location.getText().toString();
                                urls[5] = guests.getText().toString();
                                urls[6] = username;
                                urls[7] = familyusername;
                                Event event = new Event(urls[0], urls[1], urls[2], urls[3], urls[4], urls[5].split(","));
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                                try {
                                    cal.setTime(sdf.parse(startDate.getText().toString()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                MyEventCreatorAsyncTask task = new MyEventCreatorAsyncTask();
                                task.execute(urls);
                                PendingIntent pendingIntent;
                                Intent myIntent = new Intent(getActivity(), MyReceiver.class);
                                myIntent.putExtra("event", event);
                                pendingIntent = PendingIntent.getBroadcast(getActivity(), event.getId(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                Log.d("EventCreator Cal", "cal = " + cal.getTime().toString());
                                alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        return alertDialogBuilder.create();
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:us");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private class MyEventCreatorAsyncTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... urls) {
            String url = MyUtility.SERVER_LINK + "createevent/" + urls[0] + "/" + urls[1]
                    + "/" + urls[2] + "/" + urls[3] + "/" + urls[4] + "/" + urls[5] + "/" + urls[6] + "/" + urls[7];
            url = url.replaceAll(" ", "%20");

            MyUtility.downloadJSON(url);
            Log.d("MyEventCreatorAsyncTask.url", MyUtility.SERVER_LINK + "createevent/" + urls[0] + "/" + urls[1]
                    + "/" + urls[2] + "/" + urls[3] + "/" + urls[4] + "/" + urls[5] + "/" + urls[6] + "/" + urls[7]);
            Log.d("MyEventCreatorAsyncTask.urlnew", url);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {

            //MainActivity.MyGetEventsAsyncTask task = new MainActivity.MyGetEventsAsyncTask(Fragment_RecyclerView.myRecyclerViewAdapter, Fragment_CalendarView.adapter);
            //task.execute(MainActivity.familyUsername);
            handler.post(calendarUpdater);
        }
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            MainActivity.eventList = Utility.readCalendarEvent(context);
            SharedPreferences prefs = context.getSharedPreferences("SyncItUserData", 0);
            String familyUsername = prefs.getString("familyusername", "");
            MainActivity.MyGetEventsAsyncTask task = new MainActivity.MyGetEventsAsyncTask(Fragment_RecyclerView.myRecyclerViewAdapter, Fragment_CalendarView.adapter);
            task.execute(familyUsername);
            Log.d("eventListBefore", MainActivity.eventList.toString());
            MainActivity.MyQuickSort sorter = new MainActivity.MyQuickSort();
            sorter.sort(MainActivity.eventList);
        }
    };

}