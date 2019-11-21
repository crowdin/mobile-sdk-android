package com.crowdin.platform

import android.content.res.Resources
import android.util.AttributeSet
import com.crowdin.platform.util.TextUtils
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class TextUtilsTest {

    private lateinit var mockAttributeSet: AttributeSet
    private lateinit var mockResources: Resources

    @Before
    fun setUp() {
        mockAttributeSet = mock(AttributeSet::class.java)
        mockResources = mock(Resources::class.java)
    }

    @Test
    fun getTextForAttribute_whenAttributeValueNull_shouldReturnNull() {
        // Given
        val index = 0
        val expected: CharSequence? = null

        // When
        val actual = TextUtils.getTextForAttribute(
            mockAttributeSet,
            index,
            mockResources
        )

        // Then
        assertThat(actual, `is`(expected))
    }

    @Test
    fun getTextForAttribute_whenAttributeValueNotText_shouldReturnNull() {
        // Given
        val index = 0
        val attributeValue = "testValue"
        `when`(mockAttributeSet.getAttributeValue(index)).thenReturn(attributeValue)
        val expected: CharSequence? = null

        // When
        val actual = TextUtils.getTextForAttribute(
            mockAttributeSet,
            index,
            mockResources
        )

        // Then
        assertThat(actual, `is`(expected))
    }

    @Test
    fun getTextForAttribute_whenAttributeResourceValueNotFound_shouldReturnNull() {
        // Given
        val index = 0
        val attributeValue = "@testValue"
        `when`(mockAttributeSet.getAttributeValue(index)).thenReturn(attributeValue)
        `when`(
            mockAttributeSet.getAttributeResourceValue(
                index,
                0
            )
        ).thenThrow(Resources.NotFoundException())
        val expected: CharSequence? = null

        // When
        val actual = TextUtils.getTextForAttribute(
            mockAttributeSet,
            index,
            mockResources
        )

        // Then
        assertThat(actual, `is`(expected))
    }

    @Test
    fun getTextForAttribute_whenAttributeResourceValueFound_shouldReturnText() {
        // Given
        val expectedText: CharSequence = "expectedText"
        val index = 0
        val attributeValue = "@testValue"
        `when`(mockAttributeSet.getAttributeValue(index)).thenReturn(attributeValue)
        `when`(mockAttributeSet.getAttributeResourceValue(index, 0)).thenReturn(0)
        `when`(mockResources.getText(0)).thenReturn(expectedText)

        // When
        val actual = TextUtils.getTextForAttribute(
            mockAttributeSet,
            index,
            mockResources
        )

        // Then
        assertThat(actual, `is`(expectedText))
    }

    @Test
    fun getTextAttributeKey_whenAttributeValueNull_shouldReturnNull() {
        // Given
        val index = 0
        val expected: CharSequence? = null

        // When
        val actual = TextUtils.getTextAttributeKey(
            mockResources,
            mockAttributeSet,
            index
        )

        // Then
        assertThat(actual, `is`(expected))
    }

    @Test
    fun getTextAttributeKey_whenAttributeValueNotText_shouldReturnNull() {
        // Given
        val index = 0
        val attributeValue = "testValue"
        `when`(mockAttributeSet.getAttributeValue(index)).thenReturn(attributeValue)
        val expected: CharSequence? = null

        // When
        val actual = TextUtils.getTextAttributeKey(
            mockResources,
            mockAttributeSet,
            index
        )

        // Then
        assertThat(actual, `is`(expected))
    }

    @Test
    fun getTextAttributeKey_whenAttributeResourceValueNotFound_shouldReturnNull() {
        // Given
        val index = 0
        val attributeValue = "@testValue"
        `when`(mockAttributeSet.getAttributeValue(index)).thenReturn(attributeValue)
        `when`(
            mockAttributeSet.getAttributeResourceValue(
                index,
                0
            )
        ).thenThrow(Resources.NotFoundException())
        val expected: CharSequence? = null

        // When
        val actual = TextUtils.getTextAttributeKey(
            mockResources,
            mockAttributeSet,
            index
        )

        // Then
        assertThat(actual, `is`(expected))
    }

    @Test
    fun getTextAttributeKey_whenAttributeResourceValueFound_shouldReturnText() {
        // Given
        val expectedText = "expectedText"
        val index = 0
        val attributeValue = "@testValue"
        `when`(mockAttributeSet.getAttributeValue(index)).thenReturn(attributeValue)
        `when`(mockAttributeSet.getAttributeResourceValue(index, 0)).thenReturn(0)
        `when`(mockResources.getResourceEntryName(0)).thenReturn(expectedText)

        // When
        val actual = TextUtils.getTextAttributeKey(
            mockResources,
            mockAttributeSet,
            index
        )

        // Then
        assertThat(actual, `is`(expectedText))
    }
}
