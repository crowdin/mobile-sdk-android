# Multiple Flavor Apps

If your application has multiple flavors, you can also localize each flavor using Crowdin Android SDK.

To use the Crowdin Android SDK in a multi-flavor application do the following things:

- Upload your localization files for each flavor to Crowdin. It could be separated using folders or branches (recommended).
- Create a different distribution for each flavor by selecting only the files related to that flavor.
- Create `buildConfigFields` in the `build.gradle`:

  ```groovy
  productFlavors {
      flavor1 {
          buildConfigField "String", "DISTRIBUTION", "\"<flavor1_distribution_hash>\""
      }
      flavor2 {
          buildConfigField "String", "DISTRIBUTION", "\"<flavor2_distribution_hash>\""
      }
  }
  ```

- Use the new `buildConfigField` in the Crowdin SDK configuration:

  ```kotlin
  Crowdin.init(
      applicationContext,
      CrowdinConfig.Builder()
          .withDistributionHash(BuildConfig.DISTRIBUTION)        
          .build()
  )
  ```
