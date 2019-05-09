package io.mosip.registration.processor.abis.handler.stage;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.abis.handler.exception.AbisHandlerException;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.*;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.*;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Class AbisHandlerStage.
 * 
 * @author M1048358 Alok
 */
@Service
public class AbisHandlerStage extends MosipVerticleManager {

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

	/** The description. */
	private String description = "";

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.ABIS_HANDLER_BUS_IN,
				MessageBusAddress.ABIS_HANDLER_BUS_OUT);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		Boolean isTransactionSuccessful = false;
		String regId = object.getRid();
		InternalRegistrationStatusDto registrationStatusDto = null;
		try {
			registrationStatusDto = registrationStatusService.getRegistrationStatus(regId);
			String transactionTypeCode = registrationStatusDto.getLatestTransactionTypeCode();
			String transactionId = registrationStatusDto.getLatestRegistrationTransactionId();
			String bioRefId = getUUID();

			Boolean isIdentifyRequestPresent = packetInfoManager.getIdentifyByTransactionId(transactionId, IDENTIFY);
			List<AbisApplicationDto> abisApplicationDtoList = packetInfoManager.getAllAbisDetails();

			if (!isIdentifyRequestPresent) {
				List<RegBioRefDto> bioRefDtos = packetInfoManager.getBioRefIdByRegId(regId);
				if (bioRefDtos.isEmpty()) {
					insertInBioRef(regId, bioRefId);
					createInsertRequest(abisApplicationDtoList, transactionId, bioRefId, regId);
					createIdentifyRequest(abisApplicationDtoList, transactionId, bioRefId, transactionTypeCode);
					object.setMessageBusAddress(MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN);
				} else {
					createIdentifyRequest(abisApplicationDtoList, transactionId, bioRefId, transactionTypeCode);
					object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);
				}
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
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		} finally {
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			String moduleId = isTransactionSuccessful ? "Abis-Handler Success" : "";
			String moduleName = "Abis-Handler";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, regId);
		}

		return object;
	}

	/**
	 * Creates the identify request.
	 *
	 * @param abisApplicationDtoList the abis application dto list
	 * @param transactionId the transaction id
	 * @param bioRefId the bio ref id
	 * @param transactionTypeCode the transaction type code
	 */
	private void createIdentifyRequest(List<AbisApplicationDto> abisApplicationDtoList, String transactionId,
			String bioRefId, String transactionTypeCode) {
		byte[] abisIdentifyRequestBytes = getIdentifyRequestBytes(transactionId, bioRefId, transactionTypeCode);
		String batchId = getUUID();
		for (AbisApplicationDto applicationDto : abisApplicationDtoList) {
			AbisRequestDto abisRequestDto = new AbisRequestDto();
			abisRequestDto.setId(getUUID());
			abisRequestDto.setAbisAppCode(applicationDto.getCode());
			abisRequestDto.setBioRefId(bioRefId);
			abisRequestDto.setRequestType(IDENTIFY);
			abisRequestDto.setReqBatchId(batchId);
			abisRequestDto.setRefRegtrnId(transactionId);
			abisRequestDto.setReqText(abisIdentifyRequestBytes);
			abisRequestDto.setStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			abisRequestDto.setStatusComment(null);
			abisRequestDto.setLangCode(ENG);
			abisRequestDto.setCrBy(USER);
			abisRequestDto.setCrDtimes(LocalDateTime.now());
			abisRequestDto.setUpdBy(null);
			abisRequestDto.setUpdDtimes(LocalDateTime.now());
			abisRequestDto.setIsDeleted(Boolean.FALSE);
			abisRequestDto.setRequestDtimes(LocalDateTime.now());
			packetInfoManager.saveAbisRequest(abisRequestDto);
		}
	}

	/**
	 * Gets the identify request bytes.
	 *
	 * @param transactionId the transaction id
	 * @param bioRefId the bio ref id
	 * @param transactionTypeCode the transaction type code
	 * @return the identify request bytes
	 */
	private byte[] getIdentifyRequestBytes(String transactionId, String bioRefId, String transactionTypeCode) {
		AbisIdentifyRequestDto abisIdentifyRequestDto = new AbisIdentifyRequestDto();
		abisIdentifyRequestDto.setId(MOSIP_ABIS_IDENTIFY);
		abisIdentifyRequestDto.setVer(VERSION);
		abisIdentifyRequestDto.setRequestId(getUUID());

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

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(abisIdentifyRequestDto);
			oos.flush();
		} catch (IOException e) {
			description = "Internal Error occured in Abis Handler identify request";
			regProcLogger.error("Internal Error occured in Abis Handler in identify", "", "", "");
			throw new AbisHandlerException(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode());
		}
		return bos.toByteArray();
	}

	/**
	 * Insert in bio ref.
	 *
	 * @param regId the reg id
	 * @param bioRefId the bio ref id
	 */
	private void insertInBioRef(String regId, String bioRefId) {
		RegBioRefDto regBioRefDto = new RegBioRefDto();
		regBioRefDto.setBioRefId(bioRefId);
		regBioRefDto.setCrBy(USER);
		regBioRefDto.setCrDtimes(LocalDateTime.now());
		regBioRefDto.setDelDtimes(null);
		regBioRefDto.setIsActive(Boolean.TRUE);
		regBioRefDto.setIsDeleted(Boolean.FALSE);
		regBioRefDto.setRegId(regId);
		regBioRefDto.setUpdBy(null);
		regBioRefDto.setUpdDtimes(LocalDateTime.now());
		packetInfoManager.saveBioRef(regBioRefDto);
	}

	/**
	 * Creates the insert request.
	 *
	 * @param abisApplicationDtoList the abis application dto list
	 * @param transactionId the transaction id
	 * @param bioRefId the bio ref id
	 * @param regId the reg id
	 */
	private void createInsertRequest(List<AbisApplicationDto> abisApplicationDtoList, String transactionId,
			String bioRefId, String regId) {
		byte[] abisInsertRequestBytes = getInsertRequestBytes(regId);
		String batchId = getUUID();
		for (AbisApplicationDto applicationDto : abisApplicationDtoList) {
			AbisRequestDto abisRequestDto = new AbisRequestDto();
			abisRequestDto.setId(getUUID());
			abisRequestDto.setAbisAppCode(applicationDto.getCode());
			abisRequestDto.setBioRefId(bioRefId);
			abisRequestDto.setRequestType(INSERT);
			abisRequestDto.setReqBatchId(batchId);
			abisRequestDto.setRefRegtrnId(transactionId);
			abisRequestDto.setReqText(abisInsertRequestBytes);
			abisRequestDto.setStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			abisRequestDto.setStatusComment(null);
			abisRequestDto.setLangCode(ENG);
			abisRequestDto.setCrBy(USER);
			abisRequestDto.setCrDtimes(LocalDateTime.now());
			abisRequestDto.setUpdBy(null);
			abisRequestDto.setUpdDtimes(LocalDateTime.now());
			abisRequestDto.setIsDeleted(Boolean.FALSE);
			abisRequestDto.setRequestDtimes(LocalDateTime.now());
			packetInfoManager.saveAbisRequest(abisRequestDto);
		}
	}

	/**
	 * Gets the insert request bytes.
	 *
	 * @param regId the reg id
	 * @return the insert request bytes
	 */
	private byte[] getInsertRequestBytes(String regId) {
		AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();
		abisInsertRequestDto.setId(MOSIP_ABIS_INSERT);
		abisInsertRequestDto.setReferenceId(getUUID());
		abisInsertRequestDto.setReferenceURL(url + regId);
		abisInsertRequestDto.setRequestId(getUUID());
		abisInsertRequestDto.setTimestamp(TIMESTAMP);
		abisInsertRequestDto.setVer(VERSION);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(abisInsertRequestDto);
			oos.flush();
		} catch (IOException e) {
			description = "Internal Error occured in Abis Handler in insert request";
			regProcLogger.error("Internal Error occured in Abis Handler in insert", "", "", "");
			throw new AbisHandlerException(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode());
		}
		return bos.toByteArray();
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
