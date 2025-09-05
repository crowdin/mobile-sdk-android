package com.crowdin.platform.data.model

data class AuthConfig
    @Deprecated(
        "Use constructor without organizationName instead. organizationName is now part of the `CrowdinConfig` " +
            "and should be provided with this method: `.withOrganizationName(organizationName)`",
        replaceWith = ReplaceWith("AuthConfig(clientId, clientSecret, redirectURI, requestAuthDialog)"),
    )
    constructor(
        val clientId: String,
        val clientSecret: String,
        val redirectURI: String = DEFAULT_REDIRECT_URI,
        @Deprecated("Use CrowdinConfig property instead")
        val organizationName: String? = null,
        val requestAuthDialog: Boolean = true,
    ) {
        constructor(clientId: String, clientSecret: String, redirectURI: String = DEFAULT_REDIRECT_URI) : this(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectURI = redirectURI,
            organizationName = null,
            requestAuthDialog = true,
        )

        constructor(
            clientId: String,
            clientSecret: String,
            requestAuthDialog: Boolean,
            redirectURI: String = DEFAULT_REDIRECT_URI,
        ) : this(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectURI = redirectURI,
            organizationName = null,
            requestAuthDialog = requestAuthDialog,
        )

        private companion object {
            private const val DEFAULT_REDIRECT_URI = "crowdintest://"
        }
    }
