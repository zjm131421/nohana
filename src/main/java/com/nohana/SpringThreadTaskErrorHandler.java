package com.nohana;


import com.nohana.helper.ExceptionHelpers;
import org.apache.logging.log4j.Level;
import org.springframework.util.ErrorHandler;

public class SpringThreadTaskErrorHandler implements ErrorHandler{

    @Override
    public void handleError(Throwable e) {
        Nohana.log(Level.ERROR,String.format(" Error in thread task :%s ", ExceptionHelpers.getRootMessage(e)));
    }
}
