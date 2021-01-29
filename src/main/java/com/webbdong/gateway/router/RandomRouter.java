package com.webbdong.gateway.router;

import com.webbdong.gateway.config.SystemConfigHolder;
import com.webbdong.gateway.model.RouteConfig;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Webb Dong
 * @description: 随机路由
 * @date 2021-01-27 7:05 PM
 */
public class RandomRouter implements Router {

    @Override
    public String route(String requestUri) {
        final RouteConfig routeConfig = SystemConfigHolder.ROUTE_CONFIG_MAP.get(requestUri);
        if (routeConfig == null) {
            return null;
        }
        final String[] urls = routeConfig.getListOfServers();
        return urls[ThreadLocalRandom.current().nextInt(urls.length)];
    }

}
