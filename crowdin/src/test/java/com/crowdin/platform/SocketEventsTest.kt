package com.crowdin.platform

import com.crowdin.platform.realtimeupdate.SubscribeSuggestionEvent
import com.crowdin.platform.realtimeupdate.SubscribeUpdateEvent
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class SocketEventsTest {

    @Test
    fun updateEventTest() {
        val wsHash = "wsHash"
        val projectId = "projectId"
        val userId = "userId"
        val language = "EN"
        val mappingId = "mappingId"
        val expected = "{" +
                "\"action\":\"subscribe\", " +
                "\"event\": \"update-draft:$wsHash:$projectId:$userId:$language:$mappingId\"" +
                "}"

        val update = SubscribeUpdateEvent(wsHash, projectId, userId, language, mappingId)

        assertThat(update.toString(), `is`(expected))
    }

    @Test
    fun suggestionEventTest() {
        val wsHash = "wsHash"
        val projectId = "projectId"
        val language = "EN"
        val mappingId = "mappingId"
        val expected = "{" +
                "\"action\":\"subscribe\", " +
                "\"event\": \"top-suggestion:$wsHash:$projectId:$language:$mappingId\"" +
                "}"

        val update = SubscribeSuggestionEvent(wsHash, projectId, language, mappingId)

        assertThat(update.toString(), `is`(expected))
    }
}
