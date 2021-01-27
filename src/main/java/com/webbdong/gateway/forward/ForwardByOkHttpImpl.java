package com.webbdong.gateway.forward;

import com.webbdong.gateway.util.OkHttpClientUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Webb Dong
 * @description:
 * @date 2021-01-27 6:26 PM
 */
public class ForwardByOkHttpImpl implements Forwarder {

    @Override
    public FullHttpResponse forward(FullHttpRequest fullRequest) {
        OkHttpClient client = OkHttpClientUtil.getInstance();

        final Headers.Builder headersBuilder = new Headers.Builder();
        fullRequest.headers().forEach(entry -> headersBuilder.add(entry.getKey(), entry.getValue()));

        fullRequest.headers().get("");
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://localhost:8082")
                .append(fullRequest.uri());
        Request request = new Request.Builder()
                .get()
                .headers(headersBuilder.build())
                .url(urlBuilder.toString())
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(response.body().string().getBytes("utf-8")));
            response.headers().forEach(v -> httpResponse.headers().add(v.getFirst(), v.getSecond()));
            return httpResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
