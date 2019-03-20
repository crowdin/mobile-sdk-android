package com.crowdin.platform.example

import android.app.Application
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Crowdin.init(applicationContext,
                CrowdinConfig.Builder()
                        .persist(true)
                        .withDistributionKey("d32682e5a6a5f53a950d934e2eee861e")
                        .withFilePaths(
                                "strings.xml")
//                                "strings_plus_array.xml")
                        .build()
        )
    }
}
