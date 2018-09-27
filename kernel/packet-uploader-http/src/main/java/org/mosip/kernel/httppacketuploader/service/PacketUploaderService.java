package org.mosip.kernel.httppacketuploader.service;

import org.mosip.kernel.httppacketuploader.dto.PacketUploaderResponceDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for packet uploader
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Service
public interface PacketUploaderService {

	/**
	 * this stores packet into configured location
	 * 
	 * @param packet
	 *            packet to store
	 * @return {@link PacketUploaderResponceDTO} with file information
	 */
	PacketUploaderResponceDTO storePacket(MultipartFile packet);

}
