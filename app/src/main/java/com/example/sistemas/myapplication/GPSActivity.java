package com.example.sistemas.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;

public class GPSActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private final int REQUEST_CHECK_SETTINGS = 5;
    private final double RADIUS_OF_EARTH_KM = 6371;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private TextView latitud;
    private TextView longitud;
    private TextView elevacion;
    private  TextView distancia;
    private Location mCurrentLocation;
    private JSONArray localizaciones;
    private ScrollView scrollView;
    private TextView posicion;
    private String pocisiones = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        latitud = (TextView) findViewById(R.id.textLatitud);
        longitud = (TextView) findViewById(R.id.textLongitud);
        elevacion = (TextView) findViewById(R.id.textElevacion);
        distancia = findViewById(R.id.textDistancia);
        scrollView = findViewById(R.id.scrollPocisiones);
        posicion = findViewById(R.id.textPosiciones);

        mLocationRequest = createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("LOCATION", "onSusses Location");
                if (location != null) {
                    mCurrentLocation = location;
                    latitud.setText(String.valueOf(location.getLatitude()));
                    longitud.setText(String.valueOf(location.getLongitude()));
                    elevacion.setText(String.valueOf(location.getAltitude()));
                    Log.i(" LOCATION ", "Longitud: " + location.getLongitude());
                    Log.i(" LOCATION ", "Latitud: " + location.getLatitude());
                }

            }
        });
        mLocationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    latitud.setText(String.valueOf(location.getLatitude()));
                    longitud.setText(String.valueOf(location.getLongitude()));
                    elevacion.setText(String.valueOf(location.getAltitude()));
                    distancia.setText(String.valueOf(distance(location.getLatitude(),location.getLongitude(),4.628946, -74.064546))+ "KM");
                }
            }

        };

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(GPSActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {

                        } break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    public void GuardarButonAction(View v){
        writeJSONObject();

    }


    private void writeJSONObject(){
        MyLocation myLocation = new MyLocation();
        Date fecha = new Date(System.currentTimeMillis());
        myLocation.setFecha(fecha);
        myLocation.setLatitud(mCurrentLocation.getLatitude());
        myLocation.setLongitud(mCurrentLocation.getLongitude());

        pocisiones = posicion.getText() + fecha.toString() + " Latitud " + String.valueOf(mCurrentLocation.getLatitude()) + " Longitud " +
                mCurrentLocation.getLongitude() + " ";
        posicion.setText(pocisiones);
        localizaciones.put(myLocation.toJSON());
        Writer output = null;
        String filename= "locations.json";
        try {
            File file = new File(getBaseContext().getExternalFilesDir(null), filename);
            Log.i("LOCATION", "Ubicacion de archivo: "+file);
            output = new BufferedWriter(new FileWriter(file));
            output.write(localizaciones.toString());
            output.close();
            Toast.makeText(getApplicationContext(), "Location saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this,
                    "Sin acceso a localización, hardware deshabilitado!", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback); }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
}
