package com.webbdong.gateway.forward;

import com.webbdong.gateway.model.NettyClientResponseData;
import com.webbdong.gateway.util.GuardedSuspensionObject;
import com.webbdong.gateway.util.HttpNettyClientUtil;
import com.webbdong.gateway.util.UriUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Webb Dong
 * @description: 使用 Netty Client 转发
 * @date 2021-01-28 12:43 PM
 */
@Slf4j
public class NettyClientForwarder implements Forwarder {

    @Override
    public FullHttpResponse forward(String forwardUrl, FullHttpRequest fullRequest) {
        final long gsoKey = System.nanoTime();
        GuardedSuspensionObject<NettyClientResponseData> gso = GuardedSuspensionObject.create(
                gsoKey, 100, TimeUnit.MILLISECONDS);
        UriUtil.Host host = UriUtil.getHostNameAndPortFromUrl(forwardUrl);
        log.info("host: {}", host);

        ChannelFuture f;
        // HttpNettyClientUtil 的 listener 是共享变量，加锁
        synchronized (this) {
            HttpNettyClientUtil.registerInitChannelListener(
                    ch -> ch.pipeline().addLast(new HttpClientHandler(gsoKey, gso)));
            try {
                f = HttpNettyClientUtil.connect(host.getHost(), host.getPort()).sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("connect success");

        URI uri;
        try {
            uri = new URI(UriUtil.urlConcat(forwardUrl, fullRequest.uri()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
        request.headers().setAll(fullRequest.headers());
        request.headers().set(HttpHeaderNames.HOST, host.getHost());
        f.channel().writeAndFlush(request);
        log.info("writeAndFlush success");

        try {
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 异步转同步，阻塞等待数据
        return parseResponseData(gso.get(rd -> rd != null));
    }

    /**
     * 处理响应数据
     * @param responseData
     * @return
     */
    private FullHttpResponse parseResponseData(NettyClientResponseData responseData) {
        List<ByteBuf> byteBufList = responseData.getByteBufList();
        ByteBuf totalByteBuf = Unpooled.directBuffer(responseData.getTotalBytesLength());
        byteBufList.forEach(buf -> {
            totalByteBuf.writeBytes(buf);
            ReferenceCountUtil.release(buf);
        });
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(responseData.getStatusCode()), totalByteBuf);
        httpResponse.headers().setAll(responseData.getResponseHeaders());
        return httpResponse;
    }

    private final static class HttpClientHandler extends ChannelInboundHandlerAdapter {

        /**
         * 用作 GuardedSuspensionObject 的 key
         */
        private Object gsoKey;

        private NettyClientResponseData responseData = new NettyClientResponseData();

        private GuardedSuspensionObject<NettyClientResponseData> gso;

        public HttpClientHandler(Object gsoKey, GuardedSuspensionObject<NettyClientResponseData> gso) {
            this.gsoKey = gsoKey;
            this.gso = gso;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (Objects.equals(msg.getClass(), DefaultHttpContent.class)) {
                ByteBuf byteBuf = ((DefaultHttpContent) msg).content();
                responseData.getByteBufList().add(byteBuf);
                responseData.addLength(byteBuf.readableBytes());
            } else if (Objects.equals(msg.getClass(), DefaultHttpResponse.class)) {
                DefaultHttpResponse httpResponse = (DefaultHttpResponse) msg;
                responseData.setResponseHeaders(httpResponse.headers());
                responseData.setStatusCode(httpResponse.status().code());
            } else {
                // 处理 DefaultLastHttpContent 和 EMPTY_LAST_CONTENT
                if (Objects.equals(msg.getClass(), DefaultLastHttpContent.class)) {
                    ByteBuf byteBuf = ((DefaultLastHttpContent) msg).content();
                    responseData.getByteBufList().add(byteBuf);
                    responseData.addLength(byteBuf.readableBytes());
                }
                // 设置数据，唤醒等待的线程
                gso.fireEvent(gsoKey, responseData);
                ctx.close();
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

    }

}
