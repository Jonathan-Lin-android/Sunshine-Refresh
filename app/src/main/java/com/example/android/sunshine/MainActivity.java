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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import java.io.IOException;
import java.net.URL;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherDataTextView;

    private TextView mErrorMessageTextView;

    private ProgressBar mLoadingIndicatorProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forecast);

        mWeatherDataTextView = (TextView) findViewById(R.id.tv_weather_data);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_messsage);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        loadWeatherData();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int refresh = R.id.action_refresh;
        switch (item.getItemId()) {
            case refresh: {
                loadWeatherData();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
            Fetch data from the weather server
             */
    class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
            showWeatherData();
        }

        @Override
        protected String[] doInBackground(final String... strSearch) {
            if (strSearch.length == 0) {
                showErrorMessage("Search field is empty");
                return null;
            }
            //bilding URL
            URL builtURL = NetworkUtils.buildUrl(strSearch[0]);

            //query
            String[] parsedJSONResponse = null;
            try {
                String jsonStrResponse = NetworkUtils.getResponseFromHttpUrl(builtURL);
                parsedJSONResponse = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonStrResponse);
            } catch (IOException | JSONException e) {
                showErrorMessage();
                e.printStackTrace();
            }
            return parsedJSONResponse;
        }

        @Override
        protected void onPostExecute(final String[] jsonStrResult) {
            mLoadingIndicatorProgressBar.setVisibility(View.INVISIBLE);

            if (jsonStrResult == null || jsonStrResult.length == 0) {
                return;
            }
            //display JSON result / parsed josn result
            for (String s : jsonStrResult) {
                mWeatherDataTextView.append(s + "\n\n\n");
            }
        }
    }

    private void loadWeatherData() {
        mWeatherDataTextView.setText("");
        //"94043,USA";
        new FetchWeatherTask().execute(SunshinePreferences.getPreferredWeatherLocation(this));
    }

    private void showErrorMessage()
    {
        mErrorMessageTextView.setText(getString(R.string.error_message));
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mWeatherDataTextView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(String message) {
        mErrorMessageTextView.setText(message);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mWeatherDataTextView.setVisibility(View.INVISIBLE);
    }

    private void showWeatherData() {
        mWeatherDataTextView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }
}