package com.crowdin.platform

import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.parser.StringResourceParser
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class StringResourceParserTest {

    @Test
    fun getLanguageData_getDefaultLanguageTest() {
        // Given
        val stringResourceParser = StringResourceParser()

        // When
        val result = stringResourceParser.getLanguageData()

        // Then
        MatcherAssert.assertThat(result.language, CoreMatchers.`is`(""))
        MatcherAssert.assertThat(result.resources, CoreMatchers.`is`(emptyList<StringData>()))
        MatcherAssert.assertThat(result.arrays, CoreMatchers.`is`(emptyList<ArrayData>()))
        MatcherAssert.assertThat(result.plurals, CoreMatchers.`is`(emptyList<PluralData>()))
    }
}