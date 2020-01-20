package io.mosip.authentication.common.service.impl.patrner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

//This covers the negative tests as positive tests are covered by the tests of the target class's caller classes.
public class PartnerServiceImplTest {
	
	protected Environment env = Mockito.mock(Environment.class);
	protected ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
	private PartnerServiceImpl partnerService = new PartnerServiceImpl();
	
	@Before
	public void init() {
		ReflectionTestUtils.setField(partnerService, "env", env);
		ReflectionTestUtils.setField(partnerService, "mapper", mapper);
	}


	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetPolicy() throws Exception {
		String policyId = "";

		// default test
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn("abc");
		Mockito.when(mapper.readValue(Mockito.any(byte[].class), Mockito.any(Class.class))).thenThrow(new IOException());
		partnerService.getPolicy(policyId);
	}

	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetPartner() throws Exception {
		String partnerId = "";

		// default test
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn("abc");
		Mockito.when(mapper.readValue(Mockito.any(byte[].class), Mockito.any(Class.class))).thenThrow(new IOException());
		partnerService.getPartner(partnerId);
	}

	@Test
	public void testGetMispPartnerMapping() throws Exception {
		String partnerId = "abc";
		String mispId = "123";

		// default test
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn("abc");
		Mockito.when(env.getProperty("misp.partner.mapping." + mispId  +"." + partnerId, boolean.class, false)).thenReturn(true);
		Mockito.when(mapper.readValue(Mockito.any(byte[].class), Mockito.any(Class.class))).thenThrow(new IOException());

		assertTrue(partnerService.getMispPartnerMapping(partnerId, mispId));
	}

	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetLicense() throws Exception {
		String licenseKey = "";

		// default test
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn("abc");
		Mockito.when(mapper.readValue(Mockito.any(byte[].class), Mockito.any(Class.class))).thenThrow(new IOException());
		partnerService.getLicense(licenseKey);
	}
}