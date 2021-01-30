package com.webbdong.gateway.filter;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Webb Dong
 * @description: 后置过滤器接口
 * @date 2021-01-30 9:53 PM
 */
public interface PostFilter {

    void filter(FullHttpResponse response);

}
