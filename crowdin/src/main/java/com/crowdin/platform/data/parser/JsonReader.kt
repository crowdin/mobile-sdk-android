package com.crowdin.platform.data.parser

import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.StringData
import com.google.gson.Gson
import java.io.InputStream

internal class JsonReader : Reader {
    override fun parseInput(byteStream: InputStream): LanguageData =
        try {
            val json = byteStream.bufferedReader().use { it.readText() }
            val result = Gson().fromJson(json, Map::class.java) as Map<String, String>
            val stringList = mutableListOf<StringData>()
            result.keys.forEach {
                stringList.add(StringData(it, result[it] ?: ""))
            }
            val languageData = LanguageData()
            languageData.resources = stringList

            languageData
        } catch (e: Exception) {
            LanguageData()
        }
}
