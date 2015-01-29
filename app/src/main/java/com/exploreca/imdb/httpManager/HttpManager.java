package com.exploreca.imdb.httpManager;

/**
 * Created by ALOKNATH on 1/15/2015.
 */

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager {

    public static String getData(String uri) {

        BufferedReader reader = null;

        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public static String getData(String uri, String userName, String password) {

        BufferedReader reader = null;
        HttpURLConnection con = null;

        byte[] loginBytes = (userName + ":" + password).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        try {
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();

            con.addRequestProperty("Authorization", loginBuilder.toString());

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                int status = con.getResponseCode();
                Log.d("HttpManager", "HTTP response code: " + status);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

    }

    public static String getData(String uri, String API_VERSION, String CONTEXT_PATH, String API_KEY) {

        BufferedReader reader = null;
        HttpURLConnection con = null;

        //String value = "http://api.themoviedb.org/3/movie/upcoming?api_key=f47dd4de64c6ef630c2b0d50a087cc33";

        byte[] loginBytes = (API_VERSION + "/" + CONTEXT_PATH + "?api_key=" + API_KEY).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        Log.i("Movie_URL", API_VERSION + "/" + CONTEXT_PATH + "?api_key=" + API_KEY);

        try {
            URL url = new URL(uri+ API_VERSION + "/" + CONTEXT_PATH + "?api_key=" + API_KEY);
            Log.i("URL", url.toString());
            con = (HttpURLConnection) url.openConnection();
            //con.addRequestProperty("Authorization", loginBuilder.toString());

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            Log.i("WEBService", sb.toString());
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                int status = con.getResponseCode();
                Log.d("HttpManager", "HTTP response code: " + status);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}

