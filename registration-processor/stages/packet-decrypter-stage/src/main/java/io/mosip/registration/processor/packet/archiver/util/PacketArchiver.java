package io.mosip.registration.processor.packet.archiver.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.archiver.util.exception.constant.PacketNotFoundExceptionConstant;
import io.mosip.registration.processor.packet.archiver.util.exception.constant.UnableToAccessPathExceptionConstant;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

/**
 * The Class PacketArchiver.
 * 
 * @author M1039285
 */
@Component
public class PacketArchiver {

	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The filesystem ceph adapter impl. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapter;

	/** The filemanager. */
	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	String description = "";
	boolean isTransactionSuccessful = false;
	
	/**
	 * Archive packet.
	 *
	 * @param registrationId
	 *            the registration id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws UnableToAccessPathException
	 *             the unable to access path exception
	 * @throws PacketNotFoundException
	 *             the packet not found exception
	 */
	public void archivePacket(String registrationId)
			throws IOException, UnableToAccessPathException, PacketNotFoundException {
		 description = "failure";

		InputStream encryptedpacket = filesystemCephAdapter.getPacket(registrationId);

		if (encryptedpacket != null) {
			try {
				filemanager.put(registrationId, encryptedpacket, DirectoryPathDto.ARCHIVE_LOCATION);
				description = "description--The file is successfully copied to VM";
			} catch (FilePathNotAccessibleException e) {
				description = "description--Unable to access the File path";
				throw new UnableToAccessPathException(
						UnableToAccessPathExceptionConstant.UNABLE_TO_ACCESS_PATH_ERROR_CODE.getErrorCode(),
						UnableToAccessPathExceptionConstant.UNABLE_TO_ACCESS_PATH_ERROR_CODE.getErrorMessage(),
						e.getCause());

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
		} else {
			description = "description--Packet not found in DFS";


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

		
			throw new PacketNotFoundException(PacketNotFoundExceptionConstant.PACKET_NOT_FOUND_ERROR.getErrorCode(),
					PacketNotFoundExceptionConstant.PACKET_NOT_FOUND_ERROR.getErrorMessage());
		}
	}
	

	

}
