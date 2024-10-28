package com.crowdin.platform.realtimeupdate

internal const val UPDATE_DRAFT = "update-draft"
internal const val TOP_SUGGESTION = "top-suggestion"

internal class SubscribeUpdateEvent(
    private var wsHash: String,
    private var projectId: String,
    private var userId: String,
    private var language: String,
    private var mappingId: String
) {

    override fun toString(): String =
        "{" +
            "\"action\":\"subscribe\", " +
            "\"event\": \"$UPDATE_DRAFT:$wsHash:$projectId:$userId:$language:$mappingId\"" +
            "}"
}

internal class SubscribeSuggestionEvent(
    private var wsHash: String,
    private var projectId: String,
    private var language: String,
    private var mappingId: String
) {

    override fun toString(): String =
        "{" +
            "\"action\":\"subscribe\", " +
            "\"event\": \"$TOP_SUGGESTION:$wsHash:$projectId:$language:$mappingId\"" +
            "}"
}
