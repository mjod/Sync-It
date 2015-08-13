package com.example.matthew.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class MainActivity extends ActionBarActivity {
    public static final String GOOGLE_PROJECT_ID = "210692216093";
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    static final String TAG = "Register Activity";

    public static Handler handler;
    public static ArrayList<Event> eventList;
    public static ArrayList<FamilyMem> familyMembers;
    private Toolbar mToolBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private DrawerRecyclerViewAdapter mDrawerRecyclerViewAdapter;
    static DrawerData drawerData;
    public static String familyUsername;
    public static String username;
    public static String familyPhoto = "";
    static ImageButton nav_banner;
    GoogleCloudMessaging gcm;
    static Context context;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences("SyncItUserData", 0);
        String name = prefs.getString("name", "");
        username = prefs.getString("username", "");
        String pwd = prefs.getString("password", "");
        String familysname = prefs.getString("familyname", "");
        familyUsername = prefs.getString("familyusername", "");
        String familyPwd = prefs.getString("familypassword", "");
        Log.d("savedInfo", name + ", " + username + ", " + pwd + ", " +
                familysname + ", " + familyUsername + ", " + familyPwd);
        familyMembers = new ArrayList<>();
        TextView surname_nav = (TextView) findViewById(R.id.surname);
        TextView surname_toolbar = (TextView) findViewById(R.id.surname_toolbar);
        surname_nav.setText(familysname + " Family");
        surname_toolbar.setText(familysname + " Family");

        drawerData = new DrawerData();
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        ImageButton addEvent = (ImageButton) findViewById(R.id.addEvent);
        addEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Date date = new Date(System.currentTimeMillis());
                Fragment_EventCreator dialog = Fragment_EventCreator.newInstance(date);
                dialog.show(getSupportFragmentManager(), "DatePicker Dialog: Get Result");
            }
        });
        ImageButton threeBar = (ImageButton) findViewById(R.id.threeBarButton);
        threeBar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDrawerLayout.openDrawer(findViewById(R.id.drawer));
            }
        });
        mDrawerList = (RecyclerView) findViewById(R.id.drawer_list);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));
        mDrawerRecyclerViewAdapter = new DrawerRecyclerViewAdapter(this, drawerData.getDataList());
        mDrawerRecyclerViewAdapter.SetOnItemClickListener(new DrawerRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Map<String, ?> entry = drawerData.getItem(position);
                HashMap<String, Boolean> itemMap_bool = (HashMap<String, Boolean>) entry;
                if (entry.get("name").toString().contains("Upcoming Events") && !itemMap_bool.get("selection")) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, Fragment_ViewPager.newInstance(0))
                            .commit();
                    mDrawerLayout.closeDrawer(findViewById(R.id.drawer));
                } else if (entry.get("name").toString().contains("Calendar") && !itemMap_bool.get("selection")) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, Fragment_ViewPager.newInstance(1))
                            .commit();

                    mDrawerLayout.closeDrawer(findViewById(R.id.drawer));
                } else if (entry.get("name").toString().contains("About Me") && !itemMap_bool.get("selection")) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, PlaceholderFragment.newInstance(R.layout.fragment_about_me))
                            .commit();

                    //mViewPager.setCurrentItem(1);
                    mDrawerLayout.closeDrawer(findViewById(R.id.drawer));
                } else if (entry.get("name").toString().contains("Parental Controls") && !itemMap_bool.get("selection")) {
                    /*
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, PlaceholderFragment.newInstance(R.layout.fragment_settings))
                            .commit();
                            */

                    Fragment_Settings settings = Fragment_Settings.newInstance();
                    settings.show(getSupportFragmentManager(), "settings");
                    //mViewPager.setCurrentItem(1);
                    mDrawerLayout.closeDrawer(findViewById(R.id.drawer));
                } else if (entry.get("name").toString().contains("Logout") && !itemMap_bool.get("selection")) {
                    logout();
                    //mViewPager.setCurrentItem(1);
                    mDrawerLayout.closeDrawer(findViewById(R.id.drawer));
                }
                mDrawerLayout.closeDrawer(findViewById(R.id.drawer));
                if(!entry.get("name").toString().contains("Parental Controls")) {
                    drawerData.makeFalse();
                    entry = drawerData.getItem(position);
                    itemMap_bool = (HashMap<String, Boolean>) entry;
                    itemMap_bool.put("selection", true);
                    mDrawerRecyclerViewAdapter.notifyDataSetChanged();
                }

            }
        });
        mDrawerList.setAdapter(mDrawerRecyclerViewAdapter);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolBar, R.string.open, R.string.close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        nav_banner = (ImageButton) findViewById(R.id.nav_banner);
        String picture = prefs.getString("picture", "");
        if (picture != "")
            nav_banner.setImageBitmap(StringToBitMap(picture));
        nav_banner.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        handler = new Handler();
        handler.post(calendarUpdater);


        context = getApplicationContext();
        if (TextUtils.isEmpty(regId)) {
            regId = registerGCM();
            Log.d("RegisterActivity", "GCM RegId: " + regId);
        } else {
            /*
            Toast.makeText(getApplicationContext(),
                    "Already Registered with GCM Server!",
                    Toast.LENGTH_LONG).show();
                    */
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Fragment_ViewPager.newInstance(0))
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                SharedPreferences prefs = getSharedPreferences("SyncItUserData", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("picture", BitMapToString(bitmap));
                editor.apply();
                nav_banner.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void logout() {
        SharedPreferences settings = this.getSharedPreferences("SyncItUserData", this.MODE_PRIVATE);
        settings.edit().clear().commit();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            eventList = Utility.readCalendarEvent(MainActivity.this);
            SharedPreferences prefs = getSharedPreferences("SyncItUserData", 0);
            String familyUsername = prefs.getString("familyusername", "");
            MyGetEventsAsyncTask task = new MyGetEventsAsyncTask(Fragment_RecyclerView.myRecyclerViewAdapter, Fragment_CalendarView.adapter);
            task.execute(familyUsername);
            MyQuickSort sorter = new MyQuickSort();
            sorter.sort(eventList);
        }
    };

    public static class PlaceholderFragment extends Fragment {

        public static final String ARG_NUM = "fragment_id";


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            setRetainInstance(true);
        }


        public static PlaceholderFragment newInstance(int num) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_NUM, num);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;


            final int id = getArguments().getInt(ARG_NUM);
            switch (id) {
                case R.layout.fragment_about_me:
                    rootView = inflater.inflate(R.layout.fragment_about_me, container, false);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_about_me, container, false);

            }
            return rootView;
        }

    }

    public static class MyQuickSort {
        ArrayList<Event> array;
        int length;

        public void sort(ArrayList<Event> inputArr) {

            if (inputArr == null || inputArr.size() == 0) {
                Log.d("compareEvent", "inputArr is null");
                return;
            }
            this.array = inputArr;
            length = inputArr.size();
            quickSort(0, length - 1);
        }

        private void quickSort(int lowerIndex, int higherIndex) {

            int i = lowerIndex;
            int j = higherIndex;
            // calculate pivot number, I am taking pivot as middle index number
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                Date pivot = sdf.parse(array.get((lowerIndex + (higherIndex - lowerIndex) / 2)).getStartDate());

                // Divide into two arrays
                while (i <= j) {
                    /**
                     * In each iteration, we will identify a number from left side which
                     * is greater then the pivot value, and also we will identify a number
                     * from right side which is less then the pivot value. Once the search
                     * is done, then we exchange both numbers.
                     */
                    while (sdf.parse(array.get(i).getStartDate()).compareTo(pivot) < 0) {
                        i++;
                    }
                    while (sdf.parse(array.get(j).getStartDate()).compareTo(pivot) > 0) {
                        j--;
                    }
                    if (i <= j) {
                        exchangeNumbers(i, j);
                        //move index to next position on both sides
                        i++;
                        j--;
                    }
                }
                // call quickSort() method recursively
                if (lowerIndex < j)
                    quickSort(lowerIndex, j);
                if (i < higherIndex)
                    quickSort(i, higherIndex);
            } catch (ParseException e) {              // Insert this block.
                e.printStackTrace();
            }
        }

        private void exchangeNumbers(int i, int j) {
            Event temp = eventList.get(i);
            eventList.set(i, eventList.get(j));
            eventList.set(j, temp);
        }
    }


    public static class MyGetEventsAsyncTask extends AsyncTask<String, Void, Boolean> {
        private final WeakReference<MyRecyclerViewAdapter> rvAdapter;
        private final WeakReference<CalendarAdapter> cAdapter;

        public MyGetEventsAsyncTask(MyRecyclerViewAdapter myRecyclerViewAdapter, CalendarAdapter calendarAdapter) {
            rvAdapter = new WeakReference<>(myRecyclerViewAdapter);
            cAdapter = new WeakReference<>(calendarAdapter);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String familyEventsJson = MyUtility.downloadJSON(MyUtility.SERVER_LINK + "getfamilyevents/" + urls[0]);
            //Log.d("familyEventsJson", familyEventsJson);

            try {
                JSONArray userJsonArray = new JSONArray(familyEventsJson);
                for (int i = 0; i < userJsonArray.length(); i++) {
                    JSONObject userObject = userJsonArray.getJSONObject(i);
                    if (userObject != null) {
                        String eventName = userObject.getString("name");
                        String eventDescription = userObject.getString("description");
                        String eventStart = userObject.getString("start");
                        String eventEnd = userObject.getString("end");
                        String location = userObject.getString("location");
                        String guests = userObject.getString("guests");
                        int id = userObject.getInt("event_id");
                        String[] guestsArray = guests.split(",");
                        Event onlineEvents = new Event(eventName, eventDescription, eventStart, eventEnd, location, guestsArray, id);
                        eventList.add(onlineEvents);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {

            MyQuickSort sorter = new MyQuickSort();
            sorter.sort(eventList);

            if (rvAdapter != null && Fragment_RecyclerView.myRecyclerViewAdapter != null) {
                Fragment_RecyclerView.myRecyclerViewAdapter.setItems(MainActivity.eventList);
                Fragment_RecyclerView.myRecyclerViewAdapter.notifyDataSetChanged();
                Log.d("onelineEventAdding", "I am here");
            }
            if (cAdapter != null && Fragment_CalendarView.adapter != null && Fragment_CalendarView.items != null) {
                Fragment_CalendarView.items.clear();
                for (int i = 0; i < MainActivity.eventList.size(); i++) {
                    Fragment_CalendarView.itemmonth.add(GregorianCalendar.DATE, 1);
                    Fragment_CalendarView.items.add(eventList.get(i).getStartDate().toString().split(" ")[0]);

                    //Log.d("itemAdded", Utility.eventData.get(i).getStartDate().toString());
                }
                Fragment_CalendarView.adapter.setItems(Fragment_CalendarView.items);
                Fragment_CalendarView.adapter.notifyDataSetChanged();
            }
            familyMembers.clear();
            MyUtility.MyGetFamilyMembersAsyncTask task = new MyUtility.MyGetFamilyMembersAsyncTask();
            task.execute(familyUsername);
            //MyGetFamilyPhoto t = new MyGetFamilyPhoto();
            //t.execute(familyUsername);

        }
    }


    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        } else {
            final SharedPreferences prefs = getSharedPreferences(
                    MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            int appVersion = getAppVersion(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(REG_ID, regId);
            editor.putInt(APP_VERSION, appVersion);
            editor.commit();
            MyServerRegIdUpdaterAsyncTask myServerRegIdUpdaterAsyncTask = new MyServerRegIdUpdaterAsyncTask();
            myServerRegIdUpdaterAsyncTask.execute(new String[]{username, regId});
            /*

            Toast.makeText(getApplicationContext(),
                    "RegId already available. RegId: " + regId,
                    Toast.LENGTH_LONG).show();
                    */
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                /*
                Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
                        */
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
        MyServerRegIdUpdaterAsyncTask myServerRegIdUpdaterAsyncTask = new MyServerRegIdUpdaterAsyncTask();
        myServerRegIdUpdaterAsyncTask.execute(new String[]{username, regId});
    }

    private class MyServerRegIdUpdaterAsyncTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length >= 2) {
                String url = MyUtility.SERVER_LINK + "updateregid/" + urls[0] + "/" + urls[1];

                MyUtility.downloadJSON(url);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {


        }
    }
    /*
    public class MySetFamilyPhoto extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            String url = MyUtility.SERVER_LINK + "updatefamilyphoto/" + urls[0] + "/" + urls[1];
            MyUtility.downloadJSON(url.replaceAll(" ","%20"));

            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            //MainActivity.MyGetEventsAsyncTask task = new MainActivity.MyGetEventsAsyncTask(Fragment_RecyclerView.myRecyclerViewAdapter, Fragment_CalendarView.adapter);
            //task.execute(MainActivity.familyUsername);
        }
    }
    public static class MyGetFamilyPhoto extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String url = MyUtility.SERVER_LINK + "getfamilyphoto/" + urls[0];
            String familyImageJson = MyUtility.downloadJSON(url);
            String famPicture ="";
            try {
                JSONArray userJsonArray = new JSONArray(familyImageJson);
                for (int i = 0; i < userJsonArray.length(); i++) {
                    JSONObject userObject = userJsonArray.getJSONObject(i);
                    if (userObject != null) {
                        famPicture = userObject.getString("family_picture");
                        famPicture.replaceAll("~","/");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return famPicture;
        }

        @Override
        protected void onPostExecute(String strFamPic) {
            if (!familyPhoto.equals(strFamPic)){
                familyPhoto = strFamPic;
                nav_banner.setImageBitmap(StringToBitMap(familyPhoto));
            }
            //MainActivity.MyGetEventsAsyncTask task = new MainActivity.MyGetEventsAsyncTask(Fragment_RecyclerView.myRecyclerViewAdapter, Fragment_CalendarView.adapter);
            //task.execute(MainActivity.familyUsername);
        }
    }
    */
}
