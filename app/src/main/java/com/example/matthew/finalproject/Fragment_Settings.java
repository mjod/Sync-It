package com.example.matthew.finalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;


public class Fragment_Settings extends DialogFragment {
    private RecyclerView mMembersList;
    private ParentalControlRecyclerAdapter mParentalControlRecyclerAdapter;
    public static int count = 0;
    public static String familyUsername = "";

    public Fragment_Settings() {
    }

    public static Fragment_Settings newInstance() {
        Fragment_Settings fragment = new Fragment_Settings();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, null);
        SharedPreferences prefs = getActivity().getSharedPreferences("SyncItUserData", 0);
        familyUsername = prefs.getString("familyusername", "");
        mMembersList = (RecyclerView) v.findViewById(R.id.recyclerFamily);
        mMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mParentalControlRecyclerAdapter = new ParentalControlRecyclerAdapter(getActivity(), MainActivity.familyMembers);
        mMembersList.setAdapter(mParentalControlRecyclerAdapter);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(v)
                .setTitle("Parental Controls")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i = 0; i < MainActivity.familyMembers.size();i++){
                                    Log.d("GetParent", MainActivity.familyMembers.get(i).getName() + ": " + MainActivity.familyMembers.get(i).getParent());
                                }
                                count = 0;
                                String[] urls = new String[2];
                                urls[0] = MainActivity.familyMembers.get(count).getUsername();
                                urls[1] = MainActivity.familyMembers.get(count).getParent() + "";
                                MySetParentalControl mySetParentalControl = new MySetParentalControl();
                                mySetParentalControl.execute(urls);

                            }
                        });
        return alertDialogBuilder.create();
    }

    public static class MySetParentalControl extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            String url = MyUtility.SERVER_LINK + "updateparentalcontrol/" + urls[0] + "/" + urls[1];
            Log.d("Setting", "Updating " +urls[0] + " to " + urls[1]);
            MyUtility.downloadJSON(url);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            count++;
            if (count < MainActivity.familyMembers.size()) {
                String[] urls = new String[2];
                urls[0] = MainActivity.familyMembers.get(count).getUsername();
                urls[1] = MainActivity.familyMembers.get(count).getParent() + "";
                MySetParentalControl mySetParentalControl = new MySetParentalControl();
                mySetParentalControl.execute(urls);
            }
        }
    }
}
