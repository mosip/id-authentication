package io.mosip.registration.processor.packet.uploader.stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.exception.DFSNotAccessibleException;
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

	/** The adapter. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The audit log request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The packet archiver. */
	@Autowired
	private PacketArchiver packetArchiver;

	/** The env. */
	@Autowired
	private Environment env;

	/** The description. */
	private String description = "";

	/** The is transaction successful. */
	private boolean isTransactionSuccessful = false;

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
		try {

			this.registrationId = object.getRid();
			InternalRegistrationStatusDto dto = registrationStatusService.getRegistrationStatus(registrationId);
			uploadpacket(dto);

		} catch (TablenotAccessibleException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.name() + e.getMessage());

			this.isTransactionSuccessful = false;
			this.description = "Registration status table is not accessible for packet " + this.registrationId;

		} finally {

			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = this.isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(this.description, eventId, eventName, eventType,
					this.registrationId);

		}

		return null;
	}

	/**
	 * Uploadpacket.
	 *
	 * @param dto
	 *            the dto
	 */
	private void uploadpacket(InternalRegistrationStatusDto dto) {
		try {
			packetArchiver.archivePacket(dto.getRegistrationId());
			String filepath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString()) + File.separator
					+ dto.getRegistrationId() + ".zip";
			File file = new File(filepath);
			InputStream decryptedData = new FileInputStream(file);
			sendToDFS(dto, decryptedData);
		} catch (PacketNotFoundException ex) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PUM_PACKET_NOT_FOUND_EXCEPTION.name() + ex.getMessage());
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.name() + e.getMessage());

		}
	}

	/**
	 * Send to DFS.
	 *
	 * @param entry
	 *            the entry
	 * @param decryptedData
	 *            the decrypted data
	 */
	private void sendToDFS(InternalRegistrationStatusDto entry, InputStream decryptedData) {

		registrationId = entry.getRegistrationId();
		try {

			adapter.storePacket(registrationId, decryptedData);
			adapter.unpackPacket(registrationId);

			if (adapter.isPacketPresent(registrationId)) {
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_DEC, registrationId);
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
				fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_UNPACK, registrationId);

				entry.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
				entry.setStatusComment("Packet " + registrationId + " is uploaded in file system.");
				entry.setUpdatedBy(USER);

				registrationStatusService.updateRegistrationStatus(entry);
				isTransactionSuccessful = true;
				description = registrationId + " packet successfully has been send to DFS";
			}
		} catch (DFSNotAccessibleException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_DFS.name() + e.getMessage());

			description = "FileSytem is not accessible for packet " + registrationId;
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.name() + e.getMessage());
			description = "Virus scan path is not accessible for packet " + registrationId;
		} catch (TablenotAccessibleException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.name() + e.getMessage());
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

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {

		mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consume(mosipEventBus, MessageBusAddress.PACKET_UPLOADER_IN);

	}

}
