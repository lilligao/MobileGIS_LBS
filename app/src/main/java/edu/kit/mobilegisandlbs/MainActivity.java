package edu.kit.mobilegisandlbs;


import android.Manifest;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {
    int PERMISSION_ID = 44;
    // initializing
    // FusedLocationProviderClient
    // object
    // FusedLocationProviderClient mFusedLocationClient;


    @RequiresApi(api = Build.VERSION_CODES.O) // Denotes that the annotated element should only be called on the given API
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView provider = findViewById(R.id.provider);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerNames = locationManager.getAllProviders();
        StringBuilder stringBuilder = new StringBuilder();
        for (String providerName : providerNames) {
            stringBuilder.append(providerName).append("\n");
        }
        provider.setText(stringBuilder.toString());
        // get text field by id
        TextView text = findViewById(R.id.location_coord);
        // Create location services client
        // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // method to get the location
        // getLastLocation();

        // check if location is enabled
        if (isLocationEnabled(locationManager)) {
            Location location = requestNewLocationData(locationManager);

            if (location == null) {
                location = requestNewLocationData(locationManager);
            }
            // getAccuracy (): Returns the estimated horizontal accuracy radius in meters of this location at the 68th percentile confidence level.
            // getVerticalAccuracyMeters (): Returns the estimated altitude accuracy in meters of this location at the 68th percentile confidence level.
            text.setText("GPS Provider Location\nLatitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude() + "\nAltitude: " + location.getAltitude() + "\nHorizontal accuracy radius: " + location.getAccuracy() + "m\nAltitude accuracy: " + location.getVerticalAccuracyMeters() + "m");
        } else {
            // A toast provides simple feedback about an operation in a small popup.
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            // An intent is an abstract description of an operation to be performed.
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

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