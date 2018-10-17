package io.mosip.kernel.packetuploader.http.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.packetuploader.http.constant.PacketUploaderConstants;
import io.mosip.kernel.packetuploader.http.constant.PacketUploaderExceptionConstants;
import io.mosip.kernel.packetuploader.http.exception.MosipInvalidFileName;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Component
public class PacketUploaderUtils {
	/**
	 * validation method for packetFile
	 * 
	 * @param packet
	 *            packet to be uploaded
	 */
	public void check(MultipartFile packet) {
		if (packet.getOriginalFilename().contains(PacketUploaderConstants.INVALID_FILE.getValue())) {
			throw new MosipInvalidFileName(PacketUploaderExceptionConstants.MOSIP_INVALID_FILE_NAME_EXCEPTION);
		} else if (packet.getSize() == Long.parseLong(PacketUploaderConstants.PACKET_MIN_SIZE.getValue())) {
			throw new MaxUploadSizeExceededException(
					Long.parseLong(PacketUploaderConstants.PACKET_MIN_SIZE.getValue()));
		}

	}
}
