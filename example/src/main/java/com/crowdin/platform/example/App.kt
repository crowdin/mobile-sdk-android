package com.crowdin.platform.example

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.util.Log
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.data.model.ApiAuthConfig
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.remote.NetworkType
import com.crowdin.platform.example.utils.updateLocale

class App : Application() {

    lateinit var languagePreferences: LanguagePreferences

    /**
     * Should be overridden in case you want to change locale programmatically.
     * For custom language set your application locale taking into account constraints for language and country/region
     * This should match with `Locale code:` for your custom language on Crowdin platform.
     *
     * language - [a-zA-Z]{2,8}
     * country/region - [a-zA-Z]{2} | [0-9]{3}
     *
     * Example: "aa-BB"
     * */
    override fun attachBaseContext(newBase: Context) {
        languagePreferences = LanguagePreferences(newBase)
        super.attachBaseContext(ContextWrapper(newBase.updateLocale(languagePreferences.getLanguageCode())))
    }

    override fun onCreate() {
        super.onCreate()

        // Crowdin sdk initialization
        val distributionHash = "your_distribution_hash"     // "7a0c1...7uo3b"
        val networkType = NetworkType.WIFI                  // ALL, CELLULAR, WIFI
        val sourceLanguage =
            "source_language"                               // Source language code in your Crowdin project - e.g. 'en'
        val intervalInSeconds: Long = 18 * 60               // 18 minutes, min 15 min
        val clientId = "your_client_id"                     // "gpY2yC...cx3TYB"
        val clientSecret = "your_client_secret"             // "Xz95tfedd0A...TabEDx9T"
        val organizationName = "your_organization_name"     // for Crowdin Enterprise users only
        val requestAuthDialog = true                        // Request authorization dialog `true` by default or `false`
        val apiToken = "your_api_token"

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
                .withAuthConfig(
                    AuthConfig(
                        clientId = clientId,
                        clientSecret = clientSecret,
                        requestAuthDialog = requestAuthDialog
                    )
                )
                .withOrganizationName(organizationName)                                 // required for Crowdin Enterprise
                .withInitSyncDisabled()                                                 // optional
                .withApiAuthConfig(ApiAuthConfig(apiToken))
                .build(),
            loadingStateListener = object : LoadingStateListener {
                override fun onDataChanged() {
                    Log.d("TAG", "Crowdin: onDataChanged")
                }

                override fun onFailure(throwable: Throwable) {
                    Log.d("TAG", "Crowdin: onFailure")
                }
            }
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
