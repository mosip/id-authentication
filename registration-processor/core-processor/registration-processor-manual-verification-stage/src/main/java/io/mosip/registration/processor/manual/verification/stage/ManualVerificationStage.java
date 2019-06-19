package io.mosip.registration.processor.manual.verification.stage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.manual.verification.constants.ManualVerificationConstants;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.exception.handler.ManualVerificationExceptionHandler;
import io.mosip.registration.processor.manual.verification.request.dto.ManualAppBiometricRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualAppDemographicRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualVerificationAssignmentRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualVerificationDecisionRequestDTO;
import io.mosip.registration.processor.manual.verification.response.builder.ManualVerificationResponseBuilder;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationAssignResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationBioDemoResponseDTO;
import io.mosip.registration.processor.manual.verification.service.ManualVerificationService;
import io.mosip.registration.processor.manual.verification.util.ManualVerificationRequestValidator;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * This class sends message to next stage after successful completion of manual
 * verification.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
@Component
public class ManualVerificationStage extends MosipVerticleAPIManager {

	@Autowired
	private ManualVerificationService manualAdjudicationService;

	/** The mosip event bus. */
	private MosipEventBus mosipEventBus;

	/**
	 * vertx Cluster Manager Url
	 */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;
	
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ManualVerificationStage.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	ManualVerificationRequestValidator manualVerificationRequestValidator;

	@Autowired
	ManualVerificationExceptionHandler manualVerificationExceptionHandler;

	@Autowired
	ManualVerificationResponseBuilder manualVerificationResponseBuilder;

	/** Mosip router for APIs */
	@Autowired
	MosipRouter router;


	/**
	 * server port number
	 */
	@Value("${server.port}")
	private String port;


	@Value("${server.servlet.path}")
	private String contextPath;


	private static final String APPLICATION_JSON = "application/json";

	/**
	 * Deploy stage.
	 */
	public void deployStage() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl);
	}

	@Override
	public void start() {
		router.setRoute(this.postUrl(vertx, null ,MessageBusAddress.MANUAL_VERIFICATION_BUS));
		this.routes(router);
		this.createServer(router.getRouter(), Integer.parseInt(port));
	}

	private void routes(MosipRouter router) {
		router.post(contextPath+"/applicantBiometric");
		router.handler(event -> {
			try {
				processBiometric(event);
			} catch (PacketDecryptionFailureException | ApisResourceAccessException | IOException
					| java.io.IOException e2) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), "", "",
						 ExceptionUtils.getStackTrace(e2));	

			}
		}, handlerObj -> {
			manualVerificationExceptionHandler.setId(env.getProperty(ManualVerificationConstants.BIOMETRIC_SERVICE_ID));
			manualVerificationExceptionHandler.setResponseDtoType(new ManualVerificationBioDemoResponseDTO());
			this.setResponseWithDigitalSignature(handlerObj, manualVerificationExceptionHandler.handler(handlerObj.failure()), APPLICATION_JSON);
		});


		router.post(contextPath+"/applicantDemographic");
		router.handler(event -> {
			try {
				processDemographic(event);
			} catch (PacketDecryptionFailureException | ApisResourceAccessException | IOException
					| java.io.IOException e1) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), "", "",
						 ExceptionUtils.getStackTrace(e1));	
				}
		}, handlerObj -> {
			manualVerificationExceptionHandler.setId(env.getProperty(ManualVerificationConstants.DEMOGRAPHIC_SERVICE_ID));
			manualVerificationExceptionHandler.setResponseDtoType(new ManualVerificationBioDemoResponseDTO());
			this.setResponseWithDigitalSignature(handlerObj, manualVerificationExceptionHandler.handler(handlerObj.failure()), APPLICATION_JSON);

		});


		router.post(contextPath+"/assignment");
		router.handler(this::processAssignment, handlerObj -> {
			manualVerificationExceptionHandler.setId(env.getProperty(ManualVerificationConstants.ASSIGNMENT_SERVICE_ID));
			manualVerificationExceptionHandler.setResponseDtoType(new ManualVerificationAssignResponseDTO());
			this.setResponseWithDigitalSignature(handlerObj, manualVerificationExceptionHandler.handler(handlerObj.failure()), APPLICATION_JSON);


		});


		router.post(contextPath+"/decision");
		router.handler(this::processDecision, handlerObj -> {
			manualVerificationExceptionHandler.setId(env.getProperty(ManualVerificationConstants.DECISION_SERVICE_ID));
			manualVerificationExceptionHandler.setResponseDtoType(new ManualVerificationAssignResponseDTO());
			this.setResponseWithDigitalSignature(handlerObj, manualVerificationExceptionHandler.handler(handlerObj.failure()), APPLICATION_JSON);

		});


		router.post(contextPath+"/packetInfo");
		router.handler(event -> {
			try {
				processPacketInfo(event);
			} catch (PacketDecryptionFailureException | ApisResourceAccessException | IOException
					| java.io.IOException e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), "", "",
						 ExceptionUtils.getStackTrace(e));	
			}
		}, handlerObj -> {
			manualVerificationExceptionHandler.setId(env.getProperty(ManualVerificationConstants.PACKETINFO_SERVICE_ID));
			manualVerificationExceptionHandler.setResponseDtoType(new ManualVerificationAssignResponseDTO());
			this.setResponseWithDigitalSignature(handlerObj, manualVerificationExceptionHandler.handler(handlerObj.failure()), APPLICATION_JSON);

		});

	}
	
	public void processBiometric(RoutingContext ctx) throws PacketDecryptionFailureException, ApisResourceAccessException, IOException, java.io.IOException {

		JsonObject obj = ctx.getBodyAsJson();
		manualVerificationRequestValidator.validate(obj, env.getProperty(ManualVerificationConstants.BIOMETRIC_SERVICE_ID));
		ManualAppBiometricRequestDTO pojo = Json.mapper.convertValue(obj.getMap(), ManualAppBiometricRequestDTO.class);
		byte[] packetInfo = manualAdjudicationService.getApplicantFile(pojo.getRequest().getRegId(),
				PacketFiles.BIOMETRIC.name());
		if (packetInfo != null) {
			String byteAsString = new String(packetInfo);
			String responseData=ManualVerificationResponseBuilder.buildManualVerificationSuccessResponse(byteAsString,	env.getProperty(ManualVerificationConstants.BIOMETRIC_SERVICE_ID), env.getProperty(ManualVerificationConstants.MVS_APPLICATION_VERSION),env.getProperty(ManualVerificationConstants.DATETIME_PATTERN));
			this.setResponseWithDigitalSignature(ctx,responseData,APPLICATION_JSON);
		}

	}

	public void processDemographic(RoutingContext ctx) throws PacketDecryptionFailureException, ApisResourceAccessException, IOException, java.io.IOException {
		JsonObject obj = ctx.getBodyAsJson();
		manualVerificationRequestValidator.validate(obj, env.getProperty(ManualVerificationConstants.DEMOGRAPHIC_SERVICE_ID));
		ManualAppBiometricRequestDTO pojo = Json.mapper.convertValue(obj.getMap(), ManualAppBiometricRequestDTO.class);
		byte[] packetInfo = manualAdjudicationService.getApplicantFile(pojo.getRequest().getRegId(),
				PacketFiles.DEMOGRAPHIC.name());

		if (packetInfo != null) {
			String byteAsString = new String(packetInfo);
			String responseData=ManualVerificationResponseBuilder.buildManualVerificationSuccessResponse(byteAsString,	env.getProperty(ManualVerificationConstants.DEMOGRAPHIC_SERVICE_ID), env.getProperty(ManualVerificationConstants.MVS_APPLICATION_VERSION), env.getProperty(ManualVerificationConstants.DATETIME_PATTERN));
			this.setResponseWithDigitalSignature(ctx,responseData,APPLICATION_JSON);
		}

	}

	public void processAssignment(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();
		ManualVerificationAssignmentRequestDTO pojo = Json.mapper.convertValue(obj.getMap(),
				ManualVerificationAssignmentRequestDTO.class);
		manualVerificationRequestValidator.validate(obj, env.getProperty(ManualVerificationConstants.ASSIGNMENT_SERVICE_ID));
		ManualVerificationDTO manualVerificationDTO = manualAdjudicationService.assignApplicant(pojo.getRequest());
		if (manualVerificationDTO != null) {
			String responseData=ManualVerificationResponseBuilder.buildManualVerificationSuccessResponse(manualVerificationDTO,env.getProperty(ManualVerificationConstants.ASSIGNMENT_SERVICE_ID), env.getProperty(ManualVerificationConstants.MVS_APPLICATION_VERSION),env.getProperty(ManualVerificationConstants.DATETIME_PATTERN));
			this.setResponseWithDigitalSignature(ctx,responseData,APPLICATION_JSON);

		}

	}

	public void processDecision(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();
		ManualVerificationDecisionRequestDTO pojo = Json.mapper.convertValue(obj.getMap(),
				ManualVerificationDecisionRequestDTO.class);
		manualVerificationRequestValidator.validate(obj, env.getProperty(ManualVerificationConstants.DECISION_SERVICE_ID));
		ManualVerificationDTO updatedManualVerificationDTO = manualAdjudicationService
				.updatePacketStatus(pojo.getRequest(), this.getClass().getSimpleName());
		if (updatedManualVerificationDTO != null) {
			String responseData=ManualVerificationResponseBuilder.buildManualVerificationSuccessResponse(updatedManualVerificationDTO, env.getProperty(ManualVerificationConstants.DECISION_SERVICE_ID),env.getProperty(ManualVerificationConstants.MVS_APPLICATION_VERSION), env.getProperty(ManualVerificationConstants.DATETIME_PATTERN));
			this.setResponseWithDigitalSignature(ctx,responseData,APPLICATION_JSON);
		}

	}

	public void processPacketInfo(RoutingContext ctx) throws PacketDecryptionFailureException, ApisResourceAccessException, IOException, java.io.IOException {
		JsonObject obj = ctx.getBodyAsJson();
		ManualAppDemographicRequestDTO pojo = Json.mapper.convertValue(obj.getMap(),
				ManualAppDemographicRequestDTO.class);
		manualVerificationRequestValidator.validate(obj, env.getProperty(ManualVerificationConstants.PACKETINFO_SERVICE_ID));
		PacketMetaInfo packetInfo = manualAdjudicationService.getApplicantPacketInfo(pojo.getRequest().getRegId());
		if (packetInfo != null) {
			String responseData=ManualVerificationResponseBuilder.buildManualVerificationSuccessResponse(packetInfo,env.getProperty(ManualVerificationConstants.PACKETINFO_SERVICE_ID), env.getProperty(ManualVerificationConstants.MVS_APPLICATION_VERSION),env.getProperty(ManualVerificationConstants.DATETIME_PATTERN));
			this.setResponseWithDigitalSignature(ctx,responseData,APPLICATION_JSON);
		}

	}

	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.MANUAL_VERIFICATION_BUS, messageDTO);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}
}
