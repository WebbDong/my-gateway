package com.webbdong.gateway.router;

/**
 * @author Webb Dong
 * @description: 路由接口
 * @date 2021-01-27 6:45 PM
 */
public interface Router {

    /**
     * 路由
     * @param requestUrl
     * @return
     */
    String route(String requestUrl);

}
