package com.example.mostritascabili;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Splash extends AppCompatActivity {
    final String url = "https://ewserver.di.unimi.it/mobicomp/mostri/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent intent = new Intent(this, MainActivity.class);

        // Create sharedPrefs to store session_id.
        final SharedPreferences storedSessionID = getSharedPreferences("session_id", MODE_PRIVATE);
        String session_id = storedSessionID.getString("session_id", null);

        if (NetworkRequestHandler.isConnected(this) == false) {
            new AlertDialog.Builder(Splash.this)
                    .setTitle("Non sei connesso alla rete!")
                    .setMessage("Ci dispiace, ma per utilizzare Mostri Tascabili, abbiamo bisogno che tu sia connesso ad una rete! (Wifi o mobile)")
                    .setCancelable(false)
                    .setPositiveButton("Ho capito.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).show();
        } else {
            // If session_id is null, fetch a new one.
            if (session_id == null) {
            NetworkRequestHandler.getSessionID(this, new ServerCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    //Extract session_id
                    String session_id = null;
                    try {
                        session_id = response.getString("session_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Save session_id inside SharedPreferences
                    SharedPreferences.Editor editor = storedSessionID.edit();
                    editor.putString("session_id", session_id);
                    editor.apply();

                    // Feed response to getProfile and getMapObjects
                    NetworkRequestHandler.getProfile(Splash.this, response, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                        ProfileModel.getInstance().populate(response);
                        }
                    });
                    NetworkRequestHandler.getMapObjects(Splash.this, response, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {

                        }
                    });
                }
            });
            } else {
                Log.d("SharedPrefs", "session_id found: " + session_id + ". Not requesting new one.");

                //Create JSONObject
                JSONObject sessionIdObject = new JSONObject();
                try {
                    sessionIdObject.put("session_id", session_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Fetch profile and mapObjects
                // Feed response to getProfile and getMapObjects
                NetworkRequestHandler.getProfile(this, sessionIdObject, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        ProfileModel.getInstance().populate(response);
                    }
                });
                NetworkRequestHandler.getMapObjects(this, sessionIdObject, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {

                    }
                });
            }

            //Todo: check that we actually have finished populating models.
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }

            }, 2000); // 2000ms delay

        }
    }

}
