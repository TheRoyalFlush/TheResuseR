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
        SharedPreferences sharedPreferences = getSharedPreferences("Langiage",MODE_PRIVATE);
        String lang = sharedPreferences.getString("language",null);
        if (lang != null){
            changeLanguage(lang);
        }
        setContentView(R.layout.activity_main);
        resources = getBaseContext().getResources();
         context = getBaseContext();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showSplash();

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);


    }

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
                if (count == 2){
                    screen.setImageResource(R.drawable.two);
                }
                if (count == 3){
                    screen.setImageResource(R.drawable.three);
                }
                if (count == 4){
                    screen.setImageResource(R.drawable.four);
                }
                if (count == 5){
                    screen.setImageResource(R.drawable.five);
                }
                if (count == 6){
                    screen.setImageResource(R.drawable.six);
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous.setVisibility(View.VISIBLE);
                if (count != 7) {
                    count += 1;
                }
                if(count == 7){
                    next.setVisibility(View.INVISIBLE);
                }

                if (count == 2){
                    screen.setImageResource(R.drawable.two);
                }
                if (count == 3){
                    screen.setImageResource(R.drawable.three);
                }
                if (count == 4){
                    screen.setImageResource(R.drawable.four);
                }
                if (count == 5){
                    screen.setImageResource(R.drawable.five);
                }
                if (count == 6){
                    screen.setImageResource(R.drawable.six);
                }
                if (count == 7){
                    screen.setImageResource(R.drawable.seven);
                }
            }
        });
        dialog.show();

    }

    public void changeLanguage(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
    }

}
