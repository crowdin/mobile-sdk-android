[<p align="center"><img src="https://support.crowdin.com/assets/logos/crowdin-dark-symbol.png" data-canonical-src="https://support.crowdin.com/assets/logos/crowdin-dark-symbol.png" width="200" height="200" align="center"/></p>](https://crowdin.com)

# Crowdin Android SDK

Crowdin Android SDK delivers all new translations from Crowdin project to the application immediately. So there is no need to update this application via Google Play Store to get the new version with the localization.

The SDK provides:

* Over-The-Air Content Delivery – the localized content can be sent to the application from the project whenever needed.
* Real-Time Preview – all the translations that are done in the Editor can be shown in your version of the application in real-time. View the translations already made and the ones you're currently typing in.
* Screenshots – all the screenshots made in the application may be automatically sent to your Crowdin project with tagged source strings.

## Status

[![Download](https://api.bintray.com/packages/crowdin/mobile-sdk/mobile-sdk-android/images/download.svg?version=1.1.6)](https://bintray.com/crowdin/mobile-sdk/mobile-sdk-android/1.1.6/link)
[![GitHub issues](https://img.shields.io/github/issues/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/issues)
[![GitHub Release Date](https://img.shields.io/github/release-date/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/releases/latest)
[![GitHub contributors](https://img.shields.io/github/contributors/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/graphs/contributors)
[![GitHub](https://img.shields.io/github/license/crowdin/mobile-sdk-android?cacheSeconds=3600)](https://github.com/crowdin/mobile-sdk-android/blob/master/LICENSE)

[![Azure DevOps builds (branch)](https://img.shields.io/azure-devops/build/crowdin/mobile-sdk-android/12/master?logo=azure-pipelines&cacheSeconds=800)](https://dev.azure.com/crowdin/mobile-sdk-android/_build/latest?definitionId=12&branchName=master)
[![codecov](https://codecov.io/gh/crowdin/mobile-sdk-android/branch/master/graph/badge.svg)](https://codecov.io/gh/crowdin/mobile-sdk-android)
[![Azure DevOps tests (branch)](https://img.shields.io/azure-devops/tests/crowdin/mobile-sdk-android/13/master?cacheSeconds=800)](https://dev.azure.com/crowdin/mobile-sdk-android/_build/latest?definitionId=12&branchName=master)

## Table of Contents
* [Requirements](#requirements)
* [Installation](#installation)
* [Example Project](#example-project)
* [Setup](#setup)
* [Advanced Features](#advanced-features)
  * [Real-time Preview](#real-time-preview)
  * [Screenshots](#screenshots)
* [File Export Patterns](#file-export-patterns)
* [Notes](#notes)
* [Limitations](#limitations)
* [Contributing](#contributing)
* [Seeking Assistance](#seeking-assistance)
* [Security](#security)
* [License](#license)

## Requirements

* Android SDK version 16+

## Installation

You have two ways to install Crowdin Android SDK.

- From JCenter

   ```groovy
   implementation 'com.crowdin.platform:mobile-sdk:1.1.6'
   ```
   
   For Android project which already have [transitive dependency](https://docs.gradle.org/current/userguide/dependency_management_terminology.html#sub:terminology_transitive_dependency) of `com.google.code.gson`, after integration of Crowdin SDK, it will show you the following error during build time:
   
   `Duplicate class com.google.gson.DefaultDateTypeAdapter found in modules xxx.jar (xxx.jar) and jetified-gson-2.8.5.jar (com.google.code.gson:gson:2.8.5)`
   
   To resolve, either exclude `gson` from Crowdin or from your library is OK, but be sure to keep the newer one for backward-compatibility.
   
   ```groovy
    implementation ('com.crowdin.platform:mobile-sdk:1.1.6') {
        exclude group: 'com.google.code.gson', module: 'gson'
    }
   ```
   

- Manually download or clone this module.

## Example Project

To discover how Android SDK is integrated into a real project see the [Example project](https://github.com/crowdin/mobile-sdk-android/tree/master/example). You can set up this project for yourself, run, and test. 

To run the example project, first clone the repo, fill in SDK configuration in the *App* class and run project in the Example directory. 

## Setup

To configure Android SDK integration you need to:

- Upload your *xml* localization files to Crowdin. If you have ready translations, you can also upload them.
- Set up Distribution in Crowdin.
- Set up SDK and enable Over-The-Air Content Delivery feature in your project.

**Distribution** is a CDN vault that mirrors the translated content of your project and is required for integration with Android app.

To manage distributions open the needed project and go to *Over-The-Air Content Delivery*. You can create as many distributions as you need and choose different files for each. You’ll need to click the *Release* button next to the necessary distribution every time you want to send new translations to the app.

**Notes:**
- The CDN feature does not update the localization files. if you want to add new translations to the localization files you need to do it yourself.
- Once SDK receives the translations, it's stored on the device as application files for further sessions to minimize requests the next time the app starts. Storage time can be configured using `withUpdateInterval` option.
- CDN caches all the translation in release for up to 15 minutes and even when new translations are released in Crowdin, CDN may return it with a delay.

---

To integrate SDK with your application you need to follow step by step instructions:

1. Inject Crowdin translations by adding *override* method in *BaseActivity* class to inject Crowdin translations into the Context.

   **Note!** If you don’t have *BaseActivity* class add the following code to all of your activities.

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

2. Enable *Over-The-Air Content Delivery* in your project so that application can pull translations from CDN vault. Add the following code to *App*/*Application* class:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate() {
       super.onCreate()

       Crowdin.init(applicationContext,
           CrowdinConfig.Builder()
               .withDistributionHash(your_distribution_hash)
               .withNetworkType(network_type)                // optional
               .withUpdateInterval(interval_in_seconds)      // optional
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
              .withDistributionHash(your_distribution_hash)
              .withNetworkType(network_type)                // optional
              .withUpdateInterval(interval_in_seconds)      // optional
              .build());
   }
   ```
   </details>

    | Config option              | Description                                                 | Example                                        |
    |----------------------------|-------------------------------------------------------------|------------------------------------------------|
    | `withDistributionHash`     | Distribution Hash | `withDistributionHash("7a0c1...7uo3b")`
    | `withNetworkType`          | Network type to be used for translations download | Acceptable values are:<br>- `NetworkType.ALL` (default)<br> - `NetworkType.CELLULAR`<br>- `NetworkType.WIFI`
    | `withUpdateInterval`       | Translations update interval in seconds. The minimum and the default value is 15 minutes. Translations will be updated every defined time interval once per application load | `withUpdateInterval(900)`

## Advanced Features
### Real-time Preview

All the translations that are done in the Editor can be shown in your version of the application in real-time. View the translations already made and the ones you're currently typing in.

[<p align='center'><img src='https://github.com/crowdin/mobile-sdk-android/blob/docs/sdk_preview.gif' width='500'/></p>](#)

1. Add the following code to the *Application* class:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate() {
       super.onCreate()

       Crowdin.init(applicationContext,
           CrowdinConfig.Builder()
               .withDistributionHash(your_distribution_hash)
               .withRealTimeUpdates()
               .withSourceLanguage(source_language)
               .withAuthConfig(AuthConfig(client_id, client_secret, organization_name))
               .withNetworkType(network_type)                                           // optional
               .withUpdateInterval(interval_in_seconds)                                 // optional
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
              .withDistributionHash(your_distribution_hash)
              .withRealTimeUpdates()
              .withSourceLanguage(source_language)
              .withAuthConfig(new AuthConfig(client_id, client_secret, organization_name_or_null))
              .withNetworkType(network_type)                                           // optional
              .withUpdateInterval(interval_in_seconds)                                 // optional
              .build());
   }
   ```
   </details>

    | Config option                  | Description                                                 | Example                                        |
    |--------------------------------|-------------------------------------------------------------|------------------------------------------------|
    | `withDistributionHash`         | Distribution Hash | `withDistributionHash("7a0c1...7uo3b")`
    | `withRealTimeUpdates`          | Enable Real-Time Preview feature | `withRealTimeUpdates()`
    | `withSourceLanguage`           | Source language code in your Crowdin project | `withSourceLanguage("en")`
    | `withAuthConfig`               | Crowdin authorization config | `withAuthConfig(AuthConfig("client_id", "client_secret", "organization_name"))`
    | `client_id`, `client_secret`   | Crowdin OAuth Client ID and Client Secret  | `"gpY2yC...cx3TYB"`, `"Xz95tfedd0A...TabEDx9T"`
    | `organization_name`            | An Organization domain name (for Crowdin Enterprise users only) | `"mycompany"` for Crowdin Enterprise or `null` for crowdin.com
    | `withNetworkType`              | Network type to be used for translations download | Acceptable values are:<br>- `NetworkType.ALL` (default)<br> - `NetworkType.CELLULAR`<br>- `NetworkType.WIFI`
    | `withUpdateInterval`           | Translations update interval in seconds. The minimum and the default value is 15 minutes. Translations will be updated every defined time interval once per application load | `withUpdateInterval(900)`

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

[<p align='center'><img src='https://github.com/crowdin/mobile-sdk-android/blob/docs/sdk_screenshots.gif' width='500'/></p>](#)

You can take a screenshots and automatically upload them tagged to Crowdin in two ways: using **system buttons** or create **own handler** (for example, clicking on some button in your application).

1. Add the following code to the *Application* class:

   <details>
   <summary>Kotlin</summary>

   ```kotlin
   override fun onCreate() {
       super.onCreate()

       Crowdin.init(applicationContext,
           CrowdinConfig.Builder()
               .withDistributionHash(your_distribution_hash)
               .withScreenshotEnabled()
               .withSourceLanguage(source_language)
               .withAuthConfig(AuthConfig(client_id, client_secret, organization_name))
               .withNetworkType(network_type)                                           // optional
               .withUpdateInterval(interval_in_millisec)                                // optional
               .build())
    }

   // Using system buttons to take screenshots and automatically upload them to Crowdin.
   Crowdin.registerScreenShotContentObserver(this)
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
              .withDistributionHash(your_distribution_hash)
              .withScreenshotEnabled()
              .withSourceLanguage(source_language)
              .withAuthConfig(new AuthConfig(client_id, client_secret, organization_name_or_null))
              .withNetworkType(network_type)                                           // optional
              .withUpdateInterval(interval_in_seconds)                                 // optional
              .build());
   }
   ```
   </details>

   | Config option                  | Description                                                 | Example                                        |
   |--------------------------------|-------------------------------------------------------------|------------------------------------------------|
   | `withDistributionHash`         | Distribution Hash | `withDistributionHash("7a0c1...7uo3b")`
   | `withScreenshotEnabled`        | Enable Screenshots feature | `withScreenshotEnabled()`
   | `withSourceLanguage`           | Source language code in your Crowdin project | `withSourceLanguage("en")`
   | `withAuthConfig`               | Crowdin authorization config | `withAuthConfig(AuthConfig("client_id", "client_secret", "organization_name"))`
   | `client_id`, `client_secret`   | Crowdin OAuth Client ID and Client Secret  | `"gpY2yC...cx3TYB"`, `"Xz95tfedd0A...TabEDx9T"`
   | `organization_name`            | An Organization domain name (for Crowdin Enterprise users only) | `"mycompany"` for Crowdin Enterprise or `null` for crowdin.com
   | `withNetworkType`              | Network type to be used for translations download | Acceptable values are:<br>- `NetworkType.ALL` (default)<br> - `NetworkType.CELLULAR`<br>- `NetworkType.WIFI`
   | `withUpdateInterval`           | Translations update interval in seconds. The minimum and the default value is 15 minutes. Translations will be updated every defined time interval once per application load | `withUpdateInterval(900)`

    **Note!** Using `Crowdin.registerScreenShotContentObserver(this)` (system buttons handler) for sending screenshots to Crowdin requires Storage permission for your app.

2. Crowdin Authorization is required for Screenshots feature. Create connection using *Activity/Fragment* method in *MainActivity* class:

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

3. If you want to setup own handler for capturing screenshots you can do it programmatically with callback:

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

1. Crowdin works with current locale, so if you change locale with
   ```java
   Locale.setDefault(newLocale);
   ```
   Crowdin will start using strings of the new locale.

2. For displaying a string, Crowdin tries to find that in dynamic strings, and will use bundled version as fallback. In the other words, Only the new provided strings will be overridden and for the rest the bundled version will be used.

3. To translate menu items you need to update your `onCreateOptionsMenu` method:

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

4. In case you have custom views that uses `TypedArray` and `stylable` attributes, you will need to use such approach:
   ```kotlin
   val textId = typedArray.getResourceId(R.styleable.sample_item, 0)
   textView.setText(textId)
   ```
   instead of `typedArray.getString(R.styleable.sample_item)`

5. Activity title defined via *AndroidManifest* won't be translated.
   ```xml
   <activity
       android:name=".activities.SampleActivity"
       android:label="@string/title"/>
   ```

   You can simply update your `toolbar` inside of activity or fragment:
   ```java
   toolbar.setTitle(R.string.title);
   ```

6. In case your project already overrides `attachBaseContext`:
   ```java
   super.attachBaseContext(Crowdin.wrapContext(SomeLib.wrap(newBase)));
   ```

7. You can register/unregister observer for data changes by adding this lines:
   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       Crowdin.registerDataLoadingObserver(this)
   }
   ```

8. ShakeDetector for triggering force upload from Crowdin. It will try to download latest updates from release.
   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       Crowdin.registerShakeDetector(this)
   }
   ```

   On each shake event it will trigger `Crowdin.forceUpdate(this)` method. You can call this method from your app.
   Also there are other public methods in `Crowdin` class. You can find details in `kotlin doc` files.

9. Currently, Custom Languages, Dialects, and Language Mapping are not supported for Android SDK.

## Limitations
1. Plurals are supported from Android SDK version 24.
2. TabItem text added via xml won't be updated. There is workaround: you can store tabItem titles in your string-array and add tabs dynamically.
3. `PreferenceScreen` defined via XML not supported.

## Contributing

If you want to contribute please read the [Contributing](/CONTRIBUTING.md) guidelines.

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
The Crowdin Android SDK is licensed under the MIT License.
See the LICENSE file distributed with this work for additional 
information regarding copyright ownership.

Except as contained in the LICENSE file, the name(s) of the above copyright 
holders shall not be used in advertising or otherwise to promote the sale, 
use or other dealings in this Software without prior written authorization.
</pre>
