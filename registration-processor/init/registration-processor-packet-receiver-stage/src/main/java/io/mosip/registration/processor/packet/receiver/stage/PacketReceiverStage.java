package io.mosip.registration.processor.packet.receiver.stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.manager.exception.systemexception.UnexpectedException;
import io.mosip.registration.processor.packet.receiver.builder.PacketReceiverResponseBuilder;
import io.mosip.registration.processor.packet.receiver.exception.PacketReceiverAppException;
import io.mosip.registration.processor.packet.receiver.exception.handler.PacketReceiverExceptionHandler;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * The Class PacketReceiverStage.
 */

// @RefreshScope
@Service
public class PacketReceiverStage extends MosipVerticleAPIManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketReceiverStage.class);

	/** vertx Cluster Manager Url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** server port number. */
	@Value("${server.port}")
	private String port;

	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String MODULE_ID = "mosip.registration.processor.packet.id";

	/** The Packet Receiver Service. */
	@Autowired
	public PacketReceiverService<File, MessageDTO> packetReceiverService;

	/** Exception handler. */
	@Autowired
	public PacketReceiverExceptionHandler globalExceptionHandler;

	/** The packet receiver response builder. */
	@Autowired
	PacketReceiverResponseBuilder packetReceiverResponseBuilder;
	/**
	 * The mosip event bus.
	 */
	private MosipEventBus mosipEventBus;

	FileUpload fileUpload;

	/**
	 * deploys this verticle.
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);

	}

	/** The Constant APPLICATION_JSON. */
	private static final String APPLICATION_JSON = "application/json";

	List<String> listObj = new ArrayList<>();

	@Autowired
	private Environment env;

	@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}

	/**
	 * contains all the routes in the stage.
	 *
	 * @param router
	 *            the router
	 */
	private void routes(Router router) {

		router.post("/packetreceiver/registration-processor/registrationpackets/v1.0").blockingHandler(ctx -> {
			processURL(ctx);
		}, false).blockingHandler(ctx -> {
			processPacket(ctx);
		}, false).failureHandler(failureHandler -> {
			this.setResponse(failureHandler, globalExceptionHandler.handler(failureHandler.failure()),
					APPLICATION_JSON);
		});

		router.get("/packetreceiver/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
		}).failureHandler(context -> {
			this.setResponse(context, context.failure().getMessage());
		});
	}

	private void processPacket(RoutingContext ctx) {
		fileUpload = ctx.fileUploads().iterator().next();
		File file = null;
		try {
			listObj.add(env.getProperty(MODULE_ID));
			FileUtils.copyFile(new File(fileUpload.uploadedFileName()),
					new File(new File(fileUpload.uploadedFileName()).getParent() + "/" + fileUpload.fileName()));
			FileUtils.forceDelete(new File(fileUpload.uploadedFileName()));
			file = new File(new File(fileUpload.uploadedFileName()).getParent() + "/" + fileUpload.fileName());
			MessageDTO messageDTO = packetReceiverService.validatePacket(file, this.getClass().getSimpleName());
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new UnexpectedException(e.getMessage());
		} finally {
			if (file != null) {
				if (file.exists()) {
					deleteFile(file);
				}
			}
		}

	}

	/**
	 * contains process logic for the context passed.
	 *
	 * @param ctx
	 *            the ctx
	 * @throws PacketReceiverAppException
	 */
	public void processURL(RoutingContext ctx) throws PacketReceiverAppException {
		FileUpload fileUpload = ctx.fileUploads().iterator().next();
		File file = null;
		try {
			listObj.add(env.getProperty(MODULE_ID));
			FileUtils.copyFile(new File(fileUpload.uploadedFileName()),
					new File(new File(fileUpload.uploadedFileName()).getParent() + "/" + fileUpload.fileName()));
			FileUtils.forceDelete(new File(fileUpload.uploadedFileName()));
			file = new File(new File(fileUpload.uploadedFileName()).getParent() + "/" + fileUpload.fileName());
			MessageDTO messageDTO = packetReceiverService.validatePacket(file, this.getClass().getSimpleName());
			listObj.add(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
			listObj.add(env.getProperty(APPLICATION_VERSION));
			if (messageDTO.getIsValid()) {
				this.setResponse(ctx,
						PacketReceiverResponseBuilder.buildPacketReceiverResponse(
								RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString(), listObj),
						APPLICATION_JSON);
				this.sendMessage(messageDTO);
			}
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new UnexpectedException(e.getMessage());
		}
	}

	/**
	 * deletes a file.
	 *
	 * @param file
	 *            the file
	 */
	private void deleteFile(File file) {
		try {
			FileUtils.forceDelete(file);
		} catch (IOException e) {
			throw new UnexpectedException(e.getMessage());
		}
	}

	/**
	 * sends messageDTO to camel bridge.
	 *
	 * @param messageDTO
	 *            the message DTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.PACKET_RECEIVER_OUT, messageDTO);
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
