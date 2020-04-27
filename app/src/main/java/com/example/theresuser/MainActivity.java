package com.example.theresuser;

import android.app.FragmentManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Fragment;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Calling the fragment of the next page where user can donate items
    public void DonateStuff(View view){

        Fragment fragment = null;
        fragment = new DonateStuff();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout,
                fragment).addToBackStack("back").commit();
    }
    //Calling the fragment of next page where user can see all the posts of items
    public void FindStuff(View view){
        Fragment fragment = null;
        fragment = new FindStuff();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout,
                fragment).addToBackStack("back").commit();
    }

}
