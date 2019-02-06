package io.mosip.registration.processor.packet.receiver.stage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.manager.exception.systemexception.UnexpectedException;
import io.mosip.registration.processor.packet.receiver.exception.handler.GlobalExceptionHandler;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * The Class PacketReceiverStage.
 */

@RefreshScope
@Service
public class PacketReceiverStage extends MosipVerticleAPIManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketReceiverStage.class);
	
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
	 * The Packet Receiver Service
	 */
	@Autowired
	public PacketReceiverService<File, MessageDTO> packetReceiverService;

	/**
	 * Exception handler
	 */
	@Autowired
	public GlobalExceptionHandler globalExceptionHandler;
	
	/**
	 * The mosip event bus.
	 */
	private MosipEventBus mosipEventBus;

	/**
	 * deploys this verticle
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 * 
	 */
	
	@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}

	/**
	 * contains all the routes in the stage
	 * 
	 * @param router
	 */
	private void routes(Router router) {
		
		router.post("/packetreceiver/v0.1/registration-processor/packet-receiver/registrationpackets").handler(ctx -> {
			processURL(ctx);
		}).failureHandler(failureHandler -> {
			this.setResponse(failureHandler, globalExceptionHandler.handler(failureHandler.failure()));
		});
		
		router.get("/packetreceiver/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
		}).failureHandler(context->{
			this.setResponse(context, context.failure().getMessage());
		});
	}

	/**
	 * contains process logic for the context passed
	 * 
	 * @param ctx
	 */
	public void processURL(RoutingContext ctx) {
		FileUpload fileUpload = ctx.fileUploads().iterator().next();
		File file = null;
		try {
			FileUtils.copyFile(new File(fileUpload.uploadedFileName()),
					new File(new File(fileUpload.uploadedFileName()).getParent() + "/" + fileUpload.fileName()));
			FileUtils.forceDelete(new File(fileUpload.uploadedFileName()));
			file = new File(new File(fileUpload.uploadedFileName()).getParent() + "/" + fileUpload.fileName());
			MessageDTO messageDTO = packetReceiverService.storePacket(file);
			if (messageDTO.getIsValid()) {
				this.setResponse(ctx, RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN);
				this.sendMessage(messageDTO);
			} else {
				this.setResponse(ctx, RegistrationStatusCode.DUPLICATE_PACKET_RECIEVED);
			}
		} catch (IOException e) {
			throw new UnexpectedException(e.getMessage());
		} finally {
			if (file.exists())
				deleteFile(file);
		}
	}

	/**
	 * deletes a file
	 * 
	 * @param file
	 */
	private void deleteFile(File file) {
		try {
			FileUtils.forceDelete(file);
		} catch (IOException e) {
			throw new UnexpectedException(e.getMessage());
		}
	}

	/**
	 * sends messageDTO to camel bridge
	 * 
	 * @param messageDTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.VIRUS_SCAN_BUS_IN, messageDTO);
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
}
