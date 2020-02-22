package com.crowdin.platform.data.model

data class FileResponse(
    val data: List<FileData>
)

data class FileData(
    val data: File
)

data class File(
    val id: Long,
    val projectId: Long,
    val name: String,
    val title: String
)
