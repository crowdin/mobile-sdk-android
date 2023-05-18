override fun onCreate() {
    super.onCreate()

    Crowdin.init(applicationContext,
        CrowdinConfig.Builder()
            .withDistributionHash(your_distribution_hash)
            .withScreenshotEnabled()
            .withSourceLanguage(source_language)
            .withAuthConfig(AuthConfig(
                client_id,
                client_secret,
                organization_name,
                request_auth_dialog
            ))
            .withNetworkType(network_type)             // optional
            .withUpdateInterval(interval_in_seconds)   // optional
            .build())
}

// Using system buttons to take screenshots and automatically upload them to Crowdin.
Crowdin.registerScreenShotContentObserver(this)