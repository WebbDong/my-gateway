package com.webbdong.gateway.util;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @author Webb Dong
 * @description: OkHttp工具类，单例模式
 * @date 2021-01-26 5:10 PM
 */
public class OkHttpClientUtil {

    private OkHttpClientUtil() {}

    private static class Inner {

        private static final OkHttpClient INSTANCE = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .hostnameVerifier((s, sslSession) -> true)
                .build();

    }

    public static OkHttpClient getInstance() {
        return Inner.INSTANCE;
    }

}
