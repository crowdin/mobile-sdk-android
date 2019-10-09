[<p align="center"><img src="https://support.crowdin.com/assets/logos/crowdin-dark-symbol.png" data-canonical-src="https://support.crowdin.com/assets/logos/crowdin-dark-symbol.png" width="200" height="200" align="center"/></p>](https://crowdin.com)

# Crowdin Android SDK

Crowdin Android SDK delivers all new translations from Crowdin project to the application immediately. So there is no need to update this application via Google Play Store to get the new version with the localization.

The SDK provides:

* Over-The-Air Content Delivery – the localized files can be sent to the application from the project whenever needed
* Real-time Preview – all the translations that are done via Editor can be shown in the application in real-time
* Screenshots – all screenshots made in the application may be automatically sent to your Crowdin project with tagged source strings


[![GitHub issues](https://img.shields.io/github/issues/crowdin/mobile-sdk-android)](https://github.com/crowdin/mobile-sdk-android/issues)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/m/crowdin/mobile-sdk-android)](https://github.com/crowdin/mobile-sdk-android/graphs/commit-activity)
[![GitHub last commit](https://img.shields.io/github/last-commit/crowdin/mobile-sdk-android)](https://github.com/crowdin/mobile-sdk-android/commits/master)
[![GitHub contributors](https://img.shields.io/github/contributors/crowdin/mobile-sdk-android)](https://github.com/crowdin/mobile-sdk-android/graphs/contributors)
[![GitHub](https://img.shields.io/github/license/crowdin/mobile-sdk-android)](https://github.com/crowdin/mobile-sdk-android/blob/master/LICENSE)

## Table of Contents
* [Requirements](#requirements)
* [Installation](#installation)
* [Setup](#setup)
* [Advanced Features](#advanced-features)
  * [Real-time Preview](#real-time-preview)
  * [Screenshots](#screenshots)
* [Notes](#notes)
* [Limitations](#limitations)
* [Contribution](#contribution)
* [Seeking Assistance](#seeking-assistance)
* [License](#license)

## Requirements

* Android SDK version 16+

## Installation

1. Maven [TBA]
2. Download or clone this module.

## Setup

1. Open Android Studio and go to **File > New > Import Module**. Point the exact path to crowdin module.

2. Connect *crowdin* module in *setting.gradle* file. For this, use the following parameter: 

   ```groovy
   include ':crowdin'
   ```

3. Add dependency on module in *build.gradle* file:

   ```groovy
   implementation project(":crowdin")
   ```

4. Inject Crowdin translations by adding *override* method in *BaseActivity class* to inject Crowdin translations into the Context.

   Note: If you don’t have BaseActivity class add the code to all of your activities.

   <details>
   <summary>Kotlin</summary>

    ```kotlin
   override fun attachBaseContext(newBase: Context) {
     super.attachBaseContext(Crowdin.wrapContext(newBase))
   }
   ```
   </details>

   <details>
   <summary>Java</summary>

   ```java
   @Override 
   protected void attachBaseContext(Context newBase) {
     super.attachBaseContext(Crowdin.wrapContext(newBase));
   }
   ```
   </details>

      In case your project already overrides *attachBaseContext* use the following code:
   ```java
   @Override 
   super.attachBaseContext(Crowdin.wrapContext(SomeLib.wrap(newBase)));
   ```

   SKD uses `androidx` version of libraries. In case your project still not migrated to `androidx` you can add this lines in the `gradle.properties` file:
   ```groovy
   android.enableJetifier=true
   android.useAndroidX=true
   ```
   It might require additional changes in your code. 


5. Enable *Over-The-Air Content Delivery* in your Crowdin project so that application can pull translations from CDN vault. Add the following code to *Application class*:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate() {
       super.onCreate()

       Crowdin.init(applicationContext,
           CrowdinConfig.Builder()
               .withDistributionHash(your_distribution_hash)                            // required
               .withFilePaths(your_file_path)                                           // required
               .withNetworkType(network_type)                                           // optional
               .withRealTimeUpdates()                                                   // optional
               .withScreenshotEnabled()                                                 // optional
               .withSourceLanguage(source_language)                                     // optional
               .withUpdateInterval(interval_in_milisec)                                 // optional
               .withAuthConfig(AuthConfig(client_id, client_secret, organization_name)) // optional
               .build())
       }
   ```
   </details>

   <details>
   <summary>Java</summary>

   ```java
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      Crowdin.init(this,
          new CrowdinConfig.Builder()
              .withDistributionHash(your_distribution_hash)                            // required
              .withFilePaths(your_file_paths)                                          // required
              .withNetworkType(network_type)                                           // optional
              .withRealTimeUpdates()                                                   // optional
              .withScreenshotEnabled()                                                 // optional
              .withSourceLanguage(source_language)                                     // optional
              .withUpdateInterval(interval_in_milisec)                                 // optional
              .withAuthConfig(AuthConfig(client_id, client_secret, organization_name)) // optional
              .build());
   }
   ```
   </details>

   `your_distribution_hash` - when distribution added you will get your unique hash.

   `your_file_path` - files from Crowdin project, translations from which will be sent to the application. Example: `"strings.xml", "arrays.xml", "plurals.xml"`

   `network_type` - optional. NetworkType.ALL, NetworkType.CELLULAR, NetworkType.WIFI;

   `source_language` - source language in your Crowdin project. Example - "en". Required for real time/screenshot functionalities.

   `interval_in_milisec` - interval updates in millisec.

   `client_id`, `client_secret` - crowdin OAuth.

   `organization_name` - add this parameter only if you have own domain.


## Advanced Features

### Real-time Preview

This feature allows translators to see translations in the application in real-time. It can also be used by managers and QA team to preview translations before release.

1. Add the following code to the *Application class*:

   <details>
   <summary>Kotlin</summary>
 
   ```kotlin
   Crowdin.init(applicationContext,
       CrowdinConfig.Builder()
        ... 
        .withRealTimeUpdates()
        .withSourceLanguage(source_language)
        .withAuthConfig(AuthConfig(client_id, client_secret, organization_name))
        ...)
   ```
   </details>

   <details>
   <summary>Java</summary>

   ```java
   Crowdin.init(this,
       new CrowdinConfig.Builder()
           ...
           .withRealTimeUpdates()
           .withSourceLanguage(source_language)
           .withAuthConfig(AuthConfig(client_id, client_secret, organization_name))
           ...);
   ```
   </details>

2. Crowdin Authorization is required for Real-Time Preview. Create connection using *Activity/Fragment* method *inMainActivity class*:  

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       ...
       // Crowdin Auth. required for screenshot/realtime preview functionality.
       Crowdin.authorize(activity)
   }
   ```
   </details>

   <details>
   <summary>Java</summary>

   ```java
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
       super.onCreate(savedInstanceState, persistentState);
       Crowdin.authorize(this);
   }
   ```
   </details>

It will redirect to Crowdin OAuth form and after authorization download all required data automatically.
After loading finished `real-time updates` feature ready for use.

You can disconnect via:
```kotlin
override fun onDestroy() {
    super.onDestroy()
    // Close connection with crowdin.
    Crowdin.disconnectRealTimeUpdates()
}
```

### Screenshots

Enable if you want all the screenshots made in the application to be automatically sent to your Crowdin project with tagged strings. This will provide additional context for translators.

1. Add the following code to the *Application class*:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   Crowdin.init(applicationContext,
       CrowdinConfig.Builder()
           ...
           .withScreenshotEnabled()
           .withSourceLanguage(source_language)
           .withAuthConfig(AuthConfig(client_id, client_secret, organization_name))
           ...)

   // Using system buttons to take screenshot automatically will upload them to crowdin.
   Crowdin.registerScreenShotContentObserver(this)
   ```
   </details>


   <details>
   <summary>Java</summary>

   ```java
   Crowdin.init(this,
       new CrowdinConfig.Builder()
           ...
           .withScreenshotEnabled()
           .withSourceLanguage(source_language)
           .withAuthConfig(AuthConfig(client_id, client_secret, organization_name))
           .build());

   Crowdin.registerScreenShotContentObserver(this); // required for screenshots
   ```
   </details>

2. Crowdin Authorization is required for screenshots. Create connection using *Activity/Fragment* method *inMainActivity class*:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       ...
       // Crowdin Auth. required for screenshot/realtime update functionality.
       Crowdin.authorize(activity)
   }
   ```
   </details>
        
   <details>
   <summary>Java</summary>

   ```java
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
       super.onCreate(savedInstanceState, persistentState);
       Crowdin.authorize(this);
   }
   ```
   </details>

3. After auth success you can use system buttons to capture screenshots or do it programmatically with callback:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   Crowdin.sendScreenshot(activity!!, object : ScreenshotCallback {
      override fun onSuccess() {
          Log.d(TAG, "Screenshot uploaded")
      }

      override fun onFailure(throwable: Throwable) {
          Log.d(TAG, throwable.localizedMessage)
      }
   })
   ```
   </details>

   <details>
   <summary>Java</summary>

   ```java
   View.OnClickListener oclBtnOk = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Crowdin.sendScreenshot(YourActivity.this, new ScreenshotCallback() {
              @Override
              public void onSuccess() {
                  Log.d("", "Screenshot uploaded");
              }

              @Override
              public void onFailure(Throwable throwable) {
                  Log.d("", String.valueOf(throwable));
              }
          });

      }
   };
   ```
   </details>

## Notes
1. You can provide new Strings

   Load your Strings in any way / any time / any place and just call this:
   ```java
   Crowdin.setStrings(language, newStrings);
   // e.g. language="en" newStrings=map of (key-value)s
   ```

2. Please note that Crowdin works with current locale, so if you change locale with
   ```java
   Locale.setDefault(newLocale);
   ```
   Crowdin will start using strings of the new locale.

3. For displaying a string, Crowdin tries to find that in dynamic strings, and will use bundled version as fallback. In the other words, Only the new provided strings will be overridden and for the rest the bundled version will be used.

4. To translate menu items you need to update your `onCreateOptionsMenu` method:

   ```kotlin
   override fun onCreateOptionsMenu(menu: Menu): Boolean {
       menuInflater.inflateWithCrowdin(R.menu.activity_menu, menu, resources)
       return true
   }
   ```

   ```java
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       ExtentionsKt.inflateWithCrowdin(getMenuInflater(), R.menu.your_menu, menu, getResources());
       return true;
   }
   ```

5. In case you have custom views that uses `TypedArray` and `stylable` attributes, you will need to use such approach: 
   ```kotlin
   val textId = typedArray.getResourceId(R.styleable.sample_item, 0)
   textView.setText(textId)
   ```
   instead of `typedArray.getString(R.styleable.sample_item)`

6. Activity title defined via AndroidManifest won't be translated.
   ```xml
   <activity
       android:name=".activities.SampleActivity"
       android:label="@string/title"/>
   ```

   You can simply update your `toolbar` inside of activity or fragment: 
   ```java
   toolbar.setTitle(R.string.title);
   ```

7. In case your project already overrides `attachBaseContext`:
   ```java
   super.attachBaseContext(Crowdin.wrapContext(SomeLib.wrap(newBase)));
   ```

8. You can register/unregister observer for data changes by adding this lines:
   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       // Observe data loading.
       Crowdin.registerDataLoadingObserver(this)
   }
   ```

9. ShakeDetector for triggering force upload from crowdin. It will try to download latest updates from release.
   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       // Simple device shake detector. Could be used for triggering force update.
       Crowdin.registerShakeDetector(this)
   }
   ```
   On each shake event it will trigger this method: `Crowdin.forceUpdate(this)`
   You can call this method from your app. 
   Also there are other public methods in `Crowdin` class. You can find details in `kotlin doc` files. 

## Limitations
1. Plurals are supported from SDK version 24.
2. TabItem text added via xml won't be updated. There is workaround: you can store tabItem titles in your string-array and add tabs dynamically.
3. `PreferenceScreen` defined via XML not supported.

## Contribution
We are happy to accept contributions to the Crowdin Android SDK. To contribute please do the following:
1. Fork the repository on GitHub.
2. Decide which code you want to submit. Commit your changes and push to the new branch.
3. Ensure that your code adheres to standard conventions, as used in the rest of the library.
4. Ensure that there are unit tests for your code.
5. Submit a pull request with your patch on Github.

## Seeking Assistance
If you find any problems or would like to suggest a feature, please feel free to file an issue on Github at [Issues Page](https://github.com/crowdin/mobile-sdk-android/issues).

If you've found an error in these samples, please [contact](https://crowdin.com/contacts) our Support Team.

## License
<pre>
Copyright © 2019 Crowdin

The Crowdin Android SDK is licensed under the MIT License.
See the LICENSE file distributed with this work for additional 
information regarding copyright ownership.

Except as contained in the LICENSE file, the name(s) of the above copyright 
holders shall not be used in advertising or otherwise to promote the sale, 
use or other dealings in this Software without prior written authorization.
</pre>
