package io.mosip.registration.processor.packet.service.util.hmac;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.json.metadata.HashSequence;

/**
 * Hash generation for packet DTO
 * 
 * @author Sowmya
 * @since 1.0.0
 */
public class HMACGeneration {

	private HMACGeneration() {

	}

	/**
	 * Generates hash for registrationDTO and Demographic JSON file which includes
	 * biometric and demographic
	 * 
	 * @param registrationDTO
	 *            has to be hash updation
	 * @param demographicJsonBytes
	 *            has to be hash updation
	 * @param hashSequence
	 *            contains the hash Sequence
	 * 
	 * @return hash byte array
	 */
	public static byte[] generatePacketDTOHash(final RegistrationDTO registrationDTO,
			final Map<String, byte[]> filesGeneratedForPacket, HashSequence sequence) {

		// Demographic json hash
		generateHash(filesGeneratedForPacket.get(RegistrationConstants.DEMOGRPAHIC_JSON_NAME),
				RegistrationConstants.DEMOGRPAHIC_JSON_NAME, sequence.getDemographicSequence().getApplicant());

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
	}

	private static void generateHash(final byte[] byteArray, final String filename, List<String> hashOrder) {
		// Hash updation
		if (byteArray != null) {
			HMACUtils.update(byteArray);
			if (hashOrder != null) {
				if (filename.contains(".")) {
					hashOrder.add(filename.substring(0, filename.lastIndexOf('.')));
				} else {
					hashOrder.add(filename);
				}
			}
		}
	}

	/**
	 * Generates the HMAC for the files that are part of Packet OSI Data
	 * 
	 * @param generatedFilesForPacket
	 *            contains the files that has to be hashed
	 * @param osiDataHashSequence
	 *            stores the file hashing order
	 * @return the HMAC data as {@link String}
	 */
	public static byte[] generatePacketOSIHash(final Map<String, byte[]> generatedFilesForPacket,
			List<String> osiDataHashSequence) {
		// Generate Hash for Officer CBEFF file
		generateHash(generatedFilesForPacket.get(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME),
				RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME, osiDataHashSequence);

		// Generate Hash for Officer CBEFF file
		generateHash(generatedFilesForPacket.get(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME),
				RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME, osiDataHashSequence);

		// Generate Hash for Audit.json
		generateHash(generatedFilesForPacket.get(RegistrationConstants.AUDIT_JSON_FILE),
				RegistrationConstants.AUDIT_JSON_FILE, osiDataHashSequence);

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
	}

}
