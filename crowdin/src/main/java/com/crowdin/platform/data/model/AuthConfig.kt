package com.crowdin.platform.data.model

data class AuthConfig
    @Deprecated(
        "Use constructor without organizationName instead. organizationName is now part of the `CrowdinConfig` " +
            "and should be provided with this method: `.withOrganizationName(organizationName)`",
        replaceWith = ReplaceWith("AuthConfig(clientId, clientSecret, requestAuthDialog)"),
    )
    constructor(
        val clientId: String,
        val clientSecret: String,
        @Deprecated("Use CrowdinConfig property instead")
        val organizationName: String? = null,
        val requestAuthDialog: Boolean = true,
        val apiToken: String? = null,
    ) {
        constructor(clientId: String, clientSecret: String, apiToken: String? = null) : this(
            clientId = clientId,
            clientSecret = clientSecret,
            organizationName = null,
            requestAuthDialog = true,
            apiToken = apiToken,
        )

        constructor(clientId: String, clientSecret: String, requestAuthDialog: Boolean, apiToken: String? = null) : this(
            clientId = clientId,
            clientSecret = clientSecret,
            organizationName = null,
            requestAuthDialog = requestAuthDialog,
            apiToken = apiToken,
        )
    }
