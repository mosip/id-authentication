package io.mosip.authentication.service.impl.otpgen.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import io.mosip.authentication.service.impl.id.service.impl.IdInfoServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;

/**
 * Test class for KycServiceImpl.
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@TestPropertySource("classpath:sample-output-test.properties")
public class KycServiceImplTest {
	
	@Autowired
	Environment env;
	
	IdInfoServiceImpl idInfoServiceImpl = new IdInfoServiceImpl();
	
	@InjectMocks
	private KycServiceImpl kycServiceImpl;
	
	@Value("${sample.demo.entity}")
	String value;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "idInfoServiceImpl", idInfoServiceImpl);
		ReflectionTestUtils.setField(idInfoServiceImpl, "value", value);
	}
	
	@Test
	public void validUIN() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121",KycType.FULL, true, false);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN2() {
		try {
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323",KycType.FULL, true, false);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
