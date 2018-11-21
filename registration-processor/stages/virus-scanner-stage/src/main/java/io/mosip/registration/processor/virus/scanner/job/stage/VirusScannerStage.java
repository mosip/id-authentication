package io.mosip.registration.processor.virus.scanner.job.stage;

import java.io.File;
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
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
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
	VirusScanner<Boolean, String> virusScannerService;

	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	private static final String RETRY_FOLDER_NOT_ACCESSIBLE = "The Retry Folder set by the System"
			+ " is not accessible";
	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";
	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Enrolment Status"
			+ " table is not accessible";
	private static final String VIRUS_SCAN_FAILED = "The Virus Scan for the Packet Failed";
	private static final String UNABLE_TO_DELETE = "unable to delete after sending to DFS.";

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consume(mosipEventBus, MessageBusAddress.LANDING_ZONE_BUS_OUT);
	}

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

			String filepath = env.getProperty(DirectoryPathDto.VIRUS_SCAN.toString()) + File.separator
					+ getFileName(entry.getRegistrationId());
			File file = new File(filepath);
			boolean isClean = false;

			try {
				isClean = virusScannerService.scanFile(filepath);
				if (isClean) {
					sendToDFS(file, entry);
				} else {
					sendToRetry(entry);
				}
			} catch (VirusScanFailedException e) {
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
	 */
	private void sendToRetry(InternalRegistrationStatusDto entry) {
		try {
			if (entry.getRetryCount() == null)
				entry.setRetryCount(0);
			fileManager.copy(entry.getRegistrationId(), DirectoryPathDto.VIRUS_SCAN, DirectoryPathDto.VIRUS_SCAN_RETRY);
			entry.setRetryCount(entry.getRetryCount() + 1);
			entry.setStatusCode(RegistrationStatusCode.VIRUS_SCAN_FAILED.toString());
			entry.setStatusComment("packet is in status PACKET_FOR_VIRUS_SCAN_RETRY");
			entry.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(entry);
			fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN, DirectoryPathDto.VIRUS_SCAN_RETRY,
					entry.getRegistrationId());
			LOGGER.info(LOGDISPLAY, entry.getRegistrationId(),
					"File is infected. It has been sent" + " to RETRY_FOLDER.");
		} catch (IOException | FilePathNotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, RETRY_FOLDER_NOT_ACCESSIBLE, e);
		} catch (TablenotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
		}

	}

	/**
	 * Send to DFS.
	 *
	 * @param file
	 *            the file
	 * @param entry
	 *            the entry
	 */
	private void sendToDFS(File file, InternalRegistrationStatusDto entry) {
		String filename = file.getName();
		filename = filename.substring(0, filename.lastIndexOf('.'));
		try {
			adapter.storePacket(filename, file);
			if (adapter.isPacketPresent(entry.getRegistrationId())) {
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN, entry.getRegistrationId());
				LOGGER.info(LOGDISPLAY, entry.getRegistrationId(),
						"File is Already exists in DFS location " + " And its now Deleted from Virus scanner job ");
			} else {
				LOGGER.info(LOGDISPLAY, entry.getRegistrationId(),
						"File is successfully scanned. " + "It has been sent to DFS.");
			}
			entry.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
			registrationStatusService.updateRegistrationStatus(entry);
		} catch (DFSNotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e);
		} catch (IOException | FilePathNotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, entry.getRegistrationId() + UNABLE_TO_DELETE, e);
		}
	}

	private String getFileName(String fileName) {
		return fileName + extention;
	}

}
