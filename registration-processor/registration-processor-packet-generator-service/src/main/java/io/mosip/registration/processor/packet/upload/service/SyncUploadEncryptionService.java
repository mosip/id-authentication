package io.mosip.registration.processor.packet.upload.service;

import java.io.File;

import io.mosip.registration.processor.packet.service.dto.PackerGeneratorResDto;

/**
 * The Interface SyncUploadEncryptionService.
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
	PackerGeneratorResDto uploadUinPacket(File uinZipFile, String registrationId, String creationTime);

}