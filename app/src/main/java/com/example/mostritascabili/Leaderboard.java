package com.example.mostritascabili;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;

public class Leaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        MaterialToolbar toolbar =  findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Leaderboard");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });


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

}
