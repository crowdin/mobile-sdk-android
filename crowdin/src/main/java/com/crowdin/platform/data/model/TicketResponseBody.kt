package com.crowdin.platform.data.model

internal data class TicketResponseBody(
    val data: TicketData,
)

internal data class TicketData(
    val ticket: String,
)
