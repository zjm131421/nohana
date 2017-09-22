package com.nohana.helper;


import com.nohana.exception.GeneralException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class FileHelpers {

    public static String getPackageRootPath(Class<?> tClass){
        try {
            //tClass.getProtectionDomain() 类的保护域
            //.getCodeSource() 保护域的源代码
            //.getLocation()  源代码的位置
            //.toURL()
            //.getPath()
            String path = new File(URLDecoder.decode(tClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath(),CharsetHelpers.UTF8_NAME)).getParentFile().getAbsolutePath();

            return StringUtils.stripEnd(StringUtils.stripEnd(path,"/"),"\\")+"/";
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            throw new GeneralException(ExceptionUtils.getMessage(e));
        }
    }

    public static void main(String[] args) {

    }

}
