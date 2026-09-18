package com.crowdin.platform.data.model

/**
 * Persisted state of [com.crowdin.platform.data.remote.DistributionFailureTracker].
 *
 * Bound to a distribution hash and an app build: a different hash or a new build of the
 * application starts from scratch, because the previous verdict says nothing about them.
 */
internal data class DistributionFailureState(
    val distributionHash: String = "",
    val appVersion: Long = 0L,
    val failureCount: Int = 0,
    val pauseCount: Int = 0,
    val pausedUntil: Long = 0L,
    val lastFailureAt: Long = 0L,
    val disabled: Boolean = false,
)
