package com.guguluya.k8s.management.joey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guguluya.k8s.management.DemoUtil;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapKeySelector;
import io.kubernetes.client.openapi.models.V1ConfigMapKeySelectorBuilder;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerBuilder;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1ContainerPortBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentBuilder;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarBuilder;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1EnvVarSourceBuilder;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.Config;

public class TestConfigMap {

	public static void main(String[] args) throws ApiException, IOException {
		DemoUtil.initConfiguration();
		createConfigMap();
		createDeployment();
	}

	private static void createConfigMap() throws ApiException, IOException {
		ApiClient client = Config.defaultClient();
		Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api(client);

		String namespace = "default";
		V1ConfigMap body = new V1ConfigMap();
		body.apiVersion("v1");
		body.kind("ConfigMap");
		Map<String, String> configMapBody = new HashMap<String, String>();
		configMapBody.put("DEVICE_ID", "A1001");
		body.data(configMapBody);

		V1ObjectMeta metadata = new V1ObjectMeta();
		metadata.setName("test-config-map");
		body.setMetadata(metadata);
		api.createNamespacedConfigMap(namespace, body, null, null, null);
	}

	private static void createDeployment() throws ApiException, IOException {
		Map<String, String> metadataLabels = new HashMap<>();
		metadataLabels.put("app", "test-deployment");
		V1ConfigMapKeySelector configMapKeyRef = new V1ConfigMapKeySelectorBuilder().withName("test-config-map")
				.withKey("DEVICE_ID").build();
		V1EnvVarSource valueFrom = new V1EnvVarSourceBuilder().withConfigMapKeyRef(configMapKeyRef).build();
		V1EnvVar envVar = new V1EnvVarBuilder().withName("DEVICE_ID").withValueFrom(valueFrom).build();
		List<V1EnvVar> envVarList = new ArrayList<V1EnvVar>();
		envVarList.add(envVar);
		V1ContainerPort containerPort = new V1ContainerPortBuilder().withContainerPort(80).build();
		V1Container container = new V1ContainerBuilder().withName("nginx").withImage("nginx:1.14.2").withEnv(envVarList)
				.withPorts(containerPort).build();
		V1Deployment body = new V1DeploymentBuilder().withApiVersion("apps/v1").withKind("Deployment").withNewMetadata()
				.withName("test-deployment").withLabels(metadataLabels).endMetadata().withNewSpec().withNewSelector()
				.addToMatchLabels("app", "test-deployment").endSelector().withReplicas(1).withNewTemplate()
				.withNewMetadata().withLabels(metadataLabels).endMetadata().withNewSpec().withContainers(container)
				.endSpec().endTemplate().endSpec().build();
		ApiClient client = Config.defaultClient();
		AppsV1Api api = new AppsV1Api(client);
		api.createNamespacedDeployment("default", body, null, null, null);
	}
}
