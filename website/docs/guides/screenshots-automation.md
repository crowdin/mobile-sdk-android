# Screenshots Automation

This guide shows how to automate the process of taking screenshots and uploading them to Crowdin to provide context for translators or AI. It covers the necessary setup, how to use the Crowdin SDK, and a sample automation test.

## Prerequisites

Before you start, ensure the following:

1. **Crowdin SDK Integration:** The Crowdin SDK should be integrated into your project (See [Setting Up the Crowdin SDK](#setting-up-the-crowdin-sdk)).
2. **Android Testing Dependencies:** Add the following dependencies to your `build.gradle` file:

   ```groovy title="build.gradle"
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

   ```groovy title="build.gradle"
   implementation 'com.github.crowdin.mobile-sdk-android:sdk:1.11.1'
   ```

2. **Initialize the SDK:**

   Use the following code to initialize the Crowdin SDK, ensuring you include the `withApiAuthConfig` configuration for automation:

   ```kotlin title="Application.kt"
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

- `withApiAuthConfig(ApiAuthConfig)` - Sets up API token-based authentication for automated workflows. This method is essential for CI/CD pipelines and scenarios where user interaction isn't required.
- `withDistributionHash(String)` - Defines the unique distribution hash from your Crowdin project.
- `withScreenshotEnabled()` - Activates the Screenshots feature in your application, enabling automatic capture and upload of tagged screenshots.

:::tip
See the [Screenshots](/advanced-features/screenshots) guide for more information on setting up the Crowdin SDK for screenshots.
:::

## Capturing Screenshots with Crowdin SDK

Screenshots can be automatically uploaded to Crowdin by calling the `Crowdin.sendScreenshot()` method. This method requires a bitmap image, a screenshot name, and a `ScreenshotCallback` (optional) to handle success or failure.

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

This guide demonstrates how to create automation tests that capture and upload screenshots using the Crowdin SDK. More details can be found in the [`ScreenshotAutomationTest.kt`](https://github.com/crowdin/mobile-sdk-android/blob/master/example/src/androidTest/java/com/crowdin/platform/example/ScreenshotAutomationTest.kt) file of the Crowdin SDK Example project.

As a result, you can provide translators with visual context, ensuring more accurate and contextually appropriate translations.
