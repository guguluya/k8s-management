package com.guguluya.k8s.management.models;

import java.util.List;
import java.util.stream.Collectors;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;

public class DemoNamespace {

    /**
     * 获取 NameSpace 一览
     */
    public static List<String> listNameSpace() throws ApiException {
        CoreV1Api api = new CoreV1Api();
        V1NamespaceList listNamespace = api.listNamespace("true", null, null, null, null, 0, null, Integer.MAX_VALUE, Boolean.FALSE);
        return listNamespace.getItems().stream().map(v1Namespace -> v1Namespace.getMetadata().getName()).collect(Collectors.toList());
    }

    /**
     * 打印 NameSpace 一览
     */
    public static void printNameSpaces(List<String> list) {
        System.out.println("NameSpace 一览:");
        for (String item : list) {
            System.out.println("\t" + item);
        }
    }
}
