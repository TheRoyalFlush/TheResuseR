package com.example.theresuser;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.nitri.gauge.Gauge;


import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dashboard extends Fragment {

    LineChart lineChart;
    HorizontalBarChart barChart;
    View view;
    GoogleSignInAccount account;
    PieChart pieChart;
    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //final NavController navController = Navigation.findNavController(view);
         account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null){
          //  navController.navigate(R.id.action_dashboard_to_login);
        }
        TextView welcome = (TextView)view.findViewById(R.id.welcome);
        welcome.setText("WELCOME "+account.getDisplayName());


        lineChart = (LineChart)view.findViewById(R.id.lineChart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.animate();

        barChart = (HorizontalBarChart)view.findViewById(R.id.barChart);
        pieChart = (PieChart)view.findViewById(R.id.pieChart);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int off = (int) (height*0.5);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)pieChart.getLayoutParams();
        params.setMargins(0,0,0,-off);
        pieChart.setLayoutParams(params);

        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(17,"0-20 kgs"));
        values.add(new PieEntry(17,"20-40 kgs"));
        values.add(new PieEntry(16,"40-60 kgs"));

        PieDataSet pieDataSet = new PieDataSet(values,"V");
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setColors(Color.RED,Color.YELLOW,Color.GREEN);
        pieDataSet.setValueTextColor(Color.BLACK);



        PieData data = new PieData(pieDataSet);
        data.setValueTextSize(0f);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setMaxAngle(180f);
        pieChart.setRotationAngle(180f);
        pieChart.setData(data);
        pieChart.invalidate();

        UserActivityData userActivityData = new UserActivityData();
        userActivityData.execute();

        return view;
    }


    public class UserActivityData extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
            String userEmail = "";
            if (account != null){
                userEmail = "{\"user_email\":\""+account.getEmail()+"\"}";
            }
            System.out.println(userEmail);
            return AsyncTaskData.userActivity(userEmail);
        }

        @Override
        protected void onPostExecute(String s) {
            List<Date> dateList = new ArrayList<>();
            List<Float> dateValue = new ArrayList<>();
            float totalWeight = 0;
            try {
                Set<Date> dateSet = new HashSet<>();
                JSONArray activityArray = new JSONArray(s);
                for (int i = 0; i<= activityArray.length() - 1; i++){
                    totalWeight = totalWeight + Float.valueOf(activityArray.getJSONObject(i).getString("kg"));
                    Date date = new Date(activityArray.getJSONObject(i).getString("contributed_date"));
                    dateSet.add(date);
                }
                System.out.println(totalWeight);
                dateList = new ArrayList<>(dateSet);
                dateValue = new ArrayList<>();
                for (int j =0; j<= dateList.size() -1;j++){
                    float value = 0;
                    for (int k = 0; k <= activityArray.length() - 1; k++){
                        Date date1 = new Date(activityArray.getJSONObject(k).getString("contributed_date"));
                        if (date1.getDate() == dateList.get(j).getDate() && date1.getMonth() == dateList.get(j).getMonth()){
                            float total = Float.valueOf(activityArray.getJSONObject(k).getString("kg")) * Float.valueOf(activityArray.getJSONObject(k).getString("carbon_intensity"));
                            value = value + total;
                        }
                    }
                    System.out.println(value);
                    dateValue.add(value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<Entry> values = new ArrayList<>();
            float totalValue = 0;
            for (int i = 0;i <= dateList.size()-1; i++){
                totalValue = totalValue + dateValue.get(i);
                values.add(new Entry(i,totalValue));
            }

            LineDataSet set1 = new LineDataSet(values,"DS1");

            set1.setFillAlpha(110);
            set1.setLineWidth(4f);
            set1.setColor(Color.parseColor("#009688"));
            set1.setValueTextColor(Color.parseColor("#009688"));
            set1.setValueTextSize(12f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData lineData = new LineData(dataSets);
            lineData.notifyDataChanged();

            lineChart.setData(lineData);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

            if (totalWeight > 0 && totalWeight <=20){
                pieChart.setCenterText(String.valueOf(totalWeight));
                pieChart.setCenterTextSize(30f);
                pieChart.setCenterTextColor(Color.RED);
                pieChart.invalidate();

            }
            else if (totalWeight > 20 && totalWeight <=40){
                pieChart.setCenterText(String.valueOf(totalWeight));
                pieChart.setCenterTextSize(30f);
                pieChart.setCenterTextColor(Color.YELLOW);
                pieChart.invalidate();
            }
            else if (totalWeight > 40 ){
                pieChart.setCenterText(String.valueOf(totalWeight));
                pieChart.setCenterTextSize(30f);
                pieChart.setCenterTextColor(Color.GREEN);
                pieChart.invalidate();
            }

            TopThree topThree = new TopThree();
            topThree.execute();
        }
    }

    public class TopThree extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            Date date = new Date();
            int month = date.getMonth() + 1;
            String topThreeRequest = "{\"user_email\":\""+account.getEmail()+"\",\"month\":"+month+"}";
            System.out.println(topThreeRequest);
            return AsyncTaskData.topThree(topThreeRequest);
        }

        @Override
        protected void onPostExecute(String s) {
            List<String> topThreeList = new ArrayList<>();
            int counter = 0;
            try {
                JSONArray topThreeArray = new JSONArray(s);
                for (int i = 0; i <= topThreeArray.length() - 1;i++) {
                    String user = topThreeArray.getJSONObject(i).getString("user_email");
                    topThreeList.add(user);
                    if (user.equals(account.getEmail())){
                        counter += 1;
                    }
                }
                if (counter == 1){
                    userNotInTopThree(topThreeArray);
                }
                if(counter == 2){
                    userInTopThree(topThreeArray);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void userNotInTopThree(JSONArray topThreeArray) throws JSONException {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i<= topThreeArray.length()-1;i++){
            barEntries.add(new BarEntry(i*2, Float.parseFloat(topThreeArray.getJSONObject(i).getString("total_ci"))));
        }

        BarDataSet set1;
        set1 = new BarDataSet(barEntries,"DS1");

        BarData barData = new BarData(set1);
        barData.setBarWidth(1f);
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    private void userInTopThree(JSONArray topThreeArray) throws JSONException {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i<= topThreeArray.length()-2;i++){
            System.out.println(topThreeArray.getJSONObject(i).getString("total_ci"));
            barEntries.add(new BarEntry(i*2, Float.parseFloat(topThreeArray.getJSONObject(i).getString("total_ci"))));
        }

        BarDataSet set1;
        set1 = new BarDataSet(barEntries,"DS1");

        BarData barData = new BarData(set1);
        barData.setBarWidth(1f);
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

}