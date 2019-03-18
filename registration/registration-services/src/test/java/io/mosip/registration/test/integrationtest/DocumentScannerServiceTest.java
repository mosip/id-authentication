package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.device.scanner.IMosipDocumentScannerService;
import io.mosip.registration.device.scanner.impl.DocumentScannerSaneServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class DocumentScannerServiceTest {
	
	@Autowired
	private DocumentScannerSaneServiceImpl documentScannerServiceImpl;

	static BufferedImage bufferedImage;

	static List<BufferedImage> bufferedImages = new ArrayList<>();

	@BeforeClass
	public static void initialize() throws IOException, java.io.IOException {
		URL url = DocumentScannerServiceTest.class.getResource("/applicantPhoto.jpg");
		System.out.println(url);
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
		bufferedImage = documentScannerServiceImpl.scan();
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
	
	@Test
	public void pdfToImagesTest() throws java.io.IOException {
		intializeValues();
		byte[] data = documentScannerServiceImpl.asPDF(bufferedImages);
		documentScannerServiceImpl.pdfToImages(data);
		assertNotNull(data);

	}
	
	@Test
	public void getImageBytesFromBufferedImageTest() throws java.io.IOException {
		intializeValues();
		byte[] data = documentScannerServiceImpl.getImageBytesFromBufferedImage(bufferedImage);
		assertNotNull(data);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getImageBytesFromBufferedImageTestNull() throws java.io.IOException {
		intializeValues();
		byte[] data = documentScannerServiceImpl.getImageBytesFromBufferedImage(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void pdfToImagesTestNull() throws java.io.IOException {
		intializeValues();
		byte[] data = documentScannerServiceImpl.asPDF(bufferedImages);
		documentScannerServiceImpl.pdfToImages(null);

	}
	
	@Test
	public void getSingleImageFromListTestNull() throws java.io.IOException {
		intializeValues();
		byte[] data = documentScannerServiceImpl.asImage(null);
		assertNull(data);
	}
	
	@Test(expected = NullPointerException.class)
	public void getSinglePDFInBytesTestNull() {
		intializeValues();
		byte[] data = documentScannerServiceImpl.asPDF(null);

	}
	private void intializeValues() {
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerDpi", 300);
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerhost", "192.168.43.253");
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerPort", 6566);
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerImgType", "jpg");
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerTimeout", 2000);

	}
}
