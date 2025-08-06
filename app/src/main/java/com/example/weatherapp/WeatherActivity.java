package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.IOException;
import android.widget.Button;
import android.widget.ImageView;
import okhttp3.*;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView temperatureText, locationText, weatherStatText, feelsLikeText, weatherText, minTempText, maxTempText;
    private EditText userCityInput;
    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey = "c7b0165ffcfa1810770957f3ca0c619b";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        temperatureText = findViewById(R.id.temperature);
        locationText = findViewById(R.id.cityName);
        weatherStatText = findViewById(R.id.weatherStatus);
        feelsLikeText = findViewById(R.id.feelsLike);
        minTempText = findViewById(R.id.minTemp);
        maxTempText = findViewById(R.id.maxTemp);
        weatherText = findViewById(R.id.weatherText);

        userCityInput = findViewById(R.id.editTextInput);
        Button searchButton = findViewById(R.id.searchButton);

        // This sets the default location.
        userCityInput.setOnClickListener(this);
        fetchWeather("Toronto");

        // Search button implementation
        searchButton.setOnClickListener(this);
    }

    /**
     * Handles click events for the views in this activity.
     *
     * More precisely, when the user enters a city name, it triggers a weather fetch. If the
     * user enters login, it navigates them into the register or login screen depending on
     * if they are a new user or a current one.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String input = userCityInput.getText().toString().trim();

        if (input.equalsIgnoreCase("login")) {
            Intent intent = new Intent(WeatherActivity.this, IntroActivity.class);
            startActivity(intent);
        }
        else if (!input.isEmpty()) {
            fetchWeather(input);
        }
        else {
            userCityInput.setError("Please enter a city name");
        }
    }

    /**
     * Sets the weather icon depending on given weather
     *
     * @param weather The current weather of the city
     */
    private void setWeatherIcon(String weather){
        ImageView weatherImage = findViewById(R.id.weatherIconMain);
        switch (weather.toLowerCase()) {
            case "clear":
                weatherImage.setImageResource(R.drawable.clear);
                break;
            case "clouds":
                weatherImage.setImageResource(R.drawable.cloudy);
                break;
            case "few clouds":
                weatherImage.setImageResource(R.drawable.fewclouds);
                break;
            case "drizzle":
            case "rain":
                weatherImage.setImageResource(R.drawable.rain);
                break;
            case "thunderstorm":
                weatherImage.setImageResource(R.drawable.thunderstorm);
                break;
            case "lightning":
                weatherImage.setImageResource(R.drawable.lightning);
                break;
            case "sunny":
                weatherImage.setImageResource(R.drawable.sunny);
                break;
            case "snow":
                weatherImage.setImageResource(R.drawable.snow);
                break;
            default:
                weatherImage.setImageResource(R.drawable.sunny);
                break;
        }
    }

    /**
     * Generates a formatted string that is displayed on the UI.
     *
     * @param city The city that user inputs
     * @param description A brief description of the weather
     * @param temp The temp of the city that user inputs in °C
     * @param feelsLike The current temp of the city in °C
     * @param tempMax The maximum temperature of the city in °C
     * @param tempMin The minimum temperature of the city in °C
     * @param humidity The current humidity of the the city
     * @param windSpeed The current wind speed of the the city
     * @param windDeg The current wind degree of the city
     * @return A complete weather summary string based on given parameters
     */
    public String generateWeatherString(String city, String description, double temp, double feelsLike, double tempMax, double tempMin, double humidity, double windSpeed, double windDeg){
        return "The current weather in " + city + " is " + description +
                " with a temperature of " + temp + "°C. It feels like " + feelsLike +
                "°C, with a high of " + tempMax + "°C and a low of " + tempMin +
                "°C. Humidity is at " + humidity + "% and wind is blowing at " +
                windSpeed + " m/s from " + windDeg + "°.";
    }

    private void fetchWeather(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&units=metric&appid=" + apiKey;

        Request req = new Request.Builder().url(url).build();


        client.newCall(req).enqueue(new Callback() {

            // If the call fails for whatever reason we set it as a Network Error.
            @Override
            public void onFailure(Call c, IOException e) {
                runOnUiThread(() -> temperatureText.setText("Network error"));
            }

            // If class goes through and server responds.
            // SuppressLint because we do not want warnings for different formatting issues.
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(Call c, Response r) throws IOException {
                String body = r.body().string();
                if (!r.isSuccessful()) {
                    runOnUiThread(() ->
                            temperatureText.setText("Temp Error: " + r.code()));
                    return;
                }
                try {
                    // This fetches the updated weather parameters from the JSON which is what the API stores.
                    JSONObject root   = new JSONObject(body);
                    double temp   = root.getJSONObject("main").getDouble("temp");
                    String city   = root.getString("name");
                    String weather = root.getJSONArray("weather").getJSONObject(0).getString("main");
                    double feelsLike = root.getJSONObject("main").getDouble("feels_like");
                    double minTemp = root.getJSONObject("main").getDouble("temp_min");
                    double maxTemp = root.getJSONObject("main").getDouble("temp_max");

                    double humidity = root.getJSONObject("main").getDouble("humidity");
                    double windSpeed = root.getJSONObject("wind").getDouble("speed");
                    double windDegree = root.getJSONObject("wind").getDouble("deg");

                    // Changing the text once all the information we get is stored.
                    runOnUiThread(() -> {
                        locationText.setText(city);
                        temperatureText.setText(String.format("%.1f °C", temp));
                        weatherStatText.setText(weather);
                        feelsLikeText.setText(String.format("Feels like %.1f °C", feelsLike));
                        minTempText.setText(String.format("Min: %.1f°C", minTemp));
                        maxTempText.setText(String.format("Max: %.1f°C", maxTemp));
                        weatherText.setText(generateWeatherString(city, weather, temp, feelsLike, maxTemp, minTemp, humidity, windSpeed, windDegree));
                        setWeatherIcon(weather);

                    });
                } catch (Exception e) {
                    runOnUiThread(() -> temperatureText.setText("Error"));
                }

            }
        });
    }
}
