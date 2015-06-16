package com.westpac.yaniv.myweather;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    public static final String EXTRA_FORECAST = "com.westpac.yaniv.myweather.FORECAST";
    public static final int DELAY = 5000;
    public static final int PERIOD = 30000;

    private final String FOLDER_SEPARATOR = "/";
    private final String PARAM_SEPARATOR = ",";
    private final long MIN_TIME = 1;
    private final float MIN_DISTANCE = 500;
    private final Handler mHandler = new Handler();

    private Timer mTimer = null;
    private TimerTask mNotFoundTimerTask = null;
    private Location mCurrentLocation = null;
    private LocationManager mLocationManager;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            if (location != null) {
                updateCheckWeatherDisplay();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

        Button checkWeatherButton = (Button) findViewById(R.id.button_check_weather);
        checkWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weatherUrl = getString(R.string.weather_url_prefix) +
                        getString(R.string.weather_url_apikey) +
                        FOLDER_SEPARATOR +
                        mCurrentLocation.getLatitude() +
                        PARAM_SEPARATOR +
                        mCurrentLocation.getLongitude();
                new GetWeather().execute(weatherUrl);
            }
        });

        //updateCheckWeatherDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentLocation == null)
            startNotFoundTimer();
    }

    @Override
    public void onPause() {
        if (mCurrentLocation == null)
            stopNotFoundTimer();
        super.onPause();
    }

    private void updateCheckWeatherDisplay() {
        Button checkWeatherButton = (Button) findViewById(R.id.button_check_weather);
        TextView errorTextView = (TextView) findViewById(R.id.error_msg);
        checkWeatherButton.setEnabled(mCurrentLocation != null);
        errorTextView.setVisibility(mCurrentLocation == null? View.VISIBLE : View.INVISIBLE);

        if (errorTextView.getVisibility() == View.VISIBLE)
        {
            startNotFoundTimer();
        }
        else {
            stopNotFoundTimer();
        }
    }

    private void startNotFoundTimer() {
        if (mTimer != null) return;
        mTimer = new Timer();
        mNotFoundTimerTask = new TimerTask() {

            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.location_not_found_msg), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        mTimer.schedule(mNotFoundTimerTask, DELAY, PERIOD);
    }

    private void stopNotFoundTimer() {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
    }

    private class GetWeather extends AsyncTask<String,String,String>{

        public static final int STATUS_CODE_OK = 200;

        @Override
        protected String doInBackground(String... parameters) {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(parameters[0]);
            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == STATUS_CODE_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    Log.e(GetWeather.class.toString(), "Error in http response");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();

        }

        protected void onPostExecute(String result) {
            Intent intent = new Intent(getApplicationContext(), WeatherForecastActivity.class);
            ForecastData forecastData = getForecastData(result);
            intent.putExtra(EXTRA_FORECAST, forecastData);
            startActivity(intent);
        }
    }

    private ForecastData getForecastData(String result) {
        ForecastData res = new ForecastData();
        try {
            JSONObject json = new JSONObject(result);
            res.init(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;

    }
}
