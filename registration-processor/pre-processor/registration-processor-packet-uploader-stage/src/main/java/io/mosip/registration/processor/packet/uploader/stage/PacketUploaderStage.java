package io.mosip.registration.processor.packet.uploader.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.uploader.service.PacketUploaderService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * The Class PacketUploaderStage.
 *
 * @author Rishabh Keshari
 */
@Component
public class PacketUploaderStage extends MosipVerticleAPIManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketUploaderStage.class);

	/** The cluster url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** server port number. */
	@Value("${server.port}")
	private String port;

	/**
	 * The mosip event bus.
	 */
	private MosipEventBus mosipEventBus;

	/** Mosip router for APIs */
	private Router router;
	/** The context path. */
	@Value("${server.servlet.path}")
	private String contextPath;

	/** The packet uploader service. */
	@Autowired
	PacketUploaderService<MessageDTO> packetUploaderService;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}

	/**
	 * contains all the routes in this stage
	 * 
	 * @param router
	 */
	private void routes(Router router) {
		router.post(contextPath + "/securezone").blockingHandler(this::processURL, false).failureHandler(this::failure);

		router.get(contextPath + "/securezone/health").handler(this::health);

	}

	/**
	 * This is for failure handler
	 *
	 * @param routingContext
	 */
	private void failure(RoutingContext routingContext) {
		this.setResponse(routingContext, routingContext.failure().getMessage());
	}

	/**
	 * This is for health check up
	 *
	 * @param routingContext
	 */
	private void health(RoutingContext routingContext) {
		this.setResponse(routingContext, "Server is up and running");
	}

	/**
	 * method to process the context received.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void processURL(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setInternalError(Boolean.FALSE);
		messageDTO.setIsValid(obj.getBoolean("isValid"));
		messageDTO.setRid(obj.getString("rid"));
		messageDTO = packetUploaderService.validateAndUploadPacket(messageDTO.getRid(),
				this.getClass().getSimpleName());
		if (messageDTO.getIsValid()) {
			sendMessage(messageDTO);
			this.setResponse(ctx, "Packet with registrationId '" + obj.getString("rid")
					+ "' has been forwarded to Packet validation stage");
			regProcLogger.info(obj.getString("rid"), "Packet with registrationId '" + obj.getString("rid")
					+ "' has been forwarded to Packet validation stage", null, null);
		} else {
			this.setResponse(ctx,
					"Packet with registrationId '" + obj.getString("rid") + "' has not been uploaded to file System");
			regProcLogger.info(obj.getString("rid"),
					"Packet with registrationId '" + obj.getString("rid") + "' has not been uploaded to file System",
					null, null);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}

	/**
	 * sends messageDTO to camel bridge.
	 *
	 * @param messageDTO
	 *            the message DTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.PACKET_UPLOADER_OUT, messageDTO);
	}

}
