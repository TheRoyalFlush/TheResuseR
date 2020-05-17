package com.example.theresuser;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {

    Switch english,chinese;
    View view;
    SharedPreferences sharedPreferences;

    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        english = (Switch)view.findViewById(R.id.english);
        chinese = (Switch)view.findViewById(R.id.chinese);

        String language = sharedPreferences.getString("language",null);
        if (language != null) {
            if (language.equals("english")){
                english.setChecked(true);
            }
            if (language.equals("chinese")){
                chinese.setChecked(true);
            }
        }
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (english.isChecked()){
                    chinese.setChecked(false);
                    editor.putString("language","english");
                    editor.apply();
                    changeLang("en");
                }
                else {
                    chinese.setChecked(true);
                    editor.putString("language","chinese");
                    editor.apply();
                    changeLang("zh");
                }
            }
        });

        chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chinese.isChecked()){
                    english.setChecked(false);
                    editor.putString("language","chinese");
                    editor.apply();
                    changeLang("zh");

                }
                else{
                    english.setChecked(true);
                    editor.putString("language","english");
                    editor.apply();
                    changeLang("zh");
                }
            }
        });

        return view;
    }

    public void changeLang(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());
    }

}
