package com.nohana.helper;


import org.apache.commons.lang.exception.ExceptionUtils;

public class ExceptionHelpers {

    public static String getRootMessage(Throwable e){
        Throwable root = ExceptionUtils.getRootCause(e); //获取异常根的原因
        root = (null == root ? e : root);
        return getMessage(root);
    }

    private static String getMessage(Throwable th) {
        if(null == th){
            return "";
        }
        return th.getMessage();
    }

}
