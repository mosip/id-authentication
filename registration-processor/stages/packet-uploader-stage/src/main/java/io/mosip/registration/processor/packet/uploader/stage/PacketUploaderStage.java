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
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
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
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

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

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		boolean isTransactionSuccessful = false;
		String description = "";
		try {
			this.registrationId = object.getRid();
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "PacketUploaderStage::process()::entry");
			InternalRegistrationStatusDto dto = registrationStatusService.getRegistrationStatus(registrationId);
			object=uploadpacket(dto,object);

			isTransactionSuccessful = true;
			description = "Packet uploaded to DFS sucessfully for registrationId " + this.registrationId;
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,description);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,"PacketUploaderStage::process()::exit");
		} catch (TablenotAccessibleException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.name() + ExceptionUtils.getStackTrace(e));

			description = "Registration status TablenotAccessibleException for registrationId " + this.registrationId
					+ "::" + e.getMessage();

		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.PACKET_UPLOAD_FAILED.name() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing for registrationId " + registrationId + "::"
					+ e.getMessage();
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
					this.registrationId);

		}

		return object;
	}

	/**
	 * Uploadpacket.
	 *
	 * @param dto
	 *            the dto
	 */
    private MessageDTO uploadpacket(InternalRegistrationStatusDto dto,MessageDTO object) {
        boolean isTransactionSuccessful = false;
		String description = "";
		try {
			packetArchiver.archivePacket(dto.getRegistrationId());
			String filepath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString()) + File.separator
					+ dto.getRegistrationId() + ".zip";
			File file = new File(filepath);
			InputStream decryptedData = new FileInputStream(file);
			object=sendToDFS(dto, decryptedData,object);
			isTransactionSuccessful = true;
			description = "Packet sent to DFS with registrationId " + dto.getRegistrationId();
		} catch (PacketNotFoundException ex) {
			object.setInternalError(true);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PUM_PACKET_NOT_FOUND_EXCEPTION.name() + ExceptionUtils.getStackTrace(ex));
			description = "Packet not found in DFS for registrationId " + registrationId + "::" + ex.getMessage();
		} catch (IOException e) {
			object.setInternalError(true);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.name() + ExceptionUtils.getStackTrace(e));
			description = "Virus scan decryption path not found for registrationId " + registrationId + "::"
					+ e.getMessage();

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
		return object;
	}

	/**
	 * Send to DFS.
	 *
	 * @param entry
	 *            the entry
	 * @param decryptedData
	 *            the decrypted data
	 */
    private MessageDTO sendToDFS(InternalRegistrationStatusDto entry, InputStream decryptedData,MessageDTO object) {
        boolean isTransactionSuccessful = false;
		String description = "";
		registrationId = entry.getRegistrationId();
		try {

			hdfsAdapter.storePacket(registrationId, decryptedData);
			hdfsAdapter.unpackPacket(registrationId);

			if (hdfsAdapter.isPacketPresent(registrationId)) {

				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_DEC, registrationId);
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
				fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_UNPACK, registrationId);

				entry.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
				entry.setStatusComment("Packet " + registrationId + " is uploaded in file system.");

				entry.setUpdatedBy(USER);
				registrationStatusService.updateRegistrationStatus(entry);

				object.setInternalError(false);
				object.setIsValid(true);
				object.setRid(registrationId);


				isTransactionSuccessful = true;
				description = " packet sent to DFS for registrationId " + registrationId;
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						PlatformErrorMessages.RPR_PUM_PACKET_DELETION_INFO.getMessage());

			}
		} catch (FSAdapterException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PUM_PACKET_STORE_NOT_ACCESSIBLE.name() + e.getMessage());

			description = "DFS not accessible for registrationId " + registrationId + "::" + e.getMessage();
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.name() + e.getMessage());
			description = "Virus scan path not accessible for registrationId " + registrationId + "::" + e.getMessage();
			object.setInternalError(true);
			description = "Virus scan path is not accessible for packet " + registrationId;
		} catch (TablenotAccessibleException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.name() + e.getMessage());

			description = "The Registration Status table is not accessible for packet " + registrationId;
		}   finally {

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
		return object;
	}

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {

		mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.PACKET_UPLOADER_IN,MessageBusAddress.PACKET_UPLOADER_OUT);

	}

}
