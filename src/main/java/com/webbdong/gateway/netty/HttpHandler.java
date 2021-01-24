package com.webbdong.gateway.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Webb Dong
 * @description: HttpHandler
 * @date 2021-01-21 22:50
 */
public class HttpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest fullRequest = (FullHttpRequest) msg;
        System.out.println(fullRequest.method());
        FullHttpResponse httpResponse = null;
        try {
            httpResponse = forward(fullRequest);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fullRequest != null && httpResponse != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(httpResponse).addListener(ChannelFutureListener.CLOSE);
                } else {
                    httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.write(httpResponse);
                }
            }
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 转发
     * @param fullRequest
     * @return
     */
    private FullHttpResponse forward(FullHttpRequest fullRequest) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();
        final Headers.Builder headersBuilder = new Headers.Builder();
        fullRequest.headers().forEach(entry -> headersBuilder.add(entry.getKey(), entry.getValue()));
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://localhost:8080")
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
