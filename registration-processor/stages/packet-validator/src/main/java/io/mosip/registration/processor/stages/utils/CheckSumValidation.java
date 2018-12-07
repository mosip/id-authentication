package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class CheckSumValidation.
 *
 * @author M1048358 Alok Ranjan
 */

public class CheckSumValidation {

	/** The Constant HMAC_FILE. */
	public static final String HMAC_FILE = "HMACFILE";

	/** The adapter. */
	private FileSystemAdapter<InputStream, Boolean> adapter;

	InternalRegistrationStatusDto registrationStatusDto;

	/**
	 * Instantiates a new check sum validation.
	 *
	 * @param adapter
	 *            the adapter
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
	 * @param packetInfo
	 *            the packet info
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean checksumvalidation(String registrationId, Identity identity) throws IOException {
		List<FieldValueArray> hashSequence = identity.getHashSequence();

		// Getting checksum from HMAC File
		InputStream hmacFileStream = adapter.getFile(registrationId, HMAC_FILE);
		byte[] hmacFileHashByte = IOUtils.toByteArray(hmacFileStream);

		// Generating checksum using hashSequence
		CheckSumGeneration checkSumGeneration = new CheckSumGeneration(adapter);
		byte[] generatedHash = checkSumGeneration.generateIdentityHash(hashSequence, registrationId);

		Boolean isChecksumValid = Arrays.equals(generatedHash, hmacFileHashByte);

		if (!isChecksumValid)
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_CHECKSUM_VALIDATION_FAILURE);

		return isChecksumValid;
	}

}
