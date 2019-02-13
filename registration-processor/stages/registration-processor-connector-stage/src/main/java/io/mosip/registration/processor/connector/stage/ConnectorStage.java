package io.mosip.registration.processor.connector.stage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@Service
public class ConnectorStage extends MosipVerticleAPIManager {
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ConnectorStage.class);
	
	/**
	 * vertx Cluster Manager Url
	 */
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/**
	 * server port number
	 */
	@Value("${server.port}")
	private String port;
	
	/**
	 * The mosip event bus.
	 */
	private MosipEventBus mosipEventBus;

	/**
	 * deploys this verticle
	 */
	public void deployVerticle() {
		
		this.mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 * 
	 */
	
	
	public void start() {
		Router router = this.postUrl(this.mosipEventBus.getEventbus());
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}
	private void routes(Router router) {
		router.post("/registrationconnector/v0.1/registration-processor/connector").handler(ctx -> {
			processURL(ctx);
		}).failureHandler(failureHandler -> {
			this.setResponse(failureHandler, failureHandler.failure().getMessage());
		});
		
		router.get("/registrationconnector/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
		}).failureHandler(context->{
			this.setResponse(context, context.failure().getMessage());
		});
		
	}

	public void processURL(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();
		
		MessageDTO messageDTO= new MessageDTO();
		messageDTO.setInternalError(Boolean.valueOf(obj.getString("internalError")));
		messageDTO.setIsValid(Boolean.valueOf(obj.getString("isValid")));
		messageDTO.setRid(obj.getString("rid"));
		sendMessage( messageDTO);
		this.setResponse(ctx, "Packet with registrationId '"+obj.getString("rid")+"' has been forwarded to Packet validation stage");
	}

	/**
	 * sends messageDTO to camel bridge
	 * 
	 * @param messageDTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.PACKET_VALIDATOR_BUS_IN, messageDTO);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}

}
