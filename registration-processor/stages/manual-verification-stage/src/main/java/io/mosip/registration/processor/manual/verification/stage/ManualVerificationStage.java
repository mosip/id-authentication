package io.mosip.registration.processor.manual.verification.stage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.exception.ManualVerificationAppException;
import io.mosip.registration.processor.manual.verification.exception.handler.ManualAssignDecisionExceptionHandler;
import io.mosip.registration.processor.manual.verification.exception.handler.ManualBioDemoExceptionHandler;
import io.mosip.registration.processor.manual.verification.request.dto.ManualAppBiometricRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualAppDemographicRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualVerificationAssignmentRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualVerificationDecisionRequestDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationAssignResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationBioDemoResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationErrorDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationPacketResponseDTO;
import io.mosip.registration.processor.manual.verification.service.ManualVerificationService;
import io.mosip.registration.processor.manual.verification.util.ManualVerificationBioDemoJsonSerializer;
import io.mosip.registration.processor.manual.verification.util.ManualVerificationReqRespJsonSerializer;
import io.mosip.registration.processor.manual.verification.util.ManualVerificationRequestValidator;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * This class sends message to next stage after successful completion of manual
 * verification.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
@Component
public class ManualVerificationStage extends MosipVerticleAPIManager{

	@Autowired
	private ManualVerificationService manualAdjudicationService;

	/** The mosip event bus. */
	private MosipEventBus mosipEventBus;

	/**
	 * vertx Cluster Manager Url
	 */
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ManualVerificationStage.class);

	private static final String ASSIGNMENT_SERVICE_ID = "mosip.manual.verification.assignment";
	private static final String DECISION_SERVICE_ID = "mosip.manual.verification.decision";
	private static final String BIOMETRIC_SERVICE_ID = "mosip.manual.verification.biometric";
	private static final String DEMOGRAPHIC_SERVICE_ID = "mosip.manual.verification.demographic";
	private static final String PACKETINFO_SERVICE_ID = "mosip.manual.verification.packetinfo";
	private static final String MVS_APPLICATION_VERSION = "1.0";
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	
	
	
	
	@Autowired
	ManualVerificationRequestValidator manualVerificationRequestValidator;
	
	@Autowired
	ManualBioDemoExceptionHandler manualBioDemoExceptionHandler;
	
	@Autowired
	ManualAssignDecisionExceptionHandler manualAssignDecisionExceptionHandler;
	/**
	 * server port number
	 */
	@Value("${server.port}")
	private String port;
	private static final String APPLICATION_JSON = "application/json";
	/**
	 * Deploy stage.
	 */
	public void deployStage() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl);
	}

	@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}

	private void routes(Router router) {
		router.post("/manualverification/v0.1/registration-processor/manual-verification/applicantBiometric").handler(ctx -> {
			processBiometric(ctx);
		}).failureHandler(handlerObj -> {
			manualBioDemoExceptionHandler.setId(BIOMETRIC_SERVICE_ID);
			this.setResponse(handlerObj, manualBioDemoExceptionHandler.handler(handlerObj.failure()),APPLICATION_JSON); 
		});

		router.post("/manualverification/v0.1/registration-processor/manual-verification/applicantDemographic").handler(ctx -> {
			processDemographic(ctx);
		}).failureHandler(handlerObj -> {
			manualBioDemoExceptionHandler.setId(DEMOGRAPHIC_SERVICE_ID);
			this.setResponse(handlerObj, manualBioDemoExceptionHandler.handler(handlerObj.failure()),APPLICATION_JSON); 
		
		});
		
		router.post("/manualverification/v0.1/registration-processor/manual-verification/assignment").handler(ctx -> {
			processAssignment(ctx);
		}).failureHandler(handlerObj -> {
			manualAssignDecisionExceptionHandler.setId(ASSIGNMENT_SERVICE_ID);
			this.setResponse(handlerObj, manualAssignDecisionExceptionHandler.handler(handlerObj.failure()),APPLICATION_JSON); 
		 
		});

		router.post("/manualverification/v0.1/registration-processor/manual-verification/decision").handler(ctx -> {
			processDecision(ctx);
		}).failureHandler(handlerObj -> {
			manualAssignDecisionExceptionHandler.setId(DECISION_SERVICE_ID);
			this.setResponse(handlerObj, manualAssignDecisionExceptionHandler.handler(handlerObj.failure()),APPLICATION_JSON);  
		});

		router.post("/manualverification/v0.1/registration-processor/manual-verification/packetInfo").handler(ctx -> {
			processPacketInfo(ctx);
		}).failureHandler(handlerObj -> {
			manualAssignDecisionExceptionHandler.setId(PACKETINFO_SERVICE_ID);
			this.setResponse(handlerObj, manualAssignDecisionExceptionHandler.handler(handlerObj.failure()),APPLICATION_JSON);  
		});

		router.get("/manualverification/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
		}).failureHandler(handlerObj->{
			this.setResponse(handlerObj, handlerObj.failure().getMessage()); 
		});

	}

	public void processBiometric(RoutingContext ctx){

		try {
			JsonObject obj = ctx.getBodyAsJson();
			manualVerificationRequestValidator.validate(obj,BIOMETRIC_SERVICE_ID);
			ManualAppBiometricRequestDTO pojo = Json.mapper.convertValue ( obj.getMap(), ManualAppBiometricRequestDTO.class );
			byte[] packetInfo = manualAdjudicationService.getApplicantFile(pojo.getRequest().getRegId(),pojo.getRequest().getFileName());
			String byteAsString = new String(packetInfo);
			if (packetInfo != null) {
				this.setResponse(ctx, buildBioDemoSuccessResponse(byteAsString,BIOMETRIC_SERVICE_ID),APPLICATION_JSON);
			}
		} catch (ManualVerificationAppException e) {
			this.setResponse(ctx,buildManualExceptionResponse(e,BIOMETRIC_SERVICE_ID),APPLICATION_JSON);
		}
	}

	public void processDemographic(RoutingContext ctx) {
		try {
			JsonObject obj = ctx.getBodyAsJson();
			manualVerificationRequestValidator.validate(obj,DEMOGRAPHIC_SERVICE_ID);
			ManualAppDemographicRequestDTO pojo = Json.mapper.convertValue ( obj.getMap(), ManualAppDemographicRequestDTO.class );
			byte[] packetInfo = manualAdjudicationService.getApplicantFile(pojo.getRequest().getRegId(),PacketFiles.DEMOGRAPHIC.name());
			String byteAsString = new String(packetInfo);
			if (packetInfo != null) {
				this.setResponse(ctx, buildBioDemoSuccessResponse(byteAsString,DEMOGRAPHIC_SERVICE_ID),APPLICATION_JSON);
			}
		} catch (ManualVerificationAppException e) {
			this.setResponse(ctx,buildManualExceptionResponse(e,DEMOGRAPHIC_SERVICE_ID),APPLICATION_JSON);

		}
	}

	public void processAssignment(RoutingContext ctx) {
		try {
			JsonObject obj = ctx.getBodyAsJson();
			ManualVerificationAssignmentRequestDTO pojo = Json.mapper.convertValue ( obj.getMap(), ManualVerificationAssignmentRequestDTO.class );
			manualVerificationRequestValidator.validate(obj,ASSIGNMENT_SERVICE_ID);
			ManualVerificationDTO manualVerificationDTO = manualAdjudicationService.assignApplicant(pojo.getRequest());
			if (manualVerificationDTO != null) {
				this.setResponse(ctx, buildAssignDecisionSuccessResponse(manualVerificationDTO,ASSIGNMENT_SERVICE_ID),APPLICATION_JSON);

			}

		} catch (ManualVerificationAppException e) {
			this.setResponse(ctx,buildAssignDecisionExceptionResponse(e,ASSIGNMENT_SERVICE_ID),APPLICATION_JSON);

		}
	}

	public void processDecision(RoutingContext ctx) {
		try {
			JsonObject obj = ctx.getBodyAsJson();
			ManualVerificationDecisionRequestDTO pojo = Json.mapper.convertValue ( obj.getMap(), ManualVerificationDecisionRequestDTO.class );
			manualVerificationRequestValidator.validate(obj,DECISION_SERVICE_ID);
			ManualVerificationDTO updatedManualVerificationDTO = manualAdjudicationService.updatePacketStatus(pojo.getRequest());
			if (updatedManualVerificationDTO != null) {
				this.setResponse(ctx, buildAssignDecisionSuccessResponse(updatedManualVerificationDTO,DECISION_SERVICE_ID),APPLICATION_JSON);
			}
		} catch (ManualVerificationAppException e) {
			this.setResponse(ctx,buildAssignDecisionExceptionResponse(e,DECISION_SERVICE_ID),APPLICATION_JSON);

		}
	}

	public void processPacketInfo(RoutingContext ctx) {
		try {
			JsonObject obj = ctx.getBodyAsJson();
			ManualAppDemographicRequestDTO pojo = Json.mapper.convertValue ( obj.getMap(), ManualAppDemographicRequestDTO.class );
			manualVerificationRequestValidator.validate(obj,PACKETINFO_SERVICE_ID);
			PacketMetaInfo packetInfo = manualAdjudicationService.getApplicantPacketInfo(pojo.getRequest().getRegId());
			if (packetInfo != null) {
				this.setResponse(ctx, buildAPacketMetaInfoSuccessResponse(packetInfo,PACKETINFO_SERVICE_ID),APPLICATION_JSON);
			}
		}catch(ManualVerificationAppException e) {
			this.setResponse(ctx,buildAssignDecisionExceptionResponse(e,PACKETINFO_SERVICE_ID),APPLICATION_JSON);

		}
	}

	
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.MANUAL_VERIFICATION_BUS, messageDTO);
	}

	
	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}

	private String buildBioDemoSuccessResponse(String byteData,String id) {

		ManualVerificationBioDemoResponseDTO response = new ManualVerificationBioDemoResponseDTO();
		if (Objects.isNull(response.getId())) {
			response.setId(id);
		}
		response.setError(null);
		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(MVS_APPLICATION_VERSION);
		response.setFile(byteData);
		Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(ManualVerificationBioDemoResponseDTO.class, new ManualVerificationBioDemoJsonSerializer()).create();
		return gson.toJson(response);
	}

	private String buildManualExceptionResponse(Exception ex,String id) {

		ManualVerificationBioDemoResponseDTO response = new ManualVerificationBioDemoResponseDTO();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(id);
		}
		if (e instanceof BaseCheckedException)
		{
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();
			List<ManualVerificationErrorDTO> errors = errorTexts.parallelStream().map(errMsg -> new ManualVerificationErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct().collect(Collectors.toList());
			response.setError(errors.get(0));
		}
		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<ManualVerificationErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ManualVerificationErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setError(errors.get(0));
		}

		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(MVS_APPLICATION_VERSION);
		response.setFile(null);
		Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(ManualVerificationBioDemoResponseDTO.class, new ManualVerificationBioDemoJsonSerializer()).create();
		return gson.toJson(response);
	}

	private String buildAssignDecisionSuccessResponse(ManualVerificationDTO updatedManualVerificationDTO,String id) {

		ManualVerificationAssignResponseDTO response = new ManualVerificationAssignResponseDTO();
		if (Objects.isNull(response.getId())) {
			response.setId(id);
		}
		response.setError(null);
		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(MVS_APPLICATION_VERSION);
		response.setResponse(updatedManualVerificationDTO);
		Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(ManualVerificationAssignResponseDTO.class, new ManualVerificationReqRespJsonSerializer()).create();
		return gson.toJson(response);
	}

	private String buildAssignDecisionExceptionResponse(Exception ex,String id) {

		ManualVerificationAssignResponseDTO response = new ManualVerificationAssignResponseDTO();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(id);
		}
		if (e instanceof BaseCheckedException)
		{
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();
			List<ManualVerificationErrorDTO> errors = errorTexts.parallelStream().map(errMsg -> new ManualVerificationErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct().collect(Collectors.toList());
			response.setError(errors.get(0));
		}
		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<ManualVerificationErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ManualVerificationErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setError(errors.get(0));
		}

		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(MVS_APPLICATION_VERSION);
		response.setResponse(null);
		Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(ManualVerificationAssignResponseDTO.class, new ManualVerificationReqRespJsonSerializer()).create();
		return gson.toJson(response);
	}
	
	private String buildAPacketMetaInfoSuccessResponse(PacketMetaInfo packetMetaInfo,String id) {

		ManualVerificationPacketResponseDTO response = new ManualVerificationPacketResponseDTO();
		if (Objects.isNull(response.getId())) {
			response.setId(id);
		}
		response.setError(null);
		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(MVS_APPLICATION_VERSION);
		response.setResponse(packetMetaInfo);
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(response);
	}
}
