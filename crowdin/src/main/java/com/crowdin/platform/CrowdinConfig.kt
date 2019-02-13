package com.crowdin.platform

/**
 * Contains configuration properties for initializing Crowdin.
 */
internal class CrowdinConfig private constructor() {

    var isPersist: Boolean = false

    internal class Builder {
        private var persist: Boolean = false

        fun persist(persist: Boolean): Builder {
            this.persist = persist
            return this
        }

        fun build(): CrowdinConfig {
            val config = CrowdinConfig()
            config.isPersist = persist
            return config
        }
    }

    companion object {

        internal val default: CrowdinConfig
            get() = Builder()
                    .persist(true)
                    .build()
    }
}