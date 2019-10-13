package com.crowdin.platform

import com.crowdin.platform.data.model.StringData
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class StringDataTest {

    @Test
    fun updateStringDataTest() {
        // Given
        val actualStringData = StringData(
                "key",
                "value",
                arrayOf("test arg"),
                StringBuilder("testBuilder"))
        val expectedStringData = StringData(
                "expectedKey",
                "expectedValue",
                arrayOf("expected arg"),
                StringBuilder("expected Builder"))

        // When
        actualStringData.updateResources(expectedStringData)

        // Then
        assertThat(actualStringData, `is`(expectedStringData))
    }
}