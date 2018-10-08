package io.mosip.registration.processor.packet.archiver.util;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.archiver.util.exception.constant.PacketNotFoundExceptionConstant;
import io.mosip.registration.processor.packet.archiver.util.exception.constant.UnableToAccessPathExceptionConstant;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.status.code.AuditLogTempConstant;

@Component
public class PacketArchiver {

	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	private FileSystemAdapter<InputStream, PacketFiles, Boolean> filesystemCephAdapterImpl = new FilesystemCephAdapterImpl();

	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	public void archivePacket(String registrationId)
			throws IOException, UnableToAccessPathException, PacketNotFoundException {
		String description = "failure";

		InputStream encryptedpacket = filesystemCephAdapterImpl.getPacket(registrationId);

		if (encryptedpacket != null) {
			try {
				filemanager.put(getFileName(registrationId), encryptedpacket, DirectoryPathDto.ARCHIVE_LOCATION);
				description = "description--The file is successfully copied to VM";
			} catch (IOException e) {
				description = "description--Unable to access the File path";
				throw new UnableToAccessPathException(
						UnableToAccessPathExceptionConstant.UNABLE_TO_ACCESS_PATH_ERROR_CODE.getErrorCode(),
						UnableToAccessPathExceptionConstant.UNABLE_TO_ACCESS_PATH_ERROR_CODE.getErrorMessage(),
						e.getCause());

			} finally {
				createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
						AuditLogTempConstant.APPLICATION_NAME.toString(), description,
						AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
						AuditLogTempConstant.EVENT_TYPE.toString());
			}
		} else {
			description = "description--Packet not found in DFS";
			createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
					AuditLogTempConstant.APPLICATION_NAME.toString(), description,
					AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
					AuditLogTempConstant.EVENT_TYPE.toString());
			throw new PacketNotFoundException(PacketNotFoundExceptionConstant.PACKET_NOT_FOUND_ERROR.getErrorCode(),
					PacketNotFoundExceptionConstant.PACKET_NOT_FOUND_ERROR.getErrorMessage());
		}
		/*
		 * if (filemanager.checkIfFileExists(DirectoryPathDto.ARCHIVE_LOCATION,
		 * registrationId)) { try {
		 * filesystemCephAdapterImpl.deletePacket(registrationId); description =
		 * "description--The file is successfully deleted from DFS"; } catch (Exception
		 * e) { description = "Packet not deleted from DFS"; } finally {
		 * 
		 * createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
		 * AuditLogTempConstant.APPLICATION_NAME.toString(), description,
		 * AuditLogTempConstant.EVENT_ID.toString(),
		 * AuditLogTempConstant.EVENT_TYPE.toString(),
		 * AuditLogTempConstant.EVENT_TYPE.toString()); } }
		 */
	}

	public void createAuditRequestBuilder(String applicationId, String applicationName, String description,
			String eventId, String eventName, String eventType) {
		auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId(applicationId)
				.setApplicationName(applicationName).setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
				.setDescription(description).setEventId(eventId).setEventName(eventName).setEventType(eventType)
				.setHostIp(AuditLogTempConstant.HOST_IP.toString())
				.setHostName(AuditLogTempConstant.HOST_NAME.toString()).setId(AuditLogTempConstant.ID.toString())
				.setIdType(AuditLogTempConstant.ID_TYPE.toString())
				.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
				.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
				.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
				.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());

		AuditRequestDto auditRequestDto = auditRequestBuilder.build();
		auditHandler.writeAudit(auditRequestDto);
	}

	public String getFileName(String id) {
		return id + ".zip";
	}

}
