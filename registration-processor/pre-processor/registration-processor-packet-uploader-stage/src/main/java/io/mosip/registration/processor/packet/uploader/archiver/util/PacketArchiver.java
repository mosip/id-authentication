package io.mosip.registration.processor.packet.uploader.archiver.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.constant.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.SftpJschConnectionDto;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.uploader.exception.PacketNotFoundException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

/**
 * The Class PacketArchiver.
 * 
 * @author M1049387
 */
@Component
public class PacketArchiver {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketArchiver.class);

	/** The audit log request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The filemanager. */
	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	/** The env. */
	@Autowired
	private Environment env;

	//@Value("${registration.processor.server.ppk.filelocation}")
	private String ppkFileLocation;

	//@Value("${registration.processor.server.ppk.filename}")
	private String ppkFileName;

	@Value("${registration.processor.dmz.server.host}")
	private String host;

	@Value("${registration.processor.dmz.server.port}")
	private String dmzPort;

	@Value("${registration.processor.dmz.server.user}")
	private String dmzServerUser;

	@Value("${registration.processor.dmz.server.protocal}")
	private String dmzServerProtocal;

	public boolean archivePacket(String registrationId) throws IOException {

		boolean isTransactionSuccessful = false;
		String description = "";

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "PacketArchiver::archivePacket()::entry");
		try {

			String fromFilePath = env.getProperty(DirectoryPathDto.LANDING_ZONE.toString()) + File.separator + registrationId+ ".zip";
			String toFilePath = env.getProperty(DirectoryPathDto.ARCHIVE_LOCATION.toString()) + File.separator + registrationId+ ".zip";
			SftpJschConnectionDto jschConnectionDto=new SftpJschConnectionDto();
			jschConnectionDto.setHost(host);
			jschConnectionDto.setPort(Integer.parseInt(dmzPort));
			jschConnectionDto.setPpkFileLocation(ppkFileLocation+File.separator+ppkFileName);
			jschConnectionDto.setUser(dmzServerUser);
			jschConnectionDto.setProtocal(dmzServerProtocal);

			if (filemanager.moveFile(fromFilePath, toFilePath, jschConnectionDto)) {
				description = "Packet successfully archived for registrationId " + registrationId;
				isTransactionSuccessful = true;
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
						registrationId, "PacketArchiver::archivePacket()::exit");
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
						registrationId, description);
			} else {
				description = "Packet not moved from LANDING ZONE ENCRYPTED FOLDER DURING ARCHIVAL " + registrationId + "::"
						+ PlatformErrorMessages.RPR_PUM_PACKET_NOT_FOUND_EXCEPTION.getMessage();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
				throw new PacketNotFoundException(
						PlatformErrorMessages.RPR_PUM_PACKET_NOT_FOUND_EXCEPTION.getMessage());

			}
		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.ARCHIVE_PACKETS.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId, ApiName.AUDIT);
		}
		return isTransactionSuccessful;
	}


}
