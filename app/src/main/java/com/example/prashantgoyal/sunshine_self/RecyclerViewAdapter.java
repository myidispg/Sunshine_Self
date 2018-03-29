package com.example.prashantgoyal.sunshine_self;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Prashant Goyal on 10-03-2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    LayoutInflater inflater;
    private ArrayList<Weather> weathers;
    private boolean isUnitsFahrenheit = false;

    public RecyclerViewAdapter(Context context, ArrayList<Weather> weatherArrayList, boolean isUnitsFahrenheit){
        weathers = weatherArrayList;
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.isUnitsFahrenheit = isUnitsFahrenheit;
    }

    public class RowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iconView;
        TextView dateView;
        TextView weatherDescriptionView;
        TextView highTempView;
        TextView lowTempView;

        public RowViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.weather_icon);
            dateView =  itemView.findViewById(R.id.date);
            weatherDescriptionView =  itemView.findViewById(R.id.weather_description);
            highTempView =  itemView.findViewById(R.id.high_temperature);
            lowTempView =  itemView.findViewById(R.id.low_temperature);
            itemView.setOnClickListener(this); //onClick
        }

        // function for onClick
        @Override
        public void onClick(View view) {
            Double maxWeather = weathers.get(getAdapterPosition()).getTemp_max();
            Double minWeather = weathers.get(getAdapterPosition()).getTemp_min();
            String date = weathers.get(getAdapterPosition()).getDate();
            String condition = weathers.get(getAdapterPosition()).getCondition();
            String humidity = weathers.get(getAdapterPosition()).getHumidity();
            String windSpeed = weathers.get(getAdapterPosition()).getWindSpeed();
            String sunrise = weathers.get(getAdapterPosition()).getSunriseTime();
            String sunset = weathers.get(getAdapterPosition()).getSunsetTime();
            URL url = weathers.get(getAdapterPosition()).getWeatherIcon();
            String urlString = url.toString();
            Intent intent = new Intent(mContext, WeatherDescriptionActivity.class);
            intent.putExtra("MAX WEATHER", maxWeather);
            intent.putExtra("MIN WEATHER", minWeather);
            intent.putExtra("Date", date);
            intent.putExtra("CONDITION", condition);
            intent.putExtra("HUMIDITY", humidity);
            intent.putExtra("WINDSPEED", windSpeed);
            intent.putExtra("SUNRISE", sunrise);
            intent.putExtra("SUNSET", sunset);
            intent.putExtra("IMAGE ICON", urlString);
            intent.putExtra("Fahrenheit", isUnitsFahrenheit);
            mContext.startActivity(intent);
        }
    }

    public class TodayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView dateView;
        ImageView iconView;
        TextView weatherDescriptionView;
        TextView highTempView;
        TextView lowTempView;
        public TodayViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.weather_icon_today);
            dateView =  itemView.findViewById(R.id.date_today);
            weatherDescriptionView =  itemView.findViewById(R.id.weather_description_today);
            highTempView =  itemView.findViewById(R.id.high_temperature_today);
            lowTempView =  itemView.findViewById(R.id.low_temperature_today);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Log.d("recycler view onClick", "position: " + getAdapterPosition());
            Double maxWeather = weathers.get(getAdapterPosition()).getTemp_max();
            Double minWeather = weathers.get(getAdapterPosition()).getTemp_min();
            String date = weathers.get(getAdapterPosition()).getDate();
            String condition = weathers.get(getAdapterPosition()).getCondition();
            String humidity = weathers.get(getAdapterPosition()).getHumidity();
            String windSpeed = weathers.get(getAdapterPosition()).getWindSpeed();
            String sunrise = weathers.get(getAdapterPosition()).getSunriseTime();
            String sunset = weathers.get(getAdapterPosition()).getSunsetTime();
            URL url = weathers.get(getAdapterPosition()).getWeatherIcon();
            String urlString = url.toString();
            Intent intent = new Intent(mContext, WeatherDescriptionActivity.class);
            intent.putExtra("MAX WEATHER", maxWeather);
            intent.putExtra("MIN WEATHER", minWeather);
            intent.putExtra("Date", date);
            intent.putExtra("CONDITION", condition);
            intent.putExtra("HUMIDITY", humidity);
            intent.putExtra("WINDSPEED", windSpeed);
            intent.putExtra("SUNRISE", sunrise);
            intent.putExtra("SUNSET", sunset);
            intent.putExtra("IMAGE ICON", urlString);
            intent.putExtra("Fahrenheit", isUnitsFahrenheit);
            mContext.startActivity(intent);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case Weather.TODAY_TYPE:
                view = inflater.inflate(R.layout.recyclerview_row_today, parent, false);
                return new TodayViewHolder(view); //onCLick
            case Weather.ROW_TYPE:
                view = inflater.inflate(R.layout.recyclerview_row, parent, false);
                return new RowViewHolder(view); //onClick
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if(weathers.get(position).getType() == 0)
            return Weather.TODAY_TYPE;
        else if(weathers.get(position).getType() == 1)
            return Weather.ROW_TYPE;
        else return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Weather weather = weathers.get(position);
        if(weather != null){
            switch (weather.getType()){
                case Weather.ROW_TYPE:
                    new DownloadImageTask(((RowViewHolder) holder).iconView).execute(weather.getWeatherIcon().toString());
                    String date = displayDayAndDate(weather.getDate());
                    ((RowViewHolder) holder).dateView.setText(date);
                    String weatherDescription = weather.getCondition();
                    ((RowViewHolder) holder).weatherDescriptionView.setText(weatherDescription);
                    Double doubleHighTemp, doubleLowTemp;
                    if(isUnitsFahrenheit) {
                        doubleHighTemp = weather.getTemp_max_fahrenheit();
                        String highTemp = String.format("%.0f", doubleHighTemp);
                        ((RowViewHolder) holder).highTempView.setTextSize(22);
                        ((RowViewHolder) holder).highTempView.setText(highTemp + " F");
                        doubleLowTemp = weather.getTemp_min_fahrenheit();
                        String lowTemp = String.format("%.0f", doubleLowTemp);
                        ((RowViewHolder) holder).lowTempView.setTextSize(22);
                        ((RowViewHolder) holder).lowTempView.setText(lowTemp + " F");
                    }
                    else {
                        doubleHighTemp = weather.getTemp_max();
                        String highTemp = String.format("%.0f", doubleHighTemp);
                        ((RowViewHolder) holder).highTempView.setText(highTemp + (char) 0x00B0);
                        doubleLowTemp = weather.getTemp_min();
                        String lowTemp = String.format("%.0f", doubleLowTemp);
                        ((RowViewHolder) holder).lowTempView.setText(lowTemp + (char) 0x00B0);
                    }
                    break;
                case Weather.TODAY_TYPE:
                    new DownloadImageTask(((TodayViewHolder) holder).iconView).execute(weather.getWeatherIcon().toString());
                    String dateToday = displayTodayDate(weather.getDate());
                    ((TodayViewHolder) holder).dateView.setText("Today, " + dateToday);
                    String weatherDescriptionToday = weather.getCondition();
                    ((TodayViewHolder) holder).weatherDescriptionView.setText(weatherDescriptionToday);
                    Double highTempToday, lowTempToday;
                    if(isUnitsFahrenheit){
                        highTempToday = weather.getTemp_max_fahrenheit();
                        String highTemp = String.format("%.0f", highTempToday);
                        ((TodayViewHolder) holder).highTempView.setTextSize(66);
                        ((TodayViewHolder) holder).highTempView.setText(highTemp + " F");
                        lowTempToday = weather.getTemp_min_fahrenheit();
                        String lowTemp = String.format("%.0f", lowTempToday);
                        ((TodayViewHolder) holder).lowTempView.setTextSize(30);
                        ((TodayViewHolder) holder).lowTempView.setText(lowTemp + " F");
                    }else
                    {
                        highTempToday = weather.getTemp_max();
                        String highTemp = String.format("%.0f", highTempToday);
                        ((TodayViewHolder) holder).highTempView.setText(highTemp + (char) 0x00B0);
                        lowTempToday = weather.getTemp_min();
                        String lowTemp = String.format("%.0f", lowTempToday);
                        ((TodayViewHolder) holder).lowTempView.setText(lowTemp + (char) 0x00B0);
                    }
//                    Double doubleHighTempToday = weather.getTemp_max();
//                    String highTempToday = String.format("%.0f", doubleHighTempToday);
//                    ((TodayViewHolder) holder).highTempView.setText(highTempToday + (char) 0x00B0);
//                    Double doubleLowTempToday = weather.getTemp_min();
//                    String lowTempToday = String.format("%.0f", doubleLowTempToday);
//                    ((TodayViewHolder) holder).lowTempView.setText(lowTempToday + (char) 0x00B0);


            }
        }
    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage){
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String ... urls){
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try{
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result){
            bmImage.setImageBitmap(result);
        }
    }

    public String displayDayAndDate(String urlDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date newDate = simpleDateFormat.parse(urlDate);
            simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            urlDate = simpleDateFormat.format(newDate);
            return urlDate;
        }catch (Exception e){
            e.printStackTrace();
        }
        return urlDate;
    }

    public String displayTodayDate(String urlDate)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date newDate = simpleDateFormat.parse(urlDate);
            simpleDateFormat = new SimpleDateFormat("MMMM dd");
            urlDate = simpleDateFormat.format(newDate);
            return urlDate;
        }catch (Exception e){
            e.printStackTrace();
        }
        return urlDate;
    }

}
