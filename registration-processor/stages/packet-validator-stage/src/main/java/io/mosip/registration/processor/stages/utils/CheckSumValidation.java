package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class CheckSumValidation.
 *
 * @author M1048358 Alok Ranjan
 */

public class CheckSumValidation {

	/** The adapter. */
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The registration status dto. */
	private InternalRegistrationStatusDto registrationStatusDto;

	/**
	 * Instantiates a new check sum validation.
	 *
	 * @param adapter
	 *            the adapter
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	public CheckSumValidation(FileSystemAdapter<InputStream, Boolean> adapter,
			InternalRegistrationStatusDto registrationStatusDto) {
		this.registrationStatusDto = registrationStatusDto;
		this.adapter = adapter;

	}

	/**
	 * Checksumvalidation.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param identity
	 *            the identity
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean checksumvalidation(String registrationId, Identity identity) throws IOException {
		List<FieldValueArray> hashSequence = identity.getHashSequence();
		List<String> hashSequence2 = identity.getHashSequence2();

		Boolean isValid = false;

		// Getting hash bytes from packet
		InputStream packetDataHashStream = adapter.getFile(registrationId, PacketFiles.PACKET_DATA_HASH.name());
		InputStream packetOsiHashStream = adapter.getFile(registrationId, PacketFiles.PACKET_OSI_HASH.name());

		byte[] packetDataHashByte = IOUtils.toByteArray(packetDataHashStream);
		byte[] packetOsiHashByte = IOUtils.toByteArray(packetOsiHashStream);

		// Generating hash bytes using files
		CheckSumGeneration checkSumGeneration = new CheckSumGeneration(adapter);
		byte[] generatedHash = checkSumGeneration.generateIdentityHash(hashSequence, registrationId);
		byte[] packetOsiHash = checkSumGeneration.generatePacketOSIHash(hashSequence2, registrationId);

		Boolean isDataCheckSumEqual = Arrays.equals(generatedHash, packetDataHashByte);
		Boolean isOsiCheckSumEqual = Arrays.equals(packetOsiHash, packetOsiHashByte);

		if ((!isDataCheckSumEqual) || (!isOsiCheckSumEqual)) {
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_CHECKSUM_VALIDATION_FAILURE);
		}

		if (isDataCheckSumEqual && isOsiCheckSumEqual) {
			isValid = true;
		}

		return isValid;
	}

}
