package com.example.mostritascabili;

import android.util.Log;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class MapObjectModel{
    private static final MapObjectModel ourInstance = new MapObjectModel();
    private ArrayList<MapObject> mapObject;

    public static MapObjectModel getInstance(){return ourInstance;}

    private MapObjectModel(){
        mapObject = new ArrayList<MapObject>();
    }

    public void populate(JSONObject response){
        try {
            JSONArray mapObjectsArray = response.getJSONArray("mapobjects");
            for (int i = 0; i < mapObjectsArray.length(); i++) {
                JSONObject current = mapObjectsArray.getJSONObject(i);
                Gson gson = new Gson();

                MapObject mapObjectData = gson.fromJson(current.toString(), MapObject.class);
                Log.d("MapObjectModel.populate", mapObjectData.toString());
                mapObject.add(mapObjectData);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<MapObject> getMapObjects() {
        return mapObject;
    }

}