package io.mosip.registration.processor.packet.receiver.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.receiver.stage.PacketReceiverStage;
import io.mosip.registration.processor.packet.receiver.util.StatusMessage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
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
public class PacketReceiverServiceImpl implements PacketReceiverService<MultipartFile, Boolean> {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketReceiverServiceImpl.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOG_FORMATTER. */
	public static final String LOG_FORMATTER = "{} - {}";

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
	@Autowired
	private PacketReceiverStage packetReceiverStage;

	@Autowired
	private RegistrationStatusMapUtil registrationStatusMapUtil;

	/** The env. */
	@Autowired
	private Environment env;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.id.issuance.packet.handler.service.PacketUploadService#storePacket(
	 * java.lang.Object)
	 */
	@Override
	public Boolean storePacket(MultipartFile file) {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setInternalError(false);

		messageDTO.setIsValid(false);
		boolean storageFlag = false;

		if (file.getOriginalFilename() != null && !file.isEmpty()) {
			String fileOriginalName = file.getOriginalFilename();
			String registrationId = fileOriginalName.split("\\.")[0];
			messageDTO.setRid(registrationId);
			boolean isTransactionSuccessful = false;
			SyncRegistrationEntity regEntity = syncRegistrationService.findByRegistrationId(registrationId);
			if (regEntity == null) {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Registration Packet is Not yet sync in Sync table.");
				throw new PacketNotSyncException(PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
			}

			if (file.getSize() > getMaxFileSize()) {
				throw new FileSizeExceedException(
						PlatformErrorMessages.RPR_PKR_PACKET_SIZE_GREATER_THAN_LIMIT.getMessage());
			}
			if (!(fileOriginalName.endsWith(getFileExtension()))) {
				throw new PacketNotValidException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
			} else if (isNotDuplicatePacket(registrationId)) {
			if (!(fileOriginalName.endsWith(getFileExtension()))) {
				throw new PacketNotValidException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
			} else if (file.getSize() > getMaxFileSize()) {
				throw new FileSizeExceedException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
			} else if (isNotDuplicatePacket(registrationId)) {
				throw new DuplicateUploadRequestException(
						PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getMessage());
			} else {
				messageDTO.setRid(registrationId);
				boolean isTransactionSuccessful = false;
				SyncRegistrationEntity regEntity = syncRegistrationService.findByRegistrationId(registrationId);
				if (regEntity == null) {
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							"Registration Packet is Not yet sync in Sync table.");
					throw new PacketNotSyncException(PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
				}
				try {
					fileManager.put(registrationId, file.getInputStream(), DirectoryPathDto.VIRUS_SCAN_ENC);
					InternalRegistrationStatusDto dto = new InternalRegistrationStatusDto();
					dto.setRegistrationId(registrationId);
					dto.setRegistrationType(regEntity.getRegistrationType());
					dto.setReferenceRegistrationId(null);
					dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
					dto.setLangCode("eng");
					dto.setStatusComment(StatusMessage.PACKET_UPLOADED_VIRUS_SCAN);
					dto.setIsActive(true);
					dto.setCreatedBy(USER);
					dto.setIsDeleted(false);
					registrationStatusService.addRegistrationStatus(dto);
					storageFlag = true;
					isTransactionSuccessful = true;
				} catch (DataAccessException | IOException e) {
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							"Error while updating status : " + e.getMessage());
				} finally {
					logError(isTransactionSuccessful, registrationId);
				}
			}
		} else {
			throw new PacketNotValidException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		}
		if (storageFlag) {

			messageDTO.setIsValid(true);
			packetReceiverStage.sendMessage(messageDTO);

		}
		return storageFlag;
	}

	private void logError(boolean isTransactionSuccessful, String registrationId) {
		String eventId = "";
		String eventName = "";
		String eventType = "";
		eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
		eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
				: EventName.EXCEPTION.toString();
		eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
				: EventType.SYSTEM.toString();
		String description = isTransactionSuccessful ? "Packet registration status updated successfully"
				: "Packet registration status updation unsuccessful";

		auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, registrationId);
	}

	/**
	 * Gets the file extension.
	 *
	 * @return the file extension
	 */
	public String getFileExtension() {
		return env.getProperty("registration.processor.packet.ext");

	}

	/**
	 * Gets the max file size.
	 *
	 * @return the max file size
	 */
	public long getMaxFileSize() {
		int maxFileSize = Integer.parseInt(env.getProperty("registration.processor.max.file.size"));
		return maxFileSize * 1024L * 1024;
	}

	/**
	 * Checks if registration id is already present in registration status table.
	 *
	 * @param enrolmentId
	 *            the enrolment id
	 * @return the boolean
	 */
	public Boolean isNotDuplicatePacket(String enrolmentId) {
		List<RegistrationStatusDto> registrations = registrationStatusService.getByIds(enrolmentId);

		 RegistrationExternalStatusCode mappedValue = registrationStatusMapUtil.getExternalStatus(registrations.get(0).getStatusCode(),registrations.get(0).getRetryCount());
		return (registrationStatusService.getRegistrationStatus(enrolmentId) == null) && (mappedValue.toString().equals(RESEND)) ;
	}

}