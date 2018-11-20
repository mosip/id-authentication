package io.mosip.registration.util.hmac;

import java.util.List;

import io.mosip.kernel.core.util.HMACUtils;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.json.metadata.BiometricSequence;
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
	public static byte[] generatePacketDTOHash(final RegistrationDTO registrationDTO, final byte[] demographicJsonBytes,
			HashSequence sequence) {

		// Sequence
		DemographicSequence demographicSequence = sequence.getDemographicSequence();
		BiometricSequence biometricSequence = sequence.getBiometricSequence();

		// generates packet biometric hash which may includes applicant, hof and
		// introducer
		if (registrationDTO.getBiometricDTO() != null) {
			generatesPacketBiometricsHash(registrationDTO.getBiometricDTO(), biometricSequence);
		}

		// Demographic json hash
		generateHash(demographicJsonBytes, "DemographicInfo", demographicSequence.getApplicant());

		// generates demographic hash
		if (registrationDTO.getDemographicDTO() != null) {
			generateDemographicHash(registrationDTO.getDemographicDTO(), demographicSequence);
		}

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
	}

	private static void generatesPacketBiometricsHash(final BiometricDTO biometricDTO,
			final BiometricSequence biometricSequence) {
		// hash for applicant
		if (biometricDTO.getApplicantBiometricDTO() != null) {
			generateBiometricInfoHash(biometricDTO.getApplicantBiometricDTO(), biometricSequence.getApplicant());
		}

		// hash for introducer
		if (biometricDTO.getIntroducerBiometricDTO() != null) {
			generateBiometricInfoHash(biometricDTO.getIntroducerBiometricDTO(), biometricSequence.getIntroducer());
		}

	}

	private static void generateBiometricInfoHash(final BiometricInfoDTO biometricInfoDTO, List<String> hashOrder) {
		// hash for fingerprints
		if (biometricInfoDTO.getFingerprintDetailsDTO() != null) {
			biometricInfoDTO.getFingerprintDetailsDTO().forEach(fingerprintDetailsDTO -> {
				if (fingerprintDetailsDTO != null)
					generateHash(fingerprintDetailsDTO.getFingerPrint(),
							fingerprintDetailsDTO.getFingerprintImageName(), hashOrder);
			});
		}
		// hash for iris
		if (biometricInfoDTO.getIrisDetailsDTO() != null) {
			biometricInfoDTO.getIrisDetailsDTO().forEach(irisDetailsDTO -> {
				if (irisDetailsDTO != null)
					generateHash(irisDetailsDTO.getIris(), irisDetailsDTO.getIrisImageName(), hashOrder);
			});
		}

	}

	private static void generateDemographicHash(final DemographicDTO demographicDTO,
			final DemographicSequence demographicSequence) {
		// generates applicant document hash
		if (demographicDTO.getApplicantDocumentDTO() != null) {
			generateApplicantDocumentHash(demographicDTO.getApplicantDocumentDTO(), demographicSequence.getApplicant());
		}
	}

	private static void generateApplicantDocumentHash(final ApplicantDocumentDTO applicantDocument,
			List<String> hashOrder) {
		List<DocumentDetailsDTO> documentDetailsDTOList = applicantDocument.getDocumentDetailsDTO();
		byte[] applicantPhotoBytes = applicantDocument.getPhoto();
		byte[] applicantExceptionPhotoBytes = applicantDocument.getExceptionPhoto();
		byte[] registrationAck = applicantDocument.getAcknowledgeReceipt();

		// for documents hash
		if (documentDetailsDTOList != null) {
			documentDetailsDTOList.forEach(document -> {
				if (document != null)
					generateHash(document.getDocument(), document.getDocumentName(), hashOrder);
			});
		}

		// hash for applicant photo
		if (applicantPhotoBytes != null) {
			generateHash(applicantPhotoBytes, applicantDocument.getPhotographName(), hashOrder);
		}
		// hash for exception Photo
		if (applicantExceptionPhotoBytes != null) {
			generateHash(applicantExceptionPhotoBytes, applicantDocument.getExceptionPhotoName(), hashOrder);
		}

		// Hash for Acknowledgement Receipt
		if (registrationAck != null) {
			generateHash(registrationAck, applicantDocument.getAcknowledgeReceiptName(), hashOrder);
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
}
