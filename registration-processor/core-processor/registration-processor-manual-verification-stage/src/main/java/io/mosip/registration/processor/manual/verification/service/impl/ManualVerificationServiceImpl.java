package io.mosip.registration.processor.manual.verification.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.constant.RegistrationType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.util.PacketStructure;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.kernel.master.dto.UserResponseDTOWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.status.util.StatusUtil;
import io.mosip.registration.processor.core.status.util.TrimExceptionMessage;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.manual.verification.constants.ManualVerificationConstants;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationStatus;
import io.mosip.registration.processor.manual.verification.dto.UserDto;
import io.mosip.registration.processor.manual.verification.exception.InvalidFileNameException;
import io.mosip.registration.processor.manual.verification.exception.InvalidUpdateException;
import io.mosip.registration.processor.manual.verification.exception.MatchTypeNotFoundException;
import io.mosip.registration.processor.manual.verification.exception.NoRecordAssignedException;
import io.mosip.registration.processor.manual.verification.exception.UserIDNotPresentException;
import io.mosip.registration.processor.manual.verification.service.ManualVerificationService;
import io.mosip.registration.processor.manual.verification.stage.ManualVerificationStage;
import io.mosip.registration.processor.manual.verification.util.StatusMessage;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class ManualVerificationServiceImpl.
 */
@Component
@Transactional
public class ManualVerificationServiceImpl implements ManualVerificationService {

	/** The logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ManualVerificationServiceImpl.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";
	/** The audit log request builder. */

	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The filesystem ceph adapter impl. */
	@Autowired
	private PacketManager filesystemCephAdapterImpl;

	/** The base packet repository. */
	@Autowired
	private BasePacketRepository<ManualVerificationEntity, String> basePacketRepository;

	/** The manual verification stage. */
	@Autowired
	private ManualVerificationStage manualVerificationStage;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	RegistrationExceptionMapperUtil registrationExceptionMapperUtil;

	/*
	 * * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.manual.adjudication.service.
	 * ManualAdjudicationService#assignStatus(io.mosip.registration.processor.manual
	 * .adjudication.dto.UserDto)
	 */

	@Override
	public ManualVerificationDTO assignApplicant(UserDto dto) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				dto.getUserId(), "ManualVerificationServiceImpl::assignApplicant()::entry");
		ManualVerificationDTO manualVerificationDTO = new ManualVerificationDTO();
		List<ManualVerificationEntity> entities;
		String matchType = dto.getMatchType();
		if (dto.getUserId() == null || dto.getUserId().isEmpty()) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
					dto.getUserId(), "ManualVerificationServiceImpl::assignApplicant()::UserIDNotPresentException"
							+ PlatformErrorMessages.RPR_MVS_NO_USER_ID_SHOULD_NOT_EMPTY_OR_NULL.getMessage());
			throw new UserIDNotPresentException(
					PlatformErrorMessages.RPR_MVS_NO_USER_ID_SHOULD_NOT_EMPTY_OR_NULL.getCode(),
					PlatformErrorMessages.RPR_MVS_NO_USER_ID_SHOULD_NOT_EMPTY_OR_NULL.getMessage());
		}
		checkUserIDExistsInMasterList(dto);
		entities = basePacketRepository.getAssignedApplicantDetails(dto.getUserId(),
				ManualVerificationStatus.ASSIGNED.name());

		if (!(matchType.equalsIgnoreCase(DedupeSourceName.ALL.toString())
				|| matchType.equalsIgnoreCase(DedupeSourceName.DEMO.toString())
				|| matchType.equalsIgnoreCase(DedupeSourceName.BIO.toString()))) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
					dto.getUserId(), "ManualVerificationServiceImpl::assignApplicant()"
							+ PlatformErrorMessages.RPR_MVS_NO_MATCH_TYPE_PRESENT.getMessage());
			throw new MatchTypeNotFoundException(PlatformErrorMessages.RPR_MVS_NO_MATCH_TYPE_PRESENT.getCode(),
					PlatformErrorMessages.RPR_MVS_NO_MATCH_TYPE_PRESENT.getMessage());
		}
		ManualVerificationEntity manualVerificationEntity;

		if (!entities.isEmpty()) {
			manualVerificationEntity = entities.get(0);
			manualVerificationDTO.setRegId(manualVerificationEntity.getId().getRegId());
			manualVerificationDTO.setMatchedRefId(manualVerificationEntity.getId().getMatchedRefId());
			manualVerificationDTO.setMatchedRefType(manualVerificationEntity.getId().getMatchedRefType());
			manualVerificationDTO.setMvUsrId(manualVerificationEntity.getMvUsrId());
			manualVerificationDTO.setStatusCode(manualVerificationEntity.getStatusCode());
			manualVerificationDTO.setReasonCode(manualVerificationEntity.getReasonCode());
		} else {
			if (matchType.equalsIgnoreCase(DedupeSourceName.ALL.toString())) {
				entities = basePacketRepository.getFirstApplicantDetailsForAll(ManualVerificationStatus.PENDING.name());
			} else if (matchType.equalsIgnoreCase(DedupeSourceName.DEMO.toString())
					|| matchType.equalsIgnoreCase(DedupeSourceName.BIO.toString())) {
				entities = basePacketRepository.getFirstApplicantDetails(ManualVerificationStatus.PENDING.name(),
						matchType);
			}
			if (entities.isEmpty()) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
						dto.getUserId(), "ManualVerificationServiceImpl::assignApplicant()"
								+ PlatformErrorMessages.RPR_MVS_NO_ASSIGNED_RECORD.getMessage());
				throw new NoRecordAssignedException(PlatformErrorMessages.RPR_MVS_NO_ASSIGNED_RECORD.getCode(),
						PlatformErrorMessages.RPR_MVS_NO_ASSIGNED_RECORD.getMessage());
			} else {
				manualVerificationEntity = entities.get(0);
				manualVerificationEntity.setStatusCode(ManualVerificationStatus.ASSIGNED.name());
				manualVerificationEntity.setMvUsrId(dto.getUserId());
				ManualVerificationEntity updatedManualVerificationEntity = basePacketRepository
						.update(manualVerificationEntity);
				if (updatedManualVerificationEntity != null) {
					manualVerificationDTO.setRegId(updatedManualVerificationEntity.getId().getRegId());
					manualVerificationDTO.setMatchedRefId(updatedManualVerificationEntity.getId().getMatchedRefId());
					manualVerificationDTO
							.setMatchedRefType(updatedManualVerificationEntity.getId().getMatchedRefType());
					manualVerificationDTO.setMvUsrId(updatedManualVerificationEntity.getMvUsrId());
					manualVerificationDTO.setStatusCode(updatedManualVerificationEntity.getStatusCode());
				}
			}

		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				dto.getUserId(), "ManualVerificationServiceImpl::assignApplicant()::exit");
		return manualVerificationDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.manual.adjudication.service.
	 * ManualAdjudicationService#getApplicantFile(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public byte[] getApplicantFile(String regId, String fileName) throws PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException, IOException {

		byte[] file = null;
		InputStream fileInStream = null;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "ManualVerificationServiceImpl::getApplicantFile()::entry");
		if (regId == null || regId.isEmpty()) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, "ManualVerificationServiceImpl::getApplicantFile()"
							+ PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getMessage());
			throw new InvalidFileNameException(PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getCode(),
					PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getMessage());
		}
		if (PacketFiles.BIOMETRIC.name().equals(fileName)) {
			fileInStream = getApplicantBiometricFile(regId, PacketFiles.APPLICANT_BIO_CBEFF.name());
		} else if (PacketFiles.DEMOGRAPHIC.name().equals(fileName)) {
			fileInStream = getApplicantDemographicFile(regId, PacketFiles.ID.name());
		} else {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, "ManualVerificationServiceImpl::getApplicantFile()"
							+ PlatformErrorMessages.RPR_MVS_INVALID_FILE_REQUEST.getMessage());
			throw new InvalidFileNameException(PlatformErrorMessages.RPR_MVS_INVALID_FILE_REQUEST.getCode(),
					PlatformErrorMessages.RPR_MVS_INVALID_FILE_REQUEST.getMessage());
		}
		try {
			file = IOUtils.toByteArray(fileInStream);
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "ManualVerificationServiceImpl::getApplicantFile()::exit");
		return file;
	}

	/**
	 * Gets the applicant biometric file.
	 *
	 * @param regId
	 *            the reg id
	 * @param fileName
	 *            the file name
	 * @return the applicant biometric file
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws ApisResourceAccessException
	 * @throws PacketDecryptionFailureException
	 */
	private InputStream getApplicantBiometricFile(String regId, String fileName)
			throws PacketDecryptionFailureException, ApisResourceAccessException,
			io.mosip.kernel.core.exception.IOException, IOException {
		return filesystemCephAdapterImpl.getFile(regId, PacketStructure.BIOMETRIC + fileName);
	}

	/**
	 * Gets the applicant demographic file.
	 *
	 * @param regId
	 *            the reg id
	 * @param fileName
	 *            the file name
	 * @return the applicant demographic file
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws ApisResourceAccessException
	 * @throws PacketDecryptionFailureException
	 */
	private InputStream getApplicantDemographicFile(String regId, String fileName)
			throws PacketDecryptionFailureException, ApisResourceAccessException,
			io.mosip.kernel.core.exception.IOException, IOException {
		return filesystemCephAdapterImpl.getFile(regId, PacketStructure.APPLICANTDEMOGRAPHIC + fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.manual.adjudication.service.
	 * ManualAdjudicationService#updatePacketStatus(io.mosip.registration.processor.
	 * manual.adjudication.dto.ManualVerificationDTO)
	 */
	@Override
	public ManualVerificationDTO updatePacketStatus(ManualVerificationDTO manualVerificationDTO, String stageName) {
		TrimExceptionMessage trimExceptionMessage = new TrimExceptionMessage();
		String registrationId = manualVerificationDTO.getRegId();
		String matchedRefId = manualVerificationDTO.getMatchedRefId();
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setInternalError(false);
		messageDTO.setIsValid(false);
		messageDTO.setRid(manualVerificationDTO.getRegId());
		if (registrationId == null || registrationId.isEmpty() || matchedRefId == null || matchedRefId.isEmpty()) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "ManualVerificationServiceImpl::updatePacketStatus()::InvalidFileNameException"
							+ PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getMessage());
			throw new InvalidFileNameException(PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getCode(),
					PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getMessage());
		}

		String description = "";
		boolean isTransactionSuccessful = false;
		ManualVerificationEntity manualVerificationEntity;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				manualVerificationDTO.getRegId(), "ManualVerificationServiceImpl::updatePacketStatus()::entry");
		if (!manualVerificationDTO.getStatusCode().equalsIgnoreCase(ManualVerificationStatus.REJECTED.name())
				&& !manualVerificationDTO.getStatusCode().equalsIgnoreCase(ManualVerificationStatus.APPROVED.name())) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "ManualVerificationServiceImpl::updatePacketStatus()"
							+ PlatformErrorMessages.RPR_MVS_INVALID_STATUS_UPDATE.getMessage());
			throw new InvalidUpdateException(PlatformErrorMessages.RPR_MVS_INVALID_STATUS_UPDATE.getCode(),
					PlatformErrorMessages.RPR_MVS_INVALID_STATUS_UPDATE.getMessage());
		}
		List<ManualVerificationEntity> entities = basePacketRepository.getSingleAssignedRecord(
				manualVerificationDTO.getRegId(), manualVerificationDTO.getMatchedRefId(),
				manualVerificationDTO.getMvUsrId(), ManualVerificationStatus.ASSIGNED.name());

		if (entities.isEmpty()) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "ManualVerificationServiceImpl::updatePacketStatus()"
							+ PlatformErrorMessages.RPR_MVS_NO_ASSIGNED_RECORD.getMessage());
			throw new NoRecordAssignedException(PlatformErrorMessages.RPR_MVS_NO_ASSIGNED_RECORD.getCode(),
					PlatformErrorMessages.RPR_MVS_NO_ASSIGNED_RECORD.getMessage());
		} else {
			manualVerificationEntity = entities.get(0);
			manualVerificationEntity.setStatusCode(manualVerificationDTO.getStatusCode());
			manualVerificationEntity.setReasonCode(manualVerificationDTO.getReasonCode());
		}
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);
		messageDTO.setReg_type(RegistrationType.valueOf(registrationStatusDto.getRegistrationType()));
		try {
			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.MANUAL_VERIFICATION.toString());
			registrationStatusDto.setRegistrationStageName(stageName);

			if (manualVerificationDTO.getStatusCode().equalsIgnoreCase(ManualVerificationStatus.APPROVED.name())) {
				if (registrationStatusDto.getRegistrationType().equalsIgnoreCase(RegistrationType.LOST.toString()))
					packetInfoManager.saveRegLostUinDet(registrationId, manualVerificationDTO.getMatchedRefId());
				messageDTO.setIsValid(true);
				manualVerificationStage.sendMessage(messageDTO);
				registrationStatusDto.setStatusComment(StatusUtil.MANUAL_VERIFIER_APPROVED_PACKET.getMessage());
				registrationStatusDto.setSubStatusCode(StatusUtil.MANUAL_VERIFIER_APPROVED_PACKET.getCode());
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());

				isTransactionSuccessful = true;
				description = ManualVerificationConstants.VERIFICATION_APPROVED + registrationId;
			} else {
				registrationStatusDto.setStatusCode(RegistrationStatusCode.REJECTED.toString());
				registrationStatusDto.setStatusComment(StatusUtil.MANUAL_VERIFIER_REJECTED_PACKET.getMessage());
				registrationStatusDto.setSubStatusCode(StatusUtil.MANUAL_VERIFIER_REJECTED_PACKET.getCode());
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());

				description = ManualVerificationConstants.VERIFICATION_REJECTED + registrationId;
			}
			ManualVerificationEntity maVerificationEntity = basePacketRepository.update(manualVerificationEntity);
			manualVerificationDTO.setStatusCode(maVerificationEntity.getStatusCode());
			registrationStatusDto.setUpdatedBy(USER);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					manualVerificationDTO.getRegId(), description);

		} catch (TablenotAccessibleException e) {

			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.TABLE_NOT_ACCESSIBLE_EXCEPTION));
			registrationStatusDto.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.DB_NOT_ACCESSIBLE.getMessage() + e.getMessage()));
			registrationStatusDto.setSubStatusCode(StatusUtil.DB_NOT_ACCESSIBLE.getCode());
			description = ManualVerificationConstants.TABLE_NOT_ACCESSIBLE + registrationId + "::" + e.getMessage();

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					manualVerificationDTO.getRegId(), e.getMessage() + ExceptionUtils.getStackTrace(e));
		}

		finally {
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, registrationId,
					ApiName.AUDIT);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				manualVerificationDTO.getRegId(), "ManualVerificationServiceImpl::updatePacketStatus()::exit");
		return manualVerificationDTO;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.mosip.registration.processor.manual.verification.service.
	 * ManualVerificationService#getApplicantPacketInfo(java.lang.String)
	 */
	@Override
	public PacketMetaInfo getApplicantPacketInfo(String regId) throws PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException, IOException {
		PacketMetaInfo packetMetaInfo = new PacketMetaInfo();

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "ManualVerificationServiceImpl::getApplicantPacketInfo()::entry");
		if (regId == null || regId.isEmpty()) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "ManualVerificationServiceImpl::getApplicantPacketInfo()"
							+ PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getMessage());
			throw new InvalidFileNameException(PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getCode(),
					PlatformErrorMessages.RPR_MVS_REG_ID_SHOULD_NOT_EMPTY_OR_NULL.getMessage());
		}
		InputStream fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.PACKETMETAINFO);
		try {
			packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(fileInStream, PacketMetaInfo.class);
		} catch (UnsupportedEncodingException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, e.getMessage() + ExceptionUtils.getStackTrace(e));
		}
		if (packetMetaInfo != null) {
			packetMetaInfo.getIdentity().setMetaData(null);
			packetMetaInfo.getIdentity().setHashSequence(null);
			packetMetaInfo.getIdentity().setCheckSum(null);
			packetMetaInfo.getIdentity().setOsiData(null);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "ManualVerificationServiceImpl::getApplicantPacketInfo()::exit");
		return packetMetaInfo;
	}

	@SuppressWarnings("unchecked")
	private void checkUserIDExistsInMasterList(UserDto dto) {
		ResponseWrapper<UserResponseDTOWrapper> responseWrapper = new ResponseWrapper<>();
		UserResponseDTOWrapper userResponseDTOWrapper;
		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(ManualVerificationConstants.USERS);
		pathSegments.add(dto.getUserId());
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat(ManualVerificationConstants.TIME_FORMAT);
		String effectiveDate = dateFormat.format(date);
		// pathSegments.add("2019-05-16T06:12:52.994Z");
		pathSegments.add(effectiveDate);
		try {

			responseWrapper = (ResponseWrapper<UserResponseDTOWrapper>) restClientService.getApi(ApiName.MASTER,
					pathSegments, "", "", ResponseWrapper.class);

			if (responseWrapper.getResponse() != null) {
				userResponseDTOWrapper = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
						UserResponseDTOWrapper.class);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
						dto.getUserId(),
						"ManualVerificationServiceImpl::checkUserIDExistsInMasterList()::get MASTER USERS service call ended with response data : "
								+ JsonUtil.objectMapperObjectToJson(userResponseDTOWrapper));
				if (!userResponseDTOWrapper.getUserResponseDto().get(0).getStatusCode()
						.equals(ManualVerificationConstants.ACT)) {
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), null,
							PlatformErrorMessages.RPR_MVS_USER_STATUS_NOT_ACTIVE.getCode(),
							PlatformErrorMessages.RPR_MVS_USER_STATUS_NOT_ACTIVE.getMessage() + dto.getUserId());
					throw new UserIDNotPresentException(PlatformErrorMessages.RPR_MVS_USER_STATUS_NOT_ACTIVE.getCode(),
							PlatformErrorMessages.RPR_MVS_USER_STATUS_NOT_ACTIVE.getMessage());
				}
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), null,
						PlatformErrorMessages.RPR_MVS_NO_USER_ID_PRESENT.getCode(),
						PlatformErrorMessages.RPR_MVS_NO_USER_ID_PRESENT.getMessage());
				throw new UserIDNotPresentException(PlatformErrorMessages.RPR_MVS_NO_USER_ID_PRESENT.getCode(),
						PlatformErrorMessages.RPR_MVS_NO_USER_ID_PRESENT.getMessage());

			}
		} catch (ApisResourceAccessException | IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), null,
					PlatformErrorMessages.RPR_MVS_NO_USER_ID_PRESENT.getCode(),
					PlatformErrorMessages.RPR_MVS_NO_USER_ID_PRESENT.getMessage() + e);
			throw new UserIDNotPresentException(PlatformErrorMessages.RPR_MVS_NO_USER_ID_PRESENT.getCode(),
					PlatformErrorMessages.RPR_MVS_NO_USER_ID_PRESENT.getMessage());

		}
	}

}
