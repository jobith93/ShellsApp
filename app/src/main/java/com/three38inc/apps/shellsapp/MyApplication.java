package com.three38inc.apps.shellsapp;

/**
 * Created by jobith on 24/01/17.
 */

import android.app.Application;
import com.pushbots.push.Pushbots;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Pushbots Library
        Pushbots.sharedInstance().init(this);
        Pushbots.sharedInstance().setCustomHandler(customHandler.class);
    }
}