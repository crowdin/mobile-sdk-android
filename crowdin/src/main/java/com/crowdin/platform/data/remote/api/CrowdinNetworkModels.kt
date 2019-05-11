package com.crowdin.platform.data.remote.api

data class CreateScreenshotRequestBody(var storageId: Int, var name: String)

data class CreateScreenshotResponse(var data: Data)

data class TagData(var stringId: Int,
                   var position: Position)

data class Position(var x: Int,
                    var y: Int,
                    var width: Int,
                    var height: Int)

data class UploadScreenshotResponse(var data: Data? = null)

data class Data(var id: Int? = null)