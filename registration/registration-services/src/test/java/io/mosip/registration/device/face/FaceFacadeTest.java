package io.mosip.registration.device.face;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserBiometricId;

public class FaceFacadeTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private FaceFacade faceFacade;

	@Test
	public void validateFaceTest() {
		byte[] testData = "face".getBytes();
		FaceDetailsDTO faceDetailsDTO = new FaceDetailsDTO(); 
		faceDetailsDTO.setFace(testData);

		List<UserBiometric> userBiometrics = new ArrayList<>();
		UserBiometric userBiometric1 = new UserBiometric();
		UserBiometricId userBiometricId = new UserBiometricId();
		userBiometricId.setUsrId("mosip");
		userBiometric1.setBioIsoImage(testData);
		userBiometric1.setUserBiometricId(userBiometricId);
		UserBiometric userBiometric2 = new UserBiometric();
		userBiometric2.setBioIsoImage(testData);
		userBiometric2.setUserBiometricId(userBiometricId);
		userBiometrics.add(userBiometric1);
		userBiometrics.add(userBiometric2);

		Boolean res = faceFacade.validateFace(faceDetailsDTO, userBiometrics);
		assertTrue(res);
	}

	@Test
	public void validateFaceFailureTest() {
		byte[] testData = "face".getBytes();
		FaceDetailsDTO faceDetailsDTO = new FaceDetailsDTO(); 
		faceDetailsDTO.setFace("face123".getBytes());

		List<UserBiometric> userBiometrics = new ArrayList<>();
		UserBiometric userBiometric1 = new UserBiometric();
		UserBiometricId userBiometricId = new UserBiometricId();
		userBiometricId.setUsrId("mosip");
		userBiometric1.setBioIsoImage(testData);
		userBiometric1.setUserBiometricId(userBiometricId);
		UserBiometric userBiometric2 = new UserBiometric();
		userBiometric2.setBioIsoImage(testData);
		userBiometric2.setUserBiometricId(userBiometricId);
		userBiometrics.add(userBiometric1);
		userBiometrics.add(userBiometric2);

		Boolean res = faceFacade.validateFace(faceDetailsDTO, userBiometrics);
		assertTrue(!res);
	}

}
