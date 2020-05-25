package com.example.theresuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

//Cass populating the options for users to selcet the item to be posted
public class DonateStuff extends Fragment {
    View view;
    Spinner colorSpinner,itemSpinner,yearSpinner;
    Button proceed,addMore;
    JSONArray colorArray,itemArray,yearArray;
    ListView itemListView;
    List<String[]> currentItemsList;
    TextView heading;
    Boolean edit = false;
    int pos = 0;
    Item itemObject;
    JSONArray finalPostArray;
    String finalArray  ="";
    List<Integer> postIdList = new ArrayList<>();
    Context con;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_donate_stuff, container, false);

        itemListView  =(ListView) view.findViewById(R.id.item_list_view);
        addMore = (Button) view.findViewById(R.id.addMore);
        itemSpinner =(Spinner) view.findViewById(R.id.itemSpinner);
        colorSpinner =(Spinner) view.findViewById(R.id.colorSpinner);
        heading = (TextView)view.findViewById(R.id.headding);

        yearSpinner =(Spinner) view.findViewById(R.id.yearSpinner);
        proceed = (Button)view.findViewById(R.id.proceed);
        currentItemsList = new ArrayList<String[]>();
        final ListViewCustomAdapter adapter = new ListViewCustomAdapter(con,R.layout.custom_list_view, (ArrayList<String[]>) currentItemsList);
        itemListView.setAdapter(adapter);

        finalPostArray = new JSONArray();

        ItemDataAsyncTaks itemDataAsyncTaks = new ItemDataAsyncTaks();
        itemDataAsyncTaks.execute();

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                heading.setText(getString(R.string.edit));
                addMore.setText(getString(R.string.save1));
                pos = position;
                edit = true;
                try {
                for (int i = 0; i<= itemArray.length()-1;i++){
                    if (currentItemsList.get(position)[0].equals(itemArray.getJSONObject(i).getString("item_name"))){
                        itemSpinner.setSelection(i);
                    }
                }
                for (int i = 0; i<= colorArray.length()-1;i++){
                    if (currentItemsList.get(position)[1].equals(colorArray.getJSONObject(i).getString("color_name"))){
                        colorSpinner.setSelection(i);
                    }
                }
                for (int i = 0; i<= yearArray.length()-1;i++){
                    if (currentItemsList.get(position)[2].equals(yearArray.getJSONObject(i).getString("year_range"))){
                        yearSpinner.setSelection(i);
                    }
                }
                }

                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!edit) {

                        currentItemsList.add(new String[]{itemArray.getJSONObject(itemSpinner.getSelectedItemPosition()).getString("item_name"),
                                colorArray.getJSONObject(colorSpinner.getSelectedItemPosition()).getString("color_name"),
                                yearArray.getJSONObject(yearSpinner.getSelectedItemPosition()).getString("year_range")});
                        adapter.notifyDataSetChanged();

                    } else {
                        currentItemsList.set(pos,new String[]{itemArray.getJSONObject(itemSpinner.getSelectedItemPosition()).getString("item_name"),
                                colorArray.getJSONObject(colorSpinner.getSelectedItemPosition()).getString("color_name"),
                                yearArray.getJSONObject(yearSpinner.getSelectedItemPosition()).getString("year_range")});
                        heading.setText(getString(R.string.selectitem));
                        addMore.setText("ADD MORE");
                        edit = false;
                        adapter.notifyDataSetChanged();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItemsList.size() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.noitem), Toast.LENGTH_LONG).show();
                } else {
                    final NavController navController = Navigation.findNavController(view);
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(con);
                    if (account == null) {
                        navController.navigate(R.id.action_donateStuff_to_login);
                    } else {
                        if (edit) {
                            Toast.makeText(con, getString(R.string.save), Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                Toast.makeText(getActivity(),"Your items have been posted online for others to see.",Toast.LENGTH_LONG).show();
                                SharedPreferences sharedPreferences = con.getSharedPreferences("post_data", Context.MODE_PRIVATE);
                                float latitude = sharedPreferences.getFloat("latitude", 0);
                                float longitude = sharedPreferences.getFloat("longitude", 0);
                                int postId = sharedPreferences.getInt("post_id", 0);
                                int itemId = 0;
                                int colorId = 0;
                                int yearId = 0;
                                int count = 0;
                                for (int i = 0; i <= currentItemsList.size() - 1; i++) {
                                    for (int j = 0; j <= itemArray.length() - 1; j++) {
                                        if (currentItemsList.get(i)[0].equals(itemArray.getJSONObject(j).getString("item_name"))) {
                                            itemId = Integer.parseInt(itemArray.getJSONObject(j).getString("item_id"));

                                        }
                                    }
                                    for (int j = 0; j <= colorArray.length() - 1; j++) {
                                        if (currentItemsList.get(i)[1].equals(colorArray.getJSONObject(j).getString("color_name"))) {
                                            colorId = Integer.parseInt(colorArray.getJSONObject(j).getString("color_id"));
                                        }
                                    }
                                    for (int j = 0; j <= yearArray.length() - 1; j++) {
                                        if (currentItemsList.get(i)[2].equals(yearArray.getJSONObject(j).getString("year_range"))) {
                                            yearId = Integer.parseInt(yearArray.getJSONObject(j).getString("year_id"));
                                        }
                                    }
                                    if (count > 0) {
                                        postId = postId + 1;
                                    }
                                    itemObject = new Item(postId, colorId, itemId, yearId, latitude, longitude);
                                    postIdList.add(postId);
                                    count = count + 1;
                                    Gson gson = new GsonBuilder().create();
                                    String stringJson = gson.toJson(itemObject);
                                    System.out.println(stringJson);
                                    if (i != currentItemsList.size() - 1) {
                                        finalArray = finalArray + stringJson + ",";
                                    } else {
                                        finalArray = finalArray + stringJson;
                                    }
                                    JSONObject newObject = new JSONObject(stringJson);
                                    finalPostArray.put(newObject);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            finalArray = "[" + finalArray + "]";
                            SendDataAysnc sendDataAysnc = new SendDataAysnc();
                            sendDataAysnc.execute();
                            Toast.makeText(getActivity(),"Thank You for your donation towards a greener planet",Toast.LENGTH_SHORT).show();
                            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.findStuff, true).build();
                            navController.navigate(R.id.action_donateStuff_to_locationConfirmation2, null, navOptions);
                        }
                    }
                }
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


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        con = context;

    }

    public class SendDataAysnc extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            AsyncTaskData.postItem(finalArray);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            RecordActivity recordActivity = new RecordActivity();
            recordActivity.execute();
        }
    }

    public class RecordActivity extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String recordData = AsyncTaskData.RecordId();
            return recordData;
        }

        @Override
        protected void onPostExecute(String s) {
            int counter = 0;
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(con);
            Date date = new Date();
            System.out.println(date);
            String activityAraay = "";
            try {
                JSONArray arrayData = new JSONArray(s);
                for(int i = 0; i <= arrayData.length()-1; i++){
                    System.out.println(Integer.valueOf(arrayData.getJSONObject(i).getString("record_id")));
                    if(Integer.valueOf(arrayData.getJSONObject(i).getString("record_id")) > counter){
                        counter = Integer.valueOf(arrayData.getJSONObject(i).getString("record_id"));
                    }
                }
                counter += 1;
                for (int j = 0; j <= postIdList.size()-1; j++){
                    if(j>0){
                        counter = counter + 1;
                    }
                    UserActivity userActivity = new UserActivity(account.getEmail(),counter,postIdList.get(j),1,date);
                    Gson gson = new GsonBuilder().create();
                    String stringJson = gson.toJson(userActivity);
                    if (j != postIdList.size() - 1) {
                        activityAraay = activityAraay + stringJson + ",";
                    } else {
                        activityAraay = activityAraay + stringJson;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            activityAraay = "["+activityAraay+"]";
            System.out.println(activityAraay);
            UpdateActivity updateActivity = new UpdateActivity();
            updateActivity.execute(activityAraay);
        }
    }

    public class UpdateActivity extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            AsyncTaskData.postActivity(strings[0]);
            return null;
        }
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

                List colorList = new ArrayList();
                List itemList = new ArrayList();
                List yearList = new ArrayList();

                for (int i = 0; i <= colorArray.length() - 1;i++ ){
                    colorList.add(colorArray.getJSONObject(i).get("color_name"));
                }
                for(int i = 0; i<= itemArray.length() - 1;i++){
                    itemList.add(itemArray.getJSONObject(i).get("item_name"));
                }
                for(int i = 0; i<= yearArray.length() - 1;i++){
                    yearList.add(yearArray.getJSONObject(i).get("year_range"));
                }


                final ArrayAdapter<String> colorSpinnerAdapter = new ArrayAdapter<String>(con,android.R.layout.simple_spinner_item, colorList);
                colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

                final ArrayAdapter<String> itemSpinnerAdapter = new ArrayAdapter<String>(con,android.R.layout.simple_spinner_item, itemList);
                colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

                final ArrayAdapter<String> yearSpinnerAdapter = new ArrayAdapter<String>(con,android.R.layout.simple_spinner_item, yearList);
                colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

                colorSpinner.setAdapter(colorSpinnerAdapter);
                itemSpinner.setAdapter(itemSpinnerAdapter);
                yearSpinner.setAdapter(yearSpinnerAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
