package com.example.prashantgoyal.sunshine_self;

import android.Manifest;
import android.app.ActionBar;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

   private static String WEATHER_REQUEST_URL =
        "http://api.apixu.com/v1/forecast.json?key=ebaad8e6acfe4cbb8de132039180903&q=";
    private static String WEATHER_REQUEST_URL_RAW =
            "http://api.apixu.com/v1/forecast.json?key=ebaad8e6acfe4cbb8de132039180903&q=";
   Double locationLatitude;
   Double locationLongitude;
   String cityName;
   private RecyclerViewAdapter recyclerViewAdapter;
   private RecyclerView recyclerView;
   SwipeRefreshLayout swipeRefreshLayout;
   ArrayList<Weather> weatherList;
   ProgressBar progressBar;
   private boolean isUnitsFahrenheit = false;
   LocationManager locationManager;
   SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        recyclerView = findViewById(R.id.recycler_view_home);
        progressBar = findViewById(R.id.pb_loading_indicator);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city_name", "Gurgaon");
        editor.commit();
        editor.putBoolean("show_celsius", false);
        editor.commit();
        getLocationAndMakeRequest();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask();
                    weatherAsyncTask.execute(WEATHER_REQUEST_URL);
                }else {
                    Toast.makeText(getApplicationContext(), "Make sure the device has an internet connection", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask();
            weatherAsyncTask.execute(WEATHER_REQUEST_URL);
        }else {
            Toast.makeText(getApplicationContext(), "Make sure the device has an internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    //method to get location of user
    void getLocationAndMakeRequest(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for(String provider : providers){
                Location l = locationManager.getLastKnownLocation(provider);
                if(l == null)
                    continue;
                if(bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
                    bestLocation = l;
            }
                locationLatitude = bestLocation.getLatitude();
                locationLongitude = bestLocation.getLongitude();
                WEATHER_REQUEST_URL = WEATHER_REQUEST_URL + locationLatitude.toString() + "," + locationLongitude.toString() + "&days=10";
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask();
                    weatherAsyncTask.execute(WEATHER_REQUEST_URL);
                }else {
                    Toast.makeText(getApplicationContext(), "Make sure the device has an internet connection", Toast.LENGTH_SHORT).show();
                }
        }
    }
    // Result of location permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                getLocationAndMakeRequest();
        }
    }

    //Menu Creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    //menu onClick
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_main_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.menu_main_about_sunshine:
                Intent startAboutActivity = new Intent(this, AboutActivity.class);
                startActivity(startAboutActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class WeatherAsyncTask extends AsyncTask<String, Void, List<Weather>> implements SharedPreferences.OnSharedPreferenceChangeListener{

        LinearLayoutManager layoutManager;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            isUnitsFahrenheit = sharedPreferences.getBoolean("show_celsius", false);
            cityName = sharedPreferences.getString("city_name", "Gurgaon");
            WEATHER_REQUEST_URL = WEATHER_REQUEST_URL_RAW + latitudeLongitudeFromCityName(cityName);
            WEATHER_REQUEST_URL = WEATHER_REQUEST_URL_RAW + cityName + "&days=10";
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        protected List<Weather> doInBackground(String... urls) {
            if(urls.length <1 || urls[0] == null)
                return null;
            ArrayList<Weather> result = MakeRequestAndGetData.fetchWeatherData(WEATHER_REQUEST_URL);
            weatherList = result;
            return result;
        }

        @Override
        protected void onPostExecute(List<Weather> weathers) {
            super.onPostExecute(weathers);
            progressBar.setVisibility(View.INVISIBLE);
            layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), weatherList, isUnitsFahrenheit);
            recyclerView.setAdapter(recyclerViewAdapter);
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        public String latitudeLongitudeFromCityName(String cityName){

            Log.d("Selected City= ", cityName);
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> addresses;
            try{
                addresses = geocoder.getFromLocationName(cityName,5);
                if(addresses == null){
                    Toast.makeText(getApplicationContext(), "Invalid City Name", Toast.LENGTH_SHORT).show();
                }
                Address location = addresses.get(0);
                locationLatitude = location.getLatitude();
                locationLongitude = location.getLongitude();
                Log.d("Location lat and long-" , locationLatitude.toString() +", " + locationLongitude.toString());
            }catch(IOException e){
                e.printStackTrace();
            }
            return locationLatitude.toString() + "," + locationLongitude.toString() + "&days=10";
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("show_celsius")){
                WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask();
                weatherAsyncTask.execute(WEATHER_REQUEST_URL);
            }
            if(key.equals("city_name")){
                WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask();
                weatherAsyncTask.execute(WEATHER_REQUEST_URL);
            }
        }

    }
}
