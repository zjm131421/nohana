package com.nohana.helper;


import com.nohana.DataFactoryHandler;
import com.nohana.annotation.KeyNameAlias;
import com.nohana.exception.GeneralException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class DataHelpers {

    public static boolean isNotEmpty(Object object){
        return ! isEmpty(object);
    }

    public static boolean isEmptyString(String str){
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmptyString(String str) {
        return ! isEmptyString(str);
    }

    public static boolean isEmptyInt(long data) {
        return data == 0;
    }

    public static boolean isNotEmptyInt(long data) {
        return ! isEmptyInt(data);
    }

    public static boolean isEmpty(Object object){
        if(null == object){
            return true;
        }
        Class<?> objectClass = object.getClass();
        return false;
    }

    public static <T> T factory(Class<T> clazz,Map data){
        T object = newInstance(clazz);
        bindData(clazz,object,data);
        factoryDone(clazz,object);

        return object;
    }

    public static <T> T factory(Class<T> clazz){
        return factory(clazz,MapHelpers.EMPTY_STRING_MAP);
    }

    public static <T> T newInstance(Class<T> clazz){
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GeneralException(String.format("Failed to instance class %s,error: %s [%s]",clazz.toGenericString(), ExceptionUtils.getMessage(e),ExceptionUtils.getStackTrace(e)));
        }
    }

    @SuppressWarnings("unchecked")
    private static void bindData(Class<?> clazz,Object object,Map data){
        if(TypeHelpers.isOrSubClass(clazz,Map.class)){
            data.keySet().forEach(key -> ((Map)object).put(key,data.get(key)));
        }else {
            ReflectionHelpers.getDeclaredAndInheritFields(clazz).stream().forEach(
                    field -> {
                        Object fieldData = findFieldDataFromMap(field,data);
                        //TODO
//                        Object fieldValue = createFieldValue(field, fieldData, data);
//                        ReflectionHelpers.setFieldValue(field, object, fieldValue);
                    });
        }

    }

    private static void factoryDone(Class<?> clazz,Object object){
        if(TypeHelpers.isOrSubClass(clazz,DataFactoryHandler.class)){
            ((DataFactoryHandler)object).afterFactory();
        }
    }

    private static Object findFieldDataFromMap(Field field,Map data){
        String fieldName = field.getName();
        if(data.containsKey(fieldName)){
            return data.get(fieldName);
        }
        fieldName = StringHelpers.camelCaseToLowerUnderscore(fieldName);
        if (data.containsKey(fieldName)) {
            return data.get(fieldName);
        }
        if (field.isAnnotationPresent(KeyNameAlias.class)) {
            String[] fieldNameAliasList = field.getAnnotation(KeyNameAlias.class).value();
            for(String fieldNameAlias : fieldNameAliasList) {
                if (data.containsKey(fieldNameAlias)) {
                    return data.get(fieldNameAlias);
                }
            }
        }

        return null;
    }

}
