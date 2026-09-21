package com.crowdin.platform.data.remote

import android.util.Log
import com.crowdin.platform.Crowdin
import com.crowdin.platform.Preferences
import com.crowdin.platform.data.model.DistributionFailureState
import com.crowdin.platform.data.remote.DistributionFailureTracker.Companion.FAILURES_BEFORE_FIRST_PAUSE
import com.crowdin.platform.data.remote.DistributionFailureTracker.Companion.MAX_PAUSES
import okhttp3.Headers
import java.net.HttpURLConnection

/**
 * Stops the SDK from polling a distribution that no longer exists.
 *
 * A deleted distribution keeps answering 4xx forever, while old builds of an application stay
 * in use for months. Every one of those requests is billed, so after enough failures the SDK
 * backs off:
 *
 * 1. [FAILURES_BEFORE_FIRST_PAUSE] failed responses pause all requests for a day. Every
 *    failed response counts, including the several a single launch makes: each one is billed,
 *    so each one is spent from the same allowance.
 * 2. A single failure after a pause has expired arms the next one - the distribution already
 *    proved itself missing, there is no reason to spend another ten requests on it.
 * 3. After [MAX_PAUSES] pauses the SDK stops requesting the distribution for good.
 *
 * Any successful response clears the state, as does a new distribution hash or a new build of
 * the application.
 */
internal class DistributionFailureTracker(
    private val preferences: Preferences,
    private val distributionHash: String,
    private val appVersion: Long,
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() },
) {
    /**
     * `true` when a request to the distribution may be sent.
     */
    @Synchronized
    fun isRequestAllowed(): Boolean {
        val state = readState()
        if (state.disabled) {
            Log.w(
                Crowdin.CROWDIN_TAG,
                "Distribution \"$distributionHash\" kept responding as missing, so the SDK stopped " +
                    "requesting it. Translations will not be updated. Check the distribution in " +
                    "Crowdin and call Crowdin.resetDistributionFailureState() once it is restored.",
            )
            return false
        }

        val remaining = state.pausedUntil - currentTimeMillis()
        if (remaining <= 0) {
            return true
        }

        // The pause can never legitimately outlast its own duration. A longer one means the
        // device clock moved backwards, and without this the pause would outlive the device.
        if (remaining > PAUSE_DURATION_MILLIS) {
            Log.w(
                Crowdin.CROWDIN_TAG,
                "Distribution pause outlasts its duration, the device clock has changed. Resuming requests.",
            )
            writeState(state.copy(pausedUntil = 0L))
            return true
        }

        Log.w(
            Crowdin.CROWDIN_TAG,
            "Distribution \"$distributionHash\" responded as missing. Requests are paused for " +
                "${remaining / MILLIS_IN_MINUTE} more minute(s), pause ${state.pauseCount} of $MAX_PAUSES.",
        )
        return false
    }

    /**
     * Feed a distribution response back into the tracker. Transport failures are deliberately
     * not reported here: an offline device says nothing about the distribution.
     */
    @Synchronized
    fun onResponse(
        code: Int,
        headers: Headers,
    ) {
        when {
            isSuccessful(code) -> onDistributionAvailable()
            isDistributionMissing(code, headers) -> onDistributionMissing()
            else -> Unit
        }
    }

    /**
     * Forget every recorded failure and resume requests.
     */
    @Synchronized
    fun reset() {
        writeState(DistributionFailureState(distributionHash, appVersion))
    }

    private fun onDistributionAvailable() {
        val state = readState()
        if (state == DistributionFailureState(distributionHash, appVersion)) {
            // Nothing recorded - avoid a storage write on every successful update.
            return
        }
        reset()
    }

    private fun onDistributionMissing() {
        val state = readState()
        val now = currentTimeMillis()
        if (state.disabled || state.pausedUntil > now) {
            return
        }

        val updated =
            when {
                state.pauseCount == 0 -> {
                    val failureCount = state.failureCount + 1
                    if (failureCount < FAILURES_BEFORE_FIRST_PAUSE) {
                        state.copy(failureCount = failureCount)
                    } else {
                        state.copy(
                            failureCount = 0,
                            pauseCount = 1,
                            pausedUntil = now + PAUSE_DURATION_MILLIS,
                        )
                    }
                }

                state.pauseCount >= MAX_PAUSES -> {
                    state.copy(disabled = true)
                }

                else -> {
                    state.copy(
                        pauseCount = state.pauseCount + 1,
                        pausedUntil = now + PAUSE_DURATION_MILLIS,
                    )
                }
            }

        writeState(updated)

        when {
            updated.disabled -> {
                Log.w(
                    Crowdin.CROWDIN_TAG,
                    "Distribution \"$distributionHash\" responded as missing for $MAX_PAUSES days in a row. " +
                        "The SDK will not request it again from this build.",
                )
            }

            updated.pauseCount > state.pauseCount -> {
                Log.w(
                    Crowdin.CROWDIN_TAG,
                    "Distribution \"$distributionHash\" responded as missing. Requests are paused for a day " +
                        "(pause ${updated.pauseCount} of $MAX_PAUSES).",
                )
            }

            else -> {
                Log.w(
                    Crowdin.CROWDIN_TAG,
                    "Distribution \"$distributionHash\" responded as missing " +
                        "(${updated.failureCount} of $FAILURES_BEFORE_FIRST_PAUSE attempts).",
                )
            }
        }
    }

    private fun isSuccessful(code: Int): Boolean =
        code in HttpURLConnection.HTTP_OK until HttpURLConnection.HTTP_MULT_CHOICE ||
            code == HttpURLConnection.HTTP_NOT_MODIFIED

    private fun isDistributionMissing(
        code: Int,
        headers: Headers,
    ): Boolean {
        if (code !in CLIENT_ERROR_RANGE || code in TRANSIENT_CLIENT_ERRORS) {
            return false
        }

        // A deleted distribution answers with an S3 `AccessDenied` relayed by CloudFront. A 4xx
        // without the CloudFront signature comes from something in between - a captive portal or
        // a corporate proxy - and must not be held against the distribution.
        return headers[CLOUDFRONT_REQUEST_ID_HEADER] != null
    }

    private fun readState(): DistributionFailureState {
        val stored =
            preferences.getData<DistributionFailureState>(
                DISTRIBUTION_FAILURE_STATE,
                DistributionFailureState::class.java,
            )

        return if (stored == null ||
            stored.distributionHash != distributionHash ||
            stored.appVersion != appVersion
        ) {
            DistributionFailureState(distributionHash, appVersion)
        } else {
            stored
        }
    }

    private fun writeState(state: DistributionFailureState) {
        preferences.saveData(DISTRIBUTION_FAILURE_STATE, state)
    }

    internal companion object {
        const val DISTRIBUTION_FAILURE_STATE = "distribution_failure_state"

        /** Failed attempts tolerated before the first pause. */
        const val FAILURES_BEFORE_FIRST_PAUSE = 10

        /** Pauses served before the SDK gives up on the distribution entirely. */
        const val MAX_PAUSES = 3

        const val PAUSE_DURATION_MILLIS = 24 * 60 * 60 * 1000L

        private const val MILLIS_IN_MINUTE = 60 * 1000L
        private const val CLOUDFRONT_REQUEST_ID_HEADER = "x-amz-cf-id"
        private val CLIENT_ERROR_RANGE = 400..499

        /** Retryable 4xx: timeout, too early, throttling. */
        private val TRANSIENT_CLIENT_ERRORS = setOf(408, 425, 429)
    }
}
