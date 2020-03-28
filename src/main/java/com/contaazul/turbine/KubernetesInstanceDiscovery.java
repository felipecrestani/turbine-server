package com.contaazul.turbine;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.discovery.InstanceDiscovery;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

@Component
public class KubernetesInstanceDiscovery implements InstanceDiscovery {

	public static final String POD_HASH_LABEL_KEY = "pod-template-hash";
	private final KubernetesClient client;
	Logger logger = LoggerFactory.getLogger( KubernetesInstanceDiscovery.class );

	public KubernetesInstanceDiscovery() {
		this.client = new DefaultKubernetesClient();
	}

	private static String extractClusterNameFor(Pod pod) {
		String podBaseName = pod.getMetadata().getGenerateName();
		// Remove auto-generated hashes, if there are any
		if (pod.getMetadata().getLabels() != null && pod.getMetadata().getLabels().containsKey( POD_HASH_LABEL_KEY )) {
			String hash = pod.getMetadata().getLabels().get( POD_HASH_LABEL_KEY );
			podBaseName = podBaseName.replace( hash + "-", "" );
		}
		// Pod's base names always end with a '-', remove it
		return podBaseName.substring( 0, podBaseName.length() - 1 );

	}

	@Override
	public Collection<Instance> getInstanceList() {
		var list = client.pods().inNamespace( "default" )
				.list()
				.getItems().stream()
				.filter( pod -> pod.getMetadata().getAnnotations() != null )  // Ignore pods without annotations
				.map( pod -> {
					String host = pod.getStatus().getPodIP();
					boolean running = pod.getStatus().getPhase().equals( "Running" );
					var i = new Instance( host, extractClusterNameFor( pod ), running ); //
					logger.info( i.toString() );
					return i;
				} )
				.filter( Objects::nonNull )
				.collect( Collectors.toList() );

		return list;
	}
}
