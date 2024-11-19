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
                request_auth_dialog
            ))
            .withApiAuthConfig(ApiAuthConfig(api_token))
            .withOrganizationName(organization_name)   // required for Crowdin Enterprise
            .withNetworkType(network_type)             // optional
            .withUpdateInterval(interval_in_seconds)   // optional
            .build())
}

// Using system buttons to take screenshots and automatically upload them to Crowdin.
Crowdin.registerScreenShotContentObserver(this)