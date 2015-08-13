package com.example.matthew.finalproject;

/**
 * Created by Matthew on 4/9/2015.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import android.content.DialogInterface;

public class Fragment_CalendarView extends Fragment {


    public static GregorianCalendar month, itemmonth;// calendar instances.

    public static CalendarAdapter adapter;// adapter instance
    // marker.
    public static ArrayList<String> items; // container to store calendar items which
    // needs showing the event marker
    LinearLayout rLayout;
    ArrayList<String> date;
    ArrayList<Event> desc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static Fragment_CalendarView newInstance() {
        Fragment_CalendarView fragment = new Fragment_CalendarView();
        return fragment;
    }

    public Fragment_CalendarView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;
        rootView = inflater.inflate(R.layout.calendar, container, false);
        Locale.setDefault(Locale.US);

        rLayout = (LinearLayout) rootView.findViewById(R.id.text);
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemmonth = (GregorianCalendar) month.clone();

        items = new ArrayList<>();
        setItems();
        adapter = new CalendarAdapter(rootView.getContext(), month);
        adapter.setItems(items);
        //Log.d("testlength",items.size()+"");
        adapter.notifyDataSetChanged();

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        Handler handler = new Handler();
        handler.post(calendarUpdater);

        TextView title = (TextView) rootView.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        RelativeLayout previous = (RelativeLayout) rootView.findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });

        RelativeLayout next = (RelativeLayout) rootView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();

            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (((LinearLayout) rLayout).getChildCount() > 0) {
                    ((LinearLayout) rLayout).removeAllViews();
                }
                desc = new ArrayList<Event>();
                date = new ArrayList<String>();
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                String selectedGridDate = CalendarAdapter.dayString
                        .get(position);
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*",
                        "");
                int gridvalue = Integer.parseInt(gridvalueString);
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);

                for (int i = 0; i < MainActivity.eventList.size(); i++) {
                    if (MainActivity.eventList.get(i).getStartDate().split(" ")[0].equals(selectedGridDate)) {
                        desc.add(MainActivity.eventList.get(i));
                    }
                }
                Log.d("Selected date", selectedGridDate);
                if (desc.size()>0) {
                    new AlertDialog.Builder(rootView.getContext())
                            .setTitle(selectedGridDate)
                            .setView(new EventsView(rootView.getContext(), desc))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .show();
                }
                desc = null;

            }

        });
        return rootView;

    }

    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }

    }

    protected void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

    }

    public void refreshCalendar() {
        TextView title = (TextView) getView().findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        MainActivity.handler.post(calendarUpdater); // generate some calendar items

        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {

            //items.clear();

            // Print dates of the current week
            //DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            MainActivity.eventList = Utility.readCalendarEvent(getActivity());
            SharedPreferences prefs = getActivity().getSharedPreferences("SyncItUserData", 0);
            String familyUsername = prefs.getString("familyusername", "");
            MainActivity.MyGetEventsAsyncTask task = new MainActivity.MyGetEventsAsyncTask(Fragment_RecyclerView.myRecyclerViewAdapter, Fragment_CalendarView.adapter);
            task.execute(familyUsername);
            /*
            MainActivity.MyQuickSort myQuickSort = new MainActivity.MyQuickSort();
            myQuickSort.sort(MainActivity.eventList);
            //Log.d("=====Event====", MainActivity.eventList.get(1).toString());
            //Log.d("=====Date ARRAY====", Utility.eventData.toString());


            for (int i = 0; i < MainActivity.eventList.size(); i++) {
                itemmonth.add(GregorianCalendar.DATE, 1);
                items.add(MainActivity.eventList.get(i).getStartDate().toString());

                //Log.d("itemAdded", Utility.eventData.get(i).getStartDate().toString());
            }
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
            if (Fragment_RecyclerView.myRecyclerViewAdapter != null) {
                Fragment_RecyclerView.myRecyclerViewAdapter.setItems(MainActivity.eventList);
                Fragment_RecyclerView.myRecyclerViewAdapter.notifyDataSetChanged();
            }
            */
        }
    };

    public void setItems() {
        items.clear();

        // Print dates of the current week
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        MainActivity.eventList = Utility.readCalendarEvent(getActivity());
        MainActivity.MyQuickSort myQuickSort = new MainActivity.MyQuickSort();
        myQuickSort.sort(MainActivity.eventList);
        //Log.d("=====Event====", MainActivity.eventList.get(1).toString());
        //Log.d("=====Date ARRAY====", Utility.eventData.toString());
        //Log.d("EventSize", Utility.eventData.size()+"");
        for (Event event : MainActivity.eventList) {
            itemmonth.add(GregorianCalendar.DATE, 1);
            items.add(event.getStartDate().toString());
        }
    }

    public class EventsView extends FrameLayout{


        Context mContext;
        View rootView;
        ArrayList<Event> events;
        MyCalendarRecyclerViewAdapter myRecyclerViewAdapter;
        RecyclerView mRecyclerView;

        public EventsView(Context context, ArrayList<Event> events) {
            super(context);
            this.mContext = context;
            this.events = events;

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RecyclerView.LayoutManager mLayoutManager;
            rootView = inflater.inflate(R.layout.fragment_calendar_recyclerview, null);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            myRecyclerViewAdapter = new MyCalendarRecyclerViewAdapter(mContext, events);


            myRecyclerViewAdapter.SetOnItemClickListener(new MyCalendarRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemLongClick(View v, int position) {
                }

                @Override
                public void onItemClick(View v, int position) {
                    Event event = myRecyclerViewAdapter.getDataSet().get(position);
                    Intent intent = new Intent(getActivity(), DetailViewActivity.class);
                    intent.putExtra("event", event);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

                /*
                Map<String,?> entry = (Map<String,?>) movieData.getItem(position);
                Toast.makeText(getActivity(), "Selected: " + entry.get("name"), Toast.LENGTH_SHORT).show();
                if (getActivity().getLocalClassName().contains("ViewPagerActivity")){
                    Intent intent = new Intent(getActivity(), DetailViewActivity.class);
                    intent.putExtra("movie",(HashMap<String, ?>) movieData.getItem(position));
                    startActivity(intent);
                }
                else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, Fragment_DetailView.newInstance((HashMap<String, ?>) movieData.getItem(position)))
                            .addToBackStack("fragmentMovie")
                            .commit();
                }
                */
                }


            });
            mRecyclerView.setAdapter(myRecyclerViewAdapter);
            //mStockDataList.addItemDecoration(new DividerItemDecoration(mContext.getDrawable(R.drawable.divider)));


            addView(rootView);
        }
    }
}
