package com.appinventiv.searchplacesusinggooglemap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceDetailActivity extends AppCompatActivity {

    private ImageView imgShowPlaceImage;
    private TextView tvPlaceName, tvPlaceAddress;
    public static final String TAG = "PlaceDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        imgShowPlaceImage = findViewById(R.id.img_place_pic);
        tvPlaceName = findViewById(R.id.tv_place_name);
        tvPlaceAddress = findViewById(R.id.tv_place_address);
        String name = getIntent().getStringExtra("NAME");
        String placeAddress = getIntent().getStringExtra("Address");
        String photoURL = getIntent().getStringExtra("PhotoURL");
        tvPlaceName.setText(name);
        tvPlaceAddress.setText(placeAddress);
        Log.d(TAG, "Image URL  "+ photoURL);

    }
}
