package com.webbdong.gateway.forward;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Webb Dong
 * @description: 转发接口
 * @date 2021-01-27 6:20 PM
 */
public interface Forwarder {

    /**
     * 服务id，通过这个id来获取对应的路由配置
     */
    String FORWARD_ROUTE_SERVER_ID_HTTP_HEADER = "Server-Id";

    /**
     * 转发到下游服务
     * @param fullRequest
     * @return
     */
    FullHttpResponse forward(FullHttpRequest fullRequest);

}
