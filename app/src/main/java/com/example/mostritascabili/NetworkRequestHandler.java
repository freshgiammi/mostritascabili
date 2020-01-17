package com.example.mostritascabili;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NetworkRequestHandler
 * Main class used to create Volley request and server communications.
 * Interfaces to ServerCallback to customize the use of the response.
 */

public class NetworkRequestHandler {

    //Check whether we have internet connection or not.
    //Todo: find out how to use ConnectivityManager.NetworkCallback
    public static boolean isConnected(Context context){
      ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    // Fetch Map Objects
    public static void getMapObjects(Context context, final JSONObject param, final ServerCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.d("NetworkRequestHandler", "getMapObjects: Initialized");

        JsonObjectRequest mapObject = new JsonObjectRequest(
                Request.Method.POST,
                "https://ewserver.di.unimi.it/mobicomp/mostri/getmap.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("NetworkRequestHandler", "getmap.php response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NetworkRequestHandler", "getMapObjectsError: " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(mapObject);
    }

    // Fetch profile info
    public static void getProfile(Context context, JSONObject param, final ServerCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.d("NetworkRequestHandler", "getProfile: Initialized");

        JsonObjectRequest profile = new JsonObjectRequest(
                Request.Method.POST,
                "https://ewserver.di.unimi.it/mobicomp/mostri/getprofile.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("NetworkRequestHandler", "getprofile.php response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NetworkRequestHandler", "getProfile Error: " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(profile);
    }

    // Fetch Session ID
    public static void getSessionID(Context context, final ServerCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.d("NetworkRequestHandler", "getSessionID: Initialized");

        JsonObjectRequest profile = new JsonObjectRequest(
                Request.Method.POST,
                "https://ewserver.di.unimi.it/mobicomp/mostri/register.php",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("NetworkRequestHandler", "register.php response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NetworkRequestHandler", "getSessionID Error: " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(profile);
    }

    // Fetch mapObject img, returns Base64 img (as JSONObject, string)
    public static void getObjectImg(Context context, final JSONObject param, final ServerCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.d("NetworkRequestHandler", "getObjectImg: Initialized");

        JsonObjectRequest mapObject = new JsonObjectRequest(
                Request.Method.POST,
                "https://ewserver.di.unimi.it/mobicomp/mostri/getimage.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("NetworkRequestHandler", "getimage.php response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NetworkRequestHandler", "getObjectImg Error: " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(mapObject);
    }

    // Update profile: to be used with fightEat or on UserProfileFragment
    public static void setProfile(final Context context, final JSONObject param, final ServerCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.d("NetworkRequestHandler", "setProfile: Initialized");

        JsonObjectRequest mapObject = new JsonObjectRequest(
                Request.Method.POST,
                "https://ewserver.di.unimi.it/mobicomp/mostri/setprofile.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("NetworkRequestHandler", "setprofile.php response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if(param.getString("img")!= null){
                       Toast.makeText(context, "Image is too big!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("NetworkRequestHandler", "setProfile Error: " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(mapObject);
    }

    // Fight mobs or eat candies
    public static void fightEat(Context context, final JSONObject param, final ServerCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.d("NetworkRequestHandler", "fightEat: Initialized");

        JsonObjectRequest mapObject = new JsonObjectRequest(
                Request.Method.POST,
                "https://ewserver.di.unimi.it/mobicomp/mostri/fighteat.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("NetworkRequestHandler", "fighteat.php response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NetworkRequestHandler", "fightEat Error: " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(mapObject);
    }

    // Get the best 20 users
    public static void getLeaderboard(Context context, final JSONObject param, final ServerCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.d("NetworkRequestHandler", "getLeaderboard: Initialized");
        JsonObjectRequest mapObject = new JsonObjectRequest(
                Request.Method.POST,
                "https://ewserver.di.unimi.it/mobicomp/mostri/ranking.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("NetworkRequestHandler", "ranking.php response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NetworkRequestHandler", "getLeaderboard Error: " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(mapObject);
    }
}


