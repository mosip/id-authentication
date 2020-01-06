package io.mosip.registration.util.hmac;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.json.metadata.DemographicSequence;
import io.mosip.registration.dto.json.metadata.HashSequence;

/**
 * Hash generation for packet DTO
 * 
 * Hash-based Message Authentication Code is a specific type of message
 * authentication code (MAC) involving a cryptographic hash function and a
 * secret cryptographic key.
 * 
 * The cryptographic strength of the HMAC depends upon the cryptographic
 * strength of the underlying hash function, the size of its hash output, and
 * the size and quality of the key
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
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
	 * @param filesGeneratedForPacket
	 *            map containing the files to be hashed
	 * @param sequence
	 *            contains the hash Sequence
	 * 
	 * @return hash byte array of HMAC
	 */
	public static byte[] generatePacketDTOHash(final RegistrationDTO registrationDTO, final Map<String, byte[]> filesGeneratedForPacket,
			HashSequence sequence) {
		// generates packet biometric hash which may include applicant and introducer
		if (registrationDTO.getBiometricDTO() != null) {
			generateHash(filesGeneratedForPacket.get(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME),
					RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME,
					sequence.getBiometricSequence().getApplicant());
			generateHash(filesGeneratedForPacket.get(RegistrationConstants.AUTHENTICATION_BIO_CBEFF_FILE_NAME),
					RegistrationConstants.AUTHENTICATION_BIO_CBEFF_FILE_NAME,
					sequence.getBiometricSequence().getIntroducer());
		}

		// Demographic json hash
		generateHash(filesGeneratedForPacket.get(RegistrationConstants.DEMOGRPAHIC_JSON_NAME),
				RegistrationConstants.DEMOGRPAHIC_JSON_NAME, sequence.getDemographicSequence().getApplicant());

		// generates demographic hash
		if (registrationDTO.getDemographicDTO() != null) {
			generateDemographicHash(registrationDTO.getDemographicDTO(), sequence.getDemographicSequence());
		}

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
	}

	private static void generateDemographicHash(final DemographicDTO demographicDTO,
			final DemographicSequence demographicSequence) {
		List<String> hashOrder = demographicSequence.getApplicant();

		// generates applicant document hash
		for (Entry<String, DocumentDetailsDTO> documentCategory : demographicDTO.getApplicantDocumentDTO()
				.getDocuments().entrySet()) {
			generateHash(documentCategory.getValue().getDocument(), documentCategory.getValue().getValue(), hashOrder);
		}

		// Hash for Acknowledgement Receipt
		byte[] registrationAck = demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceipt();
		if (registrationAck != null) {
			generateHash(registrationAck, demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceiptName(),
					hashOrder);
		}
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
	 * Generates the HMAC for the files that are part of Packet OSI Data. Which
	 * consists of Officer Bio, Supervisor Bio and AuditJson File
	 * 
	 * This also generates hash for Packet introducer exception photo
	 * 
	 * @param generatedFilesForPacket
	 *            contains the files that has to be hashed
	 * @param osiDataHashSequence
	 *            stores the file hashing order
	 * @return the HMAC data as byte array
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

		// Generate Hash for PARENT exception photo
		generateHash(generatedFilesForPacket.get(RegistrationConstants.PARENT.concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO_NAME)),
				RegistrationConstants.PARENT.toLowerCase().concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO_NAME), osiDataHashSequence);

		// Generate Hash for INDIVIDUAL exception photo
				generateHash(generatedFilesForPacket.get(RegistrationConstants.INDIVIDUAL.concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO_NAME)),
						RegistrationConstants.INDIVIDUAL.toLowerCase().concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO_NAME), osiDataHashSequence);

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
	}

}
