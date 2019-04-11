package io.mosip.registration.processor.packet.receiver.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
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

	@Value("${registration.processor.max.file.size}")
	private String fileSize;

	@Autowired
	private RegistrationStatusMapUtil registrationStatusMapUtil;

	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();
	
	SyncRegistrationEntity regEntity;
	@Autowired
	private VirusScanner<Boolean, InputStream> virusScannerService;

	Boolean storageFlag;
	private boolean isEncryptedFileCleaned;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.id.issuance.packet.handler.service.PacketUploadService#storePacket(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO validatePacket(File file, String stageName) {

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setInternalError(false);
		InternalRegistrationStatusDto dto;
		messageDTO.setIsValid(false);
		boolean storageFlag = false;
		if (file.getName() != null && file.exists()) {
			String fileOriginalName = file.getName();
			String registrationId = fileOriginalName.split("\\.")[0];
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "PacketReceiverServiceImpl::storePacket()::entry");
			messageDTO.setRid(registrationId);
			String description = "";
			boolean isTransactionSuccessful = false;
			 regEntity = syncRegistrationService.findByRegistrationId(registrationId);

			 if (regEntity == null) {
					description = "PacketNotSync exception in packet receiver for registartionId " + registrationId + "::"
							+ PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage();
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
					throw new PacketNotSyncException(PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getMessage());
				}
			 
			 
			try {
				dto = registrationStatusService.getRegistrationStatus(registrationId);
				InputStream encryptedInputStream = new FileInputStream(file.getAbsolutePath());
				validateHashCode(registrationId, encryptedInputStream);
				validatePacketFormat(fileOriginalName,registrationId);
				validatePacketSize(file.length(), registrationId);
				if (isDuplicatePacket(registrationId) && !isExternalStatusResend(registrationId)) {
					throw new DuplicateUploadRequestException(
							PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getMessage());
				}
				//scan encryptedInputStream 
				scanFile(encryptedInputStream);
				//decrypt file 
				//scan decrypted stream
				//call storePacket and set storage flag
				
			}  catch (IOException e) {

				description = " IOException in packet receiver for registrationId" + registrationId + "::"
						+ e.getMessage();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Error while updating status : " + e.getMessage());
			} catch (DataAccessException e) {

				description = "DataAccessException in packet receiver for registrationId" + registrationId + "::"
						+ e.getMessage();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Error while updating status : " + e.getMessage());
			}
			finally {


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

	private void validatePacketFormat(String fileOriginalName,String regId) {
		if (!(fileOriginalName.endsWith(getExtention()))) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), regId,
					PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
			throw new PacketNotValidException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getMessage());
		}
		
	}

	private void scanFile(InputStream encryptedInputStream) {
		//call kernel scan file and throw exception if it fails and handle exception
			isEncryptedFileCleaned=virusScannerService.scanFile(encryptedInputStream);
		if(!isEncryptedFileCleaned)
		{
			//throw new VirusScanFailedException("");
		}
	}

	/**
	 * check if file exists or not
	 *
	 * @param file
	 * @param fileOriginalName
	 * @return
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
	private void validateHashCode(String registrationId,InputStream inputStream) {
		
		
	}
	private void validatePacketSize(long length,String regid) {
		if (length > getMaxFileSize()) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), regid,
					PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
			throw new FileSizeExceedException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		}
		
	}
	

}