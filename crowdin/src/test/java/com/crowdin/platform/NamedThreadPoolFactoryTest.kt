package com.crowdin.platform

import com.crowdin.platform.util.NamedThreadPoolFactory
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class NamedThreadPoolFactoryTest {

    @Test
    fun newThreadTest() {
        // Given
        val prefix = "test"
        val factory = NamedThreadPoolFactory(prefix)
        val runnable = Runnable { }

        // When
        val thread = factory.newThread(runnable)

        // Then
        assertThat(thread.name, `is`("test-0"))
        assertThat(thread.priority, `is`(4))
    }
}