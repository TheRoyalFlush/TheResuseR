package com.example.theresuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

//Cass populating the options for users to selcet the item to be posted
public class DonateStuff extends Fragment {
    View view;
    Spinner colorSpinner,itemSpinner,typeSpinner,yearSpinner;
    Button proceed;
    JSONArray colorArray,itemArray,typeArray,yearArray;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_donate_stuff, container, false);

        itemSpinner =(Spinner) view.findViewById(R.id.itemSpinner);
        colorSpinner =(Spinner) view.findViewById(R.id.colorSpinner);
        typeSpinner =(Spinner) view.findViewById(R.id.typeSpinner);
        yearSpinner =(Spinner) view.findViewById(R.id.yearSpinner);
        proceed = (Button)view.findViewById(R.id.proceed);
        ItemDataAsyncTaks itemDataAsyncTaks = new ItemDataAsyncTaks();
        itemDataAsyncTaks.execute();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LocationConfirmation.class);
                //Adding all the data to the sharedpreference to be accessed later in the application
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("post_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();

                try {
                    editor.putString("color",colorArray.getJSONObject(colorSpinner.getSelectedItemPosition()).getString("color_id"));
                    editor.putString("item",itemArray.getJSONObject(itemSpinner.getSelectedItemPosition()).getString("item_id"));
                    editor.putString("type",typeArray.getJSONObject(typeSpinner.getSelectedItemPosition()).getString("type_id"));
                    editor.putString("year",yearArray.getJSONObject(yearSpinner.getSelectedItemPosition()).getString("year_id"));
                    editor.putString("color_name",colorArray.getJSONObject(colorSpinner.getSelectedItemPosition()).getString("color_name"));
                    editor.putString("item_name",itemArray.getJSONObject(itemSpinner.getSelectedItemPosition()).getString("item_name"));
                    editor.putString("type_name",typeArray.getJSONObject(typeSpinner.getSelectedItemPosition()).getString("type_name"));
                    editor.putString("year_range",yearArray.getJSONObject(yearSpinner.getSelectedItemPosition()).getString("year_range"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.apply();
                startActivity(intent);
            }
        });
        //Sending the user to the landing page when back button is pressed
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    return true;
                }
                return false;
            }
        });
        return view;
    }


    //Populating all the options for the items to be posted
    public class ItemDataAsyncTaks extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String spinnerData = AsyncTaskData.PopulateSpinner("items");
            return spinnerData;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                //Populating the drop down menues for the user to select the item to be posted
                JSONObject mainObject = new JSONObject(s);
                colorArray = new JSONArray(mainObject.getString("Color"));
                itemArray = new JSONArray(mainObject.getString("Item"));
                yearArray = new JSONArray(mainObject.getString("Year"));
                typeArray = new JSONArray(mainObject.getString("ItemCategory"));
                List colorList = new ArrayList();
                List itemList = new ArrayList();
                List yearList = new ArrayList();
                List typeList = new ArrayList();
                for (int i = 0; i <= colorArray.length() - 1;i++ ){
                    colorList.add(colorArray.getJSONObject(i).get("color_name"));
                }
                for(int i = 0; i<= itemArray.length() - 1;i++){
                    itemList.add(itemArray.getJSONObject(i).get("item_name"));
                }
                for(int i = 0; i<= yearArray.length() - 1;i++){
                    yearList.add(yearArray.getJSONObject(i).get("year_range"));
                }
                for(int i = 0; i<= typeArray.length() - 1;i++){
                    typeList.add(typeArray.getJSONObject(i).get("type_name"));
                }

                final ArrayAdapter<String> colorSpinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, colorList);
                colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

                final ArrayAdapter<String> itemSpinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, itemList);
                colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

                final ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, typeList);
                colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

                final ArrayAdapter<String> yearSpinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, yearList);
                colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

                colorSpinner.setAdapter(colorSpinnerAdapter);
                itemSpinner.setAdapter(itemSpinnerAdapter);
                typeSpinner.setAdapter(typeSpinnerAdapter);
                yearSpinner.setAdapter(yearSpinnerAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
