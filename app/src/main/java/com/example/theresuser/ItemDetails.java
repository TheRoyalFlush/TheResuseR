package com.example.theresuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemDetails extends AppCompatActivity {
    JSONArray markerArray;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView listView = (ListView)findViewById(R.id.itemsListView);

        Intent intent = getIntent();
        ArrayList<String> s = intent.getStringArrayListExtra("Item");
        if (intent.getStringExtra("markerArray") != null){
            try {
                markerArray = new JSONArray(intent.getStringExtra("markerArray"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final List<String> postId = new ArrayList<>();
        List<String> itemList = new ArrayList<>();
        for (int i = 0; i <= markerArray.length() - 1;i++){
            try {
                if (s != null && s.contains(markerArray.getJSONObject(i).getString("post_id"))){
                        itemList.add(markerArray.getJSONObject(i).getString("item_name"));
                        postId.add(markerArray.getJSONObject(i).getString("post_id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final ArrayAdapter<String> itemListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemList);
        itemListAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        listView.setAdapter(itemListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemContent = "";
                for (int i = 0; i <= markerArray.length() -1;i++){
                    try {
                        if (markerArray.getJSONObject(i).getString("post_id").equals(postId.get(position))){
                            itemContent = String.valueOf(markerArray.getJSONObject(i));
                            name = markerArray.getJSONObject(i).getString("item_name");
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("claim_data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor =sharedPreferences.edit();
                            editor.putString("item_content",itemContent);
                            editor.apply();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                CarbonIntensityAsyncTask carbonIntensityAsyncTask = new CarbonIntensityAsyncTask();
                carbonIntensityAsyncTask.execute();
            }
        });

    }

    public class CarbonIntensityAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String itemName = "{\"item_name\":\""+name+"\"}";
            System.out.println(itemName);
            String carbonIntensity = AsyncTaskData.carbonIntensity(itemName);
            return carbonIntensity;
        }

        @Override
        protected void onPostExecute(String s) {
            String carbonIntensity = "";
            try {
                JSONObject carbonObject = new JSONObject(s);
                carbonObject.getString("carbon_intensity");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("claim_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString("carbon_intensity",carbonIntensity);
            editor.apply();
            Intent intent = new Intent(getApplicationContext(),ItemClaim.class);
            startActivity(intent);
        }
    }

}
