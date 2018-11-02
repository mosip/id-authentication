package io.mosip.registration.processor.packet.archiver.util;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.archiver.util.exception.constant.PacketNotFoundExceptionConstant;
import io.mosip.registration.processor.packet.archiver.util.exception.constant.UnableToAccessPathExceptionConstant;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;

/**
 * The Class PacketArchiver.
 * 
 * @author M1039285
 */
@Component
public class PacketArchiver {

	/** The filesystem ceph adapter impl. */
	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapterImpl = new FilesystemCephAdapterImpl();

	/** The filemanager. */
	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;
	
	/** The event id. */
	private String eventId = "";
	
	/** The event name. */
	private String eventName = "";
	
	/** The event type. */
	private String eventType = "";
	
	/** The core audit request builder. */
	@Autowired
	CoreAuditRequestBuilder coreAuditRequestBuilder;

	
	/** The is transaction successful. */
	private boolean isTransactionSuccessful = false;
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
		String description = " ";
		InputStream encryptedpacket = filesystemCephAdapterImpl.getPacket(registrationId);

		if (encryptedpacket != null) {
			try {
				filemanager.put(registrationId, encryptedpacket, DirectoryPathDto.ARCHIVE_LOCATION);
				eventId = EventId.RPR_407.toString();
				eventName = EventName.ADD.toString();
				eventType = EventType.BUSINESS.toString();
				isTransactionSuccessful=true;
			} catch (IOException e) {
				eventId = EventId.RPR_405.toString();
				eventName = EventName.EXCEPTION.toString();
				eventType = EventType.SYSTEM.toString();
				throw new UnableToAccessPathException(
						UnableToAccessPathExceptionConstant.UNABLE_TO_ACCESS_PATH_ERROR_CODE.getErrorCode(),
						UnableToAccessPathExceptionConstant.UNABLE_TO_ACCESS_PATH_ERROR_CODE.getErrorMessage(),
						e.getCause());
			} finally {
				description = isTransactionSuccessful ? "The file is successfully copied to VM for registration Id :"+registrationId
						: "The file copying to VM is failured for registration Id: "+registrationId;
				coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
						registrationId);
			}
		} else {
			eventId = EventId.RPR_401.toString();
			eventName = EventName.GET.toString();
			eventType = EventType.BUSINESS.toString();
			description = "Packet not found in DFS";
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);
			throw new PacketNotFoundException(PacketNotFoundExceptionConstant.PACKET_NOT_FOUND_ERROR.getErrorCode(),
					PacketNotFoundExceptionConstant.PACKET_NOT_FOUND_ERROR.getErrorMessage());
		}

	}

}
