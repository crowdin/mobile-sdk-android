package com.crowdin.platform

import com.crowdin.platform.data.remote.DistributionFailureTracker
import com.crowdin.platform.data.remote.DistributionFailureTracker.Companion.FAILURES_BEFORE_FIRST_PAUSE
import com.crowdin.platform.data.remote.DistributionFailureTracker.Companion.FAILURE_WINDOW_MILLIS
import com.crowdin.platform.data.remote.DistributionFailureTracker.Companion.MAX_PAUSES
import com.crowdin.platform.data.remote.DistributionFailureTracker.Companion.PAUSE_DURATION_MILLIS
import okhttp3.Headers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class DistributionFailureTrackerTest {
    private lateinit var preferences: InMemoryPreferences
    private lateinit var tracker: DistributionFailureTracker
    private var now = 1_700_000_000_000L

    @Before
    fun setUp() {
        preferences = InMemoryPreferences()
        now = 1_700_000_000_000L
        tracker = givenFailureTracker(preferences = preferences, currentTimeMillis = { now })
    }

    @Test
    fun whenNothingRecorded_shouldAllowRequests() {
        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenFailuresBelowThreshold_shouldAllowRequests() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE - 1)

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenThresholdReached_shouldPauseRequests() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)

        assertThat(tracker.isRequestAllowed(), equalTo(false))
    }

    @Test
    fun whenPauseAlmostOver_shouldKeepRequestsPaused() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)

        now += PAUSE_DURATION_MILLIS - 1

        assertThat(tracker.isRequestAllowed(), equalTo(false))
    }

    @Test
    fun whenPauseExpired_shouldAllowRequests() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)

        now += PAUSE_DURATION_MILLIS

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenSingleFailureAfterPause_shouldPauseAgain() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)
        now += PAUSE_DURATION_MILLIS

        givenMissingDistribution(1)

        assertThat(tracker.isRequestAllowed(), equalTo(false))
    }

    @Test
    fun whenFailureAfterLastPause_shouldStopRequestsForGood() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)
        repeat(MAX_PAUSES - 1) {
            now += PAUSE_DURATION_MILLIS
            givenMissingDistribution(1)
        }
        now += PAUSE_DURATION_MILLIS

        givenMissingDistribution(1)

        assertThat(tracker.isRequestAllowed(), equalTo(false))
        // Unlike a pause, this one never lifts on its own.
        now += PAUSE_DURATION_MILLIS * 365
        assertThat(tracker.isRequestAllowed(), equalTo(false))
    }

    @Test
    fun whenDistributionAvailableAgain_shouldForgetFailures() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE - 1)

        tracker.onResponse(200, CLOUDFRONT_HEADERS)
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE - 1)

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenNotModified_shouldForgetFailures() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE - 1)

        tracker.onResponse(304, CLOUDFRONT_HEADERS)
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE - 1)

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenClientErrorHasNoCloudFrontSignature_shouldBeIgnored() {
        // A captive portal or a corporate proxy answering 403 for everything.
        repeat(FAILURES_BEFORE_FIRST_PAUSE * 2) {
            tracker.onResponse(403, PROXY_HEADERS)
            now += FAILURE_WINDOW_MILLIS
        }

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenServerError_shouldBeIgnored() {
        repeat(FAILURES_BEFORE_FIRST_PAUSE * 2) {
            tracker.onResponse(503, CLOUDFRONT_HEADERS)
            now += FAILURE_WINDOW_MILLIS
        }

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenThrottled_shouldBeIgnored() {
        repeat(FAILURES_BEFORE_FIRST_PAUSE * 2) {
            tracker.onResponse(429, CLOUDFRONT_HEADERS)
            now += FAILURE_WINDOW_MILLIS
        }

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenFailuresLandInSameWindow_shouldCountAsOneAttempt() {
        // Strings, mapping and translation repositories all ask for the manifest on one launch.
        repeat(FAILURES_BEFORE_FIRST_PAUSE * 3) {
            tracker.onResponse(403, CLOUDFRONT_HEADERS)
        }

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenDistributionHashChanged_shouldStartOver() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)

        val other = givenFailureTracker("anotherHash", preferences) { now }

        assertThat(other.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenApplicationUpdated_shouldStartOver() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)

        val afterUpdate = DistributionFailureTracker(preferences, "hash", APP_VERSION_CODE + 1) { now }

        assertThat(afterUpdate.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenReset_shouldAllowRequestsAgain() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)

        tracker.reset()

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    @Test
    fun whenDeviceClockMovedBackwards_shouldNotPauseForever() {
        givenMissingDistribution(FAILURES_BEFORE_FIRST_PAUSE)

        now -= PAUSE_DURATION_MILLIS * 2

        assertThat(tracker.isRequestAllowed(), equalTo(true))
    }

    private fun givenMissingDistribution(times: Int) {
        // Leaves the clock on the last failure, so a following pause starts from there.
        repeat(times) { index ->
            if (index > 0) {
                now += FAILURE_WINDOW_MILLIS
            }
            tracker.onResponse(403, CLOUDFRONT_HEADERS)
        }
    }

    private companion object {
        val CLOUDFRONT_HEADERS: Headers =
            Headers.headersOf(
                "server",
                "AmazonS3",
                "x-amz-cf-id",
                "sjzovv-IoPyttGQnlz4ZCDvvHztu223DSCEgyzCJyrQSgIjWOiINmQ==",
            )

        val PROXY_HEADERS: Headers = Headers.headersOf("content-type", "text/html")
    }
}
