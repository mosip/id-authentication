package io.mosip.registration.processor.packet.receiver.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

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
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.decrypter.Decryptor;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketDecryptionFailureException;
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

	/** The registration exception mapper util. */
	private RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	private RegistrationStatusEntity entity = new RegistrationStatusEntity();

	/** The reg entity. */
	private SyncRegistrationEntity regEntity;

	/** The virus scanner service. */
	@Autowired
	private VirusScanner<Boolean, InputStream> virusScannerService;

	/** The decryptor. */
	@Autowired
	private Decryptor packetReceiverDecryptor;

	/** The storage flag. */
	private Boolean storageFlag = false;

	/** The description. */
	private String description = "";

	/** The is transaction successful. */
	boolean isTransactionSuccessful = false;

	/** The registration id. */
	private String registrationId;

	InternalRegistrationStatusDto dto;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.id.issuance.packet.handler.service.PacketUploadService#
	 * validatePacket( java.lang.Object)
	 */
	@Override
	public MessageDTO validatePacket(File file, String stageName) {

		MessageDTO messageDTO = new MessageDTO();

		messageDTO.setInternalError(false);
		messageDTO.setIsValid(false);

		if (file.getName() != null && file.exists()) {
			String fileOriginalName = file.getName();
			registrationId = fileOriginalName.split("\\.")[0];
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "PacketReceiverServiceImpl::validatePacket()::entry");
			messageDTO.setRid(registrationId);

			regEntity = syncRegistrationService.findByRegistrationId(registrationId);
			try (InputStream encryptedInputStream = new FileInputStream(file.getAbsolutePath())) {
				byte[] encryptedByteArray = IOUtils.toByteArray(encryptedInputStream);
				validatePacketWithSync();
				validateHashCode(new ByteArrayInputStream(encryptedByteArray));
				validatePacketFormat(fileOriginalName);
				validatePacketSize(file.length());
				if (isDuplicatePacket() && !isExternalStatusResend()) {
					throw new DuplicateUploadRequestException(
							PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getMessage());
				}
				storePacket(stageName);
				messageDTO.setReg_type(RegistrationType.valueOf(regEntity.getRegistrationType()));
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"PacketReceiverServiceImpl::validatePacket()::exit");

			} catch (IOException e) {

				description = " IOException in packet receiver for registrationId" + registrationId + "::"
						+ e.getMessage();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
				throw new PacketReceiverAppException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
						PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			} catch (DataAccessException e) {

				description = "DataAccessException in packet receiver for registrationId" + registrationId + "::"
						+ e.getMessage();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, "Error while updating status : "
								+ PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getMessage());
				throw new PacketReceiverAppException(PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getCode(),
						PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getMessage());

			} finally {

				String eventId = "";
				String eventName = "";
				String eventType = "";
				eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
				eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
						: EventName.EXCEPTION.toString();
				eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
						: EventType.SYSTEM.toString();

				auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
						registrationId, ApiName.AUDIT);
			}

			if (storageFlag) {
				messageDTO.setIsValid(true);
			}

		}

		return messageDTO;
	}

	/**
	 * validate packet with reg entity.
	 */
	private void validatePacketWithSync() {

		if (regEntity == null) {
			description = "PacketNotSync exception in packet receiver for registartionId " + registrationId + "::"
					+ PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage();
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
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void storePacket(String stageName) throws IOException {

		dto = registrationStatusService.getRegistrationStatus(registrationId);
		if (dto == null) {
			dto = new InternalRegistrationStatusDto();
			dto.setRetryCount(0);
		} else {
			int retryCount = dto.getRetryCount() != null ? dto.getRetryCount() + 1 : 1;
			dto.setRetryCount(retryCount);

		}
		dto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.PACKET_RECEIVER.toString());
		dto.setRegistrationStageName(stageName);

		dto.setRegistrationId(registrationId);
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
		isTransactionSuccessful = true;
		description = "Packet sucessfully uploaded for registrationId " + registrationId;

	}

	/**
	 * Validate packet format.
	 *
	 * @param fileOriginalName
	 *            the file original name
	 * @param regId
	 *            the reg id
	 */
	private void validatePacketFormat(String fileOriginalName) {
		if (!(fileOriginalName.endsWith(getExtention()))) {
			description = " Invalid packet format" + registrationId;

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
	 */
	private boolean scanFile(InputStream inputStream) {
		try {
			boolean isInputFileClean = virusScannerService.scanFile(inputStream);
			if (!isInputFileClean) {
				description = "Packet virus scan failed  in packet receiver for registrationId ::" + registrationId
						+ PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCAN_FAILED.getMessage();
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
			description = "Virus scanner service failed ::" + registrationId;
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
	private Boolean isDuplicatePacket() {
		return registrationStatusService.getRegistrationStatus(registrationId) != null;
	}

	/**
	 * Checks if is external status resend.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the boolean
	 */
	public Boolean isExternalStatusResend() {
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
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void validateHashCode(InputStream inputStream) throws IOException {
		// TO-DO testing
		byte[] isbytearray = IOUtils.toByteArray(inputStream);
		HMACUtils.update(isbytearray);
		String hashSequence = HMACUtils.digestAsPlainText(HMACUtils.updatedHash());
		String packetHashSequence = regEntity.getPacketHashValue();
		if (!(packetHashSequence.equals(hashSequence))) {
			description = "The Registration Packet HashSequence is not equal as synced packet HashSequence"
					+ registrationId;
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
	private void validatePacketSize(long length) {

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
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setInternalError(false);
		messageDTO.setIsValid(false);
		boolean scanningFlag;
		String fileOriginalName = file.getName();
		registrationId = fileOriginalName.split("\\.")[0];
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "PacketReceiverServiceImpl::processPacket()::entry");
		messageDTO.setRid(registrationId);
		regEntity = syncRegistrationService.findByRegistrationId(registrationId);
		messageDTO.setReg_type(RegistrationType.valueOf(regEntity.getRegistrationType()));
		try (InputStream encryptedInputStream = new FileInputStream(file.getAbsolutePath())) {
			byte[] encryptedByteArray = IOUtils.toByteArray(encryptedInputStream);
			scanningFlag = scanFile(new ByteArrayInputStream(encryptedByteArray));
			if (scanningFlag) {
				InputStream decryptedData = packetReceiverDecryptor.decrypt(new ByteArrayInputStream(encryptedByteArray),
						registrationId);
				scanningFlag = scanFile(decryptedData);
			}
			if (scanningFlag) {
				fileManager.put(registrationId, new ByteArrayInputStream(encryptedByteArray), DirectoryPathDto.LANDING_ZONE);
				dto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				dto.setStatusComment(StatusMessage.PACKET_UPLOADED_TO_LANDING_ZONE);
				dto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
				messageDTO.setIsValid(Boolean.TRUE);
				description = PlatformSuccessMessages.RPR_PKR_PACKET_RECEIVER.getMessage() + "-------" + registrationId;
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"PacketReceiverServiceImpl::processPacket()::exit");
			}

		} catch (IOException e) {

			description = " IOException in packet receiver for registrationId" + registrationId + "::" + e.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
		} catch (DataAccessException e) {

			description = "DataAccessException in packet receiver for registrationId" + registrationId + "::"
					+ e.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Error while updating status : "
							+ PlatformErrorMessages.RPR_PKR_DATA_ACCESS_EXCEPTION.getMessage());
		} catch (PacketDecryptionFailureException e) {
			dto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			dto.setStatusComment(StatusMessage.PACKET_DECRYPTION_FAILED);
			dto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PACKET_DECRYPTION_FAILURE_EXCEPTION));
			description = "Packet decryption failed for registrationId " + registrationId + "::" + e.getErrorCode()
					+ e.getErrorText();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, e.getMessage());
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "API resource not accessible : "
							+ PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage());
			description = PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage();

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

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, registrationId,
					ApiName.AUDIT);
		}

		return messageDTO;
	}

}