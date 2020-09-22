package com.example.theresuser;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.nitri.gauge.Gauge;


import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
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
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//Fragment to populate the dashboard
public class Dashboard extends Fragment {

   GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 101;
    Context con;
    View view;
    double ciWeight = 0;
    double numKm = 0;
    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        account = GoogleSignIn.getLastSignedInAccount(con);
        if (account == null){
            Toast.makeText(con,getString(R.string.signin),Toast.LENGTH_LONG).show();
            //navController.navigate(R.id.action_dashboard_to_login);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(con, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        else {
            UserActivityData userActivityData = new UserActivityData();
            userActivityData.execute();
        }

        SeekBar seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            TextView textView = (TextView)view.findViewById(R.id.contribution);
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0){
                    textView.setText(" ");
                }
                else if (progress >= 10 && progress < 20){
                    //textView.setText(getString(R.string.red)+" "+(int)(ciWeight)+" "+getString(R.string.kgreduced)+" "+(int)(numKm)+" "+getString(R.string.km));
                    textView.setText(getString(R.string.red));
                }
                else if (progress >=20 && progress < 40){
                    textView.setText(getString(R.string.red)+" "+(int)(ciWeight)+" kgs");
                }
                else if (progress >= 40 && progress < 60){
                    textView.setText(getString(R.string.red)+" "+(int)(ciWeight)+" kgs of CO2 from going into atmosphser");
                }
                else if (progress >= 60 && progress <80){
                    textView.setText(getString(R.string.red)+" "+(int)(ciWeight)+" "+getString(R.string.kgreduced));
                }
                else if (progress >=80 && progress <= 100){
                    textView.setText(getString(R.string.red)+" "+(int)(ciWeight)+" "+getString(R.string.kgreduced)+" "+(int)(numKm)+" "+getString(R.string.km));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        } catch (ApiException e) {
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
    public class UserActivityData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            TextView username = (TextView)view.findViewById(R.id.username);
            username.setText(getString(R.string.welcom)+" "+account.getGivenName());
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
            if (s != null) {
                try {
                    JSONArray activityArray = new JSONArray(s);
                    for (int i = 0; i <= activityArray.length() - 1; i++) {
                        double holder = Double.valueOf(activityArray.getJSONObject(i).getString("carbon_intensity")) * Double.valueOf(activityArray.getJSONObject(i).getString("kg"));
                        ciWeight = ciWeight + holder;
                    }
                    numKm = ciWeight * 3.4568;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            TopThree topThree = new TopThree();
            topThree.execute();
        }

        //Getting the top three contributers
        public class TopThree extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... strings) {
                String topThreeRequest = "{\"user_email\":\"" + account.getEmail() + "\",\"month\":" + 5 +"}";
                System.out.println(topThreeRequest);
                return AsyncTaskData.topThree(topThreeRequest);
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONArray topThreeArray = new JSONArray(s);
                    int userRank = 0;
                    for (int i = 0;i<=topThreeArray.length() - 1;i++){
                        if (topThreeArray.getJSONObject(i).getString("user_email").equals(account.getEmail())){
                            userRank = topThreeArray.getJSONObject(i).getInt("rank");
                        }
                    }
                    if (userRank <= 3){
                        userInTopThree(userRank);
                    }
                    else if (userRank > 3){
                        userNotInTopThree(userRank);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TotalItemsReused totalItemsReused = new TotalItemsReused();
                totalItemsReused.execute();
            }
        }
    }

    private void userInTopThree(final int userRank) {
        ListView topThreeList = (ListView)view.findViewById(R.id.topThreeList);
        List<String[]> topList = new ArrayList<>();
        for (int i = 0; i<= 2;i++){
            String rank = "none";
            if (i == userRank - 1){
                topList.add(new String[]{rank,account.getGivenName()});
            }
            else {
                topList.add(new String[]{rank,getString(R.string.contributer)});
            }
        }
        TopThreeClass adapter = new TopThreeClass(con,R.layout.leaderboard, (ArrayList<String[]>) topList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView imageView = (ImageView)view.findViewById(R.id.rank);
                if (position == userRank -1 ){
                    view.setBackgroundColor(Color.parseColor("#74d4c0"));
                }
                if (position == 0){
                    //view.setBackgroundColor(Color.parseColor("#ffd700"));
                    imageView.setImageResource(R.drawable.gold);
                }
                else if (position == 1){
                    //view.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    imageView.setImageResource(R.drawable.silver);
                }
                else if (position == 2){
                    //view.setBackgroundColor(Color.parseColor("#d28c47"));
                    imageView.setImageResource(R.drawable.bronze);
                }
                return view;
            }
        };
        topThreeList.setAdapter(adapter);
    }
    private void userNotInTopThree(final int userRank) {
        ListView topThreeList = (ListView)view.findViewById(R.id.topThreeList);
        List<String[]> topList = new ArrayList<>();
        for (int i = 0; i<= 2;i++){
            topList.add(new String[]{"",getString(R.string.contribute)});
        }
        topList.add(new String[]{"","#"+userRank+"   "+account.getGivenName()});
        TopThreeClass adapter = new TopThreeClass(con,R.layout.leaderboard, (ArrayList<String[]>) topList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                ImageView imageView = (ImageView)view.findViewById(R.id.rank);
                if (position == 0){
                    //view.setBackgroundColor(Color.parseColor("#ffd700"));
                    imageView.setImageResource(R.drawable.gold);
                }
                else if (position == 1){
                    //view.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    imageView.setImageResource(R.drawable.silver);
                }
                else if (position == 2){
                    //view.setBackgroundColor(Color.parseColor("#d28c47"));
                    imageView.setImageResource(R.drawable.bronze);
                }
                else if (position == 3){
                    view.setBackgroundColor(Color.parseColor("#74d4c0"));
                }
                return view;
            }
        };
        topThreeList.setAdapter(adapter);
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
