package com.guguluya.k8s.management.test0703;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guguluya.k8s.management.DemoUtil;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ContainerBuilder;
import io.kubernetes.client.openapi.models.V1ContainerPortBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentBuilder;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.openapi.models.V1ServicePort;

public class DeployResource {

    public static void main(String[] args) throws ApiException {
        
        // 初始化连接
        DemoUtil.initConfiguration();
        
        // 部署 Deployment
        Map<String, String> deploymentMetadataLabel = new HashMap<>();
        deploymentMetadataLabel.put("app", "nginx");
        
        deployDeployment("default", "nginx-deployment-test", deploymentMetadataLabel, 1, "nginx:1.14.2", 80);
        
        // 部署 Service
        Map<String, String> serviceMetadataLabels = new HashMap<>();
        serviceMetadataLabels.put("app", "nginx-service-test");
        
        Map<String, String> serviceSelectors = new HashMap<>();
        serviceSelectors.put("app", "nginx");
        
        List<V1ServicePort> servicePort = buildServicePort(80, 80, 30000);
        
        deployService("default", "nginx-service-test", serviceMetadataLabels,"NodePort",serviceSelectors, servicePort);
    }

    public static void deployDeployment(String namespace,String deploymentName, Map<String, String> metadataLabels, int replicas, String containerImage, int containerPort) throws ApiException {

        V1Deployment deploymentBody = new V1DeploymentBuilder()
                // apiVersion
                .withApiVersion("apps/v1")
                // kind
                .withKind("Deployment")
                // metadata
                .withNewMetadata()
                .withName(deploymentName)
                .withLabels(metadataLabels)
                .endMetadata()
                // spec
                .withNewSpec()
                    .withNewSelector().addToMatchLabels("app", "nginx").endSelector()
                    .withReplicas(replicas)
                    .withNewTemplate()
                        .withNewMetadata().withLabels(metadataLabels).endMetadata()
                        .withNewSpec().withContainers(new V1ContainerBuilder().withName("nginx").withImage(containerImage)
                        .withPorts(new V1ContainerPortBuilder().withContainerPort(containerPort).build()).build())
                        .endSpec()
                    .endTemplate()
                .endSpec().build();

        AppsV1Api api = new AppsV1Api();
        V1Deployment createResult = api.createNamespacedDeployment(namespace, deploymentBody, null, null, null);

        System.out.println(createResult);
    }
    
    public static void deployService(String namespace,String serviceName,Map<String, String> metadataLabels, String serviceType,Map<String, String> selector,List<V1ServicePort> servicePort) throws ApiException {
        V1Service serviceBody = new V1ServiceBuilder()
                .withApiVersion("v1")
                .withKind("Service")
                .withNewMetadata()
                .withName(serviceName)
                .withLabels(metadataLabels)
                .endMetadata()
                .withNewSpec()
                .withType(serviceType)
                .withSelector(selector)
                .withPorts(servicePort)
                .endSpec()
                .build();
        
        CoreV1Api api = new CoreV1Api();
        V1Service createResult = api.createNamespacedService(namespace, serviceBody, null, null, null);

        System.out.println(createResult);   
    }
    
    public static List<V1ServicePort> buildServicePort(int containerPort, int targetPort, int nodePort){
        V1ServicePort servicePort = new V1ServicePort();
        servicePort.setProtocol("TCP");
        servicePort.setPort(containerPort);
        servicePort.setTargetPort(new IntOrString(targetPort));
        servicePort.setNodePort(nodePort);
        List<V1ServicePort> list = new ArrayList<>();
        list.add(servicePort);
        return list;
    }
}
