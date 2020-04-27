package com.example.theresuser;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LocationConfirmation extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    GeoDataClient geoDataClient;
    PlaceDetectionClient placeDetectionClient;
    Location lastLocation;
    boolean permission;
    Item item;
    Integer colorId,yearId,itemId,typeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_confirmation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geoDataClient = Places.getGeoDataClient(this,null);
        placeDetectionClient = Places.getPlaceDetectionClient(this,null);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void permissionHandller(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            permission = true;
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permission = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (permission) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastLocation = null;
                permissionHandller();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getDeviceLocation() {
        try {
            if (permission) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            lastLocation = (Location) task.getResult();
                            if (lastLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastLocation.getLatitude(),
                                                lastLocation.getLongitude()), 20));
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
                            }
                            else{
                                Toast.makeText(getApplication(),"Check you Location Settings",Toast.LENGTH_LONG).show();
                            }
                            } else {
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 20));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();
        getDeviceLocation();
    }

    public void SendData(View view){
        IdGenerator idGenerator = new IdGenerator();
        idGenerator.execute();
    }


    public class IdGenerator extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String idData = AsyncTaskData.generateId();
            return idData;
        }

        @Override
        protected void onPostExecute(String s) {
            List<Integer> idList = new ArrayList<Integer>();
            int flag = 0;
            try {
                JSONArray idArray = new JSONArray(s);
                for(int i = 0; i <= idArray.length() - 1; i++){
                    idList.add(Integer.parseInt((idArray.getJSONObject(i).getString("post_id"))));
                }
                if (idList != null){
                    for(int i = 0; i <=idList.size()-1; i++){
                        if(idList.get(i) > flag){
                            flag = idList.get(i);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPreferences = getApplication().getSharedPreferences("post_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putFloat("latitude",(float)lastLocation.getLatitude());
            editor.putFloat("longitude",(float)lastLocation.getLongitude());
            editor.putInt("post_id",flag+1);
            editor.apply();
            CarbonIntensityAsyncTask carbonIntensityAsyncTask = new CarbonIntensityAsyncTask();
            carbonIntensityAsyncTask.execute();
        }
    }

    public class CarbonIntensityAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String name = getApplicationContext().getSharedPreferences("post_data",MODE_PRIVATE).getString("item_name",null);
            String itemName = "{\"item_name\":\""+name+"\"}";
            System.out.println(itemName);
            String carbonIntensity = AsyncTaskData.carbonIntensity(itemName);
            return carbonIntensity;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray carbonIntensityArray = new JSONArray(s);
                String carbonIntensity = carbonIntensityArray.getJSONObject(0).getString("carbon_intensity");
                SharedPreferences sharedPreferences = getApplication().getSharedPreferences("post_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putString("carbon_intensity",carbonIntensity);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(),PostDataReview.class);
            startActivity(intent);
        }
    }
}
