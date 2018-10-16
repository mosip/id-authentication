package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

public class CheckSumValidation {
	
	private CheckSumValidation() {
		
	}
	
	private static FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter = new FilesystemCephAdapterImpl();
	
	public static final String HMAC_FILE = "HMACFile";
	
	public static boolean checksumvalidation(String registrationId, PacketInfo packetInfo) throws IOException {
		HashSequence hashSequence = packetInfo.getHashSequence();

		// Getting checksum from HMAC File
		InputStream HMACFileStream = adapter.getFile(registrationId, HMAC_FILE);
		byte[] HMACFileHashByte = IOUtils.toByteArray(HMACFileStream);

		// Generating checksum using hashSequence
		byte[] generatedHash = CheckSumGeneration.generatePacketInfoHash(hashSequence, registrationId);

		return Arrays.equals(generatedHash, HMACFileHashByte);
	}

}
