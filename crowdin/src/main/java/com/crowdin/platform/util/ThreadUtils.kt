package com.crowdin.platform.util

import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.max

internal object ThreadUtils {

    var DEFAULT_BACKGROUND_PRIORITY = max(Thread.NORM_PRIORITY - 1, Thread.MIN_PRIORITY)
    private var NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    private var sLocalWorkPool: ExecutorService? = null

    private fun ensurePoolCreated() {
        if (sLocalWorkPool == null) {
            // Initial pool size (1/2 number of cores is preferred size
            // Maximum size is total cores + 1.  Added one here because availableProcessors might not return
            // the actual number of cores on teh device if some are asleep.  A minor variance of one gives us potentially
            // one more thread than cores, but also gives us an extra thread when we've created a thread pool that doesn't
            // represent the actual number of cores.
            sLocalWorkPool = ThreadPoolExecutor(
                    NUMBER_OF_CORES / 2,
                    NUMBER_OF_CORES + 1,
                    15L,
                    TimeUnit.SECONDS,
                    LinkedBlockingDeque<Runnable>(),
                    NamedThreadPoolFactory("LocalWorkThreadPool"))
        }
    }

    /**
     * Attempts to run the runnable on the current thread if not on the UI thread.  If called from the
     * UI thread, it will create a new thread.  Only use this if you need a new thread or you have a long
     * running operation that will block the thread for a significant amount of time.  If that's not your use case
     * use [ThreadUtils.runInBackgroundPool].
     * @param name The name of the thread to create.
     *
     * @return If a new thread is started, returns the new [Thread]. If ran in the current thread, returns null.
     */
    fun runInBackground(runnable: Runnable, name: String): Thread? {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            runnable.run()
            return null
        }

        val newThread = Thread(runnable)
        newThread.priority = DEFAULT_BACKGROUND_PRIORITY
        newThread.name = name
        newThread.start()
        return newThread
    }

    /**
     * Attempts to run the runnable in the current thread if not on the UI Thread.  If on the UI thread,
     * the work item will be queued up and ran on the first available thread in the thread pool.  If you have a
     * long running operation or your runnable blocks the thread for a significant amount of time, please use
     * [ThreadUtils.runInBackground].  Blocking the thread will hijack a thread from the pool and prevent future work from
     * being able to execute (ultimately waiting in the queue for a thread).
     */
    fun runInBackgroundPool(runnable: Runnable, allowInCurrentThread: Boolean) {
        ensurePoolCreated()
        if (allowInCurrentThread && Looper.myLooper() != Looper.getMainLooper()) {
            runnable.run()
        } else {
            sLocalWorkPool?.execute(runnable)
        }
    }
}