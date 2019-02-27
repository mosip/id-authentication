package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.service.integration.KeyManager;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthFilterTest {

	@Autowired
	Environment env;

	InternalAuthFilter internalAuthFilter = new InternalAuthFilter();
	
	Map<String, Object> requestBody = new HashMap<>();

	Map<String, Object> responseBody = new HashMap<>();

	@Autowired
	ObjectMapper mapper;

	@Before
	public void before() {
		ReflectionTestUtils.setField(internalAuthFilter, "mapper", mapper);
		ReflectionTestUtils.setField(internalAuthFilter, "env", env);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValidDecodedRequest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(internalAuthFilter, "keyManager", keyManager);
		requestBody.put("request",
				"ew0KCSJhdXRoVHlwZSI6IHsNCgkJImFkZHJlc3MiOiAidHJ1ZSIsDQoJCSJiaW8iOiAidHJ1ZSIsDQoJCSJmYWNlIjogInRydWUiLA0KCQkiZmluZ2VycHJpbnQiOiAidHJ1ZSIsDQoJCSJmdWxsQWRkcmVzcyI6ICJ0cnVlIiwNCgkJImlyaXMiOiAidHJ1ZSIsDQoJCSJvdHAiOiAidHJ1ZSIsDQoJCSJwZXJzb25hbElkZW50aXR5IjogInRydWUiLA0KCQkicGluIjogInRydWUiDQoJfQ0KfQ==");
		responseBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		Mockito.when(keyManager.requestData(Mockito.any(), Mockito.any())).thenReturn(new ObjectMapper().readValue(
				"{\"authType\":{\"address\":\"true\",\"bio\":\"true\",\"face\":\"true\",\"fingerprint\":\"true\",\"fullAddress\":\"true\",\"iris\":\"true\",\"otp\":\"true\",\"personalIdentity\":\"true\",\"pin\":\"true\"}}"
						.getBytes(),
				Map.class));
		assertEquals(responseBody.toString(), internalAuthFilter.decipherRequest(requestBody).toString());

	}

	@Test
	public void testValidEncodedRequest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		requestBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		responseBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		assertEquals(requestBody.toString(), internalAuthFilter.encipherResponse(responseBody).toString());

	}

	@Test
	public void testTxnId() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			IOException, NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		Map<String, Object> reqValue = mapper.readValue("{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"3016379867\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-26T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> resValue = mapper.readValue("{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-26T09:19:11.110+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-26T08:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000000000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"}",
				new TypeReference<Map<String, Object>>() {
		});
		assertNotNull(internalAuthFilter.setResponseParams(reqValue,resValue));
	}
	
	@Test
	public void testSign() throws IdAuthenticationAppException {
		assertEquals(true, internalAuthFilter.validateSignature("something", "something".getBytes()));
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void testInValidDecodedRequest() throws IdAuthenticationAppException, JsonParseException, JsonMappingException, IOException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(internalAuthFilter, "keyManager", keyManager);
		requestBody.put("request",
				123214214);
		internalAuthFilter.decipherRequest(requestBody);
	}

}
