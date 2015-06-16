package com.westpac.yaniv.myweather;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Yaniv on 16/06/2015.
 */
public class MainActivityTestCase extends ActivityUnitTestCase<MainActivity> {

    TextView mErrorTextView = null;
    Button mCheckWeatherButton = null;
    MainActivity mMainActivity;

    public MainActivityTestCase() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        // Starts the MainActivity of the target application
        startActivity(new Intent(getInstrumentation().getTargetContext(), MainActivity.class), null, null);

        // Getting a reference to the MainActivity of the target application
        mMainActivity = (MainActivity)getActivity();
        mErrorTextView = (TextView) mMainActivity.findViewById(R.id.error_msg);
        mCheckWeatherButton = (Button) mMainActivity.findViewById(R.id.button_check_weather);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testErrorMsgShown() {
        assertEquals(mErrorTextView.getVisibility(), View.VISIBLE);
    }

    public void testCheckWeatherButtonDisabled() {
        assertFalse(mCheckWeatherButton.isEnabled());
    }

}
