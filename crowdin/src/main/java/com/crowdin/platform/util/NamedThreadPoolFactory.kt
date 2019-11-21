package com.crowdin.platform.util

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

internal class NamedThreadPoolFactory(private val threadPrefix: String) : ThreadFactory {

    private val defaultFactory = Executors.defaultThreadFactory()
    private val counter = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        val result = defaultFactory.newThread(r)
        result.name = String.format("%s-%s", threadPrefix, counter.getAndIncrement().toString())
        result.priority = ThreadUtils.DEFAULT_BACKGROUND_PRIORITY
        return result
    }
}
