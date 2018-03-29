package com.example.prashantgoyal.sunshine_self;

import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prashant Goyal on 10-03-2018.
 */

public class MakeRequestAndGetData {

    public static final String LOG_TAG = MakeRequestAndGetData.class.getSimpleName();

    private MakeRequestAndGetData(){

    }
    public static URL createURL(String stringURL){
        URL url = null;
        try{
            url = new URL(stringURL);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem building the url", e);
        }
        return url;
    }

    public static String makeHTTPRequest(URL url) throws IOException{
        String jsonResponse = "";
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the weather JSON results.", e);
        }finally {
            if(urlConnection != null)
                urlConnection.disconnect();
            if(inputStream != null)
                inputStream.close();

        }
        return jsonResponse;
    }

    public static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while(line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Weather> extractFeatureFromJSON(String weatherJSON){
        if(TextUtils.isEmpty(weatherJSON))
            return null;

        ArrayList<Weather> weathers = new ArrayList<>();
        try{
            JSONObject baseJSONResponse = new JSONObject(weatherJSON);
            JSONObject forecastJSONObject = baseJSONResponse.getJSONObject("forecast");
            JSONArray forecastdayJSONArray = forecastJSONObject.getJSONArray("forecastday");
            for(int i = 0; i<forecastdayJSONArray.length();i++){
            JSONObject currentDay = forecastdayJSONArray.getJSONObject(i);
            String currentDate = currentDay.getString("date");
            JSONObject dayJSONObject = currentDay.getJSONObject("day");
            Double maxTemp = dayJSONObject.getDouble("maxtemp_c");
            Double minTemp = dayJSONObject.getDouble("mintemp_c");
            String windSpeed = dayJSONObject.getString("maxwind_kph");
            String humididty = dayJSONObject.getString("avghumidity");
            JSONObject conditionJSONObject = dayJSONObject.getJSONObject("condition");
            String condition = conditionJSONObject.getString("text");
            String iconURLString = conditionJSONObject.getString("icon");
            iconURLString = "http:" + iconURLString;
            Double tempMaxFahrenheit = dayJSONObject.getDouble("maxtemp_f");
            Double tempMinFahrenheit = dayJSONObject.getDouble("mintemp_f");
            URL iconURL = null;
                try{
                    iconURL = new URL(iconURLString);
                }catch (MalformedURLException e){
                    Log.e(LOG_TAG, "Problem building the url", e);
                }
            JSONObject currentDayAstro = currentDay.getJSONObject("astro");
            String sunriseTime = currentDayAstro.getString("sunrise");
            String sunsetTime = currentDayAstro.getString("sunset");
            if(i == 0) {
                Weather weather = new Weather(Weather.TODAY_TYPE, maxTemp, minTemp, tempMaxFahrenheit, tempMinFahrenheit, currentDate, condition, humididty, windSpeed, sunriseTime, sunsetTime, iconURL);
                weathers.add(weather);
            }else{
                Weather weather = new Weather(Weather.ROW_TYPE, maxTemp, minTemp, tempMaxFahrenheit, tempMinFahrenheit, currentDate, condition, humididty, windSpeed, sunriseTime, sunsetTime, iconURL);
                weathers.add(weather);
            }
            }
        }catch(JSONException e){
            Log.e("MakeRequestAndGetData", "Problem parsing weather JSON response", e);
        }
        return weathers;
    }

    public static ArrayList<Weather> fetchWeatherData(String requestURL){
        URL url = createURL(requestURL);
        String jsonResponse = null;
        try{
            jsonResponse = makeHTTPRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }
        return extractFeatureFromJSON(jsonResponse);
    }


}
