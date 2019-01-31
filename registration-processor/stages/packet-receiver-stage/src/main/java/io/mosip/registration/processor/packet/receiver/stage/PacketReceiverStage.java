package io.mosip.registration.processor.packet.receiver.stage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.packet.receiver.dto.ExceptionJSONInfo;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
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
		router.post("/v0.1/registration-processor/packet-receiver/registrationpackets").handler(ctx -> {
			processURL(ctx);
		}).failureHandler(failureHandler -> {
			String response = globalExceptionHandler.handler(failureHandler.failure());
			this.setResponse(failureHandler, response);
		});
		
		
		
		
		router.get("/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
			
		}).failureHandler(context->{
			String obj = context.failure().getMessage();
			this.setResponse(context, obj);
		});

	}

	/**
	 * contains process logic for the context passed
	 * 
	 * @param ctx
	 */
	public void processURL(RoutingContext ctx) {
		FileUpload f = ctx.fileUploads().iterator().next();
		File file = null;

		try {
			FileUtils.copyFile(new File(f.uploadedFileName()),
					new File(new File(f.uploadedFileName()).getParent() + "/" + f.fileName()));
			FileUtils.forceDelete(new File(f.uploadedFileName()));
			file = new File(new File(f.uploadedFileName()).getParent() + "/" + f.fileName());
			MessageDTO messageDTO = packetReceiverService.storePacket(file);
			if (messageDTO.getIsValid()) {
				this.setResponse(ctx, RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN);
				this.sendMessage(messageDTO);
			} else {
				this.setResponse(ctx, RegistrationStatusCode.DUPLICATE_PACKET_RECIEVED);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
