package com.webbdong.gateway.router;

import com.webbdong.gateway.config.SystemConfigHolder;
import com.webbdong.gateway.model.RouteConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Webb Dong
 * @description: 轮询路由
 * @date 2021-01-27 7:21 PM
 */
public class RoundRobin implements Router {

    private Map<String, Integer> roundRobinPosMap = new HashMap<>();

    public RoundRobin() {
/*        final List<RouteConfig> routes = SystemConfigHolder.CONFIG.getMyGateway().getRoutes();
        if (routes != null) {
            routes.forEach((routeConfig) -> {
                roundRobinPosMap.put(, 0);
            });
        }*/
    }

    @Override
    public String route(String requestUrl) {
        return null;
    }

}
