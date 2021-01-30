package com.webbdong.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author Webb Dong
 * @description: 前置过滤器接口
 * @date 2021-01-30 9:51 PM
 */
public interface PreFilter {

    void filter(ChannelHandlerContext ctx, FullHttpRequest fullRequest);

}
