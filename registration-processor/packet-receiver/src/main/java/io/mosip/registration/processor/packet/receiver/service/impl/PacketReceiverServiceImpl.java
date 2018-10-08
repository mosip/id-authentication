package io.mosip.registration.processor.packet.receiver.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
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
	private SyncRegistrationService syncRegistrationService;

	@Autowired
	private RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

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
		if(!syncRegistrationService.isPresent(registrationId)) {
			
			logger.info("Registration Packet is Not yet sync in Sync table");
			throw new PacketNotSyncException(RegistrationStatusCode.PACKET_NOT_YET_SYNC.name());
		}
		boolean storageFlag = false;
		if (file.getSize() > getMaxFileSize()) {
			throw new FileSizeExceedException(RegistrationStatusCode.PACKET_SIZE_GREATER_THAN_LIMIT.name());
		}
		if (!(file.getOriginalFilename().endsWith(getFileExtension()))) {
			throw new PacketNotValidException(RegistrationStatusCode.INVALID_PACKET_FORMAT.toString());
		} else if (!(isDuplicatePacket(registrationId))) {
				try {
					fileManager.put(file.getOriginalFilename(), file.getInputStream(), DirectoryPathDto.LANDING_ZONE);
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
				} catch (IOException e) {
					logger.error(e.getMessage());
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
		return (this.maxFileSize * 1024 * 1024);
	}

	/**
	 * Checks if registration id is already present in registration status table.
	 * 
	 * @param enrolmentId
	 * @return
	 */
	private Boolean isDuplicatePacket(String enrolmentId) {
		return (registrationStatusService.getRegistrationStatus(enrolmentId) != null);
	}

}
