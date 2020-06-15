package com.guguluya.k8s.management;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;

public class DemoNamespace {

    public static void main(String[] args) throws ApiException, IOException {

        initConfiguration();

        listPods();

        listNameSpaces();
    }

    /**
     * 初始化连接
     */
    private static void initConfiguration() {
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
     * Pod 一览
     */
    public static void listPods() throws ApiException {
        // the CoreV1Api loads default api-client from global configuration.
        CoreV1Api api = new CoreV1Api();

        // invokes the CoreV1Api client
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
    }

    /**
     * NameSpace 一览
     */
    public static List<String> listNameSpaces() throws ApiException {
        CoreV1Api api = new CoreV1Api();
        V1NamespaceList listNamespace = api.listNamespace("true", null, null, null, null, 0, null, Integer.MAX_VALUE, Boolean.FALSE);
        List<String> list = listNamespace.getItems().stream().map(v1Namespace -> v1Namespace.getMetadata().getName()).collect(Collectors.toList());
        for (String item : list) {
            System.out.println(item);
        }
        return list;
    }
}
