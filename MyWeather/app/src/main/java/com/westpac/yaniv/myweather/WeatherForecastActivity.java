package com.westpac.yaniv.myweather;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class WeatherForecastActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        Intent intent = getIntent();

        ForecastData forecast = (ForecastData) intent.getSerializableExtra(MainActivity.EXTRA_FORECAST);

        TextView forecastTextView = (TextView) findViewById(R.id.forecast_result);

        TextView forecastSummaryTextView = (TextView) findViewById(R.id.forecast_result_summary);

        forecastTextView.setText(forecast.getIcon());
        forecastSummaryTextView.setText(forecast.getDailySummary());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_forcast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
