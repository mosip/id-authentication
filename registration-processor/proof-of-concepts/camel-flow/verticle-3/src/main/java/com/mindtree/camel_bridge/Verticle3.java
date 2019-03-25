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
public class Verticle3 extends AbstractVerticle {
	private static String ADDRESS = "verticle3";
	private static String IN_ADDRESS = "in";
	private static String ADDRESS_SEPERATOR="-";
	private static String OUT_ADDRESS="out";
	public static void main(String[] args) {
		VertxOptions options = new VertxOptions().setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(Verticle3.class.getName(),
						new DeploymentOptions().setHa(false).setWorker(true));
			} else {
				System.out.println();
			}
		});
	}
	@Override
		public void start() throws Exception {
		String inAddress=ADDRESS+ADDRESS_SEPERATOR+IN_ADDRESS;
		vertx.eventBus().consumer(inAddress, msg->{
			System.out.println("+++++++++++++++Received in Vertice3++++++++++ "+msg.body());
			String flow = ((JsonObject)msg.body()).getString("requestType");
			String outAddress = ADDRESS+ADDRESS_SEPERATOR+flow+ADDRESS_SEPERATOR+OUT_ADDRESS;
			System.out.println("+++++++++++out address++++++++++ "+outAddress);
			vertx.eventBus().send(outAddress, msg.body());
		});
		}
}
