package com.example.mostritascabili;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Leaderboard extends Fragment {


    public Leaderboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Setup a RecyclerView with an empty Adapter, updated inside ServerCallback.onSuccess
        View view = inflater.inflate(R.layout.fragment_leaderboard,container,false);
        RecyclerView recyclerView =view.findViewById(R.id.recyclerView);
        final LeaderboardAdapter adapter = new LeaderboardAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

       //GET SESSION JSON OBJECT FROM BUNDLE
        final SharedPreferences storedSessionID = getActivity().getSharedPreferences("session_id", MODE_PRIVATE);
        String session_id = storedSessionID.getString("session_id", null);
        JSONObject sessionIdObject = new JSONObject();
        try {
            sessionIdObject.put("session_id", session_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkRequestHandler.getLeaderboard(getActivity().getApplicationContext(), sessionIdObject, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                LeaderboardModel.getInstance().populate(response);
                Log.d("model",LeaderboardModel.getInstance().getProfile().toString());
                adapter.notifyDataSetChanged(); // Update the set of data inside the Adapter.
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

}
