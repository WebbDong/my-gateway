package com.webbdong.gateway.util;

/**
 * @author Webb Dong
 * @description: UriUtil
 * @date 2021-01-27 9:21 PM
 */
public class UriUtil {

    public static final String WILDCARD = "*";

    public static String removeWildcard(String uri) {
        if (uri.endsWith(WILDCARD)) {
            return uri.substring(0, uri.length() - 2);
        }
        return uri;
    }

}
