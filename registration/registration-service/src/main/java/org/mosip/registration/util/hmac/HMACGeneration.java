package org.mosip.registration.util.hmac;

import java.util.LinkedList;
import java.util.List;

import org.mosip.kernel.core.utils.HMACUtil;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.dto.biometric.BiometricDTO;
import org.mosip.registration.dto.biometric.BiometricInfoDTO;
import org.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import org.mosip.registration.dto.demographic.DemographicDTO;
import org.mosip.registration.dto.demographic.DocumentDetailsDTO;
import org.mosip.registration.dto.json.metadata.HashSequence;

/**
 * Hash generation for packet DTO
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */
public class HMACGeneration {
	
	private HMACGeneration() {
		
	}

	private static LinkedList<String> hmacApplicantSequence;
	private static LinkedList<String> hmacHOFSequence;
	private static LinkedList<String> hmacIntroducerSequence;

	/**
	 * * Generates hash for registration Dto and Demographic json file which includes
	 * biometric, demographic and registration Id
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
			HashSequence hashSequence) {

		hmacApplicantSequence = hashSequence.getApplicant();
		hmacHOFSequence = hashSequence.getHof();
		hmacIntroducerSequence = hashSequence.getIntroducer();

		// generates packet biometric hash which may includes applicant, hof and
		// introducer
		if (registrationDTO.getBiometricDTO() != null) {
			generatesPacketBiometricsHash(registrationDTO.getBiometricDTO());
		}

		// Demographic json hash
		generateHash(demographicJsonBytes, "DemographicJson", hmacApplicantSequence);

		// generates demographic hash
		if (registrationDTO.getDemographicDTO() != null) {
			generateDemographicHash(registrationDTO.getDemographicDTO());
		}

		// generates enrollment id hash
		if (registrationDTO.getRegistrationId() != null) {
			generateHash(registrationDTO.getRegistrationId().getBytes(), "applicantRegistrationId", hmacApplicantSequence);
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
		// hash for finger prints
		if (biometricInfoDTO.getFingerprintDetailsDTO() != null) {
			biometricInfoDTO.getFingerprintDetailsDTO().forEach(fingerprintDetailsDTO -> {
				if (fingerprintDetailsDTO != null)
					generateHash(fingerprintDetailsDTO.getFingerPrint(), fingerprintDetailsDTO.getFingerPrintName(),
							hashOrder);
			});
		}
		// hash for iris
		if (biometricInfoDTO.getIrisDetailsDTO() != null) {
			biometricInfoDTO.getIrisDetailsDTO().forEach(irisDetailsDTO -> {
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
		if (demographicDTO.getHOFUIN() != null) {
			generateHash(demographicDTO.getHOFUIN().getBytes(), "hofUIN", hmacHOFSequence);
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
			documentDetailsDTOList.forEach(document -> {
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
