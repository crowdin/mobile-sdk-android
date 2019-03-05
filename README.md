## Crowdin 1.0
An easy way to replace bundled Strings dynamically via Crowdin platform.

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

Initialization is done via provider so no need to call additional methods inside of your application.


### 4. Inject into Context

if you have a `BaseActivity` you can add this there, otherwise you have to add it to all of your activities!
```java
@Override
protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(Crowdin.wrapContext(newBase));
}
```

### 5. Done!

Now all strings in your app will be overriden by new strings provided to Crowdin.

## Notes:

Additional info:
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

3. For displaying a string, Crowdin tries to find that in dynamic strings, and will use bundled version as fallback. In the other words, Only the new provided strings will be overriden and for the rest the bundled version will be used.

4. If your application uses `adnroidx` and crowdin imported as a module you'll need to add next lines in the `gradle.properties` file:
```java
android.enableJetifier=true
android.useAndroidX=true
```

5. To translate menu items you need to update your `onCreateOptionsMenu` method:
```java
@Override
public void onCreateOptionsMenu(...) {
    // inflate(R.menu.your_menu, menu);
    Crowdin.updateMenuItemsText(menu, getResources(), R.menu.your_menu);
}
```

6. In case you have custom views that uses `TypedArray` and `stylable` attributes, you will need to use such approach: 
```java
int textId = typedArray.getResourceId(R.styleable.sample_item, 0);
(TextView) textView.setText(textId);
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
 
    
## Limitations:
1. Plurals are supported from SDK version 24.
2. TabItem text added via xml won't be updated. There is workaround: you can store tabItem titles in your string-array and add tabs dynamically.


## License
<pre>
Copyright 2018 Crowdin

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
