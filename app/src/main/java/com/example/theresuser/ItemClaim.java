package com.example.theresuser;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemClaim extends AppCompatActivity {
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_claim);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        data = intent.getStringExtra("ItemDetails");
        TextView textView = (TextView)findViewById(R.id.tv);
        try {
            JSONObject jsonObject = new JSONObject(data);
            textView.setText(jsonObject.getString("item_name")+"\n"+jsonObject.getString("type_name")+
                    "\n"+jsonObject.getString("year_range"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void Claim(View view){
        ClaimAsyncTask claimAsyncTask = new ClaimAsyncTask();
        claimAsyncTask.execute();
    }

    public class ClaimAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String id = "";
            try {
                JSONObject idJson = new JSONObject(data);
                id = idJson.getString("post_id");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String itemId = "{\"post_id\":"+id+"}";
            System.out.println(itemId);
            AsyncTaskData.claimItem(itemId);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            CarbonIntensityAsyncTask carbonIntensityAsyncTask = new CarbonIntensityAsyncTask();
            carbonIntensityAsyncTask.execute();
        }
    }

    public class CarbonIntensityAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String name = "";
            try {
                JSONObject idJson = new JSONObject(data);
                name = idJson.getString("item_name");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String itemName = "{\"item_name\":\""+name+"\"}";
            System.out.println(itemName);
            AsyncTaskData.carbonIntensity(itemName);
            return null;
        }
    }

}