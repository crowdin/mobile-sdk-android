import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig

Crowdin.init(
    application,
    CrowdinConfig.Builder()
        .withDistributionHash("your_distribution_hash")
        .withRealTimeUpdates()
        .withRealTimeComposeEnabled(true) // Enable Real-time Compose support
        .build()
)
