package com.crowdin.platform.compose

import android.content.res.Resources
import android.os.Looper
import com.crowdin.platform.CrowdinResources
import com.crowdin.platform.data.model.TextMetaData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.ArgumentCaptor
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ComposeStringRepositoryTest {

    private lateinit var crowdinResources: CrowdinResources
    private lateinit var repository: ComposeStringRepository
    private lateinit var context: android.content.Context
    private lateinit var resources: Resources

    @Before
    fun setUp() {
        context = mock(android.content.Context::class.java)
        resources = mock(Resources::class.java)
        crowdinResources = mock(CrowdinResources::class.java)
        `when`(context.resources).thenReturn(resources)
        repository = ComposeStringRepository(context, crowdinResources)
    }

    @Test
    fun getString_shouldReturnResourceValue() {
        // Given
        val id = 123
        val expectedValue = "Hello World"
        `when`(crowdinResources.getString(id)).thenReturn(expectedValue)

        // When
        val state = repository.getStringState(id)

        // Then
        assertEquals(expectedValue, state.value)
    }

    @Test
    fun onDataChanged_shouldUpdateSubscribers() {
        // Given
        val id = 123
        val firstValue = "Hello"
        val secondValue = "Hola"
        
        `when`(crowdinResources.getString(id)).thenReturn(firstValue)
        val state = repository.getStringState(id)
        repository.registerWatcher(id)

        assertEquals(firstValue, state.value)

        // When
        `when`(crowdinResources.getString(id)).thenReturn(secondValue)
        repository.forceUpdate()

        // Then
        assertEquals(secondValue, state.value)
    }

    @Test
    fun webSocketCallback_shouldBeInvokedOnWatcherRegistration() {
        // Given
        val id = 123
        val resourceKey = "test_string"
        val defaultValue = "Test"
        var callbackInvoked = false
        var capturedMetaData: TextMetaData? = null

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)
        `when`(resources.getResourceEntryName(id)).thenReturn(resourceKey)
        `when`(resources.getString(id)).thenReturn(defaultValue)

        repository.setWebSocketCallbacks(
            onWatcherRegistered = { metaData ->
                callbackInvoked = true
                capturedMetaData = metaData
            },
            onWatcherDeregistered = null
        )

        // When
        repository.getStringState(id)
        repository.registerWatcher(id)

        // Then
        assertTrue(callbackInvoked)
        assertNotNull(capturedMetaData)
        assertEquals(resourceKey, capturedMetaData?.textAttributeKey)
        assertEquals(defaultValue, capturedMetaData?.stringDefault)
        assertEquals(id, capturedMetaData?.resourceId)
    }

    @Test
    fun webSocketCallback_shouldBeInvokedOnWatcherDeregistration() {
        // Given
        val id = 123
        val resourceKey = "test_string"
        val defaultValue = "Test"
        var deregisterCallbackInvoked = false
        var capturedMetaData: TextMetaData? = null

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)
        `when`(resources.getResourceEntryName(id)).thenReturn(resourceKey)
        `when`(resources.getString(id)).thenReturn(defaultValue)

        repository.setWebSocketCallbacks(
            onWatcherRegistered = { },
            onWatcherDeregistered = { metaData ->
                deregisterCallbackInvoked = true
                capturedMetaData = metaData
            }
        )

        // When
        repository.getStringState(id)
        repository.registerWatcher(id)
        repository.deRegisterWatcher(id)

        // Then
        assertTrue(deregisterCallbackInvoked)
        assertNotNull(capturedMetaData)
        assertEquals(resourceKey, capturedMetaData?.textAttributeKey)
    }

    @Test
    fun webSocketCallback_shouldNotBeInvokedWhenNotSet() {
        // Given
        val id = 123
        val defaultValue = "Test"

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)
        `when`(resources.getResourceEntryName(id)).thenReturn("test_string")
        `when`(resources.getString(id)).thenReturn(defaultValue)

        // When - no callbacks set
        repository.getStringState(id)
        repository.registerWatcher(id)
        repository.deRegisterWatcher(id)

        // Then - no exception should be thrown
        assertTrue(true) // Test passed if no exception
    }

    @Test
    fun multipleWatchers_shouldIncrementWatcherCount() {
        // Given
        val id = 123
        val defaultValue = "Test"
        val registerCount = AtomicInteger(0)

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)
        `when`(resources.getResourceEntryName(id)).thenReturn("test_string")
        `when`(resources.getString(id)).thenReturn(defaultValue)

        repository.setWebSocketCallbacks(
            onWatcherRegistered = { registerCount.incrementAndGet() },
            onWatcherDeregistered = null
        )

        // When
        repository.getStringState(id)
        repository.registerWatcher(id)
        repository.registerWatcher(id)
        repository.registerWatcher(id)

        // Then - callback should only be invoked once for first watcher
        assertEquals(1, registerCount.get())
    }

    @Test
    fun multipleWatchers_shouldOnlyDeregisterWhenAllWatchersRemoved() {
        // Given
        val id = 123
        val defaultValue = "Test"
        val deregisterCount = AtomicInteger(0)

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)
        `when`(resources.getResourceEntryName(id)).thenReturn("test_string")
        `when`(resources.getString(id)).thenReturn(defaultValue)

        repository.setWebSocketCallbacks(
            onWatcherRegistered = { },
            onWatcherDeregistered = { deregisterCount.incrementAndGet() }
        )

        // When
        repository.getStringState(id)
        repository.registerWatcher(id)
        repository.registerWatcher(id)
        repository.registerWatcher(id)

        // Deregister twice - should not invoke callback yet
        repository.deRegisterWatcher(id)
        repository.deRegisterWatcher(id)
        assertEquals(0, deregisterCount.get())

        // Deregister third time - should invoke callback now
        repository.deRegisterWatcher(id)

        // Then
        assertEquals(1, deregisterCount.get())
    }

    @Test
    fun multipleWatchers_shouldKeepStateUntilAllDeregistered() {
        // Given
        val id = 123
        val defaultValue = "Test"

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)
        `when`(resources.getResourceEntryName(id)).thenReturn("test_string")
        `when`(resources.getString(id)).thenReturn(defaultValue)

        // When
        val state = repository.getStringState(id)
        repository.registerWatcher(id)
        repository.registerWatcher(id)
        repository.registerWatcher(id)

        // Deregister twice
        repository.deRegisterWatcher(id)
        repository.deRegisterWatcher(id)

        // State should still be accessible
        assertEquals(defaultValue, state.value)

        // Deregister third time
        repository.deRegisterWatcher(id)

        // State should still hold the last value
        assertEquals(defaultValue, state.value)
    }

    @Test
    fun deregisterNonExistentWatcher_shouldNotThrowException() {
        // Given
        val id = 999

        // When - deregister without registering
        repository.deRegisterWatcher(id)

        // Then - should not throw exception
        assertTrue(true)
    }

    @Test
    fun deregisterMoreThanRegistered_shouldNotThrowException() {
        // Given
        val id = 123
        val defaultValue = "Test"

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)
        `when`(resources.getResourceEntryName(id)).thenReturn("test_string")
        `when`(resources.getString(id)).thenReturn(defaultValue)

        // When
        repository.getStringState(id)
        repository.registerWatcher(id)
        repository.deRegisterWatcher(id)

        // Deregister again - should not throw
        repository.deRegisterWatcher(id)
        repository.deRegisterWatcher(id)

        // Then
        assertTrue(true)
    }

    @Test
    fun registerWatcherBeforeGetStringState_shouldHandleGracefully() {
        // Given
        val id = 123
        val defaultValue = "Test"

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)

        // When - register before getting state
        repository.registerWatcher(id)

        // Then - should not throw exception
        assertTrue(true)
    }

    @Test
    fun concurrentAccess_shouldBeSafeForMultipleThreads() {
        // Given
        val resourceCount = 10
        val threadsPerResource = 5
        val latch = CountDownLatch(resourceCount * threadsPerResource)
        val executor = Executors.newFixedThreadPool(10)
        val exceptions = mutableListOf<Exception>()

        for (id in 1..resourceCount) {
            `when`(crowdinResources.getString(id)).thenReturn("Value $id")
            `when`(resources.getResourceEntryName(id)).thenReturn("string_$id")
            `when`(resources.getString(id)).thenReturn("Value $id")
        }

        // When - concurrent access from multiple threads
        for (id in 1..resourceCount) {
            for (thread in 1..threadsPerResource) {
                executor.submit {
                    try {
                        repository.getStringState(id)
                        repository.registerWatcher(id)
                        Thread.sleep(10)
                        repository.deRegisterWatcher(id)
                    } catch (e: Exception) {
                        synchronized(exceptions) {
                            exceptions.add(e)
                        }
                    } finally {
                        latch.countDown()
                    }
                }
            }
        }

        // Wait for all threads to complete
        latch.await(5, TimeUnit.SECONDS)
        executor.shutdown()

        // Then - no exceptions should occur
        assertTrue("Expected no exceptions, but got: $exceptions", exceptions.isEmpty())
    }

    @Test
    fun concurrentSameResource_shouldBeSafe() {
        // Given
        val id = 123
        val threadCount = 20
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val exceptions = mutableListOf<Exception>()

        `when`(crowdinResources.getString(id)).thenReturn("Test")
        `when`(resources.getResourceEntryName(id)).thenReturn("test_string")
        `when`(resources.getString(id)).thenReturn("Test")

        // When - multiple threads accessing same resource
        repeat(threadCount) {
            executor.submit {
                try {
                    repository.getStringState(id)
                    repository.registerWatcher(id)
                    Thread.sleep(5)
                    repository.deRegisterWatcher(id)
                } catch (e: Exception) {
                    synchronized(exceptions) {
                        exceptions.add(e)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        // Wait for all threads to complete
        latch.await(5, TimeUnit.SECONDS)
        executor.shutdown()

        // Then
        assertTrue("Expected no exceptions, but got: $exceptions", exceptions.isEmpty())
    }

    @Test
    fun getActiveWatchers_shouldReturnRegisteredWatchers() {
        // Given
        val id1 = 123
        val id2 = 456

        `when`(crowdinResources.getString(id1)).thenReturn("Test1")
        `when`(crowdinResources.getString(id2)).thenReturn("Test2")
        `when`(resources.getResourceEntryName(id1)).thenReturn("test_string_1")
        `when`(resources.getResourceEntryName(id2)).thenReturn("test_string_2")
        `when`(resources.getString(id1)).thenReturn("Test1")
        `when`(resources.getString(id2)).thenReturn("Test2")

        // When
        repository.getStringState(id1)
        repository.getStringState(id2)
        repository.registerWatcher(id1)
        repository.registerWatcher(id2)

        // Then
        val activeWatchers = repository.getActiveWatchers()
        assertEquals(2, activeWatchers.size)
    }

    @Test
    fun getActiveWatchers_shouldBeEmptyAfterDeregistration() {
        // Given
        val id = 123

        `when`(crowdinResources.getString(id)).thenReturn("Test")
        `when`(resources.getResourceEntryName(id)).thenReturn("test_string")
        `when`(resources.getString(id)).thenReturn("Test")

        // When
        repository.getStringState(id)
        repository.registerWatcher(id)
        repository.deRegisterWatcher(id)

        // Then
        val activeWatchers = repository.getActiveWatchers()
        assertTrue(activeWatchers.isEmpty())
    }

    @Test
    fun cleanup_shouldRemoveStateWhenAllWatchersDeregistered() {
        // Given
        val id = 123
        val defaultValue = "Test"
        val deregisterCount = AtomicInteger(0)

        `when`(crowdinResources.getString(id)).thenReturn(defaultValue)
        `when`(resources.getResourceEntryName(id)).thenReturn("test_string")
        `when`(resources.getString(id)).thenReturn(defaultValue)

        repository.setWebSocketCallbacks(
            onWatcherRegistered = { },
            onWatcherDeregistered = { deregisterCount.incrementAndGet() }
        )

        // When
        repository.getStringState(id)
        repository.registerWatcher(id)
        repository.deRegisterWatcher(id)

        // Then
        assertEquals(1, deregisterCount.get())
        assertTrue(repository.getActiveWatchers().isEmpty())
    }
}
