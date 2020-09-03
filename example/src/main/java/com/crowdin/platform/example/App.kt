package com.crowdin.platform.example

import android.app.Application
import android.content.res.Configuration
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.remote.NetworkType

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Crowdin sdk initialization
        val distributionHash = "your_distribution_hash"
        val networkType = NetworkType.WIFI                  //  ALL, CELLULAR, WIFI
        val sourceLanguage = "source_language"
        val intervalInSeconds: Long = 18 * 60               // 18 minutes
        val clientId = "your_client_id"
        val clientSecret = "your_client_secret"
        val organizationName = "your_organization_name"     // for Crowdin Enterprise users only

        Crowdin.init(
            applicationContext,
            CrowdinConfig.Builder()
                .withDistributionHash(distributionHash)                                 // required
                .withNetworkType(networkType)                                           // optional
                .withRealTimeUpdates()                                                  // optional
                .withScreenshotEnabled()                                                // optional
                .withSourceLanguage(sourceLanguage)                                     // optional
                .withUpdateInterval(intervalInSeconds)                                  // optional
                .withAuthConfig(AuthConfig(clientId, clientSecret, organizationName))   // optional
                .build()
        )

        // Using system buttons to take screenshot automatically will upload them to crowdin.
        Crowdin.registerScreenShotContentObserver(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Crowdin.onConfigurationChanged()
    }
}
