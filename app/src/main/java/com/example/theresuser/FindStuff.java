package com.example.theresuser;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FindStuff extends Fragment implements OnMapReadyCallback {
    View view;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    GeoDataClient geoDataClient;
    PlaceDetectionClient placeDetectionClient;
    Location lastLocation;
    boolean permission;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    JSONArray markerArray;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find_stuff, container, false);
        setHasOptionsMenu(true);

        MapFragment mapFragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        geoDataClient = Places.getGeoDataClient(getActivity(),null);
        placeDetectionClient = Places.getPlaceDetectionClient(getActivity(),null);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        PopulateMap populateMap = new PopulateMap();
        populateMap.execute();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.searchMenu);
        SearchView searchView = (SearchView)menuItem.getActionView();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(markerArray != null){
                    for(int i = 0; i<= markerArray.length() - 1;i++){
                        Intent intent = new Intent(getActivity(),ItemDetails.class);
                        intent.putExtra("Item", (ArrayList<String>) marker.getTag());
                        if (markerArray != null){
                            intent.putExtra("markerArray", String.valueOf(markerArray));
                        }
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }


    public class PopulateMap extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String mapData = AsyncTaskData.getMapData();
            return mapData;
        }


        @Override
        protected void onPostExecute(String s) {
            try {
                Set<LatLng> markerSet = new HashSet<>();
                JSONArray mapArray = new JSONArray(s);
                markerArray = mapArray;
                for(int i = 0; i<= mapArray.length() - 1;i++){
                    markerSet.add(new LatLng(Double.valueOf(mapArray.getJSONObject(i).getString("latitude")),Double.valueOf(mapArray.getJSONObject(i).getString("longitude"))));
                }
                System.out.println(markerSet);
                List<LatLng> markerList = new ArrayList<>(markerSet);
                for (int i = 0;i <= markerList.size() -1;i++){
                    List<String> markerTagList = new ArrayList<>();
                    String postName = "";
                    for (int j = 0; j <= mapArray.length() - 1;j++){
                        if (markerList.get(i).equals(new LatLng(Double.valueOf(mapArray.getJSONObject(j).getString("latitude")),Double.valueOf(mapArray.getJSONObject(j).getString("longitude"))))){
                            postName = postName + mapArray.getJSONObject(j).getString("item_name").toUpperCase()+"\n";
                            markerTagList.add(mapArray.getJSONObject(j).getString("post_id"));
                        }
                    }
                    mMap.addMarker(new MarkerOptions().position(markerList.get(i)).title("Item").snippet(postName)).setTag(markerTagList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public void permissionHandller(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            permission = true;
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            lastLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastLocation.getLatitude(),
                                            lastLocation.getLongitude()), 10));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())).title("Your Location"));
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
}
