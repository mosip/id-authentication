package io.mosip.registration.processor.abis.handler.stage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.processor.abis.handler.exception.AbisHandlerException;
import io.mosip.registration.processor.abis.queue.dto.AbisQueueDetails;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.AbisStatusCode;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestGalleryDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.ReferenceIdDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegBioRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class AbisHandlerStage.
 * 
 * @author M1048358 Alok
 */
@Service
public class AbisHandlerStage extends MosipVerticleManager {

	/** The Constant BIO_DEDUPE_STAGE. */
	private static final String BIO_DEDUPE_STAGE = "BioDedupeStage";

	/** The Constant DEMO_DEDUPE_STAGE. */
	private static final String DEMO_DEDUPE_STAGE = "DemoDedupeStage";

	/** The Constant MOSIP_ABIS_INSERT. */
	public static final String MOSIP_ABIS_INSERT = "mosip.abis.insert";

	/** The Constant VERSION. */
	public static final String VERSION = "1.0";

	/** The Constant TIMESTAMP. */
	public static final String TIMESTAMP = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L);

	/** The Constant ENG. */
	public static final String ENG = "eng";

	/** The Constant USER. */
	public static final String USER = "MOSIP";

	/** The Constant INSERT. */
	public static final String INSERT = "INSERT";

	/** The Constant IDENTIFY. */
	public static final String IDENTIFY = "IDENTIFY";

	/** The Constant MOSIP_ABIS_IDENTIFY. */
	public static final String MOSIP_ABIS_IDENTIFY = "mosip.abis.identify";

	/** The Constant DEMOGRAPHIC_VERIFICATION. */
	public static final String DEMOGRAPHIC_VERIFICATION = "DEMOGRAPHIC_VERIFICATION";

	/** The Constant BIOGRAPHIC_VERIFICATION. */
	public static final String BIOGRAPHIC_VERIFICATION = "BIOGRAPHIC_VERIFICATION";

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** The url. */
	@Value("${registration.processor.biometric.reference.url}")
	private String url;

	/** The max results. */
	@Value("${registration.processor.abis.maxResults}")
	private Integer maxResults;

	/** The target FPIR. */
	@Value("${registration.processor.abis.targetFPIR}")
	private Integer targetFPIR;

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(AbisHandlerStage.class);

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private Utilities utility;

	/** The description. */
	private String description = "";

	/** The transaction type code. */
	private String transactionTypeCode = null;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.ABIS_HANDLER_BUS_IN,
				MessageBusAddress.ABIS_HANDLER_BUS_OUT);
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
		object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);
		Boolean isTransactionSuccessful = false;
		String regId = object.getRid();
		InternalRegistrationStatusDto registrationStatusDto = null;
		String bioRefId = null;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
				"AbisHandlerStage::process()::entry");
		try {
			registrationStatusDto = registrationStatusService.getRegistrationStatus(regId);
			transactionTypeCode = registrationStatusDto.getLatestTransactionTypeCode();
			String transactionId = registrationStatusDto.getLatestRegistrationTransactionId();

			Boolean isIdentifyRequestPresent = packetInfoManager.getIdentifyByTransactionId(transactionId, IDENTIFY);

			if (!isIdentifyRequestPresent) {
				List<AbisQueueDetails> abisQueueDetails = utility.getAbisQueueDetails();
				if (abisQueueDetails.isEmpty()) {
					description = "Abis Queue  details not found";
					regProcLogger.error("Abis Queue  details not found", "", "", "");
					throw new AbisHandlerException(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode());
				}
				List<RegBioRefDto> bioRefDtos = packetInfoManager.getBioRefIdByRegId(regId);

				if (bioRefDtos.isEmpty()) {
					bioRefId = getUUID();
					insertInBioRef(regId, bioRefId);
				} else {
					bioRefId = bioRefDtos.get(0).getBioRefId();
				}
				createInsertRequest(abisQueueDetails, transactionId, bioRefId, regId);
				createIdentifyRequest(abisQueueDetails, transactionId, bioRefId, transactionTypeCode);
				object.setMessageBusAddress(MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN);
			} else {
				if (transactionTypeCode.equalsIgnoreCase(DEMOGRAPHIC_VERIFICATION)) {
					object.setMessageBusAddress(MessageBusAddress.DEMO_DEDUPE_BUS_IN);
				} else if (transactionTypeCode.equalsIgnoreCase(BIOGRAPHIC_VERIFICATION)) {
					object.setMessageBusAddress(MessageBusAddress.BIO_DEDUPE_BUS_IN);
				}
			}
			description = "Abis Handler Success";
			isTransactionSuccessful = true;
		} catch (Exception e) {
			description = "Internal Error occured in Abis Handler";
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
			if (transactionTypeCode.equalsIgnoreCase(DEMOGRAPHIC_VERIFICATION)) {
				registrationStatusDto.setRegistrationStageName(DEMO_DEDUPE_STAGE);
			} else if (transactionTypeCode.equalsIgnoreCase(BIOGRAPHIC_VERIFICATION)) {
				registrationStatusDto.setRegistrationStageName(BIO_DEDUPE_STAGE);
			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		} finally {
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			String moduleId = isTransactionSuccessful ? "Abis-Handler Success" : "";
			String moduleName = "Abis-Handler";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, regId);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
					"AbisHandlerStage::process()::exit");
		}

		return object;
	}

	/**
	 * Creates the identify request.
	 *
	 * @param abisApplicationDtoList
	 *            the abis application dto list
	 * @param transactionId
	 *            the transaction id
	 * @param bioRefId
	 *            the bio ref id
	 * @param transactionTypeCode
	 *            the transaction type code
	 */
	private void createIdentifyRequest(List<AbisQueueDetails> abisQueueDetails, String transactionId, String bioRefId,
			String transactionTypeCode) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisHandlerStage::createIdentifyRequest()::entry");
		String batchId = getUUID();
		for (AbisQueueDetails abisQueue : abisQueueDetails) {
			AbisRequestDto abisRequestDto = new AbisRequestDto();
			String id = getUUID();
			abisRequestDto.setId(id);
			abisRequestDto.setAbisAppCode(abisQueue.getName());
			abisRequestDto.setBioRefId(bioRefId);
			abisRequestDto.setRequestType(IDENTIFY);
			abisRequestDto.setReqBatchId(batchId);
			abisRequestDto.setRefRegtrnId(transactionId);

			byte[] abisIdentifyRequestBytes = getIdentifyRequestBytes(transactionId, bioRefId, transactionTypeCode, id);
			abisRequestDto.setReqText(abisIdentifyRequestBytes);

			abisRequestDto.setStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			abisRequestDto.setStatusComment(null);
			abisRequestDto.setLangCode(ENG);
			abisRequestDto.setCrBy(USER);
			abisRequestDto.setUpdBy(null);
			abisRequestDto.setIsDeleted(Boolean.FALSE);
			packetInfoManager.saveAbisRequest(abisRequestDto);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisHandlerStage::createIdentifyRequest()::exit");
	}

	/**
	 * Gets the identify request bytes.
	 *
	 * @param transactionId
	 *            the transaction id
	 * @param bioRefId
	 *            the bio ref id
	 * @param transactionTypeCode
	 *            the transaction type code
	 * @param id
	 *            the id
	 * @return the identify request bytes
	 */
	private byte[] getIdentifyRequestBytes(String transactionId, String bioRefId, String transactionTypeCode,
			String id) {
		AbisIdentifyRequestDto abisIdentifyRequestDto = new AbisIdentifyRequestDto();
		abisIdentifyRequestDto.setId(MOSIP_ABIS_IDENTIFY);
		abisIdentifyRequestDto.setVer(VERSION);
		abisIdentifyRequestDto.setRequestId(id);
		abisIdentifyRequestDto.setReferenceId(bioRefId);
		abisIdentifyRequestDto.setTimestamp(TIMESTAMP);
		abisIdentifyRequestDto.setMaxResults(maxResults);
		abisIdentifyRequestDto.setTargetFPIR(targetFPIR);

		// Added Gallery data for demo dedupe
		if (transactionTypeCode.equalsIgnoreCase(DEMOGRAPHIC_VERIFICATION)) {
			List<RegDemoDedupeListDto> regDemoDedupeListDtoList = packetInfoManager
					.getDemoListByTransactionId(transactionId);
			if (regDemoDedupeListDtoList.isEmpty()) {
				description = "Potential Match Records are Not Found for Demo Dedupe Potential Match";
				regProcLogger.error("Potential Match Records are Not Found for Demo Dedupe Potential Match", "", "",
						"");
				throw new AbisHandlerException(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode());
			}
			List<ReferenceIdDto> referenceIdDtos = new ArrayList<>();

			for (RegDemoDedupeListDto dedupeListDto : regDemoDedupeListDtoList) {
				ReferenceIdDto dto = new ReferenceIdDto();
				dto.setReferenceId(dedupeListDto.getMatchedRegId());
				referenceIdDtos.add(dto);
			}
			AbisIdentifyRequestGalleryDto galleryDto = new AbisIdentifyRequestGalleryDto();
			galleryDto.setReferenceIds(referenceIdDtos);
			abisIdentifyRequestDto.setGallery(galleryDto);
		}

		try {
			String jsonString = JsonUtils.javaObjectToJsonString(abisIdentifyRequestDto);
			return jsonString.getBytes();
		} catch (JsonProcessingException e) {
			description = "Internal Error occured in Abis Handler identify request";
			regProcLogger.error("Internal Error occured in Abis Handler in identify", "", "", "");
			throw new AbisHandlerException(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode(), e);
		}
	}

	/**
	 * Insert in bio ref.
	 *
	 * @param regId
	 *            the reg id
	 * @param bioRefId
	 *            the bio ref id
	 */
	private void insertInBioRef(String regId, String bioRefId) {
		RegBioRefDto regBioRefDto = new RegBioRefDto();
		regBioRefDto.setBioRefId(bioRefId);
		regBioRefDto.setCrBy(USER);
		regBioRefDto.setIsActive(Boolean.TRUE);
		regBioRefDto.setIsDeleted(Boolean.FALSE);
		regBioRefDto.setRegId(regId);
		regBioRefDto.setUpdBy(null);
		packetInfoManager.saveBioRef(regBioRefDto);
	}

	/**
	 * Creates the insert request.
	 *
	 * @param abisApplicationDtoList
	 *            the abis application dto list
	 * @param transactionId
	 *            the transaction id
	 * @param bioRefId
	 *            the bio ref id
	 * @param regId
	 *            the reg id
	 */
	private void createInsertRequest(List<AbisQueueDetails> abisQueueDetails, String transactionId, String bioRefId,
			String regId) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisHandlerStage::createInsertRequest()::entry");
		String batchId = getUUID();
		List<String> abisProcessedInsertAppCodeList = packetInfoManager.getAbisProcessedRequestsAppCodeByBioRefId(
				bioRefId, AbisStatusCode.INSERT.toString(), AbisStatusCode.PROCESSED.toString());
		List<String> abisAppCodeList = new ArrayList<>();
		for (AbisQueueDetails abisQueue : abisQueueDetails) {
			abisAppCodeList.add(abisQueue.getName());
		}

		for (String appCode : abisAppCodeList) {

			AbisRequestDto abisRequestDto = new AbisRequestDto();
			String id = getUUID();
			abisRequestDto.setId(id);
			abisRequestDto.setAbisAppCode(appCode);
			abisRequestDto.setBioRefId(bioRefId);
			abisRequestDto.setRequestType(INSERT);
			abisRequestDto.setReqBatchId(batchId);
			abisRequestDto.setRefRegtrnId(transactionId);

			byte[] abisInsertRequestBytes = getInsertRequestBytes(regId, id, bioRefId);
			abisRequestDto.setReqText(abisInsertRequestBytes);

			abisRequestDto.setStatusCode(AbisStatusCode.IN_PROGRESS.toString());
			abisRequestDto.setStatusComment(null);
			abisRequestDto.setLangCode(ENG);
			abisRequestDto.setCrBy(USER);
			abisRequestDto.setUpdBy(null);
			abisRequestDto.setIsDeleted(Boolean.FALSE);
			if (abisProcessedInsertAppCodeList != null && abisProcessedInsertAppCodeList.contains(appCode)) {
				abisRequestDto.setStatusCode(AbisStatusCode.ALREADY_PROCESSED.toString());
				packetInfoManager.saveAbisRequest(abisRequestDto);
			} else {
				abisRequestDto.setStatusCode(AbisStatusCode.IN_PROGRESS.toString());
				packetInfoManager.saveAbisRequest(abisRequestDto);
			}

		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisHandlerStage::createInsertRequest()::exit");
	}

	/**
	 * Gets the insert request bytes.
	 *
	 * @param regId
	 *            the reg id
	 * @param id
	 *            the id
	 * @param bioRefId
	 *            the bio ref id
	 * @return the insert request bytes
	 */
	private byte[] getInsertRequestBytes(String regId, String id, String bioRefId) {
		AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();
		abisInsertRequestDto.setId(MOSIP_ABIS_INSERT);
		abisInsertRequestDto.setReferenceId(bioRefId);
		abisInsertRequestDto.setReferenceURL(url + "/" + regId);
		abisInsertRequestDto.setRequestId(id);
		abisInsertRequestDto.setTimestamp(TIMESTAMP);
		abisInsertRequestDto.setVer(VERSION);
		try {
			String jsonString = JsonUtils.javaObjectToJsonString(abisInsertRequestDto);
			return jsonString.getBytes();
		} catch (JsonProcessingException e) {
			description = "Internal Error occured in Abis Handler identify request";
			regProcLogger.error("Internal Error occured in Abis Handler in identify", "", "", "");
			throw new AbisHandlerException(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode(), e);
		}
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	private String getUUID() {
		return UUID.randomUUID().toString();
	}
}
