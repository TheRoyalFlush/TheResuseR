package com.example.theresuser;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.nitri.gauge.Gauge;


import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
//Fragment to populate the dashboard
public class Dashboard extends Fragment {

    LineChart lineChart;
    HorizontalBarChart barChart;
    View view;
    GoogleSignInAccount account;
    PieChart pieChart;
    Spinner filterSpinner;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 101;
    Context con;
    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        filterSpinner = (Spinner) view.findViewById(R.id.filterSpinnerLine);

        //Setting up charts
        lineChart = (LineChart) view.findViewById(R.id.lineChart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.animate();

        barChart = (HorizontalBarChart) view.findViewById(R.id.barChart);
        pieChart = (PieChart) view.findViewById(R.id.pieChart);

        lineChart.setNoDataText("No activities recorded for this month.");
        barChart.setNoDataText("No activities recorded for this month.");

        //Display display = con.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int off = (int) (height * 0.5);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) pieChart.getLayoutParams();
        params.setMargins(0, 0, 0, -off);
        pieChart.setLayoutParams(params);
//Setting up spinner
        List<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        final ArrayAdapter<String> filterSpinnerAdapter = new ArrayAdapter<String>(con, android.R.layout.simple_spinner_item, months);
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

        filterSpinner.setAdapter(filterSpinnerAdapter);
        Date monthDate = new Date();
        filterSpinner.setSelection(monthDate.getMonth());
        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(17, "0-20 kgs"));
        values.add(new PieEntry(17, "20-40 kgs"));
        values.add(new PieEntry(16, "40-60 kgs"));

        PieDataSet pieDataSet = new PieDataSet(values, "V");
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setColors(Color.RED, Color.YELLOW, Color.GREEN);
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
        //final NavController navController = Navigation.findNavController(view);
        account = GoogleSignIn.getLastSignedInAccount(con);
        if (account == null){
            Toast.makeText(con,"Sign In to your Google Account first.",Toast.LENGTH_LONG).show();
            //navController.navigate(R.id.action_dashboard_to_login);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(con, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        else {
            TextView welcome = (TextView) view.findViewById(R.id.welcome);
            welcome.setText(getString(R.string.welcome) + account.getDisplayName());
            UserActivityData userActivityData = new UserActivityData();
            userActivityData.execute();

            filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("selected");
                    UserActivityData userActivityData = new UserActivityData();
                    userActivityData.execute();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        con = context;

    }
    //Getting user to signin
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount Gaccount = completedTask.getResult(ApiException.class);
            System.out.println(Gaccount.getDisplayName());
            UserDetails userDetails = new UserDetails(Gaccount.getEmail(),Gaccount.getGivenName());
            Gson gson = new GsonBuilder().create();
            String stringJson = gson.toJson(userDetails);
            account = GoogleSignIn.getLastSignedInAccount(con);
            Registeruser registerUser = new Registeruser();
            registerUser.execute(stringJson);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("error", "signInResult:failed code=" + e.getStatusCode());

        }
    }
//Registering user
    public class Registeruser extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            AsyncTaskData.registerUser(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            UserActivityData userActivityData = new UserActivityData();
            userActivityData.execute();
        }
    }
//Getting the user activities
    public class UserActivityData extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(con);
            String userEmail = "";
            if (account != null) {
                userEmail = "{\"user_email\":\"" + account.getEmail() + "\"}";
            }
            System.out.println(userEmail);
            return AsyncTaskData.userActivity(userEmail);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s!=null) {
                List<Date> dateList = new ArrayList<>();
                List<Float> dateValue = new ArrayList<>();
                float totalWeight = 0;
                try {
                    Set<Date> dateSet = new HashSet<>();
                    JSONArray activityArray = new JSONArray(s);
                    for (int i = 0; i <= activityArray.length() - 1; i++) {
                        Date date = new Date(activityArray.getJSONObject(i).getString("contributed_date"));
                        dateSet.add(date);
                        if (date.getMonth() == filterSpinner.getSelectedItemPosition()) {
                            totalWeight = totalWeight + Float.valueOf(activityArray.getJSONObject(i).getString("kg"));
                        }
                    }
                    dateList = new ArrayList<>(dateSet);
                    dateValue = new ArrayList<>();
                    for (int j = 0; j <= dateList.size() - 1; j++) {
                        float value = 0;
                        for (int k = 0; k <= activityArray.length() - 1; k++) {
                            Date date1 = new Date(activityArray.getJSONObject(k).getString("contributed_date"));
                            if (date1.getDate() == dateList.get(j).getDate() && date1.getMonth() == dateList.get(j).getMonth()) {
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
                for (int i = 0; i <= dateList.size() - 1; i++) {
                    if (dateList.get(i).getMonth() == filterSpinner.getSelectedItemPosition()) {
                        totalValue = totalValue + dateValue.get(i);
                        values.add(new Entry(i, totalValue));
                    }
                }
                if (values.size() != 0) {
                    LineDataSet set1 = new LineDataSet(values, "DS1");

                    set1.setFillAlpha(110);
                    set1.setLineWidth(4f);
                    set1.setColor(Color.parseColor("#009688"));
                    set1.setValueTextColor(Color.parseColor("#009688"));
                    set1.setValueTextSize(12f);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);

                    LineData lineData = new LineData(dataSets);
                    lineData.notifyDataChanged();
                    //Adding values to the charts
                    lineChart.setData(lineData);
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                } else {
                    lineChart.clear();
                    lineChart.invalidate();

                }
                if (totalWeight >= 0 && totalWeight <= 20) {
                    pieChart.setCenterText(String.valueOf(totalWeight) + getString(R.string.kg));
                    pieChart.setCenterTextSize(30f);
                    pieChart.setCenterTextColor(Color.RED);
                    pieChart.invalidate();

                } else if (totalWeight > 20 && totalWeight <= 40) {
                    pieChart.setCenterText(String.valueOf(totalWeight) + getString(R.string.kg));
                    pieChart.setCenterTextSize(30f);
                    pieChart.setCenterTextColor(Color.YELLOW);
                    pieChart.invalidate();
                } else if (totalWeight > 40) {
                    pieChart.setCenterText(String.valueOf(totalWeight) + getString(R.string.kg));
                    pieChart.setCenterTextSize(30f);
                    pieChart.setCenterTextColor(Color.GREEN);
                    pieChart.invalidate();
                }

                TopThree topThree = new TopThree();
                topThree.execute();
            }
        }
    }
//Getting the top three contributers
    public class TopThree extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String topThreeRequest = "{\"user_email\":\""+account.getEmail()+"\",\"month\":"+(filterSpinner.getSelectedItemPosition()+1)+"}";
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
                    Toast.makeText(getActivity(),"Congratulations! you are one of the top three contributers.",Toast.LENGTH_LONG).show();
                }

                if (topThreeArray.length() == 0){
                    barChart.clear();
                    barChart.invalidate();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
//CHecking if user in the top three contributers
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
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
        if (barEntries.size() == 0){
            barChart.clear();
            barChart.invalidate();
        }
    }
//Populating the bar chart
    private void userInTopThree(JSONArray topThreeArray) throws JSONException {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i<= topThreeArray.length()-2;i++){
            barEntries.add(new BarEntry(i*2, Float.parseFloat(topThreeArray.getJSONObject(i).getString("total_ci"))));
        }

        BarDataSet set1;
        set1 = new BarDataSet(barEntries,"DS1");

        BarData barData = new BarData(set1);
        barData.setBarWidth(1f);
        barChart.setData(barData);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

}
