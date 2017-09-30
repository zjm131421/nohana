package com.nohana.helper;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionHelpers {


    public static List<Field> getDeclaredAndInheritFields(Class<?> clazz) {

        List<Field> fields = new ArrayList<>();
        boolean inheritOnly = false;
        for (; clazz != null && !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
            fields.addAll(getDeclaredFieldsStream(clazz, inheritOnly));
            if (!inheritOnly) {
                inheritOnly = true;
            }
        }
        return fields;
    }

    private static List<Field> getDeclaredFieldsStream(Class<?> clazz, boolean inheritOnly) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) &&
                        (Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers()) || !inheritOnly))
                .collect(Collectors.toList());
    }

}
