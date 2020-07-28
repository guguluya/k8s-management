package com.guguluya.k8s.management.joey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guguluya.k8s.management.DemoUtil;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapVolumeSource;
import io.kubernetes.client.openapi.models.V1ConfigMapVolumeSourceBuilder;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentBuilder;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentSpecBuilder;
import io.kubernetes.client.openapi.models.V1HostPathVolumeSource;
import io.kubernetes.client.openapi.models.V1HostPathVolumeSourceBuilder;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LabelSelectorBuilder;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ObjectMetaBuilder;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodSpecBuilder;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpecBuilder;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeBuilder;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.openapi.models.V1VolumeMountBuilder;
import io.kubernetes.client.util.Config;

public class TestVolume {

	public static void main(String[] args) throws ApiException, IOException {
		DemoUtil.initConfiguration();
		createConfigMap();
		createDeployment();
	}

	private static void createConfigMap() throws ApiException, IOException {
		V1ConfigMap body = new V1ConfigMap();
		body.apiVersion("v1");
		body.kind("ConfigMap");
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("test-volume-config-map.txt", "this is a test text for config map...");
		body.data(data);

		V1ObjectMeta metadata = new V1ObjectMeta();
		metadata.setName("test-config-map3");
		body.setMetadata(metadata);

		ApiClient client = Config.defaultClient();
		CoreV1Api api = new CoreV1Api(client);
		api.replaceNamespacedConfigMap("test-config-map3", "default", body, null, null, null);
	}

	private static void createDeployment() throws ApiException, IOException {
		Map<String, String> labels = new HashMap<>();
		labels.put("k8s-app", "test-deployment3");
		V1ObjectMeta metadata = new V1ObjectMetaBuilder().withName("test-deployment3").withLabels(labels).build();

		List<String> argsList = new ArrayList<String>();
		argsList.add("/bin/sh");
		argsList.add("-c");
		argsList.add("while true; do echo `cat /home/volume/host-path/test-volume-host-path.txt`; echo `cat /home/volume/config-map/test-volume-config-map.txt`; sleep 3; done");
		List<V1VolumeMount> volumeMountList = new ArrayList<V1VolumeMount>();
		V1VolumeMount volumeMount = new V1VolumeMountBuilder().withName("test-host-path")
				.withMountPath("/home/volume/host-path").build();
		volumeMountList.add(volumeMount);
		volumeMount = new V1VolumeMountBuilder().withName("test-config-map")
				.withMountPath("/home/volume/config-map").build();
		volumeMountList.add(volumeMount);

		V1Container container = new V1ContainerBuilder().withName("buybox").withImage("busybox").withArgs(argsList)
				.withVolumeMounts(volumeMountList).build();
		List<V1Volume> volumeList = new ArrayList<V1Volume>();
		V1HostPathVolumeSource hostPath = new V1HostPathVolumeSourceBuilder()
				.withPath("/C/03Develpment/01General/workspace/k8s-management/src/main/resources/")
				.withType("Directory").build();
		V1Volume volume = new V1VolumeBuilder().withName("test-host-path").withHostPath(hostPath).build();
		volumeList.add(volume);
		V1ConfigMapVolumeSource configMap = new V1ConfigMapVolumeSourceBuilder().withName("test-config-map3").build();
		volume = new V1VolumeBuilder().withName("test-config-map").withConfigMap(configMap).build();
		volumeList.add(volume);

		V1LabelSelector selector = new V1LabelSelectorBuilder().withMatchLabels(labels).build();
		V1PodSpec podSpec = new V1PodSpecBuilder().withContainers(container).withVolumes(volumeList).build();
		V1PodTemplateSpec template = new V1PodTemplateSpecBuilder().withMetadata(metadata).withSpec(podSpec).build();
		V1DeploymentSpec deploymentSpec = new V1DeploymentSpecBuilder().withSelector(selector).withReplicas(1)
				.withTemplate(template).build();

		V1Deployment body = new V1DeploymentBuilder().withApiVersion("apps/v1").withKind("Deployment")
				.withMetadata(metadata).withSpec(deploymentSpec).build();
		ApiClient client = Config.defaultClient();
		client.setDebugging(true);
		AppsV1Api api = new AppsV1Api(client);
		api.replaceNamespacedDeployment("test-deployment3", "default", body, null, null, null);
	}

}
