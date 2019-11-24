package com.example.mostritascabili;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

public class Splash extends AppCompatActivity {

    final String url = "https://ewserver.di.unimi.it/mobicomp/mostri/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(this, MainActivity.class);

        //Todo: Add database data, populate model, then pivot to MainActivity

        //Todo: Check if user is connected to the internet, and move permissions logic here

        //Todo: Check database, if we already have a session_id then DO NOT FETCH THIS.
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest getSessionID = new JsonObjectRequest(
                Request.Method.POST,
                url+"register.php",
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        Log.d("getSessionID", "Correct: " + response.toString());

                        //Extract session_id
                        String session_id = null;
                        try {
                            session_id = response.getString("session_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("getSessionID", session_id);

                        //Todo: WORKAROUND: Create an interface that work with callback methods
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
                        Log.d("getProfile", "Correct: " + response.toString());
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
                        Log.d("getMapObjects", "Correct: " + response.toString());
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
