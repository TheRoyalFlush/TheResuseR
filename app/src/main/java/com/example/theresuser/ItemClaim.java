package com.example.theresuser;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
    String data,carbonIntensity,latitude,longitude,userLatitude,userLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_claim);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        data = getApplicationContext().getSharedPreferences("claim_data",MODE_PRIVATE).getString("item_content",null);
        carbonIntensity = getApplicationContext().getSharedPreferences("claim_data",MODE_PRIVATE).getString("carbon_intensity",null);
        userLatitude = getApplicationContext().getSharedPreferences("claim_data",MODE_PRIVATE).getString("user_latitude",null);
        userLongitude = getApplicationContext().getSharedPreferences("claim_data",MODE_PRIVATE).getString("user_longitude",null);
        TextView textView = (TextView)findViewById(R.id.tv);
        try {
            JSONObject jsonObject = new JSONObject(data);
            latitude = jsonObject.getString("latitude");
            longitude = jsonObject.getString("longitude");
            textView.setText(jsonObject.getString("item_name")+"\n"+jsonObject.getString("type_name")+
                    "\n"+jsonObject.getString("year_range")+"\n"+carbonIntensity);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void Navigate(View view){
        String uri = "http://maps.google.com/maps?saddr=" + userLatitude + "," + userLongitude + "&daddr=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
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

        }
    }


}
