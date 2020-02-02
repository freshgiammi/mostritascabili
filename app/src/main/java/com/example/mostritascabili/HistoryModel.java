package com.example.mostritascabili;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryModel {
    private static final HistoryModel ourInstance = new HistoryModel();
    private ArrayList<HistoryMob> mobs;

    public static HistoryModel getInstance(){return ourInstance;}

    private HistoryModel(){
        mobs = new ArrayList<HistoryMob>();
    }

    public void populate(JSONObject response){
        try {
            clearAll();
            JSONArray historyArray = response.getJSONArray("history");
            for (int i = 0; i < historyArray.length(); i++) {
                JSONObject current = historyArray.getJSONObject(i);
                Gson gson = new Gson();

                HistoryMob historyMob = gson.fromJson(current.toString(), HistoryMob.class);
                Log.d("HistoryModel", "Populating HistoryModel with: " + historyMob.toString());
                mobs.add(historyMob);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    // Clears model
    public void clearAll(){
        mobs.clear();
    }

    public ArrayList<HistoryMob> getHistory() {
        return mobs;
    }

    public HistoryMob getMobById(int id) {
        for (HistoryMob mob : mobs){
            if (id == mob.getObject_id())
                return mob;
        }
        return null; //Method requires return value if no Object_ID is found
    }

    public void addImg(int id, String img){
        for (HistoryMob mob : mobs){
            if (id == mob.getObject_id())
                mob.setImg(img);
        }
    }

    public HistoryMob getMobByIndex (int i){
        return mobs.get(i);
    }

}
