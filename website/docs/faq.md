---
description: Explore the Crowdin Android SDK FAQ page for quick answers to your questions. Find troubleshooting tips to optimize your experience.
---

# FAQ

## Is there a caching mechanism in the SDK?

Yes, the SDK caches translations locally. The cache TTL can be configured by the developer. There is also a CDN cache. There is not much control over it, but it is usually 1 hour, so there is a possible delay for new translations to appear in the app. Visit the [Cache](/cache) page for more details.

## What translations will be displayed if the current locale is not present in the Crowdin project?

The SDK resolves the device locale against the project target languages in the following order:

1. Exact locale match — `es-MX` picks the `es-MX` target language.
2. Parent-locale match, mirroring how Android resolves bundled resources — `es-MX` picks `es-419`, `pt-MZ` picks `pt-PT`, `zh-HK` picks `zh-TW`, `zh-SG` picks `zh-CN`, and a Latin-script Serbian locale picks `sr-CS`.
3. Language subtag match — `es-MX` picks `es`.

If none of these match a project target language, the app will use the bundled translations or the default language as a fallback. It will not fall back to any other Crowdin locale.

Parent-locale matching follows the [CLDR parent locale](https://github.com/unicode-org/cldr/blob/main/common/supplemental/supplementalData.xml) data, so a device gets the same language it would get from bundled resources. Script-based matching (Traditional vs. Simplified Chinese, Cyrillic vs. Latin Serbian) requires Android 5.0 or higher, because older versions cannot represent a script subtag in a locale.

## Will the SDK download all translations from Crowdin every time the app launches?

No, the SDK downloads and caches translations locally. It will only download translations if they are not in the cache or if the cache has expired.

## Will the SDK download all translations from the Crowdin CDN or just the current language?

The SDK downloads only the current language translations.

## How do I test the new translations before releasing the distribution?

You can use the Real-Time Preview feature for this. After authorization, it will download the latest translations from the Crowdin project, which can be tested before delivering all translations to users. You can also create a new distribution and test it before releasing the main distribution.

## What format is used to transfer translations from Crowdin to the app?

The translations are transferred in the same format as they are stored in the Crowdin project.

## What translations will be displayed if I lose my Internet connection?

The SDK uses the cached translations. If there are no cached translations, it will use the bundled translations or the default language as a fallback.

## If there are multiple branches in the Crowdin project, which translations will be displayed in the app?

The SDK will use the translations from the branch that is specified in the distribution configuration.

## How can I programmatically get or set translations for a specific language?

The SDK provides `Crowdin.getString(language, key)` and `Crowdin.setString(language, key, value)` methods to programmatically
access and modify translations. This is useful for dynamic content, testing, or implementing custom fallback logic. For more
details, see the [Programmatic String Access](/guides/programmatic-strings) guide.
