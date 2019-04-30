package io.mosip.registration.processor.packet.uploader.stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
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
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.exception.PacketNotFoundException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class PacketUploaderStage.
 * 
 * @author M1049387
 */
@Component
public class PacketUploaderStage extends MosipVerticleManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketUploaderStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The cluster url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	@Value("${registration.processor.uploader.max.retry.count}")
	private int maxRetryCount;

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private FileSystemAdapter hdfsAdapter;

	/** The audit log request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The packet archiver. */
	@Autowired
	private PacketArchiver packetArchiver;

	/** The env. */
	@Autowired
	private Environment env;

	/** The registration id. */
	private String registrationId = "";

	/** The file manager. */
	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	private boolean isTransactionSuccessful;

	String description = "";
	RegistrationExceptionMapperUtil registrationStatusMapperUtil = new RegistrationExceptionMapperUtil();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		this.registrationId = object.getRid();

		isTransactionSuccessful = false;
		InternalRegistrationStatusDto dto = new InternalRegistrationStatusDto();
		try {
			dto = registrationStatusService.getRegistrationStatus(registrationId);
			int retrycount = (dto.getRetryCount() == null) ? 0 : dto.getRetryCount() + 1;
			dto.setRetryCount(retrycount);
			dto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.UPLOAD_PACKET.toString());
			dto.setRegistrationStageName(this.getClass().getSimpleName());
			if (retrycount < getMaxRetryCount()) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"PacketUploaderStage::process()::entry");

				object = uploadpacket(dto, object);
				if (object.getIsValid()) {
					dto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
					isTransactionSuccessful = true;
					description = "Packet uploaded to DFS successfully for registrationId " + this.registrationId;
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							"PacketUploaderStage::process()::exit");

				}
			} else {

				object.setInternalError(Boolean.TRUE);
				description = "Failure in uploading the packet to Packet Store" + registrationId;
				dto.setLatestTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.PACKET_UPLOADER_FAILED));
				dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOAD_TO_PACKET_STORE_FAILED.toString());
				dto.setStatusComment("Packet upload to packet store failed for " + registrationId);
				dto.setUpdatedBy(USER);
			}
		} catch (TablenotAccessibleException e) {
			dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOAD_TO_PACKET_STORE_REPROCESSING.toString());
			dto.setStatusComment(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage());
			dto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.TABLE_NOT_ACCESSIBLE_EXCEPTION));
			object.setInternalError(true);
			object.setIsValid(false);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.name()
							+ ExceptionUtils.getStackTrace(e));

			description = "Registration status TablenotAccessibleException for registrationId " + this.registrationId
					+ "::" + e.getMessage();

		} catch (PacketNotFoundException ex) {
			dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOAD_TO_PACKET_STORE_FAILED.toString());
			dto.setStatusComment(PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_PACKET_STORE.getMessage());
			dto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PACKET_NOT_FOUND_EXCEPTION));
			object.setInternalError(true);
			object.setIsValid(false);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_PUM_PACKET_NOT_FOUND_EXCEPTION.name() + ExceptionUtils.getStackTrace(ex));
			description = "Packet not found in DFS for registrationId " + registrationId + "::" + ex.getMessage();
		} catch (FSAdapterException e) {
			dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOAD_TO_PACKET_STORE_REPROCESSING.toString());
			dto.setStatusComment(PlatformErrorMessages.RPR_PUM_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			dto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			object.setInternalError(true);
			object.setIsValid(false);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PUM_PACKET_STORE_NOT_ACCESSIBLE.name() + e.getMessage());

			description = "DFS not accessible for registrationId " + registrationId + "::" + e.getMessage();
		} catch (IOException e) {
			dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOAD_TO_PACKET_STORE_FAILED.toString());
			dto.setStatusComment(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			dto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			object.setIsValid(false);
			object.setInternalError(true);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.name() + ExceptionUtils.getStackTrace(e));
			description = "Virus scan decryption path not found for registrationId " + registrationId + "::"
					+ e.getMessage();

		} catch (Exception e) {
			dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOAD_TO_PACKET_STORE_FAILED.toString());
			dto.setStatusComment(ExceptionUtils.getMessage(e));
			dto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			object.setInternalError(true);
			object.setIsValid(false);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.PACKET_UPLOAD_FAILED.name() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occurred while processing for registrationId " + registrationId + "::"
					+ e.getMessage();
		} finally {
			registrationStatusService.updateRegistrationStatus(dto);
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					this.registrationId, ApiName.AUDIT);

		}

		return object;
	}

	/**
	 * Uploadpacket.
	 *
	 * @param dto
	 *            the dto
	 * @throws IOException
	 */
	private MessageDTO uploadpacket(InternalRegistrationStatusDto dto, MessageDTO object) throws IOException {

		packetArchiver.archivePacket(dto.getRegistrationId());
		String filepath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString()) + File.separator
				+ dto.getRegistrationId() + ".zip";
		File file = new File(filepath);
		InputStream decryptedData = new FileInputStream(file);
		object = sendToDFS(dto, decryptedData, object);
		if (object.getIsValid()) {
			isTransactionSuccessful = true;
			description = "Packet sent to DFS with registrationId " + dto.getRegistrationId();
		}

		return object;
	}

	/**
	 * Send to DFS.
	 *
	 * @param entry
	 *            the entry
	 * @param decryptedData
	 *            the decrypted data
	 * @throws IOException
	 */
	private MessageDTO sendToDFS(InternalRegistrationStatusDto entry, InputStream decryptedData, MessageDTO object)
			throws IOException {

		object.setIsValid(false);

		registrationId = entry.getRegistrationId();

		hdfsAdapter.storePacket(registrationId, decryptedData);
		hdfsAdapter.unpackPacket(registrationId);

		if (hdfsAdapter.isPacketPresent(registrationId)) {

			fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_DEC, registrationId);
			fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
			fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_UNPACK, registrationId);

			entry.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
			entry.setStatusComment("Packet " + registrationId + " is uploaded in file system.");
			entry.setUpdatedBy(USER);
			object.setInternalError(false);
			object.setIsValid(true);
			object.setRid(registrationId);

			isTransactionSuccessful = true;
			description = " packet sent to DFS for registrationId " + registrationId;
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PUM_PACKET_DELETION_INFO.getMessage());

		}

		return object;
	}

	/**
	 * Get max retry count.
	 * 
	 * @return maxRetryCount
	 */
	public int getMaxRetryCount() {
		return maxRetryCount;
	}

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {

		mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.PACKET_UPLOADER_IN, MessageBusAddress.PACKET_UPLOADER_OUT);

	}

}
