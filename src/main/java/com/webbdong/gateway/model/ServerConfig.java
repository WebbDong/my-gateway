package com.webbdong.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Webb Dong
 * @description: 服务器配置
 * @date 2021-01-24 17:10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerConfig {

    /**
     * 端口号
     */
    private Integer port;

}
