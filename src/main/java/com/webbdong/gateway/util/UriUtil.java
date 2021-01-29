package com.webbdong.gateway.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Webb Dong
 * @description: UriUtil
 * @date 2021-01-27 9:21 PM
 */
public class UriUtil {

    private static final String HTTP = "http://";

    private static final String HTTPS = "https://";

    public static String getHostNameFromUrl(String url) {
        int startIndex = url.indexOf(HTTP);
        int startOffset = HTTP.length();
        if (startIndex == -1) {
            startIndex = url.indexOf(HTTPS);
            startOffset = HTTPS.length();
        }
        if (startIndex == -1) {
            throw new IllegalArgumentException("url format is wrong!");
        }
        int endIndex = url.lastIndexOf(":");
        if (endIndex == -1 || endIndex < startOffset) {
            endIndex = url.length();
        }
        if (url.endsWith("/")) {
            endIndex--;
        }
        return url.substring(startIndex + startOffset, endIndex);
    }

    public static int getPortFromUrl(String url) {
        int startIndex = url.lastIndexOf(":");
        if (startIndex == 4 || startIndex == 5) {
            return 80;
        }
        url = url.substring(startIndex + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = url.length(); i < len; i++) {
            char c = url.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            } else {
                break;
            }
        }
        return Integer.parseInt(sb.toString());
    }

    public static Host getHostNameAndPortFromUrl(String url) {
        return Host.builder()
                .host(getHostNameFromUrl(url))
                .port(getPortFromUrl(url))
                .build();
    }

    public static String urlConcat(String url, String uri) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url)
                .append(uri == null || uri == "" ? "/" : uri);
        return urlBuilder.toString();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Host {

        private String host;

        private int port;

    }

}
