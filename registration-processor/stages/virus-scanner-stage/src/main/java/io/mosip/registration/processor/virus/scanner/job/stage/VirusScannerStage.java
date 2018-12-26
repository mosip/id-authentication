package io.mosip.registration.processor.virus.scanner.job.stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.util.ZipUtils;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.virus.scanner.job.decrypter.Decryptor;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;
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
	Decryptor decryptor;

	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Enrolment Status"
			+ " table is not accessible";
	private static final String VIRUS_SCAN_FAILED = "The Virus Scan for the Packet Failed";

	String description = "";
	boolean isTransactionSuccessful = false;

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.VIRUS_SCAN_BUS_IN, MessageBusAddress.PACKET_UPLOADER_IN);
	}

	@Override
	public MessageDTO process(MessageDTO object) {

		String registrationId = object.getRid();

		String encryptedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString()) + File.separator
				+ getFileName(registrationId);
		File encryptedFile = new File(encryptedPacketPath);
		boolean isEncryptedFileCleaned;
		boolean isUnpackedFileCleaned;
		InputStream encryptedPacket = null;
		InputStream decryptedData = null;

		try {
			encryptedPacket = new FileInputStream(encryptedFile);

			InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
					.getRegistrationStatus(registrationId);
			isEncryptedFileCleaned = virusScannerService.scanFile(encryptedPacketPath);
			if (isEncryptedFileCleaned) {
				decryptedData = decryptor.decrypt(encryptedPacket, registrationId);

				fileManager.put(registrationId, decryptedData, DirectoryPathDto.VIRUS_SCAN_DEC);
				String decryptedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())
						+ File.separator + getFileName(registrationId);
				String unpackedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_UNPACK.toString())
						+ File.separator + registrationId;

				ZipUtils.unZipDirectory(decryptedPacketPath, unpackedPacketPath);
				isUnpackedFileCleaned = virusScannerService.scanFolder(unpackedPacketPath);

				if (isUnpackedFileCleaned) {
					sendToPacketUploaderStage(registrationStatusDto);
				} else {
					fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
					fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_DEC, registrationId);
					// unpacked file doesn't contain extension
					fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_UNPACK, registrationId);
					processVirusScanFailure(registrationStatusDto);
				}

			} else {
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
				processVirusScanFailure(registrationStatusDto);

			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		} catch (FileNotFoundException e) {
			LOGGER.error(LOGDISPLAY, "FILE_NOT_FOUND_EXCEPTION", e);
		} catch (VirusScanFailedException | PacketDecryptionFailureException | IOException
				| io.mosip.kernel.core.exception.IOException e) {
			LOGGER.error(LOGDISPLAY, VIRUS_SCAN_FAILED, e);
		}

		return object;
	}

	private void processVirusScanFailure(InternalRegistrationStatusDto registrationStatusDto) {
		String registrationId = registrationStatusDto.getRegistrationId();
		try {

			registrationStatusDto.setStatusCode(RegistrationStatusCode.VIRUS_SCAN_FAILED.toString());
			registrationStatusDto.setStatusComment("packet is in status PACKET_FOR_VIRUS_SCAN_FAILED");
			registrationStatusDto.setUpdatedBy(USER);
			description = registrationId + " packet is infected.";
			LOGGER.info(LOGDISPLAY, registrationStatusDto.getRegistrationId(), "File is infected.");
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

	private void sendToPacketUploaderStage(InternalRegistrationStatusDto entry) {
		String registrationId = entry.getRegistrationId();
		try {
			entry.setStatusCode(RegistrationStatusCode.VIRUS_SCAN_SUCCESSFUL.toString());
			entry.setStatusComment("Packet virus scan is sucessfull");
			entry.setUpdatedBy(USER);
			isTransactionSuccessful = true;

			description = registrationId + " packet successfully  scanned for virus";
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
