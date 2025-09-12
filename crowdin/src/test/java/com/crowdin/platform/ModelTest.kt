package com.crowdin.platform

import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.AuthResponse
import com.crowdin.platform.data.model.RefreshToken
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.data.model.TokenRequest
import com.crowdin.platform.data.model.ViewData
import com.crowdin.platform.data.parser.MenuItemStrings
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ModelTest {
    @Test
    fun tokenRequestTest() {
        // Given
        val grantType = "password"
        val clientId = "213455asc112"
        val clientSecret = "lad12141ax"
        val redirectUri = "test"
        val code = "544"

        // When
        val tokenRequest = TokenRequest(grantType, clientId, clientSecret, redirectUri, code)

        // Then
        assertThat(tokenRequest.grantType, `is`(grantType))
        assertThat(tokenRequest.clientId, `is`(clientId))
        assertThat(tokenRequest.clientSecret, `is`(clientSecret))
        assertThat(tokenRequest.redirectUri, `is`(redirectUri))
        assertThat(tokenRequest.code, `is`(code))
    }

    @Test
    fun authConfigTest() {
        // Given
        val clientId = "213455asc112"
        val clientSecret = "lad12141ax"
        val redirectURI = "redirectURI"
        val organizationName = "Test"

        // When
        val authConfig = AuthConfig(clientId, clientSecret, redirectURI, organizationName)

        // Then
        assertThat(authConfig.clientId, `is`(clientId))
        assertThat(authConfig.clientSecret, `is`(clientSecret))
        assertThat(authConfig.redirectURI, `is`(redirectURI))
        assertThat(authConfig.organizationName, `is`(organizationName))
    }

    @Test
    fun authResponseTest() {
        // Given
        val tokenType = "test"
        val expiresIn = 10
        val accessToken = "1234"
        val refreshToken = "qwerty"

        // When
        val authResponse = AuthResponse(tokenType, expiresIn, accessToken, refreshToken)

        // Then
        assertThat(authResponse.tokenType, `is`(tokenType))
        assertThat(authResponse.expiresIn, `is`(expiresIn))
        assertThat(authResponse.accessToken, `is`(accessToken))
        assertThat(authResponse.refreshToken, `is`(refreshToken))
    }

    @Test
    fun authDataTest() {
        // Given
        val tokenType = "test"
        val expiresIn = 1000
        val accessToken = "1234"
        val refreshToken = "qwerty"
        val authResponse = AuthResponse(tokenType, expiresIn, accessToken, refreshToken)

        // When
        val authInfo1 = AuthInfo(authResponse)
        val authInfo2 = AuthInfo(authResponse)

        // Then
        assertThat(authInfo1.isExpired(), `is`(false))
        assertThat(authInfo1.accessToken, `is`(accessToken))
        assertThat(authInfo1.refreshToken, `is`(refreshToken))
        assertThat(authInfo1.hashCode(), `is`(authInfo2.hashCode()))
    }

    @Test
    fun refreshTokenTest() {
        // Given
        val grantType = "test"
        val clientId = "213455asc112"
        val clientSecret = "lad12141ax"
        val refreshToken = "token"

        // When
        val token = RefreshToken(grantType, clientId, clientSecret, refreshToken)

        // Then
        assertThat(token.grantType, `is`(grantType))
        assertThat(token.clientId, `is`(clientId))
        assertThat(token.clientSecret, `is`(clientSecret))
        assertThat(token.refreshToken, `is`(refreshToken))
    }

    @Test
    fun viewDataTest() {
        // Given
        val textMetaData = TextMetaData()
        val x = 2
        val y = 4
        val width = 5
        val height = 10

        // When
        val viewData = ViewData(textMetaData, x, y, width, height)

        // Then
        assertThat(viewData.textMetaData, `is`(textMetaData))
        assertThat(viewData.x, `is`(x))
        assertThat(viewData.y, `is`(y))
        assertThat(viewData.width, `is`(width))
        assertThat(viewData.height, `is`(height))
    }

    @Test
    fun textMetaDataParseStringsTest() {
        // Given
        val actualTextMetaData = TextMetaData()
        val testMetaData = TextMetaData()
        testMetaData.textAttributeKey = "string1"

        // When
        actualTextMetaData.parseResult(testMetaData)

        // Then
        assertThat(actualTextMetaData.hasAttributeKey, `is`(true))
    }

    @Test
    fun textMetaDataParseArraysTest() {
        // Given
        val actualTextMetaData = TextMetaData()
        val testMetaData = TextMetaData()
        testMetaData.arrayName = "array1"
        testMetaData.arrayIndex = 1

        // When
        actualTextMetaData.parseResult(testMetaData)

        // Then
        assertThat(actualTextMetaData.isArrayItem, `is`(true))
    }

    @Test
    fun textMetaDataParsePluralTest() {
        // Given
        val actualTextMetaData = TextMetaData()
        val testMetaData = TextMetaData()
        testMetaData.pluralName = "plural1"
        testMetaData.pluralQuantity = 1

        // When
        actualTextMetaData.parseResult(testMetaData)

        // Then
        assertThat(actualTextMetaData.isPluralData, `is`(true))
    }

    @Test
    fun textMetaDataEqualityTest() {
        val textMetaData1 = TextMetaData()
        val textMetaData2 = TextMetaData()

        assertThat(textMetaData1, `is`(textMetaData2))
        assertThat(textMetaData1.hashCode(), `is`(textMetaData2.hashCode()))
    }

    @Test
    fun menuItemStingsTest() {
        val menuItemStrings = MenuItemStrings(2, 5)
        val menuItemStrings1 = MenuItemStrings(2, 5)

        assertThat(menuItemStrings, `is`(menuItemStrings1))
        assertThat(menuItemStrings.hashCode(), `is`(menuItemStrings1.hashCode()))
        assertThat(menuItemStrings.title, `is`(2))
        assertThat(menuItemStrings.titleCondensed, `is`(5))
    }
}
