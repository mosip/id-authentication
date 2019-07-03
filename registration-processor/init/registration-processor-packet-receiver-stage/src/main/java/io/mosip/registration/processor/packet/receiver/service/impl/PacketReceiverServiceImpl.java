package io.mosip.registration.processor.packet.receiver.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.h2.store.fs.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.RegistrationType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.manager.decryptor.Decryptor;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.constants.PacketReceiverConstant;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.exception.PacketReceiverAppException;
import io.mosip.registration.processor.packet.receiver.exception.PacketSizeNotInSyncException;
import io.mosip.registration.processor.packet.receiver.exception.UnequalHashSequenceException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.receiver.util.StatusMessage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusSubRequestDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

/**
 * The Class PacketReceiverServiceImpl.
 *
 */
@RefreshScope
@Component
public class PacketReceiverServiceImpl implements PacketReceiverService<File, MessageDTO> {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketReceiverServiceImpl.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOG_FORMATTER. */
	public static final String LOG_FORMATTER = "{} - {}";

	/** The Constant RESEND. */
	private static final String RESEND = "RESEND";

	/** The file manager. */
	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The sync registration service. */
	@Autowired
	private SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The packet receiver stage. */

	@Value("${registration.processor.packet.ext}")
	private String extention;

	/** The file size. */
	@Value("${registration.processor.max.file.size}")
	private String fileSize;

	/** The virus scanner service. */
	@Autowired
	private VirusScanner<Boolean, InputStream> virusScannerService;

	/** The decryptor. */
	@Autowired
	private Decryptor packetReceiverDecryptor;

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.id.issuance.packet.handler.service.PacketUploadService#
	 * validatePacket( java.lang.Object)
	 */
	@Override
	public MessageDTO validatePacket(File file, String stageName) {

		LogDescription description = new LogDescription();
		InternalRegistrationStatusDto dto = new InternalRegistrationStatusDto();
		MessageDTO messageDTO = new MessageDTO();
		Boolean storageFlag = false;
		messageDTO.setInternalError(false);
		messageDTO.setIsValid(false);
		SyncRegistrationEntity regEntity;
		String registrationId = "";
		if (file.getName() != null && file.exists()) {
			String fileOriginalName = file.getName();
			registrationId = fileOriginalName.split("\\.")[0];
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "PacketReceiverServiceImpl::validatePacket()::entry");
			messageDTO.setRid(registrationId);
			regEntity = syncRegistrationService.findByRegistrationId(registrationId);
			try (InputStream encryptedInputStream = FileUtils.newInputStream(file.getAbsolutePath())) {
				byte[] encryptedByteArray = IOUtils.toByteArray(encryptedInputStream);
				validatePacketWithSync(regEntity, registrationId, description);
				messageDTO.setReg_type(RegistrationType.valueOf(regEntity.getRegistrationType()));
				validateHashCode(new ByteArrayInputStream(encryptedByteArray), regEntity, registrationId, description);
				validatePacketFormat(fileOriginalName, registrationId, description);
				validatePacketSize(file.length(), regEntity, registrationId);
				if (isDuplicatePacket(registrationId) && !isExternalStatusResend(registrationId)) {
					throw new DuplicateUploadRequestException(
							PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getMessage());
				}
				description.setMessage(PacketReceiverConstant.PACKET_SUCCESS_UPLOADED_IN_PACKET_RECIVER + dto.getRegistrationId());
				
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), description.getCode() + " -- " + registrationId, PacketReceiverConstant.PACKET_RECEIVER_VALIDATION_SUCCESS);
				storageFlag = storePacket(stageName, regEntity, dto, description);
			

			} catch (IOException e) {

				description.setMessage(PacketReceiverConstant.IOEXCEPTION_IN_PACKET_RECIVER + registrationId + "::"
						+ e.getMessage());
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
				throw new PacketReceiverAppException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
						PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			} catch (DataAccessException e) {

				description.setMessage(PacketReceiverConstant.DATA_ACCESS_EXCEPTION_IN_PACKET_RECIVER + registrationId + "::"
						+ e.getMessage());
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, PacketReceiverConstant.ERROR_IN_PACKET_RECIVER
								+ PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getMessage());
				throw new PacketReceiverAppException(PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getCode(),
						PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getMessage());

			} finally {

				String eventId = "";
				String eventName = "";
				String eventType = "";
				eventId = storageFlag ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
				eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
						: EventName.EXCEPTION.toString();
				eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
						: EventType.SYSTEM.toString();

				auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
						registrationId, ApiName.AUDIT);
			}

			if (storageFlag) {
				messageDTO.setIsValid(true);
			}

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
				LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
				"PacketReceiverServiceImpl::validatePacket()::exit");
		return messageDTO;
	}

	/**
	 * validate packet with reg entity.
	 * @param description 
	 */
	private void validatePacketWithSync(SyncRegistrationEntity regEntity, String registrationId, LogDescription description) {

		if (regEntity == null) {
			description.setMessage(PacketReceiverConstant.PACKETNOTSYNC_IN_PACKET_RECIVER + registrationId + "::"
					+ PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
			throw new PacketNotSyncException(PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
		}
	}

	/**
	 * Store packet.
	 *
	 * @param encryptedInputStream
	 *            the encrypted input stream
	 * @param registrationId
	 *            the registration id
	 * @param stageName
	 *            the stage name
	 * @param description 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean storePacket(String stageName, SyncRegistrationEntity regEntity, InternalRegistrationStatusDto dto, LogDescription description)
			throws IOException {
		Boolean storageFlag = false;
		dto = registrationStatusService.getRegistrationStatus(dto.getRegistrationId());
		if (dto == null) {
			dto = new InternalRegistrationStatusDto();
			dto.setRetryCount(0);
		} else {
			int retryCount = dto.getRetryCount() != null ? dto.getRetryCount() + 1 : 1;
			dto.setRetryCount(retryCount);

		}
		dto.setRegistrationId(regEntity.getRegistrationId());
		dto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.PACKET_RECEIVER.toString());
		dto.setRegistrationStageName(stageName);
		dto.setRegistrationType(regEntity.getRegistrationType());
		dto.setReferenceRegistrationId(null);
		dto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
		dto.setLangCode("eng");
		dto.setStatusComment(StatusMessage.PACKET_RECEIVED);
		dto.setReProcessRetryCount(0);
		dto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
		dto.setIsActive(true);
		dto.setCreatedBy(USER);
		dto.setIsDeleted(false);
		registrationStatusService.addRegistrationStatus(dto);
		storageFlag = true;
		description.setMessage(PacketReceiverConstant.PACKET_SUCCESS_UPLOADED_IN_PACKET_RECIVER + dto.getRegistrationId());
		return storageFlag;
	}

	/**
	 * Validate packet format.
	 *
	 * @param fileOriginalName
	 *            the file original name
	 * @param description 
	 * @param regId
	 *            the reg id
	 */
	private void validatePacketFormat(String fileOriginalName, String registrationId, LogDescription description) {
		if (!(fileOriginalName.endsWith(getExtention()))) {
			description.setMessage(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage() + registrationId);

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
			throw new PacketNotValidException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
		}

	}

	/**
	 * Scan file.
	 *
	 * @param inputStream
	 *            the input stream
	 * @param description 
	 */
	private boolean scanFile(InputStream inputStream, RegistrationExceptionMapperUtil registrationExceptionMapperUtil,
			String registrationId, InternalRegistrationStatusDto dto, LogDescription description) {
		try {
			boolean isInputFileClean = virusScannerService.scanFile(inputStream);
			if (!isInputFileClean) {
				description.setMessage(PacketReceiverConstant.PACKET_VIRUS_SCAN_FAILED_PR + registrationId
						+ PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCAN_FAILED.getMessage());
				dto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				dto.setStatusComment(StatusMessage.VIRUS_SCAN_FAILED);
				dto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.VIRUS_SCAN_FAILED_EXCEPTION));
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCAN_FAILED.getMessage());
			}
			return isInputFileClean;
		} catch (VirusScannerException e) {
			description.setMessage(StatusMessage.VIRUS_SCANNER_SERVICE_FAILED + registrationId);
			dto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			dto.setStatusComment(StatusMessage.VIRUS_SCANNER_SERVICE_FAILED);
			dto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.VIRUS_SCANNER_SERVICE_FAILED));

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCANNER_SERVICE_FAILED.getMessage());
			return false;
		}

	}

	/**
	 * Gets the max file size.
	 *
	 * @return the max file size
	 */
	public long getMaxFileSize() {
		int maxFileSize = Integer.parseInt(fileSize);
		return maxFileSize * 1024L * 1024;
	}

	/**
	 * Gets the extention.
	 *
	 * @return the extention
	 */
	public String getExtention() {
		return extention;
	}

	/**
	 * Checks if registration id is already present in registration status table.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the boolean
	 */
	private Boolean isDuplicatePacket(String registrationId) {
		return registrationStatusService.getRegistrationStatus(registrationId) != null;
	}

	/**
	 * Checks if is external status resend.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the boolean
	 */
	public Boolean isExternalStatusResend(String registrationId) {
		List<RegistrationStatusSubRequestDto> regIds = new ArrayList<>();
		RegistrationStatusSubRequestDto registrationStatusSubRequestDto = new RegistrationStatusSubRequestDto();
		registrationStatusSubRequestDto.setRegistrationId(registrationId);
		regIds.add(registrationStatusSubRequestDto);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "PacketReceiverServiceImpl::isExternalStatusResend()::entry");

		List<RegistrationStatusDto> registrationExternalStatusCode = registrationStatusService.getByIds(regIds);

		String mappedValue = registrationExternalStatusCode.get(0).getStatusCode();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "PacketReceiverServiceImpl::isExternalStatusResend()::exit");
		return (mappedValue.equals(RESEND));
	}

	/**
	 * Validate hash code.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param inputStream
	 *            the input stream
	 * @param description 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void validateHashCode(InputStream inputStream, SyncRegistrationEntity regEntity, String registrationId, LogDescription description)
			throws IOException {
		// TO-DO testing
		byte[] isbytearray = IOUtils.toByteArray(inputStream);
		HMACUtils.update(isbytearray);
		String hashSequence = HMACUtils.digestAsPlainText(HMACUtils.updatedHash());
		String packetHashSequence = regEntity.getPacketHashValue();
		if (!(MessageDigest.isEqual(packetHashSequence.getBytes(),hashSequence.getBytes()))) {
			description.setMessage(PacketReceiverConstant.UNEQUAL_PACKET_HASH_PR
					+ registrationId);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PKR_PACKET_HASH_NOT_EQUALS_SYNCED_HASH.getMessage());
			throw new UnequalHashSequenceException(
					PlatformErrorMessages.RPR_PKR_PACKET_HASH_NOT_EQUALS_SYNCED_HASH.getMessage());
		}
	}

	/**
	 * Validate packet size.
	 *
	 * @param length
	 *            the length
	 * @param regid
	 *            the regid
	 */
	private void validatePacketSize(long length, SyncRegistrationEntity regEntity, String registrationId) {

		long packetSize = regEntity.getPacketSize().longValue();
		if (length != packetSize) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE_SYNCED.getMessage());
			throw new PacketSizeNotInSyncException(
					PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE_SYNCED.getMessage());
		}

		if (length > getMaxFileSize()) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
			throw new FileSizeExceedException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		}

	}

	@Override
	public MessageDTO processPacket(File file) {
		LogDescription description = new LogDescription();
		MessageDTO messageDTO = new MessageDTO();
		RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();
		messageDTO.setInternalError(false);
		messageDTO.setIsValid(false);
		boolean scanningFlag;
		boolean isTransactionSuccessful=false;
		String registrationId = "";
		SyncRegistrationEntity regEntity;
		String fileOriginalName = file.getName();
		registrationId = fileOriginalName.split("\\.")[0];
		InternalRegistrationStatusDto dto = registrationStatusService.getRegistrationStatus(registrationId);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "PacketReceiverServiceImpl::processPacket()::entry");
		messageDTO.setRid(registrationId);
		regEntity = syncRegistrationService.findByRegistrationId(registrationId);
		messageDTO.setReg_type(RegistrationType.valueOf(regEntity.getRegistrationType()));
		try (InputStream encryptedInputStream = FileUtils.newInputStream(file.getAbsolutePath())) {
			byte[] encryptedByteArray = IOUtils.toByteArray(encryptedInputStream);
			scanningFlag = scanFile(new ByteArrayInputStream(encryptedByteArray), registrationExceptionMapperUtil,
					registrationId, dto, description);
			if (scanningFlag) {
				InputStream decryptedData = packetReceiverDecryptor
						.decrypt(new ByteArrayInputStream(encryptedByteArray), registrationId);
				scanningFlag = scanFile(decryptedData, registrationExceptionMapperUtil, registrationId, dto, description);
			}
			if (scanningFlag) {
				fileManager.put(registrationId, new ByteArrayInputStream(encryptedByteArray),
						DirectoryPathDto.LANDING_ZONE);
				dto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				dto.setStatusComment(StatusMessage.PACKET_UPLOADED_TO_LANDING_ZONE);
				dto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
				messageDTO.setIsValid(Boolean.TRUE);
				isTransactionSuccessful=true;
				dto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.PACKET_RECEIVER.toString());
				description.setMessage(PlatformSuccessMessages.RPR_PKR_PACKET_RECEIVER.getMessage() + "-------" + registrationId);
				
			}

		} catch (IOException e) {
			messageDTO.setInternalError(Boolean.TRUE);
			description.setMessage(PacketReceiverConstant.IOEXCEPTION_IN_PACKET_RECIVER + registrationId + "::" + e.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
		} catch (DataAccessException e) {
			messageDTO.setInternalError(Boolean.TRUE);
			description.setMessage(PacketReceiverConstant.DATA_ACCESS_EXCEPTION_IN_PACKET_RECIVER + registrationId + "::"
					+ e.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PacketReceiverConstant.ERROR_IN_PACKET_RECIVER + PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		} catch (ApisResourceAccessException e) {
			messageDTO.setInternalError(Boolean.TRUE);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,PacketReceiverConstant.API_RESOURCE_UNAVAILABLE
							+ PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description.setMessage(PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage());

		} catch (io.mosip.registration.processor.core.exception.PacketDecryptionFailureException e) {
			messageDTO.setInternalError(Boolean.TRUE);
			dto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			dto.setStatusComment(StatusMessage.PACKET_DECRYPTION_FAILED);
			dto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PACKET_DECRYPTION_FAILURE_EXCEPTION));
			description.setMessage(PacketReceiverConstant.PACKET_DECRYPTION_FAILED+ registrationId + "::" + e.getErrorCode()
					+ e.getErrorText());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
		
		} finally {

			registrationStatusService.updateRegistrationStatus(dto);
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType, registrationId,
					ApiName.AUDIT);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
				LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
				"PacketReceiverServiceImpl::processPacket()::exit");
		return messageDTO;
	}

}