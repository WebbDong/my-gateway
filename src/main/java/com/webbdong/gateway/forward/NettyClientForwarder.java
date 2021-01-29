package com.webbdong.gateway.forward;

import com.webbdong.gateway.util.GuardedSuspensionObject;
import com.webbdong.gateway.util.UriUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Webb Dong
 * @description: 使用 Netty Client 转发
 * @date 2021-01-28 12:43 PM
 */
public class NettyClientForwarder implements Forwarder {

    @Override
    public FullHttpResponse forward(String forwardUrl, FullHttpRequest fullRequest) {
        final long gsoKey = System.nanoTime();
        GuardedSuspensionObject<ResponseData> gso = GuardedSuspensionObject.create(
                gsoKey, 100, TimeUnit.MILLISECONDS);
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpResponseDecoder())
                                    .addLast(new HttpRequestEncoder())
                                    .addLast(new HttpClientHandler(gsoKey, gso));
                        }

                    });

            UriUtil.Host host = UriUtil.getHostNameAndPortFromUrl(forwardUrl);
            System.out.println(host);
            ChannelFuture f = b.connect(host.getHost(), host.getPort()).sync();
            System.out.println("connect success");
            URI uri = new URI(UriUtil.urlConcat(forwardUrl, fullRequest.uri()));
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers().setAll(fullRequest.headers());
            request.headers().set(HttpHeaderNames.HOST, host.getHost());
            f.channel().writeAndFlush(request);
            System.out.println("writeAndFlush");
            f.channel().closeFuture().sync();
            // 异步转同步，阻塞等待数据
            ResponseData responseData = gso.get(rd -> rd != null);
            return parseResponseData(responseData);
        } catch (InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            clientGroup.shutdownGracefully();
        }
        return null;
    }

    /**
     * 处理响应数据
     * @param responseData
     * @return
     */
    private FullHttpResponse parseResponseData(ResponseData responseData) {
        List<ByteBuf> byteBufList = responseData.getByteBufList();
        ByteBuf totalByteBuf = Unpooled.directBuffer(responseData.getTotalBytesLength());
        byteBufList.forEach(buf -> {
            totalByteBuf.writeBytes(buf);
            ReferenceCountUtil.release(buf);
        });
        HttpHeaders headers = responseData.getHeaders();
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(responseData.getStatusCode()), totalByteBuf);
        httpResponse.headers().setAll(headers);
        return httpResponse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private final static class ResponseData {

        private HttpHeaders headers;

        private List<ByteBuf> byteBufList = new ArrayList<>();

        private int statusCode;

        private int totalBytesLength;

        public void addLength(int length) {
            totalBytesLength += length;
        }

        @Override
        public String toString() {
            return super.toString();
        }

    }

    private final static class HttpClientHandler extends ChannelInboundHandlerAdapter {

        /**
         * 用作 GuardedSuspensionObject 的 key
         */
        private Object gsoKey;

        private ResponseData responseData = new ResponseData();

        private GuardedSuspensionObject<ResponseData> gso;

        public HttpClientHandler(Object gsoKey, GuardedSuspensionObject<ResponseData> gso) {
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
                responseData.setHeaders(httpResponse.headers());
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
