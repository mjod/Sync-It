package com.example.matthew.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends ActionBarActivity {
    EditText nameText, userText, passText;
    TextView invalidText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);

        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        nameText = (EditText) findViewById(R.id.reg_fullname);
        userText = (EditText) findViewById(R.id.reg_email);
        passText = (EditText) findViewById(R.id.reg_password);
        invalidText = (TextView) findViewById(R.id.invalidLogin);
        Button regBtn = (Button) findViewById(R.id.btnRegister);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userText.setEnabled(false);
                passText.setEnabled(false);
                nameText.setEnabled(false);
                MyCreateUserAsyncTask task = new MyCreateUserAsyncTask();
                task.execute(new String[]{
                                userText.getText().toString(),
                                passText.getText().toString(),
                                nameText.getText().toString()
                        }

                );
            }
        });
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Switching to Login Screen/closing register screen
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    private class MyCreateUserAsyncTask extends AsyncTask<String, Void, Boolean> {
        String name;
        String familyName;

        @Override
        protected Boolean doInBackground(String... urls) {
            String userAccount = MyUtility.downloadJSON(MyUtility.SERVER_LINK + "createuser/" + urls[0] +
                    "/" + urls[1] + "/" + urls[2]); //:user/:pass/:name
            //Log.d("userAccount", userAccount);

            boolean isUser = true;
            try {
                JSONArray userJsonArray = new JSONArray(userAccount);
                if (userJsonArray.length() > 0) {
                    isUser = false;
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
                editor.putString("username", userText.getText().toString());
                editor.putString("password", passText.getText().toString());
                editor.putString("name", nameText.getText().toString());
                editor.apply();
                Intent i = new Intent(getApplicationContext(), LoginFamilyActivity.class);
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
                invalidText.setText("Username is already taken\nPlease try again");
                userText.startAnimation(shake);
                passText.startAnimation(shake);
                nameText.startAnimation(shake);
            }
        }
    }
}