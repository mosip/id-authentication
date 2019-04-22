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
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.uploader.service.PacketUploaderService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * The Class PacketUploaderStage.
 * 
 * @author Rishabh Keshari
 */
@Component
public class PacketUploaderStage extends MosipVerticleAPIManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketUploaderStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The cluster url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/**
	 * server port number
	 */
	@Value("${server.port}")
	private String port;


	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;




	@Value("${server.servlet.path}")
	private String contextPath;




	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;


	/** The audit log request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	PacketUploaderService<MessageDTO> packetUploaderService;

	/** The registration id. */
	private String registrationId = "";

	private boolean isTransactionSuccessful;

	String description = "";
	RegistrationExceptionMapperUtil registrationStatusMapperUtil = new RegistrationExceptionMapperUtil();







	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl);
	}

	@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}

	/**
	 * contains all the routes in this stage
	 * @param router
	 */
	private void routes(Router router) {
		router.post(contextPath+ "/securezone").handler(ctx -> {
			processURL(ctx);
		}).failureHandler(failureHandler -> {
			this.setResponse(failureHandler, failureHandler.failure().getMessage());	
		});

		router.get(contextPath+"/securezone/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
		}).failureHandler(context->{
			this.setResponse(context, context.failure().getMessage());
		});

	}


	/**
	 * method to process the context received
	 * @param ctx
	 */
	public void processURL(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();

		MessageDTO messageDTO= new MessageDTO();
		messageDTO.setInternalError(Boolean.FALSE);
		messageDTO.setIsValid(obj.getBoolean("isValid"));
		messageDTO.setRid(obj.getString("rid"));
		//	process(messageDTO);

		messageDTO=packetUploaderService.validateAndUploadPacket("10003100030010320190412102906");
		if(messageDTO.getIsValid())
		sendMessage( messageDTO);
		
		
		this.setResponse(ctx, "Packet with registrationId '"+obj.getString("rid")+"' has been forwarded to Packet validation stage");
		regProcLogger.info(obj.getString("rid"), "Packet with registrationId '"+obj.getString("rid")+"' has been forwarded to Packet validation stage", null, null);


	}

	@Override
	public MessageDTO process(MessageDTO object) {




		try {
			System.out.println("will you call");




		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}


	/**
	 * sends messageDTO to camel bridge
	 * 
	 * @param messageDTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.REGISTRATION_CONNECTOR_BUS_OUT, messageDTO);
	}




}
