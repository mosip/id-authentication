package io.mosip.registration.processor.quality.checker.stage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import io.mosip.registration.processor.core.abstractverticle.*;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.status.util.StatusUtil;
import io.mosip.registration.processor.core.status.util.TrimExceptionMessage;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.quality.checker.exception.FileMissingException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class QualityCheckerStage.
 * 
 * @author M1048358 Alok Ranjan
 */
public class QualityCheckerStage extends MosipVerticleAPIManager {

	/** The Constant FINGER. */
	private static final String FINGER = "FINGER";

	/** The Constant THUMB. */
	private static final String THUMB = "Thumb";

	/** The Constant RIGHT. */
	private static final String RIGHT = "Right";

	/** The Constant LEFT. */
	private static final String LEFT = "Left";

	/** The Constant IRIS. */
	private static final String IRIS = "IRIS";

	/** The Constant FACE. */
	private static final String FACE = "FACE";

	/** The Constant INDIVIDUAL_BIOMETRICS. */
	public static final String INDIVIDUAL_BIOMETRICS = "individualBiometrics";

	/** The Constant UTF_8. */
	public static final String UTF_8 = "UTF-8";

	/** The Constant VALUE. */
	public static final String VALUE = "value";
	
	private TrimExceptionMessage trimExceptionMsg = new TrimExceptionMessage();

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** The iris threshold. */
	@Value("${mosip.registration.iris_threshold}")
	private Integer irisThreshold;

	/** The left finger threshold. */
	@Value("${mosip.registration.leftslap_fingerprint_threshold}")
	private Integer leftFingerThreshold;

	/** The right finger threshold. */
	@Value("${mosip.registration.rightslap_fingerprint_threshold}")
	private Integer rightFingerThreshold;

	/** The thumb finger threshold. */
	@Value("${mosip.registration.thumbs_fingerprint_threshold}")
	private Integer thumbFingerThreshold;

	/** The face threshold. */
	@Value("${mosip.registration.facequalitythreshold}")
	private Integer faceThreshold;

	/** server port number. */
	@Value("${server.port}")
	private String port;

	/** The adapter. */
	@Autowired
	private PacketManager adapter;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The utilities. */
	@Autowired
	private Utilities utilities;

	/** Mosip router for APIs */
	@Autowired
	private MosipRouter router;

	/** The bio Api. */
	@Autowired
	private IBioApi bioAPi;

	/** The cbeff util. */
	@Autowired
	private CbeffUtil cbeffUtil;

	/** The registration status mapper util. */
	@Autowired
	private RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(QualityCheckerStage.class);

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = File.separator;

	private MosipEventBus mosipEventBus = null;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.QUALITY_CHECKER_BUS_IN,
				MessageBusAddress.QUALITY_CHECKER_BUS_OUT);
	}

	@Override
	public void start() {
		router.setRoute(
				this.postUrl(mosipEventBus.getEventbus(), MessageBusAddress.OSI_BUS_IN, MessageBusAddress.OSI_BUS_OUT));
		this.createServer(router.getRouter(), Integer.parseInt(port));
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
		object.setMessageBusAddress(MessageBusAddress.QUALITY_CHECKER_BUS_IN);
		String regId = object.getRid();
		String description = "";
		Boolean isTransactionSuccessful = Boolean.FALSE;
		InternalRegistrationStatusDto registrationStatusDto = null;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
				"QualityCheckerStage::process()::entry");

		registrationStatusDto = registrationStatusService.getRegistrationStatus(regId);

		try {
			registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());
			InputStream idJsonStream = adapter.getFile(regId,
					PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
			String idJsonString = IOUtils.toString(idJsonStream, UTF_8);
			JSONObject idJsonObject = JsonUtil.objectMapperReadValue(idJsonString, JSONObject.class);
			JSONObject identity = JsonUtil.getJSONObject(idJsonObject,
					utilities.getGetRegProcessorDemographicIdentity());
			JSONObject individualBiometricsObject = JsonUtil.getJSONObject(identity, INDIVIDUAL_BIOMETRICS);
			if (individualBiometricsObject == null) {
				description = "Individual Biometric parameter is not present in ID Json";
				object.setIsValid(Boolean.TRUE);
				isTransactionSuccessful = Boolean.TRUE;
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				registrationStatusDto.setStatusComment(StatusUtil.INDIVIDUAL_BIOMETRIC_NOT_FOUND.getMessage());
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
						"Individual Biometric parameter is not present in ID Json");
			} else {
				String biometricFileName = JsonUtil.getJSONValue(individualBiometricsObject, VALUE);
				if (biometricFileName == null || biometricFileName.isEmpty()) {
					description = "File Name of individual biometric is not present";
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), regId,
							PlatformErrorMessages.RPR_QCR_FILENAME_MISSING.getMessage());
					throw new FileMissingException(PlatformErrorMessages.RPR_QCR_FILENAME_MISSING.getCode(),
							PlatformErrorMessages.RPR_QCR_FILENAME_MISSING.getMessage());
				}
				InputStream cbeffStream = adapter.getFile(regId,
						PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR + biometricFileName);
				if (cbeffStream == null) {
					description = "Applicant biometric file missing";
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), regId,
							PlatformErrorMessages.RPR_QCR_BIO_FILE_MISSING.getMessage());
					throw new FileMissingException(PlatformErrorMessages.RPR_QCR_BIO_FILE_MISSING.getCode(),
							PlatformErrorMessages.RPR_QCR_BIO_FILE_MISSING.getMessage());
				}
				List<BIRType> birTypeList = cbeffUtil.getBIRDataFromXML(IOUtils.toByteArray(cbeffStream));
				List<BIR> birList = cbeffUtil.convertBIRTypeToBIR(birTypeList);
				int scoreCounter = 0;
				for (BIR bir : birList) {
					SingleType singleType = bir.getBdbInfo().getType().get(0);
					;
					List<String> subtype = bir.getBdbInfo().getSubtype();
					Integer threshold = getThresholdBasedOnType(singleType, subtype);
					QualityScore qualityScore = bioAPi.checkQuality(bir, null);
					if (qualityScore.getInternalScore() < threshold) {
						object.setIsValid(Boolean.FALSE);
						isTransactionSuccessful = Boolean.FALSE;
						registrationStatusDto
								.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
						registrationStatusDto.setStatusCode(RegistrationStatusCode.REJECTED.toString());
						registrationStatusDto.setStatusComment(StatusUtil.BIOMETRIC_QUALITY_CHECK_FAILED.getMessage());
						description = "The Quality score of biometrics is below threshold";
						break;
					} else {
						scoreCounter++;
					}
				}
				if (scoreCounter == birTypeList.size()) {
					object.setIsValid(Boolean.TRUE);
					description = "Biometric Quality Check Successful";
					isTransactionSuccessful = Boolean.TRUE;
					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
					registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
					registrationStatusDto.setStatusComment(StatusUtil.BIOMETRIC_QUALITY_CHECK_SUCCESS.getMessage());
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), regId, "QualityCheckerImpl::success");
				}
			}

			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.QUALITY_CHECK.toString());

		} catch (FSAdapterException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto
					.setStatusComment(trimExceptionMsg.trimExceptionMessage(StatusUtil.FS_ADAPTER_EXCEPTION.getMessage() + e.getMessage()));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_UGS_PACKET_STORE_NOT_ACCESSIBLE.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			isTransactionSuccessful = false;
			description = "FileSytem is not accessible for packet " + regId + "::" + e.getMessage();
			object.setRid(regId);
		}catch(FileMissingException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto.setStatusComment(StatusUtil.BIO_METRIC_FILE_MISSING.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.BIOMETRIC_EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_QCR_BIOMETRIC_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			isTransactionSuccessful = false;
			description = "Applicant biometric fileName/file is missing " + regId + "::" + e.getMessage();
			object.setRid(regId);
			
		}
		catch (BiometricException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto.setStatusComment(trimExceptionMsg.trimExceptionMessage(StatusUtil.BIO_METRIC_EXCEPTION.getMessage() + e.getMessage()));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.BIOMETRIC_EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_QCR_BIOMETRIC_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			isTransactionSuccessful = false;
			description = "Biometric exception from IDA for " + regId + "::" + e.getMessage();
			object.setRid(regId);
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(trimExceptionMsg.trimExceptionMessage(StatusUtil.UNKNOWN_EXCEPTION_OCCURED.getMessage() + ex.getMessage()));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId,
					RegistrationStatusCode.FAILED.toString() + ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			isTransactionSuccessful = Boolean.FALSE;
			description = "Internal error occurred in QualityChecker stage while processing registrationId " + regId
					+ ex.getMessage();
		} finally {
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			String moduleId = isTransactionSuccessful ? "Quality-Check Success" : "";
			String moduleName = "Quality-Checker";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, regId);

		}

		return object;
	}

	/**
	 * Gets the threshold based on type.
	 *
	 * @param singleType
	 *            the single type
	 * @param subtype
	 *            the subtype
	 * @return the threshold based on type
	 */
	private Integer getThresholdBasedOnType(SingleType singleType, List<String> subtype) {
		if (singleType.value().equalsIgnoreCase(FINGER)) {
			if (subtype.contains(THUMB)) {
				return thumbFingerThreshold;
			} else if (subtype.contains(RIGHT)) {
				return rightFingerThreshold;
			} else if (subtype.contains(LEFT)) {
				return leftFingerThreshold;
			}
		} else if (singleType.value().equalsIgnoreCase(IRIS)) {
			return irisThreshold;
		} else if (singleType.value().equalsIgnoreCase(FACE)) {
			return faceThreshold;
		}
		return 0;
	}
}
