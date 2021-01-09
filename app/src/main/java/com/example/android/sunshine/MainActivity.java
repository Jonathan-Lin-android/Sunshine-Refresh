/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import java.io.IOException;
import java.net.URL;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private TextView weatherDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        // dummy weather data
        String[] rgDummyWeatherData = {
                "Today, May 17 - Clear - 17°C / 15°C",
                "Tomorrow - Cloudy - 19°C / 15°C",
                "Thursday - Rainy- 30°C / 11°C",
                "Friday - Thunderstorms - 21°C / 9°C",
                "Saturday - Thunderstorms - 16°C / 7°C",
                "Sunday - Rainy - 16°C / 8°C",
                "Monday - Partly Cloudy - 15°C / 10°C",
                "Tue, May 24 - Meatballs - 16°C / 18°C",
                "Wed, May 25 - Cloudy - 19°C / 15°C",
                "Thu, May 26 - Stormy - 30°C / 11°C",
                "Fri, May 27 - Hurricane - 21°C / 9°C",
                "Sat, May 28 - Meteors - 16°C / 7°C",
                "Sun, May 29 - Apocalypse - 16°C / 8°C",
                "Mon, May 30 - Post Apocalypse - 15°C / 10°C",
        };
*/
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forecast);

        weatherDataTextView = (TextView) findViewById(R.id.tv_weather_data);
//"94043,USA";
        new FetchWeatherTask().execute(SunshinePreferences.getPreferredWeatherLocation(this));

    }


    /*
    search based on location. (user entered text) or dummy location. default preferred location
    build a url based on query
    query the database and return response (JSONString)
    display the string.
     */
    class FetchWeatherTask extends AsyncTask<String, Void, String []>
    {
        @Override
        protected String [] doInBackground(final String... strSearch) {
            //bilding URL
            URL builtURL = NetworkUtils.buildUrl(strSearch[0]);

            //query
            String[] parsedJSONResponse = null;
            try {
                String jsonStrResponse = NetworkUtils.getResponseFromHttpUrl(builtURL);
                parsedJSONResponse= OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, jsonStrResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return parsedJSONResponse;
        }

        @Override
        protected void onPostExecute(final String [] jsonStrResult) {
            //display JSON result / parsed josn result
            for (String s : jsonStrResult)
                weatherDataTextView.append(s + "\n\n\n");

        }
    }

}