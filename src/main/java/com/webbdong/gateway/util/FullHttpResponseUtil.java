package com.webbdong.gateway.util;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author Webb Dong
 * @description: FullHttpResponseUtil
 * @date 2021-01-28 5:09 PM
 */
public class FullHttpResponseUtil {

    public static FullHttpResponse create404Response() {
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        httpResponse.headers().set("Content-Type", "text/html;charset=utf-8");
        httpResponse.headers().set("Content-Length", 0);
        return httpResponse;
    }

    public static FullHttpResponse create500Response() {
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        httpResponse.headers().set("Content-Type", "text/html;charset=utf-8");
        httpResponse.headers().set("Content-Length", 0);
        return httpResponse;
    }

    public static FullHttpResponse createResponseByStatusCode(int code) {
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(code));
        httpResponse.headers().set("Content-Type", "text/html;charset=utf-8");
        httpResponse.headers().set("Content-Length", 0);
        return httpResponse;
    }

}
