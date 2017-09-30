package com.nohana;


import com.nohana.config.NohanaConfig;
import com.nohana.config.SpringConfig;
import com.nohana.exception.GeneralException;
import com.nohana.http.ConnectionManager;
import com.nohana.http.KeyMaterialFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Map;
import java.util.Optional;

/**
 * 初始化及框架日志
 */
public class Nohana {

    private static boolean initialized = false;

    private static AbstractApplicationContext context = null;

    private static final Logger logger = LogManager.getLogger(Nohana.class);

    private static final Logger debugLogger = LogManager.getLogger("debugLogger");

    public static void debug(String message, Object... args) {
        debugLogger.printf(Level.DEBUG, message, args);
    }

    public static void log(Level level, String message, Object... args) {
        logger.printf(level, message, args);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void init() {
        try {
            //判断是否已经初始化
            if (initialized) {
                return;
            }
            logger.printf(Level.INFO, "Nohana starting up ...");
            initialized = true;

            configSpring();
            ConnectionManager.init(getSpringBean(KeyMaterialFactory.class).orElse(null));
            loadRoutes();


        } catch (Throwable e) {

        }
    }

    public static <T> Optional<T> getSpringBean(Class<T> requiredType) throws BeansException {
        if (context == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(context.getBean(requiredType));
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }

    public static <T> T requireStpringBean(Class<T> requiredType) throws BeansException{
        return getSpringBean(requiredType).orElseThrow(() -> new GeneralException(String.format(" %s is not in spring beans ", requiredType.getName())));
    }

    public static <T> Optional<Map<String,T>> getSpringBeansOfType(Class<T> requiredType) throws BeansException {
        if(null == context){
            return Optional.empty();
        }
        return Optional.of(context.getBeansOfType(requiredType));
    }

    public static <T> Map<String,T> requireSpringBeansOfType(Class<T> requiredType) throws BeansException {
        return  getSpringBeansOfType(requiredType).orElseThrow(() -> new GeneralException(String.format("%s is not in spring beans",requiredType.getName())));
    }

    //配置spring
    private static void configSpring() {
        if (null == context) {
            Optional<Class<?>> appSpringConfigClass = NohanaConfig.getInstance().getAppSpringConfigClass();
            if (appSpringConfigClass.isPresent()) {
                context = new AnnotationConfigApplicationContext(SpringConfig.class, appSpringConfigClass.get());
            } else {
                context = new AnnotationConfigApplicationContext(SpringConfig.class); //依赖于注解作为容器配置信息的来源
            }

        }
    }

    private static void loadRoutes(){
        Routes routes = Routes.getInstance();

        NohanaConfig.getInstance().getRoutesConfig();
    }


}
