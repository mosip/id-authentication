package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
public class IdTemplateManagerTest {



	private static final String OTP_SMS_TEMPLATE_TXT = "otp-sms-template";

	@InjectMocks
	private IdTemplateManager idTemplateManager;

	@InjectMocks
	private MasterDataManager masterDataManager;

	@Mock
	private TemplateManager templateManager;

	@Mock
	private PDFGenerator pdfGenerator;

	@Autowired
	private Environment environment;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;

	@Mock
	private RestRequestFactory restFactory;

	@Mock
	private RestHelper restHelper;

	@InjectMocks
	private ObjectMapper mapper;
	@InjectMocks
	private TemplateManagerBuilderImpl templateManagerBuilder;
	
	@Mock
	private IdInfoFetcher idInfoFetcher;

	/** UTF type. */
	private static final String ENCODE_TYPE = "UTF-8";

	/** Class path. */
	private static final String CLASSPATH = "classpath";
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(idTemplateManager, "masterDataManager", masterDataManager);
		ReflectionTestUtils.setField(idTemplateManager, "environment", environment);
		ReflectionTestUtils.setField(idTemplateManager, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", environment);
		ReflectionTestUtils.setField(masterDataManager, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(restFactory, "env", environment);
		ReflectionTestUtils.setField(idTemplateManager, "templateManagerBuilder", templateManagerBuilder);
		templateManagerBuilder.encodingType(ENCODE_TYPE).enableCache(false).resourceLoader(CLASSPATH).build();
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidApplyTemplate() throws IOException, IdAuthenticationBusinessException, RestServiceException {
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any())).thenReturn(null);
		Mockito.when(idInfoFetcher.getLanguageCode(LanguageType.PRIMARY_LANG)).thenReturn("fra");
		Mockito.when(idInfoFetcher.getLanguageCode(LanguageType.SECONDARY_LANG)).thenReturn("ara");
		mockRestCalls();
		Map<String, Object> valueMap = new HashMap<>();
		idTemplateManager.applyTemplate("otp-sms-template", valueMap);
	}

	@Test
	public void Testpostconstruct() {
		idTemplateManager.idTemplateManagerPostConstruct();
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestTemplateResourceNotFoundException()
			throws IOException, IdAuthenticationBusinessException, RestServiceException {
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IOException());
		mockRestCalls();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		idTemplateManager.applyTemplate(OTP_SMS_TEMPLATE_TXT, valueMap);
	}

	@Test
	public void TestfetchTemplate() throws IdAuthenticationBusinessException, RestServiceException {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) environment));
		mockenv.setProperty("mosip.notification.language-type", "primary");
		ReflectionTestUtils.setField(idTemplateManager, "environment", mockenv);
		mockRestCalls();
		idTemplateManager.fetchTemplate("test");
	}
	@Test
	public void TestfetchTemplate_LangSecondary() throws IdAuthenticationBusinessException, RestServiceException {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) environment));
		mockenv.setProperty("mosip.notification.language-type", "BOTH");
		ReflectionTestUtils.setField(idTemplateManager, "environment", mockenv);
		mockRestCalls();
		idTemplateManager.fetchTemplate("otp-sms-template");
	}
	
	@Test
	public void TestInvalidLangtype() throws IdAuthenticationBusinessException, RestServiceException {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) environment));
		mockenv.setProperty("mosip.notification.language-type", "invalid");
		ReflectionTestUtils.setField(idTemplateManager, "environment", mockenv);
		mockRestCalls();
		idTemplateManager.fetchTemplate("test");
	}

	@Test
	public void TestApplyTemplate() throws IOException, IdAuthenticationBusinessException, RestServiceException {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin", "1234567890");
		valueMap.put("otp", "123456");
		valueMap.put("datetimestamp", "2018-11-20T12:02:57.086+0000");
		valueMap.put("validTime", "3");
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/otp-sms-template.txt");
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any())).thenReturn(is);
		mockRestCalls();
		assertNotNull(idTemplateManager.applyTemplate(OTP_SMS_TEMPLATE_TXT, valueMap));
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

	private void mockRestCalls() throws IDDataValidationException, RestServiceException {
		Mockito.when(restFactory.buildRequest(RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE, null, Map.class))
				.thenReturn(getRestRequestDTO());
		Mockito.when(restFactory.buildRequest(RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG, null, Map.class))
		.thenReturn(getRestRequestDTO_Multi());
		Map<String, List<Map<String, Object>>> valuemap = new HashMap<>();
		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> actualMap = new HashMap<>();
		actualMap.put("fileText",
				"OTP pour UIN $uin est $otp et est valide pour $validTime minutes. (Généré le $date à $time Hrs)");
		actualMap.put("langCode","ara");
		actualMap.put("templateTypeCode",OTP_SMS_TEMPLATE_TXT);
		actualMap.put("isActive", true);
		finalList.add(actualMap);
		finalList.add(actualMap);
		valuemap.put("templates", finalList);
		Map<String, Map<String, List<Map<String, Object>>>> finalMap = new HashMap<>();
		finalMap.put("response", valuemap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(finalMap);
	}

	private RestRequestDTO getRestRequestDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("https://integ.mosip.io/masterdata/v1.0/templates/{langcode}/{templatetypecode}");
		restRequestDTO.setResponseType(Map.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}
	private RestRequestDTO getRestRequestDTO_Multi() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("https://integ.mosip.io/masterdata/v1.0/templates/templatetypecodes/{code}");
		restRequestDTO.setResponseType(Map.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

}
