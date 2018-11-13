package io.mosip.authentication.service.impl.otpgen.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
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
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.integration.IdTemplateManager;

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
	
	IdInfoServiceImpl idInfoServiceImpl = new IdInfoServiceImpl();
	
	@InjectMocks
	private KycServiceImpl kycServiceImpl;
	
	@Mock
	IdTemplateManager idTemplateManager;
	
	@Value("${sample.demo.entity}")
	String value;
	
	@Before
	public void before() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("eKycPDFTemplate");
		ReflectionTestUtils.setField(kycServiceImpl, "messageSource", source);
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "idInfoServiceImpl", idInfoServiceImpl);
		ReflectionTestUtils.setField(idInfoServiceImpl, "value", value);
		ReflectionTestUtils.setField(kycServiceImpl, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(kycServiceImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(demoHelper, "environment", environment);
		ReflectionTestUtils.setField(demoHelper, "idMappingConfig", idMappingConfig);
		
	}
	
	@Test
	public void validUIN() {
		try {
			Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.anyMap())).thenReturn("pdf generated");
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN2() {
		try {
			Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.anyMap())).thenReturn("pdf generated");
			KycInfo k = kycServiceImpl.retrieveKycInfo("1223232345665", KycType.FULL, true, true);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
