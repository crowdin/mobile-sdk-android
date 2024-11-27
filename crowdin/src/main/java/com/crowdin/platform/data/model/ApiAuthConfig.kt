package com.crowdin.platform.data.model

/**
 * Configuration class for API authentication.
 *
 * This class provides a way to configure API authentication using a Crowdin API token.
 * Use this class to pass the `apiToken` for direct API calls, bypassing the need for OAuth-based
 * authentication with client ID and secret.
 *
 * @property apiToken The API token for authenticating API requests.
 *                    This token is required to enable direct API calls without web-based authorization.
 *
 * Example usage:
 * ```
 * val apiAuthConfig = ApiAuthConfig(apiToken = "your_api_token_here")
 * ```
 */
data class ApiAuthConfig(
    val apiToken: String,
)
