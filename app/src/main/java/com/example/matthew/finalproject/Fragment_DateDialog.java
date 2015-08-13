package com.example.matthew.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Fragment_DateDialog extends DialogFragment {
    Date mDate, startDate;
    String mType;
    final static public String DATE_ARGS = "date";
    final static public String TYPE_ARGS = "type";

    public Fragment_DateDialog() {
    }

    public static Fragment_DateDialog newInstance(Date date, String type) {
        Fragment_DateDialog fragment = new Fragment_DateDialog();
        Bundle args = new Bundle();
        args.putSerializable(DATE_ARGS, date);
        args.putString(TYPE_ARGS, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDate = (Date) getArguments().getSerializable(DATE_ARGS);
        startDate = mDate;
        mType = getArguments().getString(TYPE_ARGS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View v = getActivity().getLayoutInflater().inflate(R.layout.date_dialog, null);
        Button okay = (Button) v.findViewById(R.id.okayButton);
        Button cancel = (Button) v.findViewById(R.id.cancelButton);
        okay.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);


        final DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
        datePicker.updateDate(mDate.getYear(), mDate.getMonth(), mDate.getDay());

        Calendar myCal = new GregorianCalendar();
        myCal.setTime(mDate);
        datePicker.setMinDate(myCal.getTimeInMillis());
        final TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        timePicker.setCurrentHour(mDate.getHours());
        timePicker.setCurrentMinute(mDate.getMinutes());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(v)
                .setTitle("Create Event")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("onAlertClicked", mDate.toString());
                                Calendar cal = Calendar.getInstance();

                                cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                                Log.d("onAlertClicked.cal", cal.getTime().toString());
                                mDate = cal.getTime();
                                SimpleDateFormat sfdate = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                                timePicker.getCurrentHour();
                                sfdate.format(mDate);
                                if (mType.equals("Start")) {
                                    Fragment_EventCreator.startDate.setText(sfdate.format(mDate).toString());
                                } else if (mType.equals("End")) {
                                    Fragment_EventCreator.endDate.setText(sfdate.format(mDate).toString());
                                }

                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        return alertDialogBuilder.create();
    }

}