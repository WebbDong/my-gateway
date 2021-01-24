package com.webbdong.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Webb Dong
 * @description: 路由配置
 * @date 2021-01-24 17:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteConfig {

    private String id;

    private String listOfServers;

}
