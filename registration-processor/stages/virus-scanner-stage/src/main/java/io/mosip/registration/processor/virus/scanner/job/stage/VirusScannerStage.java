package io.mosip.registration.processor.virus.scanner.job.stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.virus.scanner.job.decrypter.Decryptor;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.DFSNotAccessibleException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.VirusScanFailedException;

@Service
public class VirusScannerStage extends MosipVerticleManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(VirusScannerStage.class);

	private static final String USER = "MOSIP_SYSTEM";

	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	private Environment env;

	@Value("${registration.processor.packet.ext}")
	private String extention;

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	VirusScanner<Boolean, String> virusScannerService;

	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Autowired
	Decryptor decryptor;

	private static final String RETRY_FOLDER_NOT_ACCESSIBLE = "The Retry Folder set by the System"
			+ " is not accessible";
	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";
	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Enrolment Status"
			+ " table is not accessible";
	private static final String VIRUS_SCAN_FAILED = "The Virus Scan for the Packet Failed";
	private static final String UNABLE_TO_DELETE = "unable to delete after sending to DFS.";

	String description = "";
	boolean isTransactionSuccessful = false;
	String registrationId = "";

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consume(mosipEventBus, MessageBusAddress.PACKET_RECEIVER_OUT);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		List<InternalRegistrationStatusDto> registrationStatusDtoList = null;
		try {
			registrationStatusDtoList = registrationStatusService
					.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
		} catch (TablenotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
			return object;
		}
		for (InternalRegistrationStatusDto entry : registrationStatusDtoList) {

			String encryptedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString()) + File.separator + getFileName(entry.getRegistrationId());
			File encryptedFile = new File(encryptedPacketPath);
			boolean isClean1;
			boolean isClean2;
			InputStream encryptedPacket = null;
			InputStream decryptedData = null;

			try {
				encryptedPacket = new FileInputStream(encryptedFile);
			} catch (FileNotFoundException e1) {
			
			}
			try {
				isClean1 = virusScannerService.scanFile(encryptedPacketPath);
				if (isClean1) {
					decryptedData = decryptor.decrypt(encryptedPacket, entry.getRegistrationId());

					try {
 
						fileManager.put(entry.getRegistrationId(), decryptedData, DirectoryPathDto.VIRUS_SCAN_DEC);
						String decryptedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString()) + File.separator + getFileName(entry.getRegistrationId());
						isClean2=virusScannerService.scanFile(decryptedPacketPath);
						
						if (isClean2) {
							sendToPacketUploaderStage(entry);
						}
						
						
						
						
					} catch (IOException e) {
					
					
					}
				
					// sendToDFS(file, entry);

				} else {
					// fileManager.cleanUpFile(srcFolderLoc, destFolderLoc, fileName);
				}
			} catch (VirusScanFailedException e) {
				LOGGER.error(LOGDISPLAY, VIRUS_SCAN_FAILED, e);
			} catch (PacketDecryptionFailureException e) {
				LOGGER.error(LOGDISPLAY, VIRUS_SCAN_FAILED, e);

			}

		}
		return object;
	}

	/**
	 * Send to virus scan retry.
	 *
	 * @param entry
	 *            the entry
	 *//*
	private void sendToRetry(InternalRegistrationStatusDto entry) {
		registrationId = entry.getRegistrationId();
		try {
			if (entry.getRetryCount() == null)
				entry.setRetryCount(0);
			fileManager.copy(entry.getRegistrationId(), DirectoryPathDto.VIRUS_SCAN, DirectoryPathDto.VIRUS_SCAN_RETRY);
			entry.setRetryCount(entry.getRetryCount() + 1);
			entry.setStatusCode(RegistrationStatusCode.VIRUS_SCAN_FAILED.toString());
			entry.setStatusComment("packet is in status PACKET_FOR_VIRUS_SCAN_RETRY");
			entry.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(entry);
			fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN, DirectoryPathDto.VIRUS_SCAN_RETRY,entry.getRegistrationId());
			description = registrationId + " packet is infected. It has been sent" + " to RETRY_FOLDER.";
			LOGGER.info(LOGDISPLAY, entry.getRegistrationId(),
					"File is infected. It has been sent" + " to RETRY_FOLDER.");
		} catch (IOException | FilePathNotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, RETRY_FOLDER_NOT_ACCESSIBLE, e);
			description = "RETRY_FOLDER is not accessible for packet  " + registrationId;
		} catch (TablenotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
			description = "The Registration Status table is not accessible for packet " + registrationId;
		} finally {

			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}

	}
*/
	
	
	
	private void sendToPacketUploaderStage(InternalRegistrationStatusDto entry) {
		registrationId = entry.getRegistrationId();
		try {
			entry.setStatusCode(RegistrationStatusCode.VIRUS_SCAN_SUCCESSFUL.toString());
			registrationStatusService.updateRegistrationStatus(entry);
			isTransactionSuccessful = true;

			description = registrationId + " packet successfully  scanned for virus. I thas been send to DFS";
		} catch (TablenotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
			description = "The Registration Status table is not accessible for packet " + registrationId;
		} finally {

			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}

	}

	private String getFileName(String fileName) {
		return fileName + extention;
	}

}
