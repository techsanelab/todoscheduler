package com.techsanelab.todo;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    // Google services
    GoogleMap googleMap;
    GeofencingClient geofencingClient;
    GeofenceHelper geofenceHelper;
    LatLng selectedtLatLng;
    Activity activity;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        EasyFont easyFont = new EasyFont(this);
        easyFont.tfBold(R.id.add_location);
        activity = this;
        context = this;

        findViewById(R.id.add_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGeofence(selectedtLatLng, getIntent().getStringExtra("title"), getIntent().getStringExtra("text"));
            }
        });

        // Location settings
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHandler.customDialog(context, activity,
                        getString(R.string.location_dia_title),
                        getString(R.string.location_dia_text),
                        getDrawable(R.drawable.map_dia),
                        DialogHandler.DialogImageSize.BIG);
            }
        });

        // first time settings
        SharedPreferences prefs = getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        boolean isFirstTime = prefs.getBoolean(Utils.LOCATION_FIRST_KEY, true);

        // first time settings
        if (isFirstTime) {
            findViewById(R.id.info).callOnClick();
            editor.putBoolean(Utils.LOCATION_FIRST_KEY,false);
            editor.commit();
        }


    }

    @Override
    public void onMapReady(GoogleMap gm) {
        googleMap = gm;
        final MarkerOptions markerOptions = new MarkerOptions();

        // Shiraz University CSE department
        LatLng cuLocation = new LatLng(29.628393, 52.519007);

        markerOptions.position(cuLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cuLocation, 15f));
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        // create new markers
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                googleMap.addCircle(new CircleOptions().center(latLng)
                        .radius(geofenceHelper.GEOFENCE_RADIUS)
                        .strokeColor(getColor(R.color.accent))
                        .fillColor(getColor(R.color.mapCircleColor))
                        .strokeWidth(2));
                selectedtLatLng = latLng;

            }
        });

    }


    public void createGeofence(LatLng latLng, String title, String text) {
        Geofence geofence = geofenceHelper.getGeofence("some id", latLng);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getGeofencePI(title, text);

        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Geo adding onSuccess");
                Toast.makeText(getApplicationContext(), getString(R.string.success_geofence), Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String em = geofenceHelper.getErrorString(e);
                Log.d(TAG, "onFailure: " + em);
                Toast.makeText(getApplicationContext(), getString(R.string.failure_geofence), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
