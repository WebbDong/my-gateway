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

    /**
     * 服务id
     */
    private String id;

    /**
     * 下游转发的 url 数组
     */
    private String[] listOfServers;

    /**
     * url 匹配条件数组
     */
    private String[] paths;

}
