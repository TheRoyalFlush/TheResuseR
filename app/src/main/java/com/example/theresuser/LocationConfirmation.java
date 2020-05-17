package com.example.theresuser;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
//Class responsible for getting the user location to be able to post the item
public class LocationConfirmation extends Fragment implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    GeoDataClient geoDataClient;
    PlaceDetectionClient placeDetectionClient;
    Location lastLocation;
    boolean permission;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_location_confirmation, container, false);

        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "Please enable location services before proceeding", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        geoDataClient = Places.getGeoDataClient(getActivity(),null);
        placeDetectionClient = Places.getPlaceDetectionClient(getActivity(),null);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Button sendData = (Button)view.findViewById(R.id.sendData);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdGenerator idGenerator = new IdGenerator();
                idGenerator.execute();
            }
        });

        return view;
    }
    //Handelling permissions for the application to get the users location services
    public void permissionHandller(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            permission = true;
            updateLocationUI();
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    //Handling the permission request for the user
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

                    updateLocationUI();
                }
            }
        }
        updateLocationUI();
    }
    //Updating the location of the user
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (permission) {

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
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

    //Getting the location of the user
    private void getDeviceLocation() {

        try {
            if (permission) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            lastLocation = (Location) task.getResult();
                            if (lastLocation != null) {

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastLocation.getLatitude(),
                                                lastLocation.getLongitude()), 20));
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))).setDraggable(true);
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("post_data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor =sharedPreferences.edit();
                                editor.putFloat("latitude",(float)lastLocation.getLatitude());
                                editor.putFloat("longitude",(float)lastLocation.getLongitude());
                                editor.apply();
                            }
                            else{
                                updateLocationUI();
                                //Toast.makeText(getApplication(),"Check you Location Settings",Toast.LENGTH_LONG).show();
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
    //Calling the map to initialize when the activity is launched
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Toast.makeText(getActivity(),"Move the marker to the location of your kerb.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng newLocation = marker.getPosition();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("post_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putFloat("latitude", (float) newLocation.latitude);
                editor.putFloat("longitude",(float)newLocation.longitude);
                editor.apply();

            }
        });


        //getDeviceLocation();
    }

//Calling the api to generate the user id
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
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("post_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putInt("post_id",flag+1);
            editor.apply();
            final NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_locationConfirmation2_to_donateStuff);
        }
    }
}
