package io.mosip.authentication.service.impl.otpgen.service;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;

/**
 * Test class for KycServiceImpl.
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@Import(IDAMappingConfig.class)
@TestPropertySource("classpath:sample-output-test.properties")
public class KycServiceImplTest {
	
	@Autowired
	Environment env;
	
	@Autowired
	Environment environment;
	
	@InjectMocks
	private DemoHelper demoHelper;
	
	@Autowired
	private IDAMappingConfig idMappingConfig;
	
	IdInfoService idInfoService = new IdInfoServiceImpl();
	
	@InjectMocks
	private KycServiceImpl kycServiceImpl;
	
	IdAuthService idAuthService = new IdAuthServiceImpl();
	
	IdTemplateManager idTemplateManager = new IdTemplateManager();
	
	PDFGenerator pdfGenerator = new PDFGeneratorImpl();
	
	@Value("${sample.demo.entity}")
	String value;
	
	@Before
	public void before() {
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
		
	}
	
	@Test
	public void validUIN() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN1() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, true);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN2() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, false, false);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void validUIN3() {
		try {			
			KycInfo k = kycServiceImpl.retrieveKycInfo("1223232345665", KycType.FULL, true, true);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN4() {
		try {			
			KycInfo k = kycServiceImpl.retrieveKycInfo("1223232345665", KycType.FULL, true, false);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN5() {
		try {			
			KycInfo k = kycServiceImpl.retrieveKycInfo("1223232345665", KycType.FULL, false, false);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

}
