# Programmatic String Access

In some cases, you may need to programmatically get or set translations for specific languages without relying on the standard
Android resource system. The Crowdin SDK provides methods to directly access and modify translation strings.

:::info Version Requirement
Available starting from Crowdin SDK version 1.16.0.
:::

## Getting a String

You can retrieve a translation for a specific language using the `Crowdin.getString()` method:

```kotlin
val translation = Crowdin.getString("en-US", "string_key")
```

### Parameters

- `language` - Language code in the format `language` or `language-country` (e.g., `en`, `en-GB`, `en-US`).
  See [Crowdin Language Codes](https://support.crowdin.com/developer/language-codes/) for more details.
- `key` - The string resource key as defined in your Crowdin project.

### Return Value

Returns the translated string for the specified language and key. If the translation is not found, an empty string is returned.

## Setting a String

You can programmatically set a translation for a specific language using the `Crowdin.setString()` method:

```kotlin
Crowdin.setString("en-US", "string_key", "Hello, World!")
```

### Parameters

- `language` - Language code in the format `language` or `language-country` (e.g., `en`, `en-GB`, `en-US`)
- `key` - The string resource key
- `value` - The translation string value

## Best Practices

### Use Configuration Locale

When retrieving translations for the current language, always use `resources.configuration.getLocale()` instead of
`Locale.getDefault()`. This ensures the language tag matches exactly what the SDK uses to store translations from the
distribution.

```kotlin
// Recommended: Use configuration locale
val languageTag = resources.configuration.getLocale().toLanguageTag()
val translation = Crowdin.getString(languageTag, "string_key")

// Not recommended: Using default locale may not match distribution locale
val languageTag = Locale.getDefault().toLanguageTag()
val translation = Crowdin.getString(languageTag, "string_key")
```

**This is especially important when implementing in-app language switching or when using Android's per-app language settings (
Android 13+).** If your app allows users to change the language within the app, or if users set a different language for your app
in system settings, `Locale.getDefault()` will still return the system locale, not the app's current language. Using
`resources.configuration.getLocale()` ensures you always get the correct locale that matches your app's current language setting.

## Use Cases

These methods are useful for:

- **Dynamic Content**: Setting translations dynamically based on runtime conditions
- **Testing**: Injecting test translations without modifying resource files
- **Custom Fallbacks**: Implementing custom fallback logic for missing translations
- **A/B Testing**: Switching between different translation variants programmatically
- **Analytics**: Retrieving specific translations for logging or analytics purposes

:::info
- Strings set using `setString()` are stored in memory and will be available for the current session
- These strings will be included in the SDK's cache and persist across app restarts
  :::
