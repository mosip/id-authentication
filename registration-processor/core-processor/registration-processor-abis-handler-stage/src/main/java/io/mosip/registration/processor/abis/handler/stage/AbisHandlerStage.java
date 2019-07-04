package io.mosip.registration.processor.abis.handler.stage;

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
import io.mosip.registration.processor.abis.handler.constant.AbisHandlerStageConstant;
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
import io.mosip.registration.processor.core.logger.LogDescription;
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
		LogDescription description = new LogDescription();
		object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);
		Boolean isTransactionSuccessful = false;
		String regId = object.getRid();
		InternalRegistrationStatusDto registrationStatusDto = null;
		String bioRefId = null;
		String transactionTypeCode = null;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "AbisHandlerStage::process()::entry");
		try {
			registrationStatusDto = registrationStatusService.getRegistrationStatus(regId);
			transactionTypeCode = registrationStatusDto.getLatestTransactionTypeCode();
			String transactionId = registrationStatusDto.getLatestRegistrationTransactionId();

			Boolean isIdentifyRequestPresent = packetInfoManager.getIdentifyByTransactionId(transactionId,
					AbisHandlerStageConstant.IDENTIFY);

			if (!isIdentifyRequestPresent) {
				List<AbisQueueDetails> abisQueueDetails = utility.getAbisQueueDetails();
				if (abisQueueDetails.isEmpty()) {
					description.setMessage(AbisHandlerStageConstant.DETAILS_NOT_FOUND);
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "",
							AbisHandlerStageConstant.DETAILS_NOT_FOUND);
					throw new AbisHandlerException(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode());
				}
				List<RegBioRefDto> bioRefDtos = packetInfoManager.getBioRefIdByRegId(regId);

				if (bioRefDtos.isEmpty()) {
					bioRefId = getUUID();
					insertInBioRef(regId, bioRefId);
				} else {
					bioRefId = bioRefDtos.get(0).getBioRefId();
				}
				createInsertRequest(abisQueueDetails, transactionId, bioRefId, regId, description);
				createIdentifyRequest(abisQueueDetails, transactionId, bioRefId, transactionTypeCode, description);
				object.setMessageBusAddress(MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN);
			} else {
				if (transactionTypeCode.equalsIgnoreCase(AbisHandlerStageConstant.DEMOGRAPHIC_VERIFICATION)) {
					object.setMessageBusAddress(MessageBusAddress.DEMO_DEDUPE_BUS_IN);
				} else if (transactionTypeCode.equalsIgnoreCase(AbisHandlerStageConstant.BIOGRAPHIC_VERIFICATION)) {
					object.setMessageBusAddress(MessageBusAddress.BIO_DEDUPE_BUS_IN);
				}
			}
			description.setMessage(AbisHandlerStageConstant.ABIS_HANDLER_SUCCESS);
			isTransactionSuccessful = true;
		} catch (Exception e) {
			description.setMessage(AbisHandlerStageConstant.ERROR_IN_ABIS_HANDLER);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
			if (transactionTypeCode.equalsIgnoreCase(AbisHandlerStageConstant.DEMOGRAPHIC_VERIFICATION)) {
				registrationStatusDto.setRegistrationStageName(AbisHandlerStageConstant.DEMO_DEDUPE_STAGE);
			} else if (transactionTypeCode.equalsIgnoreCase(AbisHandlerStageConstant.BIOGRAPHIC_VERIFICATION)) {
				registrationStatusDto.setRegistrationStageName(AbisHandlerStageConstant.BIO_DEDUPE_STAGE);
			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		} finally {
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			String moduleId = isTransactionSuccessful ? "Abis-Handler Success" : "";
			String moduleName = "Abis-Handler";

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					moduleId, moduleName, regId);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "AbisHandlerStage::process()::exit");
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
	 * @param description
	 */
	private void createIdentifyRequest(List<AbisQueueDetails> abisQueueDetails, String transactionId, String bioRefId,
			String transactionTypeCode, LogDescription description) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisHandlerStage::createIdentifyRequest()::entry");
		String batchId = getUUID();
		for (AbisQueueDetails abisQueue : abisQueueDetails) {
			AbisRequestDto abisRequestDto = new AbisRequestDto();
			String id = getUUID();
			abisRequestDto.setId(id);
			abisRequestDto.setAbisAppCode(abisQueue.getName());
			abisRequestDto.setBioRefId(bioRefId);
			abisRequestDto.setRequestType(AbisHandlerStageConstant.IDENTIFY);
			abisRequestDto.setReqBatchId(batchId);
			abisRequestDto.setRefRegtrnId(transactionId);

			byte[] abisIdentifyRequestBytes = getIdentifyRequestBytes(transactionId, bioRefId, transactionTypeCode, id,
					description);
			abisRequestDto.setReqText(abisIdentifyRequestBytes);

			abisRequestDto.setStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			abisRequestDto.setStatusComment(null);
			abisRequestDto.setLangCode(AbisHandlerStageConstant.ENG);
			abisRequestDto.setCrBy(AbisHandlerStageConstant.USER);
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
	 * @param description
	 * @return the identify request bytes
	 */
	private byte[] getIdentifyRequestBytes(String transactionId, String bioRefId, String transactionTypeCode, String id,
			LogDescription description) {
		AbisIdentifyRequestDto abisIdentifyRequestDto = new AbisIdentifyRequestDto();
		abisIdentifyRequestDto.setId(AbisHandlerStageConstant.MOSIP_ABIS_IDENTIFY);
		abisIdentifyRequestDto.setVer(AbisHandlerStageConstant.VERSION);
		abisIdentifyRequestDto.setRequestId(id);
		abisIdentifyRequestDto.setReferenceId(bioRefId);
		abisIdentifyRequestDto.setTimestamp(AbisHandlerStageConstant.TIMESTAMP);
		abisIdentifyRequestDto.setMaxResults(maxResults);
		abisIdentifyRequestDto.setTargetFPIR(targetFPIR);

		// Added Gallery data for demo dedupe
		if (transactionTypeCode.equalsIgnoreCase(AbisHandlerStageConstant.DEMOGRAPHIC_VERIFICATION)) {
			List<RegDemoDedupeListDto> regDemoDedupeListDtoList = packetInfoManager
					.getDemoListByTransactionId(transactionId);
			if (regDemoDedupeListDtoList.isEmpty()) {
				description.setMessage(AbisHandlerStageConstant.NO_RECORD_FOUND);

				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"Potential Match Records are Not Found for Demo Dedupe Potential Match");
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
			description.setMessage(AbisHandlerStageConstant.ERROR_IN_ABIS_HANDLER_IDENTIFY_REQUEST);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", AbisHandlerStageConstant.ERROR_IN_ABIS_HANDLER_IDENTIFY_REQUEST);
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
		regBioRefDto.setCrBy(AbisHandlerStageConstant.USER);
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
	 * @param description
	 */
	private void createInsertRequest(List<AbisQueueDetails> abisQueueDetails, String transactionId, String bioRefId,
			String regId, LogDescription description) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "AbisHandlerStage::createInsertRequest()::entry");
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
			abisRequestDto.setRequestType(AbisHandlerStageConstant.INSERT);
			abisRequestDto.setReqBatchId(batchId);
			abisRequestDto.setRefRegtrnId(transactionId);

			byte[] abisInsertRequestBytes = getInsertRequestBytes(regId, id, bioRefId, description);
			abisRequestDto.setReqText(abisInsertRequestBytes);

			abisRequestDto.setStatusCode(AbisStatusCode.IN_PROGRESS.toString());
			abisRequestDto.setStatusComment(null);
			abisRequestDto.setLangCode(AbisHandlerStageConstant.ENG);
			abisRequestDto.setCrBy(AbisHandlerStageConstant.USER);
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
	 * @param description
	 * @return the insert request bytes
	 */
	private byte[] getInsertRequestBytes(String regId, String id, String bioRefId, LogDescription description) {
		AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();
		abisInsertRequestDto.setId(AbisHandlerStageConstant.MOSIP_ABIS_INSERT);
		abisInsertRequestDto.setReferenceId(bioRefId);
		abisInsertRequestDto.setReferenceURL(url + "/" + regId);
		abisInsertRequestDto.setRequestId(id);
		abisInsertRequestDto.setTimestamp(AbisHandlerStageConstant.TIMESTAMP);
		abisInsertRequestDto.setVer(AbisHandlerStageConstant.VERSION);
		try {
			String jsonString = JsonUtils.javaObjectToJsonString(abisInsertRequestDto);
			return jsonString.getBytes();
		} catch (JsonProcessingException e) {
			description.setMessage(AbisHandlerStageConstant.ERROR_IN_ABIS_HANDLER_IDENTIFY_REQUEST);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", AbisHandlerStageConstant.ERROR_IN_ABIS_HANDLER_IDENTIFY_REQUEST);
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
