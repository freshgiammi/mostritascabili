package com.example.mostritascabili;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MapObjectModel{
    private static final MapObjectModel ourInstance = new MapObjectModel();
    private ArrayList<MapObject> mapObject;

    public static MapObjectModel getInstance(){return ourInstance;}

    private MapObjectModel(){
        mapObject = new ArrayList<MapObject>();
    }

    // Populate model with mapObjects
    public void populate(JSONObject response){
        try {
            JSONArray mapObjectsArray = response.getJSONArray("mapobjects");
            for (int i = 0; i < mapObjectsArray.length(); i++) {
                JSONObject current = mapObjectsArray.getJSONObject(i);
                Gson gson = new Gson();

                MapObject mapObjectData = gson.fromJson(current.toString(), MapObject.class);
                Log.d("MapObjectModel", "Populating model with: "+mapObjectData.toString());
                mapObject.add(mapObjectData);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Create JsonElement of a given mapObject, used to store data inside Symbols
    //Todo: Improve conversion method
    public JsonElement mapObjectJSON(MapObject mapObject) throws JSONException {
      JSONObject object = new JSONObject();
      object.put("id",mapObject.getId());
      object.put("lat",mapObject.getLat());
      object.put("lon",mapObject.getLon());
      object.put("type",mapObject.getType());
      object.put("size",mapObject.getSize());
      object.put("name",mapObject.getName());
      Gson gson = new Gson();
      JsonElement element = gson.fromJson(object.toString(), JsonElement.class);
      return element;
    }

    // Return all mapObjects
    public ArrayList<MapObject> getMapObjects() {
        return mapObject;
    }

}