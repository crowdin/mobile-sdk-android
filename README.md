[<p align="center"><img src="https://support.crowdin.com/assets/logos/crowdin-dark-symbol.png" data-canonical-src="https://support.crowdin.com/assets/logos/crowdin-dark-symbol.png" width="200" height="200" align="center"/></p>](https://crowdin.com)

# Crowdin Android SDK

Crowdin Android SDK delivers all new translations from Crowdin project to the application immediately. So there is no need to update this application via Google Play Store to get the new version with the localization.

The SDK provides:

* Over-The-Air Content Delivery – the localized files can be sent to the application from the project whenever needed
* Real-time Preview – all the translations that are done via Crowdin Editor can be shown in the application in real-time
* Screenshots – all screenshots made in the application may be automatically sent to your Crowdin project with tagged source strings

## Status

[![Download](https://api.bintray.com/packages/crowdin/mobile-sdk/mobile-sdk-android/images/download.svg?version=1.1.2)](https://bintray.com/crowdin/mobile-sdk/mobile-sdk-android/1.1.2/link)
[![GitHub issues](https://img.shields.io/github/issues/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/issues)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/m/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/graphs/commit-activity)
[![GitHub last commit](https://img.shields.io/github/last-commit/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/commits/master)
[![GitHub contributors](https://img.shields.io/github/contributors/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/graphs/contributors)
[![GitHub](https://img.shields.io/github/license/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/blob/master/LICENSE)

[![Azure DevOps builds (branch)](https://img.shields.io/azure-devops/build/crowdin/mobile-sdk-android/12/master?logo=azure-pipelines&cacheSeconds=800)](https://dev.azure.com/crowdin/mobile-sdk-android/_build/latest?definitionId=12&branchName=master)
[![codecov](https://codecov.io/gh/crowdin/mobile-sdk-android/branch/master/graph/badge.svg)](https://codecov.io/gh/crowdin/mobile-sdk-android)
[![Azure DevOps tests (branch)](https://img.shields.io/azure-devops/tests/crowdin/mobile-sdk-android/13/master?cacheSeconds=800)](https://dev.azure.com/crowdin/mobile-sdk-android/_build/latest?definitionId=12&branchName=master)


## Table of Contents
* [Requirements](#requirements)
* [Installation](#installation)
* [Setup](#setup)
* [Advanced Features](#advanced-features)
  * [Real-time Preview](#real-time-preview)
  * [Screenshots](#screenshots)
* [Parameters](#parameters)
* [File Export Patterns](#file-export-patterns)
* [Notes](#notes)
* [Limitations](#limitations)
* [Contribution](#contribution)
* [Seeking Assistance](#seeking-assistance)
* [Security](#security)
* [License](#license)

## Requirements

* Android SDK version 16+

## Installation

1. JCenter 
   
   ```groovy
   implementation 'com.crowdin.platform:mobile-sdk:1.1.2'
   ```

2. Download or clone this module.

## Setup

To configure Android SDK integration you need to:

- Set up Distribution in Crowdin.
- Set up SDK and enable Over-The-Air Content Delivery feature using Android Studio.

**Distribution** is a CDN vault that mirrors the translated content of your project and is required for integration with Android app.

To manage distributions open the needed project and go to *Over-The-Air Content Delivery*. You can create as many distributions as you need and choose different files for each. You’ll need to click the *Release* button next to the necessary distribution every time you want to send new translations to the app.

**Note:** currently, Custom Languages, Dialects, and Language Mapping are not supported for Android SDK.

To integrate SDK with your application you need to follow step by step instructions:

1. Inject Crowdin translations by adding *override* method in *BaseActivity* class to inject Crowdin translations into the Context.

   **Note:** If you don’t have *BaseActivity* class add the code to all of your activities.

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

2. Enable *Over-The-Air Content Delivery* in your Crowdin project so that application can pull translations from CDN vault. Add the following code to *App*/*Application* class:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate() {
       super.onCreate()

       Crowdin.init(applicationContext,
           CrowdinConfig.Builder()
               .withDistributionHash(your_distribution_hash)                            // required
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

   `network_type` - optional. Acceptable values are `NetworkType.ALL`, `NetworkType.CELLULAR`, `NetworkType.WIFI`;

   `source_language` - source language in your Crowdin project. Example - `"en"`. Required for Screenshots and Real-Time Preview features.

   `interval_in_milisec` - translations update interval in milliseconds.

   `client_id`, `client_secret` - Crowdin OAuth Client ID and Client Secret.

   `organization_name` - Organization domain name (for Crowdin Enterprise users only).

## Advanced Features

### Real-time Preview

This feature allows translators to see translations in the application in real-time. It can also be used by managers and QA team to preview translations before release.

1. Add the following code to the *Application* class:

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

2. Crowdin Authorization is required for Real-Time Preview feature. Create connection using *Activity/Fragment* method *inMainActivity* class:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       Crowdin.authorize(this)
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

**Note:** tt will redirect to Crowdin OAuth form and after authorization download all required data automatically.

You can disconnect via:
```kotlin
override fun onDestroy() {
    super.onDestroy()
    // Close connection with Crowdin.
    Crowdin.disconnectRealTimeUpdates()
}
```

### Screenshots

Enable if you want all the screenshots made in the application to be automatically sent to your Crowdin project with tagged strings. This will provide additional context for translators.
You can use system buttons to a take screenshot and automatically upload them to Crowdin or you can create your own handler (for example, clicking on some button in your application).

1. Add the following code to the *Application* class:

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

   // Using system buttons to take screenshot automatically will upload them to Crowdin.
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

    // Using system buttons to take screenshot automatically will upload them to Crowdin.
   Crowdin.registerScreenShotContentObserver(this);
   ```
   </details>

    **Note:** using `Crowdin.registerScreenShotContentObserver(this)` (system buttons handler) for sending screenshots to Crowdin requires Storage permission for your app.

2. Crowdin Authorization is required for screenshots. Create connection using *Activity/Fragment* method in *MainActivity* class:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
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

## Parameters

<table class="table table-bordered" style="font-size: 15px;">
   <tr><td colspan="2"><b>Required for all features </b></td></tr>
   <tr><td style="vertical-align:middle"> your_distribution_hash</td><td>Unique hash which you can get by going to <b>Over-The-Air Content Delivery</b> in your project settings. To see the distribution hash open the needed distribution, choose <b>Edit</b> and copy distribution hash</td></tr>
   <tr><td colspan="2"><b>Required for advanced features</b></td></tr>
   <tr><td style="vertical-align:middle">source_language</td><td>Source language in your Crowdin project (e.g. "en")</td></tr>
   <tr><td style="vertical-align:middle">client_id; <br>client_secret</td><td>Crowdin authorization credentials. Open the project and go to <b>Over-The-Air Content Delivery</b>, choose the feature you need and click <b>Get Credentials</b></td></tr>
   <tr><td colspan="2"><b>Optional</b></td></tr>
   <tr><td style="vertical-align:middle">network_type</td><td>Network type to be used. You may select <code>NetworkType.ALL</code>, <code>NetworkType.CELLULAR</code>, or <code>NetworkType.WIFI</code></td></tr>
   <tr><td style="vertical-align:middle">interval_in_milisec</td><td>Translations update intervals in milliseconds. Allowed values - from 15 minutes. If not set - translations in an application will be updated once per application load.</td></tr>
  </table>

## File Export Patterns

You can set file export patterns and check existing ones using *File Settings*. The following placeholders are supported:

<table class="table table-bordered">
  <thead>
    <tr>
      <th>Name</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="vertical-align:middle">%language%</td>
      <td>Language name (e.g. Ukrainian)</td>
    </tr>
    <tr>
      <td style="vertical-align:middle">%two_letters_code%</td>
      <td>Language code ISO 639-1 (e.g. uk)</td>
    </tr>
    <tr>
      <td style="vertical-align:middle">%three_letters_code%</td>
      <td>Language code ISO 639-2/T (e.g. ukr)</td>
    </tr>
    <tr>
      <td style="vertical-align:middle">%locale%</td>
      <td>Locale (e.g. uk-UA)</td>
    </tr>
    <tr>
      <td style="vertical-align:middle">%locale_with_underscore%</td>
      <td>Locale (e.g. uk_UA)</td>
    </tr>
      <tr>
      <td style="vertical-align:middle">%android_code%</td>
      <td>Android Locale identifier used to name "values-" directories</td>
    </tr>
   </tbody>
</table>

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

6. Activity title defined via *AndroidManifest* won't be translated.
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

9. ShakeDetector for triggering force upload from Crowdin. It will try to download latest updates from release.
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
1. Plurals are supported from Android SDK version 24.
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

Need help working with Crowdin Android SDK or have any questions?
[Contact Customer Success Service](https://crowdin.com/contacts).

## Security

Crowdin Android SDK CDN feature is built with security in mind, which means minimal access possible from the end-user is required. 
When you decide to use Crowdin Android SDK, please make sure you’ve made the following information accessible to your end-users.

- We use the advantages of Amazon Web Services (AWS) for our computing infrastructure. AWS has ISO 27001 certification and has completed multiple SSAE 16 audits. All the translations are stored at AWS servers.
- When you use Crowdin Android SDK CDN – translations are uploaded to Amazon CloudFront to be delivered to the app and speed up the download. Keep in mind that your users download translations without any additional authentication.
- We use encryption to keep your data private while in transit.
- We do not store any Personally Identifiable Information (PII) about the end-user, but you can decide to develop the opt-out option inside your application to make sure your users have full control.
- The Automatic Screenshots and Real-Time Preview features are supposed to be used by the development team and translators team. Those features should not be compiled to the production version of your app. Therefore, should not affect end-users privacy in any way.

## License
<pre>
Copyright © 2020 Crowdin

The Crowdin Android SDK is licensed under the MIT License.
See the LICENSE file distributed with this work for additional 
information regarding copyright ownership.

Except as contained in the LICENSE file, the name(s) of the above copyright 
holders shall not be used in advertising or otherwise to promote the sale, 
use or other dealings in this Software without prior written authorization.
</pre>
