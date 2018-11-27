package io.mosip.kernel.packetuploader.http.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.packetuploader.http.constant.PacketUploaderConstant;

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
		if (packet.getSize() == Long.parseLong(PacketUploaderConstant.PACKET_MIN_SIZE.getValue())) {
			throw new MaxUploadSizeExceededException(
					Long.parseLong(PacketUploaderConstant.PACKET_MIN_SIZE.getValue()));
		}

	}
}
