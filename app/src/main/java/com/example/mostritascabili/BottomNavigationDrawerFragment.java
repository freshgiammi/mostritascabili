package com.example.mostritascabili;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Google says that a BottomSheetDialog not being expanded is the expected behaviour. On landscape though,
         *  this looks kinda weird. Override the onShow method of the dialog to set the BottomSheetBehaviour to STATE_EXPANDED.
         *  Note that this still isn't perfect, as sliding the menu down could put it in a STATE_COLLAPSED state.
         * */
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

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
                        Intent i = new Intent(getActivity(), Leaderboard.class);
                        startActivity(i);
                        break;
                }
                return false;
            }
        });
        return view;
    }

}
