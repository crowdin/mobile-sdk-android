# Synchronous Mode

You can trigger force upload from Crowdin while launching Splash Activity and then open Main Activity:

```kotlin title="App.kt"
override fun onCreate() {
   super.onCreate()

   Crowdin.init(applicationContext,
       CrowdinConfig.Builder()
           .withDistributionHash(your_distribution_hash)
           .withInitSyncDisabled()  // Initial data synchronization should be disabled in this case.
           .build())
}
```

```kotlin title="YourActivity.kt"
override fun onCreate(savedInstanceState: Bundle?) {
    Crowdin.forceUpdate(this) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
```
