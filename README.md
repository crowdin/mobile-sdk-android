## Crowdin 1.0
Crowdin SDK delivers all new translations from Crowdin project to the application immediately. So there is no need to update this application via Google Play Store to get the new version with the localization.

### 1. Add dependency.

- add SDK as a library module to your project.

### 2. Update your gradle configuration.

setting.gradle:
```groovy
include ':crowdin'
```

app build.gradle:
```groovy
implementation project(":crowdin")
```
Note:
maven: implementation 'TODO.Coming soon'

### 3. Initialize

Add this code in your ``Application`` class.

```kotlin
Crowdin.init(applicationContext,
        CrowdinConfig.Builder()
                .withDistributionHash(your_distributionHash)    // required
                .withFilePaths(your_file_path) .                // required
                .withNetworkType(NetworkType.WIFI)              // optional
                .withRealTimeUpdates(true)                      // optional
                .withScreenshotEnabled(true)                    // optional
                .withSourceLanguage(source_language)            // optional
                .withUpdateInterval(interval_in_milisec)        // optional
                .build())
```
`distributionHash` - when distribution added you will get your unique hash.

`filePaths` - files from Crowdin project, translations from which will be sent to the application. Example: `"strings.xml", "arrays.xml", "plurals.xml"`

`networkType` - optional. NetworkType.ALL, NetworkType.CELLULAR, NetworkType.WIFI;

`source_language` - source language in your Crowdin project. Example - "en". Required for real time/screenshot functionalities.

`interval_in_milisec` - interval updates in millisec.

### 4. Inject into Context

if you have a `BaseActivity` you can add this there, otherwise you have to add it to all of your activities!
```kotlin
override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(Crowdin.wrapContext(newBase))
}
```

### 5. Done!

Now all strings in your app will be overridden by new strings provided to Crowdin.


## Real-time updates

1. Add the following code in Application class:
```kotlin
Crowdin.init(applicationContext,
        CrowdinConfig.Builder()
                 ... , 
                 .withRealTimeUpdates(true)
                 .withSourceLanguage(source_language)
                 ...)
```

2. Crowdin Authorization is required for Real-Time updates. To create connection use this method:  
Activity/Fragment:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {...
    // Crowdin Auth. required for screenshot/realtime update functionality.
    Crowdin.connectRealTimeUpdates(this)
}
```
In case you are not authorized you will be prompted to login in crowdin platform. Otherwise connection will be created.

You can disconnect via:
```kotlin
override fun onDestroy() {
   super.onDestroy()
   // Close connection with crowdin.
   Crowdin.disconnectRealTimeUpdates()
}
```

## Screenshots

1. Add the following code in Application class:
```kotlin
Crowdin.init(applicationContext, ... ,
        CrowdinConfig.Builder()
                ...
                .withScreenshotEnabled(true)                           
                .withSourceLanguage("en")
                .build())
                
// Using system buttons to take screenshot automatically will upload them to crowdin.
Crowdin.registerScreenShotContentObserver(this)
```

In case you are not authorized you can start auth process from your code by call this method:

`CrowdinWebActivity.launchActivityForResult(activity)`

It will open login webView.
Additionally you can handle result if needed.
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
   when (requestCode) {
       CrowdinWebActivity.REQUEST_CODE -> {
           if (resultCode == Activity.RESULT_OK) {
               // Auth. success, can proceed with screenshot functionality.
           }
       }
   }
}
```

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


## Notes:

Additional info:

1. SKD uses `androidx` version of libraries. In case your project still not migrated to androidx you can add this lines in the `gradle.properties` file:
```groovy
android.enableJetifier=true
android.useAndroidX=true
```
It might require additional changes in your code. 

List of SDK dependencies:
```groovy
dependencies {
    implementation "androidx.appcompat:appcompat:1.0.2"
    
    // Scheduled updates.
    implementation "androidx.work:work-runtime-ktx:2.0.1"
    
    // Support for material components
    implementation "com.google.android.material:material:1.0.0"
    
    // Networking
    implementation "com.squareup.retrofit2:retrofit:2.6.0"
    implementation 'com.squareup.retrofit2:converter-gson:2.6.0'
    implementation "com.google.code.gson:gson:2.8.5"
    implementation "com.squareup.okhttp3:logging-interceptor:3.11.0"
    // Kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.31"
}
```


2. You can provide new Strings

Load your Strings in any way / any time / any place and just call this:
```java
Crowdin.setStrings(language, newStrings);
// e.g. language="en" newStrings=map of (key-value)s
```

3. Please note that Crowdin works with current locale, so if you change locale with
```java
Locale.setDefault(newLocale);
```
Crowdin will start using strings of the new locale.

4. For displaying a string, Crowdin tries to find that in dynamic strings, and will use bundled version as fallback. In the other words, Only the new provided strings will be overridden and for the rest the bundled version will be used.

5. To translate menu items you need to update your `onCreateOptionsMenu` method:
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

6. In case you have custom views that uses `TypedArray` and `stylable` attributes, you will need to use such approach: 
```kotlin
val textId = typedArray.getResourceId(R.styleable.sample_item, 0)
textView.setText(textId)
```
instead of `typedArray.getString(R.styleable.sample_item)`

7. Activity title defined via AndroidManifest won't be translated.
```xml
<activity
    android:name=".activities.SampleActivity"
    android:label="@string/title"/>
```

You can simply update your `toolbar` inside of activity or fragment: 
```java
toolbar.setTitle(R.string.title);
```

8. In case your project already overrides `attachBaseContext`:
```java
super.attachBaseContext(Crowdin.wrapContext(SomeLib.wrap(newBase)));
```

## Additionally
1. You can register/unregister observer for data changes by adding this lines:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    // Observe data loading.
    Crowdin.registerDataLoadingObserver(this)
}
```

2. ShakeDetector for triggering force upload from crowdin. It will try to download latest updates from release.
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    // Simple device shake detector. Could be used for triggering force update.
    Crowdin.registerShakeDetector(this)
}
```
On each shake event it will trigger this method: `Crowdin.forceUpdate(this)`
You can call this method from your app. 
Also there are other public methods in `Crowdin` class. You can find details in `kotlin doc` files. 


## Limitations:
1. Plurals are supported from SDK version 24.
2. TabItem text added via xml won't be updated. There is workaround: you can store tabItem titles in your string-array and add tabs dynamically.
3. `PreferenceScreen` defined via XML not supported.


## License
<pre>
Copyright 2019 Crowdin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
