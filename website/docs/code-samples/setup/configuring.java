@Override
protected void onCreate() {
    super.onCreate();

    Crowdin.init(this,
        new CrowdinConfig.Builder()
            .withDistributionHash(your_distribution_hash)
            .withOrganizationName(organization_name)      // required for Crowdin Enterprise
            .withNetworkType(network_type)                // optional
            .withUpdateInterval(interval_in_seconds)      // optional
            .build());
}