package com.webbdong.gateway;

import com.webbdong.gateway.netty.MyGatewayServer;

/**
 * @author Webb Dong
 * @description: 网关启动类
 * @date 2021-01-24 19:06
 */
public class MyGatewayApplication {

    public static void main(String[] args) throws InterruptedException {
        MyGatewayServer server = new MyGatewayServer();
        server.start();
    }

}
