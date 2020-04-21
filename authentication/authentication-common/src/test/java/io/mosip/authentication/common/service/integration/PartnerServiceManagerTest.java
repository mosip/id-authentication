package io.mosip.authentication.common.service.integration;

import java.util.HashMap;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class PartnerServiceManagerTest {
	
	@Mock
	private RestHelper restHelper;

	@Mock
	private RestRequestFactory restRequestFactory;
	
	@Autowired
	ConfigurableEnvironment env;
	
	@Autowired
    private ObjectMapper mapper;
	
	@InjectMocks
	private PartnerServiceManager partnerServiceManager;
	
	@Mock
	private PartnerServiceManager partnerServiceManagerMock;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(partnerServiceManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(partnerServiceManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(partnerServiceManager, "mapper", mapper);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	public void validateAndGetPolicyTest_S1() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
		.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(getResponse());
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551", "bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).
		thenThrow(NullPointerException.class);		
	}
	
	@Test
	public void validateAndGetPolicyTest_S2() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
		.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(getErrorResponse());
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551", "bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).
		thenReturn(response);
	}

	private HashMap<String, Object> getResponse() {
		HashMap<String, Object> object = new HashMap<String, Object>();  
		 Object obj =	"{\r\n" + 
				"    \"id\": null,\r\n" + 
				"    \"version\": null,\r\n" + 
				"    \"responsetime\": \"2020-04-14T09:34:20.179Z\",\r\n" + 
				"    \"metadata\": null,\r\n" + 
				"    \"response\": {\r\n" + 
				"        \"policyId\": \"21\",\r\n" + 
				"        \"policyName\": null,\r\n" + 
				"        \"policyDescription\": \"Desc about policy\",\r\n" + 
				"        \"policyStatus\": true,\r\n" + 
				"        \"policy\": {\r\n" + 
				"            \"policyId\": \"21\",\r\n" + 
				"            \"policies\": {\r\n" + 
				"                \"authPolicies\": [\r\n" + 
				"                    {\r\n" + 
				"                        \"authType\": \"otp\",\r\n" + 
				"                        \"authSubType\": null,\r\n" + 
				"                        \"mandatory\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"authType\": \"demo\",\r\n" + 
				"                        \"authSubType\": null,\r\n" + 
				"                        \"mandatory\": false\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"authType\": \"bio\",\r\n" + 
				"                        \"authSubType\": \"FINGER\",\r\n" + 
				"                        \"mandatory\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"authType\": \"bio\",\r\n" + 
				"                        \"authSubType\": \"IRIS\",\r\n" + 
				"                        \"mandatory\": false\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"authType\": \"bio\",\r\n" + 
				"                        \"authSubType\": \"FACE\",\r\n" + 
				"                        \"mandatory\": false\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"authType\": \"kyc\",\r\n" + 
				"                        \"authSubType\": null,\r\n" + 
				"                        \"mandatory\": false\r\n" + 
				"                    }\r\n" + 
				"                ],\r\n" + 
				"                \"allowedKycAttributes\": [\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"fullName\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"dateOfBirth\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"gender\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"phone\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"email\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"addressLine1\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"addressLine2\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"addressLine3\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"location1\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"location2\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"location3\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"postalCode\",\r\n" + 
				"                        \"required\": false\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"attributeName\": \"photo\",\r\n" + 
				"                        \"required\": true\r\n" + 
				"                    }\r\n" + 
				"                ]\r\n" + 
				"            }\r\n" + 
				"        },\r\n" + 
				"        \"partnerId\": \"320216\",\r\n" + 
				"        \"partnerName\": \"Vodafone\"\r\n" + 
				"    },\r\n" + 
				"    \"errors\": null\r\n" + 
				"}";
		
		 object.put("serviceResponse", obj);
		return object;
	}
    
	private HashMap<String, Object> getErrorResponse(){
		HashMap<String, Object> object = new HashMap<String, Object>(); 
		Object obj ="{\r\n" + 
				"    \"id\": \"mosip.partnermanagement.partners.retrieve\",\r\n" + 
				"    \"version\": \"1.0\",\r\n" + 
				"    \"responsetime\": \"2020-04-14T10:42:42.004Z\",\r\n" + 
				"    \"metadata\": null,\r\n" + 
				"    \"response\": null,\r\n" + 
				"    \"errors\": [\r\n" + 
				"        {\r\n" + 
				"            \"errorCode\": \"PMS_PMP_021\",\r\n" + 
				"            \"message\": \"MISP license key is expired.\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		 object.put("serviceResponse", obj);
		return object;
	}
}
