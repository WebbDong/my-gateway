package com.webbdong.gateway.forward;

/**
 * @author Webb Dong
 * @description: 转发器客户端类型
 * @date 2021-01-28 12:57 PM
 */
public interface ForwarderClientType {

    String OK_HTTP_CLIENT = "okhttp";

    String NETTY_CLIENT = "netty";

    String HTTP_CLIENT = "httpclient";

}
