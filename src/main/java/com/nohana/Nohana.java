package com.nohana;


import com.nohana.config.NohanaConfig;
import com.nohana.config.SpringConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 初始化及框架日志
 * */
public class Nohana {

    private static boolean initialized = false;

    private static AbstractApplicationContext context = null;

    private static final Logger logger = LogManager.getLogger(Nohana.class);

    private static final Logger debugLogger = LogManager.getLogger("debugLogger");

    public static void debug(String message,Object...args){
        debugLogger.printf(Level.DEBUG,message,args);
    }

    public static void log(Level level,String message,Object...args){
        logger.printf(level,message,args);
    }

    public static boolean isInitialized(){
        return initialized;
    }

    public static void init(){
        try{
            //判断是否已经初始化
            if(initialized){
                return;
            }
            logger.printf(Level.INFO, "Nohana starting up ...");
            initialized = true;

            configSpring();


        }catch (Throwable e){

        }
    }

    //配置spring
    private static void configSpring() {
        if(null == context){
            NohanaConfig.getInstance().getAppSpringConfigClass();
            context = new AnnotationConfigApplicationContext(SpringConfig.class); //依赖于注解作为容器配置信息的来源
        }
    }

}
