package com.crowdin.platform.example

import android.app.Application
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.data.remote.NetworkType

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val fifteenMinutes: Long = 60 * 15
        Crowdin.init(applicationContext,
                CrowdinConfig.Builder()
                        .withDistributionKey("d32682e5a6a5f53a950d934e2eee861e")
                        .withFilePaths("strings.xml",
                                "arrays.xml",
                                "plurals.xml")
                        .withNetworkType(NetworkType.WIFI)
                        .withRealTimeUpdates(true, "en")
                        .withUpdateInterval(fifteenMinutes)
                        .build())
    }
}
