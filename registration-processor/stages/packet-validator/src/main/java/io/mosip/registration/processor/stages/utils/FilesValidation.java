package io.mosip.registration.processor.stages.utils;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import io.mosip.registration.processor.core.packet.dto.BiometricSequence;
import io.mosip.registration.processor.core.packet.dto.DemographicSequence;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

public class FilesValidation {
	
	private FilesValidation() {
		
	}

	private static FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter = new FilesystemCephAdapterImpl();
	
	public static boolean filesValidation(String registrationId, PacketInfo packetInfo) {
		boolean filesValidated = false;

		HashSequence hashSequence = packetInfo.getHashSequence();
		filesValidated = validateHashSequence(registrationId, hashSequence);

		return filesValidated;

	}

	private static boolean validateHashSequence(String registrationId, HashSequence hashSequence) {
		boolean isHashSequenceValidated = false;

		if (validateBiometricSequence(registrationId, hashSequence.getBiometricSequence())
				&& validateDemographicSequence(registrationId, hashSequence.getDemographicSequence())) {
			isHashSequenceValidated = true;
		}

		return isHashSequenceValidated;
	}

	private static boolean validateDemographicSequence(String registrationId, DemographicSequence demographicSequence) {
		boolean isDemographicSequenceValidated = false;
		for (String applicantFile : demographicSequence.getApplicant()) {
			String fileName = "";  
			if (PacketFiles.APPLICANTPHOTO.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
						+ File.separator + PacketFiles.APPLICANTPHOTO.name();
			} else if (PacketFiles.REGISTRATIONACKNOWLDEGEMENT.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
						+ File.separator + PacketFiles.REGISTRATIONACKNOWLDEGEMENT.name();
			} else if (PacketFiles.DEMOGRAPHICINFO.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.DEMOGRAPHICINFO.name();
			} else if (PacketFiles.PROOFOFADDRESS.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
						+ File.separator + PacketFiles.PROOFOFADDRESS.name();
			} else if (PacketFiles.EXCEPTIONPHOTO.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
						+ File.separator + PacketFiles.EXCEPTIONPHOTO.name();
			}else if (PacketFiles.PROOFOFIDENTITY.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
				+ File.separator + PacketFiles.PROOFOFIDENTITY.name();
	         } 
			
			isDemographicSequenceValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isDemographicSequenceValidated) {
				break;
			}
		}

		return isDemographicSequenceValidated;
	}

	private static boolean validateBiometricSequence(String registrationId, BiometricSequence biometricSequence) {

		boolean isBiometricSequenceValidated = false;

		if (validateBiometricApplicant(registrationId, biometricSequence.getApplicant())
				&& validateBiometricIntroducer(registrationId, biometricSequence.getIntroducer())) {
			isBiometricSequenceValidated = true;
		}

		return isBiometricSequenceValidated;
	}

	private static boolean validateBiometricIntroducer(String registrationId, List<String> introducer) {
		boolean isIntroducerValidated = false;

		for (String applicantFile : introducer) {
			String fileName = "";
			if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
						+ File.separator + PacketFiles.LEFTEYE.name();
			} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
						+ File.separator + PacketFiles.RIGHTEYE.name();
			} else if (PacketFiles.LEFTTHUMB.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
						+ File.separator + PacketFiles.LEFTTHUMB.name();
			} else if (PacketFiles.RIGHTTHUMB.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
						+ File.separator + PacketFiles.RIGHTTHUMB.name();
			}
			isIntroducerValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isIntroducerValidated) {
				break;
			}
		}
		return isIntroducerValidated;
	}

	private static boolean validateBiometricApplicant(String registrationId, List<String> applicant) {
		boolean isApplicantValidated = false;

		for (String applicantFile : applicant) {
			String fileName = "";
			if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.LEFTEYE.name();
			} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.RIGHTEYE.name();
			} else if (PacketFiles.LEFTPALM.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.LEFTPALM.name();
			} else if (PacketFiles.RIGHTPALM.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.RIGHTPALM.name();
			} else if (PacketFiles.BOTHTHUMBS.name().equalsIgnoreCase(applicantFile)) {
				fileName = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator
						+ PacketFiles.BOTHTHUMBS.name();
			}
			isApplicantValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isApplicantValidated) {
				break;
			}
		}
		return isApplicantValidated;
	}

}
