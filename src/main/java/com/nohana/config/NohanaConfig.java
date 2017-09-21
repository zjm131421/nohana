package com.nohana.config;


import org.apache.commons.configuration.PropertiesConfiguration;

public class NohanaConfig {

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

    private void loadFolderPath() {

    }

    private void loadConfiguration() {
    }


}
