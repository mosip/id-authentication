package io.mosip.registration.processor.packet.receiver.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
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
import io.mosip.registration.processor.packet.receiver.exception.UnequalHashSequenceException;
import io.mosip.registration.processor.packet.receiver.exception.VirusScanFailedException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.receiver.util.StatusMessage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusSubRequestDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.utilities.RegistrationStatusMapUtil;

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

	/** The registration status map util. */
	@Autowired
	private RegistrationStatusMapUtil registrationStatusMapUtil;

	/** The registration exception mapper util. */
	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	/** The reg entity. */
	private SyncRegistrationEntity regEntity;

	/** The virus scanner service. */
	@Autowired
	private VirusScanner<Boolean, InputStream> virusScannerService;

	/** The decryptor. */
	@Autowired
	private Decryptor decryptor;

	/** The storage flag. */
	private Boolean storageFlag = false;

	/** The is encrypted file cleaned. */
	private boolean isEncryptedFileCleaned;

	/** The description. */
	private String description = "";

	/** The is transaction successful. */
	boolean isTransactionSuccessful = false;

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
			String registrationId = fileOriginalName.split("\\.")[0];
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "PacketReceiverServiceImpl::validatePacket()::entry");
			messageDTO.setRid(registrationId);

			regEntity = syncRegistrationService.findByRegistrationId(registrationId);
			try (InputStream encryptedInputStream = new FileInputStream(file.getAbsolutePath())) {
				validatePacketWithRegEntity();
				validateHashCode(registrationId, encryptedInputStream);
				validatePacketFormat(fileOriginalName, registrationId);
				validatePacketSize(file.length(), registrationId);
				if (isDuplicatePacket(registrationId) && !isExternalStatusResend(registrationId)) {
					throw new DuplicateUploadRequestException(
							PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getMessage());
				}

				scanFile(encryptedInputStream);

				InputStream decryptedData = decryptor.decrypt(encryptedInputStream, registrationId);

				scanFile(decryptedData);

				storePacket(encryptedInputStream, registrationId, stageName);

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

			} catch (PacketDecryptionFailureException e) {
				description = "Packet decryption failed for registrationId " + registrationId + "::" + e.getErrorCode()
						+ e.getErrorText();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, e.getMessage());
				throw new PacketReceiverAppException(e.getErrorCode(), e.getMessage());

			} catch (ApisResourceAccessException e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, "API resource not accessible : "
								+ PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage());
				description = PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage();
				throw new PacketReceiverAppException(PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getCode(),
						PlatformErrorMessages.RPR_PKR_API_RESOUCE_ACCESS_FAILED.getMessage());

			} catch (Exception e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, "Error while updating status : "
								+ PlatformErrorMessages.RPR_PKR_UNKNOWN_EXCEPTION.getMessage());
				description = PlatformErrorMessages.RPR_PKR_UNKNOWN_EXCEPTION.getMessage();
				throw new PacketReceiverAppException(PlatformErrorMessages.RPR_PKR_UNKNOWN_EXCEPTION.getCode(),
						PlatformErrorMessages.RPR_PKR_UNKNOWN_EXCEPTION.getMessage());

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
	private void validatePacketWithRegEntity() {

		if (regEntity == null) {
			description = "PacketNotSync exception in packet receiver for registartionId "
					+ regEntity.getRegistrationId() + "::"
					+ PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regEntity.getRegistrationId(), PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
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
	private void storePacket(InputStream encryptedInputStream, String registrationId, String stageName)
			throws IOException {
		InternalRegistrationStatusDto dto;

		dto = registrationStatusService.getRegistrationStatus(registrationId);
		if (dto == null)
			dto = new InternalRegistrationStatusDto();
		else {
			int retryCount = dto.getRetryCount() != null ? dto.getRetryCount() + 1 : 1;
			dto.setRetryCount(retryCount);

		}
		fileManager.put(registrationId, encryptedInputStream, DirectoryPathDto.LANDING_ZONE);

		dto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.PACKET_RECEIVER.toString());
		dto.setRegistrationStageName(stageName);

		dto.setRegistrationId(registrationId);
		dto.setRegistrationType(regEntity.getRegistrationType());
		dto.setReferenceRegistrationId(null);
		dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
		dto.setLangCode("eng");
		dto.setStatusComment(StatusMessage.PACKET_UPLOADED_VIRUS_SCAN);
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
	private void validatePacketFormat(String fileOriginalName, String regId) {
		if (!(fileOriginalName.endsWith(getExtention()))) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
			throw new PacketNotValidException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
		}

	}

	/**
	 * Scan file.
	 *
	 * @param inputStream
	 *            the input stream
	 */
	private void scanFile(InputStream inputStream) {
		// call kernel scan file and throw exception if it fails and handle exception
		boolean isInputFileClean = virusScannerService.scanFile(inputStream);
		if (!isInputFileClean) {
			description = "Packet virus scan failed exception in packet receiver  ::"
					+ PlatformErrorMessages.PRP_PKR_PACKET_VISRUS_SCAN_FAILED.getMessage();
			throw new VirusScanFailedException(PlatformErrorMessages.PRP_PKR_PACKET_VISRUS_SCAN_FAILED.getMessage());
		}
	}

	/**
	 * check if file exists or not.
	 *
	 * @param file
	 *            the file
	 * @param fileOriginalName
	 *            the file original name
	 * @return true, if successful
	 */
	boolean fileExists(MultipartFile file, String fileOriginalName) {
		return file.getOriginalFilename() != null && !file.isEmpty() && fileOriginalName != null;
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
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void validateHashCode(String registrationId, InputStream inputStream) throws IOException {
		byte[] isbytearray = IOUtils.toByteArray(inputStream);
		byte[] hasheSquence = HMACUtils.generateHash(isbytearray);
		byte[] packetHashSequenceFromEntity = isbytearray;// PacketHashSequesnce
		if (!(Arrays.equals(hasheSquence, packetHashSequenceFromEntity))) {
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
	private void validatePacketSize(long length, String regid) {
		if (length > getMaxFileSize()) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regid, PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
			throw new FileSizeExceedException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		}

	}

}