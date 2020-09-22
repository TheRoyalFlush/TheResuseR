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

import org.w3c.dom.Text;

import java.util.Locale;


//Main landing page handling the flow of the application
public class MainActivity extends AppCompatActivity  {

    Context context;
    Resources resources;
    BottomNavigationView bottomNavigationView;
    ImageView screen;
    TextView msg;
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
        final TextView skip,next;
        final Dialog dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
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
        screen = (ImageView)dialog.findViewById(R.id.screen);
        count = 0;
        System.out.println("splash");
        final Resources resources = getResources();
        screen.setImageDrawable(resources.getDrawable(R.drawable.new_icon));
        //screen.setImageResource(R.drawable.new_icon);
        next = (TextView)dialog.findViewById(R.id.next);
        skip = (TextView)dialog.findViewById(R.id.skip);
        msg = (TextView)dialog.findViewById(R.id.msg);
        msg.setText("");
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

//Populating images
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                System.out.println(count);
                if (count == 1){
                    screen.setImageDrawable(resources.getDrawable(R.drawable.one));
                    msg.setText("Select location where your items are located on the kerb.");
                }
                if (count == 2){
                    screen.setImageDrawable(resources.getDrawable(R.drawable.two));
                    msg.setTextSize(14);
                    msg.setText("Add details of the items plcaed on the kerb. \n Press Add More to add more items into the list.\nPress post to let your community know about the listed items.");
                }
                if (count == 3){
                    screen.setImageResource(R.drawable.three);
                    msg.setTextSize(18);
                    msg.setText("Locate items around you to look for items to reuse.");
                }
                if (count == 4){
                    screen.setImageResource(R.drawable.four);
                    msg.setText("Know your contribution towards a greener earth.");
                }
                if (count == 5){
                    screen.setImageResource(R.drawable.five);
                    msg.setText("Seamlessly switch between English and Chinese language");
                }
                if (count == 6){
                    next.setText("Get Started");
                    screen.setImageResource(R.drawable.six);
                    msg.setText("Learn More about the application and Monash council.");
                }
                if (count == 7){
                    dialog.cancel();
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
