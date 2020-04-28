package com.example.theresuser;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Fragment;
import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;


//Main landing page handling the flow of the application
public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, new MainFragment()).commit();
        Intent intent = getIntent();
        if (intent.getStringExtra("findStuff") != null){
            if (intent.getStringExtra("findStuff").equals("find")){
                Fragment fragment = null;
                fragment = new FindStuff();
                fragmentManager.beginTransaction().replace(R.id.frame_layout,
                        fragment).addToBackStack("back").commit();
            }
        }
        if (intent.getStringExtra("postNewItem") != null){
            if (intent.getStringExtra("postNewItem").equals("newPost")){
                Fragment fragment = null;
                fragment = new DonateStuff();
                fragmentManager.beginTransaction().replace(R.id.frame_layout,
                        fragment).addToBackStack("back").commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Calling the fragment of the next page where user can donate items
    public void DonateStuff(View view){
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(getApplicationContext(),"Please enable location services before proceeding",Toast.LENGTH_LONG).show();
            startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;

        }
        else {
            Fragment fragment = null;
            fragment = new DonateStuff();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_layout,
                    fragment).addToBackStack("back").commit();
        }
    }
    //Calling the fragment of next page where user can see all the posts of items
    public void FindStuff(View view){
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(getApplicationContext(),"Please enable location services before proceeding",Toast.LENGTH_LONG).show();
            startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;

        }
        else{
            Fragment fragment = null;
            fragment = new FindStuff();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_layout,
                    fragment).addToBackStack("back").commit();
        }
    }

}
