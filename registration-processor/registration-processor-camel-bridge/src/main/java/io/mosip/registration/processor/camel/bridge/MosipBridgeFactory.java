package io.mosip.registration.processor.camel.bridge;

import java.util.concurrent.ExecutionException;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.camel.bridge.util.BridgeUtil;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

/**
 * This class provides
 *
 * @author Mukul Puspam
 *
 */
public class MosipBridgeFactory {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MosipBridgeFactory.class);
	

	private MosipBridgeFactory() {

	}

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws ExecutionException
	 *             the execution exception
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
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"failed : ",vertx.cause().toString());
			}
		});
	}
}
