package edu.kit.mobilegisandlbs;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int DEFAULT_ZOOM = 14;
    int PERMISSION_ID = 44;
    Circle circle;
    List<Circle> circleList = new ArrayList<>();
    List<LatLng> latLngList = new ArrayList<>();
    Polyline line;
    GoogleMap googleMap;
    // initializing
    // FusedLocationProviderClient
    // object
    // FusedLocationProviderClient mFusedLocationClient;

    // [START_EXCLUDE]
    // [START maps_marker_get_map_async]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        TileProvider wmsTileProvider = TileProviderFactory.getOsgeoWmsTileProvider();
        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(wmsTileProvider));

        getDeviceLocation();
    }


    private void getDeviceLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // check if location is enabled
        if (isLocationEnabled(locationManager)) {
            Location location = requestNewLocationData(locationManager);

            if (location == null) {
                location = requestNewLocationData(locationManager);
            }

            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            latLngList.add(currentPosition);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, DEFAULT_ZOOM));

            circle = googleMap.addCircle(new CircleOptions()
                    .center(currentPosition)
                    .zIndex(100)
                    .radius(40)
                    .strokeColor(Color.argb(255, 80, 132, 255))
                    .fillColor(Color.argb(150, 80, 132, 228)));

            circleList.add(circle);

            line = googleMap.addPolyline(new PolylineOptions()
                    .add(currentPosition)
                    .width(5)
                    .color(Color.RED));

            // [START_EXCLUDE silent]
            googleMap.setOnCameraIdleListener(() -> {
                //circle.setRadius(40*Math.pow(2, 14-googleMap.getCameraPosition().zoom));

                for (int i = 0; i < circleList.size(); i++) {
                    circleList.get(i).setRadius(40*Math.pow(2, 14-googleMap.getCameraPosition().zoom));
                }
            });

            // [END_EXCLUDE]

        } else {
            // A toast provides simple feedback about an operation in a small popup.
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            // An intent is an abstract description of an operation to be performed.
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


    }



    @SuppressLint("MissingPermission")
    private Location requestNewLocationData(LocationManager locationManager) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,//GPS as provider
                5000,//update every 5 sec
                3,//every 1 m
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        latLngList.add(currentPosition);

                        circle = googleMap.addCircle(new CircleOptions()
                                .center(currentPosition)
                                .zIndex(100)
                                .radius(40)
                                .strokeColor(Color.argb(255, 80, 132, 255))
                                .fillColor(Color.argb(150, 80, 132, 228)));

                        circleList.add(circle);
                        line.setPoints(latLngList);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,DEFAULT_ZOOM));


                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d("Latitude", "disable");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d("Latitude", "enable");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d("Latitude", "status");
                    }
                }
        );
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    // method to check if location is enabled
    private boolean isLocationEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



}