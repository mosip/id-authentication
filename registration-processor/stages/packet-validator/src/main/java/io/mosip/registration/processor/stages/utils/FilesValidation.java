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
	
	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.APPLICANT.name()
	+ File.separator;
	
	public static final String BIOMETRIC_APPLICANT = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.APPLICANT.name() + File.separator;
	
	public static final String BIOMETRIC_INTRODUCER = PacketFiles.BIOMETRIC.name() + File.separator + PacketFiles.INTRODUCER.name()
	+ File.separator;
	
	
	private  FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter;
	
	
	
	public FilesValidation(FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter) {
		
		this.adapter = adapter;
	}

	public  boolean filesValidation(String registrationId, PacketInfo packetInfo) {
		boolean filesValidated = false;

		HashSequence hashSequence = packetInfo.getHashSequence();
		filesValidated = validateHashSequence(registrationId, hashSequence);

		return filesValidated;

	}

	private  boolean validateHashSequence(String registrationId, HashSequence hashSequence) {
		boolean isHashSequenceValidated = false;

		if (validateBiometricSequence(registrationId, hashSequence.getBiometricSequence())
				&& validateDemographicSequence(registrationId, hashSequence.getDemographicSequence())) {
			isHashSequenceValidated = true;
		}

		return isHashSequenceValidated;
	}

	private  boolean validateDemographicSequence(String registrationId, DemographicSequence demographicSequence) {
		boolean isDemographicSequenceValidated = false;
		for (String applicantFile : demographicSequence.getApplicant()) {
			String fileName = "";  
			if (PacketFiles.APPLICANTPHOTO.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.APPLICANTPHOTO.name();
			} else if (PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name();
			} else if (PacketFiles.DEMOGRAPHICINFO.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.DEMOGRAPHICINFO.name();
			} else if (PacketFiles.PROOFOFADDRESS.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFADDRESS.name();
			} else if (PacketFiles.EXCEPTIONPHOTO.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.EXCEPTIONPHOTO.name();
			}else if (PacketFiles.PROOFOFIDENTITY.name().equalsIgnoreCase(applicantFile)) {
				fileName = DEMOGRAPHIC_APPLICANT +PacketFiles.PROOFOFIDENTITY.name();
	         } 
			
			isDemographicSequenceValidated = adapter.checkFileExistence(registrationId, fileName);

			if (!isDemographicSequenceValidated) {
				break;
			}
		}

		return isDemographicSequenceValidated;
	}

	private  boolean validateBiometricSequence(String registrationId, BiometricSequence biometricSequence) {

		boolean isBiometricSequenceValidated = false;

		if (validateBiometricApplicant(registrationId, biometricSequence.getApplicant())
				&& validateBiometricIntroducer(registrationId, biometricSequence.getIntroducer())) {
			isBiometricSequenceValidated = true;
		}

		return isBiometricSequenceValidated;
	}

	private  boolean validateBiometricIntroducer(String registrationId, List<String> introducer) {
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

	private  boolean validateBiometricApplicant(String registrationId, List<String> applicant) {
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
