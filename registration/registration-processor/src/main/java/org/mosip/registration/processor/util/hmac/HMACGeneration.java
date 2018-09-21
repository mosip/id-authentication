package org.mosip.registration.processor.util.hmac;

import java.util.LinkedList;
import java.util.List;

import org.mosip.kernel.core.utils.hmac.HMACUtil;
import org.mosip.registration.processor.dto.PacketDTO;
import org.mosip.registration.processor.dto.biometric.BiometricDTO;
import org.mosip.registration.processor.dto.biometric.BiometricInfoDTO;
import org.mosip.registration.processor.dto.demographic.ApplicantDocumentDTO;
import org.mosip.registration.processor.dto.demographic.DemographicDTO;
import org.mosip.registration.processor.dto.demographic.DocumentDetailsDTO;

/**
 * Hash generation for packet DTO
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */
public class HMACGeneration {

	public static LinkedList<String> hmacApplicantSequence = new LinkedList<>();
	public static LinkedList<String> hmacHOFSequence = new LinkedList<>();
	public static LinkedList<String> hmacIntroducerSequence = new LinkedList<>();

	/**
	 * * Generates hash for packet Dto and Demographic json file which includes
	 * biometric, demographic and enrollmentId
	 * 
	 * @param packetDTO
	 *            has to be hash updation
	 * @param demographicJsonBytes
	 *            has to be hash updation
	 * @param hmacApplicantSequence
	 *            contains the applicant HMAC Sequence
	 * @param hmacHOFSequence
	 *            contains the hof HMAC Sequence
	 * @param hmacIntroducerSequence
	 *            contains the introducer HMAC Sequence
	 * 
	 * @return hash byte array
	 */
	public static byte[] generatePacketDtoHash(final PacketDTO packetDTO, final byte[] demographicJsonBytes,
			LinkedList<String> hmacApplicantSequence, LinkedList<String> hmacHOFSequence,
			LinkedList<String> hmacIntroducerSequence) {

		HMACGeneration.hmacApplicantSequence = hmacApplicantSequence;
		HMACGeneration.hmacHOFSequence = hmacHOFSequence;
		HMACGeneration.hmacIntroducerSequence = hmacIntroducerSequence;

		// generates packet biometric hash which may includes applicant, hof and
		// introducer
		if (packetDTO.getBiometricDTO() != null) {
			generatesPacketBiometricsHash(packetDTO.getBiometricDTO());
		}

		// Demographic json hash
		generateHash(demographicJsonBytes, "DemographicJson", hmacApplicantSequence);

		// generates demographic hash
		if (packetDTO.getDemographicDTO() != null) {
			generateDemographicHash(packetDTO.getDemographicDTO());
		}

		// generates enrollment id hash
		if (packetDTO.getEnrollmentID() != null) {
			generateHash(packetDTO.getEnrollmentID().getBytes(), "applicantEnrollmentId", hmacApplicantSequence);
		}

		// generated hash
		return HMACUtil.digestAsPlainText(HMACUtil.updatedHash()).getBytes();
	}

	/**
	 * generates hash which may includes applicant, hof and introducer
	 * 
	 * @param biometricDTO
	 *            has to be hash updation
	 */
	private static void generatesPacketBiometricsHash(final BiometricDTO biometricDTO) {
		// hash for applicant
		if (biometricDTO.getApplicantBiometricDTO() != null) {
			generateBiometricInfoHash(biometricDTO.getApplicantBiometricDTO(), hmacApplicantSequence);
		}
		// hash for hof
		if (biometricDTO.getHofBiometricDTO() != null) {
			generateBiometricInfoHash(biometricDTO.getHofBiometricDTO(), hmacHOFSequence);
		}
		// hash for introducer
		if (biometricDTO.getIntroducerBiometricDTO() != null) {
			generateBiometricInfoHash(biometricDTO.getIntroducerBiometricDTO(), hmacIntroducerSequence);
		}

	}

	/**
	 * Generates hash for applicant biometric hash which includes fingerprints and
	 * iris
	 * 
	 * @param biometricInfoDTO
	 *            has to be hash updation
	 * @param hashOrder
	 *            has to be updated in specified hmacSequenceList
	 *
	 */
	private static void generateBiometricInfoHash(final BiometricInfoDTO biometricInfoDTO,
			LinkedList<String> hashOrder) {
		// hash for fingerprints
		if (biometricInfoDTO.getFingerprintDetailsDTO() != null) {
			biometricInfoDTO.getFingerprintDetailsDTO().forEach((fingerprintDetailsDTO) -> {
				if (fingerprintDetailsDTO != null)
					generateHash(fingerprintDetailsDTO.getFingerPrint(), fingerprintDetailsDTO.getFingerPrintName(),
							hashOrder);
			});
		}
		// hash for iris
		if (biometricInfoDTO.getIrisDetailsDTO() != null) {
			biometricInfoDTO.getIrisDetailsDTO().forEach((irisDetailsDTO) -> {
				if (irisDetailsDTO != null)
					generateHash(irisDetailsDTO.getIris(), irisDetailsDTO.getIrisName(), hashOrder);
			});
		}

	}

	/**
	 * Generates hash for demographic documents which may includes ProofOfIdentity,
	 * ProofOfResidenty, ProofOfAddress ApplicantPhoto, ExceptionPhoto and
	 * demographic json file
	 * 
	 * @param demographicDTO
	 *            has to be hash updation
	 */
	private static void generateDemographicHash(final DemographicDTO demographicDTO) {
		// generates applicant document hash
		if (demographicDTO.getApplicantDocumentDTO() != null) {
			generateApplicantDocumentHash(demographicDTO.getApplicantDocumentDTO(), hmacApplicantSequence);
		}
		// generates hofUIN hash
		if (demographicDTO.getHofUIN() != null) {
			generateHash(demographicDTO.getHofUIN().getBytes(), "hofUIN", hmacHOFSequence);
		}
		// generates introducerUIN hash
		if (demographicDTO.getIntroducerUIN() != null) {
			generateHash(demographicDTO.getIntroducerUIN().getBytes(), "introducerUIN", hmacIntroducerSequence);
		}

	}

	/**
	 * generates hash for applicant documents, photo and exception photo (In
	 * exceptional cases)
	 * 
	 * @param applicantDocument
	 *            has to be hash updation
	 * @param hashOrder
	 *            has to be updated in specified hmacSequenceList
	 *
	 */
	private static void generateApplicantDocumentHash(final ApplicantDocumentDTO applicantDocument,
			LinkedList<String> hashOrder) {
		List<DocumentDetailsDTO> documentDetailsDTOList = applicantDocument.getDocumentDetailsDTO();
		byte[] applicantPhotoBytes = applicantDocument.getPhoto();
		byte[] applicantExceptionPhotoBytes = applicantDocument.getExceptionPhoto();

		// for documents hash
		if (documentDetailsDTOList != null) {
			documentDetailsDTOList.forEach((document) -> {
				if (document != null)
					generateHash(document.getDocument(), document.getDocumentName(), hashOrder);
			});
		}

		// hash for applicant photo
		if (applicantExceptionPhotoBytes != null) {
			generateHash(applicantPhotoBytes, "applicantPhoto", hashOrder);
		}
		// hash for exception Photo
		if (applicantExceptionPhotoBytes != null) {
			generateHash(applicantExceptionPhotoBytes, "applicantExceptionPhoto", hashOrder);
		}

	}

	/**
	 * Generates hash for byte Array and store its type to hash sequence
	 * 
	 * @param byteArray
	 *            has to be hash updation
	 * @param filename
	 *            to add it in hash sequence
	 * @param hashOrder
	 *            has to be updated in specified hmacSequenceList
	 *
	 */
	private static void generateHash(final byte[] byteArray, final String filename, LinkedList<String> hashOrder) {
		// Hash updation
		if (byteArray != null) {
			HMACUtil.update(byteArray);
			hashOrder.add(filename);
		}

	}
}
