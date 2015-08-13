package com.example.matthew.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


public class RegisterFamilyActivity extends ActionBarActivity {
    EditText nameText, userText, passText;
    TextView invalidText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_family);
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        nameText = (EditText) findViewById(R.id.reg_fullname);
        userText = (EditText) findViewById(R.id.reg_email);
        passText = (EditText) findViewById(R.id.reg_password);
        invalidText = (TextView) findViewById(R.id.invalidLogin);
        Button regBtn = (Button) findViewById(R.id.btnRegister);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("SyncItUserData", 0);
                String uname = prefs.getString("username", "");
                String[] urls = new String[4];
                urls[0] = userText.getText().toString();
                urls[1] = passText.getText().toString();
                urls[2] = nameText.getText().toString();
                urls[3] = uname;
                MyCreateFamilyAsyncTask myCreateFamilyAsyncTask = new MyCreateFamilyAsyncTask();
                myCreateFamilyAsyncTask.execute(urls);
            }
        });
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Switching to Login Screen/closing register screen
                Intent i = new Intent(getApplicationContext(), LoginFamilyActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    private class MyCreateFamilyAsyncTask extends AsyncTask<String, Void, Boolean> {
        String name;
        String familyName;

        @Override
        protected Boolean doInBackground(String... urls) {
            MyUtility.downloadJSON(MyUtility.SERVER_LINK + "createfamily/" + urls[0] +
                    "/" + urls[1] + "/" + urls[2] + "/" + urls[3]); //:familyuser/:familypass/:familyname/:username
            //Log.d("userAccount", userAccount);

            boolean isUser = true;
            /*
            try {
                JSONArray userJsonArray = new JSONArray(userAccount);
                if (userJsonArray.length() > 0) {
                    isUser = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            */
            return isUser;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                SharedPreferences prefs = getSharedPreferences("SyncItUserData", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("familyusername", userText.getText().toString());
                editor.putString("familypassword", passText.getText().toString());
                editor.putString("familyname", nameText.getText().toString());
                editor.apply();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                finish();
            } else {
                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                shake.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        userText.setText("");
                        passText.setText("");
                        nameText.setText("");
                        userText.setEnabled(true);
                        passText.setEnabled(true);
                        nameText.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                invalidText.setText("Family username is already taken\nPlease try again");
                userText.startAnimation(shake);
                passText.startAnimation(shake);
                nameText.startAnimation(shake);
            }
        }
    }
}