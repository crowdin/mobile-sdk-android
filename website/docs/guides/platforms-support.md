# Platforms Support

## Android TV

Android TV is an Android-based smart TV operating system developed by Google for televisions, digital media players, set-top boxes, and sound bars.

Crowdin SDK is compatible with Android TV. You can use Over-The-Air, Screenshots, Real-Time Preview features for your Android TV application in the same way as for regular Android apps.

## Fire OS

Fire OS is a mobile operating system based on the Android Open Source Project and created by Amazon for its Fire tablets, Echo smart speakers, and Fire TV devices.

| Fire OS Version | Android Version                               |
|-----------------|-----------------------------------------------|
| Fire OS 5       | Based on Android 5.1 (Lollipop, API level 22) |
| Fire OS 6       | Based on Android 7.1 (Nougat, API level 25)   |
| Fire OS 7       | Based on Android 9 (Pie, API level 28)        |

Because both Amazon Fire TV and Android TV use Android, you can publish the same Android app to both the Amazon Appstore and the Google Play Store.

When you test your Amazon Fire TV app code, you use a real Fire TV device (either the set-top box or stick) instead of a virtual emulator. See Connecting to Fire TV via ADB for more details.

The **Crowdin SDK is compatible with Fire OS**. You can use Over-The-Air, Screenshots, and Real-Time Preview features.

:::caution
Some issues are observed with the `initCrowdinControl` and overlay permission. In case you have such issues, please disable SDK Controls.
:::
