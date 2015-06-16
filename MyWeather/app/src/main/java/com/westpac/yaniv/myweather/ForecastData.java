package com.westpac.yaniv.myweather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Yaniv on 14/06/2015.
 */
public class ForecastData implements Serializable {
    public static final String SUMMARY = "summary";
    public static final String ICON = "icon";

    public static final String DAILY = "daily";

    private String dailySummary;
    private String icon;

    public String getDailySummary() {
        return dailySummary;
    }

    public String getIcon() {
        return icon;
    }


    public void init(JSONObject json) {
        if (json == null) return;
        try {

            JSONObject daily = json.getJSONObject(DAILY);
            dailySummary = daily.getString(SUMMARY);
            icon = daily.getString(ICON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
