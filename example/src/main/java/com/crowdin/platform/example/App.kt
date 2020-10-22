package com.crowdin.platform.example

import android.app.Application
import android.content.res.Configuration
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Crowdin sdk initialization
//        val distributionHash = "your_distribution_hash"
//        val networkType = NetworkType.WIFI                  //  ALL, CELLULAR, WIFI
//        val sourceLanguage = "source_language"
//        val intervalInSeconds: Long = 18 * 60 * 1000    // 18 minutes
//        val clientId = "your_client_id"
//        val clientSecret = "your_client_secret"
//        val organizationName = "your_organization_name"     // for Crowdin Enterprise users only

        val distributionHash = "01b75f4d892f38ab5c2025876j9"
        val clientId = "nyjTPt1mCx6PLmgCUWvZ"
        val clientSecret = "OumG8dVQudHsimUYVniLvacNtVAcp1yOIeXjVwJp"
        val sourceLanguage = "en"
//        val organizationName = "test000"     // for Crowdin Enterprise users only

        Crowdin.init(
            applicationContext,
            CrowdinConfig.Builder()
                .withDistributionHash(distributionHash)
//                .withScreenshotEnabled()
//                .withRealTimeUpdates()
//                .withSourceLanguage(sourceLanguage)
//                .withAuthConfig(AuthConfig(clientId, clientSecret))
                .build())

        // Using system buttons to take screenshot automatically will upload them to crowdin.
//        Crowdin.registerScreenShotContentObserver(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Crowdin.onConfigurationChanged()
    }
}
