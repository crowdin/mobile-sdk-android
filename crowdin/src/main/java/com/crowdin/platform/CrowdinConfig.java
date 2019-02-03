package com.crowdin.platform;

/**
 * Contains configuration properties for initializing Crowdin.
 */
public class CrowdinConfig {

    private boolean persist;

    boolean isPersist() {
        return persist;
    }

    private CrowdinConfig() {
    }

    public static class Builder {
        private boolean persist;

        public Builder persist(boolean persist) {
            this.persist = persist;
            return this;
        }

        public CrowdinConfig build() {
            CrowdinConfig config = new CrowdinConfig();
            config.persist = persist;
            return config;
        }
    }

    static CrowdinConfig getDefault() {
        return new Builder()
                .persist(true)
                .build();
    }
}