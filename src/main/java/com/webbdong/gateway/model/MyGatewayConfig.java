package com.webbdong.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Webb Dong
 * @description: 网关配置
 * @date 2021-01-24 17:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyGatewayConfig {

    /**
     * 路由配置
     */
    private RouteConfig[] routes;

}
