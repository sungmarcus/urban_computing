package com.example.sungm.urbanapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sungm.urbanapp.objects.LuasPoints;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.utils.ColorUtils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LuasPoints luasPoints;
    private CircleManager circleManager;
    private Context mContext;
    // Get the widgets reference from XML layout
    private ConstraintLayout mLayout;
    private LocationComponent locationComponent;
    Point userLocation;
    private NearbyStaton nearbyStaton;

    private PopupWindow mPopupWindow;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    final String userID = "marcus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        // Get the activity
        nearbyStaton= new NearbyStaton("",false);
        Mapbox.getInstance(this,
                getString(R.string.access_token));
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mLayout = findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        InputStream inputStream = this.getResources().openRawResource(R.raw.luas_stops);
        String jsonString = readJsonFile(inputStream);
        Gson gson = new Gson();
        luasPoints = gson.fromJson(jsonString, LuasPoints.class);
        Toast.makeText(this, luasPoints.getFeatures().get(0).getProperties().getName(), Toast.LENGTH_SHORT).show();
//        addStationDB();




    }

    @SuppressLint("MissingPermission")
    private void checkLocation() {
        LatLng currentLocation = new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
        String nearStation="";
        LatLng station;
        for(int i=0;i<luasPoints.getFeatures().size();i++){
            station = new LatLng(luasPoints.getFeatures().get(i).getGeometry().getCoordinates().get(1),luasPoints.getFeatures().get(i).getGeometry().getCoordinates().get(0));
            Double distance = currentLocation.distanceTo(station);
            if(distance <=20){
                nearStation = luasPoints.getFeatures().get(i).getProperties().getName();
            }
        }

        if (nearStation==""){
            userLocation=Point.fromLngLat(currentLocation.getLongitude(),currentLocation.getLatitude());
            if(nearbyStaton.isNear) {
                setNumber(nearbyStaton.name,"decrease");
                nearbyStaton.clear();
            }
        }else{
            if(nearbyStaton.isNear) {
                if (!(nearStation.equals(nearbyStaton.name))) {
                    setNumber(nearbyStaton.name, "decrease");
                    nearbyStaton = new NearbyStaton(nearStation, true);
                }
            }else{
                nearbyStaton.setName(nearStation);
                nearbyStaton.setNear(true);
                setNumber(nearbyStaton.name, "increase");
            }
        }


    }

    public void setNumber(String station,String operation) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoreRef = rootRef.child(station);
       if(operation.equals("increase")){
           scoreRef.child(userID).setValue("here");
       }else{
           scoreRef.child(userID).setValue(null);
       }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap nmapboxMap) {
        this.mapboxMap = nmapboxMap;
        mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                circleManager = new CircleManager(mapView, mapboxMap, mapboxMap.getStyle());
                circleManager.addClickListener(circle -> {
                   // Toast.makeText(MapActivity.this, String.format("Stop: %s", luasPoints.getFeatures().get((int) circle.getId()).getProperties().getName()), Toast.LENGTH_SHORT).show();
                    int index = (int) circle.getId();
                    onClickCircle(index);
                });
                //circleManager.addLongClickListener();

                List<CircleOptions> circleOptionsList = new ArrayList<>();
                for (int i = 0; i < luasPoints.getFeatures().size() - 1; i++) {
                    circleOptionsList.add(new CircleOptions()
                            .withLatLng(new LatLng(luasPoints.getFeatures().get(i).getGeometry().getCoordinates().get(1),
                                    luasPoints.getFeatures().get(i).getGeometry().getCoordinates().get(0)))
                            .withCircleColor(String.valueOf(ColorUtils.colorToRgbaString(Color.YELLOW)))
                            .withCircleRadius(8f)
                            .setDraggable(false)
                    );
                }
                circleManager.create(circleOptionsList);
                FloatingActionButton fab = findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkLocation();
                    }
                });
            }
        });
    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
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

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(this, loadedMapStyle);

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
            userLocation = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),locationComponent.getLastKnownLocation().getLatitude());
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }


    private String readJsonFile(InputStream inputStream) {
// TODO Auto-generated method stub
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte bufferByte[] = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(bufferByte)) != -1) {
                outputStream.write(bufferByte, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

    private void onClickCircle(int index){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.pop_up,null);

        mPopupWindow = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21

            mPopupWindow.setElevation(5.0f);


        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation(mLayout, Gravity.CENTER,0,0);
    }

    private void addStationDB(){
        List<com.example.sungm.urbanapp.objects.Feature> luasStations = luasPoints.getFeatures();
        Map<String, Station> stations = new HashMap<>();
        String name;
        for(int i=0;i<luasStations.size();i++){
            name = luasStations.get(i).getProperties().getName();
            stations.put(name,new Station(0));
        }
        ref.setValue(stations);
    }

    public static class Station{
        public int numberOfPeople;
        public Station(int numberOfPeople){
            this.numberOfPeople=numberOfPeople;
        }
    }

    public static class NearbyStaton{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isNear() {
            return isNear;
        }

        public void setNear(boolean near) {
            isNear = near;
        }

        private boolean isNear;
        public NearbyStaton(String name,boolean isNear){
            this.name=name;
            this.isNear=isNear;
        }

        public void clear(){
            this.name = "";
            this.isNear= false;
        }
    }


}

