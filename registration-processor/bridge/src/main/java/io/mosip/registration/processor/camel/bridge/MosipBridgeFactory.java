package io.mosip.registration.processor.camel.bridge;

import java.util.concurrent.ExecutionException;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

public class MosipBridgeFactory {

	static Logger log = LoggerFactory.getLogger(MosipBridgeFactory.class);

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException the execution exception
	 */
	public static void getEventBus() throws InterruptedException, ExecutionException {
		
		ClusterManager mgr = new IgniteClusterManager();
		VertxOptions options = new VertxOptions().setClusterManager(mgr).setHAEnabled(true).setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(MosipCamelBridge.class.getName(),
						new DeploymentOptions().setHa(true).setWorker(true));
			} else
				log.error("Failed: " + vertx.cause());
		});
	}
}
