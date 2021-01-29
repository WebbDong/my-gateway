package com.webbdong.gateway.forward;

import com.webbdong.gateway.util.UriUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Webb Dong
 * @description: 使用 Netty Client 转发
 * @date 2021-01-28 12:43 PM
 */
public class NettyClientForwarder implements Forwarder {

    @Override
    public FullHttpResponse forward(String forwardUrl, FullHttpRequest fullRequest) {
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
                                    .addLast(new HttpClientHandler());
                        }

                    });

            UriUtil.Host host = UriUtil.getHostNameAndPortFromUrl(forwardUrl);
            ChannelFuture f = b.connect(host.getHost(), host.getPort()).sync();
            URI uri = new URI(UriUtil.urlConcat(forwardUrl, fullRequest.uri()));
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers().setAll(fullRequest.headers());
            request.headers().set(HttpHeaderNames.HOST, host.getHost());
            f.channel().writeAndFlush(request);
            f.channel().closeFuture().sync();
        } catch (InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            clientGroup.shutdownGracefully();
        }
        return null;
    }

    private static class HttpClientHandler extends SimpleChannelInboundHandler<HttpResponse> {

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpResponse httpResponse) throws Exception {
            ctx.writeAndFlush(httpResponse);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

    }

}
