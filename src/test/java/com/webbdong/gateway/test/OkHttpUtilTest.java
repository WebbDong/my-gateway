package com.webbdong.gateway.test;

import com.webbdong.gateway.util.OkHttpClientUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Webb Dong
 * @description:
 * @date 2021-01-27 1:33 AM
 */
public class OkHttpUtilTest {

    public static void main(String[] args) {
        OkHttpClient client = OkHttpClientUtil.getInstance();

        Request request = new Request.Builder()
                .get()
                .url("http://localhost:8085")
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
