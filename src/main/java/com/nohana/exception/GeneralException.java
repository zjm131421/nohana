package com.nohana.exception;


import com.nohana.constant.ExceptionCodes;
import org.apache.logging.log4j.Level;

public class GeneralException extends RuntimeException {

    protected Level logLevel = Level.ERROR;
    protected int code = ExceptionCodes.ERROR;

    public int getCode(){return code;}

    public GeneralException(String message){
        super(message);
    }

    public Level getLogLevel(){
        return logLevel;
    }


}
