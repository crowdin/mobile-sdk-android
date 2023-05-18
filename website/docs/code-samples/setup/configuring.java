@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Crowdin.init(this,
        new CrowdinConfig.Builder()
            .withDistributionHash(your_distribution_hash)
            .withNetworkType(network_type)                // optional
            .withUpdateInterval(interval_in_seconds)      // optional
            .build());
}