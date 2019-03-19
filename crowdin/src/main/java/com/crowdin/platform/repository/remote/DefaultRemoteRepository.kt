package com.crowdin.platform.repository.remote

import android.util.Log
import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.remote.api.CrowdinApi
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executors


internal class DefaultRemoteRepository(private val crowdinApi: CrowdinApi) : RemoteRepository {

    override fun fetchData(distributionKey: String?, currentLocale: String, languageDataCallback: LanguageDataCallback) {
        Executors.newSingleThreadExecutor().submit {
            crowdinApi.getFileUpdates(distributionKey, currentLocale, "strings.xml")
                    .enqueue(object : Callback<ResponseBody> {

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.code() == 200) {
                                val pullParserFactory: XmlPullParserFactory
                                try {
                                    pullParserFactory = XmlPullParserFactory.newInstance()
                                    val parser = pullParserFactory.newPullParser()

                                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                                    parser.setInput(response.body()?.byteStream(), null)

                                    if (parser != null) {
                                        val resources = parseXML(parser)
                                        Log.d(TAG, "resources: $resources")
                                        // TODO: handle response with proper storing in local repo
                                    }

                                } catch (e: XmlPullParserException) {
                                    e.printStackTrace()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                            Log.d(TAG, "error: ${throwable.localizedMessage}")
                        }
                    })
        }
    }

    private fun parseXML(parser: XmlPullParser): MutableMap<String, String> {
        val resources: MutableMap<String, String> = mutableMapOf()
        var eventType = parser.eventType
        var wasStringStart = false
        var key: String? = null
        var value: String? = null

        // TODO: add support for arrays/plurals
        while (eventType != XmlPullParser.END_DOCUMENT) {
            var name: String?
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    if (name == TAG_STRING || wasStringStart) {
                        val attrCount = parser.attributeCount
                        if (attrCount > 0) {
                            for (item: Int in 0 until parser.attributeCount) {
                                if (wasStringStart) {
                                    value += "<${parser.name}>"
                                } else {
                                    key = parser.getAttributeValue(item)
                                }

                                if (parser.next() == XmlPullParser.TEXT) {
                                    if (value == null) {
                                        value = parser.text
                                    } else {
                                        value += parser.text
                                    }
                                }

                                wasStringStart = true
                                break
                            }
                        } else {
                            if (wasStringStart) {
                                value += "<${parser.name}>"

                                if (parser.next() == XmlPullParser.TEXT) {
                                    value += parser.text
                                }
                            }
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    if (wasStringStart) {
                        value += parser.text
                    }
                }
                XmlPullParser.END_TAG -> {
                    name = parser.name
                    if (wasStringStart) {
                        if (name == TAG_STRING) {
                            if (key != null && value != null) {
                                resources[key] = value
                            }
                            wasStringStart = false
                            key = null
                            value = null

                        } else {
                            value += "</${parser.name}>"
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return resources
    }

    companion object {

        private val TAG: String = DefaultRemoteRepository::class.java.simpleName
        private const val TAG_STRING: String = "string"
    }
}
