package com.example.matthew.finalproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class LocationActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        String msg = getIntent().getStringExtra("msg");
        Log.d("LocationActivity", "received: " +msg);
        Log.d("LocationActivity", "recieved: " +getIntent().getStringArrayExtra("cords")[0] + ":" + getIntent().getStringArrayExtra("cords")[1]);

        Fragment_Location fragment_location = Fragment_Location.newInstance(getIntent().getStringArrayExtra("cords"), msg);
        fragment_location.show(getSupportFragmentManager(), "location");


    }
}
