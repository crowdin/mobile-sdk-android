# Synchronous Mode

By default, Crowdin SDK works in asynchronous mode. It means that the SDK will download translations in the background and apply them when they are ready on the next app launch.

However, you can switch to synchronous mode if you need to wait for translations to be downloaded at a specific point in your app, such as during a custom splash screen or when navigating to a specific section. In synchronous mode, you can control when translations are downloaded and applied.

Translations downloaded in synchronous mode will only be applied when the activity is recreated or on the next app launch.

## Switching to Synchronous Mode

To enable synchronous mode, you need to disable the initial data synchronization and manually force an update when required. Here’s how you can set it up:

## Using Synchronous Mode on Splash Screen or Specific Sections

For example, you can wait for translations to be downloaded while launching the Splash Activity and then open the Main Activity. Similarly, you can defer translations loading at any other point in your app, such as when the user navigates to a specific section.


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
