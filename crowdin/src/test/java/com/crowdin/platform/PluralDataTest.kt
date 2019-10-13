package com.crowdin.platform

import com.crowdin.platform.data.model.PluralData
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class PluralDataTest {

    @Test
    fun updatePluralDataTest() {
        // Given
        val actualPluralData = PluralData(
                "actualKey",
                mutableMapOf(Pair("key0", "value0")),
                0,
                arrayOf("test0"))
        val expectedPluralData = PluralData(
                "expectedKey",
                mutableMapOf(Pair("key1", "value1")),
                1,
                arrayOf("test1"))

        // When
        actualPluralData.updateResources(expectedPluralData)

        // Then
        assertThat(actualPluralData, `is`(expectedPluralData))
    }
}