package com.webbdong.gateway.forward;

import com.webbdong.gateway.config.SystemConfigHolder;
import com.webbdong.gateway.exception.UnknownForwarderClientTypeException;

/**
 * @author Webb Dong
 * @description: 转发器工厂
 * @date 2021-01-28 12:50 PM
 */
public class ForwarderFactory {

    private ForwarderFactory() {}

    public static Forwarder createForwarder() {
        final String forwardClientType = SystemConfigHolder.CONFIG.getMyGateway().getForwardClientType();
        if (ForwarderClientType.OK_HTTP_CLIENT.equals(forwardClientType)) {
            return new OkHttpForwarder();
        } else if (ForwarderClientType.HTTP_CLIENT.equals(forwardClientType)) {
            return new ApacheHttpClientForwarder();
        } else if (ForwarderClientType.NETTY_CLIENT.equals(forwardClientType)) {
            return new NettyClientForwarder();
        } else {
            throw new UnknownForwarderClientTypeException(forwardClientType);
        }
    }

}
