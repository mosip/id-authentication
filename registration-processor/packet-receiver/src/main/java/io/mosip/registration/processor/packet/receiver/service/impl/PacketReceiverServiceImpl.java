package io.mosip.registration.processor.packet.receiver.service.impl;

import java.io.IOException;
import java.io.InputStream;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

/**
 * The Class PacketReceiverServiceImpl.
 *
 */
@RefreshScope
@Component
public class PacketReceiverServiceImpl implements PacketReceiverService<MultipartFile, Boolean> {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(PacketReceiverServiceImpl.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	public static final String LOG_FORMATTER = "{} - {}";
	
	/** The file extension. */
	@Value("${registration.processor.file.extension}")
	private String fileExtension;

	/** The max file size. */
	@Value("${registration.processor.max.file.size}")
	private int maxFileSize;

	/** The file manager. */
	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The sync registration service. */
	@Autowired
	private SyncRegistrationService<SyncRegistrationDto> syncRegistrationService;

	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.id.issuance.packet.handler.service.PacketUploadService#storePacket(
	 * java.lang.Object)
	 */
	@Override
	public Boolean storePacket(MultipartFile file) {

		String registrationId = file.getOriginalFilename().split("\\.")[0];
		boolean storageFlag = false;
		boolean isTransactionSuccessful = false;
		SyncRegistrationEntity regEntity = syncRegistrationService.findByRegistrationId(registrationId);
		if (regEntity==null) {
			logger.info("Registration Packet is Not yet sync in Sync table");
			throw new PacketNotSyncException(PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
		}

		if (file.getSize() > getMaxFileSize()) {
			throw new FileSizeExceedException(PlatformErrorMessages.RPR_PKR_PACKET_SIZE_GREATER_THAN_LIMIT.getMessage());
		}
		if (!(file.getOriginalFilename().endsWith(getFileExtension()))) {
			throw new PacketNotValidException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
		} else if (!(isDuplicatePacket(registrationId))) {
			try {
				fileManager.put(registrationId, file.getInputStream(), DirectoryPathDto.LANDING_ZONE);

				InternalRegistrationStatusDto dto = new InternalRegistrationStatusDto();
				dto.setRegistrationId(registrationId);
				dto.setRegistrationType(regEntity.getRegistrationType());
				dto.setReferenceRegistrationId(null);
				dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString());
				dto.setLangCode("eng");
				dto.setStatusComment("Packet is in PACKET_UPLOADED_TO_LANDING_ZONE status");
				dto.setIsActive(true);
				dto.setCreatedBy(USER);
				dto.setIsDeleted(false);
				dto.setApplicantType("NEW");
				registrationStatusService.addRegistrationStatus(dto);
				storageFlag = true;
				isTransactionSuccessful = true;
			} catch (DataAccessException | IOException e) {
				logger.info(LOG_FORMATTER, "Error while updating status", e.getMessage());
			} finally {
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

				auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
						registrationId);
			}
		} else {
			throw new DuplicateUploadRequestException(PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getMessage());
		}

		return storageFlag;
	}

	/**
	 * Gets the file extension.
	 *
	 * @return the file extension
	 */
	public String getFileExtension() {
		return this.fileExtension;
	}

	/**
	 * Gets the max file size.
	 *
	 * @return the max file size
	 */
	public long getMaxFileSize() {
		return this.maxFileSize * 1024L * 1024;
	}

	/**
	 * Checks if registration id is already present in registration status table.
	 *
	 * @param enrolmentId
	 *            the enrolment id
	 * @return the boolean
	 */
	private Boolean isDuplicatePacket(String enrolmentId) {
		return registrationStatusService.getRegistrationStatus(enrolmentId) != null;
	}

}