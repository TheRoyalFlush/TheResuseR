package com.example.theresuser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

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
import android.widget.TextView;
//Class handling the data posted in the database of the items that user is donating
public class PostDataReview extends Fragment {

    Integer colorId,typeId,itemId,yearId;
    Float latitude,longitude;
    Integer postId;
    Item itemObject;
    String color,type,year,item,carbonIntensity;
    TextView itemName,itemYear,itemType,itemColor,carbonIntensityMessage;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.content_post_data_review, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("post_data", Context.MODE_PRIVATE);
        if (sharedPreferences != null){
            //Getting the data saved in the shared preference
            colorId = Integer.parseInt(getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("color",null));
            typeId = Integer.parseInt(getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("type",null));
            itemId = Integer.parseInt(getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("item",null));
            yearId = Integer.parseInt(getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("year",null));
            latitude = getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getFloat("latitude",0);
            longitude = getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getFloat("longitude",0);
            postId = getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getInt("post_id",0);
            color = getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("color_name",null);
            type = getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("type_name",null);
            item = getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("item_name",null);
            year = getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("year_range",null);
            carbonIntensity = getActivity().getSharedPreferences("post_data",Context.MODE_PRIVATE).getString("carbon_intensity",null);
        }
        itemName = (TextView)view.findViewById(R.id.itemName);
        itemColor = (TextView)view.findViewById(R.id.item_color);
        itemType = (TextView)view.findViewById(R.id.item_type);
        itemYear = (TextView)view.findViewById(R.id.item_year);
        carbonIntensityMessage = (TextView)view.findViewById(R.id.carbon_message);

        //Showing the details of the items that the user is posting
        itemType.setText(type);
        itemYear.setText(year);
        itemColor.setText(color);
        itemName.setText(item);
        carbonIntensityMessage.setText(item+" contains"+carbonIntensity+" carbon intensity(amount of CO2 in kg per kg of the item)."+"\n"+" Thank you! for reducing that from going into atmosphere by providing the item for reuse through Resuser");

        itemObject = new Item(postId,colorId,itemId,yearId,latitude,longitude);

        Button postData = (Button)view.findViewById(R.id.postData);
        postData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendDataAysnc sendDataAysnc = new SendDataAysnc();
                sendDataAysnc.execute();
            }
        });
        return view;
    }

//Calling the api to save the item into the online database
    public class SendDataAysnc extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            //AsyncTaskData.postItem(itemObject);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
            builder.setTitle("Post Created").setMessage("Your post has been uploaded and can be seen by people nearby.")
                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final NavController navController = Navigation.findNavController(view);
                    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.donateStuff,true).build();
                    //navController.navigate(R.id.action_postDataReview_to_donateStuff,null,navOptions);

                }
            });

            AlertDialog alertDialog =builder.create();
            alertDialog.show();
        }
    }

}
