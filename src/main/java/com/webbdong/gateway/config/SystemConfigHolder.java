package com.webbdong.gateway.config;

import com.webbdong.gateway.model.RouteConfig;
import com.webbdong.gateway.model.SystemConfig;
import org.ho.yaml.Yaml;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Webb Dong
 * @description: SystemConfigHolder
 * @date 2021-01-24 17:19
 */
public class SystemConfigHolder {

    public static final SystemConfig CONFIG;

    public static final Map<String, RouteConfig> ROUTE_CONFIG_MAP;

    private static final String CONFIG_FILE_NAME = "application.yml";

    static {
        try {
            CONFIG = Yaml.loadType(ClassLoader.getSystemResourceAsStream(CONFIG_FILE_NAME),
                    SystemConfig.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ROUTE_CONFIG_MAP = new HashMap<>();
        final RouteConfig[] routes = CONFIG.getMyGateway().getRoutes();
        for (int i = 0, len = routes.length; i < len; i++) {
            final RouteConfig routeConfig = routes[i];
            final String[] paths = routeConfig.getPaths();
            for (int j = 0, len2 = paths.length; j < len2; j++) {
                ROUTE_CONFIG_MAP.put(paths[j], routeConfig);
            }
        }
    }

    private SystemConfigHolder() {}

}
