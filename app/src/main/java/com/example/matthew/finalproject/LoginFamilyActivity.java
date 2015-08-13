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
import org.json.JSONObject;


public class LoginFamilyActivity extends ActionBarActivity {
    EditText userText, passText;
    TextView invalidText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_family);
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        invalidText = (TextView) findViewById(R.id.invalidLogin);
        userText = (EditText) findViewById(R.id.userText);
        passText = (EditText) findViewById(R.id.passText);
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterFamilyActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        Button login = (Button) findViewById(R.id.btnLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userText.setEnabled(false);
                passText.setEnabled(false);
                SharedPreferences prefs = getSharedPreferences("SyncItUserData", 0);
                String username = prefs.getString("username", "");
                MyUserPassCheckerAsyncTask task = new MyUserPassCheckerAsyncTask();
                task.execute(new String[]{userText.getText().toString(), username});
            }
        });
    }

    private class MyUserPassCheckerAsyncTask extends AsyncTask<String, Void, Boolean> {
        String name;
        String familyName;

        @Override
        protected Boolean doInBackground(String... urls) {
            String familyAccount = MyUtility.downloadJSON(MyUtility.SERVER_LINK + "familyuser/" + urls[0] + "/" + urls[1]);
            Log.d("familyAccount", familyAccount);

            boolean isUser = false;
            try {
                JSONArray userJsonArray = new JSONArray(familyAccount);
                for (int i = 0; i < userJsonArray.length(); i++) {
                    JSONObject userObject = userJsonArray.getJSONObject(i);
                    if (userObject != null) {
                        if (userObject.get("family_username").equals(userText.getText().toString())
                                && userObject.get("family_password").equals(passText.getText().toString())) {
                            familyName = (String) userObject.get("family_name");
                            isUser = true;
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return isUser;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                SharedPreferences prefs = getSharedPreferences("SyncItUserData", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("familyusername", userText.getText().toString());
                editor.putString("familypassword", passText.getText().toString());
                editor.putString("familyname", familyName);
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
                        userText.setEnabled(true);
                        passText.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                invalidText.setText("Username and/or Password Invalid\nPlease try again or create account");
                userText.startAnimation(shake);
                passText.startAnimation(shake);
            }
        }
    }
}