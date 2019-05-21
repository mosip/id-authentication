package com.mindtree.camel_bridge;

import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

/**
 * Hello world!
 *
 */
@Component
public class Verticle1 extends AbstractVerticle {
	private static String ADDRESS = "verticle1";
	private static String OUT_ADDRESS = "out";
	private static String ADDRESS_SEPERATOR = "-";
	private Vertx vertx;
	
	public void getEventBus() {
		VertxOptions options = new VertxOptions().setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(Verticle1.class.getName(),
						new DeploymentOptions().setHa(false).setWorker(true));
				this.vertx = vertx.result();
			} else {
				System.out.println("");
			}
		});
	}
	
	public void sendMessage(JsonObject jsonObject){
		String flow = jsonObject.getString("requestType");
		String address = ADDRESS + ADDRESS_SEPERATOR + flow + ADDRESS_SEPERATOR + OUT_ADDRESS;
		System.out.println("+++++++++++out address++++++++++ "+address);
		vertx.eventBus().send(address, jsonObject);
	}
}
