# Synchronous Mode

By default, Crowdin SDK works in asynchronous mode. It means that the SDK will download translations in the background and apply them when they are ready on the next app launch. 

However, you can switch to synchronous mode if you need to wait for translations to be downloaded before the app starts. You can wait for translations to be downloaded while launching Splash Activity and then open Main Activity:

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

:::info
The `forceUpdate` method will either download translations if they are not in the cache or if the cache has expired. If translations are already in the cache, the method will not download them again.
:::
