package io.mosip.authentication.common.service.integration;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.repository.ApiKeyDataRepository;
import io.mosip.authentication.common.service.repository.MispLicenseDataRepository;
import io.mosip.authentication.common.service.repository.PartnerDataRepository;
import io.mosip.authentication.common.service.repository.PartnerMappingRepository;
import io.mosip.authentication.common.service.repository.PolicyDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;

/**
 * Test class for PartnerServiceManager
 * 
 * @author Nagarjuna
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class PartnerServiceManagerTest {

	@Autowired
	ConfigurableEnvironment env;

	@Mock
	private PartnerMappingRepository partnerMappingRepo;

	@Mock
	private PartnerDataRepository partnerDataRepo;

	@Mock
	private PolicyDataRepository policyDataRepo;

	@Mock
	private ApiKeyDataRepository apiKeyRepo;

	@Mock
	private MispLicenseDataRepository mispLicDataRepo;

	@Autowired
	private ObjectMapper mapper;

	@Mock
	private IdAuthSecurityManager securityManager;

	@InjectMocks
	private PartnerServiceManager partnerServiceManager;

	@Mock
	private PartnerServiceManager partnerServiceManagerMock;

	@Before
	public void before() {
		ReflectionTestUtils.setField(partnerServiceManager, "mapper", mapper);
	}
	
	String obj = "{\"policyId\":\"21\",\"policyName\":\"\",\"policyDescription\":\"Desc about policy\",\"policyStatus\":true,\"policy\":{\"policyId\":\"21\",\"policies\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"partnerId\":\"10302\",\"partnerName\":\"Idea\"}";

	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S1() throws RestServiceException, IdAuthenticationBusinessException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551", "bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).
		thenThrow(NullPointerException.class);	
		partnerServiceManager.validateAndGetPolicy("320216", "76185551", "bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);
	}

	// MISP license key not exists
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S2() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// MISP license key is expired.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S3() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// License key of MISP is blocked.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S4() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// Partner is not active.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S5() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// Partner does not exist.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S6() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// Partner is not mapped to any policy.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S8() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// Policy is not active..
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S9() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// "Partner policy is not active.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S10() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// Partner policy got expired.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S11() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);

	}

	// Default error
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S12() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		PartnerPolicyResponseDTO response = null;
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false)).thenReturn(response);
		partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA", false);
	}
}
