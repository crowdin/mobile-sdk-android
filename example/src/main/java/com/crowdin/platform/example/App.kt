package com.crowdin.platform.example

import android.app.Application
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.data.remote.NetworkType

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Crowdin sdk initialization
        val fifteenMinutes: Long = 60 * 15
        val distributionHash = "d5d8249cef350c21219d048106i"
        Crowdin.init(applicationContext,
                CrowdinConfig.Builder()
                        .withDistributionHash(distributionHash)          // required
                        .withFilePaths(                        // required
                                "strings.xml",
                                "arrays.xml",
                                "plurals.xml")
                        .withNetworkType(NetworkType.WIFI)               // optional
                        .withRealTimeUpdates(true) // optional
                        .withScreenshotEnabled(true)   // optional
                        .withSourceLanguage("en")          // optional
                        .withUpdateInterval(fifteenMinutes)              // optional
                        .build())

        // Using system buttons to take screenshot automatically will upload them to crowdin.
        Crowdin.registerScreenShotContentObserver(this)
    }
}
