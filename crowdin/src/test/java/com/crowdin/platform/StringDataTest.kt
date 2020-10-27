package com.crowdin.platform

import com.crowdin.platform.data.model.StringData
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StringDataTest {

    @Test
    fun updateStringDataTest() {
        // Given
        val actualStringData = StringData(
            "key",
            "value",
            arrayOf("test arg"),
            StringBuilder("testBuilder")
        )
        val expectedStringData = StringData(
            "expectedKey",
            "expectedValue",
            arrayOf("expected arg"),
            StringBuilder("expected Builder")
        )

        // When
        actualStringData.updateResources(expectedStringData)

        // Then
        assertThat(actualStringData, `is`(expectedStringData))
    }

    @Test
    fun stringDataTest() {
        val builder = StringBuilder("testBuilder")
        val stringData1 = StringData(
            "key", "value",
            arrayOf("test arg"), builder
        )
        val stringData2 = StringData(
            "key", "value",
            arrayOf("test arg"), builder
        )

        assertThat(stringData1.hashCode(), `is`(stringData2.hashCode()))
    }
}
