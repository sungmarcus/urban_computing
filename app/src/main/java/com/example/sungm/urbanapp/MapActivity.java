package com.example.sungm.urbanapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sungm.urbanapp.objects.LuasPoints;
import com.google.gson.Gson;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener,MapboxMap.OnMapClickListener {
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private Style style ;
    private String geoJsonString;
    private List<Feature> markerCoordinates;
    private LuasPoints luasPoints;
    private CircleManager circleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this,
                getString(R.string.access_token));
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        InputStream inputStream = this.getResources().openRawResource(R.raw.luas_stops);
        String jsonString = readJsonFile(inputStream);
        Gson gson = new Gson();
        luasPoints = gson.fromJson(jsonString, LuasPoints.class);

    }

    @Override
    public void onMapReady(@NonNull MapboxMap nmapboxMap) {
        this.mapboxMap = nmapboxMap;
        style = mapboxMap.getStyle();




        mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                FeatureCollection featureCollection = FeatureCollection.fromJson(geoJsonString);
                markerCoordinates = featureCollection.features();
//                style.addSource(new GeoJsonSource("marker-source",
//                        FeatureCollection.fromFeatures(markerCoordinates)));
//
//// Add the marker image to map
//                style.addImage("my-marker-image", BitmapFactory.decodeResource(
//                        MapActivity.this.getResources(), R.drawable.blue_marker));
//
//// Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
//// middle of the icon being fixed to the coordinate point.
//                style.addLayer(new SymbolLayer("marker-layer", "marker-source")
//                        .withProperties(PropertyFactory.iconImage("my-marker-image"),
//                                iconOffset(new Float[]{0f, -9f})));
//
//// Add the selected marker source and layer
//                style.addSource(new GeoJsonSource("selected-marker"));
//
//// Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
//// middle of the icon being fixed to the coordinate point.
//                style.addLayer(new SymbolLayer("selected-marker-layer", "selected-marker")
//                        .withProperties(PropertyFactory.iconImage("my-marker-image"),
//                                iconOffset(new Float[]{0f, -9f})));
//                mapboxMap.addOnMapClickListener(MapActivity.this);


            }
        });

    }

    private void drawStationMarkers() {


//        circleManager = new CircleManager(mapView,mapboxMap, mapboxMap.getStyle());
//        circleManager.addClickListener(circle -> {
//            Toast.makeText(MapActivity.this,String.format("Stop: %s", wayPoints.get((int) circle.getId()+1).getName() ), Toast.LENGTH_SHORT).show();
//
//        });
//        circleManager.addLongClickListener(circle -> Toast.makeText(MapActivity.this,
//                String.format("Station long clicked %s", luasPoints.getFeatures().get((int) circle.getId()+1).getProperties().getName()),
//                Toast.LENGTH_SHORT
//        ).show());
//
//
//        List<CircleOptions> circleOptionsList = new ArrayList<>();
//            circleOptionsList.add(new CircleOptions()
//                    .withLatLng(new LatLng(wayPoints.get(i).getCoordinates().latitude(), wayPoints.get(i).getCoordinates().longitude()))
//                    .withCircleColor(ColorUtils.colorToRgbaString(colour))
//                    .withCircleRadius(10f)
//                    .setDraggable(false)
//            );
//
//        }


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
    @SuppressWarnings( {"MissingPermission"})
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

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(this, loadedMapStyle);

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
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


}
