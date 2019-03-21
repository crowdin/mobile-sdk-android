package com.crowdin.platform.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by chris on 17/12/14.
 * Copied from Calligraphy:
 * https://github.com/chrisjenx/Calligraphy/blob/master/calligraphy/src/main/java/uk/co/chrisjenx/calligraphy/ReflectionUtils.java
 */
public class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static Field getField(Class clazz, String fieldName) {
        try {
            final Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException ignored) {
        }
        return null;
    }

    public static Object getValue(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }

    public static void setValue(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException ignored) {
        }
    }

    public static Method getMethod(Class clazz, String methodName) {
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    public static void invokeMethod(Object object, Method method, Object... args) {
        try {
            if (method == null) return;
            method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        }
    }
}
