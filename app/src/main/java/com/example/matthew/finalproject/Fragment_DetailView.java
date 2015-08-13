package com.example.matthew.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by Matthew on 2/24/2015.
 */
public class Fragment_DetailView extends Fragment {

    public static final String ARG_EVENT = "event";
    private Event event;
    ShareActionProvider mShareActionProvider;
    Context context;
    Handler handler;

    public static Fragment_DetailView newInstance(Event ev) {
        Fragment_DetailView fragment = new Fragment_DetailView();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, ev);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
        }
        context = getActivity();
        handler = new Handler();
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public Fragment_DetailView() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.fragment_detail, container, false);

        final EditText description = (EditText) rootView.findViewById(R.id.eventDescription);
        final EditText title = (EditText) rootView.findViewById(R.id.eventEdit);
        final AutoCompleteTextView location = (AutoCompleteTextView) rootView.findViewById(R.id.locationEdit);
        final EditText start = (EditText) rootView.findViewById(R.id.startDateBtn);
        final EditText end = (EditText) rootView.findViewById(R.id.endDateBtn);
        final MultiAutoCompleteTextView guests = (MultiAutoCompleteTextView) rootView.findViewById(R.id.guests);
        Button okay = (Button) rootView.findViewById(R.id.okayButton);

        title.setText(event.getName());
        description.setText(event.getDescription());
        location.setText(event.getLocation());
        start.setText(event.getStartDate());
        end.setText(event.getEndDate());
        guests.setText(event.getGuestsToString());
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) event.getId() != null) {
                    SharedPreferences prefs = getActivity().getSharedPreferences("SyncItUserData", 0);
                    String username = prefs.getString("username", "");
                    String familyusername = prefs.getString("familyusername", "");
                    String[] urls = new String[9];
                    urls[0] = title.getText().toString();
                    urls[1] = description.getText().toString();
                    urls[2] = start.getText().toString();
                    urls[3] = end.getText().toString();
                    urls[4] = location.getText().toString();
                    urls[5] = guests.getText().toString();
                    urls[6] = username;
                    urls[7] = familyusername;
                    urls[8] = String.valueOf(event.getId());
                    MyUpdateEventAsyncTask task = new MyUpdateEventAsyncTask();
                    task.execute(urls);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Can not edit this event", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }

    private class MyUpdateEventAsyncTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... urls) {
            String url = MyUtility.SERVER_LINK + "updateevent/" + urls[0] + "/" + urls[1]
                    + "/" + urls[2] + "/" + urls[3] + "/" + urls[4] + "/" + urls[5] + "/" + urls[6] + "/" + urls[7] + "/" + urls[8];
            url = url.replaceAll(" ", "%20");

            MyUtility.downloadJSON(url);

            Log.d("MyUpdateEventAsyncTask.urlnew", url);

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