package com.webbdong.gateway.forward;

import com.webbdong.gateway.util.ApacheHttpClientUtil;
import com.webbdong.gateway.util.UriUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Webb Dong
 * @description: 使用 HttpClient 转发
 * @date 2021-01-28 6:40 PM
 */
@Slf4j
public class ApacheHttpClientForwarder implements Forwarder {

    @Override
    public FullHttpResponse forward(String forwardUrl, FullHttpRequest fullRequest) {
        log.info("forwardUrl: {}", forwardUrl);
        CloseableHttpClient client = ApacheHttpClientUtil.getInstance();

        HttpGet httpGet = new HttpGet(UriUtil.urlConcat(forwardUrl, fullRequest.uri()));
        fullRequest.headers().forEach(entry -> httpGet.setHeader(entry.getKey(), entry.getValue()));
        httpGet.setHeader("Host", UriUtil.getHostNameFromUrl(forwardUrl));

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatusLine().getStatusCode()),
                    Unpooled.wrappedBuffer(EntityUtils.toByteArray(response.getEntity())));
            Arrays.stream(response.getAllHeaders())
                    .forEach(h -> httpResponse.headers().set(h.getName(), h.getValue()));
            return httpResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
