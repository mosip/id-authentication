package io.mosip.authentication.common.service.impl.patrner;

import static org.junit.Assert.assertNotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class PartnerServiceImplTest {

	@InjectMocks
	private PartnerServiceImpl partnerService;

	@Mock
	private PartnerServiceManager partnerManager;

	@Mock
	private ObjectMapper mapper;

	@Before
	public void before() {
	}

	@Test
	public void testVallidateAndGetPolicy() throws IdAuthenticationBusinessException {
		partnerService.validateAndGetPolicy("partner_id", "partner_api_key", "misp_license_key", false, "partner_header_certificate", false);
	}

	private PartnerPolicyResponseDTO getPolicyData() {
		PartnerPolicyResponseDTO response = new PartnerPolicyResponseDTO();
		response.setPartnerId("12345678");
		response.setPartnerName("Test");
		response.setPolicyId("345678");
		response.setMispExpiresOn(LocalDateTime.now().plus(Duration.ofMinutes(5)));
		response.setApiKeyExpiresOn(LocalDateTime.now().plus(Duration.ofMinutes(5)));
		response.setPolicyExpiresOn(LocalDateTime.now().plus(Duration.ofMinutes(5)));
		return response;
	}

	@Test
	public void getPartnerTest() throws IdAuthenticationBusinessException {
		String partnerId = "123";
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("key1", "value1");
		metadata.put("key2", "value2");
		metadata.put("key3", "value3");
		assertNotNull(partnerService.getPartner(partnerId, metadata));
	}

	@Test
	public void getPolicyForPartnerTest() throws IdAuthenticationBusinessException {
		String partnerId = "123";
		String partnerApiKey = "1234";
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("key1", "value1");
		metadata.put("key2", "value2");
		metadata.put("key3", "value3");
		assertNotNull(partnerService.getPolicyForPartner(partnerId, partnerApiKey, metadata));
	}

}