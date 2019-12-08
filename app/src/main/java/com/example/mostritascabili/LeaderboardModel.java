package com.example.mostritascabili;

import android.util.Log;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * LeaderboardModel
 * Model for Profiles. Holds only the best 20 profiles.
 */

public class LeaderboardModel{
    private static final LeaderboardModel ourInstance = new LeaderboardModel();
    private ArrayList<Profile> profiles;

    public static LeaderboardModel getInstance(){return ourInstance;}

    private LeaderboardModel(){
        profiles = new ArrayList<Profile>();
    }

    public void populate(JSONObject response){
        try {
            clearAll();
            JSONArray leaderboardArray = response.getJSONArray("ranking");
            for (int i = 0; i < leaderboardArray.length(); i++) {
                JSONObject current = leaderboardArray.getJSONObject(i);
                Gson gson = new Gson();

                Profile userData = gson.fromJson(current.toString(), Profile.class);
                Log.d("ProfileModel", "Populating leaderboardModel with: " + userData.toString());
                profiles.add(userData);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    // Clears model
    public void clearAll(){
        profiles.clear();
    }

    public ArrayList<Profile> getProfile() {
        return profiles;
    }

    public Profile getProfileByIndex (int i){
        return profiles.get(i);
    }

}