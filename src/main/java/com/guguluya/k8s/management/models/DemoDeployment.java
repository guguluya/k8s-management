package com.guguluya.k8s.management.models;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1ContainerBuilder;
import io.kubernetes.client.openapi.models.V1ContainerPortBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentBuilder;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.util.Yaml;

public class DemoDeployment {

    /**
     * 获取 Deployment 一览
     */
    public static V1DeploymentList listDeployments() throws ApiException {
        AppsV1Api api = new AppsV1Api();
        return api.listDeploymentForAllNamespaces(Boolean.FALSE, null, null, null, 0, "true", null, Integer.MAX_VALUE, Boolean.FALSE);
    }

    /**
     * <pre>
     * 通过 yaml 文件创建 Deployment
     * 
     * Yaml:
     * apiVersion: apps/v1
     * kind: Deployment
     * metadata:
     *   name: nginx-deployment
     *   labels:
     *     app: nginx
     * spec:
     *   selector:
     *     matchLabels:
     *       app: nginx
     *   replicas: 1
     *   template:
     *     metadata:
     *       labels:
     *         app: nginx
     *     spec:
     *       containers:
     *       - name: nginx
     *         image: nginx:1.14.2
     *         ports:
     *         - containerPort: 80
     * </pre>
     */
    public static void createDeploymentByYaml() throws IOException, ApiException {
        // https://github.com/kubernetes-client/java/blob/master/examples/src/main/java/io/kubernetes/client/examples/YamlExample.java
        Yaml.addModelMap("v1", "Deployment", V1Deployment.class);
        File file = new File("/Users/lzzheng/temp/k8s/sample-nginx-deployment.yaml");
        V1Deployment deployment = (V1Deployment) Yaml.load(file);

        AppsV1Api api = new AppsV1Api();
        V1Deployment createResult = api.createNamespacedDeployment("default", deployment, null, null, null);

        System.out.println(createResult);
    }

    /**
     * 通过 modal 创建 Deployment
     */
    public static void createDeploymentByModal() throws IOException, ApiException {

        Map<String, String> metadataLabels = new HashMap<>();
        metadataLabels.put("app", "nginx");

        V1Deployment deploymentBody = new V1DeploymentBuilder()
                // apiVersion
                .withApiVersion("apps/v1")
                // kind
                .withKind("Deployment")
                // metadata
                .withNewMetadata().withName("sample-nginx-deployment-modal").withLabels(metadataLabels).endMetadata()
                // spec
                .withNewSpec().withNewSelector().addToMatchLabels("app", "nginx").endSelector().withReplicas(1).withNewTemplate().withNewMetadata()
                .withLabels(metadataLabels).endMetadata().withNewSpec().withContainers(new V1ContainerBuilder().withName("nginx").withImage("nginx:1.14.2")
                        .withPorts(new V1ContainerPortBuilder().withContainerPort(80).build()).build())
                .endSpec().endTemplate().endSpec().build();

        AppsV1Api api = new AppsV1Api();
        V1Deployment createResult = api.createNamespacedDeployment("default", deploymentBody, null, null, null);

        System.out.println(createResult);
    }

    /**
     * 打印 Deployment 一览
     */
    public static void printDeployment(V1DeploymentList list) {
        System.out.println("Deployment 一览:");
        for (V1Deployment item : list.getItems()) {
            System.out.println("\t" + item.getMetadata().getName());
        }
    }
}
