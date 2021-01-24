package com.webbdong.gateway.config;

import com.webbdong.gateway.model.SystemConfig;
import org.ho.yaml.Yaml;

import java.io.FileNotFoundException;

/**
 * @author Webb Dong
 * @description: SystemConfigHolder
 * @date 2021-01-24 17:19
 */
public class SystemConfigHolder {

    public static final SystemConfig CONFIG;

    private static final String CONFIG_FILE_NAME = "application.yml";

    static {
        try {
            CONFIG = Yaml.loadType(
                    ClassLoader.getSystemResourceAsStream(CONFIG_FILE_NAME), SystemConfig.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private SystemConfigHolder() {}

}
