package com.crowdin.platform.realtimeupdate

internal const val UPDATE_DRAFT = "update-draft"
internal const val TOP_SUGGESTION = "top-suggestion"

internal class SubscribeUpdateEvent(var wsHash: String,
                                    var projectId: String,
                                    var userId: String,
                                    var language: String,
                                    var mappingId: String) {

    override fun toString(): String {
        return "{" +
                "\"action\":\"subscribe\", " +
                "\"event\": \"$UPDATE_DRAFT:$wsHash:$projectId:$userId:$language:$mappingId\"" +
                "}"

    }
}

internal class SubscribeSuggestionEvent(var wsHash: String,
                                        var projectId: String,
                                        var language: String,
                                        var mappingId: String) {

    override fun toString(): String {
        return "{" +
                "\"action\":\"subscribe\", " +
                "\"event\": \"$TOP_SUGGESTION:$wsHash:$projectId:$language:$mappingId\"" +
                "}"

    }
}