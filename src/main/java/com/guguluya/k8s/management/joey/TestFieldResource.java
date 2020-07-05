package com.guguluya.k8s.management.joey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guguluya.k8s.management.DemoUtil;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentBuilder;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentSpecBuilder;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarBuilder;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1EnvVarSourceBuilder;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LabelSelectorBuilder;
import io.kubernetes.client.openapi.models.V1ObjectFieldSelector;
import io.kubernetes.client.openapi.models.V1ObjectFieldSelectorBuilder;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ObjectMetaBuilder;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodSpecBuilder;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpecBuilder;
import io.kubernetes.client.openapi.models.V1ResourceFieldSelector;
import io.kubernetes.client.openapi.models.V1ResourceFieldSelectorBuilder;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1ResourceRequirementsBuilder;
import io.kubernetes.client.util.Config;

public class TestFieldResource {

	public static void main(String[] args) throws ApiException, IOException {
		DemoUtil.initConfiguration();
		createDeployment();
	}

	private static void createDeployment() throws ApiException, IOException {
		Map<String, String> labels = new HashMap<>();
		labels.put("k8s-app", "test-deployment2");
		V1ObjectMeta metadata = new V1ObjectMetaBuilder().withName("test-deployment2").withLabels(labels).build();

		List<String> argsList = new ArrayList<String>();
		argsList.add("/bin/sh");
		argsList.add("-c");
		argsList.add("printenv TEST_ENV_FIELD; printenv TEST_ENV_SRC; sleep 30000");

		V1ObjectFieldSelector fieldRef = new V1ObjectFieldSelectorBuilder().withFieldPath("spec.nodeName").build();
		V1EnvVarSource valueFrom = new V1EnvVarSourceBuilder().withFieldRef(fieldRef).build();
		List<V1EnvVar> envVarList = new ArrayList<V1EnvVar>();
		V1EnvVar envVar = new V1EnvVarBuilder().withName("TEST_ENV_FIELD").withValueFrom(valueFrom).build();
		envVarList.add(envVar);
		V1ResourceFieldSelector resourceFieldRef = new V1ResourceFieldSelectorBuilder().withContainerName("buybox")
				.withResource("requests.memory").build();
		valueFrom = new V1EnvVarSourceBuilder().withResourceFieldRef(resourceFieldRef).build();
		envVar = new V1EnvVarBuilder().withName("TEST_ENV_SRC").withValueFrom(valueFrom).build();
		envVarList.add(envVar);

		Map<String, Quantity> requests = new HashMap<String, Quantity>();
		Quantity value = new Quantity("0.5");
		requests.put("cpu", value);
		value = new Quantity("1024Mi");
		requests.put("memory", value);
		V1ResourceRequirements resources = new V1ResourceRequirementsBuilder().withRequests(requests).build();
		V1Container container = new V1ContainerBuilder().withName("buybox").withImage("busybox").withArgs(argsList)
				.withResources(resources).withEnv(envVarList).build();

		V1LabelSelector selector = new V1LabelSelectorBuilder().withMatchLabels(labels).build();
		V1PodSpec podSpec = new V1PodSpecBuilder().withContainers(container).build();
		V1PodTemplateSpec template = new V1PodTemplateSpecBuilder().withMetadata(metadata).withSpec(podSpec).build();
		V1DeploymentSpec deploymentSpec = new V1DeploymentSpecBuilder().withSelector(selector).withReplicas(1)
				.withTemplate(template).build();

		V1Deployment body = new V1DeploymentBuilder().withApiVersion("apps/v1").withKind("Deployment")
				.withMetadata(metadata).withSpec(deploymentSpec).build();
		ApiClient client = Config.defaultClient();
		client.setDebugging(true);
		AppsV1Api api = new AppsV1Api(client);
		api.replaceNamespacedDeployment("test-deployment2", "default", body, null, null, null);
	}
}
