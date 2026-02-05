# Installation

Add this to your root `build.gradle` at the end of repositories:

```groovy
allprojects {
   repositories {
       ...
       maven { url 'https://jitpack.io' }
   }
}
```

Add the dependency:

```groovy
dependencies {
   implementation 'com.github.crowdin.mobile-sdk-android:sdk:1.17.0'
}
```

:::info
For Android projects that already have the [transitive dependency](https://docs.gradle.org/current/userguide/dependency_management_terminology.html#sub:terminology_transitive_dependency) of `com.google.code.gson`, after integrating the Crowdin SDK, you will see the following error during build time:

`Duplicate class com.google.gson.DefaultDateTypeAdapter found in modules xxx.jar (xxx.jar) and jetified-gson-2.8.5.jar (com.google.code.gson:gson:2.8.5)`

To fix this, exclude `gson` from Crowdin or from your library, but be sure to keep the newer one for backward compatibility.

```groovy
implementation ('com.github.crowdin.mobile-sdk-android:sdk:1.17.0') {
    exclude group: 'com.google.code.gson', module: 'gson'
}
```
:::

## Requirements

* Android SDK version 16+

## See also

- [Setup](setup.mdx)
