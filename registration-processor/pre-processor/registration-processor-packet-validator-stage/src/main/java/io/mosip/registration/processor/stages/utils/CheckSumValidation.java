package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;

import org.apache.commons.io.IOUtils;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class CheckSumValidation.
 *
 * @author M1048358 Alok Ranjan
 */

public class CheckSumValidation {

	/** The adapter. */
	private PacketManager adapter;

	/** The registration status dto. */
	private InternalRegistrationStatusDto registrationStatusDto;
	
	
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(CheckSumValidation.class);

	/**
	 * Instantiates a new check sum validation.
	 *
	 * @param adapter
	 *            the adapter
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	public CheckSumValidation(PacketManager adapter, InternalRegistrationStatusDto registrationStatusDto) {
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
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws ApisResourceAccessException 
	 * @throws PacketDecryptionFailureException 
	 */
	public boolean checksumvalidation(String registrationId, Identity identity) throws IOException, PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		List<FieldValueArray> hashSequence = identity.getHashSequence1();
		List<FieldValueArray> hashSequence2 = identity.getHashSequence2();
        regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "CheckSumValidation::checksumvalidation::entry");
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

		Boolean isDataCheckSumEqual = MessageDigest.isEqual(generatedHash, packetDataHashByte);
		Boolean isOsiCheckSumEqual = MessageDigest.isEqual(packetOsiHash, packetOsiHashByte);

		if ((!isDataCheckSumEqual) || (!isOsiCheckSumEqual)) {
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_CHECKSUM_VALIDATION_FAILURE);
		}

		if (isDataCheckSumEqual && isOsiCheckSumEqual) {
			isValid = true;
		}
	    regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "CheckSumValidation::checksumvalidation::exit");
		return isValid;
	}

}
