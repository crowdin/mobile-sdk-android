package com.crowdin.platform.example

import android.app.Application
import android.content.res.Configuration
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.data.model.AuthConfig

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

//        val distributionHash = "4b708e5c9d0672310208c7876j9"
//        val clientId = "nyjTPt1mCx6PLmgCUWvZ"
//        val clientSecret = "OumG8dVQudHsimUYVniLvacNtVAcp1yOIeXjVwJp"
//        val sourceLanguage = "en"
//        val organizationName = "test000"     // for Crowdin Enterprise users only
        val distributionHash = "0eb2a11403886fdc331062876j9"
        val clientId = "nyjTPt1mCx6PLmgCUWvZ"
        val clientSecret = "OumG8dVQudHsimUYVniLvacNtVAcp1yOIeXjVwJp"
        val sourceLanguage = "en"

        Crowdin.init(
            applicationContext,
            CrowdinConfig.Builder()
                .withDistributionHash(distributionHash)                                 // required
                .withScreenshotEnabled()                                              // optional
                .withRealTimeUpdates()                                                // optional
                .withSourceLanguage(sourceLanguage)                                   // optional
                .withAuthConfig(AuthConfig(clientId, clientSecret))   // optional
                .build()
        )

        // Using system buttons to take screenshot automatically will upload them to crowdin.
//        Crowdin.registerScreenShotContentObserver(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Crowdin.onConfigurationChanged()
    }
}
