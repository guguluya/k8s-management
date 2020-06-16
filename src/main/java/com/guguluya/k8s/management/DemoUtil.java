package com.guguluya.k8s.management;

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
}
