package io.mosip.registration.processor.core.abstractverticle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.mosip.registration.processor.core.bridge.address.MessageBusAddress;
import io.mosip.registration.processor.core.spi.eventbus.EventBusManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

public abstract class MosipAbstractVerticle extends AbstractVerticle
		implements EventBusManager<Vertx, MessageBusAddress> {

	@Override
	public abstract void start();

	@Override
	public void setup(Class<?> verticleName) {
		CompletableFuture<EventBus> eventBus = new CompletableFuture<EventBus>();

		ClusterManager clusterManager = new IgniteClusterManager();
		VertxOptions options = new VertxOptions().setClustered(true).setClusterManager(clusterManager)
				.setHAEnabled(true);

		Vertx.clusteredVertx(options, result -> {
			if (result.succeeded()) {
				result.result().deployVerticle(verticleName.getName(), new DeploymentOptions().setHa(true));
				eventBus.complete(result.result().eventBus());
			}
		});

		try {
			eventBus.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void send(Vertx vertx, MessageBusAddress address, Object object) {
		vertx.eventBus().send(address.toString(), object);
	}

	@Override
	public void consumeAndSend(Vertx vertx, MessageBusAddress fromAddress, MessageBusAddress toAddress) {
		vertx.eventBus().consumer(fromAddress.toString(), msg -> {
			vertx.executeBlocking(future -> {
				Object result = process(msg.body());
				future.complete(result);
				vertx.eventBus().send(toAddress.toString(), result);
			}, res -> {
				if (!res.succeeded()) {
					System.out.println(res.cause());
				}
			});
		});

	}

	public void consume(Vertx vertx, MessageBusAddress fromAddress, Object object) {
		vertx.eventBus().consumer(fromAddress.toString(), msg -> {
			vertx.executeBlocking(future -> {
				Object result = process(msg.body());
				future.complete(result);
			}, res -> {
				if (!res.succeeded()) {
					System.out.println(res.cause());
				}
			});
		});
	}

}
