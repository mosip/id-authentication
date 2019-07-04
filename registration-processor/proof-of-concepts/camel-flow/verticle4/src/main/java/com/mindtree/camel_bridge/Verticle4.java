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
public class Verticle4 extends AbstractVerticle {
	private static String ADDRESS = "verticle4";
	private static String IN_ADRESS = "in";
	private static String OUT_ADDRESS = "out";
	private static String ADDRESS_SEPERATOR= "-"; 
	public static void main(String[] args) {
		VertxOptions options = new VertxOptions().setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(Verticle4.class.getName(),
						new DeploymentOptions().setHa(false).setWorker(true));
			} else {
				System.out.println();
			}
		});
	}
	@Override
		public void start() throws Exception {
		String inAddress=ADDRESS+ADDRESS_SEPERATOR+IN_ADRESS;
		vertx.eventBus().consumer(inAddress, msg->{
			System.out.println("+++++++++++++++Received in Verticle4++++++++++ "+msg.body());
			String flow = ((JsonObject)msg.body()).getString("requestType");
			String outAddress = ADDRESS+ADDRESS_SEPERATOR+flow+ADDRESS_SEPERATOR+OUT_ADDRESS;
			System.out.println("+++++++++++out address++++++++++ "+outAddress);
			vertx.eventBus().send(outAddress, msg.body());
		});
		}
}
