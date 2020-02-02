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

import java.util.ArrayList;

public class History extends AppCompatActivity {
    String session_id;
    JSONObject sessionIdObject;
    JSONObject param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MaterialToolbar toolbar =  findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final SharedPreferences storedSessionID = getSharedPreferences("session_id", MODE_PRIVATE);
        session_id = storedSessionID.getString("session_id", null);
        final JSONObject sessionIdObject = new JSONObject();
        try {
            sessionIdObject.put("session_id", session_id);
            History.this.sessionIdObject = sessionIdObject;
            param = sessionIdObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Setup a RecyclerView with an empty Adapter, updated inside ServerCallback.onSuccess
        RecyclerView recyclerView = findViewById(R.id.recyclerViewHistory);
        final HistoryAdapter adapter = new HistoryAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NetworkRequestHandler.getHistory(this, sessionIdObject, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                HistoryModel.getInstance().populate(response);
                ArrayList<HistoryMob> array = HistoryModel.getInstance().getHistory();

                for (final HistoryMob mob : array){
                    try {
                        param.put("target_id",mob.getObject_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NetworkRequestHandler.getObjectImg(History.this, param, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                HistoryModel.getInstance().addImg(mob.getObject_id(),response.getString("img"));
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
