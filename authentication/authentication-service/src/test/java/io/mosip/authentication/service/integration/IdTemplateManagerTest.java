package io.mosip.authentication.service.integration;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PDFGeneratorImpl.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class })
public class IdTemplateManagerTest {

	@InjectMocks
	IdTemplateManager idTemplateManager;

	@Mock
	private TemplateManager templateManager;

	@Autowired
	private PDFGenerator pdfGenerator;

	private final String value = "OTP for UIN  $uin is $otp and is valid for $validTime minutes. (Generated at $datetimestamp)";

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidApplyTemplate() throws IOException, IdAuthenticationBusinessException {
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
		Map<String, Object> valueMap = new HashMap<>();
		idTemplateManager.applyTemplate("test", valueMap);
	}

	@Test(expected = IOException.class)
	public void TestTemplateResourceNotFoundException() throws IOException, IdAuthenticationBusinessException {
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IOException());
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		idTemplateManager.applyTemplate("test", valueMap);
	}

	@Test
	public void TestApplyTemplate() throws IOException, IdAuthenticationBusinessException {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("otp-sms-template.txt");
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		assertNotNull(idTemplateManager.applyTemplate("otp-sms-template.txt", valueMap));
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

	@Ignore
	@Test
	public void TestvalidgeneratePDF() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("application.properties").getFile();
		InputStream is = new FileInputStream(inputFile);
		ByteArrayOutputStream bos = (ByteArrayOutputStream) pdfGenerator.generate(is);
		OutputStream outputStream = idTemplateManager.generatePDF("otp-sms-template.txt", valueMap);
		System.err.println(outputStream);
	}

}
