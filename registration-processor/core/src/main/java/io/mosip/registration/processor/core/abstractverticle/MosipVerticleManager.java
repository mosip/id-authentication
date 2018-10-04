package io.mosip.registration.processor.core.abstractverticle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.mosip.registration.processor.core.spi.eventbus.EventBusManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

public abstract class MosipVerticleManager extends AbstractVerticle
		implements EventBusManager<MosipEventBus, MessageBusAddress> {

	@Override
	public MosipEventBus getEventBus(Class<?> verticleName) {
		CompletableFuture<Vertx> eventBus = new CompletableFuture<>();
		MosipEventBus mosipEventBus = null;
		ClusterManager clusterManager = new IgniteClusterManager();
		VertxOptions options = new VertxOptions().setClustered(true).setClusterManager(clusterManager)
				.setHAEnabled(true);
		Vertx.clusteredVertx(options, result -> {
			if (result.succeeded()) {
				result.result().deployVerticle(verticleName.getName(), new DeploymentOptions().setHa(true));
				eventBus.complete(result.result());
			}
		});
		try {
			mosipEventBus = new MosipEventBus(eventBus.get());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mosipEventBus;
	}
	
	@Override
	public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress, MessageBusAddress toAddress) {
		Vertx vertx = mosipEventBus.getEventbus();
		vertx.eventBus().consumer(fromAddress.getAddress(), msg->{
			vertx.executeBlocking(future->{
				Object result = process(msg.body());
				future.complete();
				send(mosipEventBus, toAddress, result);
			}, res->{
				if(!res.succeeded()) {
					System.out.println("failure "+res.cause());
				}
			});
		});
	}
	
	public void send(MosipEventBus mosipEventBus,MessageBusAddress toAddress, Object message) {
		Vertx vertx = mosipEventBus.getEventbus();
		vertx.eventBus().send(toAddress.getAddress(), message);
	}
	
	public void consume(MosipEventBus mosipEventBus, MessageBusAddress fromAddress) {
		Vertx vertx = mosipEventBus.getEventbus();
		vertx.eventBus().consumer(fromAddress.getAddress(), message->{
			vertx.executeBlocking(future->{
				process(message.body());
				future.complete();
			}, res->{
				if(!res.succeeded()) {
					System.out.println("failure "+res.cause());
				}
			});
		});
	}

}
