package com.webbdong.gateway.model;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Webb Dong
 * @description: Netty Client 响应数据
 * @date 2021-01-30 12:42 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NettyClientResponseData {

    /**
     * 响应头
     */
    private HttpHeaders responseHeaders;

    /**
     * 数据集合
     */
    private List<ByteBuf> byteBufList = new ArrayList<>();

    /**
     * 响应码
     */
    private int statusCode;

    /**
     * 数据总字节数
     */
    private int totalBytesLength;

    public void addLength(int length) {
        totalBytesLength += length;
    }

}
