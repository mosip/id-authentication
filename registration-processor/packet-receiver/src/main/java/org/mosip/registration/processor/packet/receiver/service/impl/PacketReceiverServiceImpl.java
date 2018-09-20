package org.mosip.registration.processor.packet.receiver.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.manager.service.FileManager;
import org.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import org.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import org.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import org.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import org.mosip.registration.processor.status.code.RegistrationStatusCode;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PacketReceiverServiceImpl implements PacketReceiverService<MultipartFile, Boolean> {

	private final Logger logger = LoggerFactory.getLogger(PacketReceiverServiceImpl.class);

	@Value("${file.extension}")
	private String fileExtension;

	@Value("${max.file.size}")
	private int maxFileSize;

	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Autowired
	private RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.id.issuance.packet.handler.service.PacketUploadService#storePacket(
	 * java.lang.Object)
	 */
	@Override
	public Boolean storePacket(MultipartFile file) {
		boolean storageFlag = false;
		if (file.getSize() > getMaxFileSize()) {
			throw new FileSizeExceedException(RegistrationStatusCode.PACKET_SIZE_GREATER_THAN_LIMIT.name());
		}
		if (!(file.getOriginalFilename().endsWith(getFileExtension()))) {
			throw new PacketNotValidException(RegistrationStatusCode.INVALID_PACKET_FORMAT.toString());
		} else {
			String enrolmentId = file.getOriginalFilename().split("\\.")[0];
			if (!(isDuplicatePacket(enrolmentId))) {
				try {
					fileManager.put(file.getOriginalFilename(), file.getInputStream(), DirectoryPathDto.LANDING_ZONE);
					RegistrationStatusDto dto = new RegistrationStatusDto();
					dto.setEnrolmentId(enrolmentId);
					dto.setStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString());
					registrationStatusService.addRegistrationStatus(dto);
					storageFlag = true;
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw new DuplicateUploadRequestException(RegistrationStatusCode.DUPLICATE_PACKET_RECIEVED.toString());
			}
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
