package com.nohana.helper;


public class DataHelpers {

    public static boolean isNotEmpty(Object object){
        return ! isEmpty(object);
    }

    public static boolean isEmpty(Object object){
        if(null == object){
            return true;
        }
        Class<?> objectClass = object.getClass();
        return false;
    }

}
