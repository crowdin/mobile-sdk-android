package com.crowdin.platform.utils

import java.lang.reflect.Field

internal object ReflectionUtils {

    @JvmStatic
    fun getField(clazz: Class<*>, fieldName: String): Field? = try {
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        field
    } catch (ignored: NoSuchFieldException) {
        null
    }

    @JvmStatic
    fun getValue(field: Field, obj: Any): Any? = try {
        field.get(obj)
    } catch (ignored: IllegalAccessException) {
        null
    }

    @JvmStatic
    fun setValue(field: Field, obj: Any, value: Any) = try {
        field.set(obj, value)
    } catch (ignored: IllegalAccessException) {
    }
}