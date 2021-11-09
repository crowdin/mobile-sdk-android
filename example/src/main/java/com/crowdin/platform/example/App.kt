package com.crowdin.platform.example

import android.app.Application
import android.content.res.Configuration
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.remote.NetworkType
import com.crowdin.platform.example.utils.updateLocale

class App : Application() {

    lateinit var languagePreferences: LanguagePreferences

    override fun onCreate() {
        super.onCreate()
        languagePreferences = LanguagePreferences(this)

        // Crowdin sdk initialization
        val distributionHash = "your_distribution_hash"     // "7a0c1...7uo3b"
        val networkType = NetworkType.WIFI                  // ALL, CELLULAR, WIFI
        val sourceLanguage = "source_language"              // Source language code in your Crowdin project - e.g. 'en'
        val intervalInSeconds: Long = 18 * 60               // 18 minutes, min 15 min
        val clientId = "your_client_id"                     // "gpY2yC...cx3TYB"
        val clientSecret = "your_client_secret"             // "Xz95tfedd0A...TabEDx9T"
        val organizationName = "your_organization_name"     // for Crowdin Enterprise users only

        // Set custom locale before SDK initialization.
        this.updateLocale(languagePreferences.getLanguageCode())
        Crowdin.init(
            applicationContext,
            CrowdinConfig.Builder()
                .withDistributionHash(distributionHash)                                 // required
                .withNetworkType(networkType)                                           // optional
                .withRealTimeUpdates()                                                  // optional
                .withScreenshotEnabled()                                                // optional
                .withSourceLanguage(sourceLanguage)                                     // required if screenshot or realtime update are enabled, otherwise optional
                .withUpdateInterval(intervalInSeconds)                                  // optional
                .withAuthConfig(AuthConfig(clientId, clientSecret, organizationName))   // optional
                .skipRequestAuthDialog()                                                // optional
                .build()
        )

        // Using system buttons to take screenshot automatically will upload them to crowdin.
        Crowdin.registerScreenShotContentObserver(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reload translations on configuration change when real-time preview ON.
        Crowdin.onConfigurationChanged()
    }
}
