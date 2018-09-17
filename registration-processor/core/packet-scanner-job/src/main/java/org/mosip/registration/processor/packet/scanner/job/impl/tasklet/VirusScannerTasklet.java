package org.mosip.registration.processor.packet.scanner.job.impl.tasklet;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.mosip.registration.processor.filesystem.adapter.FileSystemAdapter;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.manager.service.FileManager;
import org.mosip.registration.processor.packet.scanner.job.VirusScannerService;
import org.mosip.registration.processor.status.code.RegistrationStatusCode;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
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

/**
 * The Class VirusScannerTasklet.
 *
 * @author M1039303
 */
@Component
public class VirusScannerTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(LandingZoneScannerTasklet.class);

	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	private Environment env;

	@Autowired
	private VirusScannerService virusScannerService;

	@Value("${packet.ext}")
	private String extention;

	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	@Autowired
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	private FileSystemAdapter<InputStream, ?, Boolean> adapter = new FilesystemCephAdapterImpl();

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
		List<RegistrationStatusDto> registrationStatusDtoList = null;
		try {
			registrationStatusDtoList = registrationStatusService
					.getByStatus(RegistrationStatusCode.PACKET_FOR_VIRUS_SCAN.toString());
		} catch (Exception e) {
			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
			return RepeatStatus.FINISHED;
		}

		for (RegistrationStatusDto entry : registrationStatusDtoList) {

			String filepath = env.getProperty(DirectoryPathDto.VIRUS_SCAN.toString()) + File.separator
					+ getFileName(entry.getEnrolmentId());
			File file = new File(filepath);
			boolean infected = false;

			try {
				infected = virusScan(filepath);
				if (infected) {
					sendToRetry(entry);
				} else {
					sendToDFS(file, entry);
				}
			} catch (Exception e) {
				LOGGER.error(LOGDISPLAY, VIRUS_SCAN_FAILED, e);
			}

		}
		return RepeatStatus.FINISHED;
	}

	/**
	 * Random method for Virus scan for now.
	 *
	 * @param filepath
	 *            the filepath
	 * @return true, if successful
	 */
	private boolean virusScan(String filepath) {
		return virusScannerService.result(filepath);
	}

	/**
	 * Send to virus scan retry.
	 *
	 * @param entry
	 *            the entry
	 */
	private void sendToRetry(RegistrationStatusDto entry) {
		try {
			if (entry.getRetryCount() == null)
				entry.setRetryCount(0);
			fileManager.copy(entry.getEnrolmentId(), DirectoryPathDto.VIRUS_SCAN, DirectoryPathDto.VIRUS_SCAN_RETRY);
			entry.setRetryCount(entry.getRetryCount() + 1);
			entry.setStatus(RegistrationStatusCode.PACKET_FOR_VIRUS_SCAN_RETRY.toString());
			registrationStatusService.updateRegistrationStatus(entry);
			fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN, DirectoryPathDto.VIRUS_SCAN_RETRY,
					entry.getEnrolmentId());
			LOGGER.info(LOGDISPLAY, entry.getEnrolmentId(), "File is infected. It has been sent" + " to RETRY_FOLDER.");
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
	private void sendToDFS(File file, RegistrationStatusDto entry) {
		String filename = file.getName();
		filename = filename.substring(0, filename.lastIndexOf('.'));
		try {
			adapter.storePacket(filename, file);
			entry.setStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_DFS.toString());
			registrationStatusService.updateRegistrationStatus(entry);
			LOGGER.info(LOGDISPLAY, entry.getEnrolmentId(),
					"File is successfully scanned. " + "It has been sent to DFS.");
		} catch (Exception e) {
			LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e);
		}
	}

	private String getFileName(String fileName) {
		return fileName + extention;
	}

}
