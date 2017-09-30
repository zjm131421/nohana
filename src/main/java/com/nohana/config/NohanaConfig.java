package com.nohana.config;


import com.nohana.Nohana;
import com.nohana.RouteConfigData;
import com.nohana.exception.GeneralException;
import com.nohana.helper.DataHelpers;
import com.nohana.helper.FileHelpers;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 获取资源文件
 * */
public class NohanaConfig {

    public static final String FILES_FOLDER_NAME = "files";

    private String rootFolder;

    private String configFolder;

    private String filesFolder;

    private PropertiesConfiguration configuration;

    private static NohanaConfig instance;

    public static NohanaConfig getInstance(){
        if(null == instance){
            instance = new NohanaConfig();
        }
        return instance;
    }

    private NohanaConfig(){
        loadFolderPath();
        loadConfiguration();
    }

    //加载目录路径
    private void loadFolderPath() {
        rootFolder = FileHelpers.getPackageRootPath(Nohana.class); //根目录
        configFolder = rootFolder + "config/"; //配置文件目录
        filesFolder = rootFolder + FILES_FOLDER_NAME + "/";

        createFileFolderIfNotExists(filesFolder);
    }

    private void createFileFolderIfNotExists(String filesFolder){
        try {
            Path staticFolderPath = Paths.get(filesFolder);
            //判断是否为目录或者目录是否存在
            if(! Files.isDirectory(staticFolderPath)){
                Files.createDirectories(staticFolderPath);
            }
            //判断目录是否是否可读或可写
            if(! Files.isReadable(staticFolderPath) || ! Files.isWritable(staticFolderPath)){
                throw new GeneralException(" Can not read/writer to static folder ");
            }
        }catch (IOException e){
            throw new GeneralException(ExceptionUtils.getMessage(e));
        }

    }

    //加载配置文件
    private void loadConfiguration() {
        try {
            configuration = new PropertiesConfiguration(configFolder + "app.properties");
            //如果属性丢失是否抛出异常
            configuration.setThrowExceptionOnMissing(false);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void configLog4j(){
        //设置系统属性
        System.setProperty("logBaseDir",rootFolder+ "logs/");
        System.setProperty("log4j.configurationFile",configFolder + "log.xml");
    }


    public String getJdbcDriverClassName() {
        return getString("jdbc.driverClassName","");
    }

    public String getJdbcUrl() {
        return getString("jdbc.url","");
    }

    public String getJdbcUsername() {
        return getString("jdbc.username","");
    }

    public String getJdbcPassword() {
        return getString("jdbc.password","");
    }

    public Optional<Class<?>> getAppSpringConfigClass() {
        String appSpringConfigClass = getString("app.springConfigClass","");
        if(DataHelpers.isEmptyString(appSpringConfigClass)){
            return Optional.empty();
        }
        try {
            return Optional.of(Class.forName(appSpringConfigClass));
        } catch (ClassNotFoundException e) {
            throw new GeneralException(String.format("Can not load class: %s %s",appSpringConfigClass,ExceptionUtils.getMessage(e)));
        }
    }

    private String getString(String name,String defaultValue){
        if(null == configuration){
            return defaultValue;
        }
        else {
            return configuration.getString(name,defaultValue);
        }
    }

    public List<RouteConfigData> getRoutesConfig() {
        String routeConfigPrefix = "routes";
        int routeConfigPrefixLength = routeConfigPrefix.length();

        List<RouteConfigData> routeConfigDataList = new ArrayList<>();

        Map<String, String> routeGroupValues = getGroupValues(routeConfigPrefix);
        routeGroupValues.keySet().forEach(key -> {
            String value = routeGroupValues.get(key);
            if(DataHelpers.isEmpty(value)){
                return;
            }
            RouteConfigData routeConfigData = DataHelpers.factory(RouteConfigData.class);
            routeConfigData.setName(key.substring(routeConfigPrefixLength + 1));
            routeConfigData.setUrlPattern(value);

            Map<String, String> defaults = new HashMap<>();
            String defaultValueConfigPrefix = "routeDefaults."+routeConfigData.getName();
            int defaultValueConfigPrefixLength = defaultValueConfigPrefix.length();

            Map<String, String> defaultConfigValues = getGroupValues(defaultValueConfigPrefix);
            defaultConfigValues.keySet().forEach(defaultValueKey -> {
                String defaultValue = defaultConfigValues.get(defaultValueKey);
                defaults.put(defaultValueKey.substring(defaultValueConfigPrefixLength + 1), defaultValue);
            });
            routeConfigData.setDefaults(defaults);

            routeConfigDataList.add(routeConfigData);
        });

        return routeConfigDataList;
    }

    public Map<String,String> getGroupValues(String group){
        Map<String,String> values = new TreeMap<>();
        Iterator<String> i = configuration.getKeys(group);
        while (i.hasNext()){
            String key = i.next();
            values.put(key,getString(key,""));
        }
        return values;
    }

    private String getAppKey(String key){return String.format("app.%s",key);}

}
