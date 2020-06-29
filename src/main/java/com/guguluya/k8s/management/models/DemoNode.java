package com.guguluya.k8s.management.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.guguluya.k8s.management.DemoUtil;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;

public class DemoNode {

    /**
     * 为指定 Node 更新/添加 label
     */
    public static void updateNodeLabel(String nodeName, String labelKey, String labelValue) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        V1NodeList nodeList = api.listNode(null, null, null, null, null, null, null, null, null);
        Optional<V1Node> optionalNode = nodeList.getItems().stream().filter(node -> nodeName.equals(node.getMetadata().getName())).findFirst();
        if (optionalNode.isPresent()) {
            V1Node node = optionalNode.get();
            Map<String, String> labels = node.getMetadata().getLabels();
            labels.put(labelKey, labelValue);
            api.replaceNode(nodeName, node, null, null, null);
            System.out.println("指定 node 的 lable 已经更新!");
        } else {
            System.err.println("指定的 node 不存在!");
        }
    }

    /**
     * 获取 Node 一览(根据集群的 Pod 信息)
     */
    public static List<String> listNodes() throws Exception {
        CoreV1Api api = new CoreV1Api();

        Set<String> nodes = new HashSet<>();
        List<String> nameSpaces = DemoNamespace.listNameSpace();
        for (String namespace : nameSpaces) {
            V1PodList podList = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
            for (V1Pod item : podList.getItems()) {
                nodes.add(item.getSpec().getNodeName());
            }
        }

        return new ArrayList<String>(nodes);
    }

    /**
     * <pre>
     * 获取指定 Node 的 Info
     * 
     * kubectl describe node 127.0.0.1 --v=9
     * GET: https://localhost:6443/api/v1/nodes/127.0.0.1
     * GET: https://localhost:6443/api/v1/pods?fieldSelector=spec.nodeName=127.0.0.1,status.phase!=Failed,status.phase!=Succeeded
     * GET: https://localhost:6443/api/v1/events?fieldSelector=involvedObject.uid=127.0.0.1,involvedObject.name=127.0.0.1,involvedObject.namespace=,involvedObject.kind=Node
     * </pre>
     */
    public static String getNodeInfo(String url, String nodeName) throws Exception {

        // url: http://localhost:8001
        // nodeName: 127.0.0.1
        return DemoUtil.sendGetRequest(url + "/api/v1/nodes/" + nodeName);
    }

    /**
     * 获取所有的 Node 和 Pod 的 mapping
     */
    public static Map<String, Set<String>> getAllNodeAndPodMapping() throws ApiException {
        CoreV1Api api = new CoreV1Api();

        Map<String, Set<String>> nodePodMapping = new HashMap<>();
        List<String> nameSpaces = DemoNamespace.listNameSpace();
        for (String namespace : nameSpaces) {
            V1PodList podList = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
            for (V1Pod item : podList.getItems()) {
                String nodeName = item.getSpec().getNodeName();
                String podName = item.getMetadata().getName();
                Set<String> podNames = nodePodMapping.getOrDefault(nodeName, new HashSet<String>());
                podNames.add(podName);
                nodePodMapping.put(nodeName, podNames);
            }
        }

        return nodePodMapping;
    }

    public static void printAllNodeAndPodMapping(Map<String, Set<String>> nodePodMapping) {
        System.out.println("Node and Pod mapping:");
        for (Entry<String, Set<String>> entry : nodePodMapping.entrySet()) {
            System.out.println("\t" + entry.getKey());
            entry.getValue().stream().forEach(s -> System.out.println("\t\t" + s));
        }
    }

    /**
     * 打印 Node 一览
     */
    public static void printNodes(List<String> list) {
        System.out.println("Node 一览:");
        for (String item : list) {
            System.out.println("\t" + item);
        }
    }

    /**
     * 打印 NodeInfo
     */
    public static void printNodeInfo(String nodeInfo) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = new JsonParser().parse(nodeInfo);
        System.out.println("nodeInfo:\n" + gson.toJson(je));
    }
}
