package com.example.sistemas.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private final int MY_PERMISSIONS_REQUEST_CAMARA=2;
    private final int MY_PERMISSIONS_REQUEST_GPS=3;
    private final int MY_PERMISSIONS_REQUEST_GALERY=4;
    private static final String TAG = " MainActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Acceso a contactos!", Toast.LENGTH_LONG).show();
                    StartActivity(ListContacts.class);
                } else {

                    //Toast.makeText(this, "Funcionalidad Limitada!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void StartActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);

    }


    private void requestPermission(Activity context, String permiso, String justificacion, int idCode, Class activity){


        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast.makeText(context, justificacion, Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, permiso  + "ok");
            ActivityCompat.requestPermissions(context, new String [] {permiso}, idCode);
        }
        else
            StartActivity(activity);

    }
    private void askPermissionContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                // Show an expanation to the user *asynchronouslyÂ  Â
                Toast.makeText(this, "Se necesita el permiso para poder mostrar los contactos!", Toast.LENGTH_LONG).show();
            }
            // Request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    public void ContactsButtonAction(View v){
        requestPermission(this, Manifest.permission.READ_CONTACTS, "Es necesario acceder a los" +
                "contactos para el buen funcionamiento de la app",MY_PERMISSIONS_REQUEST_READ_CONTACTS, ListContacts.class);

    }
    public void CameraButtonAction (View v){
        requestPermission(this, Manifest.permission.CAMERA, "ES NECESARIO EL ACCESO A LA CAMARA " +
                "PARA TOMAR FOTOGRAFIAS", MY_PERMISSIONS_REQUEST_CAMARA, PickImageActivity.class);
        requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, "ES NECESARIO EL ACCESO A LA CAMARA " +
                "PARA TOMAR FOTOGRAFIAS", MY_PERMISSIONS_REQUEST_GALERY, PickImageActivity.class);
    }

    public void GpsButtonAction(View v){
        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Es necesario Acceder a la ubicacion" +
                "para calcular la distancia" ,MY_PERMISSIONS_REQUEST_GPS , MapsActivity.class);
    }

}
