package com.crowdin.platform.data.model

internal data class FileResponse(
    val data: List<FileData>
)

internal data class FileData(
    val data: File
)

internal data class File(
    val id: Long,
    val projectId: Long,
    val name: String,
    val title: String,
    val path: String
)
