package com.webbdong.gateway.netty.handler;

import com.webbdong.gateway.forward.ForwardByOkHttpImpl;
import com.webbdong.gateway.forward.Forwarder;
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

    private Forwarder forwarder = new ForwardByOkHttpImpl();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullRequest) throws Exception {
        FullHttpResponse httpResponse;
        try {
            httpResponse = forwarder.forward(fullRequest);
            if (fullRequest != null && httpResponse != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(httpResponse).addListener(ChannelFutureListener.CLOSE);
                } else {
                    httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.write(httpResponse);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // SimpleChannelInboundHandler 不需要手动调用 ReferenceCountUtil.release，会自动调用
        /*
        finally {
            ReferenceCountUtil.release(fullRequest);
        }
         */
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
