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
                        .withDistributionKey("d5d8249cef350c21219d048106i")
                        .withFilePaths("strings.xml",
                                "arrays.xml",
                                "plurals.xml")
                        .withNetworkType(NetworkType.WIFI)
                        .withRealTimeUpdates(true, "en")
                        .withUpdateInterval(fifteenMinutes)
                        .build())

        Crowdin.registerScreenShotContentObserver(this)
    }
}
