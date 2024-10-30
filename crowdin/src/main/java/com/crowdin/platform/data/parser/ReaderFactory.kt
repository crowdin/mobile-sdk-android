package com.crowdin.platform.data.parser

internal object ReaderFactory {
    fun createReader(type: ReaderType): Reader =
        when (type) {
            ReaderType.XML -> XmlReader(StringResourceParser())
            ReaderType.JSON -> JsonReader()
        }

    enum class ReaderType {
        XML,
        JSON,
    }
}
