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
     * 转发到下游服务
     * @param fullRequest
     * @return
     */
    FullHttpResponse forward(FullHttpRequest fullRequest);

}
