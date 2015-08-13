package com.example.matthew.finalproject;

/**
 * Created by Matthew on 4/8/2015.
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class Utility {
    static ArrayList<Event> eventData = new ArrayList<>();

    public static ArrayList<Event> readCalendarEvent(Context context) {
        eventData.clear();
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse("content://com.android.calendar/events"),
                        new String[]{"_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name  
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        for (int i = 0; i < CNames.length; i++) {
            Event event = new Event();
            /*
            Log.d("EventsAdded", cursor.getString(0) + ":  "
                    + cursor.getString(1) + " "
                    + cursor.getString(2) + " "
                    + getDate(Long.parseLong(cursor.getString(3))) + " "
                    + getDate(Long.parseLong(cursor.getString(4))) + " Location:"
                    + cursor.getString(5) + "~");
            Log.d("testDate", cursor.getString(3));
            */
            event.setId(Integer.parseInt(cursor.getString(0)));
            event.setName(cursor.getString(1));
            event.setDescription(cursor.getString(2));
            event.setStartDate(getDate(Long.parseLong(cursor.getString(3))));
            event.setEndDate(getDate(Long.parseLong(cursor.getString(4))));
            event.setLocation(cursor.getString(5));
            event.setStartDateUnchanged(Long.parseLong(cursor.getString(3)));
            event.setGuests(new String[]{""});
            eventData.add(event);
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();
        }
        cursor.close();
        return eventData;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getDateAndTime(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd-MM-yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static Event getEvent(int id){
        for (Event e : MainActivity.eventList){
            if (e.getId() == id){
                return e;
            }
        }
        return null;
    }


}
