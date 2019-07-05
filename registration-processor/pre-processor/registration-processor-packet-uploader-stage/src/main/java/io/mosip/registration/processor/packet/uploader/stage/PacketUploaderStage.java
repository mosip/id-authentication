package io.mosip.registration.processor.packet.uploader.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
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

	/** The context path. */
	@Value("${server.servlet.path}")
	private String contextPath;

	/** The packet uploader service. */
	@Autowired
	PacketUploaderService<MessageDTO> packetUploaderService;

	/** Mosip router for APIs */
	@Autowired
	MosipRouter router;
	
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

		router.setRoute(this.postUrl(vertx, null, MessageBusAddress.PACKET_UPLOADER_OUT));
		this.routes(router);
		this.createServer(router.getRouter(), Integer.parseInt(port));
	}

	/**
	 * contains all the routes in this stage
	 *
	 * @param router
	 */
	private void routes(MosipRouter router) {
		router.post(contextPath + "/securezone");
		router.handler(this::processURL, this::failure);

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
	 * method to process the context received.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void processURL(RoutingContext ctx) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				"", "PacketUploaderStage::processURL()::entry");
		JsonObject obj = ctx.getBodyAsJson();

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setMessageBusAddress(MessageBusAddress.PACKET_UPLOADER_IN);
		messageDTO.setInternalError(Boolean.FALSE);
		messageDTO.setIsValid(obj.getBoolean("isValid"));
		messageDTO.setRid(obj.getString("rid"));
		messageDTO = packetUploaderService.validateAndUploadPacket(messageDTO.getRid(),
				this.getClass().getSimpleName());
		if (messageDTO.getIsValid()) {
			sendMessage(messageDTO);
			this.setResponse(ctx, "Packet with registrationId '" + obj.getString("rid")
					+ "' has been forwarded to next stage");
			regProcLogger.info(obj.getString("rid"), "Packet with registrationId '" + obj.getString("rid")
					+ "' has been forwarded to next stage", null, null);
		} else {
			this.setResponse(ctx,
					"Packet with registrationId '" + obj.getString("rid") + "' has not been uploaded to file System");
			regProcLogger.info(obj.getString("rid"),
					"Packet with registrationId '" + obj.getString("rid") + "' has not been uploaded to file System",
					null, null);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				"", "PacketUploaderStage::processURL()::exit");

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

	@Override
	public void stop() {
		packetUploaderService.disconnectSftpConnection();
	}

}
