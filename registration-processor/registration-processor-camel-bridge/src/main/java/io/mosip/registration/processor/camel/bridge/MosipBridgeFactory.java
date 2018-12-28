package io.mosip.registration.processor.camel.bridge;

import java.util.concurrent.ExecutionException;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import io.mosip.registration.processor.camel.bridge.util.BridgeUtil;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

/**
 * This class provides.
 *
 * @author Mukul Puspam
 */
public class MosipBridgeFactory {

	/** The log. */
	static Logger log = LoggerFactory.getLogger(MosipBridgeFactory.class);

	/**
	 * Instantiates a new mosip bridge factory.
	 */
	private MosipBridgeFactory() {

	}

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public static void getEventBus() {

		TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
		tcpDiscoveryVmIpFinder.setAddresses(BridgeUtil.getIpAddressPortRange());
		tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
		IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
		igniteConfiguration.setLocalHost(BridgeUtil.getLocalHost());
		igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
		ClusterManager clusterManager = new IgniteClusterManager(igniteConfiguration);
		VertxOptions options = new VertxOptions().setClusterManager(clusterManager).setHAEnabled(true)
				.setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(MosipCamelBridge.class.getName(),
						new DeploymentOptions().setHa(true).setWorker(true));
			} else {
				log.error("Failed: " + vertx.cause());
			}
		});
	}
}
