package com.webbdong.gateway.netty.handler;

import com.webbdong.gateway.forward.Forwarder;
import com.webbdong.gateway.forward.ForwarderFactory;
import com.webbdong.gateway.router.RandomRouter;
import com.webbdong.gateway.router.Router;
import com.webbdong.gateway.util.FullHttpResponseUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;

/**
 * @author Webb Dong
 * @description: 转发处理器
 * @date 2021-01-27 4:02 PM
 */
public final class ForwardHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Forwarder forwarder = ForwarderFactory.createForwarder();

    private Router router = new RandomRouter();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullRequest) throws Exception {
        System.out.println(this);
        FullHttpResponse httpResponse;
        try {
            final String forwardUrl = router.route(fullRequest.uri());
            if (forwardUrl == null) {
                httpResponse = FullHttpResponseUtil.create404Response();
            } else {
                httpResponse = forwarder.forward(forwardUrl, fullRequest);
            }
            if (httpResponse == null) {
                httpResponse = FullHttpResponseUtil.create500Response();
            }
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
                } else {
                    httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.writeAndFlush(httpResponse);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
