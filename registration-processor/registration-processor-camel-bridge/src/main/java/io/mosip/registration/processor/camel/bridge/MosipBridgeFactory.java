package io.mosip.registration.processor.camel.bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

/**
 * This class provides
 * 
 * @author Mukul Puspam
 *
 */
public class MosipBridgeFactory {

	static Logger log = LoggerFactory.getLogger(MosipBridgeFactory.class);

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static void getEventBus() throws InterruptedException, ExecutionException {

		List<String> addressList = new ArrayList<>();
		addressList.add("127.0.0.1:47500..47549");
		TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
		tcpDiscoveryVmIpFinder.setAddresses(addressList);
		tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
		IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
		igniteConfiguration.setLocalHost("127.0.0.1");
		igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
		ClusterManager clusterManager = new IgniteClusterManager(igniteConfiguration);
		VertxOptions options = new VertxOptions().setClusterManager(clusterManager).setHAEnabled(true)
				.setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(MosipCamelBridge.class.getName(),
						new DeploymentOptions().setHa(true).setWorker(true));
			} else
				log.error("Failed: " + vertx.cause());
		});
	}
}
