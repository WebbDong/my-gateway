package com.webbdong.gateway.util;

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
        int endIndex = url.length();
        if (url.endsWith("/")) {
            endIndex--;
        }
        return url.substring(startIndex + startOffset, endIndex);
    }

    public static String urlConcat(String url, String uri) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url)
                .append(uri == null || uri == "" ? "/" : uri);
        return urlBuilder.toString();
    }

}
