package io.mosip.registration.processor.virus.scanner.job.stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.ZipUtils;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.virus.scanner.job.decrypter.Decryptor;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.VirusScanFailedException;
import io.mosip.registration.processor.virus.scanner.job.util.StatusMessage;

/**
 * The Class VirusScannerStage.
 */
@Service
public class VirusScannerStage extends MosipVerticleManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(VirusScannerStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	@Autowired
	private Environment env;

	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The virus scanner service. */
	@Autowired
	private VirusScanner<Boolean, String> virusScannerService;

	/** The file manager. */
	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The decryptor. */
	@Autowired
	private Decryptor decryptor;

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** The Constant VIRUS_SCAN_FAILED. */
	private static final String VIRUS_SCAN_FAILED = "The Virus Scan for the Packet Failed";

	/** The description. */
	String description = "";

	/** The is transaction successful. */
	boolean isTransactionSuccessful = false;

	RegistrationExceptionMapperUtil registrationStatusMapperUtil = new RegistrationExceptionMapperUtil();

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.VIRUS_SCAN_BUS_IN, MessageBusAddress.VIRUS_SCAN_BUS_OUT);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {

		String registrationId = object.getRid();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "VirusScannerStage::process()::entry");
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);
		String extension = env.getProperty("registration.processor.packet.ext");
		String encryptedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString()) + File.separator
				+ registrationId + extension;
		
		String filepath=env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString());
		File encryptedFile=FileUtils.getFile(filepath, registrationId+extension);
		//File encryptedFile = new File(encryptedPacketPath);
		boolean isEncryptedFileCleaned;
		boolean isUnpackedFileCleaned;

		InputStream decryptedData = null;

		// To avoid sonar issue
		try (InputStream encryptedPacket = new FileInputStream(encryptedFile)) {
			registrationStatusDto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.VIRUS_SCAN.toString());
			registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());

			isEncryptedFileCleaned =virusScannerService.scanFile(encryptedPacketPath);
			if (isEncryptedFileCleaned) {
				decryptedData = decryptor.decrypt(encryptedPacket, registrationId);

				fileManager.put(registrationId, decryptedData, DirectoryPathDto.VIRUS_SCAN_DEC);
				String decryptedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())
						+ File.separator + registrationId + extension;
				String unpackedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_UNPACK.toString())
						+ File.separator + registrationId;

				ZipUtils.unZipDirectory(decryptedPacketPath, unpackedPacketPath);
				isUnpackedFileCleaned =virusScannerService.scanFolder(unpackedPacketPath);

				if (isUnpackedFileCleaned) {
					sendToPacketUploaderStage(registrationStatusDto);
					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());

					description = "virus scan successful for registrationId " + registrationId;
					isTransactionSuccessful = true;
				} else {
					registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
							.getStatusCode(RegistrationExceptionTypeCode.VIRUS_SCAN_FAILED_EXCEPTION));
					fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
					fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_DEC, registrationId);
					// unpacked file doesn't contain extension
					fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_UNPACK, registrationId);
					processVirusScanFailure(registrationStatusDto);
				}

			} else {
				registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.VIRUS_SCAN_FAILED_EXCEPTION));
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
				processVirusScanFailure(registrationStatusDto);

			}

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "VirusScannerStage::process()::exit");
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, description);
		} catch (VirusScanFailedException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(VIRUS_SCAN_FAILED);
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.VIRUS_SCAN_FAILED_EXCEPTION));

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					VIRUS_SCAN_FAILED + " " + e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setIsValid(false);
			object.setInternalError(Boolean.TRUE);
			description = "virus scan failed for registrationId " + registrationId + "::" + e.getMessage();
		} catch (ApisResourceAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto
					.setStatusComment(PlatformErrorMessages.RPR_PSJ_API_RESOUCE_ACCESS_FAILED.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			description = PlatformErrorMessages.RPR_PSJ_API_RESOUCE_ACCESS_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					VIRUS_SCAN_FAILED + " " + e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setIsValid(false);
			object.setInternalError(Boolean.TRUE);
		} catch (IOException | io.mosip.kernel.core.exception.IOException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					VIRUS_SCAN_FAILED + " " + e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setIsValid(false);
			object.setInternalError(Boolean.TRUE);
			description = "virus scan failed for registrationId " + registrationId + "::" + e.getMessage();
		} catch (PacketDecryptionFailureException e) {
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PACKET_DECRYPTION_FAILURE_EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					e.getErrorCode(), e.getErrorText());
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_DECRYPTION_FAILURE);
			registrationStatusDto.setUpdatedBy(USER);

			isTransactionSuccessful = false;
			object.setIsValid(false);
			object.setInternalError(Boolean.TRUE);
			description = "Packet decryption failed for registrationId " + registrationId + "::" + e.getErrorCode()
					+ e.getErrorText();
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PSJ_VIRUS_SCAN_FAILED.getMessage() + ex.getMessage()
							+ ExceptionUtils.getStackTrace(ex));
			object.setIsValid(false);
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occurred in virus scanner stage while processing registrationId : "
					+ registrationId + ex.getMessage();
		} finally {

			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, registrationId,
					ApiName.DMZAUDIT);

		}

		return object;
	}

	/**
	 * Process virus scan failure.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	private void processVirusScanFailure(InternalRegistrationStatusDto registrationStatusDto) {
		String registrationId = registrationStatusDto.getRegistrationId();

		registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
		registrationStatusDto.setStatusComment(StatusMessage.PACKET_VIRUS_SCAN_FAILURE);
		registrationStatusDto.setUpdatedBy(USER);
		isTransactionSuccessful = false;
		description = registrationId + " packet is infected.";
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationStatusDto.getRegistrationId(), "File is infected.");

	}

	/**
	 * Send to packet uploader stage.
	 *
	 * @param entry
	 *            the entry
	 */
	private void sendToPacketUploaderStage(InternalRegistrationStatusDto entry) {
		String registrationId = entry.getRegistrationId();

		entry.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
		entry.setStatusComment(StatusMessage.PACKET_VIRUS_SCAN_SUCCESSFUL);
		entry.setUpdatedBy(USER);
		isTransactionSuccessful = true;
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				entry.getRegistrationId(), "File is successfully scanned.");
		description = registrationId + " packet successfully  scanned for virus";

	}

}
