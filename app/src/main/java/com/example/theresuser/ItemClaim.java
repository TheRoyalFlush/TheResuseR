package com.example.theresuser;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
//Class responsible for handeling the data when user claims an object
public class ItemClaim extends AppCompatActivity {
    String data,carbonIntensity,latitude,longitude,userLatitude,userLongitude,name,type,year,color;
    TextView itemName,itemYear,itemType,itemColor,carbonIntensityMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_claim);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Getting all the saved data of the item that the user has selcted
        data = getApplicationContext().getSharedPreferences("claim_data",MODE_PRIVATE).getString("item_content",null);
        carbonIntensity = getApplicationContext().getSharedPreferences("claim_data",MODE_PRIVATE).getString("carbon_intensity",null);
        userLatitude = getApplicationContext().getSharedPreferences("claim_data",MODE_PRIVATE).getString("user_latitude",null);
        userLongitude = getApplicationContext().getSharedPreferences("claim_data",MODE_PRIVATE).getString("user_longitude",null);
        itemName = (TextView)findViewById(R.id.item_name);
        itemColor = (TextView)findViewById(R.id.item_color);
        itemType = (TextView)findViewById(R.id.item_type);
        itemYear = (TextView)findViewById(R.id.item_year);
        carbonIntensityMessage = (TextView)findViewById(R.id.carbon_message);
        try {
            //Populating all the data for the user to review
            JSONObject jsonObject = new JSONObject(data);
            latitude = jsonObject.getString("latitude");
            longitude = jsonObject.getString("longitude");
            name = jsonObject.getString("item_name");
            type = jsonObject.getString("type_name");
            year = jsonObject.getString("year_range");
            color = jsonObject.getString("color_name");
            itemName.setText(name);
            itemColor.setText(color);
            itemType.setText(type);
            itemYear.setText(year);
            carbonIntensityMessage.setText(name+" contains "+carbonIntensity+" carbon intensity(amount of CO2 in kg per kg of the item). Thank you! for reducing that from going into atmosphere by picking the item for reuse through Resuser");

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    //Getting the google maps with the navigation from users location to the item
    public void Navigate(View view){
        String uri = "http://maps.google.com/maps?saddr=" + userLatitude + "," + userLongitude + "&daddr=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }
    //Function to request a claim on an item
    public void Claim(View view){
        ClaimAsyncTask claimAsyncTask = new ClaimAsyncTask();
        claimAsyncTask.execute();
    }
    //Running the api to save the data of claimed object
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
            AlertDialog.Builder builder =new AlertDialog.Builder(ItemClaim.this);
            builder.setTitle("Item Claimed!!").setMessage("The item has been claimed by you.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            });

            AlertDialog alertDialog =builder.create();
            alertDialog.show();
        }
    }


}
