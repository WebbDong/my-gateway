package com.webbdong.gateway.util;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Webb Dong
 * @description: Apache HttpClient工具类，单例模式
 * @date 2021-01-28 5:46 PM
 */
public class ApacheHttpClientUtil {

    private ApacheHttpClientUtil() {}

    private static class Inner {

        static {
            Runtime.getRuntime().addShutdownHook(new Thread(ApacheHttpClientUtil.Inner::close));
        }

        private static final CloseableHttpClient INSTANCE = HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .evictIdleConnections(60, TimeUnit.SECONDS)
                .build();

        private static void close() {
            try {
                INSTANCE.close();
            } catch (IOException ignored) {
            }
        }

    }

    public static CloseableHttpClient getInstance() {
        return Inner.INSTANCE;
    }

}
