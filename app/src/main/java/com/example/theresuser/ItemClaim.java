package com.example.theresuser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

//Class responsible for handeling the data when user claims an object
public class ItemClaim extends Fragment {
    String data,carbonIntensity,latitude,longitude,userLatitude,userLongitude,name,type,year,color;
    TextView itemName,itemYear,itemType,itemColor,carbonIntensityMessage;
    Button claim;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_item_claim, container, false);

        //Getting all the saved data of the item that the user has selcted
        data = getActivity().getSharedPreferences("claim_data", Context.MODE_PRIVATE).getString("item_content",null);
        userLatitude = getActivity().getSharedPreferences("claim_data",Context.MODE_PRIVATE).getString("user_latitude",null);
        userLongitude = getActivity().getSharedPreferences("claim_data",Context.MODE_PRIVATE).getString("user_longitude",null);
        itemName = (TextView)view.findViewById(R.id.item_name);
        itemColor = (TextView)view.findViewById(R.id.item_color);
        itemType = (TextView)view.findViewById(R.id.item_type);
        itemYear = (TextView)view.findViewById(R.id.item_year);
        carbonIntensityMessage = (TextView)view.findViewById(R.id.carbon_message);
        claim = (Button)view.findViewById(R.id.claim);

        try {
            //Populating all the data for the user to review
            JSONObject jsonObject = new JSONObject(data);
            latitude = jsonObject.getString("latitude");
            longitude = jsonObject.getString("longitude");
            name = jsonObject.getString("item_name");
            type = jsonObject.getString("type_name");
            year = jsonObject.getString("year_range");
            color = jsonObject.getString("color_name");
            carbonIntensity = jsonObject.getString("carbon_intensity");

            itemName.setText(name);
            itemColor.setText(color);
            itemType.setText(type);
            itemYear.setText(year);
            carbonIntensityMessage.setText(name+" contains "+carbonIntensity+" carbon intensity(amount of CO2 in kg per kg of the item). Thank you! for reducing that from going into atmosphere by picking the item for reuse through Resuser");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClaimAsyncTask claimAsyncTask = new ClaimAsyncTask();
                claimAsyncTask.execute();
            }
        });

        return view;
    }
    //Getting the google maps with the navigation from users location to the item
    public void Navigate(View view){
        String uri = "http://maps.google.com/maps?saddr=" + userLatitude + "," + userLongitude + "&daddr=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
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
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
            Date date = new Date();
            System.out.println(date);
            String activityAraay = "";
            int id = 0;
            try {
                JSONObject idJson = new JSONObject(data);
                id = Integer.parseInt(idJson.getString("post_id"));
                JSONArray arrayData = new JSONArray(s);
                for(int i = 0; i <= arrayData.length()-1; i++){
                    System.out.println(Integer.valueOf(arrayData.getJSONObject(i).getString("record_id")));
                    if(Integer.valueOf(arrayData.getJSONObject(i).getString("record_id")) > counter){
                        counter = Integer.valueOf(arrayData.getJSONObject(i).getString("record_id"));
                    }
                }
                counter += 1;
                UserActivity userActivity = new UserActivity(account.getEmail(),counter,id,0,date);
                Gson gson = new GsonBuilder().create();
                activityAraay = gson.toJson(userActivity);

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

        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
            builder.setTitle("Item Claimed!!").setMessage("The item has been claimed by you.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final NavController navController = Navigation.findNavController(view);
                    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.findStuff,true).build();
                    navController.navigate(R.id.action_itemClaim_to_findStuff,null,navOptions);
                }
            });

            AlertDialog alertDialog =builder.create();
            alertDialog.show();
        }
    }

}
