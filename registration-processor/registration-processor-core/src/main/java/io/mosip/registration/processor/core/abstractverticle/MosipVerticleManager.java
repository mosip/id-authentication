package io.mosip.registration.processor.core.abstractverticle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;

import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;

import io.mosip.registration.processor.core.exception.DeploymentFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.eventbus.EventBusManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * This abstract class is Vert.x implementation for MOSIP.
 * 
 * This class provides functionalities to be used by MOSIP verticles.
 * 
 * @author Pranav Kumar
 * @author Mukul Puspam
 * @since 0.0.1
 *
 */
public abstract class MosipVerticleManager extends AbstractVerticle
		implements EventBusManager<MosipEventBus, MessageBusAddress, MessageDTO> {

	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(MosipVerticleManager.class);

	@Value("${eventbus.port}")
	private String eventBusPort;
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#getEventBus(java.lang.Class, java.lang.String)
	 */
	@Override
	public MosipEventBus getEventBus(Object verticleName, String clusterManagerUrl) {
		return getEventBus(verticleName, clusterManagerUrl, 1);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#getEventBus
	 * (java.lang.Class)
	 */
	@Override
	public MosipEventBus getEventBus(Object verticleName, String clusterManagerUrl, int instanceNumber) {
		CompletableFuture<Vertx> eventBus = new CompletableFuture<>();
		MosipEventBus mosipEventBus = null;
		Config config;
		try {
			config = new UrlXmlConfig(clusterManagerUrl);
		} catch (IOException e1) {
			throw new DeploymentFailureException(PlatformErrorMessages.RPR_CMB_MALFORMED_URL_EXCEPTION.getMessage());
		}
		ClusterManager clusterManager = new HazelcastClusterManager(config);
		String address = null;
		try {
			address = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			throw new DeploymentFailureException(PlatformErrorMessages.RPR_CMB_MALFORMED_URL_EXCEPTION.getMessage());
		}
		VertxOptions options = new VertxOptions().setClustered(true).setClusterManager(clusterManager)
				.setHAEnabled(false).setWorkerPoolSize(instanceNumber)
				.setEventBusOptions(new EventBusOptions().setPort(getEventBusPort()).setHost(address));
		Vertx.clusteredVertx(options, result -> {
			if (result.succeeded()) {
				result.result().deployVerticle((Verticle) verticleName,
						new DeploymentOptions().setHa(false).setWorker(true).setWorkerPoolSize(instanceNumber));
				eventBus.complete(result.result());
				logger.debug(verticleName + " deployed successfully");
			} else {
				throw new DeploymentFailureException(PlatformErrorMessages.RPR_CMB_DEPLOYMENT_FAILURE.getMessage());
			}
		});

		try {
			mosipEventBus = new MosipEventBus(eventBus.get());
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new DeploymentFailureException(PlatformErrorMessages.RPR_CMB_DEPLOYMENT_FAILURE.getMessage(), e);

		}
		return mosipEventBus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#
	 * consumeAndSend(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
			MessageBusAddress toAddress) {
		Vertx vertx = mosipEventBus.getEventbus();
		vertx.eventBus().consumer(fromAddress.getAddress(), msg -> {
			logger.debug("received from " + fromAddress.toString() + msg.body());
			vertx.executeBlocking(future -> {
				JsonObject jsonObject = (JsonObject) msg.body();
				MessageDTO messageDTO = jsonObject.mapTo(MessageDTO.class);
				MessageDTO result = process(messageDTO);
				future.complete();
				send(mosipEventBus, toAddress, result);
			}, false, res -> {
				if (!res.succeeded()) {
					logger.error("failure " + res.cause());
				}
			});
		});
	}

	/**
	 * Send.
	 *
	 * @param mosipEventBus
	 *            The Eventbus instance for communication
	 * @param toAddress
	 *            The address on which message is to be sent
	 * @param message
	 *            The message that needs to be sent
	 */
	public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		Vertx vertx = mosipEventBus.getEventbus();
		MessageBusAddress messageBusAddress = new MessageBusAddress(toAddress, message.getReg_type());
		JsonObject jsonObject = JsonObject.mapFrom(message);
		vertx.eventBus().send(messageBusAddress.getAddress(), jsonObject);
		logger.debug("sent to " + toAddress.toString() + " message " + jsonObject);
	}

	/**
	 * Consume.
	 *
	 * @param mosipEventBus
	 *            The Eventbus instance for communication
	 * @param fromAddress
	 *            The address from which message needs to be consumed
	 */
	public void consume(MosipEventBus mosipEventBus, MessageBusAddress fromAddress) {
		Vertx vertx = mosipEventBus.getEventbus();
		vertx.eventBus().consumer(fromAddress.getAddress(), message -> {
			logger.debug("received from " + fromAddress.toString() + " message " + message.body());
			vertx.executeBlocking(future -> {
				JsonObject jsonObject = (JsonObject) message.body();
				MessageDTO messageDTO = jsonObject.mapTo(MessageDTO.class);
				process(messageDTO);
				future.complete();
			}, false, res -> {
				if (!res.succeeded()) {
					logger.error("failure " + res.cause());
				}
			});
		});
	}

	public Integer getEventBusPort() {
		return Integer.parseInt(eventBusPort);
	}

}
