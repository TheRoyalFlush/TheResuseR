package com.example.theresuser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AsyncTaskData {

    public static final String BASE_URL ="http://theresuser.me/";

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

    public static void postItem(Item item){
        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath="post_items";
        try {
            url = new URL(BASE_URL + methodPath);
            System.out.println(url);
            Gson gson = new GsonBuilder().create();
            String stringJson = gson.toJson(item);
            System.out.println(stringJson);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(stringJson.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(stringJson);
            out.close();
            Log.i("error",new Integer(connection.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    public static String generateId() {
            URL url = null;
            StringBuffer sb = null;
            HttpURLConnection connection = null;
            String idData = "";
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
                    idData += inStream.nextLine();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection  .disconnect();
            }
            return idData;

        }

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
}
