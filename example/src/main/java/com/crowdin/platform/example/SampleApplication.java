package com.crowdin.platform.example;

import android.app.Application;

import com.crowdin.platform.Crowdin;
import com.crowdin.platform.CrowdinConfig;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Crowdin.init(this,
                new CrowdinConfig.Builder()
                        .persist(true)
                        .build()
        );
    }
}
