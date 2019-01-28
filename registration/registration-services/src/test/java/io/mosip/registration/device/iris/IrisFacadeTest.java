package io.mosip.registration.device.iris;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserBiometricId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ImageIO.class })
public class IrisFacadeTest {

	@InjectMocks
	private IrisFacade irisFacade;

	@Test
	public void testGetIrisImageAsDTO() throws RegBaseCheckedException, IOException {
		PowerMockito.mockStatic(ImageIO.class);
		when(ImageIO.read(Mockito.any(InputStream.class))).thenReturn(Mockito.mock(BufferedImage.class));

		IrisDetailsDTO detailsDTO = new IrisDetailsDTO();
		irisFacade.getIrisImageAsDTO(detailsDTO, "LeftEye");

		assertNotNull(detailsDTO.getIris());
		assertEquals("LeftEye.png", detailsDTO.getIrisImageName());
		assertEquals("LeftEye", detailsDTO.getIrisType());
		assertEquals(0, detailsDTO.getNumOfIrisRetry());
		assertEquals(90.5, detailsDTO.getQualityScore(), 0.1);
		assertEquals(false, detailsDTO.isForceCaptured());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testGetIrisImageAsDTOCheckedException() throws RegBaseCheckedException, IOException {
		PowerMockito.mockStatic(ImageIO.class);
		when(ImageIO.read(Mockito.any(InputStream.class))).thenThrow(new IOException("Invalid"));

		irisFacade.getIrisImageAsDTO(null, "LeftEye");
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testGetIrisImageAsDTORuntimeException() throws RegBaseCheckedException {
		irisFacade.getIrisImageAsDTO(null, "LeftEye");
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testGetIrisImageAsDTOUnCheckedException() throws RegBaseCheckedException, IOException {
		PowerMockito.mockStatic(ImageIO.class);
		when(ImageIO.read(Mockito.any(InputStream.class))).thenThrow(new NullPointerException("Invalid"));

		irisFacade.getIrisImageAsDTO(null, "LeftEye");
	}
	
	@Test
	public void validateIrisTest() {
		byte[] testData = "leftIris".getBytes();
		IrisDetailsDTO irDetailsDTO = new IrisDetailsDTO();
		irDetailsDTO.setIris(testData);
		
		List<UserBiometric> userBiometrics = new ArrayList<>();
		UserBiometric userBiometric1 = new UserBiometric();
		UserBiometricId userBiometricId = new UserBiometricId();
		userBiometricId.setBioAttributeCode("leftIris");
		userBiometricId.setUsrId("mosip");
		userBiometric1.setBioIsoImage(testData);
		userBiometric1.setUserBiometricId(userBiometricId);
		UserBiometric userBiometric2 = new UserBiometric();
		userBiometric2.setBioIsoImage(testData);
		userBiometric2.setUserBiometricId(userBiometricId);
		userBiometrics.add(userBiometric1);
		userBiometrics.add(userBiometric2);
		
		Boolean res = irisFacade.validateIris(irDetailsDTO, userBiometrics);
		assertTrue(res);
	}
	
	@Test
	public void validateIrisFailureTest() {
		byte[] testData = "leftIris".getBytes();
		IrisDetailsDTO irDetailsDTO = new IrisDetailsDTO();
		irDetailsDTO.setIris("leftI".getBytes());
		
		List<UserBiometric> userBiometrics = new ArrayList<>();
		UserBiometric userBiometric1 = new UserBiometric();
		UserBiometricId userBiometricId = new UserBiometricId();
		userBiometricId.setBioAttributeCode("leftIris");
		userBiometricId.setUsrId("mosip");
		userBiometric1.setBioIsoImage(testData);
		userBiometric1.setUserBiometricId(userBiometricId);
		UserBiometric userBiometric2 = new UserBiometric();
		userBiometric2.setBioIsoImage(testData);
		userBiometric2.setUserBiometricId(userBiometricId);
		userBiometrics.add(userBiometric1);
		userBiometrics.add(userBiometric2);
		
		Boolean res = irisFacade.validateIris(irDetailsDTO, userBiometrics);
		assertTrue(!res);
	}

}
