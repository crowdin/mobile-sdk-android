package com.crowdin.platform.repository.remote.api

internal class PluralData(var name: String = "",
                          var quantity: MutableMap<String, String> = mutableMapOf(),
                          var number: Int = -1,
                          var formatArgs: Array<out Any?> = arrayOf())
