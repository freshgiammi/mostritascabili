package com.example.mostritascabili;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;


/**
 * MobInteractionFragment
 * This fragment is displayed when the user clicks on a Symbol in the current MapView.
 * Dynamically adapts to JSON data bundled within each Symbol, and displays a button if the
 * candy can be eaten, or if the mob can be defeated.
 */
public class MobInteractionFragment extends BottomSheetDialogFragment {
    private MapObject mapObject;
    private Bitmap img;
    private Boolean enabled;

    public MobInteractionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // BUNDLE
        Bundle bundle = this.getArguments();
        MobInteractionFragment.this.enabled = bundle.getBoolean("enabled");
        byte[] decodedString = Base64.decode(bundle.getString("img"),Base64.DEFAULT);
        MobInteractionFragment.this.img = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        Gson g = new Gson();
        MobInteractionFragment.this.mapObject = g.fromJson(bundle.getString("obj"),MapObject.class);
        Log.d("YOLO",mapObject.toString());
        // BUNDLE

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

        View view = inflater.inflate(R.layout.fragment_mob_interaction,container,false);
        NavigationView navigationView = view.findViewById(R.id.navigation_view_mob);
        Menu menu = navigationView.getMenu();
        MenuItem name = menu.findItem(R.id.mob_name);
        MenuItem size = menu.findItem(R.id.mob_size);
        MenuItem type = menu.findItem(R.id.mob_type);
        ImageView mobImg = view.findViewById(R.id.mob_image);
        MaterialButton button = view.findViewById(R.id.mob_button);
        final String alertDialogText;
        final String alertDialogTitle;
        switch(mapObject.getSize()) {
            case "S":
                size.setTitle("Small");
                break;
            case "M":
                size.setTitle("Medium");
                break;
            case "L":
                size.setTitle("Large");
                break;
        }
        if (mapObject.getType().equals("MO")) {
            type.setTitle("Monster");
            button.setText("Fight!");
        } else {
            type.setTitle("Candy");
            button.setText("Eat!");
        }
        name.setTitle(mapObject.getName());
        mobImg.setImageBitmap(img);
         //if (enabled == false || ProfileModel.getInstance().getProfile().getLp() == 100) {
           // button.setEnabled(false);
            //button.setVisibility(View.GONE);
        //} else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity act = getActivity();
                    if (act instanceof MainActivity)
                        ((MainActivity) act).fightEat(mapObject);
                    dismiss(); // Closes sheet
                }
            });
       // }
        return view;
    }

}
