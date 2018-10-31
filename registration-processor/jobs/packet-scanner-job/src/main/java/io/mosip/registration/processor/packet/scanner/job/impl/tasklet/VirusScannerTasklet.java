package io.mosip.registration.processor.packet.scanner.job.impl.tasklet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.virus.scanner.service.VirusScannerService;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.scanner.job.exception.VirusScanFailedException;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class VirusScannerTasklet.
 *
 * @author Mukul Puspam
 */
@Component
public class VirusScannerTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(VirusScannerTasklet.class);

	private static final String USER = "MOSIP_SYSTEM";

	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	private Environment env;

	@Value("${registration.processor.packet.ext}")
	private String extention;

	@Autowired
	VirusScannerService<Boolean, String> virusScannerService;

	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private FileSystemAdapter<InputStream, Boolean> adapter = new FilesystemCephAdapterImpl();

	private static final String RETRY_FOLDER_NOT_ACCESSIBLE = "The Retry Folder set by the System"
			+ " is not accessible";
	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";
	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Enrolment Status"
			+ " table is not accessible";
	private static final String VIRUS_SCAN_FAILED = "The Virus Scan for the Packet Failed";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.
	 * springframework.batch.core.StepContribution,
	 * org.springframework.batch.core.scope.context.ChunkContext)
	 */

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		List<InternalRegistrationStatusDto> registrationStatusDtoList = null;
		try {
			registrationStatusDtoList = registrationStatusService
					.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
		} catch (TablenotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
			return RepeatStatus.FINISHED;
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
		return RepeatStatus.FINISHED;
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
		} catch (Exception e) {
			LOGGER.error(LOGDISPLAY, RETRY_FOLDER_NOT_ACCESSIBLE, e);
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
			entry.setStatusComment("packet is in status PACKET_UPLOADED_TO_DFS");
			entry.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(entry);
		} catch (IOException e) {
			LOGGER.error(LOGDISPLAY, entry.getRegistrationId() + ": Failed to delete the packet from Virus scan Zone",
					e);
		} catch (Exception e) {
			LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e);
		}
	}

	private String getFileName(String fileName) {
		return fileName + extention;
	}

}
