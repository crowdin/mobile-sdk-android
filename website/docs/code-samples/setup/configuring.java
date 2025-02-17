@Override
protected void onCreate() {
    super.onCreate();

    Crowdin.init(this,
        new CrowdinConfig.Builder()
            .withDistributionHash(your_distribution_hash)
            .withOrganizationName(organization_name)      // required for Crowdin Enterprise
            .withNetworkType(network_type)                // optional
            .withUpdateInterval(interval_in_seconds)      // optional
            .build(),
        new LoadingStateListener() {                      // optional
            @Override
            public void onDataChanged() {
                // Handle data changes
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Handle failures
            }
        });
}