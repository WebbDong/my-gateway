package com.webbdong.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author Webb Dong
 * @description:
 * @date 2021-01-30 9:56 PM
 */
public class HttpRequestFilter implements PreFilter {

    @Override
    public void filter(ChannelHandlerContext ctx, FullHttpRequest fullRequest) {
        fullRequest.headers().set("Server-Id", "MyGateway");
    }

}
