package io.mosip.registration.processor.camel.bridge;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.camel.bridge.util.BridgeUtil;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.camel.bridge.util.PropertyFileUtil;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

/**
 * This class provides.
 *
 * @author Mukul Puspam
 */
public class MosipBridgeFactory {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MosipBridgeFactory.class);


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

		String configServerUri = PropertyFileUtil.getProperty(MosipBridgeFactory.class, "bootstrap.properties", "vertx.ignite.configuration");
		URL url = null;
		try {
			url = new URL(configServerUri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		ClusterManager clusterManager = new IgniteClusterManager(url);
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
