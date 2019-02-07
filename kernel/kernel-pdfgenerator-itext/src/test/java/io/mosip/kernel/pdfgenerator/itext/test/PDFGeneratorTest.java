package io.mosip.kernel.pdfgenerator.itext.test;

import static org.junit.Assert.assertNotNull;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { PDFGeneratorImpl.class })
@SuppressWarnings("resource")
public class PDFGeneratorTest {
	@Autowired
	private PDFGenerator pdfGenerator;

	private static BufferedImage bufferedImage;
	private static BufferedImage bufferedImage2;

	private static List<BufferedImage> bufferedImages = new ArrayList<>();

	@BeforeClass
	public static void initialize() throws IOException, java.io.IOException {
		URL url = PDFGeneratorTest.class.getResource("/Change.jpg");
		URL url2 = PDFGeneratorTest.class.getResource("/nelsonmandela1-2x.jpg");

		bufferedImage = ImageIO.read(url);
		bufferedImages.add(bufferedImage);

		bufferedImage2 = ImageIO.read(url2);
		bufferedImages.add(bufferedImage2);
	}

	@Test
	public void testPdfGenerationWithInputStream() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("csshtml.html").getFile();
		InputStream is = new FileInputStream(inputFile);
		ByteArrayOutputStream bos = (ByteArrayOutputStream) pdfGenerator.generate(is);
		String outputPath = System.getProperty("user.dir");
		String fileSepetator = System.getProperty("file.separator");
		File OutPutPdfFile = new File(outputPath + fileSepetator + "csshtml.pdf");
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
		File OutPutPdfFile = new File(outputPath + fileSepetator + "test.pdf");
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
		pdfGenerator.generate(inputFile, outputPath, generatedPdfFileName);
		File tempoutFile = new File(outputPath + fileSepetator + generatedPdfFileName + outputFileExtension);
		assertTrue(tempoutFile.exists());
	}

	@Test(expected = PDFGeneratorException.class)
	public void testPdfGeneratorExceptionInFile() throws IOException {
		String outputPath = System.getProperty("user.dir");
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("").getFile();
		String generatedPdfFileName = "Wiki";
		pdfGenerator.generate(inputFile, outputPath, generatedPdfFileName);

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
		File OutPutPdfFile = new File(outputPath + fileSepetator + "responsive.pdf");
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
		File OutPutPdfFile = new File(outputPath + fileSeperator + "merge.pdf");
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
		File OutPutPdfFile = new File(outputPath + fileSeperator + "new_merged.pdf");
		FileOutputStream op = new FileOutputStream(OutPutPdfFile);
		op.write(byteArray);
		op.flush();
		assertTrue(OutPutPdfFile.exists());
		if (op != null) {
			op.close();
		}
	}

	@AfterClass
	public static void deleteOutputFile() {
		String outputFilePath = System.getProperty("user.dir");
		String outputFileExtension = ".pdf";
		String fileSeperator = System.getProperty("file.separator");
		File temp2 = new File(outputFilePath + fileSeperator + "test" + outputFileExtension);
		if (temp2.exists()) {
			temp2.delete();
		}
		File temp3 = new File(outputFilePath + fileSeperator + "textcontant" + outputFileExtension);
		if (temp3.exists()) {
			temp3.delete();
		}
		File temp4 = new File(outputFilePath + fileSeperator + "Wiki" + outputFileExtension);
		if (temp4.exists()) {
			temp4.delete();
		}
		File temp5 = new File(outputFilePath + fileSeperator + "csshtml" + outputFileExtension);
		if (temp5.exists()) {
			temp5.delete();
		}
		File temp1 = new File(outputFilePath + fileSeperator + "emptyFile" + outputFileExtension);
		if (temp1.exists()) {
			temp1.delete();
		}
		File temp6 = new File(outputFilePath + fileSeperator + "responsive" + outputFileExtension);
		if (temp6.exists()) {
			temp6.delete();
		}
		File OutPutPdfFile = new File(outputFilePath + fileSeperator + "merge" + outputFileExtension);
		if (OutPutPdfFile.exists()) {
			OutPutPdfFile.delete();
		}
		File OutPutPdfFile2 = new File(outputFilePath + fileSeperator + "new_merged.pdf");
		if (OutPutPdfFile2.exists()) {
			OutPutPdfFile2.delete();
		}
	}

}
