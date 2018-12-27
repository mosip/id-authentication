package io.mosip.registration.processor.camel.bridge;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import io.mosip.registration.processor.camel.bridge.util.PropertyFileUtil;
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

		String configServerUri = PropertyFileUtil.getProperty(MosipBridgeFactory.class, "bootstrap.properties", "vertx.ignite.configuration");
		URL url = null;
		try {
			url = new URL(configServerUri);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
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
				log.error("Failed: " + vertx.cause());
			}
		});
	}
}
