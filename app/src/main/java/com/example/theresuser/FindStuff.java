package com.example.theresuser;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
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
    List<String> typeList = new ArrayList<>();
    ListView itemListView;
    List<String[]> currentItemsList;
    ListViewCustomAdapter adapter;
    AlertDialog alertDialog;
    List<Integer> postId;
    Context con;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find_stuff, container, false);
        setHasOptionsMenu(true);

        LocationManager locationManager = (LocationManager)con.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(con, getString(R.string.location), Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        final SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        geoDataClient = Places.getGeoDataClient(con,null);
        placeDetectionClient = Places.getPlaceDetectionClient(con,null);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(con);

        itemListView = new ListView(con);
        currentItemsList = new ArrayList<String[]>();

        adapter = new ListViewCustomAdapter(con,R.layout.custom_list_view, (ArrayList<String[]>) currentItemsList);
        itemListView.setAdapter(adapter);

        //final NavController navController = Navigation.findNavController(view);

        final AlertDialog.Builder builder =new AlertDialog.Builder(con);
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
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(con);
                if (account == null) {
                    alertDialog.cancel();
                    final NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_findStuff_to_login);
                    return;
                } else {
                if (postId != null && markerArray != null) {
                    for (int i = 0; i <= markerArray.length() - 1; i++) {
                        try {
                            if (Integer.valueOf(markerArray.getJSONObject(i).getString("post_id")).equals(postId.get(position))) {
                                SharedPreferences sharedPreferences = con.getSharedPreferences("claim_data", Context.MODE_PRIVATE);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        con = context;

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

                        SharedPreferences sharedPreferences = con.getSharedPreferences("Marker_Data",Context.MODE_PRIVATE);
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
                mMap.getUiSettings().setMapToolbarEnabled(false);
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
        if (ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
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
                                                lastLocation.getLongitude()), 12));
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("Your Location").draggable(true)).showInfoWindow();
                                SharedPreferences sharedPreferences = con.getSharedPreferences("claim_data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_latitude", String.valueOf(lastLocation.getLatitude()));
                                editor.putString("user_longitude", String.valueOf(lastLocation.getLongitude()));
                                editor.apply();

                                Geocoder geocoder;
                                List<Address> addressList;
                                geocoder = new Geocoder(con, Locale.getDefault());

                                try {
                                    addressList = geocoder.getFromLocation(lastLocation.getLatitude(),lastLocation.getLongitude(), 1);
                                    String address = addressList.get(0).getAddressLine(0);
                                    System.out.println(address);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                            else {
                                updateLocationUI();
                                //Toast.makeText(con,"Check you Location Settings",Toast.LENGTH_LONG).show();
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
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(typeList);
                if (!nameList.contains(query.toLowerCase()) && !typeList.contains(query.toLowerCase())){
                    System.out.println("query no");
                    Toast.makeText(con,getString(R.string.no_item),Toast.LENGTH_LONG).show();
                    return false;
                }
                System.out.println("query yes");
                Toast.makeText(con,getString(R.string.zoom),Toast.LENGTH_LONG).show();
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


    //Getting the maps data with the markers and the items posted
    public class PopulateMap extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            //SharedPreferences sharedPreferences = con.getSharedPreferences("populate_maps",Context.MODE_PRIVATE);
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
                    if (mapArray.getJSONObject(i).getString("item_name").toLowerCase().equals(queryString.toLowerCase())|| mapArray.getJSONObject(i).getString("type_name").toLowerCase().equals(queryString.toLowerCase())) {
                        System.out.println("query populate");
                        markerSet.add(new LatLng(Double.valueOf(mapArray.getJSONObject(i).getString("latitude")), Double.valueOf(mapArray.getJSONObject(i).getString("longitude"))));
                    }
                }
                else{
                    markerSet.add(new LatLng(Double.valueOf(mapArray.getJSONObject(i).getString("latitude")), Double.valueOf(mapArray.getJSONObject(i).getString("longitude"))));
                    nameList.add(mapArray.getJSONObject(i).getString("item_name").toLowerCase());
                    typeList.add(mapArray.getJSONObject(i).getString("type_name").toLowerCase());
                }
            }

            List<LatLng> markerList = new ArrayList<>(markerSet);
            for (int i = 0;i <= markerList.size() -1;i++){
                List<String> markerTagList = new ArrayList<>();
                String postName = "";
                for (int j = 0; j <= mapArray.length() - 1;j++) {
                    if (queryFlag) {
                        if (mapArray.getJSONObject(j).getString("item_name").toLowerCase().equals(queryString.toLowerCase()) || mapArray.getJSONObject(j).getString("type_name").toLowerCase().equals(queryString.toLowerCase())) {
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
