package com.crowdin.platform.repository.model

internal data class ViewData(val resourceKey: String,
                             val topX: Int,
                             val topY: Int,
                             val bottomX: Int,
                             val bottomY: Int)