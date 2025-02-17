override fun onCreate() {
    super.onCreate()
        Crowdin.init(applicationContext,
            CrowdinConfig.Builder()
                .withDistributionHash(your_distribution_hash)
                .withOrganizationName(organization_name)      // required for Crowdin Enterprise
                .withNetworkType(network_type)                // optional
                .withUpdateInterval(interval_in_seconds)      // optional
                .build(),
            loadingStateListener = object : LoadingStateListener {  // optional
                override fun onDataChanged() {
                    // Handle data changes
                }

                override fun onFailure(throwable: Throwable) {
                    // Handle failures
                }
            })
}