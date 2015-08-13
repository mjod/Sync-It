package com.example.matthew.finalproject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Matthew on 4/12/2015.
 */
public class Event implements Serializable {

    String name="";
    String description="";
    String startDate="";
    String endDate="";
    long startDateUnchanged = 0;
    int id = 0;
    String location = "";
    String[] guests;

    public Event() {

    }


    public Event(String name, String description, String startDate, String endDate, String location, String[] guests) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.guests = guests;
    }

    public Event(String name, String description, String startDate, String endDate, String location, String[] guests, int id) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.guests = guests;
        this.id = id;
    }

    public Event(Event event) {
        this.name = event.getName();
        this.description = event.getDescription();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.id = event.getId();
        this.location = event.getLocation();
        this.guests = event.getGuests();

    }

    public void setStartDateUnchanged(long startDateUnchanged) {
        this.startDateUnchanged = startDateUnchanged;
    }

    public String[] getGuests() {
        return guests;
    }

    public String getGuestsToString() {
        String guestString = "";
        for (int i = 0; i < guests.length; i++) {
            if (i == guests.length - 1) {
                guestString = guestString + guests[i];
            } else {
                guestString = guestString + guests[i] + ", ";
            }
        }
        return guestString;
    }

    public void setGuests(String[] guests) {
        this.guests = guests;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getLocation() {
        return location;
    }

    public long getStartDateUnchanged() {
        return startDateUnchanged;
    }
}
