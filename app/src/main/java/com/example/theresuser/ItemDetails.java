package com.example.theresuser;

import android.app.FragmentManager;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
//Class responsible for showing user the list of items available at a location
public class ItemDetails extends Fragment {
    JSONArray markerArray;
    String name;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_item_details, container, false);
        ListView listView = (ListView)view.findViewById(R.id.itemsListView);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Marker_Data",Context.MODE_PRIVATE);
        ArrayList<String> s = new ArrayList<>(sharedPreferences.getStringSet("Item",null));
        if (sharedPreferences.getString("markerArray",null) != null){
            try {
                markerArray = new JSONArray(sharedPreferences.getString("markerArray",null));
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


        //Search feature for the user to search for stuff
        final ArrayAdapter<String> itemListAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_text, itemList);
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
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("claim_data", Context.MODE_PRIVATE);
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
        return view;

    }


    //Getting the carbon intensity of the item selected by the user
    public class CarbonIntensityAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String itemName = "{\"item_name\":\""+name+"\"}";
            String carbonIntensity = AsyncTaskData.carbonIntensity(itemName);
            return carbonIntensity;
        }

        @Override
        protected void onPostExecute(String s) {
            String carbonIntensity = "";
            try {
                JSONArray carbonArray = new JSONArray(s);
                carbonIntensity = carbonArray.getJSONObject(0).getString("carbon_intensity");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Saving the data of carbon intensity to be used in the application
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("claim_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString("carbon_intensity",carbonIntensity);
            editor.apply();
            //final NavController navController = Navigation.findNavController(view);
            //navController.navigate(R.id.action_itemDetails_to_itemClaim);
        }
    }

}
