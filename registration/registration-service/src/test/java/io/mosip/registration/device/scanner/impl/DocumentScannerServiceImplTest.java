package io.mosip.registration.device.scanner.impl;

import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.exception.IOException;

public class DocumentScannerServiceImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private DocumentScannerServiceImpl documentScannerServiceImpl;

	static BufferedImage bufferedImage;

	static List<BufferedImage> bufferedImages = new ArrayList<>();

	@BeforeClass
	public static void initialize() throws IOException, java.io.IOException {
		URL url = DocumentScannerServiceImplTest.class.getResource("/applicantPhoto.jpg");

		bufferedImage = ImageIO.read(url);
		bufferedImages.add(bufferedImage);

	}

	@Test
	public void isScannerConnectedTest() {

		intializeValues();
		boolean isConnected = documentScannerServiceImpl.isConnected();

		Assert.assertNotNull(isConnected);
	}

	@Test
	public void scanDocumentTest() {
		intializeValues();
		documentScannerServiceImpl.scan();
		Assert.assertNotNull(bufferedImage);

	}

	@Test
	public void getSinglePDFInBytesTest() {
		intializeValues();
		byte[] data = documentScannerServiceImpl.asPDF(bufferedImages);
		assertNotNull(data);

	}

	@Test
	public void getSingleImageFromListTest() throws java.io.IOException {
		intializeValues();
		byte[] data = documentScannerServiceImpl.asImage(bufferedImages);
		assertNotNull(data);

	}

	@Test
	public void getSingleImageAlternateFlowTest() throws java.io.IOException {
		intializeValues();
		bufferedImages.add(bufferedImage);
		byte[] data = documentScannerServiceImpl.asImage(bufferedImages);
		assertNotNull(data);

	}

	private void intializeValues() {
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerDpi", 300);
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerhost", "192.168.43.253");
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerPort", 6566);
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerImgType", "jpg");
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerTimeout", 2000);
		
	}

}
