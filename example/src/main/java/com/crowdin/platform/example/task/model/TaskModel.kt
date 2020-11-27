package com.crowdin.platform.example.task.model

data class TaskModel(
    var id: Int,
    var title: String,
    var task: String,
    var category: String,
    var date: String,
    var time: String
)
