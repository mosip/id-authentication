package io.mosip.kernel.pdfgenerator.itext.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.keymanager.model.CertificateEntry;
import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.kernel.core.pdfgenerator.model.Rectangle;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { PDFGeneratorImpl.class })
@SuppressWarnings("resource")
@PropertySource("application-test.properties")
public class PDFGeneratorTest {
	@Autowired
	private PDFGenerator pdfGenerator;
	
	@Autowired
	private ResourceLoader resourceLoader;

	private static BufferedImage bufferedImage;
	private static BufferedImage bufferedImage2;
	
	private static BouncyCastleProvider bouncyCastleProvider;
	
	private static CertificateEntry<X509Certificate, PrivateKey> certificateEntry;

	private static List<BufferedImage> bufferedImages = new ArrayList<>();

	@BeforeClass
	public static void initialize() throws IOException, java.io.IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SecurityException, SignatureException {
		URL url = PDFGeneratorTest.class.getResource("/Change.jpg");
		URL url2 = PDFGeneratorTest.class.getResource("/nelsonmandela1-2x.jpg");

		bufferedImage = ImageIO.read(url);
		bufferedImages.add(bufferedImage);

		bufferedImage2 = ImageIO.read(url2);
		bufferedImages.add(bufferedImage2);
		
		
		bouncyCastleProvider = new BouncyCastleProvider();
		Security.addProvider(bouncyCastleProvider);
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		KeyPair keyPair=generator.generateKeyPair();
		X509Certificate[] serverChain = new X509Certificate[1];
		X509V3CertificateGenerator serverCertGen = new X509V3CertificateGenerator();
		X500Principal serverSubjectName = new X500Principal("CN=OrganizationName");
		serverCertGen.setSerialNumber(new BigInteger("123456789"));
		// X509Certificate caCert=null;
		serverCertGen.setIssuerDN(serverSubjectName);
		serverCertGen.setNotBefore(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		serverCertGen.setNotAfter(Date.from(LocalDate.now().plusDays(21).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		serverCertGen.setSubjectDN(serverSubjectName);
		serverCertGen.setPublicKey(keyPair.getPublic());
		serverCertGen.setSignatureAlgorithm("MD5WithRSA");
	
		serverChain[0] = serverCertGen.generateX509Certificate(keyPair.getPrivate(), "BC"); // note: private key of CA
        
		certificateEntry= new CertificateEntry<X509Certificate, PrivateKey>(serverChain, keyPair.getPrivate());			
				
				
		
	
	}
	@Test
	public void testPdfGenerationWithInputStream() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("csshtml.html").getFile();
		InputStream is = new FileInputStream(inputFile);
		ByteArrayOutputStream bos = (ByteArrayOutputStream) pdfGenerator.generate(is);
		String outputPath = System.getProperty("user.dir");
		String fileSepetator = System.getProperty("file.separator");
		File OutPutPdfFile = new File("csshtml.pdf");
		FileOutputStream op = new FileOutputStream(OutPutPdfFile);
		op.write(bos.toByteArray());
		op.flush();
		assertTrue(OutPutPdfFile.exists());
		if (op != null) {
			op.close();
		}

	}

	@Test(expected = PDFGeneratorException.class)
	public void testPdfGeneratorExceptionInInputStream() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFileName = classLoader.getResource("emptyFile.html").getFile();
		File inputFile = new File(inputFileName);
		InputStream inputStream = new FileInputStream(inputFile);
		pdfGenerator.generate(inputStream);
	}

	@Test
	public void testPdfGenerationWithTemplateAsStringAndOutStream() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFileName = classLoader.getResource("test.html").getFile();
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line.trim());
		}

		ByteArrayOutputStream bos = (ByteArrayOutputStream) pdfGenerator.generate(sb.toString());
		String outputPath = System.getProperty("user.dir");
		String fileSepetator = System.getProperty("file.separator");
		File OutPutPdfFile = new File("test.pdf");
		FileOutputStream op = new FileOutputStream(OutPutPdfFile);
		op.write(bos.toByteArray());
		op.flush();
		assertTrue(OutPutPdfFile.exists());
		if (op != null) {
			op.close();

		}
	}

	@Test(expected = PDFGeneratorException.class)
	public void testPDFGeneratorGenericExceptionWithTemplateAsString() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFileName = classLoader.getResource("emptyFile.html").getFile();
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line.trim());
		}
		pdfGenerator.generate(sb.toString());
	}

	@Test
	public void testPdfGenerationWithFile() throws IOException {
		String outputPath = System.getProperty("user.dir");
		String outputFileExtension = ".pdf";
		String fileSepetator = System.getProperty("file.separator");
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("textcontant.txt").getFile();
		String generatedPdfFileName = "textcontant";
		pdfGenerator.generate(inputFile, "", generatedPdfFileName);
		File tempoutFile = new File(generatedPdfFileName + outputFileExtension);
		assertTrue(tempoutFile.exists());
	}

	@Test(expected = PDFGeneratorException.class)
	public void testPdfGeneratorExceptionInFile() throws IOException {
		String outputPath = System.getProperty("user.dir");
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("").getFile();
		String generatedPdfFileName = "Wiki";
		pdfGenerator.generate(inputFile, "", generatedPdfFileName);

	}

	@Test
	public void testPdfGenerationWithInputStreamPassingResourceLoc() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("responsive.html").getFile();
		File file = new File(inputFile);
		if (file.getParentFile().isDirectory()) {
			file = file.getParentFile();
		}
		String resourceLoc = file.getAbsolutePath();
		InputStream is = new FileInputStream(inputFile);
		ByteArrayOutputStream bos = (ByteArrayOutputStream) pdfGenerator.generate(is, resourceLoc);
		String outputPath = System.getProperty("user.dir");
		String fileSepetator = System.getProperty("file.separator");
		File OutPutPdfFile = new File("responsive.pdf");
		FileOutputStream op = new FileOutputStream(OutPutPdfFile);
		op.write(bos.toByteArray());
		op.flush();
		assertTrue(OutPutPdfFile.exists());
		if (op != null) {
			op.close();
		}

	}

	@Test(expected = PDFGeneratorException.class)
	public void testPdfGeneratorExceptionInInputStreamPassingResourceLoc() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFileName = classLoader.getResource("emptyFile.html").getFile();
		File file = new File(inputFileName);
		if (file.getParentFile().isDirectory()) {
			file = file.getParentFile();
		}
		String resourceLoc = file.getAbsolutePath();
		InputStream inputStream = new FileInputStream(inputFileName);
		pdfGenerator.generate(inputStream, resourceLoc);
	}

	@Test
	public void getSinglePDFInBytesTest() throws IOException {
		byte[] data = pdfGenerator.asPDF(bufferedImages);
		String outputPath = System.getProperty("user.dir");
		String fileSeperator = System.getProperty("file.separator");
		File OutPutPdfFile = new File("merge.pdf");
		FileOutputStream op = new FileOutputStream(OutPutPdfFile);
		op.write(data);
		op.flush();
		assertNotNull(data);
		assertTrue(OutPutPdfFile.exists());
		if (op != null) {
			op.close();
		}
	}

	@Test
	public void mergePDFTest() throws IOException {
		List<URL> pdfFiles = new ArrayList<URL>(Arrays.asList(PDFGeneratorTest.class.getResource("/sample.pdf"),
				PDFGeneratorTest.class.getResource("/pdf-sample.pdf")));
		byte[] byteArray = pdfGenerator.mergePDF(pdfFiles);

		String outputPath = System.getProperty("user.dir");
		String fileSeperator = System.getProperty("file.separator");
		File OutPutPdfFile = new File("new_merged.pdf");
		FileOutputStream op = new FileOutputStream(OutPutPdfFile);
		op.write(byteArray);
		op.flush();
		assertTrue(OutPutPdfFile.exists());
		if (op != null) {
			op.close();
		}
	}
	
	@Test
	public void testsignAndEncryptPDF() throws IOException, GeneralSecurityException, io.mosip.kernel.core.exception.IOException{
		byte[] pdf=FileUtils.readFileToByteArray(resourceLoader.getResource("classpath:dummy.pdf").getFile());
		Rectangle rectangle = new Rectangle(100, 100, 200, 200);
		assertThat(pdfGenerator.signAndEncryptPDF(pdf, rectangle, "check", 1, bouncyCastleProvider, certificateEntry, "mosip") ,isA(OutputStream.class));
	}

	@AfterClass
	public static void deleteOutputFile() {
		String outputFilePath = System.getProperty("user.dir");
		String outputFileExtension = ".pdf";
		String fileSeperator = System.getProperty("file.separator");
		File temp2 = new File("test" + outputFileExtension);
		if (temp2.exists()) {
			temp2.delete();
		}
		File temp3 = new File("textcontant" + outputFileExtension);
		if (temp3.exists()) {
			temp3.delete();
		}
		File temp4 = new File("Wiki" + outputFileExtension);
		if (temp4.exists()) {
			temp4.delete();
		}
		File temp5 = new File("csshtml" + outputFileExtension);
		if (temp5.exists()) {
			temp5.delete();
		}
		File temp1 = new File("emptyFile" + outputFileExtension);
		if (temp1.exists()) {
			temp1.delete();
		}
		File temp6 = new File("responsive" + outputFileExtension);
		if (temp6.exists()) {
			temp6.delete();
		}
		File OutPutPdfFile = new File("merge" + outputFileExtension);
		if (OutPutPdfFile.exists()) {
			OutPutPdfFile.delete();
		}
		File OutPutPdfFile2 = new File("new_merged.pdf");
		if (OutPutPdfFile2.exists()) {
			OutPutPdfFile2.delete();
		}
		File OutPutPdfFile3 = new File("protected"+ outputFileExtension);
		if (OutPutPdfFile3.exists()) {
			OutPutPdfFile3.delete();
		}
	}

}
