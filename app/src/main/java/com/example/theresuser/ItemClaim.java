package com.example.theresuser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

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
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

//Class responsible for handeling the data when user claims an object
public class ItemClaim extends Fragment {
    String data,carbonIntensity,latitude,longitude,userLatitude,userLongitude,name,type,year,color,postId;
    TextView itemName,itemYear,itemType,itemColor,carbonIntensityMessage;
    Button claim,route;
    float totalCI;
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
        route = (Button)view.findViewById(R.id.route);

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
            postId = jsonObject.getString("post_id");

            itemName.setText(" "+name);
            itemColor.setText(" "+color);
            itemType.setText(" "+type);
            itemYear.setText(" "+year+" years old");
            totalCI = Float.parseFloat(carbonIntensity)*Float.valueOf(jsonObject.getString("kg"));
            totalCI = Math.round(totalCI);
            carbonIntensityMessage.setText(name+" "+getString(R.string.contains)+" "+totalCI+" "+getString(R.string.CImsg));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(Double.valueOf(userLatitude));
        final float[] distance = new float[1];
        Location.distanceBetween(Double.valueOf(userLatitude),Double.valueOf(userLongitude),Double.valueOf(latitude),Double.valueOf(longitude),distance);

        claim.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (distance[0] < 50) {

                    CheckAvailability checkAvailability = new CheckAvailability();
                    checkAvailability.execute();
                }
                else{
                    Toast.makeText(getActivity(),getString(R.string.neartheitem),Toast.LENGTH_LONG).show();
                }
            }
        });

        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?saddr=" + userLatitude + "," + userLongitude + "&daddr=" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        return view;
    }

    public class CheckAvailability extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String allItems = AsyncTaskData.getMapData();
            return allItems;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Boolean check = false;
                JSONArray allItems = new JSONArray(s);
                for (int i=0;i<=allItems.length()-1;i++){
                    if (postId.equals(allItems.getJSONObject(i).getString("post_id"))){
                        check = true;
                    }
                }
                if (check){
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.popup_activity);
                    dialog.setCanceledOnTouchOutside(false);
                    TextView treeMsg = (TextView)dialog.findViewById(R.id.treeMsg);
                    treeMsg.setText(getString(R.string.green));
                    TextView close = (TextView)dialog.findViewById(R.id.close2);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            ClaimAsyncTask claimAsyncTask = new ClaimAsyncTask();
                            claimAsyncTask.execute();
                        }
                    });
                    dialog.show();
                }
                else {
                    Toast.makeText(getActivity(),"This item has already been claimed.",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
            builder.setTitle(getString(R.string.itemClaim)).setMessage(getString(R.string.claimMsg)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
