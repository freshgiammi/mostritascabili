package com.example.mostritascabili;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class MainActivity extends AppCompatActivity implements Style.OnStyleLoaded, OnMapReadyCallback, PermissionsListener {

    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FloatingActionButton centerFAB;
    private String session_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnJlc2hnaWFtbWkiLCJhIjoiY2szOHpjcjgzMGNweDNubmN0OGpzN2NmdiJ9.WR7W60fkc9bJEZx1pAlrJw");
        setContentView(R.layout.activity_main);

        // Acquire session_id for future references
        final SharedPreferences storedSessionID = getSharedPreferences("session_id", MODE_PRIVATE);
        session_id= storedSessionID.getString("session_id", null);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        centerFAB = findViewById(R.id.centerCameraFAB);
        centerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude();
                double lon = mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude();
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lat, lon))
                        .zoom(15)
                        .tilt(10)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);
                Toast.makeText(getApplicationContext(), "Camera centered!", Toast.LENGTH_SHORT).show();
                centerFAB.hide();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.LIGHT,this);

        mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition cameraPosition =  mapboxMap.getCameraPosition();
                    centerFAB.show();
            }
        });
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        enableLocationComponent(style);
        final SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconIgnorePlacement(true);

        mapboxMap.getStyle().addImage("donut", BitmapFactory.decodeResource(getResources(), R.drawable.donut));
        mapboxMap.getStyle().addImage("lollipop", BitmapFactory.decodeResource(getResources(), R.drawable.lollipop));
        mapboxMap.getStyle().addImage("candy", BitmapFactory.decodeResource(getResources(), R.drawable.candy));
        mapboxMap.getStyle().addImage("dragon", BitmapFactory.decodeResource(getResources(), R.drawable.dragon));
        mapboxMap.getStyle().addImage("goblin", BitmapFactory.decodeResource(getResources(), R.drawable.goblin));
        mapboxMap.getStyle().addImage("cthulhu", BitmapFactory.decodeResource(getResources(), R.drawable.cthulhu));

        ArrayList<MapObject> mapObjects = MapObjectModel.getInstance().getMapObjects();
        for( MapObject obj : mapObjects) {
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
        symbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                Log.d("Symbol",symbol.toString());
                Toast.makeText(MainActivity.this, symbol.getData().toString(), Toast.LENGTH_SHORT).show();
            }
        });
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


    // Creates request for object img, to be incapsulated inside request as param
    public JSONObject objectImgRequest(String id) {
        final JSONObject imgReqeust = new JSONObject();
        try {
            imgReqeust.put("session_id", session_id);
            imgReqeust.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imgReqeust;
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
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