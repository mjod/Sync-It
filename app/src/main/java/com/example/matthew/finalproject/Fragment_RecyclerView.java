package com.example.matthew.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;


public class Fragment_RecyclerView extends Fragment {

    public Handler handler;// for grabbing some event values for showing the dot

    static MyRecyclerViewAdapter myRecyclerViewAdapter;
    static RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        handler = new Handler();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu.findItem(R.id.action_search) == null)
            inflater.inflate(R.menu.menu_recycler_view, menu);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (search != null) {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static Fragment_RecyclerView newInstance(int num) {
        Fragment_RecyclerView fragment = new Fragment_RecyclerView();
        return fragment;
    }

    public Fragment_RecyclerView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        RecyclerView.LayoutManager mLayoutManager;
        rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), MainActivity.eventList);


        myRecyclerViewAdapter.SetOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
        return rootView;
    }

    void refreshItems() {
        handler.post(calendarUpdater);
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }


    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            //Fragment_CalendarView.items.clear();

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


            for (int i = 0; i < Utility.eventData.size(); i++) {
                Fragment_CalendarView.itemmonth.add(GregorianCalendar.DATE, 1);
                Fragment_CalendarView.items.add(Utility.eventData.get(i).getStartDate().toString());

                //Log.d("itemAdded", Utility.eventData.get(i).getStartDate().toString());
            }
            Fragment_CalendarView.adapter.setItems(Fragment_CalendarView.items);
            Fragment_CalendarView.adapter.notifyDataSetChanged();
            if (Fragment_RecyclerView.myRecyclerViewAdapter != null) {
                Fragment_RecyclerView.myRecyclerViewAdapter.setItems(MainActivity.eventList);
                Fragment_RecyclerView.myRecyclerViewAdapter.notifyDataSetChanged();
            }
            */
        }
    };
}