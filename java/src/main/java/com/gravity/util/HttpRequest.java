package com.gravity.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright (C), 2017, 黑曜石
 *
 * @author levi
 * @version 0.0.1
 * @desc http 请求util
 * @date 2017-09-08 09:31:48
 */
public class HttpRequest {
    private static Logger LOGGER = Logger.getLogger(HttpRequest.class);

    private static PoolingHttpClientConnectionManager cm = null;

    static {
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("创建SSL连接失败");
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("https", sslsf)
            .register("http", new PlainConnectionSocketFactory())
            .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
    }

    private static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();
        return httpClient;
    }

    /**
     * 高效Post请求
     * @param url
     * @param data
     * @return
     */
    public static String post(String url, String data) {
        // 创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpRequest.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost post = new HttpPost(url);
            Charset charset = Charset.forName("UTF-8");
            StringEntity entity = new StringEntity(data, charset);
            entity.setContentEncoding(charset.name());
            entity.setContentType("application/json");
            post.setEntity(entity);

            httpResponse = httpClient.execute(post);
            // response实体
            HttpEntity httpEntity = httpResponse.getEntity();
            if (null != httpEntity) {
                String response = EntityUtils.toString(httpEntity);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                LOGGER.info("当前请求返回码: " + statusCode);
                if (statusCode == HttpStatus.SC_OK) {
                    // 成功
                    return response;
                } else {
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            LOGGER.info("httpclient请求失败" + e);
            return null;
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.info("关闭response失败" + e);
                }
            }
        }
    }
}
