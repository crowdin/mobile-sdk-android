package com.crowdin.platform;

/**
 * Contains configuration properties for initializing Crowdin.
 */
public class CrowdinConfig {

    private boolean persist;
    private Crowdin.StringsLoader stringsLoader;

    public boolean isPersist() {
        return persist;
    }

    public Crowdin.StringsLoader getStringsLoader() {
        return stringsLoader;
    }

    private CrowdinConfig() {
    }

    public static class Builder {
        private boolean persist;
        private Crowdin.StringsLoader stringsLoader;

        public Builder persist(boolean persist) {
            this.persist = persist;
            return this;
        }

        public Builder stringsLoader(Crowdin.StringsLoader loader) {
            this.stringsLoader = loader;
            return this;
        }

        public CrowdinConfig build() {
            CrowdinConfig config = new CrowdinConfig();
            config.persist = persist;
            config.stringsLoader = stringsLoader;
            return config;
        }
    }

    static CrowdinConfig getDefault() {
        return new Builder()
                .persist(true)
                .build();
    }
}