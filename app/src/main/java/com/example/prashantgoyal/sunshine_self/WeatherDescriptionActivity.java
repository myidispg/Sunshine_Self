package com.example.prashantgoyal.sunshine_self;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherDescriptionActivity extends AppCompatActivity {

    TextView weatherDate;
    ImageView weatherIcon;
    TextView weatherDescription;
    TextView maxTemp;
    TextView minTemp;
    TextView Humidity;
    TextView Wind;
    TextView Sunrise;
    TextView Sunset;
    Intent intent;

    Double maxWeather;
    Double minWeather;
    String date;
    String condition;
    String humidity;
    String windSpeed;
    String sunrise;
    String sunset;
    String url;
    boolean isUnitsFahrenheit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_description);
        intent = getIntent();
        date = intent.getExtras().getString("Date");
        maxWeather = intent.getExtras().getDouble("MAX WEATHER");
        minWeather = intent.getExtras().getDouble("MIN WEATHER");
        condition = intent.getExtras().getString("CONDITION");
        humidity = intent.getExtras().getString("HUMIDITY");
        windSpeed = intent.getExtras().getString("WINDSPEED");
        sunrise = intent.getExtras().getString("SUNRISE");
        sunset = intent.getExtras().getString("SUNSET");
        url = intent.getExtras().getString("IMAGE ICON");
        isUnitsFahrenheit = intent.getExtras().getBoolean("Fahrenheit");

        if(isUnitsFahrenheit){
            weatherIcon = findViewById(R.id.desc_weather_icon);
            new DownloadImageTask(weatherIcon).execute(url);
            weatherDate = findViewById(R.id.desc_date);
            date = displayTodayDate(date);
            weatherDate.setText(date);
            weatherDescription = findViewById(R.id.desc_weather_condition);
            weatherDescription.setText(condition);
            maxTemp = findViewById(R.id.desc_high_temp);
            String highTemp = String.format("%.0f", maxWeather);
            maxTemp.setText(highTemp + "F");
            minTemp = findViewById(R.id.desc_low_temp);
            String lowTemp = String.format("%.0f", minWeather);
            minTemp.setText(lowTemp);
            Humidity = findViewById(R.id.desc_humidity_value);
            Humidity.setText(humidity + "%");
            Wind = findViewById(R.id.desc_windspeed_value);
            Wind.setText(windSpeed + "km/hr");
            Sunrise = findViewById(R.id.desc_sunrise_value);
            Sunrise.setText(sunrise);
            Sunset = findViewById(R.id.desc_sunset_value);
            Sunset.setText(sunset);
        }else{
            weatherIcon = findViewById(R.id.desc_weather_icon);
            new DownloadImageTask(weatherIcon).execute(url);
            weatherDate = findViewById(R.id.desc_date);
            date = displayTodayDate(date);
            weatherDate.setText(date);
            weatherDescription = findViewById(R.id.desc_weather_condition);
            weatherDescription.setText(condition);
            maxTemp = findViewById(R.id.desc_high_temp);
            String highTemp = String.format("%.0f", maxWeather);
            maxTemp.setText(highTemp + (char) 0x00B0);
            minTemp = findViewById(R.id.desc_low_temp);
            String lowTemp = String.format("%.0f", minWeather);
            minTemp.setText(lowTemp);
            Humidity = findViewById(R.id.desc_humidity_value);
            Humidity.setText(humidity + "%");
            Wind = findViewById(R.id.desc_windspeed_value);
            Wind.setText(windSpeed + "km/hr");
            Sunrise = findViewById(R.id.desc_sunrise_value);
            Sunrise.setText(sunrise);
            Sunset = findViewById(R.id.desc_sunset_value);
            Sunset.setText(sunset);
        }
    }

    public String displayTodayDate(String urlDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date newDate = simpleDateFormat.parse(urlDate);
            simpleDateFormat = new SimpleDateFormat("MMMM dd");
            urlDate = simpleDateFormat.format(newDate);
            return urlDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlDate;
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result){
            bmImage.setImageBitmap(result);
        }
    }
}
