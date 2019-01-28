package com.crowdin.platform.example;

import android.app.Application;

import com.crowdin.platform.Restring;
import com.crowdin.platform.RestringConfig;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Restring.init(this,
                new RestringConfig.Builder()
                        .persist(true)
                        .build()
        );
    }
}
