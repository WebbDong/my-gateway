package com.webbdong.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Webb Dong
 * @description: 系统配置
 * @date 2021-01-24 17:01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfig {

    /**
     * 服务器配置
     */
    private ServerConfig server;

    /**
     * 网关配置
     */
    private MyGatewayConfig myGateway;

}
