package io.mosip.registration.util.hmac;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	 * @param demographicJsonBytes
	 *            has to be hash updation
	 * @param hashSequence
	 *            contains the hash Sequence
	 * 
	 * @return hash byte array
	 */
	public static byte[] generatePacketDTOHash(final RegistrationDTO registrationDTO, final Map<String, byte[]> filesGeneratedForPacket,
			HashSequence sequence) {
		// generates packet biometric hash which may includes applicant and introducer
		if (registrationDTO.getBiometricDTO() != null) {
			generateHash(filesGeneratedForPacket.get(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME),
					RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME,
					sequence.getBiometricSequence().getApplicant());
			generateHash(filesGeneratedForPacket.get(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME),
					RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME,
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
		// generates applicant document hash
		generateApplicantDocumentHash(demographicDTO, demographicSequence.getApplicant());
	}

	private static void generateApplicantDocumentHash(final DemographicDTO demographicDTO,
			List<String> hashOrder) {
		byte[] applicantPhotoBytes = demographicDTO.getApplicantDocumentDTO().getPhoto();
		byte[] applicantExceptionPhotoBytes = demographicDTO.getApplicantDocumentDTO().getExceptionPhoto();
		byte[] registrationAck = demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceipt();

		DocumentDetailsDTO documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity()
				.getProofOfIdentity();

		if (documentDetailsDTO != null) {
			generateHash(documentDetailsDTO.getDocument(), documentDetailsDTO.getValue(), hashOrder);
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfAddress();

		if (documentDetailsDTO != null) {
			generateHash(documentDetailsDTO.getDocument(), documentDetailsDTO.getValue(), hashOrder);
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfRelationship();

		if (documentDetailsDTO != null) {
			generateHash(documentDetailsDTO.getDocument(), documentDetailsDTO.getValue(), hashOrder);
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfDateOfBirth();

		if (documentDetailsDTO != null) {
			generateHash(documentDetailsDTO.getDocument(), documentDetailsDTO.getValue(), hashOrder);
		}

		// hash for applicant photo
		if (applicantPhotoBytes != null) {
			generateHash(applicantPhotoBytes, demographicDTO.getApplicantDocumentDTO().getPhotographName(), hashOrder);
		}
		// hash for exception Photo
		if (applicantExceptionPhotoBytes != null) {
			generateHash(applicantExceptionPhotoBytes, demographicDTO.getApplicantDocumentDTO().getExceptionPhotoName(),
					hashOrder);
		}

		// Hash for Acknowledgement Receipt
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

	public static byte[] generatePacketOSIHash(final Map<String, byte[]> generatedFilesForPacket) {
		List<String> packetOSIHashingOrder = new LinkedList<>();

		// Generate Hash for Officer CBEFF file
		generateHash(generatedFilesForPacket.get(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME),
				RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME, packetOSIHashingOrder);

		// Generate Hash for Officer CBEFF file
		generateHash(generatedFilesForPacket.get(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME),
				RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME, packetOSIHashingOrder);

		// Generate Hash for Audit.json
		generateHash(generatedFilesForPacket.get(RegistrationConstants.AUDIT_JSON_FILE),
				RegistrationConstants.AUDIT_JSON_FILE, packetOSIHashingOrder);

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
	}

}
