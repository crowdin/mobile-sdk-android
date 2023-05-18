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
@Override
protected void attachBaseContext(Context newBase) {
    languagePreferences = new LanguagePreferences(newBase);
    super.attachBaseContext(new ContextWrapper(newBase) {
        @Override
        public Context getApplicationContext() {
            return this;
        }

        @Override
        public Resources getResources() {
            Configuration configuration = getBaseContext().getResources().getConfiguration();
            configuration.setLocale(new Locale(languagePreferences.getLanguageCode()));
            Context updatedContext = getBaseContext().createConfigurationContext(configuration);
            return updatedContext.getResources();
        }
    });
}
