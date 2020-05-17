package com.example.theresuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListViewCustomAdapter extends ArrayAdapter<String[]> {

    Context newContext;
    int value;
    TextView nameTV,detailsTV,typeTV;

    public ListViewCustomAdapter(@NonNull Context context, int resource, ArrayList<String[]> currentItemsList) {
        super(context,resource,currentItemsList);
        newContext = context;
        value = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position)[0];
        String details = getItem(position)[1];
        String type = getItem(position)[2];

        LayoutInflater inflater = LayoutInflater.from(newContext);
        convertView = inflater.inflate(value,parent,false);

        nameTV = (TextView)convertView.findViewById(R.id.name);
        typeTV = (TextView)convertView.findViewById(R.id.type);
        detailsTV = (TextView)convertView.findViewById(R.id.details);
        nameTV.setText(name);
        typeTV.setText(type);
        detailsTV.setText(details);

        return convertView;

    }
}
