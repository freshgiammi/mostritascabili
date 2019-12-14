package com.example.mostritascabili;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class LegendBottomFragment extends BottomSheetDialogFragment {

    public LegendBottomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
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

        View view = inflater.inflate(R.layout.fragment_legend_bottom, container, false);
        return view;
    }
}
