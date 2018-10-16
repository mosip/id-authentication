package io.mosip.kernel.packetuploader.http.service.impl;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.packetuploader.http.config.PacketFileStorageProperties;
import io.mosip.kernel.packetuploader.http.constant.PacketUploaderExceptionConstants;
import io.mosip.kernel.packetuploader.http.dto.PacketUploaderResponceDTO;
import io.mosip.kernel.packetuploader.http.exception.MosipDirectoryNotEmpty;
import io.mosip.kernel.packetuploader.http.exception.MosipIOException;
import io.mosip.kernel.packetuploader.http.exception.MosipPacketLocationSecurity;
import io.mosip.kernel.packetuploader.http.service.PacketUploaderService;
import io.mosip.kernel.packetuploader.http.util.PacketUploaderUtils;

/**
 * Implementation of packet Uploader interface
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Service
public class PacketUploaderServiceImpl implements PacketUploaderService {

	/**
	 * {@link PacketFileStorageProperties} instance
	 */
	@Autowired
	private PacketFileStorageProperties packetFileStorageProperties;

	/**
	 * {@link PacketUploaderUtils} instance
	 */
	@Autowired
	private PacketUploaderUtils packetUploaderUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.packetuploader.service.PacketUploaderService#storePacket(org
	 * .springframework.web.multipart.MultipartFile)
	 */
	@Override
	public PacketUploaderResponceDTO storePacket(MultipartFile packet) {
		packetUploaderUtils.check(packet);
		String fileName = packet.getOriginalFilename();
		Path packetStorageLocation = Paths.get(packetFileStorageProperties.getUploadDir()).toAbsolutePath().normalize()
				.resolve(fileName);
		long fileSizeInBytes = 0;
		try {
			fileSizeInBytes = Files.copy(packet.getInputStream(), packetStorageLocation,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (DirectoryNotEmptyException e) {
			throw new MosipDirectoryNotEmpty(
					PacketUploaderExceptionConstants.MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION, e.getCause());
		} catch (SecurityException e) {
			throw new MosipPacketLocationSecurity(
					PacketUploaderExceptionConstants.MOSIP_SECURITY_FILE_LOCATION_EXCEPTION, e);
		} catch (IOException e) {
			throw new MosipIOException(PacketUploaderExceptionConstants.MOSIP_IO_FILE_EXCEPTION, e.getCause());
		}

		return new PacketUploaderResponceDTO(fileName, fileSizeInBytes);
	}

}
