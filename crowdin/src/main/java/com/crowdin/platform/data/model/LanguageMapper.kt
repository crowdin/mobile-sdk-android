package com.crowdin.platform.data.model

fun CustomLanguage?.toLanguageInfo(): LanguageInfo? =
    this?.let {
        LanguageInfo(
            id = locale,
            name = name,
            twoLettersCode = twoLettersCode,
            threeLettersCode = threeLettersCode,
            locale = locale,
            androidCode = androidCode
        )
    }
