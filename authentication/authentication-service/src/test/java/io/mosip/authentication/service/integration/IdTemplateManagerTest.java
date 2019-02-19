package io.mosip.authentication.service.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PDFGeneratorImpl.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
public class IdTemplateManagerTest {

	private static final String OTP_SMS_TEMPLATE_TXT = "otp-sms-template.txt";

	@InjectMocks
	private IdTemplateManager idTemplateManager;

	@InjectMocks
	private MasterDataManager masterDataManager;

	@Mock
	private TemplateManager templateManager;

	@Mock
	private PDFGenerator pdfGenerator;

	@Autowired
	private PDFGenerator actpdfGenerator;

	@Autowired
	private Environment environment;

	@InjectMocks
	private IdInfoHelper idInfoHelper;

	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private RestHelper restHelper;

	private final String value = "OTP for UIN  $uin is $otp and is valid for $validTime minutes. (Generated at $datetimestamp)";

	@Before
	public void before() {
		ReflectionTestUtils.setField(idTemplateManager, "masterDataManager", masterDataManager);
		ReflectionTestUtils.setField(masterDataManager, "environment", environment);
		ReflectionTestUtils.setField(masterDataManager, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(masterDataManager, "restFactory", restFactory);
		ReflectionTestUtils.setField(masterDataManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(restFactory, "env", environment);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidApplyTemplate() throws IOException, IdAuthenticationBusinessException {
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
		Map<String, Object> valueMap = new HashMap<>();
		idTemplateManager.applyTemplate("otp-sms-template", valueMap);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestTemplateResourceNotFoundException() throws IOException, IdAuthenticationBusinessException {
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IOException());
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		idTemplateManager.applyTemplate("test", valueMap);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestApplyTemplate() throws IOException, IdAuthenticationBusinessException {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(OTP_SMS_TEMPLATE_TXT);
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		assertNotNull(idTemplateManager.applyTemplate(OTP_SMS_TEMPLATE_TXT, valueMap));
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidgeneratePDF() throws IdAuthenticationBusinessException {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		idTemplateManager.generatePDF("test", valueMap);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testPdfGenerationWithInputStream() throws IOException, IdAuthenticationBusinessException {
		ClassLoader classLoader = getClass().getClassLoader();
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		String inputFileName = classLoader.getResource("templates/" + OTP_SMS_TEMPLATE_TXT).getFile();
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line.trim());
		}
		ByteArrayOutputStream bos = (ByteArrayOutputStream) actpdfGenerator.generate(sb.toString());
		Mockito.when(pdfGenerator.generate(Mockito.any(InputStream.class))).thenReturn(bos);
		String outputPath = System.getProperty("user.dir");
		String fileSepetator = System.getProperty("file.separator");
		File OutPutPdfFile = new File(outputPath + fileSepetator + "testekyclimited.pdf");
		FileOutputStream op = new FileOutputStream(OutPutPdfFile);
		op.write(bos.toByteArray());
		op.flush();
		assertTrue(OutPutPdfFile.exists());
		if (op != null) {
			op.close();

		}
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		idTemplateManager.generatePDF(OTP_SMS_TEMPLATE_TXT, valueMap);
	}

	@Test(expected = FileNotFoundException.class)
	public void testInvalidPdfGeneration() throws IOException {
		InputStream is = new FileInputStream("dummy1.html");
		ByteArrayOutputStream bos = (ByteArrayOutputStream) pdfGenerator.generate(is);
		String outputPath = System.getProperty("user.dir");
		String fileSepetator = System.getProperty("file.separator");
		File OutPutPdfFile = new File(outputPath + fileSepetator + "testekyclimited.pdf");
		FileOutputStream op = new FileOutputStream(OutPutPdfFile);
		op.write(bos.toByteArray());
		op.flush();
		assertTrue(OutPutPdfFile.exists());
		if (op != null) {
			op.close();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidgeneratePdf() throws IOException, IdAuthenticationBusinessException {
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		Mockito.when(pdfGenerator.generate(Mockito.any(InputStream.class))).thenThrow(new IOException());
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		idTemplateManager.generatePDF("test", valueMap);
	}

	@AfterClass
	public static void deleteOutputFile() {
		String outputFilePath = System.getProperty("user.dir");
		String outputFileExtension = ".pdf";
		String fileSepetator = System.getProperty("file.separator");
		File file = new File(outputFilePath + fileSepetator + "testekyclimited" + outputFileExtension);
		if (file.exists()) {
			file.delete();
		}
	}

}
