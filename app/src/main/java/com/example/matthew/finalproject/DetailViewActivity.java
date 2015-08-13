package com.example.matthew.finalproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;


public class DetailViewActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Event event = (Event) getIntent().getSerializableExtra("event");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, Fragment_DetailView.newInstance(event))
                    .commit();
        }


    }
}
