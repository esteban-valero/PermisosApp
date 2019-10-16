package com.example.sistemas.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final int REQUEST_CHECK_SETTINGS = 5;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    private EditText buscar;
    private final double lowerLeftLatitude= 4.485486 ;
    private final double lowerLeftLongitud = -74.099007 ;
    private final double upperRightLatitude = 4.762047;
    private final double upperRightLongitud = -74.067057;
    private final double RADIUS_OF_EARTH_KM = 6371;
    private SensorManager mSensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        buscar = (EditText) findViewById(R.id.Buscar);
        buscar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == keyEvent.ACTION_DOWN){
                    switch (i){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            BuscarDireccion();
                            return true;
                            default: break;
                    }
                }
                return false;
            }
        });

        mLocationRequest = createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {

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
                            resolvable.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {

                        } break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }



    private void BuscarDireccion(){
        Geocoder mGeocoder = new Geocoder(getBaseContext());
        String addressString = buscar.getText().toString();
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        if (!addressString.isEmpty()) {
            try {

                List<Address> addresses = mGeocoder.getFromLocationName(addressString, 2, lowerLeftLatitude, lowerLeftLongitud, upperRightLatitude, upperRightLongitud);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    if (mMap != null) {
                        mMap.addMarker(new MarkerOptions().position(position).title("Marcador destino"));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                        Toast.makeText(MapsActivity.this, "distancia a "+ addressString+ " es de: "+ distance(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude(), addressResult.getLatitude(), addressResult.getLongitude())+"KM", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MapsActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
        }
    }

    private double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.formato_mapa));

        //mSensorManager.registerListener((SensorEventListener) this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_FASTEST);

        // Add a marker in Sydney and move the camera
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("LOCATION", "onSusses Location");
                if (location != null) {
                    mCurrentLocation = location;
                    LatLng bogota = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(bogota).title("Marcador en Bogotá"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
                    Log.i(" LOCATION ", "Longitud: " + location.getLongitude());
                    Log.i(" LOCATION ", "Latitud: " + location.getLatitude());
                }

            }
        });

       /* mLocationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    LatLng bogota = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(bogota).title("Marcador en Bogotá"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
                    //distancia.setText(String.valueOf(distance(location.getLatitude(),location.getLongitude(),4.628946, -74.064546))+ "KM");
                }
            }

        };*/


        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));


    }




    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback); }

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
}
