package com.crowdin.platform.data.model

data class ListScreenshotsResponse(
    val data: List<ScreenshotData>,
    val pagination: Pagination,
)

data class ScreenshotData(
    val data: Screenshot,
)

// TODO: cleanup
data class Screenshot(
    val id: Long,
    val userId: Long,
//    val url: String,
//    val webUrl: String,
    val name: String,
//    val size: Any,
//    val tagsCount: Int,
//    val tags: List<Any>,
//    val labels: List<Any>,
//    val labelIds: List<Any>,
//    val createdAt: String?,
//    val updatedAt: String?
)

data class Pagination(
    val offset: Int,
    val limit: Int,
)

// {
//    "data": [
//        {
//            "data": {
//                "id": 2,
//                "userId": 6,
//                "url": "https://production-enterprise-screenshots.downloads.crowdin.com/992000002/6/2/middle.jpg?X-Amz-Content-Sha256={sha}&X-Amz-Algorithm={algorithm}&X-Amz-Credential={credentials}&X-Amz-Date={date}&X-Amz-SignedHeaders={headers}&X-Amz-Expires={expires}&X-Amz-Signature={signature}",
//                "webUrl": "https://production-enterprise-screenshots.downloads.crowdin.com/992000002/6/2/middle.jpg?X-Amz-Content-Sha256={sha}&X-Amz-Algorithm={algorithm}&X-Amz-Credential={credentials}&X-Amz-Date={date}&X-Amz-SignedHeaders={headers}&X-Amz-Expires={expires}&X-Amz-Signature={signature}",
//                "name": "translate_with_siri.jpg",
//                "size": {
//                    "width": 267,
//                    "height": 176
//                },
//                "tagsCount": 1,
//                "tags": [
//                    {
//                        "id": 98,
//                        "screenshotId": 2,
//                        "stringId": 2822,
//                        "position": {
//                            "x": 474,
//                            "y": 147,
//                            "width": 490,
//                            "height": 99
//                        },
//                        "createdAt": "2019-09-23T09:35:31+00:00"
//                    }
//                ],
//                "labels": [
//                    1
//                ],
//                "labelIds": [
//                    1
//                ],
//                "createdAt": "2019-09-23T09:29:19+00:00",
//                "updatedAt": "2019-09-23T09:29:19+00:00"
//            }
//        }
//    ],
//    "pagination": {
//        "offset": 0,
//        "limit": 25
//    }
// }
