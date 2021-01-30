package com.webbdong.gateway.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Webb Dong
 * @description: Netty Client 实现的 Http 客户端工具
 * @date 2021-01-30 12:01 PM
 */
@Slf4j
public class HttpNettyClientUtil {

    private HttpNettyClientUtil() {}

    private static final EventLoopGroup CLIENT_GROUP = new NioEventLoopGroup();

    private static final Bootstrap BOOTSTRAP = new Bootstrap();

    private static NettyInitChannelListener listener;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> CLIENT_GROUP.shutdownGracefully()));

        BOOTSTRAP.group(CLIENT_GROUP)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        log.info("HttpNettyClientUtil initChannel");
                        ch.pipeline().addLast(new HttpResponseDecoder())
                                .addLast(new HttpRequestEncoder());
                        if (listener != null) {
                            listener.initChannel(ch);
                        }
                    }

                });
    }

    public static void registerInitChannelListener(NettyInitChannelListener listener) {
        HttpNettyClientUtil.listener = listener;
    }

    /**
     * 建立连接
     * @param inetHost
     * @param inetPort
     * @return
     */
    public static ChannelFuture connect(String inetHost, int inetPort) {
        return BOOTSTRAP.connect(inetHost, inetPort);
    }

    /**
     * 建立连接并注册监听器
     * @param inetHost
     * @param inetPort
     * @param listener
     * @return
     */
    public static ChannelFuture connect(String inetHost, int inetPort,
                                        GenericFutureListener<? extends Future<? super Void>> listener) {
        return BOOTSTRAP.connect(inetHost, inetPort).addListener(listener);
    }

    @FunctionalInterface
    public interface NettyInitChannelListener {

        void initChannel(SocketChannel sc);

    }

}
