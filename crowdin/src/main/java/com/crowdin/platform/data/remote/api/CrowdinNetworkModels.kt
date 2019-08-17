package com.crowdin.platform.data.remote.api

import com.google.gson.annotations.SerializedName

internal data class CreateScreenshotRequestBody(var storageId: Int, var name: String)

internal data class CreateScreenshotResponse(var data: Data)

internal data class TagData(var stringId: Int,
                            var position: Position) {

    internal data class Position(var x: Int,
                                 var y: Int,
                                 var width: Int,
                                 var height: Int)
}

internal data class UploadScreenshotResponse(var data: Data? = null)

internal data class Data(var id: Int? = null)

internal data class DistributionInfoResponse(var data: DistributionData) {

    internal data class DistributionData(var project: ProjectData,
                                         var user: UserData) {

        internal data class ProjectData(var id: String,
                                        var wsHash: String)

        internal data class UserData(var id: String)
    }
}

internal data class EventResponse(var event: String,
                                  var data: EventData) {

    internal data class EventData(var id: String,
                                  @SerializedName("user_id")
                                  var userId: String,
                                  var time: String,
                                  var attributes: Any,
                                  var text: String,
                                  var pluralForm: String?,
                                  @SerializedName("words_count")
                                  var wordsCount: String,
                                  @SerializedName("step_id")
                                  var stepId: String,
                                  var approved: String,
                                  @SerializedName("validation_id")
                                  var validationId: String)
}