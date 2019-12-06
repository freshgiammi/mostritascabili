package com.example.mostritascabili;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

//Todo: add a Top app bar with back button
public class Leaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        //Setup a RecyclerView with an empty Adapter, updated inside ServerCallback.onSuccess
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final LeaderboardAdapter adapter = new LeaderboardAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //GET SESSION JSON OBJECT FROM BUNDLE
        final SharedPreferences storedSessionID = getSharedPreferences("session_id", MODE_PRIVATE);
        String session_id = storedSessionID.getString("session_id", null);
        JSONObject sessionIdObject = new JSONObject();
        try {
            sessionIdObject.put("session_id", session_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkRequestHandler.getLeaderboard(this, sessionIdObject, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                LeaderboardModel.getInstance().populate(response);
                Log.d("model",LeaderboardModel.getInstance().getProfile().toString());
                adapter.notifyDataSetChanged(); // Update the set of data inside the Adapter.
            }
        });
    }

    @Override
    public void onBackPressed() {
        //HACK: Do not show Modal Bottom Sheet when going back
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
