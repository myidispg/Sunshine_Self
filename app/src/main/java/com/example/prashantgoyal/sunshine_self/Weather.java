package com.example.prashantgoyal.sunshine_self;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by Prashant Goyal on 10-03-2018.
 */

public class Weather{

    public static final int TODAY_TYPE = 0;
    public static final int ROW_TYPE = 1;

    private int type;
    private Double temp_max;
    private Double temp_min;
    private Double temp_max_fahrenheit;
    private Double temp_min_fahrenheit;
    private String date;
    private String condition;
    private String humidity;
    private String windSpeed;
    private String sunriseTime;
    private String sunsetTime;
    private URL weatherIcon;

    public Weather(int type, Double maxTemp, Double minTemp,Double temp_max_fahrenheit, Double temp_min_fahrenheit, String date, String condition, String humidity, String windSpeed, String sunriseTime, String sunsetTime, URL weatherIcon){
        this.type = type;
        this.temp_max = maxTemp;
        this.temp_min = minTemp;
        this.date = date;
        this.condition = condition;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
        this.weatherIcon = weatherIcon;
        this.temp_max_fahrenheit = temp_max_fahrenheit;
        this.temp_min_fahrenheit = temp_min_fahrenheit;
    }

    public Double getTemp_max() {
        return temp_max;
    }

    public Double getTemp_min() {
        return temp_min;
    }

    public String getDate() {
        return date;
    }

    public String getCondition() {
        return condition;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getSunriseTime() {
        return sunriseTime;
    }

    public String getSunsetTime() {
        return sunsetTime;
    }

    public URL getWeatherIcon() {
        return weatherIcon;
    }

    public int getType() {
        return type;
    }

    public Double getTemp_max_fahrenheit() {
        return temp_max_fahrenheit;
    }

    public Double getTemp_min_fahrenheit() {
        return temp_min_fahrenheit;
    }
}
