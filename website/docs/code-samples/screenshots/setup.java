@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Crowdin.init(this,
        new CrowdinConfig.Builder()
            .withDistributionHash(your_distribution_hash)
            .withScreenshotEnabled()
            .withSourceLanguage(source_language)
            .withAuthConfig(new AuthConfig(
                client_id,
                client_secret,
                request_auth_dialog
            ))
            .withOrganizationName(organization_name)   // optional
            .withNetworkType(network_type)             // optional
            .withUpdateInterval(interval_in_seconds)   // optional
            .build());
}

// Using system buttons to take screenshots and automatically upload them to Crowdin.
Crowdin.registerScreenShotContentObserver(this);
