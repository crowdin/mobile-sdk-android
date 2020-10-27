package com.crowdin.platform

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

/**
 * Helper functions that are workarounds to kotlin Runtime Exceptions when using kotlin.
 */

/**
 * Returns Mockito.eq() as nullable type to avoid java.lang.IllegalStateException when
 * null is returned.
 *
 * Generic T is nullable because implicitly bounded by Any?.
 */
fun <T> eq(obj: T): T = Mockito.eq(obj)

/**
 * Returns Mockito.any() as nullable type to avoid java.lang.IllegalStateException when
 * null is returned.
 */
fun <T> any(): T = Mockito.any()

/**
 * Returns ArgumentCaptor.capture() as nullable type to avoid java.lang.IllegalStateException
 * when null is returned.
 */
fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()

/**
 * Helper function for creating an argumentCaptor in kotlin.
 */
inline fun <reified T : Any> argumentCaptor(): ArgumentCaptor<T> =
    ArgumentCaptor.forClass(T::class.java)
