package io.mosip.registration.device.fp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.when;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserBiometricId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import javafx.scene.image.WritableImage;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ImageIO.class, IOUtils.class, FingerprintTemplate.class })
public class FingerprintFacadeTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private FingerprintFacade fingerprintFacade;

	@Mock
	private MosipFingerprintProvider fingerprintProvider;

	@Test
	public void testGetMinutia() {
		String testmin = "test Minutia";
		Mockito.when(fingerprintProvider.getMinutia()).thenReturn(testmin);
		String result = fingerprintFacade.getMinutia();
		assertTrue(result.equals(testmin));
	}

	@Test
	public void testGetIsoTemplate() {
		byte[] testdata = "SampleString".getBytes();
		Mockito.when(fingerprintProvider.getIsoTemplate()).thenReturn(testdata);
		byte[] res = fingerprintFacade.getIsoTemplate();
		assertEquals(testdata, res);
	}

	@Test
	public void testGetErrorMessage() {
		String testmin = "test Minutia";
		Mockito.when(fingerprintProvider.getErrorMessage()).thenReturn(testmin);
		String result = fingerprintFacade.getErrorMessage();
		assertTrue(result.equals(testmin));
	}

	@Test
	public void testGetFingerPrintImage() throws IOException {
		WritableImage writableImage = new WritableImage(100, 100);
		Mockito.when(fingerprintProvider.getFingerPrintImage()).thenReturn(writableImage);
		WritableImage image = fingerprintProvider.getFingerPrintImage();
		Assert.assertThat(image, CoreMatchers.is(writableImage));
	}

	@Test
	public void testGetFingerPrintImageAsDTO() throws IOException, RegBaseCheckedException {
		FingerprintDetailsDTO fingerprintDTO = new FingerprintDetailsDTO();
		FingerprintDetailsDTO fingerprintDTO1 = new FingerprintDetailsDTO();
		FingerprintDetailsDTO fingerprintDTO2 = new FingerprintDetailsDTO();

		PowerMockito.mockStatic(ImageIO.class);
		when(ImageIO.read(Mockito.any(InputStream.class))).thenReturn(Mockito.mock(BufferedImage.class));

		fingerprintFacade.getFingerPrintImageAsDTO(fingerprintDTO, "leftSlap");

		assertNotNull(fingerprintDTO.getFingerPrint());
		assertEquals("leftSlap.jpg", fingerprintDTO.getFingerprintImageName());
		assertEquals("leftSlap", fingerprintDTO.getFingerType());
		assertEquals(0, fingerprintDTO.getNumRetry());
		assertEquals(85.0, fingerprintDTO.getQualityScore(), 0.1);
		assertEquals(false, fingerprintDTO.isForceCaptured());

		fingerprintFacade.getFingerPrintImageAsDTO(fingerprintDTO1, "rightSlap");

		assertNotNull(fingerprintDTO1.getFingerPrint());
		assertEquals("rightSlap.jpg", fingerprintDTO1.getFingerprintImageName());
		assertEquals("rightSlap", fingerprintDTO1.getFingerType());
		assertEquals(0, fingerprintDTO1.getNumRetry());
		assertEquals(90.0, fingerprintDTO1.getQualityScore(), 0.1);
		assertEquals(false, fingerprintDTO1.isForceCaptured());

		fingerprintFacade.getFingerPrintImageAsDTO(fingerprintDTO2, "thumbs");

		assertNotNull(fingerprintDTO2.getFingerPrint());
		assertEquals("thumbs.jpg", fingerprintDTO2.getFingerprintImageName());
		assertEquals("thumbs", fingerprintDTO2.getFingerType());
		assertEquals(0, fingerprintDTO2.getNumRetry());
		assertEquals(90.0, fingerprintDTO2.getQualityScore(), 0.1);
		assertEquals(false, fingerprintDTO2.isForceCaptured());
	}

	@Test
	public void testSegmentFingerPrintImage() throws IOException, RegBaseCheckedException {
		FingerprintDetailsDTO fingerprintDTO = new FingerprintDetailsDTO();

		PowerMockito.mockStatic(IOUtils.class);
		when(IOUtils.resourceToByteArray(Mockito.anyString())).thenReturn("image".getBytes());
		String[] LEFTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/lefthand/leftIndex/",
				"/fingerprints/lefthand/leftLittle/" };

		fingerprintFacade.segmentFingerPrintImage(fingerprintDTO, LEFTHAND_SEGMNTD_FILE_PATHS);

		assertEquals("image", new String(fingerprintDTO.getSegmentedFingerprints().get(0).getFingerPrint()));
		assertEquals("leftIndex", fingerprintDTO.getSegmentedFingerprints().get(0).getFingerprintImageName());
		assertEquals("leftIndex", fingerprintDTO.getSegmentedFingerprints().get(0).getFingerType());
		assertEquals(0, fingerprintDTO.getSegmentedFingerprints().get(0).getNumRetry());
		assertEquals(90.0, fingerprintDTO.getSegmentedFingerprints().get(0).getQualityScore(), 0.1);
		assertEquals(false, fingerprintDTO.isForceCaptured());

		assertEquals("image", new String(fingerprintDTO.getSegmentedFingerprints().get(1).getFingerPrint()));
		assertEquals("leftLittle", fingerprintDTO.getSegmentedFingerprints().get(1).getFingerprintImageName());
		assertEquals("leftLittle", fingerprintDTO.getSegmentedFingerprints().get(1).getFingerType());
		assertEquals(0, fingerprintDTO.getSegmentedFingerprints().get(1).getNumRetry());
		assertEquals(90.0, fingerprintDTO.getSegmentedFingerprints().get(1).getQualityScore(), 0.1);
		assertEquals(false, fingerprintDTO.isForceCaptured());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testValidateException1() throws RegBaseCheckedException, IOException {
		String[] LEFTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/lefthand/leftIndex/",
				"/fingerprints/lefthand/leftLittle/" };
		PowerMockito.mockStatic(IOUtils.class);
		when(IOUtils.resourceToByteArray(Mockito.anyString())).thenThrow(new IOException("Invalid"));
		fingerprintFacade.segmentFingerPrintImage(null, LEFTHAND_SEGMNTD_FILE_PATHS);

	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException2() throws RegBaseCheckedException, IOException {
		String[] LEFTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/lefthand/leftIndex/",
				"/fingerprints/lefthand/leftLittle/" };
		PowerMockito.mockStatic(IOUtils.class);
		when(IOUtils.resourceToByteArray(Mockito.anyString())).thenThrow(new RuntimeException("Invalid"));
		fingerprintFacade.segmentFingerPrintImage(null, LEFTHAND_SEGMNTD_FILE_PATHS);

	}

	@Test(expected = RegBaseCheckedException.class)
	public void testValidateException3() throws RegBaseCheckedException, IOException {
		PowerMockito.mockStatic(ImageIO.class);
		when(ImageIO.read(Mockito.any(InputStream.class))).thenThrow(new IOException("Invalid"));
		fingerprintFacade.getFingerPrintImageAsDTO(null, "leftSlap");
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException4() throws RegBaseCheckedException, IOException {
		PowerMockito.mockStatic(ImageIO.class);
		when(ImageIO.read(Mockito.any(InputStream.class))).thenThrow(new RuntimeException("Invalid"));
		fingerprintFacade.getFingerPrintImageAsDTO(null, "leftSlap");
	}

	@Test
	public void testvalidateFP() throws Exception {
		FingerprintDetailsDTO fingerprintDTO = new FingerprintDetailsDTO();
		byte[] fpData = { 70, 77, 82, 0, 32, 50, 48, 0, 0, 0, 1, 26, 0, 0, 1, 60, 1, 98, 0, -59, 0, -59, 1, 0, 0, 0, 40,
				42, -128, -118, 0, -57, 35, 80, -128, 119, 0, -48, 46, 100, 64, 125, 0, -23, 34, 100, -128, -118, 0,
				-127, 14, 100, -128, -51, 0, -117, -29, 100, -128, 108, 1, 18, 21, 100, 64, -116, 1, 36, -4, 100, 64,
				70, 1, 15, 35, 33, 64, 59, 1, 16, -108, 33, 64, 65, 0, 105, 28, 93, 64, -27, 0, 87, -18, 100, -128, -81,
				1, 92, 119, 20, 64, -66, 0, 42, -10, 100, -128, 95, 0, 32, 19, 93, -128, -104, 0, -82, -83, 80, -128,
				-102, 0, -100, -1, 87, 64, -65, 0, -8, 87, 100, -128, -82, 0, 126, -117, 100, 64, 125, 1, 23, 12, 100,
				64, -54, 0, 119, -20, 100, -128, -21, 0, 124, 108, 100, -127, 14, 0, -39, 89, 100, -128, -127, 0, 82,
				-100, 100, -128, -49, 1, 65, -6, 100, 64, -40, 1, 60, 123, 100, 64, 56, 1, 58, 8, 87, 64, 49, 1, 72,
				-33, 67, 64, -13, 0, 41, -18, 100, -128, -125, 0, -82, 33, 93, -128, -73, 0, -94, -34, 93, -128, 100, 0,
				-27, 47, 100, 64, -41, 0, -16, 84, 100, -128, -74, 1, 25, 117, 100, 64, 94, 1, 14, 29, 100, 64, -125, 1,
				53, -13, 100, 64, 77, 1, 29, 18, 93, 64, 91, 1, 54, -4, 93, 65, 34, 0, -51, -41, 67, 64, -11, 1, 55,
				105, 100, -128, -29, 0, 58, 116, 100, -128, -92, 0, 27, 6, 100, -128, -59, 0, 22, -125, 100, 0, 0 };
		String minutiae = "{\"width\":316,\"height\":354,\"minutiae\":[{\"x\":129,\"y\":82,\"direction\":2.454369260617026,\"type\":\"bifurcation\"},{\"x\":91,\"y\":310,\"direction\":0.0981747704246807,\"type\":\"ending\"},{\"x\":131,\"y\":174,\"direction\":5.473243451175968,\"type\":\"bifurcation\"},{\"x\":140,\"y\":292,\"direction\":0.0981747704246807,\"type\":\"ending\"},{\"x\":216,\"y\":316,\"direction\":3.2643111166206444,\"type\":\"ending\"},{\"x\":77,\"y\":285,\"direction\":5.841398840268521,\"type\":\"ending\"},{\"x\":243,\"y\":41,\"direction\":0.44178646691106493,\"type\":\"ending\"},{\"x\":207,\"y\":321,\"direction\":0.14726215563702194,\"type\":\"bifurcation\"},{\"x\":49,\"y\":328,\"direction\":0.8099418560036185,\"type\":\"ending\"},{\"x\":56,\"y\":314,\"direction\":6.086835766330224,\"type\":\"ending\"},{\"x\":94,\"y\":270,\"direction\":5.5714182216006485,\"type\":\"ending\"},{\"x\":108,\"y\":274,\"direction\":5.767767762450011,\"type\":\"bifurcation\"},{\"x\":174,\"y\":126,\"direction\":2.8716120349219203,\"type\":\"bifurcation\"},{\"x\":175,\"y\":348,\"direction\":3.3624858870453256,\"type\":\"bifurcation\"},{\"x\":235,\"y\":124,\"direction\":3.6324665057131984,\"type\":\"bifurcation\"},{\"x\":100,\"y\":229,\"direction\":5.1296317546895835,\"type\":\"bifurcation\"},{\"x\":138,\"y\":129,\"direction\":5.939573610693203,\"type\":\"bifurcation\"},{\"x\":205,\"y\":139,\"direction\":0.7117670855789378,\"type\":\"bifurcation\"},{\"x\":182,\"y\":281,\"direction\":3.411573272257666,\"type\":\"bifurcation\"},{\"x\":270,\"y\":217,\"direction\":4.098796665230433,\"type\":\"bifurcation\"},{\"x\":154,\"y\":156,\"direction\":0.024543692606170175,\"type\":\"bifurcation\"},{\"x\":197,\"y\":22,\"direction\":3.067961575771282,\"type\":\"bifurcation\"},{\"x\":183,\"y\":162,\"direction\":0.8344855486097886,\"type\":\"bifurcation\"},{\"x\":227,\"y\":58,\"direction\":3.436116964863836,\"type\":\"bifurcation\"},{\"x\":65,\"y\":105,\"direction\":5.595961914206819,\"type\":\"ending\"},{\"x\":70,\"y\":271,\"direction\":5.424156065963627,\"type\":\"ending\"},{\"x\":164,\"y\":27,\"direction\":6.135923151542564,\"type\":\"bifurcation\"},{\"x\":125,\"y\":233,\"direction\":5.448699758569798,\"type\":\"ending\"},{\"x\":138,\"y\":199,\"direction\":5.424156065963627,\"type\":\"bifurcation\"},{\"x\":131,\"y\":309,\"direction\":0.31906800388021317,\"type\":\"ending\"},{\"x\":202,\"y\":119,\"direction\":0.4908738521234053,\"type\":\"ending\"},{\"x\":95,\"y\":32,\"direction\":5.816855147662351,\"type\":\"bifurcation\"},{\"x\":59,\"y\":272,\"direction\":2.650718801466388,\"type\":\"ending\"},{\"x\":119,\"y\":208,\"direction\":5.154175447295755,\"type\":\"bifurcation\"},{\"x\":152,\"y\":174,\"direction\":2.0371264863121317,\"type\":\"bifurcation\"},{\"x\":190,\"y\":42,\"direction\":0.24543692606170264,\"type\":\"ending\"},{\"x\":191,\"y\":248,\"direction\":4.147884050442774,\"type\":\"ending\"},{\"x\":215,\"y\":240,\"direction\":4.221515128261284,\"type\":\"ending\"},{\"x\":125,\"y\":279,\"direction\":5.988660995905543,\"type\":\"ending\"},{\"x\":229,\"y\":87,\"direction\":0.44178646691106493,\"type\":\"ending\"},{\"x\":245,\"y\":311,\"direction\":3.706097583531709,\"type\":\"ending\"},{\"x\":290,\"y\":205,\"direction\":1.0062913968529807,\"type\":\"ending\"}]}";
		fingerprintDTO.setFingerPrint(fpData);
		List<UserBiometric> userBiometrics = new ArrayList<>();
		UserBiometric userBiometric1 = new UserBiometric();
		UserBiometricId userBiometricId = new UserBiometricId();
		userBiometricId.setBioAttributeCode("leftIndex");
		userBiometricId.setUsrId("mosip");
		userBiometric1.setBioMinutia(minutiae);
		userBiometric1.setUserBiometricId(userBiometricId);
		UserBiometric userBiometric2 = new UserBiometric();
		userBiometric2.setBioMinutia(minutiae);
		userBiometric2.setUserBiometricId(userBiometricId);
		userBiometrics.add(userBiometric1);
		userBiometrics.add(userBiometric2);

		FingerprintTemplate fingerprintTemplate = Mockito.mock(FingerprintTemplate.class);
		PowerMockito.mockStatic(FingerprintTemplate.class);
		PowerMockito.whenNew(FingerprintTemplate.class).withNoArguments().thenReturn(fingerprintTemplate);
		Mockito.when(fingerprintTemplate.convert(fingerprintDTO.getFingerPrint())).thenReturn(fingerprintTemplate);
		Mockito.when(fingerprintTemplate.serialize()).thenReturn(minutiae);
		Mockito.when(fingerprintProvider.scoreCalculator(Mockito.anyString(), Mockito.anyString())).thenReturn(70.0);

		ReflectionTestUtils.setField(fingerprintFacade, "fingerPrintScore", 100);

		Boolean res = fingerprintFacade.validateFP(fingerprintDTO, userBiometrics);
		assertTrue(!res);
	}
	
	@Test
	public void testvalidateFPfailure() throws Exception {
		FingerprintDetailsDTO fingerprintDTO = new FingerprintDetailsDTO();
		byte[] fpData = { 70, 77, 82, 0, 32, 50, 48, 0, 0, 0, 1, 26, 0, 0, 1, 60, 1, 98, 0, -59, 0, -59, 1, 0, 0, 0, 40,
				42, -128, -118, 0, -57, 35, 80, -128, 119, 0, -48, 46, 100, 64, 125, 0, -23, 34, 100, -128, -118, 0,
				-127, 14, 100, -128, -51, 0, -117, -29, 100, -128, 108, 1, 18, 21, 100, 64, -116, 1, 36, -4, 100, 64,
				70, 1, 15, 35, 33, 64, 59, 1, 16, -108, 33, 64, 65, 0, 105, 28, 93, 64, -27, 0, 87, -18, 100, -128, -81,
				1, 92, 119, 20, 64, -66, 0, 42, -10, 100, -128, 95, 0, 32, 19, 93, -128, -104, 0, -82, -83, 80, -128,
				-102, 0, -100, -1, 87, 64, -65, 0, -8, 87, 100, -128, -82, 0, 126, -117, 100, 64, 125, 1, 23, 12, 100,
				64, -54, 0, 119, -20, 100, -128, -21, 0, 124, 108, 100, -127, 14, 0, -39, 89, 100, -128, -127, 0, 82,
				-100, 100, -128, -49, 1, 65, -6, 100, 64, -40, 1, 60, 123, 100, 64, 56, 1, 58, 8, 87, 64, 49, 1, 72,
				-33, 67, 64, -13, 0, 41, -18, 100, -128, -125, 0, -82, 33, 93, -128, -73, 0, -94, -34, 93, -128, 100, 0,
				-27, 47, 100, 64, -41, 0, -16, 84, 100, -128, -74, 1, 25, 117, 100, 64, 94, 1, 14, 29, 100, 64, -125, 1,
				53, -13, 100, 64, 77, 1, 29, 18, 93, 64, 91, 1, 54, -4, 93, 65, 34, 0, -51, -41, 67, 64, -11, 1, 55,
				105, 100, -128, -29, 0, 58, 116, 100, -128, -92, 0, 27, 6, 100, -128, -59, 0, 22, -125, 100, 0, 0 };
		String minutiae = "{\"width\":316,\"height\":354,\"minutiae\":[{\"x\":129,\"y\":82,\"direction\":2.454369260617026,\"type\":\"bifurcation\"},{\"x\":91,\"y\":310,\"direction\":0.0981747704246807,\"type\":\"ending\"},{\"x\":131,\"y\":174,\"direction\":5.473243451175968,\"type\":\"bifurcation\"},{\"x\":140,\"y\":292,\"direction\":0.0981747704246807,\"type\":\"ending\"},{\"x\":216,\"y\":316,\"direction\":3.2643111166206444,\"type\":\"ending\"},{\"x\":77,\"y\":285,\"direction\":5.841398840268521,\"type\":\"ending\"},{\"x\":243,\"y\":41,\"direction\":0.44178646691106493,\"type\":\"ending\"},{\"x\":207,\"y\":321,\"direction\":0.14726215563702194,\"type\":\"bifurcation\"},{\"x\":49,\"y\":328,\"direction\":0.8099418560036185,\"type\":\"ending\"},{\"x\":56,\"y\":314,\"direction\":6.086835766330224,\"type\":\"ending\"},{\"x\":94,\"y\":270,\"direction\":5.5714182216006485,\"type\":\"ending\"},{\"x\":108,\"y\":274,\"direction\":5.767767762450011,\"type\":\"bifurcation\"},{\"x\":174,\"y\":126,\"direction\":2.8716120349219203,\"type\":\"bifurcation\"},{\"x\":175,\"y\":348,\"direction\":3.3624858870453256,\"type\":\"bifurcation\"},{\"x\":235,\"y\":124,\"direction\":3.6324665057131984,\"type\":\"bifurcation\"},{\"x\":100,\"y\":229,\"direction\":5.1296317546895835,\"type\":\"bifurcation\"},{\"x\":138,\"y\":129,\"direction\":5.939573610693203,\"type\":\"bifurcation\"},{\"x\":205,\"y\":139,\"direction\":0.7117670855789378,\"type\":\"bifurcation\"},{\"x\":182,\"y\":281,\"direction\":3.411573272257666,\"type\":\"bifurcation\"},{\"x\":270,\"y\":217,\"direction\":4.098796665230433,\"type\":\"bifurcation\"},{\"x\":154,\"y\":156,\"direction\":0.024543692606170175,\"type\":\"bifurcation\"},{\"x\":197,\"y\":22,\"direction\":3.067961575771282,\"type\":\"bifurcation\"},{\"x\":183,\"y\":162,\"direction\":0.8344855486097886,\"type\":\"bifurcation\"},{\"x\":227,\"y\":58,\"direction\":3.436116964863836,\"type\":\"bifurcation\"},{\"x\":65,\"y\":105,\"direction\":5.595961914206819,\"type\":\"ending\"},{\"x\":70,\"y\":271,\"direction\":5.424156065963627,\"type\":\"ending\"},{\"x\":164,\"y\":27,\"direction\":6.135923151542564,\"type\":\"bifurcation\"},{\"x\":125,\"y\":233,\"direction\":5.448699758569798,\"type\":\"ending\"},{\"x\":138,\"y\":199,\"direction\":5.424156065963627,\"type\":\"bifurcation\"},{\"x\":131,\"y\":309,\"direction\":0.31906800388021317,\"type\":\"ending\"},{\"x\":202,\"y\":119,\"direction\":0.4908738521234053,\"type\":\"ending\"},{\"x\":95,\"y\":32,\"direction\":5.816855147662351,\"type\":\"bifurcation\"},{\"x\":59,\"y\":272,\"direction\":2.650718801466388,\"type\":\"ending\"},{\"x\":119,\"y\":208,\"direction\":5.154175447295755,\"type\":\"bifurcation\"},{\"x\":152,\"y\":174,\"direction\":2.0371264863121317,\"type\":\"bifurcation\"},{\"x\":190,\"y\":42,\"direction\":0.24543692606170264,\"type\":\"ending\"},{\"x\":191,\"y\":248,\"direction\":4.147884050442774,\"type\":\"ending\"},{\"x\":215,\"y\":240,\"direction\":4.221515128261284,\"type\":\"ending\"},{\"x\":125,\"y\":279,\"direction\":5.988660995905543,\"type\":\"ending\"},{\"x\":229,\"y\":87,\"direction\":0.44178646691106493,\"type\":\"ending\"},{\"x\":245,\"y\":311,\"direction\":3.706097583531709,\"type\":\"ending\"},{\"x\":290,\"y\":205,\"direction\":1.0062913968529807,\"type\":\"ending\"}]}";
		fingerprintDTO.setFingerPrint(fpData);
		List<UserBiometric> userBiometrics = new ArrayList<>();
		UserBiometric userBiometric1 = new UserBiometric();
		UserBiometricId userBiometricId = new UserBiometricId();
		userBiometricId.setBioAttributeCode("leftIndex");
		userBiometricId.setUsrId("mosip");
		userBiometric1.setBioMinutia(minutiae);
		userBiometric1.setUserBiometricId(userBiometricId);
		UserBiometric userBiometric2 = new UserBiometric();
		userBiometric2.setBioMinutia(minutiae);
		userBiometric2.setUserBiometricId(userBiometricId);
		userBiometrics.add(userBiometric1);
		userBiometrics.add(userBiometric2);

		FingerprintTemplate fingerprintTemplate = Mockito.mock(FingerprintTemplate.class);
		PowerMockito.mockStatic(FingerprintTemplate.class);
		PowerMockito.whenNew(FingerprintTemplate.class).withNoArguments().thenReturn(fingerprintTemplate);
		Mockito.when(fingerprintTemplate.convert(fingerprintDTO.getFingerPrint())).thenReturn(fingerprintTemplate);
		Mockito.when(fingerprintTemplate.serialize()).thenReturn(minutiae);
		Mockito.when(fingerprintProvider.scoreCalculator(Mockito.anyString(), Mockito.anyString())).thenReturn(700.0);

		ReflectionTestUtils.setField(fingerprintFacade, "fingerPrintScore", 100);

		Boolean res = fingerprintFacade.validateFP(fingerprintDTO, userBiometrics);
		assertTrue(res);
	}
}
