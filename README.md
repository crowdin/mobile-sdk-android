[<p align="center"><img src="https://support.crowdin.com/assets/logos/crowdin-dark-symbol.png" data-canonical-src="https://support.crowdin.com/assets/logos/crowdin-dark-symbol.png" width="200" height="200" align="center"/></p>](https://crowdin.com)

# Crowdin Android SDK

Crowdin Android SDK delivers all new translations from Crowdin project to the application immediately. So there is no need to update this application via Google Play Store to get the new version with the localization.


## Table of Contents
* [Requirements](#requirements)
* [Dependencies](#dependencies)
* [Installation](#installation)
* [Features](#features)
  * [Real-time updates](#real-time-updates)
  * [Screenshots](#screenshots)
* [Notes](#notes)
* [Limitations](#limitations)
* [Contribution](#contribution)
* [Seeking Assistance](#seeking-assistance)
* [License](#license)

## Requirements

* Android SDK version 16+

## Dependencies

* `com.android.tools.build:gradle:3.5.0`
* `org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41`
* `androidx.appcompat:appcompat:1.0.2`
* `androidx.work:work-runtime-ktx:2.0.1`
* `com.google.android.material:material:1.0.0`
* `com.squareup.retrofit2:retrofit:2.6.0`
* `com.squareup.retrofit2:converter-gson:2.6.0`
* `com.google.code.gson:gson:2.8.5`
* `com.squareup.okhttp3:logging-interceptor:4.0.1`
* `org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.41`

## Installation

### 1. Add dependency

- add SDK as a library module to your project.

### 2. Update your gradle configuration

`setting.gradle`:
```groovy
include ':crowdin'
```

app `build.gradle`:
```groovy
implementation project(":crowdin")
```

SKD uses `androidx` version of libraries. In case your project still not migrated to `androidx` you can add this lines in the `gradle.properties` file:
```groovy
android.enableJetifier=true
android.useAndroidX=true
```
It might require additional changes in your code. 

Note:
maven: implementation 'TODO.Coming soon'

### 3. Initialize

Add this code in your ``Application`` class.

```kotlin
Crowdin.init(applicationContext,
    CrowdinConfig.Builder()
        .withDistributionHash(your_distribution_hash)                           // required
        .withFilePaths(your_file_path)                                          // required
        .withNetworkType(network_type)                                          // optional
        .withRealTimeUpdates()                                                  // optional
        .withScreenshotEnabled()                                                // optional
        .withSourceLanguage(source_language)                                    // optional
        .withUpdateInterval(interval_in_milisec)                                // optional
        .withAuthConfig(AuthConfig(client_id, client_secret, organization_name))// optional
        .build())
```

`your_distribution_hash` - when distribution added you will get your unique hash.

`your_file_path` - files from Crowdin project, translations from which will be sent to the application. Example: `"strings.xml", "arrays.xml", "plurals.xml"`

`network_type` - optional. NetworkType.ALL, NetworkType.CELLULAR, NetworkType.WIFI;

`source_language` - source language in your Crowdin project. Example - "en". Required for real time/screenshot functionalities.

`interval_in_milisec` - interval updates in millisec.

`client_id`, `client_secret` - crowdin OAuth.

`organization_name` - add this parameter only if you have own domain.

### 4. Inject into Context

if you have a `BaseActivity` you can add this there, otherwise you have to add it to all of your activities!
```kotlin
override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(Crowdin.wrapContext(newBase))
}
```

### 5. Done!

Now all strings in your app will be overridden by new strings provided to Crowdin.

## Features
### Real-time updates

1. Add the following code in Application class:
```kotlin
Crowdin.init(applicationContext,
        CrowdinConfig.Builder()
                 ... 
                 .withRealTimeUpdates()
                 .withSourceLanguage(source_language)
                 .withAuthConfig(AuthConfig(client_id, client_secret, organization_name))
                 ...)
```

2. Crowdin Authorization is required for Real-Time updates. To create connection use this method:  
Activity/Fragment:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    // Crowdin Auth. required for screenshot/realtime update functionality.
    Crowdin.authorize(activity)
}
```
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

1. Add the following code in Application class:
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

2. Auth required. Step #2 from `Real-time updates`

After auth success you can use system buttons to capture screenshots or do it programmatically, with callback if needed:
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
2. Decide which code you want to submit. A submission should be a set of changes that addresses one issue in the issue tracker. Please file one change per issue, and address one issue per change. If you want to make a change that doesn't have a corresponding issue in the issue tracker, please file a new ticket!
3. Ensure that your code adheres to standard conventions, as used in the rest of the library.
4. Ensure that there are unit tests for your code.
5. Submit a pull request with your patch on Github.

## Seeking Assistance
If you find any problems or would like to suggest a feature, please feel free to file an issue on Github at [Issues Page](https://github.com/crowdin/mobile-sdk-android/issues).

If you've found an error in these samples, please [contact](https://crowdin.com/contacts) our Support Team.

## License
<pre>
Copyright © 2019 Crowdin

The Crowdin Android SDK for is licensed under the MIT License. 
See the LICENSE.md file distributed with this work for additional 
information regarding copyright ownership.
</pre>
