package com.example.mostritascabili;

import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;

public class ProfileModel{
    private static final ProfileModel ourInstance = new ProfileModel();
    private ArrayList<Profile> user;

    public static ProfileModel getInstance(){return ourInstance;}

    private ProfileModel(){
        user = new ArrayList<Profile>();
    }

    public void populate(JSONObject response){
        try {
                Gson gson = new Gson();

                Profile userData = gson.fromJson(response.toString(), Profile.class);
                Log.d("ProfileModel", "Populating model with: "+userData.toString());
                user.add(userData);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<Profile> getProfile() {
        return user;
    }

}