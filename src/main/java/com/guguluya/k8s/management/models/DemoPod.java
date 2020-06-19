package com.guguluya.k8s.management.models;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;

public class DemoPod {

    /**
     * 获取 Pod 一览
     */
    public static V1PodList listPod() throws ApiException {
        CoreV1Api api = new CoreV1Api();
        return api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
    }

    /**
     * 获取指定 Pod 的日志
     * 
     * kubectl logs <Pod>
     */
    public static void readPodLog() throws ApiException {
        CoreV1Api api = new CoreV1Api();

        String podName = "coredns-6567db4fff-xz7dj";
        String namespace = "kube-system";
        String log = api.readNamespacedPodLog(podName, namespace, null, null, null, null, null, null, null, null);
        System.out.println("Pod 日志:");
        System.out.println(log);
    }

    /**
     * 打印 Pod 一览
     */
    public static void printPods(V1PodList list) {
        System.out.println("Pod 一览:");
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
    }
}
