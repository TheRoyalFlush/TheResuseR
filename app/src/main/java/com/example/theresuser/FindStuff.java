package com.example.theresuser;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.internal.BackgroundDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.FragmentManager;
import android.widget.Toast;

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
    boolean queryFlag = false;
    String queryString = "";
    List<String> nameList = new ArrayList<>();

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


        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    return true;
                }
                return false;
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.searchMenu);
        final SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!nameList.contains(query)){
                    Toast.makeText(getActivity(),"No Items Found",Toast.LENGTH_LONG).show();
                    return false;
                }
                queryFlag = true;
                queryString = query;
                PopulateMap populateMap = new PopulateMap();
                populateMap.execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("mapready");
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
        PopulateMap populateMap = new PopulateMap();
        populateMap.execute();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.getTitle().equals("Your Location")) {
                    if (markerArray != null) {
                        for (int i = 0; i <= markerArray.length() - 1; i++) {
                            Intent intent = new Intent(getActivity(), ItemDetails.class);
                            intent.putExtra("Item", (ArrayList<String>) marker.getTag());
                            if (markerArray != null) {
                                intent.putExtra("markerArray", String.valueOf(markerArray));
                            }
                            startActivity(intent);
                        }
                    }
                }
                return false;
            }
        });

    }


    public class PopulateMap extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            System.out.println("populate maps");
            String mapData = AsyncTaskData.getMapData();
            return mapData;
        }


        @Override
        protected void onPostExecute(String s) {
            populateMap(s,0);
            queryFlag = false;


        }
    }

    public void populateMap(String arrayResult,int resultCode){
        if (mMap !=null){
            mMap.clear();
            getDeviceLocation();
        }

        try {
            Set<LatLng> markerSet = new HashSet<>();
            JSONArray mapArray = new JSONArray(arrayResult);
            markerArray = mapArray;
            for(int i = 0; i<= mapArray.length() - 1;i++) {
                if (queryFlag) {
                    if (mapArray.getJSONObject(i).getString("item_name").toLowerCase().equals(queryString.toLowerCase())) {
                        markerSet.add(new LatLng(Double.valueOf(mapArray.getJSONObject(i).getString("latitude")), Double.valueOf(mapArray.getJSONObject(i).getString("longitude"))));
                    }
                }
                else{
                    markerSet.add(new LatLng(Double.valueOf(mapArray.getJSONObject(i).getString("latitude")), Double.valueOf(mapArray.getJSONObject(i).getString("longitude"))));
                    nameList.add(mapArray.getJSONObject(i).getString("item_name"));
                }
            }

            List<LatLng> markerList = new ArrayList<>(markerSet);
            for (int i = 0;i <= markerList.size() -1;i++){
                List<String> markerTagList = new ArrayList<>();
                String postName = "";
                for (int j = 0; j <= mapArray.length() - 1;j++) {
                    if (queryFlag) {
                        if (mapArray.getJSONObject(i).getString("item_name").toLowerCase().equals(queryString.toLowerCase())) {
                            if (markerList.get(i).equals(new LatLng(Double.valueOf(mapArray.getJSONObject(j).getString("latitude")), Double.valueOf(mapArray.getJSONObject(j).getString("longitude"))))) {
                                postName = postName + mapArray.getJSONObject(j).getString("item_name").toUpperCase() + "\n";
                                markerTagList.add(mapArray.getJSONObject(j).getString("post_id"));
                            }
                        }
                    }
                    else{
                        if (markerList.get(i).equals(new LatLng(Double.valueOf(mapArray.getJSONObject(j).getString("latitude")), Double.valueOf(mapArray.getJSONObject(j).getString("longitude"))))) {
                            postName = postName + mapArray.getJSONObject(j).getString("item_name").toUpperCase() + "\n";
                            markerTagList.add(mapArray.getJSONObject(j).getString("post_id"));
                        }
                    }
                }
                mMap.addMarker(new MarkerOptions().position(markerList.get(i)).title("Item").snippet(postName)).setTag(markerTagList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())).title("Your Location").draggable(true)).showInfoWindow();
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("claim_data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor =sharedPreferences.edit();
                            editor.putString("user_latitude", String.valueOf(lastLocation.getLatitude()));
                            editor.putString("user_longitude", String.valueOf(lastLocation.getLongitude()));
                            editor.apply();
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
