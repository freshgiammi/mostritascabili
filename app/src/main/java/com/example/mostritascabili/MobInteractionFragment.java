package com.example.mostritascabili;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MobInteractionFragment extends BottomSheetDialogFragment {
    private MapObject mapObject;
    private Bitmap img;
    private Boolean enabled;

    public MobInteractionFragment(MapObject mapObject, String img, Boolean enabled) {
        MobInteractionFragment.this.mapObject = mapObject;
        byte[] decodedString = Base64.decode(img,Base64.DEFAULT);
        MobInteractionFragment.this.img = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        MobInteractionFragment.this.enabled = enabled;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        if (mapObject.getType()=="MO") {
            type.setTitle("Monster");
            button.setText("Fight!");
            alertDialogTitle="Fighting!";
            alertDialogText="Are you sure you want to fight?!";
        }else {
            type.setTitle("Candy");
            button.setText("Eat!");
            alertDialogTitle="Eating!";
            alertDialogText="Are you sure you eat this candy?";
        }
        name.setTitle(mapObject.getName());
        mobImg.setImageBitmap(img);
         if (enabled == false || ProfileModel.getInstance().getProfile().getLp() == 100) {
            button.setEnabled(false);
            button.setVisibility(View.GONE);
        }else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialAlertDialogBuilder(getActivity())
                            .setTitle(alertDialogTitle)
                            .setMessage(alertDialogText)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Activity act = getActivity();
                                    if (act instanceof MainActivity)
                                        ((MainActivity) act).fightEat(mapObject);
                                }
                            })
                            .setNegativeButton("No",null)
                            .show();
                }
            });
        }
        return view;
    }

}
