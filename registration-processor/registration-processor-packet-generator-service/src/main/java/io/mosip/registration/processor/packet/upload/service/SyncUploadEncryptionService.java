package io.mosip.registration.processor.packet.upload.service;

import java.io.File;

import io.mosip.registration.processor.packet.service.dto.PackerGeneratorResDto;

public interface SyncUploadEncryptionService {

	PackerGeneratorResDto uploadUinPacket(File uinZipFile, String registrationId, String creationTime);

}