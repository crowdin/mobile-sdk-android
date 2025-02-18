package com.crowdin.platform.data.model

internal data class TicketRequestBody(
    val event: String,
    val context: ContextData = ContextData(),
)

internal data class ContextData(
    val mode: String = "translate",
)
