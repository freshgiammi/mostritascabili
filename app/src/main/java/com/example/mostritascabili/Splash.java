package com.example.mostritascabili;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Splash
 * Used to load all data before entering the MainActivity.
 */

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent intent = new Intent(this, MainActivity.class);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Create sharedPrefs to store session_id.
        final SharedPreferences storedSessionID = getSharedPreferences("session_id", MODE_PRIVATE);
        String session_id = storedSessionID.getString("session_id", null);

        if (!NetworkRequestHandler.isConnected(this)) {
            new AlertDialog.Builder(Splash.this)
                    .setTitle("You're not connected to the internet!")
                    .setMessage("We're sorry, but to use Mostri Tascabili you need to be connected to the internet. (Wifi or mobile)")
                    .setCancelable(false)
                    .setPositiveButton("Got it.", new DialogInterface.OnClickListener() {
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
                    final JSONObject sessionIdObject = response;
                    // Feed response to getProfile and getMapObjects
                    NetworkRequestHandler.getProfile(Splash.this, sessionIdObject, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                        ProfileModel.getInstance().populate(response);
                            NetworkRequestHandler.getMapObjects(Splash.this, sessionIdObject, new ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    MapObjectModel.getInstance().populate(response);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });
                }
            });
            } else {
                Log.d("Splash", "SharedPreferences: session_id found: " + session_id + ". Not requesting new one.");

                //Create JSONObject
                final JSONObject sessionIdObject = new JSONObject();
                try {
                    sessionIdObject.put("session_id", session_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Fetch profile and mapObject
                NetworkRequestHandler.getProfile(this, sessionIdObject, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                    ProfileModel.getInstance().populate(response);
                    NetworkRequestHandler.getMapObjects(Splash.this, sessionIdObject, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            MapObjectModel.getInstance().populate(response);
                            startActivity(intent);
                            finish();
                        }
                    });
                    }
                });
            }
        }
    }
}
