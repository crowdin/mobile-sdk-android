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
//        val distributionHash = "e-1782b33219e56d471a283b2ozt"
//        val clientId = "XjNxVvoJh6XMf8NGnwuG"
//        val clientSecret = "Dw5TxCKvKQQRcPyAWEkTCZlxRGmcja6AFZNSld6U"

        // default
        val distributionHash = "27444fd9204fd35a4ade7e876j9"
        val clientId = "nyjTPt1mCx6PLmgCUWvZ"
        val clientSecret = "OumG8dVQudHsimUYVniLvacNtVAcp1yOIeXjVwJp"

        Crowdin.init(
            applicationContext,
            CrowdinConfig.Builder()
                .withDistributionHash(distributionHash)          // required
                .withNetworkType(NetworkType.ALL)                // optional
                .withRealTimeUpdates()                           // optional
                .withScreenshotEnabled()                         // optional
                .withSourceLanguage("en")          // optional
                .withUpdateInterval(fifteenMinutes)              // optional
                // enterprise
//                .withAuthConfig(AuthConfig(clientId, clientSecret, "serhiy"))
                // default
                .withAuthConfig(AuthConfig(clientId, clientSecret))
                .build()
        )

        // Using system buttons to take screenshot automatically will upload them to crowdin.
        Crowdin.registerScreenShotContentObserver(this)
    }
}
