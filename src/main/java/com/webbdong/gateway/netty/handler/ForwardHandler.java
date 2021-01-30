package com.webbdong.gateway.netty.handler;

import com.webbdong.gateway.filter.HttpRequestFilter;
import com.webbdong.gateway.filter.HttpResponseFilter;
import com.webbdong.gateway.filter.PostFilter;
import com.webbdong.gateway.filter.PreFilter;
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
import lombok.extern.slf4j.Slf4j;

/**
 * @author Webb Dong
 * @description: 转发处理器
 * @date 2021-01-27 4:02 PM
 */
@Slf4j
public final class ForwardHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Forwarder forwarder = ForwarderFactory.createForwarder();

    private Router router = new RandomRouter();

    private PreFilter preFilter = new HttpRequestFilter();

    private PostFilter postFilter = new HttpResponseFilter();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullRequest) throws Exception {
        log.info("ForwardHandler channelRead0");
        preFilter.filter(ctx, fullRequest);
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
            postFilter.filter(httpResponse);
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
