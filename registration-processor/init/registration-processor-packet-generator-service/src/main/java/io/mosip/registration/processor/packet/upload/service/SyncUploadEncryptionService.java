package io.mosip.registration.processor.packet.upload.service;

import io.mosip.registration.processor.packet.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;

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