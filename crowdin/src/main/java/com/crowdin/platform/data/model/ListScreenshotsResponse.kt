package com.crowdin.platform.data.model

data class ListScreenshotsResponse(
    val data: List<ScreenshotData>,
    val pagination: Pagination,
)

data class ScreenshotData(
    val data: Screenshot,
)

data class Screenshot(
    val id: Long,
    val userId: Long,
    val name: String,
)

data class Pagination(
    val offset: Int,
    val limit: Int,
)
