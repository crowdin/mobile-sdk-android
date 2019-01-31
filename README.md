## Crowdin 1.0
An easy way to replace bundled Strings dynamically, or provide new translations in Android

### 1. Add dependency
```groovy
implementation 'com.ice.restring:restring:1.0.0'
```

### 2. Initialize
Initialize Crowdin in your Application class:
```java
Crowdin.init(context);
```
or if you want more configurations:
```java
Crowdin.init(context,
              new RestringConfig.Builder()
                  .persist(true)
                  .stringsLoader(new SampleStringsLoader())
                  .build()
        );
```

### 3. Inject into Context
if you have a BaseActivity you can add this there, otherwise you have to add it to all of your activities!
```java
@Override
protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(Crowdin.wrapContext(newBase));
}
```

### 4. Provide new Strings
There're two ways to provide new Strings. You can use either way or both.

First way: You can implement Crowdin.StringsLoader like this:
```java
public class MyStringsLoader implements Crowdin.StringsLoader {

    //This will be called on background thread.
    @Override
    public List<String> getLanguages() {
        //return your supported languages(e.g. "en", ...)
    }

    //This will be called on background thread.
    @Override
    public Map<String, String> getStrings(String language) {
        Map<String, String> map = new HashMap<>();
        // Load your strings here into a map of (key,value)s for this language!
        return map;
    }
}
```
and initialize Crowdin like this:
```java
Crowdin.init(context,
              new RestringConfig.Builder()
                  .persist(true)
                  .stringsLoader(new MyStringsLoader())
                  .build()
        );
```

Second way:
Load your Strings in any way / any time / any place and just call this:
```java
// e.g. language="en" newStrings=map of (key-value)s
Crowdin.setStrings(language, newStrings);
```

### 5. Done!
Now all strings in your app will be overriden by new strings provided to Crowdin.

## Notes:
1. Please note that Crowdin works with current locale, so if you change locale with
```java
Locale.setDefault(newLocale);
```
Crowdin will start using strings of the new locale.

2. By default, Crowdin will use shared preferences to save all strings provided to. So if you set a StringsLoader or call .setString() to set the strings into Crowdin, the strings will be there on the next application launch. In case you don't want Crowdin saves strings into shared preferences, you can set it in initialization, like this:
```java
Crowdin.init(context,
              new RestringConfig.Builder()
                  .persist(false) //Set this to false to prevent saving into shared preferences.
                  .build()
        );
```

3. For displaying a string, Crowdin tries to find that in dynamic strings, and will use bundled version as fallback. In the other words, Only the new provided strings will be overriden and for the rest the bundled version will be used.

## Limitations
1. Plurals are not supported yet.
2. String arrays are not supported yet.

## Docs
* <a href="https://medium.com/@hamidgh/dynamically-change-bundled-strings-a24b97bfd306">Medium</a>
* <a href="https://hamidness.github.io/restring/index.html">Javadocs</a>

## License
<pre>
Copyright 2018 Hamid Gharehdaghi

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
