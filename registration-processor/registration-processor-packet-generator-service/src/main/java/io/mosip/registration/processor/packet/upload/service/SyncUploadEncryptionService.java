package io.mosip.registration.processor.packet.upload.service;

import java.io.File;

public interface SyncUploadEncryptionService {

	String uploadUinPacket(File uinZipFile);

}