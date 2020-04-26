package com.example.theresuser;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.FragmentManager;

import java.util.Set;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class FindStuff extends Fragment implements OnMapReadyCallback {
    View view;
    private GoogleMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find_stuff, container, false);
        setHasOptionsMenu(true);

        MapFragment mapFragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
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
                JSONArray mapArray = new JSONArray(s);
                for(int i = 0; i<= mapArray.length() - 1;i++){

                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(mapArray.getJSONObject(i).getString("latitude")),Double.valueOf(mapArray.getJSONObject(i).getString("longitude")))));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
