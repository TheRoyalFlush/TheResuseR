package com.example.theresuser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
//Class containing all the asynctasks to call the apis for get and post
public class AsyncTaskData {

    public static final String BASE_URL ="http://theresuser.me/";

    //Getting the data of the Spinners for selection the item kept on the kerb.
    public static String PopulateSpinner(String field){
        URL url = null;
        StringBuffer sb = null;
        HttpURLConnection connection = null;
        String spinnerData = "";
        try {
            url = new URL(BASE_URL + "allinone");
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                spinnerData += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection  .disconnect();
        }
        System.out.println(spinnerData);
        return spinnerData;
    }

    //Posting the item online to the database
    public static void postItem(String finalPostArray){
        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="post_items_list";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            //Gson gson = new GsonBuilder().create();
            //String stringJson = gson.toJson(finalPostArray);
            System.out.println(finalPostArray);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(finalPostArray.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(finalPostArray);
            out.close();
            Log.i("error",new Integer(connection.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    //Generating a new unique id for the posts by user
    public static String generateId() {
            URL url = null;
            StringBuffer sb = null;
            HttpURLConnection connection = null;
            String idData = "";
            try {
                url = new URL(BASE_URL + "allposteditems");
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(connection.getInputStream());
                while (inStream.hasNextLine()) {
                    idData += inStream.nextLine();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection  .disconnect();
            }
            return idData;

        }

        //Api call for getting the items posted to be poppulated on the maps
    public static String getMapData() {
        URL url = null;
        StringBuffer sb = null;
        HttpURLConnection connection = null;
        String mapData = "";
        try {
            url = new URL(BASE_URL + "posteditems");
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                mapData += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection  .disconnect();
        }
        System.out.println(mapData);
        return mapData;
    }

    //Post to api if someone claims the item
    public static void claimItem(String itemId){
        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="pickitem";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(itemId.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(itemId);
            out.close();
            Log.i("error",new Integer(connection.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    //Getting the carbon intensity of the items
    public static String carbonIntensity(String itemName) {
        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="carbon_intensity";
        String carbonData = "";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(itemName.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(itemName);
            out.close();
            Log.i("error",connection.getResponseMessage());
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                carbonData += inStream.nextLine();
            }
            System.out.println(carbonData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return carbonData;
    }

    public static String RecordId(){
        URL url = null;
        StringBuffer sb = null;
        HttpURLConnection connection = null;
        String recordData = "";
        try {
            url = new URL(BASE_URL + "allactivity");
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Log.i("error",new Integer(connection.getResponseCode()).toString());
            Scanner inStream = new Scanner(connection.getInputStream());
            Log.i("error",new Integer(connection.getResponseCode()).toString());
            while (inStream.hasNextLine()) {
                recordData += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection  .disconnect();
        }
        System.out.println(recordData);
        return recordData;
    }

    public static void postActivity(String finalPostArray){
        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="post_useractivity";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            //Gson gson = new GsonBuilder().create();
            //String stringJson = gson.toJson(finalPostArray);
            System.out.println(finalPostArray);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(finalPostArray.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(finalPostArray);
            out.close();
            Log.i("error",new Integer(connection.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    public static void registerUser(String finalPostArray){
        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="post_user";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            //Gson gson = new GsonBuilder().create();
            //String stringJson = gson.toJson(finalPostArray);
            System.out.println(finalPostArray);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(finalPostArray.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(finalPostArray);
            out.close();
            Log.i("error",new Integer(connection.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    public static String userActivity(String userName) {
        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="postedactivities";
        String userData = "";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(userName.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(userName);
            out.close();
            Log.i("error",connection.getResponseMessage());
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                userData += inStream.nextLine();
            }
            System.out.println(userData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return userData;
    }

    public static String topThree(String topThree) {

        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="top_three";
        String topThreeData = "";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(topThree.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(topThree);
            out.close();
            Log.i("error",connection.getResponseMessage());
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                topThreeData += inStream.nextLine();
            }
            System.out.println(topThreeData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return topThreeData;
    }

    public static String reminderDay(String location) {

        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="day";
        String reminderDay = "";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(location.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(location);
            out.close();
            Log.i("error",connection.getResponseMessage());
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                reminderDay += inStream.nextLine();
            }
            System.out.println(reminderDay);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return reminderDay;
    }
}
