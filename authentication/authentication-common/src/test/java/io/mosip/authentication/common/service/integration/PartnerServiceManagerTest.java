package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
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
	
	String obj = "{\"policyId\":\"21\",\"policyName\":\"\",\"policyDescription\":\"Desc about policy\",\"policyStatus\":true,\"policy\":{\"policyId\":\"21\",\"policies\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"partnerId\":\"10302\",\"partnerName\":\"Idea\"}";

	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S1() throws RestServiceException, IdAuthenticationBusinessException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
		.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(getResponse());
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551", "bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).
		thenThrow(NullPointerException.class);	
	}

	// MISP license key not exists
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S2() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,
						getErrorResponse("PMS_PMP_020", "MISP license key not exists."),
						mapper.readValue(getErrorResponse("PMS_PMP_020", "MISP license key not exists.").getBytes(),
								java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// MISP license key is expired.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S3() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,
						getErrorResponse("PMS_PMP_021", "MISP license key is expired."),
						mapper.readValue(getErrorResponse("PMS_PMP_021", "MISP license key is expired.").getBytes(),
								java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// License key of MISP is blocked.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S4() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_025", "License key of MISP is blocked");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// Partner is not active.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S5() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_016", "Partner is not active.");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// Partner does not exist.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S6() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_013", "Partner does not exist");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// Partner is not mapped to any policy.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S8() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_017", "Partner is not mapped to any policy.");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// Policy is not active..
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S9() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_023", "Policy is not active.");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// "Partner policy is not active.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S10() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_019", "Partner policy is not active.");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// Partner policy got expired.
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S11() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_018", "Partner policy got expired.");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);

	}

	// Policy file is corrupted
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S7() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_024", "Policy file is corrupted");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);
	}

	// Default error
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S12() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getErrorResponse("PMS_PMP_124", "Unexpected error");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);
	}

	// No error code in response
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S13() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getNoErrorResponse("PMS_PMP_124", "Unexpected error");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);
	}

	// No response body
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S14() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {
		String errorResponse = getNoResponse("PMS_PMP_124", "Unexpected error");
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, errorResponse,
						mapper.readValue(errorResponse.getBytes(), java.util.Map.class)));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);
	}

	// No response body
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyTest_S15() throws RestServiceException, IdAuthenticationBusinessException,
			JsonParseException, JsonMappingException, IOException {

		RestRequestDTO restRequestDTO = new RestRequestDTO();
		PartnerPolicyResponseDTO response = null;
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR, null, null));
		Mockito.when(partnerServiceManager.validateAndGetPolicy("320216", "76185551",
				"bac3EYy8uLmKsSnR9N2whtJh66u3DEEUm4wnXgVSdk1bFqeNnA")).thenReturn(response);
	}

	private Map<String, Object> getResponse1() {
		LinkedHashMap<String, Object> response = new LinkedHashMap<>();
		Object obj = "{\r\n" + "        \"policyId\": \"21\",\r\n" + "        \"policyName\": null,\r\n"
				+ "        \"policyDescription\": \"Desc about policy\",\r\n" + "        \"policyStatus\": true,\r\n"
				+ "        \"policy\": {\r\n" + "            \"policyId\": \"21\",\r\n"
				+ "            \"policies\": {\r\n" + "                \"authPolicies\": [\r\n"
				+ "                    {\r\n" + "                        \"authType\": \"otp\",\r\n"
				+ "                        \"authSubType\": null,\r\n"
				+ "                        \"mandatory\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"authType\": \"demo\",\r\n"
				+ "                        \"authSubType\": null,\r\n"
				+ "                        \"mandatory\": false\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"authType\": \"bio\",\r\n"
				+ "                        \"authSubType\": \"FINGER\",\r\n"
				+ "                        \"mandatory\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"authType\": \"bio\",\r\n"
				+ "                        \"authSubType\": \"IRIS\",\r\n"
				+ "                        \"mandatory\": false\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"authType\": \"bio\",\r\n"
				+ "                        \"authSubType\": \"FACE\",\r\n"
				+ "                        \"mandatory\": false\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"authType\": \"kyc\",\r\n"
				+ "                        \"authSubType\": null,\r\n"
				+ "                        \"mandatory\": false\r\n" + "                    }\r\n"
				+ "                ],\r\n" + "                \"allowedKycAttributes\": [\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"fullName\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"dateOfBirth\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"gender\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"phone\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"email\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"addressLine1\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"addressLine2\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"addressLine3\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"location1\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"location2\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"location3\",\r\n"
				+ "                        \"required\": true\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"postalCode\",\r\n"
				+ "                        \"required\": false\r\n" + "                    },\r\n"
				+ "                    {\r\n" + "                        \"attributeName\": \"photo\",\r\n"
				+ "                        \"required\": true\r\n" + "                    }\r\n"
				+ "                ]\r\n" + "            }\r\n" + "        },\r\n"
				+ "        \"partnerId\": \"320216\",\r\n" + "        \"partnerName\": \"Vodafone\"\r\n" + "    }";
		response.put("response", obj);
		response.entrySet();
		return response;
	}

	private Map<String, Object> getResponse() {
		Map<String, Object> response = new HashMap<>();
		// String obj="{policyId=21, policyName=test, policyDescription=Desc about
		// policy, policyStatus=true, policy={policyId=21,
		// policies={authPolicies=[{authType=otp, authSubType=null, mandatory=true}],
		// allowedKycAttributes=[{attributeName=fullName, required=true}]}},
		// partnerId=10302, partnerName=Idea}";
		// Object obj = "{policyId=21, policyName=null, policyDescription=Desc about
		// policy, policyStatus=true, policy={policyId=21,
		// policies={authPolicies=[{authType=otp, authSubType=null, mandatory=true},
		// {authType=demo, authSubType=null, mandatory=false}, {authType=bio,
		// authSubType=FINGER, mandatory=true}, {authType=bio, authSubType=IRIS,
		// mandatory=false}, {authType=bio, authSubType=FACE, mandatory=false},
		// {authType=kyc, authSubType=null, mandatory=false}],
		// allowedKycAttributes=[{attributeName=fullName, required=true},
		// {attributeName=dateOfBirth, required=true}, {attributeName=gender,
		// required=true}, {attributeName=phone, required=true}, {attributeName=email,
		// required=true}, {attributeName=addressLine1, required=true},
		// {attributeName=addressLine2, required=true}, {attributeName=addressLine3,
		// required=true}, {attributeName=location1, required=true},
		// {attributeName=location2, required=true}, {attributeName=location3,
		// required=true}, {attributeName=postalCode, required=false},
		// {attributeName=photo, required=true}]}}, partnerId=10302, partnerName=Idea}";
		String obj = "{\"policyId\":\"21\",\"policyName\":null,\"policyDescription\":\"Desc about policy\",\"policyStatus\":true,\"policy\":{\"policyId\":\"21\",\"policies\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":null,\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":null,\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":null,\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"partnerId\":\"10302\",\"partnerName\":\"Idea\"}";
		response.put("response", obj.toString());
		return response;
	}

	private String getErrorResponse(String errorCode, String errorMessage) {

		String response = "{\"id\":\"mosip.partnermanagement.partners.retrieve\",\"version\":\"1.0\",\"responsetime\":\"2020-06-10T16:53:14.292,Z\",\"metadata\":null,\"response\":null,\"errors\":\r\n"
				+ "[{\"errorCode\":\"wrongC\",\"message\":\"errorm\"}]}";
		return response.replace("wrongC", errorCode).replace("errorm", errorMessage);
	}

	private String getNoErrorResponse(String errorCode, String errorMessage) {

		String response = "{\"id\":\"mosip.partnermanagement.partners.retrieve\",\"version\":\"1.0\",\"responsetime\":\"2020-06-10T16:53:14.292,Z\",\"metadata\":null,\"response\":null,\"error\":\r\n"
				+ "[{\"errorCode\":\"wrongC\",\"message\":\"errorm\"}]}";
		return response.replace("wrongC", errorCode).replace("errorm", errorMessage);
	}

	private String getNoResponse(String errorCode, String errorMessage) {

		String response = "{\"id\":\"mosip.partnermanagement.partners.retrieve\",\"version\":\"1.0\",\"responsetime\":\"2020-06-10T16:53:14.292,Z\",\"metadata\":null,\"respons\":null,\"error\":\r\n"
				+ "[{\"errorCode\":\"wrongC\",\"message\":\"errorm\"}]}";
		return response.replace("wrongC", errorCode).replace("errorm", errorMessage);
	}
}
