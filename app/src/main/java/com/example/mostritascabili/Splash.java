package com.example.mostritascabili;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

//Todo: Check if user is connected to the internet, and move permissions logic here
public class Splash extends AppCompatActivity {
    final String url = "https://ewserver.di.unimi.it/mobicomp/mostri/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent intent = new Intent(this, MainActivity.class);

        // Create sharedPrefs to store session_id.
        final SharedPreferences storedSessionID = getSharedPreferences("session_id", MODE_PRIVATE);
        String session_id = storedSessionID.getString("session_id",null);

        // Set up RequestQueue for session_id, profile data and map objects.
        RequestQueue queue = Volley.newRequestQueue(this);

        // If session_id is null, fetch a new one.
        if (session_id == null){
            JsonObjectRequest getSessionID = new JsonObjectRequest(
                    Request.Method.POST,
                    url+"register.php",
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
                            Log.d("getSessionID", "register.php response: " + response.toString());

                            //Extract session_id
                            String session_id = null;
                            try {
                                session_id = response.getString("session_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //Save session_id inside SharedPreferences
                            SharedPreferences.Editor editor = storedSessionID.edit();
                            editor.putString("session_id",session_id);
                            editor.commit();

                            //Todo: WORKAROUND: Create an interface that work with callback methods

                            // Feed response to getProfile and getMapObjects
                            getProfile(response);
                            getMapObjects(response);
                        }
                    }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Log.d("getSessionID", "Error: " + error.toString());
                }
            });
            queue.add(getSessionID);
        } else {
            Log.d("SharedPrefs","session_id found: "+session_id+". Not requesting new one.");

            //Create JSONObject
            JSONObject sessionIdObject= new JSONObject();
            try {
                sessionIdObject.put("session_id",session_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getProfile(sessionIdObject);
            getMapObjects(sessionIdObject);
        }

        //Todo: check that we actually have finished populating models.

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(intent);
                finish();
            }

        }, 2000); // 5000ms delay

    }

    //Fetch profile based on session_id
    private void getProfile(JSONObject response) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest profile = new JsonObjectRequest(
                Request.Method.POST,
                url + "getprofile.php",
                response,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("getProfile", "getprofile.php response: " + response.toString());
                        ProfileModel.getInstance().populate(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getProfile", "Error: " + error.toString());
            }
        });
        queue.add(profile);
    }

    //Fetch Map Objects
    private void getMapObjects(JSONObject response) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest mapObject = new JsonObjectRequest(
                Request.Method.POST,
                url + "getmap.php",
                response,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("getMapObjects", "getmap.php response: " + response.toString());
                        MapObjectModel.getInstance().populate(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getMapObjects", "Error: " + error.toString());
            }
        });
        queue.add(mapObject);
    }


}
