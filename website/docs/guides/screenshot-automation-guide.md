
# Screenshot Automation Testing

This guide explains how to automate screenshot generation and upload them to Crowdin for localization purposes. It covers the required setup, usage of the Crowdin SDK, and an example automation test.

## Prerequisites

Before you start, ensure the following:

1. **Crowdin SDK Integration:** The Crowdin SDK should be integrated into your project. (See [Setting Up the Crowdin SDK](#setting-up-the-crowdin-sdk)).
2. **Android Testing Dependencies:** Add the following dependencies to your `build.gradle` file:

   ```groovy
   dependencies {
       androidTestImplementation "androidx.test.ext:junit:1.2.1"
       androidTestImplementation "androidx.test.espresso:espresso-core:3.6.1"
       androidTestImplementation "androidx.tracing:tracing:1.2.0"
       androidTestImplementation "androidx.test.espresso:espresso-contrib:3.6.1"
   }
   ```

## Setting Up the Crowdin SDK

To use the Crowdin SDK for automation testing, configure it as follows:

1. **Add the Crowdin SDK to Your Dependencies:**
   Add the necessary dependency to your `build.gradle` file:

   ```groovy
   implementation 'com.github.crowdin.mobile-sdk-android:sdk:1.10.1'
   ```

2. **Initialize the SDK in Your Application Class:**
   Use the following code to initialize the Crowdin SDK, ensuring you include the `withApiAuthConfig` configuration for automation.

   ```kotlin
   class MyApplication : Application() {
       override fun onCreate() {
           super.onCreate()

           Crowdin.init(
               this,
               CrowdinConfig.Builder()
                   .withDistributionHash("your_distribution_hash_here")
                   .withScreenshotEnabled()
                   .withSourceLanguage("source_language_code")
                   .withApiAuthConfig(ApiAuthConfig("your_api_token_here"))
                   .build()
           )
       }
   }
   ```

### Key Configuration Options
- **`withApiAuthConfig(ApiAuthConfig)`:** Configures API token-based authentication, ideal for automation workflows.
- **`withDistributionHash(String)`:** Specifies the Crowdin distribution hash.
- **`withScreenshotEnabled()`:** Enables the Screenshots feature for capturing and uploading screenshots.

## Capturing Screenshots with Crowdin SDK

Screenshots can be automatically uploaded to Crowdin by invoking the `Crowdin.sendScreenshot()` method.

## Example Automation Test

The following test navigates through app screens, captures screenshots, and uploads them to Crowdin:

```kotlin
@Test
fun screenshotCapture() {
    val splashScenario = ActivityScenario.launch(SplashScreenActivity::class.java)

    waitForActivity<MainActivity>()?.let { activity ->
        captureAndSendScreenshot(activity, "dashboard")

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withId(R.id.nav_add_task)).perform(click())
        captureAndSendScreenshot(activity, "add_task")

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withId(R.id.nav_category)).perform(click())
        captureAndSendScreenshot(activity, "category")
    } ?: throw IllegalStateException("MainActivity not found!")

    splashScenario.close()
}

private fun captureAndSendScreenshot(activity: Activity, name: String) {
    val latch = CountDownLatch(1)
    val rootView = activity.window.decorView.rootView
    val bitmap = captureBitmap(rootView)

    Crowdin.sendScreenshot(
        bitmap = bitmap,
        screenshotName = name,
        screenshotCallback = object : ScreenshotCallback {
            override fun onSuccess() {
                latch.countDown()
            }
            override fun onFailure(throwable: Throwable) {
                throw AssertionError("Screenshot upload failed: ${throwable.message}")
            }
        }
    )

    latch.await(30, TimeUnit.SECONDS)
}
```

### Key Points
1. **Bitmap Generation:** Screenshots are captured by drawing the root view onto a Canvas.
2. **Crowdin SDK Integration:** Use `Crowdin.sendScreenshot()` to upload screenshots with metadata.
3. **Custom View Actions:** Handle RecyclerView actions like editing or deleting items using custom `ViewAction`.

## Conclusion

This guide demonstrates how to create automation tests that capture and upload screenshots using Crowdin SDK. You can check more details in the **ScreenshotAutomationTest.kt** file.
