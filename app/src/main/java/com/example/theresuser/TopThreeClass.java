package com.example.theresuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TopThreeClass extends ArrayAdapter<String[]> {

    TextView nameTV;
    ImageView rankTV;
    Context con;
    int value;

    public TopThreeClass(@NonNull Context context, int resource, ArrayList<String[]> currentItemsList) {
        super(context, resource,currentItemsList);
        con = context;
        value = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String rank = getItem(position)[0];
        String name = getItem(position)[1];

        LayoutInflater inflater = LayoutInflater.from(con);
        convertView = inflater.inflate(value,parent,false);

        nameTV = (TextView)convertView.findViewById(R.id.user);
        rankTV = (ImageView)convertView.findViewById(R.id.rank);

        nameTV.setText(name);

        //System.out.println("ksdbajh"+con.getResources().getIdentifier(rank, "drawable", con.getPackageName()));
        //rankTV.setImageResource(con.getResources().getIdentifier(rank, "drawable", con.getPackageName()));

        return convertView;
    }
}
