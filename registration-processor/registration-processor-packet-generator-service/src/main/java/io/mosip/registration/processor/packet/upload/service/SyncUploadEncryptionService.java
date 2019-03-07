package io.mosip.registration.processor.packet.upload.service;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public interface SyncUploadEncryptionService {

	String uploadUinPacket(File uinZipFile);

	
}