# Cache

To optimize app performance and overall CDN costs, the Crowdin SDK caches downloaded translations. The cache is stored in the app's internal memory and is used to display translations when the device is offline or the cache TTL has not expired.

The minimum and default cache TTL is 15 minutes. You can change the cache TTL using the [`withUpdateInterval`](/setup#config-options) method.

## SDK Cache

The algorithm for the SDK cache is as follows:

* At first start, the SDK downloads the translations for the current locale from the distribution and caches them.
  - The distribution manifest is also cached.
* At the next start, the SDK checks the cache. If the cache has not expired, the SDK uses the cached translations. If the cache has expired, the SDK checks the distribution manifest timestamp to see if there are new releases in the distribution.
  - If there are new releases, the SDK downloads the new translations and updates the cache.
  - If there are no new releases, the SDK uses the cached translations and extends the TTL of the existing cache.

## CDN Cache

The CDN cache - is a CloudFront Content Delivery Network edge cache. The CDN cache is used to deliver translations to the application faster. The CDN cache is not controlled by the SDK, but it usually has a TTL of 1 hour. This means that there may be a delay before new translations appear in the application.

## See also

- [Content Delivery](https://support.crowdin.com/content-delivery/)
