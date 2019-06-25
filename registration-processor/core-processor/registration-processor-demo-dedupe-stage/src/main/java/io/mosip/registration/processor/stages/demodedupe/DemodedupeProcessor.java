package io.mosip.registration.processor.stages.demodedupe;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.AbisStatusCode;
import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.AbisConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.app.constants.DemoDedupeConstants;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class DemodedupeProcessor.
 */
@Service
@Transactional
public class DemodedupeProcessor {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(DemodedupeProcessor.class);

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The demo dedupe. */
	@Autowired
	private DemoDedupe demoDedupe;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The adapter. */
	@Autowired
	private PacketManager adapter;

	/** The registration exception mapper util. */
	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	/** The utility. */
	@Autowired
	Utilities utility;

	/** The registration status dao. */
	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	/** The reg processor identity json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The abis handler util. */
	@Autowired
	private ABISHandlerUtil abisHandlerUtil;

	/** The is match found. */
	private volatile boolean isMatchFound = false;

	/**
	 * Process.
	 *
	 * @param object the object
	 * @param stageName the stage name
	 * @return the message DTO
	 */
	public MessageDTO process(MessageDTO object, String stageName) {
		LogDescription description=new LogDescription();
		object.setMessageBusAddress(MessageBusAddress.DEMO_DEDUPE_BUS_IN);
		object.setInternalError(Boolean.FALSE);
		object.setIsValid(Boolean.FALSE);
		isMatchFound = false;


		/** The duplicate dtos. */
		List<DemographicInfoDto> duplicateDtos = new ArrayList<>();

		/** The demographic info stream. */
		InputStream demographicInfoStream = null;

		/** The bytes array. */
		byte[] bytesArray = null;
		
		boolean isTransactionSuccessful = false;

		String registrationId = object.getRid();
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);

		try {

			// Persist Demographic packet Data if packet Registration type is NEW
			if (registrationStatusDto.getRegistrationType().equals(RegistrationType.NEW.name())) {

				String packetStatus = abisHandlerUtil.getPacketStatus(registrationStatusDto);

				if (packetStatus.equalsIgnoreCase(AbisConstant.PRE_ABIS_IDENTIFICATION)) {
					InputStream packetMetaInfoStream = adapter.getFile(registrationId,
							PacketFiles.PACKET_META_INFO.name());
					PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil
							.inputStreamtoJavaObject(packetMetaInfoStream, PacketMetaInfo.class);
					demographicInfoStream = adapter.getFile(registrationId,
							PacketFiles.DEMOGRAPHIC.name() + DemoDedupeConstants.FILE_SEPARATOR + PacketFiles.ID.name());
					bytesArray = IOUtils.toByteArray(demographicInfoStream);
					packetInfoManager.saveDemographicInfoJson(bytesArray, registrationId,
							packetMetaInfo.getIdentity().getMetaData());
					duplicateDtos = performDemoDedupe(registrationStatusDto, object, description);
					if (duplicateDtos.isEmpty()) 
						isTransactionSuccessful= true; 
				} else if (packetStatus.equalsIgnoreCase(AbisConstant.POST_ABIS_IDENTIFICATION)) {
					isTransactionSuccessful = processDemoDedupeRequesthandler(registrationStatusDto, object, description);
				}

			} else if (registrationStatusDto.getRegistrationType().equals(RegistrationType.UPDATE.name())
					|| registrationStatusDto.getRegistrationType().equals(RegistrationType.RES_UPDATE.name())) {
				IndividualDemographicDedupe demoDedupeData = new IndividualDemographicDedupe();

				demographicInfoStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + DemoDedupeConstants.FILE_SEPARATOR + PacketFiles.ID.name());
				bytesArray = IOUtils.toByteArray(demographicInfoStream);
				String demographicJsonString = new String(bytesArray);
				IndividualDemographicDedupe demographicData = packetInfoManager
						.getIdentityKeysAndFetchValuesFromJSON(demographicJsonString);
				regProcessorIdentityJson = utility.getRegistrationProcessorIdentityJson();
				Long uinFieldCheck = utility.getUIn(registrationId);
				JSONObject jsonObject = utility.retrieveIdrepoJson(uinFieldCheck);
				if (jsonObject == null) {
					throw new IdRepoAppException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
				}
				demoDedupeData.setName(demographicData.getName() == null
						? JsonUtil.getJsonValues(jsonObject,
								regProcessorIdentityJson.getIdentity().getName().getValue())
						: demographicData.getName());
				demoDedupeData.setDateOfBirth(demographicData.getDateOfBirth() == null
						? JsonUtil.getJSONValue(jsonObject, regProcessorIdentityJson.getIdentity().getDob().getValue())
						: demographicData.getDateOfBirth());
				demoDedupeData.setGender(demographicData.getGender() == null
						? JsonUtil.getJsonValues(jsonObject,
								regProcessorIdentityJson.getIdentity().getGender().getValue())
						: demographicData.getGender());
				packetInfoManager.saveIndividualDemographicDedupeUpdatePacket(demoDedupeData, registrationId);
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
				registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			}

			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.DEMOGRAPHIC_VERIFICATION.toString());
			registrationStatusDto.setRegistrationStageName(stageName);

		} catch (FSAdapterException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto
					.setStatusComment(PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			description.setCode(PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getCode());
			description.setMessage(PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), description.getCode(), registrationId,
					description.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (IllegalArgumentException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_ILLEGAL_ACCESS_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.ILLEGAL_ARGUMENT_EXCEPTION));
			description.setCode(PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode());
			description.setMessage(PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), description.getCode(), registrationId,
					description.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			description.setCode(PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode());
			description.setMessage(PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), description.getCode(), registrationId,
					description.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} finally {
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			try {
				if (isMatchFound) {
					saveDuplicateDtoList(duplicateDtos, registrationStatusDto, object);
				}
			} catch (Exception e) {
				registrationStatusDto.setRegistrationStageName(stageName);
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
				registrationStatusService.updateRegistrationStatus(registrationStatusDto);
                description.setMessage(DemoDedupeConstants.NO_DATA_IN_DEMO);
                regProcLogger.error(DemoDedupeConstants.NO_DATA_IN_DEMO, "", "", ExceptionUtils.getStackTrace(e));
                object.setIsValid(Boolean.FALSE);
                object.setMessageBusAddress(MessageBusAddress.DEMO_DEDUPE_BUS_IN);
                object.setInternalError(Boolean.TRUE);
			}

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode() : description.getCode();
			String moduleName = ModuleName.DEMO_DEDUPE.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType, moduleId,
					moduleName, registrationId);

		}

		return object;
	}

	/**
	 * Perform demo dedupe.
	 *
	 * @param registrationStatusDto the registration status dto
	 * @param object the object
	 * @param description 
	 * @return true, if successful
	 */
	private List<DemographicInfoDto> performDemoDedupe(InternalRegistrationStatusDto registrationStatusDto, MessageDTO object, LogDescription description) {
		String registrationId = registrationStatusDto.getRegistrationId();
		// Potential Duplicate Ids after performing demo dedupe
		List<DemographicInfoDto> duplicateDtos = demoDedupe.performDedupe(registrationStatusDto.getRegistrationId());

		if (!duplicateDtos.isEmpty()) {
			isMatchFound = true;
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			registrationStatusDto.setStatusCode(RegistrationStatusCode.REJECTED.toString());
			registrationStatusDto.setStatusComment(StatusMessage.POTENTIAL_MATCH_FOUND);
			object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);
			description.setCode(PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode());
			description.setMessage(DemoDedupeConstants.RECORD_INSERTED_FROM_ABIS_HANDLER
					+ " -- " + registrationId);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					DemoDedupeConstants.RECORD_INSERTED_FROM_ABIS_HANDLER);

		} else {
			object.setIsValid(Boolean.TRUE);
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
			registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_SUCCESS);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());

			description.setCode(PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode());
			description.setMessage(PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getMessage() + " -- " + registrationId);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), description.getCode(), registrationId, description.getMessage());
			registrationStatusDto.setUpdatedBy(DemoDedupeConstants.USER);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), DemoDedupeConstants.DEMO_SUCCESS);
		}
		return duplicateDtos;
	}

	/**
	 * Gets the latest transaction id.
	 *
	 * @param registrationId the registration id
	 * @return the latest transaction id
	 */
	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;
	}

	/**
	 * Process demo dedupe requesthandler.
	 *
	 * @param registrationStatusDto the registration status dto
	 * @param object the object
	 * @param description 
	 * @return true, if successful
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	private boolean processDemoDedupeRequesthandler(InternalRegistrationStatusDto registrationStatusDto,
			MessageDTO object, LogDescription description) throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		boolean isTransactionSuccessful = false;
		List<String> responsIds = new ArrayList<>();

		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());

		List<AbisResponseDto> abisResponseDto = packetInfoManager.getAbisResponseRecords(latestTransactionId, DemoDedupeConstants.IDENTIFY);

		for (AbisResponseDto responseDto : abisResponseDto) {
			if (responseDto.getStatusCode().equalsIgnoreCase(AbisStatusCode.SUCCESS.toString())) {
				responsIds.add(responseDto.getId());
			} else {
				isTransactionSuccessful = true;
				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;
				description.setMessage(DemoDedupeConstants.SENDING_TO_REPROCESS + " -- "
						+ registrationStatusDto.getRegistrationId());
				registrationStatusDto.setRetryCount(retryCount);

				registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.DEMO_DEDUPE_ABIS_RESPONSE_ERROR));
				registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_FAILED_IN_ABIS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.REJECTED.toString());
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						DemoDedupeConstants.SENDING_TO_REPROCESS);
			}
		}

		if (!responsIds.isEmpty()) {
			List<AbisResponseDetDto> abisResponseDetDto = packetInfoManager.getAbisResponseDetRecordsList(responsIds);
			if (abisResponseDetDto.isEmpty()) {
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
				registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						DemoDedupeConstants.NO_DUPLICATES_FOUND);
				isTransactionSuccessful = true;
			} else {
				object.setIsValid(Boolean.FALSE);
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
				registrationStatusDto
						.setStatusCode(RegistrationStatusCode.REJECTED.toString());
				registrationStatusDto.setStatusComment(
						StatusMessage.POTENTIAL_MATCH_FOUND_IN_ABIS + registrationStatusDto.getRegistrationId());
				saveManualAdjudicationData(registrationStatusDto);
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						DemoDedupeConstants.SENDING_FOR_MANUAL);
			}
		}

		return isTransactionSuccessful;
	}

	/**
	 * Save duplicate dto list.
	 *
	 * @param duplicateDtos the duplicate dtos
	 * @param registrationStatusDto the registration status dto
	 * @return true, if successful
	 */
	private boolean saveDuplicateDtoList(List<DemographicInfoDto> duplicateDtos,
			InternalRegistrationStatusDto registrationStatusDto, MessageDTO object) {
		boolean isDataSaved = false;
		int numberOfProcessedPackets = 0;
		for (DemographicInfoDto demographicInfoDto : duplicateDtos) {
			InternalRegistrationStatusDto potentialMatchRegistrationDto = registrationStatusService
					.getRegistrationStatus(demographicInfoDto.getRegId());
			if (potentialMatchRegistrationDto.getLatestTransactionStatusCode()
					.equalsIgnoreCase(RegistrationTransactionStatusCode.REPROCESS.toString())
					|| potentialMatchRegistrationDto.getLatestTransactionStatusCode().equalsIgnoreCase(AbisConstant.RE_REGISTER)) {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						DemoDedupeConstants.REJECTED_OR_REREGISTER);
			} else if (potentialMatchRegistrationDto.getLatestTransactionStatusCode()
					.equalsIgnoreCase(RegistrationTransactionStatusCode.IN_PROGRESS.toString())
					|| potentialMatchRegistrationDto.getLatestTransactionStatusCode()
							.equalsIgnoreCase(RegistrationTransactionStatusCode.PROCESSED.toString())) {
				String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());
				RegDemoDedupeListDto regDemoDedupeListDto = new RegDemoDedupeListDto();
				regDemoDedupeListDto.setRegId(registrationStatusDto.getRegistrationId());
				regDemoDedupeListDto.setMatchedRegId(demographicInfoDto.getRegId());
				regDemoDedupeListDto.setRegtrnId(latestTransactionId);
				regDemoDedupeListDto.setIsDeleted(Boolean.FALSE);
				regDemoDedupeListDto.setCrBy(DemoDedupeConstants.CREATED_BY);
				packetInfoManager.saveDemoDedupePotentialData(regDemoDedupeListDto);
				isDataSaved = true;
				numberOfProcessedPackets++;
			} else {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
						"The packet status is something different");
			}
			if (numberOfProcessedPackets == 0) {
				object.setIsValid(Boolean.TRUE);
			}
		}
		return isDataSaved;
	}

	/**
	 * Save manual adjudication data.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	private void saveManualAdjudicationData(InternalRegistrationStatusDto registrationStatusDto)
			throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		List<String> matchedRegIds = abisHandlerUtil.getUniqueRegIds(registrationStatusDto.getRegistrationId(),
				SyncTypeDto.NEW.toString());
		if (!matchedRegIds.isEmpty()) {
			packetInfoManager.saveManualAdjudicationData(matchedRegIds, registrationStatusDto.getRegistrationId(),
					DedupeSourceName.DEMO);
		} else {
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					DemoDedupeConstants.NO_MATCH_FOUND);
		}

	}

}
