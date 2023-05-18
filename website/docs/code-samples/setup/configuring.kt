override fun onCreate() {
    super.onCreate()
        Crowdin.init(applicationContext,
            CrowdinConfig.Builder()
                .withDistributionHash(your_distribution_hash)
                .withNetworkType(network_type)                // optional
                .withUpdateInterval(interval_in_seconds)      // optional
                .build())
}