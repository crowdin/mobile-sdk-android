package com.crowdin.platform

import com.crowdin.platform.util.NamedThreadPoolFactory
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class NamedThreadPoolFactoryTest {

    @Test
    fun newThreadTest() {
        val prefix = "test"
        val factory = NamedThreadPoolFactory(prefix)
        val runnable = Runnable { }

        val thread = factory.newThread(runnable)

        assertThat(thread.name, `is`("test-0"))
        assertThat(thread.priority, `is`(4))
    }
}