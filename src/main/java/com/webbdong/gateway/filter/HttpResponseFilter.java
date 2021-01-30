package com.webbdong.gateway.filter;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Webb Dong
 * @description:
 * @date 2021-01-30 9:55 PM
 */
public class HttpResponseFilter implements PostFilter {

    @Override
    public void filter(FullHttpResponse response) {
        response.headers().set("Server-Id", "MyGateway");
    }

}
