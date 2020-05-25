package com.example.theresuser;


import android.graphics.drawable.ClipDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class About extends Fragment {

    View view;
    public About() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about, container, false);

        TotalItemsReused totalItemsReused = new TotalItemsReused();
        totalItemsReused.execute();
        return view;
    }

    public class TotalItemsReused extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            return AsyncTaskData.totalItemsReused();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject totalItems = new JSONObject(s);
                TextView itemsReused = (TextView)view.findViewById(R.id.itemsReused);
                itemsReused.setText(totalItems.getString("totalitemsclaimed"));

                TotalCarbonIntenisty totalCarbonIntenisty = new TotalCarbonIntenisty();
                totalCarbonIntenisty.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class TotalCarbonIntenisty extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            return AsyncTaskData.totalCarbonIntensity();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject totalCarbonIntensity = new JSONObject(s);
                TextView treesPlanted = (TextView)view.findViewById(R.id.treesPlanted);
                double trees = Double.parseDouble(totalCarbonIntensity.getString("totalcarbonintensityreduced")) * 0.0015;
                String tre = String.format("%.1f", trees);
                treesPlanted.setText(tre);
                addTree(trees);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addTree(double value) {
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.id.imageView1); imageList.add(R.id.imageView2);
        imageList.add(R.id.imageView3); imageList.add(R.id.imageView4);
        imageList.add(R.id.imageView5); imageList.add(R.id.imageView6);
        imageList.add(R.id.imageView7); imageList.add(R.id.imageView8);
        imageList.add(R.id.imageView9); imageList.add(R.id.imageView10);


        int newValue = (int)(value);
        for (int i = 0; i <= newValue -1 ;i++){
            System.out.println(i);
            ClipDrawable mImageDrawable1;
            ImageView imageView = (ImageView)view.findViewById(imageList.get(i));
            imageView.setVisibility(View.VISIBLE);
            mImageDrawable1 = (ClipDrawable) imageView.getDrawable();
            mImageDrawable1.setLevel(10000);
        }

        int nextValue = (int)((value - newValue)*10);
        if (nextValue >= 1){
            ClipDrawable mImageDrawable1;
            ImageView img = view.findViewById(imageList.get(newValue));
            img.setVisibility(View.VISIBLE);
            mImageDrawable1 = (ClipDrawable) img.getDrawable();
            mImageDrawable1.setLevel(nextValue*1000);
        }
    }
}
