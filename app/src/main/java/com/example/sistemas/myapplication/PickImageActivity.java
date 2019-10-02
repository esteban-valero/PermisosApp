package com.example.sistemas.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PickImageActivity extends AppCompatActivity {

    private static final int IMAGE_PICKER_REQUEST = 5;
    private static final int REQUEST_IMAGE_CAPTURE = 6;
    private ImageView image ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image);
        image = (ImageView) findViewById(R.id.imageView);

    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case IMAGE_PICKER_REQUEST :
                if (resultCode == RESULT_OK && requestCode == IMAGE_PICKER_REQUEST){
                    Uri uri = data.getData();
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        image.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            case REQUEST_IMAGE_CAPTURE:
                if( resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    image.setImageBitmap(imageBitmap);
                }

        }

    }

    public void SeleccionarImagen(View v){
        Intent pickImage= new Intent(Intent.ACTION_GET_CONTENT);
        pickImage.setType("image/*");
        startActivityForResult(Intent.createChooser(pickImage, "Seleccione imagen "), IMAGE_PICKER_REQUEST);
    }

    public  void TakePicture(View v){
        Intent takePictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureintent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureintent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
