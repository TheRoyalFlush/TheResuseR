package com.example.theresuser;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Settings screen for the application
public class Settings extends Fragment {

    Switch english;
    Switch chinese;
    static Switch binDay;
    View view;
    SharedPreferences sharedPreferences;
    String day;

    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language",null);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        english = (Switch)view.findViewById(R.id.english);
        chinese = (Switch)view.findViewById(R.id.chinese);
        binDay = (Switch) view.findViewById(R.id.binday);
        System.out.println(language);
//Checking the current language
        if (language != null) {
            if (language.equals("english")){
                english.setChecked(true);
            }
            if (language.equals("chinese")){
                chinese.setChecked(true);
            }
        }
        else {
            editor.putString("language","english");
            editor.apply();
            english.setChecked(true);
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
                    changeLang("en");
                }
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("reminder",Context.MODE_PRIVATE);
        if (sharedPreferences.getInt("hour",0) == 0){
            binDay.setChecked(false);
        }
        else {
            binDay.setChecked(true);
        }
        binDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binDay.isChecked()){
                    AlarmManager alarmManager;
                    Intent intent;
                    PendingIntent pendingIntent;
                    alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                    intent = new Intent(getActivity(),SheduleIntentService.class);
                    pendingIntent = PendingIntent.getBroadcast(getActivity(),100,intent,0);
                    if (alarmManager != null){
                        alarmManager.cancel(pendingIntent);
                    }

                }
                else {
                    if (day != null && !day.equals("")) {
                        binDay.setChecked(true);
                        Toast.makeText(getActivity(), day + " " + getString(R.string.bin), Toast.LENGTH_SHORT).show();
                        DialogFragment newFragment = new TimePickerFragment();
                        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                    } else {
                        binDay.setChecked(false);
                        Toast.makeText(getActivity(), getString(R.string.not), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        ReminderDay reminderDay = new ReminderDay();
        reminderDay.execute();
        return view;
    }
//Adding intent for the reminder
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        AlarmManager alarmManager;
        Intent intent;
        PendingIntent pendingIntent;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        binDay.setChecked(false);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar2 = Calendar.getInstance();
            System.out.println(calendar2.get(Calendar.DAY_OF_WEEK)+"|"+calendar2.get(Calendar.HOUR_OF_DAY)+"|"+calendar2.get(Calendar.MINUTE));
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("reminder",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("hour",hourOfDay);
            editor.putInt("minute",minute);
            editor.apply();
            SharedPreferences sp = getActivity().getSharedPreferences("reminder",Context.MODE_PRIVATE);
            String day = sp.getString("day",null);

            int dayInt =0;
            String daysArray[] = {"Sunday","Monday","Tuesday", "Wednesday","Thursday","Friday", "Saturday"};
            for (int i = 0;i<=daysArray.length-1;i++){
                if(day.equals(daysArray[i])){
                    if (i == 0){
                        dayInt = 6;
                    }
                    else {
                        dayInt = i;

                    }
                }
            }

            alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
            intent = new Intent(getActivity(),SheduleIntentService.class);
            pendingIntent = PendingIntent.getBroadcast(getActivity(),100,intent,0);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            calendar.set(Calendar.MINUTE,minute);
            calendar.set(Calendar.SECOND,00);
            calendar.set(Calendar.DAY_OF_WEEK, dayInt);
            System.out.println(calendar.get(Calendar.DAY_OF_WEEK)+"|"+calendar.get(Calendar.HOUR_OF_DAY)+"|"+calendar.get(Calendar.MINUTE));

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis() ,
                    AlarmManager.INTERVAL_DAY*7,pendingIntent);
            if (minute < 10){
                Toast.makeText(getActivity(),"Your reminder has been set for "+daysArray[dayInt]+" at "+hourOfDay+":0"+minute,Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), "Your reminder has been set for " + daysArray[dayInt] + " at " + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
            }
        }

    }

    public class ReminderDay extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("claim_data",Context.MODE_PRIVATE);
            String latitude = sharedPreferences.getString("user_latitude",null);
            String longitude = sharedPreferences.getString("user_longitude",null);
            String sendData = "{\"latitude\":"+latitude+",\"longitude\":"+longitude+"}";
            return AsyncTaskData.reminderDay(sendData);
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println(s);
            try {
                JSONObject dayArray = new JSONObject(s);
                System.out.println(dayArray.length());
                if (dayArray.length() != 0) {
                    day = dayArray.getString("day");
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("reminder", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("day", day);
                    editor.apply();
                }
                else {
                    String daysArray[] = {"Sunday","Monday","Tuesday", "Wednesday","Thursday","Friday", "Saturday"};
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("reminder", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("day", daysArray[day]);
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeLang(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());
        getActivity().recreate();
    }

}
