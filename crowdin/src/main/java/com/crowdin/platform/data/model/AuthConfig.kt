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
        val redirectURI: String,
        @Deprecated("Use CrowdinConfig property instead")
        val organizationName: String? = null,
        val requestAuthDialog: Boolean = true,
    ) {
        constructor(clientId: String, clientSecret: String, redirectURI: String) : this(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectURI = redirectURI,
            organizationName = null,
            requestAuthDialog = true,
        )

        constructor(clientId: String, clientSecret: String, requestAuthDialog: Boolean, redirectURI: String) : this(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectURI = redirectURI,
            organizationName = null,
            requestAuthDialog = requestAuthDialog,
        )
    }
