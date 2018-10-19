package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

public class CheckSumValidation {
	
	public static final String HMAC_FILE = "HMACFILE";
	
	private  FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter;
	
	public CheckSumValidation(FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter) {
		this.adapter = adapter;
		
	}
	
	public boolean checksumvalidation(String registrationId, PacketInfo packetInfo) throws IOException {
		HashSequence hashSequence = packetInfo.getHashSequence();

		// Getting checksum from HMAC File
		InputStream hmacFileStream = adapter.getFile(registrationId, HMAC_FILE);
		byte[] hmacFileHashByte = IOUtils.toByteArray(hmacFileStream);

		// Generating checksum using hashSequence
		CheckSumGeneration checkSumGeneration = new CheckSumGeneration(adapter);
		byte[] generatedHash = checkSumGeneration.generatePacketInfoHash(hashSequence, registrationId);

		return Arrays.equals(generatedHash, hmacFileHashByte);
	}

}
