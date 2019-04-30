package io.mosip.registration.processor.stages.demodedupe;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
@Transactional
public class DemodedupeProcessor {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(DemodedupeProcessor.class);

	public static final String FILE_SEPARATOR = "\\";

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The demo dedupe. */
	@Autowired
	private DemoDedupe demoDedupe;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The adapter. */
	@Autowired
	private FileSystemAdapter adapter;

	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	InputStream demographicInfoStream = null;

	byte[] bytesArray = null;
	private String description = "";

	private String code = "";

	public MessageDTO process(MessageDTO object, String stageName) {

		object.setMessageBusAddress(MessageBusAddress.DEMO_DEDUPE_BUS_IN);
		object.setInternalError(Boolean.FALSE);

		boolean isTransactionSuccessful = false;

		String registrationId = object.getRid();
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);

		try {
			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.DEMOGRAPHIC_VERIFICATION.toString());
			registrationStatusDto.setRegistrationStageName(stageName);
			registrationStatusDto.setLatestTransactionStatusCode("");

			// Persist Demographic packet Data if packet Registration type is NEW
			if (registrationStatusDto.getRegistrationType().equals(RegistrationType.NEW.name())) {
				InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
				PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
						PacketMetaInfo.class);
				demographicInfoStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
				bytesArray = IOUtils.toByteArray(demographicInfoStream);
				packetInfoManager.saveDemographicInfoJson(bytesArray, registrationId,
						packetMetaInfo.getIdentity().getMetaData());
			}

			// Potential Duplicate Ids after performing demo dedupe
			List<DemographicInfoDto> duplicateDtos = demoDedupe.performDedupe(registrationId);
			Set<String> uniqueUins = new HashSet<>();
			Set<String> uniqueMatchedRefIds = new HashSet<>();
			List<String> uniqueMatchedRefIdList = new ArrayList<>();
			for (DemographicInfoDto demographicInfoDto : duplicateDtos) {
				uniqueUins.add(demographicInfoDto.getUin());
				uniqueMatchedRefIds.add(demographicInfoDto.getRegId());
			}
			uniqueMatchedRefIdList.addAll(uniqueMatchedRefIds);
			List<String> duplicateUINList = new ArrayList<>(uniqueUins);

			if (!duplicateDtos.isEmpty()) {

				registrationStatusDto
						.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_POTENTIAL_MATCH_FOUND.toString());
				registrationStatusDto.setStatusComment(StatusMessage.POTENTIAL_MATCH_FOUND);

				registrationStatusService.updateRegistrationStatus(registrationStatusDto);
				// authenticating duplicateIds with provided packet biometrics
				boolean isDuplicateAfterAuth = demoDedupe.authenticateDuplicates(registrationId, duplicateUINList);

				if (isDuplicateAfterAuth) {
					object.setIsValid(Boolean.FALSE);

					int retryCount = registrationStatusDto.getRetryCount() != null
							? registrationStatusDto.getRetryCount() + 1
							: 1;
					description = registrationStatusDto.getStatusComment() + " -- " +registrationId;
					registrationStatusDto.setRetryCount(retryCount);

					registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_FAILED);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.toString());
					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());

					demographicDedupeRepository.updateIsActiveIfDuplicateFound(registrationId);
					// Saving potential duplicates in reg_manual_verification table
					packetInfoManager.saveManualAdjudicationData(uniqueMatchedRefIdList, registrationId,
							DedupeSourceName.DEMO);

				} else {
					object.setIsValid(Boolean.TRUE);
					registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_SUCCESS);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_SUCCESS.toString());

					code = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP_POTENTIAL_DUPLICATION_FOUND.getCode();
					description = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP_POTENTIAL_DUPLICATION_FOUND.getMessage()
							+ " -- " + registrationId;
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), code, registrationId, description);
					registrationStatusDto.setUpdatedBy(USER);
					isTransactionSuccessful = true;
				}

			} else {
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setStatusComment(StatusMessage.DEMO_DEDUPE_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_SUCCESS.toString());

				code = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode();
				description = PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getMessage() + " -- " +registrationId;
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), code, registrationId, description);
				registrationStatusDto.setUpdatedBy(USER);
				isTransactionSuccessful = true;
			}

			

		} catch (IOException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (ApisResourceAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_REPROCESSING.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_API_RESOURCE_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (ParseException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.PARSE_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (FSAdapterException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_REPROCESSING.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_PACKET_STORE_NOT_ACCESSIBLE.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (IllegalAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_ILLEGAL_ACCESS_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.ILLEGAL_ACCESS_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (IllegalArgumentException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_ILLEGAL_ACCESS_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.ILLEGAL_ARGUMENT_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (InvocationTargetException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_INVOCATION_TARGET_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.INVOCATION_TARGET_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (IntrospectionException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_INTROSPECTION_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.INTROSPECTION_EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.DEMO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			code = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} finally {

			if (registrationStatusDto.getLatestTransactionStatusCode() == "")
				registrationStatusDto.setLatestTransactionStatusCode("SUCCESS");
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

			
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_DEMO_DE_DUP.getCode() : code;
			String moduleName = ModuleName.DEMO_DEDUPE.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);

		}

		return object;
	}

}
