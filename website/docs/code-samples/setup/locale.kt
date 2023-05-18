/**
 * Should be overridden in case you want to change locale programmatically.
 * For a custom language, set your application locale with language and country/region constraints.
 * This should match with `Locale code:` for your custom language in Crowdin.
 *
 * language - [a-zA-Z]{2,8}
 * country/region - [a-zA-Z]{2} | [0-9]{3}
 *
 * Example: "aa-BB"
 */
override fun attachBaseContext(newBase: Context) {
    languagePreferences = LanguagePreferences(newBase)
    super.attachBaseContext(
        ContextWrapper(newBase.updateLocale(languagePreferences.getLanguageCode()))
    )
}