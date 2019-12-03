package com.example.mostritascabili;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

/**
 * BottomNavigationDrawerFragment
 * This fragment is displayed when the user clicks on the menu in BottomAppBar.
 * Currently can redirect to two activities: UserProfile and Leaderboard
 */
public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {

    public BottomNavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottomnavigation,container,false);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.user:
                        Log.d("onNavigationItemSelected", "User clicked!");
                        break;
                    case R.id.leaderboard:
                        Log.d("onNavigationItemSelected", "Leaderboard clicked!");
                        break;
                }
                return false;
            }
        });
        return view;
    }

}
