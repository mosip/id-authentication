package io.mosip.registration.processor.stages.utils;

import java.io.InputStream;
import java.util.List;

import io.mosip.registration.processor.core.packet.dto.BiometricSequence;
import io.mosip.registration.processor.core.packet.dto.DemographicSequence;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

/**
 * The Class FilesValidation.
 */
public class FilesValidation {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant DEMOGRAPHIC_APPLICANT. */
	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	/** The Constant BIOMETRIC_APPLICANT. */
	public static final String BIOMETRIC_APPLICANT = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	/** The Constant BIOMETRIC_INTRODUCER. */
	public static final String BIOMETRIC_INTRODUCER = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR
			+ PacketFiles.INTRODUCER.name() + FILE_SEPARATOR;

	/** The adapter. */
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/**
	 * Instantiates a new files validation.
	 *
	 * @param adapter the adapter
	 */
	public FilesValidation(FileSystemAdapter<InputStream, Boolean> adapter) {

		this.adapter = adapter;
	}

	/**
	 * Files validation.
	 *
	 * @param registrationId the registration id
	 * @param packetInfo the packet info
	 * @return true, if successful
	 */
	public boolean filesValidation(String registrationId, PacketInfo packetInfo) {
		boolean filesValidated = false;

		HashSequence hashSequence = packetInfo.getHashSequence();
		filesValidated = validateHashSequence(registrationId, hashSequence);

		return filesValidated;

	}

	/**
	 * Validate hash sequence.
	 *
	 * @param registrationId the registration id
	 * @param hashSequence the hash sequence
	 * @return true, if successful
	 */
	private boolean validateHashSequence(String registrationId, HashSequence hashSequence) {
		boolean isHashSequenceValidated = false;

		if (validateBiometricSequence(registrationId, hashSequence.getBiometricSequence())
				&& validateDemographicSequence(registrationId, hashSequence.getDemographicSequence())) {
			isHashSequenceValidated = true;
		}

		return isHashSequenceValidated;
	}

	/**
	 * Validate demographic sequence.
	 *
	 * @param registrationId the registration id
	 * @param demographicSequence the demographic sequence
	 * @return true, if successful
	 */
	private boolean validateDemographicSequence(String registrationId, DemographicSequence demographicSequence) {
		boolean isDemographicSequenceValidated = false;
		for (String applicantFile : demographicSequence.getApplicant()) {
			String fileName = "";
			if (PacketFiles.APPLICANTPHOTO.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.APPLICANTPHOTO.name();
			} else if (PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name();
			} else if (PacketFiles.DEMOGRAPHICINFO.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name();
			} else if (PacketFiles.PROOFOFADDRESS.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFADDRESS.name();
			} else if (PacketFiles.EXCEPTIONPHOTO.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.EXCEPTIONPHOTO.name();
			} else if (PacketFiles.PROOFOFIDENTITY.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFIDENTITY.name();
			}

			isDemographicSequenceValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isDemographicSequenceValidated) {
				break;
			}
		}

		return isDemographicSequenceValidated;
	}

	/**
	 * Validate biometric sequence.
	 *
	 * @param registrationId the registration id
	 * @param biometricSequence the biometric sequence
	 * @return true, if successful
	 */
	private boolean validateBiometricSequence(String registrationId, BiometricSequence biometricSequence) {

		boolean isBiometricSequenceValidated = false;

		if (validateBiometricApplicant(registrationId, biometricSequence.getApplicant())
				&& validateBiometricIntroducer(registrationId, biometricSequence.getIntroducer())) {
			isBiometricSequenceValidated = true;
		}

		return isBiometricSequenceValidated;
	}

	/**
	 * Validate biometric introducer.
	 *
	 * @param registrationId the registration id
	 * @param introducer the introducer
	 * @return true, if successful
	 */
	private boolean validateBiometricIntroducer(String registrationId, List<String> introducer) {
		boolean isIntroducerValidated = false;

		for (String applicantFile : introducer) {
			String fileName = "";
			if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_INTRODUCER + PacketFiles.LEFTEYE.name();
			} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_INTRODUCER + PacketFiles.RIGHTEYE.name();
			} else if (PacketFiles.LEFTTHUMB.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_INTRODUCER + PacketFiles.LEFTTHUMB.name();
			} else if (PacketFiles.RIGHTTHUMB.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_INTRODUCER + PacketFiles.RIGHTTHUMB.name();
			}
			isIntroducerValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isIntroducerValidated) {
				break;
			}
		}
		return isIntroducerValidated;
	}

	/**
	 * Validate biometric applicant.
	 *
	 * @param registrationId the registration id
	 * @param applicant the applicant
	 * @return true, if successful
	 */
	private boolean validateBiometricApplicant(String registrationId, List<String> applicant) {
		boolean isApplicantValidated = false;

		for (String applicantFile : applicant) {
			String fileName = "";
			if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_APPLICANT + PacketFiles.LEFTEYE.name();
			} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_APPLICANT + PacketFiles.RIGHTEYE.name();
			} else if (PacketFiles.LEFTPALM.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_APPLICANT + PacketFiles.LEFTPALM.name();
			} else if (PacketFiles.RIGHTPALM.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_APPLICANT + PacketFiles.RIGHTPALM.name();
			} else if (PacketFiles.BOTHTHUMBS.name().equalsIgnoreCase(applicantFile)) {
				fileName = BIOMETRIC_APPLICANT + PacketFiles.BOTHTHUMBS.name();
			}
			isApplicantValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isApplicantValidated) {
				break;
			}
		}
		return isApplicantValidated;
	}

}
