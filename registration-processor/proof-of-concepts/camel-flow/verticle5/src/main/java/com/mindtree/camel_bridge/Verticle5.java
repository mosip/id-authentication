package com.mindtree.camel_bridge;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

/**
 * Hello world!
 *
 */
public class Verticle5 extends AbstractVerticle {
	private static String ADDRESS = "verticle5";
	private static String IN_ADRESS = "in";
	private static String ADDRESS_SEPERATOR= "-"; 
	public static void main(String[] args) {
		VertxOptions options = new VertxOptions().setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(Verticle5.class.getName(),
						new DeploymentOptions().setHa(false).setWorker(true));
			} else {
		
			}
		});
	}
	@Override
		public void start() throws Exception {
		String inAddress=ADDRESS+ADDRESS_SEPERATOR+IN_ADRESS;
		vertx.eventBus().consumer(inAddress, msg->{
			System.out.println("+++++++++++++++Received in Vertice5++++++++++ "+msg.body());
		});
		}
}
