package io.mosip.registration.processor.request.handler.upload;

import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;

/**
 * The Interface SyncUploadEncryptionService.
 * 
 * @author Sowmya
 */
public interface SyncUploadEncryptionService {

	/**
	 * Upload uin packet.
	 *
	 * @param uinZipFile
	 *            the uin zip file
	 * @param registrationId
	 *            the registration id
	 * @param creationTime
	 *            the creation time
	 * @return the packer generator res dto
	 */
	PacketGeneratorResDto uploadUinPacket(String registrationId, String creationTime, String regType,byte[] packetZipBytes)
			throws RegBaseCheckedException;

}