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

import android.view.View;
import android.widget.TextView;
//Class handling the data posted in the database of the items that user is donating
public class PostDataReview extends AppCompatActivity {

    Integer colorId,typeId,itemId,yearId;
    Float latitude,longitude;
    Integer postId;
    Item itemObject;
    String color,type,year,item,carbonIntensity;
    TextView itemName,itemYear,itemType,itemColor,carbonIntensityMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_data_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Review Item Details");
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("post_data", Context.MODE_PRIVATE);
        if (sharedPreferences != null){
            //Getting the data saved in the shared preference
            colorId = Integer.parseInt(this.getSharedPreferences("post_data",MODE_PRIVATE).getString("color",null));
            typeId = Integer.parseInt(this.getSharedPreferences("post_data",MODE_PRIVATE).getString("type",null));
            itemId = Integer.parseInt(this.getSharedPreferences("post_data",MODE_PRIVATE).getString("item",null));
            yearId = Integer.parseInt(this.getSharedPreferences("post_data",MODE_PRIVATE).getString("year",null));
            latitude = this.getSharedPreferences("post_data",MODE_PRIVATE).getFloat("latitude",0);
            longitude = this.getSharedPreferences("post_data",MODE_PRIVATE).getFloat("longitude",0);
            postId = this.getSharedPreferences("post_data",MODE_PRIVATE).getInt("post_id",0);
            color = this.getSharedPreferences("post_data",MODE_PRIVATE).getString("color_name",null);
            type = this.getSharedPreferences("post_data",MODE_PRIVATE).getString("type_name",null);
            item = this.getSharedPreferences("post_data",MODE_PRIVATE).getString("item_name",null);
            year = this.getSharedPreferences("post_data",MODE_PRIVATE).getString("year_range",null);
            carbonIntensity = this.getSharedPreferences("post_data",MODE_PRIVATE).getString("carbon_intensity",null);
        }
        itemName = (TextView)findViewById(R.id.itemName);
        itemColor = (TextView)findViewById(R.id.item_color);
        itemType = (TextView)findViewById(R.id.item_type);
        itemYear = (TextView)findViewById(R.id.item_year);
        carbonIntensityMessage = (TextView)findViewById(R.id.carbon_message);

        //Showing the details of the items that the user is posting
        itemType.setText(type);
        itemYear.setText(year);
        itemColor.setText(color);
        itemName.setText(item);
        carbonIntensityMessage.setText(item+" contains"+carbonIntensity+" carbon intensity(amount of CO2 in kg per kg of the item)."+"\n"+" Thank you! for reducing that from going into atmosphere by providing the item for reuse through Resuser");

        itemObject = new Item(postId,colorId,itemId,yearId,typeId,latitude,longitude);
    }

    public void PostData(View view){
        SendDataAysnc sendDataAysnc = new SendDataAysnc();
        sendDataAysnc.execute();
    }
//Calling the api to save the item into the online database
    public class SendDataAysnc extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            AsyncTaskData.postItem(itemObject);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder builder =new AlertDialog.Builder(PostDataReview.this);
            builder.setTitle("Post Created").setMessage("Your post has been uploaded and can be seen by people nearby.")
                   .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }).setPositiveButton("Post New Item", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("postNewItem","newPost");
                    startActivity(intent);
                }
            });

            AlertDialog alertDialog =builder.create();
            alertDialog.show();
        }
    }

}
