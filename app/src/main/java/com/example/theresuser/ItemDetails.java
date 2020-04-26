package com.example.theresuser;

import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemDetails extends AppCompatActivity {
    JSONArray markerArray;
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
                System.out.println(position);
                String itemContent = "";
                for (int i = 0; i <= markerArray.length() -1;i++){
                    try {
                        if (markerArray.getJSONObject(i).getString("post_id").equals(postId.get(position))){
                            itemContent = String.valueOf(markerArray.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(getApplicationContext(),ItemClaim.class);
                intent.putExtra("ItemDetails",itemContent);
                startActivity(intent);
            }
        });

    }

    public void MapsOpen(View view){
        String uri = "http://maps.google.com/maps?saddr=" + "-37.879154" + "," + "145.53172";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
        }
}
