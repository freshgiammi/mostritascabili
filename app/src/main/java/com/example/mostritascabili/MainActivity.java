package com.example.mostritascabili;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 * Holds the main activity of Mostri Tascabili.
 */

public class MainActivity extends AppCompatActivity implements Style.OnStyleLoaded, OnMapReadyCallback, PermissionsListener {

    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FloatingActionButton centerFAB;
    private String session_id;
    private ProgressBar lp;
    private TextView xp;
    private JSONObject sessionIdObject;
    private SymbolManager symbolManager = null;
    private OnSymbolClickListener onSymbolClickListener;
    private Handler mapHandler;
    private Runnable mapUpdater;
    private Integer updaterInterval = 300000; // 5 Minutes
    final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnJlc2hnaWFtbWkiLCJhIjoiY2szOHpjcjgzMGNweDNubmN0OGpzN2NmdiJ9.WR7W60fkc9bJEZx1pAlrJw");
        setContentView(R.layout.activity_main);


        // UI INITIALIZATION

        //Used for Edge-to-Edge gestures
        /*
        final View decorView = findViewById(R.id.coordinatorLayout);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ViewCompat.setOnApplyWindowInsetsListener(decorView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                v.setPadding(0,0,0,insets.getSystemWindowInsetBottom());
                return insets.consumeSystemWindowInsets();
            }
        });
        */

        //WORKAROUND: Edge to edge doesn't work well with MapBox, fake it with transparency and a white navbar.
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.carbon));

        // Manage BottomAppBar and fragments
        BottomAppBar bar = findViewById(R.id.bar);
        setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());
            }
        });

        lp = findViewById(R.id.user_lp);
        xp = findViewById(R.id.user_xp);
        lp.setProgress(ProfileModel.getInstance().getProfile().getLp());
        xp.setText(String.valueOf(ProfileModel.getInstance().getProfile().getXp()));

        // ENDING UI INITIALIZATION

        // Acquire session_id for future references and update sessionIdObject
        final SharedPreferences storedSessionID = getSharedPreferences("session_id", MODE_PRIVATE);
        session_id = storedSessionID.getString("session_id", null);
        JSONObject sessionIdObject = new JSONObject();
        try {
            sessionIdObject.put("session_id", session_id);
            MainActivity.this.sessionIdObject = sessionIdObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        centerFAB = findViewById(R.id.centerCameraFAB);
        centerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = 0;
                double lon = 0;
                if (mapboxMap.getLocationComponent().getLastKnownLocation() != null) {
                    lat = mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude();
                    lon = mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude();
                }
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lat, lon))
                        .zoom(15)
                        .tilt(10)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);
                Toast.makeText(getApplicationContext(), "Camera centered!", Toast.LENGTH_SHORT).show();

                //Make FAB disappear after 1000ms, exactly when the animation stops running to avoid conflicts with CameraMoveListener
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        centerFAB.hide();
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        mapboxMap.getUiSettings().setCompassMargins(0,100,40,0); // Set margin for translucent status bar
        mapboxMap.setStyle(Style.LIGHT,this);
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        enableLocationComponent(style);

        //Show centerFAB if camera isn't centered
        mapboxMap.addOnCameraMoveStartedListener(new MapboxMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                CameraPosition cameraPosition = mapboxMap.getCameraPosition();
                // mapboxMap can't hold his shit when rotated. To avoid NPE crashes, check against mapboxMap.getLocationComponent().getLastKnownLocation()
                if (cameraPosition.target.getLatitude() != 0 && mapboxMap.getLocationComponent().getLastKnownLocation() != null){
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()))
                            .zoom(15)
                            .tilt(10)
                            .build();
                    if (!cameraPosition.equals(position))
                        centerFAB.show();
                }
            }
        });

        //Put symbols on the map
        symbolManager = new SymbolManager(mapView, mapboxMap, style);
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconIgnorePlacement(true);

        mapboxMap.getStyle().addImage("donut", BitmapFactory.decodeResource(getResources(), R.drawable.donut));
        mapboxMap.getStyle().addImage("lollipop", BitmapFactory.decodeResource(getResources(), R.drawable.lollipop));
        mapboxMap.getStyle().addImage("candy", BitmapFactory.decodeResource(getResources(), R.drawable.candy));
        mapboxMap.getStyle().addImage("dragon", BitmapFactory.decodeResource(getResources(), R.drawable.dragon));
        mapboxMap.getStyle().addImage("goblin", BitmapFactory.decodeResource(getResources(), R.drawable.goblin));
        mapboxMap.getStyle().addImage("cthulhu", BitmapFactory.decodeResource(getResources(), R.drawable.cthulhu));

        //Display symbols on first map generation
        showSymbolsOnMap(symbolManager);

        //Update map every minute
        mapHandler = new Handler();
        mapUpdater = new Runnable() {
            @Override
            public void run() {
                try{
                    NetworkRequestHandler.getMapObjects(MainActivity.this, sessionIdObject, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                                symbolManager.removeClickListener(onSymbolClickListener);
                                showSymbolsOnMap(symbolManager);
                                Log.d("mapUpdater", "Successfully updated symbols in map.");
                        }
                    });
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
                finally{
                    mapHandler.postDelayed(this, updaterInterval);
                }
            }
        };
        mapHandler.postDelayed(mapUpdater, updaterInterval);

    }

    /* Display user location icon */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            locationComponent.zoomWhileTracking(15);
            locationComponent.tiltWhileTracking(10);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "We need access to your location in order to work!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "We need access to your location in order to work!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // Display mapObjects in map, with their correspective JSON Data bundled inside.
    public void showSymbolsOnMap (final SymbolManager symbolManager){
        // Make sure we have the lastest map available.
        NetworkRequestHandler.getMapObjects(MainActivity.this, sessionIdObject, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                MapObjectModel.getInstance().clearAll(); // Clear current mapObjectModel
                MapObjectModel.getInstance().populate(response); // Update mapObjectModel
                symbolManager.deleteAll(); // Clear symbolManager.
                for( MapObject obj : MapObjectModel.getInstance().getMapObjects()) {
                    String symbolIcon = null;
                    switch(obj.getType()) {
                        case "MO":
                            switch(obj.getSize()) {
                                case "S":
                                    symbolIcon = "goblin";
                                    break;
                                case "M":
                                    symbolIcon = "dragon";
                                    break;
                                case "L":
                                    symbolIcon = "cthulhu";
                                    break;
                            }
                            break;
                        case "CA":
                            switch(obj.getSize()) {
                                case "S":
                                    symbolIcon = "candy";
                                    break;
                                case "M":
                                    symbolIcon = "lollipop";
                                    break;
                                case "L":
                                    symbolIcon = "donut";
                                    break;
                            }
                            break;
                    }
                    try {
                        symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(obj.getLat(), obj.getLon()))
                                .withIconImage(symbolIcon)
                                .withIconSize(0.09f)
                                .withData(MapObjectModel.getInstance().mapObjectJSON(obj)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Create onSymbolClickListener so that we can destroy it when regenerating
                onSymbolClickListener = new OnSymbolClickListener() {
                    @Override
                    public void onAnnotationClick(final Symbol symbol) {
                        final MapObject symbolData = gson.fromJson(symbol.getData().getAsJsonObject().toString(), MapObject.class);

                        JSONObject param = sessionIdObject;
                        try {
                            param.put("target_id",symbolData.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Location userLocation = new Location("");
                        userLocation.setLatitude(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                        userLocation.setLongitude(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude());
                        Location mobLocation = new Location("");
                        mobLocation.setLatitude(symbolData.getLat());
                        mobLocation.setLongitude(symbolData.getLon());
                        float distanceInMeters = userLocation.distanceTo(mobLocation);
                        final Boolean enabled;
                        if (distanceInMeters > 50)
                            enabled = false;
                        else
                            enabled = true;

                        NetworkRequestHandler.getObjectImg(MainActivity.this, param, new ServerCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                try {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("img",response.getString("img"));
                                    bundle.putString("obj",symbol.getData().getAsJsonObject().toString()); // put JSONObject as string, we will regenerate inside fragment
                                    bundle.putBoolean("enabled", enabled);
                                    MobInteractionFragment mobInteractionFragment = new MobInteractionFragment();
                                    mobInteractionFragment.setArguments(bundle);
                                    mobInteractionFragment.show(getSupportFragmentManager(), mobInteractionFragment.getTag());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                };
                symbolManager.addClickListener(onSymbolClickListener); // Add listener to symbolManager (clear on each update)
                Log.d("showSymbolsOnMap", "Map generated. "+MapObjectModel.getInstance().getMapObjects().size() +" mapObjects generated." );
            }
        });
    }

    public void fightEat(final MapObject symbolData){

        // We need to pass symbolManager and onSymbolClickListener because we need to destroy the listener before updating the map.
        // Since it can't be successfully destroyed in showSymbolsOnMap (which is the method that creates it), lets move the
        // logic to right before recreating the symbol map.

        symbolManager.removeClickListener(onSymbolClickListener);

        NetworkRequestHandler.getMapObjects(MainActivity.this, sessionIdObject, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                MapObjectModel.getInstance().clearAll(); // Clear current mapObjectModel
                MapObjectModel.getInstance().populate(response); // Update mapObjectModel
                ArrayList<MapObject> newObjects = MapObjectModel.getInstance().getMapObjects();
                if (newObjects.contains(symbolData)) {
                    // Item is still inside the received mapObjects. We can fight/eat.
                    Log.d("showSymbolsOnMap", "mapObject found in new request. Fight/eat stage initialized.");
                    JSONObject param = sessionIdObject;
                    try {
                        param.put("target_id",symbolData.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("showSymbolsOnMap",param.toString());
                    //Initialize fight/eat sequence
                    NetworkRequestHandler.fightEat(MainActivity.this, param, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                switch (symbolData.getType()){
                                    case "CA":
                                        int  acquiredLp = response.getInt("lp") - ProfileModel.getInstance().getProfile().getLp();
                                        Toast.makeText(getApplicationContext(), "Candy eaten! " +acquiredLp +" LP restored!"  , Toast.LENGTH_SHORT).show();
                                        break;
                                    case "MO":
                                        if (response.getBoolean("died")) {
                                            Toast.makeText(getApplicationContext(), "You're dead! Be careful next time. Starting over!", Toast.LENGTH_SHORT).show();
                                            // Reload activity
                                            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                        }
                                        else{
                                            int acquiredXp= response.getInt("xp") - ProfileModel.getInstance().getProfile().getXp();
                                            Toast.makeText(getApplicationContext(), "Good fight! You acquired "+acquiredXp+" XP!", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                }
                                NetworkRequestHandler.getProfile(MainActivity.this, sessionIdObject, new ServerCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        ProfileModel.getInstance().clearAll();
                                        ProfileModel.getInstance().populate(response);
                                        lp.setProgress(ProfileModel.getInstance().getProfile().getLp());
                                        xp.setText(String.valueOf(ProfileModel.getInstance().getProfile().getXp()));
                                        showSymbolsOnMap(MainActivity.this.symbolManager);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    // Item is missing from the received mapObjects. We can assume it's destroyed, so reload the map symbols and notify the user.
                    Log.d("mapUpdater", "mapObject not found in new request. Update map accordingly.");
                    switch(symbolData.getType()) {
                        case "MO":
                            Toast.makeText(getApplicationContext(), "Ops! It looks like this monster has already been beaten by someone else.", Toast.LENGTH_SHORT).show();
                            break;
                        case "CA":
                            Toast.makeText(getApplicationContext(), "Ops! It looks like this candy has already been eaten by someone else.", Toast.LENGTH_SHORT).show() ;
                            break;
                    }
                    showSymbolsOnMap(MainActivity.this.symbolManager);
                }
            }
        });
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        //Todo: update symbols on map
        if (mapHandler != null)
            mapHandler.postDelayed(mapUpdater,updaterInterval);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (mapHandler != null)
            mapHandler.removeCallbacks(mapUpdater);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (mapHandler != null)
            mapHandler.removeCallbacks(mapUpdater);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (mapHandler != null)
            mapHandler.removeCallbacks(mapUpdater);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Sei sicuro di voler uscire?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}