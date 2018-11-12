package io.mosip.registration.processor.packet.receiver.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.status.code.AuditLogTempConstant;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

@Component
public class PacketReceiverServiceImpl implements PacketReceiverService<MultipartFile, Boolean> {

	private final Logger logger = LoggerFactory.getLogger(PacketReceiverServiceImpl.class);
	private static final String USER = "MOSIP_SYSTEM";

	@Value("${file.extension}")
	private String fileExtension;

	@Value("${max.file.size}")
	private int maxFileSize;

	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;
	
	@Autowired
	private SyncRegistrationService<SyncRegistrationDto> syncRegistrationService;

	@Autowired
	private RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

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
		
		if(!syncRegistrationService.isPresent(registrationId)) {
			logger.info("Registration Packet is Not yet sync in Sync table");
			throw new PacketNotSyncException(RegistrationStatusCode.PACKET_NOT_YET_SYNC.name());
		}
		
		if (file.getSize() > getMaxFileSize()) {
			throw new FileSizeExceedException(RegistrationStatusCode.PACKET_SIZE_GREATER_THAN_LIMIT.name());
		}
		if (!(file.getOriginalFilename().endsWith(getFileExtension()))) {
			throw new PacketNotValidException(RegistrationStatusCode.INVALID_PACKET_FORMAT.toString());
		} else if (!(isDuplicatePacket(registrationId))) {
				try {
					fileManager.put(registrationId, file.getInputStream(), DirectoryPathDto.LANDING_ZONE);

					RegistrationStatusDto dto = new RegistrationStatusDto();
					dto.setRegistrationId(registrationId);
					dto.setRegistrationType(RegistrationType.NEW.toString());
					dto.setReferenceRegistrationId(null);
					dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString());
					dto.setLangCode("eng");
					dto.setStatusComment("Packet is in PACKET_UPLOADED_TO_LANDING_ZONE status");
					dto.setIsActive(true);
					dto.setCreatedBy(USER);
					dto.setIsDeleted(false);
					registrationStatusService.addRegistrationStatus(dto);
					storageFlag = true;
					isTransactionSuccessful = true;
				} catch (IOException e) {
					logger.error(e.getMessage());
				}finally {
					String description = "";
					if (isTransactionSuccessful) {
						description = "description--packet-receiver Success";
					} else {
						description = "description--packet-receiver Failure";
					}
					createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
							AuditLogTempConstant.APPLICATION_NAME.toString(), description,
							AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
							AuditLogTempConstant.EVENT_TYPE.toString());
				}
			} else {
				throw new DuplicateUploadRequestException(RegistrationStatusCode.DUPLICATE_PACKET_RECIEVED.toString());
			}
		return storageFlag;
	}

	public String getFileExtension() {
		return this.fileExtension;
	}

	public long getMaxFileSize() {
		return this.maxFileSize * 1024L * 1024;
	}

	/**
	 * Checks if registration id is already present in registration status table.
	 * 
	 * @param enrolmentId
	 * @return
	 */
	private Boolean isDuplicatePacket(String enrolmentId) {
		return registrationStatusService.getRegistrationStatus(enrolmentId) != null;
	}
	
	private void createAuditRequestBuilder(String applicationId, String applicationName, String description,
			String eventId, String eventName, String eventType) {
		auditRequestBuilder.setActionTimeStamp(LocalDateTime.now()).setApplicationId(applicationId)
				.setApplicationName(applicationName).setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
				.setDescription(description).setEventId(eventId).setEventName(eventName).setEventType(eventType)
				.setHostIp(AuditLogTempConstant.HOST_IP.toString())
				.setHostName(AuditLogTempConstant.HOST_NAME.toString()).setId(AuditLogTempConstant.ID.toString())
				.setIdType(AuditLogTempConstant.ID_TYPE.toString())
				.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
				.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
				.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
				.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());

		AuditRequestDto auditRequestDto = auditRequestBuilder.build();
		auditHandler.writeAudit(auditRequestDto);
	}

}
