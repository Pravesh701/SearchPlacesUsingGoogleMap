package com.appinventiv.searchplacesusinggooglemap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appinventiv.searchplacesusinggooglemap.NearbySearch.Geometry;
import com.appinventiv.searchplacesusinggooglemap.NearbySearch.Location;
import com.appinventiv.searchplacesusinggooglemap.NearbySearch.NearbySearch;
import com.appinventiv.searchplacesusinggooglemap.NearbySearch.Result;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION = 1;
    private final int DEFAULT_ZOOM = 15;
    public static boolean GET_SEARCH = false;
    public static final String TAG = "MapsActivity";
    public static final String BASE_URL = "https://maps.googleapis.com/";
    private static final String API_KEY = "AIzaSyA024ETshHqlwMtT42pKM_DecNeaSSgyNo";
    private Retrofit retrofitNearByPlaces;
    private RequestInterface request;
    private ProgressDialog progressDialog;
    private GoogleMap mMap;
    private FloatingActionButton fabSearchPlace;
    private LatLng latLng = new LatLng(28.6060756, 77.361914);
    private RecyclerView rvNearByPlaces;
    private NearbySearchAdapter nearbySearchAdapter;
    private List<Result> listNearByPlaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_show_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        rvNearByPlaces = findViewById(R.id.rv_show_nearby_place);
        rvNearByPlaces.setLayoutManager(new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.VERTICAL, false));
        listNearByPlaces = new ArrayList<>();
        nearbySearchAdapter = new NearbySearchAdapter(MapsActivity.this, listNearByPlaces);
        rvNearByPlaces.setAdapter(nearbySearchAdapter);
        fabSearchPlace = findViewById(R.id.fab_search_place);
        getPermissions();
        retrofitNearByPlaces = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        request = retrofitNearByPlaces.create(RequestInterface.class);
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Go To Setting on app and enable permission", Toast.LENGTH_SHORT).show();
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION);
            }
        } else {
            fabSearchPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
                    GET_SEARCH = true;
                    startActivity(intent);
                    finish();

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        if (GET_SEARCH){
            replaceMarkerWithSearch(getIntent().getDoubleExtra("lat",28.6060756),
                    getIntent().getDoubleExtra("log", 77.361914));

        }
    }


    private void replaceMarkerWithSearch(Double lat, Double log) {
        latLng = new LatLng(lat, log);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        makeCircle();
        showNearByPlaceList(latLng);
    }

    private void showNearByPlaceList(LatLng latLngLocal){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("NearByPlacesLoading");
        progressDialog.show();
        String latitude = String.valueOf(latLngLocal.latitude);
        String longitude = String.valueOf(latLngLocal.longitude);
        String location = latitude+","+longitude;
        Call<NearbySearch> call = request.getNearByPlaces(location, 1500, "restaurant", API_KEY);
        call.enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearch> call, @NonNull Response<NearbySearch> response) {
                if (response.isSuccessful()){

                    NearbySearch nearbySearch = response.body();
                    if (nearbySearch != null) {
                        listNearByPlaces = nearbySearch.getResults();
                    }
                    nearbySearchAdapter = new NearbySearchAdapter(MapsActivity.this, listNearByPlaces);
                    rvNearByPlaces.setAdapter(nearbySearchAdapter);
                    nearbySearchAdapter.notifyDataSetChanged();
                    addMarkerOnNearByPlaces();
                }
                else {
                    Log.d(TAG, "Not Responding  "+response.toString());
                }
            }
            @Override
            public void onFailure(@NonNull Call<NearbySearch> call, @NonNull Throwable t) {
                Log.d("Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void addMarkerOnNearByPlaces() {
        for (Result result : listNearByPlaces){
            String name = result.getName();
            Geometry geometry = result.getGeometry();
            Location location = geometry.getLocation();
            LatLng latLngLocal = new LatLng(location.getLat(), location.getLng());
            mMap.addMarker(new MarkerOptions().position(latLngLocal).title(name));
        }
        progressDialog.dismiss();
    }

    private void makeCircle() {
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(500)
                .strokeWidth(10)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(128, 255, 0, 0))
                .clickable(true));
    }
}
