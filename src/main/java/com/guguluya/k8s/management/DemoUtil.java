package com.guguluya.k8s.management;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.ClientBuilder;

public class DemoUtil {

    /**
     * 初始化连接
     */
    public static void initConfiguration() {
        // token 方式连接
//        ApiClient client = new ClientBuilder()
//                .setBasePath("ApiServer地址")
//                .setVerifyingSsl(false)
//                .setAuthentication(new AccessTokenAuthentication("Token"))
//                .build();

        // 本地连接
        ApiClient client = new ClientBuilder().setBasePath("http://localhost:8001/").setVerifyingSsl(false).build();

        Configuration.setDefaultApiClient(client);
    }

    /**
     * 调用 request Get 请求
     */
    public static String sendGetRequest(String url) throws Exception {

        URL obj = new URL(url);
        System.out.println("Request URL: " + url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer result = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
        }
        in.close();

        return result.toString();
    }
}
