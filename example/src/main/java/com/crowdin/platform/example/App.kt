package com.crowdin.platform.example

import android.app.Application
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.remote.NetworkType

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Crowdin sdk initialization
        val fifteenMinutes: Long = 60 * 15
        // enterprise
//        val distributionHash = "693743ebc92464492aa26e3b2ozt"
//        val clientId = "XjNxVvoJh6XMf8NGnwuG"
//        val clientSecret = "Dw5TxCKvKQQRcPyAWEkTCZlxRGmcja6AFZNSld6U"

        // default
        val distributionHash = "d5d8249cef350c21219d048106i"
        val clientId = "test-sdk"
        val clientSecret = "79MG6E8DZfEeomalfnoKx7dA0CVuwtPC3jQTB3ts"

        Crowdin.init(applicationContext,
                CrowdinConfig.Builder()
                        .withDistributionHash(distributionHash)          // required
                        .withFilePaths(                        // required
                                "strings.xml",
                                "arrays.xml",
                                "plurals.xml")
                        .withNetworkType(NetworkType.ALL)                // optional
                        .withRealTimeUpdates()                           // optional
                        .withScreenshotEnabled()                         // optional
                        .withSourceLanguage("en")          // optional
                        .withUpdateInterval(fifteenMinutes)              // optional
//                        .withAuthConfig(AuthConfig(clientIdEnterprise, clientSecretEnterprise, "serhiy"))
                        .withAuthConfig(AuthConfig(clientId, clientSecret))
                        .build())

        // Using system buttons to take screenshot automatically will upload them to crowdin.
        Crowdin.registerScreenShotContentObserver(this)
    }
}
