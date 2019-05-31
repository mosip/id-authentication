package io.mosip.registration.service.bio;

import java.io.IOException;
import java.util.List;

import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.exception.RegBaseCheckedException;

public interface BioService {

	public boolean validateFingerPrint(String userId) throws RegBaseCheckedException, IOException;

	public boolean validateIris(String userId) throws RegBaseCheckedException, IOException;

	public boolean validateFace(String userId) throws RegBaseCheckedException;

	public void getFingerPrintImageAsDTO(FingerprintDetailsDTO fpDetailsDTO, String fingerType)
			throws RegBaseCheckedException;

	public void getIrisImageAsDTO(IrisDetailsDTO irisDetailsDTO, String irisType) throws RegBaseCheckedException;

	public void segmentFingerPrintImage(FingerprintDetailsDTO fingerprintDetailsDTO, String[] filePath,
			String fingerType) throws RegBaseCheckedException;

	public boolean validateFP(FingerprintDetailsDTO fingerprintDetailsDTO, List<UserBiometric> userFingerprintDetails);

	public boolean validateIrisAgainstDb(IrisDetailsDTO irisDetailsDTO, List<UserBiometric> userIrisDetails);

	public boolean validateFaceAgainstDb(FaceDetailsDTO faceDetail, List<UserBiometric> userFaceDetails);

	public boolean isMdmEnabled();

}
