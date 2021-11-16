package com.aposbot.common;

import java.lang.reflect.Field;

public class ReflectionUtil {

    @SuppressWarnings("unchecked")
    public static <TObject, TValue> TValue getFieldValue(TObject object, String fieldName) {
        try {
            Field field = getField(object.getClass(), fieldName);
            field.setAccessible(true);
            Object value = field.get(object);
            return (TValue) value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Field getField(Class<?> clazz, String name) {
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception ignored) {
            }
            clazz = clazz.getSuperclass();
        }
        return field;
    }
}
