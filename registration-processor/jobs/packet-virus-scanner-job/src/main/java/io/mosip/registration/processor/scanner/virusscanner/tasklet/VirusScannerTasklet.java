package io.mosip.registration.processor.scanner.virusscanner.tasklet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.mosip.kernel.virusscanner.clamav.service.VirusScannerService;
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

import io.mosip.registration.processor.auditmanager.code.AuditLogConstant;
import io.mosip.registration.processor.auditmanager.code.EventId;
import io.mosip.registration.processor.auditmanager.code.EventName;
import io.mosip.registration.processor.auditmanager.code.EventType;
import io.mosip.registration.processor.auditmanager.requestbuilder.ClientAuditRequestBuilder;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.scanner.virusscanner.exception.VirusScanFailedException;
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

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(VirusScannerTasklet.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The env. */
	@Autowired
	private Environment env;

	@Value("${registration.processor.packet.ext}")
	private String extention;

	/** The virus scanner service. */
	@Autowired
	VirusScannerService<Boolean, String> virusScannerService;

	/** The file manager. */
	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	FilesystemCephAdapterImpl adapter;

	/** The Constant RETRY_FOLDER_NOT_ACCESSIBLE. */
	private static final String RETRY_FOLDER_NOT_ACCESSIBLE = "The Retry Folder set by the System"
			+ " is not accessible";

	/** The Constant DFS_NOT_ACCESSIBLE. */
	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";

	/** The Constant REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE. */
	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Enrolment Status"
			+ " table is not accessible";

	/** The Constant VIRUS_SCAN_FAILED. */
	private static final String VIRUS_SCAN_FAILED = "The Virus Scan for the Packet Failed";

	/** The core audit request builder. */
	@Autowired
	ClientAuditRequestBuilder clientAuditRequestBuilder;

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/** The description. */
	private String description = "";

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
		boolean isTransactionSuccessful = false;

		try {
			registrationStatusDtoList = registrationStatusService
					.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
			isTransactionSuccessful = true;

		} catch (TablenotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
			return RepeatStatus.FINISHED;
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_401.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventName.GET.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "Packet uploaded to virus scanner successfully"
					: "Packet uploading to virus scanner failed";

			clientAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.MULTIPLE_ID.toString());
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
		boolean isTransactionSuccessful = false;
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
			isTransactionSuccessful = true;

			LOGGER.info(LOGDISPLAY, entry.getRegistrationId(),
					"File is infected. It has been sent" + " to RETRY_FOLDER.");
		} catch (Exception e) {
			LOGGER.error(LOGDISPLAY, RETRY_FOLDER_NOT_ACCESSIBLE, e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_403.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_403.toString()) ? EventName.DELETE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_403.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful
					? "File is infected. It has been sent to VIRUS_SCAN_RETRY folder successfully"
					: "File is infected, sending to VIRUS_SCAN_RETRY folder failed";
			clientAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
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
		boolean isTransactionSuccessful = false;
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
			isTransactionSuccessful = true;
		} catch (IOException e) {
			LOGGER.error(LOGDISPLAY, entry.getRegistrationId() + ": Failed to delete the packet from Virus scan Zone",
					e);
		} catch (Exception e) {
			LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "Packet successfully saved to packet store"
					: "Failed to save packet in packet store";

			clientAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
		}

	}

	/**
	 * Gets the file name.
	 *
	 * @param fileName
	 *            the file name
	 * @return the file name
	 */
	private String getFileName(String fileName) {
		return fileName + extention;
	}

}
