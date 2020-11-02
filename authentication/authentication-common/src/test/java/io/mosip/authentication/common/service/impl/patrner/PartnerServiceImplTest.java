package io.mosip.authentication.common.service.impl.patrner;

import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.cache.PartnerServiceCache;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})

public class PartnerServiceImplTest {	
	
	@InjectMocks
	private PartnerServiceImpl partnerService;
	
	@Mock
	private PartnerServiceCache partnerServiceCache;
	
	@Mock
	private CacheManager cacheManager;
	
	@Mock
	private Cache cache;
	
	@Before
	public void before() {
		when(cacheManager.getCache(Mockito.any())).thenReturn(cache);
		when(cache.getNativeCache()).thenReturn(new HashMap<>());
	}
	
	@Test
	public void testGetPartner() throws Exception {
		String partnerId = "12345678";		
		Mockito.when(partnerServiceCache.getPartnerPolicy(Mockito.any(), Mockito.any())).thenReturn(getPolicyData());
		partnerService.getPartner(partnerId);
	}
	
	@Test
	public void testVallidateAndGetPolicy() throws IdAuthenticationBusinessException {
		Mockito.when(partnerServiceCache.getPartnerPolicy(Mockito.any(), Mockito.any())).thenReturn(getPolicyData());
		partnerService.validateAndGetPolicy("partner_id", "partner_api_key", "misp_license_key");
	}
	
	@Test
	public void testVallidateAndGetPolicy_S001() throws IdAuthenticationBusinessException {
		String partnerId = "12345678";		
		Mockito.when(partnerServiceCache.getPartnerPolicy(Mockito.any(), Mockito.any())).thenReturn(getPolicyData());
		partnerService.validateAndGetPolicy("partner_id", "partner_api_key", "misp_license_key");
		partnerService.getPartner(partnerId);
	}
	
	private PartnerPolicyResponseDTO getPolicyData() {
		PartnerPolicyResponseDTO response = new PartnerPolicyResponseDTO();
		response.setPartnerId("12345678");
		response.setPartnerName("Test");
		response.setPolicyId("345678");
		return response;
	}
}