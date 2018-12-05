package io.mosip.authentication.service.impl.otpgen.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdRepoServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for KycServiceImpl.
 *
 * @author Sanjay Murali
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class, TemplateManagerBuilderImpl.class })
@WebMvcTest
@Import(IDAMappingConfig.class)
@TestPropertySource("classpath:sample-output-test.properties")
public class KycServiceImplTest {
	
	@Autowired
	Environment env;
	
	@Autowired
	Environment environment;
	
	@InjectMocks
	private IdInfoHelper demoHelper;
	
	@Autowired
	private IDAMappingConfig idMappingConfig;
	
	IdRepoService idInfoService = new IdRepoServiceImpl();
	
	@InjectMocks
	private KycServiceImpl kycServiceImpl;
	
	IdAuthService idAuthService = new IdAuthServiceImpl();
	
	@Autowired
	IdTemplateManager idTemplateManager;
	
	PDFGenerator pdfGenerator = new PDFGeneratorImpl();
	
	@Value("${sample.demo.entity}")
	String value;
	
	Map<String, List<IdentityInfoDTO>> idInfo;
	
	@Before
	public void before() throws IdAuthenticationDaoException {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("eKycPDFTemplate");
		ReflectionTestUtils.setField(kycServiceImpl, "messageSource", source);
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "idInfoService", idInfoService);
		ReflectionTestUtils.setField(kycServiceImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(idInfoService, "value", value);
		ReflectionTestUtils.setField(kycServiceImpl, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(kycServiceImpl, "pdfGenerator", pdfGenerator);
		ReflectionTestUtils.setField(kycServiceImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(demoHelper, "environment", environment);
		ReflectionTestUtils.setField(demoHelper, "idMappingConfig", idMappingConfig);
		idInfo = idInfoService.getIdInfo("12232323121");
		
	}
	
	@Ignore
	@Test
	public void validUIN() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void validUIN1() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);		
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	@Test
	public void validUIN2() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, true, idInfo);	
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	@Test
	public void validUIN3() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.FULL, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	@Test
	public void validUIN4() {
		try {	
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.FULL, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	@Test
	public void validUIN5() throws IdAuthenticationDaoException {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.FULL, true, true, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

}
