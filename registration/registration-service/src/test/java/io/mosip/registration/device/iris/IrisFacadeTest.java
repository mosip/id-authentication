package io.mosip.registration.device.iris;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

}
