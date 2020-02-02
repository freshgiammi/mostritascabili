package com.example.mostritascabili;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

public class HistoryMobDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_mob_details);

        MaterialToolbar toolbar =  findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Monster History");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        String img = extras.getString("img");
        int id = extras.getInt("id");
        int times = extras.getInt("times");

        Log.d("AAAA",img);
        Log.d("AAAA",String.valueOf(id));
        Log.d("AAAA",String.valueOf(times));

    }
}
