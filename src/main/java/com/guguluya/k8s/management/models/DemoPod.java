package com.guguluya.k8s.management.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guguluya.k8s.management.DemoUtil;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;

public class DemoPod {

    /**
     * 获取所有 NameSpace 的 Pod 一览
     */
    public static V1PodList listPod() throws ApiException {
        CoreV1Api api = new CoreV1Api();
        return api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
    }

    /**
     * <pre>
     * 获取指定 Node 上的 Pod
     * 
     * GET: http://localhost:8001/api/v1/nodes/127.0.0.1/proxy/pods
     * </pre>
     */
    public static List<String> listPodsInNode(String url, String nodeName) throws Exception {

        // url: http://localhost:8001
        // nodeName: 127.0.0.1
        String requestResult = DemoUtil.sendGetRequest(url + "/api/v1/nodes/" + nodeName + "/proxy/pods");

        // 获取 Node 中的 pod 一览
        List<String> pods = new ArrayList<>();
        JsonElement je = new JsonParser().parse(requestResult);
        JsonObject root = je.getAsJsonObject();
        JsonArray items = root.get("items").getAsJsonArray();
        items.forEach(item -> {
            JsonObject elem = item.getAsJsonObject();
            JsonObject metadata = elem.get("metadata").getAsJsonObject();
            String name = metadata.get("name").getAsString();
            pods.add(name);
        });
        return pods;
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
        System.out.println("获取所有 NameSpace 的 Pod 一览:");
        for (V1Pod item : list.getItems()) {
            System.out.println("\t" + item.getMetadata().getName());
        }
    }
}
