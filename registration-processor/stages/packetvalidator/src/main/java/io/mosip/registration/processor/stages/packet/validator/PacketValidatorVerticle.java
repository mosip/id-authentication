/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

/**
 * @author M1022006
 *
 */
public class PacketValidatorVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(PacketValidatorVerticle.class);

	public static void main(String args[]) {
		ClusterManager mgr = new IgniteClusterManager();
		VertxOptions options = new VertxOptions().setClusterManager(mgr).setHAEnabled(true).setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(PacketValidatorVerticle.class.getName(),
						new DeploymentOptions().setHa(true));
			} else
				log.error("Failed: " + vertx.cause());
		});
	}

	@Override
	public void start() {

	}

	private void process() {

	}

}
