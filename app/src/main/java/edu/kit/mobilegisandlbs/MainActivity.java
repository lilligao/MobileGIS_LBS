package edu.kit.mobilegisandlbs;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int DEFAULT_ZOOM = 15;
    int PERMISSION_ID = 44;
    LatLng currentPosition;

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
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
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
            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            // A toast provides simple feedback about an operation in a small popup.
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            // An intent is an abstract description of an operation to be performed.
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


        googleMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .title("Current Position"));
        // [START_EXCLUDE silent]
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, DEFAULT_ZOOM));
        // [END_EXCLUDE]
    }

    @SuppressLint("MissingPermission")
    private Location requestNewLocationData(LocationManager locationManager) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,//GPS as provider
                1000,//update every 1 sec
                1,//every 1 m
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(currentPosition)
                                .title("Current Position"));
                        // [START_EXCLUDE silent]
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