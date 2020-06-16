package com.guguluya.k8s.management;

import java.io.IOException;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;

public class DemoPodLog {

    public static void main(String[] args) throws ApiException, IOException {

        // 初始化连接
        DemoUtil.initConfiguration();

        readPodLog();
    }

    /**
     * Pod 日志
     * 
     * kubectl logs <POD>
     */
    public static void readPodLog() throws ApiException {
        // the CoreV1Api loads default api-client from global configuration.
        CoreV1Api api = new CoreV1Api();

        // invokes the CoreV1Api client
        String podName = "coredns-6567db4fff-xz7dj";
        String namespace = "kube-system";
        String log = api.readNamespacedPodLog(podName, namespace, null, null, null, null, null, null, null, null);
        System.out.println(log);
    }
}
