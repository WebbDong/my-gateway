package com.webbdong.gateway.forward;

import com.webbdong.gateway.util.OkHttpClientUtil;
import com.webbdong.gateway.util.UriUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Webb Dong
 * @description: 使用 OkHttp 转发
 * @date 2021-01-27 6:26 PM
 */
@Slf4j
public class OkHttpForwarder implements Forwarder {

    @Override
    public FullHttpResponse forward(String forwardUrl, FullHttpRequest fullRequest) {
        log.info("forwardUrl: {}", forwardUrl);
        OkHttpClient client = OkHttpClientUtil.getInstance();

        Headers.Builder headersBuilder = new Headers.Builder();
        fullRequest.headers().forEach(entry -> headersBuilder.add(entry.getKey(), entry.getValue()));

        Request.Builder requestBuilder = new Request.Builder()
                .get()
                .headers(headersBuilder.build())
                .header("Host", UriUtil.getHostNameFromUrl(forwardUrl))
                .url(UriUtil.urlConcat(forwardUrl, fullRequest.uri()));

        Call call = client.newCall(requestBuilder.build());
        try (Response response = call.execute()) {
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.code()),
                    Unpooled.wrappedBuffer(response.body().bytes()));
            response.headers().forEach(v -> httpResponse.headers().add(v.getFirst(), v.getSecond()));
            return httpResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
