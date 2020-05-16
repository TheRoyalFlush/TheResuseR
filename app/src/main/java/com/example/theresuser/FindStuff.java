package com.example.theresuser;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

//Class to get the users location and populate the map with all the items that are available
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
    boolean queryFlag;
    String queryString = "";
    List<String> nameList = new ArrayList<>();
    ListView itemListView;
    List<String[]> currentItemsList;
    ListViewCustomAdapter adapter;
    AlertDialog alertDialog;
    List<Integer> postId;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find_stuff, container, false);
        setHasOptionsMenu(true);

        final SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        geoDataClient = Places.getGeoDataClient(getActivity(),null);
        placeDetectionClient = Places.getPlaceDetectionClient(getActivity(),null);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        itemListView = new ListView(getActivity());
        currentItemsList = new ArrayList<String[]>();

        adapter = new ListViewCustomAdapter(getActivity(),R.layout.custom_list_view, (ArrayList<String[]>) currentItemsList);
        itemListView.setAdapter(adapter);

        //final NavController navController = Navigation.findNavController(view);

        final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        builder.setTitle("Items").setMessage("Please select an item.").setView(itemListView)
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = builder.create();

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viw, int position, long id) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
                if (account == null) {
                    alertDialog.cancel();
                    final NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_findStuff_to_login);
                    return;
                } else {
                if (postId != null && markerArray != null) {
                    for (int i = 0; i <= markerArray.length() - 1; i++) {
                        try {
                            if (Integer.valueOf(markerArray.getJSONObject(i).getString("post_id")) == postId.get(position)) {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("claim_data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("item_content", String.valueOf(markerArray.getJSONObject(i)));
                                editor.apply();
                                alertDialog.cancel();
                                final NavController navController = Navigation.findNavController(view);
                                navController.navigate(R.id.action_findStuff_to_itemClaim);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            }
        });

        return view;
    }

    //Calling the map to initialize when the activity is launched
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        //getDeviceLocation();

        //Setting the marker for the users location on the map
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                List<String> tagList = new ArrayList<>();
                if(!marker.getTitle().equals("Your Location")) {
                    if (markerArray != null) {

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Marker_Data",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        ArrayList<String> mArray = (ArrayList<String>) marker.getTag();
                        Set<String> set = new HashSet<String>(mArray);

                        editor.putStringSet("Item", set);
                        editor.putString("markerArray",String.valueOf(markerArray));
                        editor.apply();
                        postId = new ArrayList<>();
                        currentItemsList.clear();
                        for (int i = 0; i <= markerArray.length() - 1; i++) {

                            try {
                                if (mArray.contains(markerArray.getJSONObject(i).getString("post_id"))) {
                                    System.out.println("yes");
                                    postId.add(Integer.valueOf(markerArray.getJSONObject(i).getString("post_id")));
                                    currentItemsList.add(new String[]{markerArray.getJSONObject(i).getString("item_name"),
                                            markerArray.getJSONObject(i).getString("color_name"),
                                            markerArray.getJSONObject(i).getString("year_range")});

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapter.notifyDataSetChanged();
                        alertDialog.show();
                    }
                }
                return false;
            }
        });

    }

    //Updating the location of the user
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateLocationUI() {

        if (mMap == null) {

            return;
        }
        try {
            if (permission) {

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
                PopulateMap populateMap = new PopulateMap();
                populateMap.execute();
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

    //Handelling permissions for the application to get the users location services
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void permissionHandller(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            permission = true;
            updateLocationUI();
        }
        else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    //Handling the permission request for the user
    @RequiresApi(api = Build.VERSION_CODES.M)
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
                    //updateLocationUI();
                    //getDeviceLocation();
                }
            }
        }
        updateLocationUI();
    }

    //Getting the location of the user
    private void getDeviceLocation() {
        try {
            if (permission) {

                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            lastLocation = (Location) task.getResult();
                            if (lastLocation != null) {

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastLocation.getLatitude(),
                                                lastLocation.getLongitude()), 20));
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("Your Location").draggable(true)).showInfoWindow();
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("claim_data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_latitude", String.valueOf(lastLocation.getLatitude()));
                                editor.putString("user_longitude", String.valueOf(lastLocation.getLongitude()));
                                editor.apply();
                            }
                            else {
                                updateLocationUI();
                                //Toast.makeText(getActivity(),"Check you Location Settings",Toast.LENGTH_LONG).show();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.searchMenu);
        final SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!nameList.contains(query)){
                    System.out.println("query no");
                    Toast.makeText(getActivity(),"No Items Found",Toast.LENGTH_LONG).show();
                    return false;
                }
                System.out.println("query yes");
                Toast.makeText(getActivity(),"Zoom out to look for items.",Toast.LENGTH_LONG).show();
                queryFlag = true;
                queryString = query;
                PopulateMap populateMap = new PopulateMap();
                populateMap.execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("text change");
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    //Getting the maps data with the markers and the items posted
    public class PopulateMap extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            //SharedPreferences sharedPreferences = getActivity().getSharedPreferences("populate_maps",Context.MODE_PRIVATE);
            //SharedPreferences.Editor edit = sharedPreferences.edit();
            //edit.putInt("launch",1);
            //edit.apply();
            String mapData = AsyncTaskData.getMapData();
            return mapData;
        }


        @Override
        protected void onPostExecute(String s) {
            populateMap(s,0);
            queryFlag = false;
        }
    }
    //Poppulating the map with the markerd of the items posted
    public void populateMap(String arrayResult,int resultCode){
        if (mMap !=null){
            mMap.clear();

            //getDeviceLocation();
        }

        try {
            Set<LatLng> markerSet = new HashSet<>();
            JSONArray mapArray = new JSONArray(arrayResult);
            markerArray = mapArray;
            for(int i = 0; i<= mapArray.length() - 1;i++) {
                if (queryFlag) {
                    if (mapArray.getJSONObject(i).getString("item_name").toLowerCase().equals(queryString.toLowerCase())) {
                        System.out.println("query populate");
                        markerSet.add(new LatLng(Double.valueOf(mapArray.getJSONObject(i).getString("latitude")), Double.valueOf(mapArray.getJSONObject(i).getString("longitude"))));
                    }
                }
                else{
                    markerSet.add(new LatLng(Double.valueOf(mapArray.getJSONObject(i).getString("latitude")), Double.valueOf(mapArray.getJSONObject(i).getString("longitude"))));
                    nameList.add(mapArray.getJSONObject(i).getString("item_name").toLowerCase());
                }
            }

            List<LatLng> markerList = new ArrayList<>(markerSet);
            for (int i = 0;i <= markerList.size() -1;i++){
                List<String> markerTagList = new ArrayList<>();
                String postName = "";
                for (int j = 0; j <= mapArray.length() - 1;j++) {
                    if (queryFlag) {
                        if (mapArray.getJSONObject(j).getString("item_name").toLowerCase().equals(queryString.toLowerCase())) {
                            System.out.println("query pupulate map");
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





}
