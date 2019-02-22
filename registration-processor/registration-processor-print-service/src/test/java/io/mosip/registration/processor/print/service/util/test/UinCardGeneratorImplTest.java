package io.mosip.registration.processor.print.service.util.test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.registration.processor.core.constant.UinCardType;
import io.mosip.registration.processor.print.service.utility.UinCardGeneratorImpl;

@RunWith(MockitoJUnitRunner.class)
public class UinCardGeneratorImplTest {
	
	@Mock
	private PDFGenerator pdfGenerator;
	
	@InjectMocks
	private UinCardGeneratorImpl cardGeneratorImpl;
		
	@Test
	public void testCardGenerationSuccess() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("csshtml.html").getFile();
		InputStream is = new FileInputStream(inputFile);
		
		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		while ((bytesRead = is.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		
		Mockito.when(pdfGenerator.generate(is)).thenReturn(outputStream);
		
		ByteArrayOutputStream bos = (ByteArrayOutputStream) cardGeneratorImpl.generateUinCard(is, UinCardType.PDF);
		
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
		OutPutPdfFile.delete();
	}
	
	@Test(expected = PDFGeneratorException.class)
	public void testPdfGeneratorException() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFileName = classLoader.getResource("emptyFile.html").getFile();
		File inputFile = new File(inputFileName);
		InputStream inputStream = new FileInputStream(inputFile);
		PDFGeneratorException e = new PDFGeneratorException(null, null);
		Mockito.doThrow(e).when(pdfGenerator).generate(inputStream);
		cardGeneratorImpl.generateUinCard(inputStream, UinCardType.PDF);
	}

}
