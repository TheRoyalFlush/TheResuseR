package com.example.theresuser;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Fragment;
import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;


//Main landing page handling the flow of the application
public class MainActivity extends AppCompatActivity  {

    Context context;
    Resources resources;
    BottomNavigationView bottomNavigationView;
    ImageView screen;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("Language",MODE_PRIVATE);
        String lang = sharedPreferences.getString("language",null);
        if (lang != null){
            changeLanguage(lang);
        }
        setContentView(R.layout.activity_main);
        resources = getBaseContext().getResources();
         context = getBaseContext();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setBackgroundColor(Color.parseColor("#f3f9fb"));

        showSplash();
        ImageView home = (ImageView)findViewById(R.id.home);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        final NavController navController = Navigation.findNavController(this,R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }
//Splash screen setup
    public void showSplash(){
        final TextView close,previous,next;
        final Dialog dialog = new Dialog(this);
        SharedPreferences sharedPreferences1 = getSharedPreferences("SplashScreen",MODE_PRIVATE);
        if (sharedPreferences1.getInt("splash",0) == 1){
            return;
        }
        else{
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putInt("splash",1);
            editor.apply();
        }
        dialog.setContentView(R.layout.popup_splash_screen);
        dialog.setCanceledOnTouchOutside(false);
        close = (TextView)dialog.findViewById(R.id.close);
        previous = (TextView)dialog.findViewById(R.id.previous);
        next = (TextView)dialog.findViewById(R.id.next);
        screen = (ImageView)dialog.findViewById(R.id.screen);
        count = 1;
        System.out.println("splash");
        screen.setImageResource(R.drawable.one);
        previous.setVisibility(View.INVISIBLE);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//Populating images
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count !=1){
                    count -= count;}
                if (count == 1){
                    previous.setVisibility(View.INVISIBLE);
                }
                if (count == 1){
                    screen.setImageResource(R.drawable.one);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous.setVisibility(View.VISIBLE);
                if (count != 4) {
                    count += 1;
                }
                if(count == 4){
                    next.setVisibility(View.INVISIBLE);
                }
            }
        });
        dialog.show();

    }

    public void changeLanguage(String lang){
        if (lang.equals("english")){
            lang = "en";
        }
        else if (lang.equals("chinese")){
            lang = "zh";
        }
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
    }

}
