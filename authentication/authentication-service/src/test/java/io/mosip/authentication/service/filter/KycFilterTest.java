package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
public class KycFilterTest {

	@Autowired
	Environment env;

	KycAuthFilter kycAuthFilter = new KycAuthFilter();

	@Autowired
	ObjectMapper mapper;
	
	@Mock
	KeyManager keyManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(kycAuthFilter, "mapper", mapper);
		ReflectionTestUtils.setField(kycAuthFilter, "env", env);
		ReflectionTestUtils.setField(kycAuthFilter, "keyManager", keyManager);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValidDecipherRequest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		Method decodeMethod = KycAuthFilter.class.getDeclaredMethod("decipherRequest", Map.class);
		decodeMethod.setAccessible(true);
		Map<String, Object> readValue = mapper.readValue("{\"authRequest\":\"ewogICJhdXRoVHlwZSI6IHsKICAgICJhZGRyZXNzIjogZmFsc2UsCiAgICAiYmlvIjogdHJ1ZSwKICAgICJvdHAiOiBmYWxzZSwKICAgICJwZXJzb25hbElkZW50aXR5IjogZmFsc2UsCiAgICAicGluIjogZmFsc2UKICB9LAogICJiaW9JbmZvIjogWwogICAgewogICAgICAiYmlvVHlwZSI6ICJmZ3JNaW4iLAogICAgICAiZGV2aWNlSW5mbyI6IHsKICAgICAgICAiZGV2aWNlSWQiOiAiMTIzMTQzIiwKICAgICAgICAibWFrZSI6ICJtYW50cmEiLAogICAgICAgICJtb2RlbCI6ICJzdGVlbCIKICAgICAgfQogICAgfQogIF0sCiAgImlkIjogIm1vc2lwLmlkZW50aXR5LmF1dGgiLAogICJpZHZJZCI6ICI1NzA2Mjc0OTE1IiwKICAiaWR2SWRUeXBlIjogIkQiLAogICJ0c3BJRCI6ICJzdHJpbmciLAogICJrZXkiOiB7CiAgICAicHVibGljS2V5Q2VydCI6ICJTVFJJTkciLAogICAgInNlc3Npb25LZXkiOiAiU3ltV0c1UkNaNXFPeWJNeHJIRXZEMTNrWTU1Y0c4aUpFam9kczJSVHF0dzh6Zjd1eDVTaEhacEZpdWpaWnVYTzdRNDlmYy01eFBXamh4UFIwTnFpcm9DRXpEZllTWUxpSTFtTkUxTHVjZEhPeHU3NXRnWmZPenJsbXY0ZlU2a3BadzZhcWo0TnJmMHZvNU9BVEVLVFdvUHBfR1RtLU1GSm12UmhuaGk3eHZ3bHJyOEQwZDhjTUxVMEFDcy1QdU94X2NubTBQMGtKTU9WVjZodXhESnlfTHItbzRJZnRVWGZZd3JyZDNDUkNXRzlaWW9sdENqTUZpdGM0Mzc4TWRuc0RJYk5fT0wzb29BTzZ4NVhPNm9JdHozWmZzS1FlT3ZtTG9VanYzRm9VNkVDaHlZWk9aZzB0NGpxWDA1d1pHYjB0dXhuRVJ5YXl3ZDcyLWoza3Z0ZWFBIgogIH0sCiAgInJlcVRpbWUiOiAiMjAxOS0wMi0yNVQxNToxNToyMy4wMjcrMDU6MzAiLAogICJyZXF1ZXN0IjogIkZfU3YwdUVzNkpEMjdVSl9UaXdIRGdEeFlZY2JScWJKUFlYNktXc0E5YXd4RHNpWU41b2VwQ2VINHY3U01rUk90dllJN2M0UXB2V0ZyWWVqQ2hFSURrV3EwSXBSR1RpcXJLTE9LRlJqWVhGSlQtUkJ0UnhTTUJKekw4SUhUMUMtbVRFNmZFbG13UFdHLXZNbDJJUDQzTWNqRWxtNk9sdXhyRDlvSW41X1hmWXhxY1dhc0h1X0pEOHJ3Ui1QdkJqUEdWN1o3WGhGQlBnVmVVWXZuTDVScUdhYzJweDlZSGVQa0hZMlkyZW13NDdyQXV0cVVialRQX0xOU0s2U0xtZHY1MHdyZ0E5Zno3dUUzRVIyRTc0OEdoYkRvRUd3d1JIN0Q5M0pqMUpmamlVTTg0Y25OTUhwZk5Vc01sNDdlTVFIa1NvWklISzVjSHRmc3JDWWVkRkFhd1QtM29YUF9KTE1mbDYwSlNuUFY2UG5jWW1DNF96bHFKSmtad0t5MEhKcnc2TUdRMWFWLUw0VWlZcDg2WVNZRmtjNEYzUE1GYTRLNXBMVXI1Nml1WjJyOVQ4UjVYcXJZcTBpcXdtS1lRVkdlS05GU0t4SWxKRVhsX1ZFQkVFQVBhdmJ5Z2NwOEcxRGJ4V2haWmdNVnRhU183bGYyRXBpMmNLYWt6cUJsdU1nSGpNTUVaMTA2S2VUVXd0TlNjQ2ZfWWJEMDlGNmItcTNlN0s0NFhycUtJMXhIVU5iZU9iR1dlbUh5VlNubWRCNzFQUGdSS0xZaWtObk54QkhrWHBTeUZrNlRfZ01LUXpMeFJ2Tnh0NDByNVdaY00wbUFyMExjOFhiM1d0bm5ieUZqcGxEaDNSMHRqNEFET2J6aEhzelpUUjFkNFpUWlVsekYxVVdNdFlkR0tSZ1VDNWRrWkN1YnowYm9wekhLcWk4cWdtQkhreUxnTUotMDJnWlNJY0p4aU5JbGJlVFlfbzdJdk9ySTNNcFFFc19oMDd3S2ZwS1hTZ3NjMkdCdmlIVmxPQTZvaUI0MUdCOUdLbU9nbHhwZW90MElDX2hncnhCN3NNOHQzcEhuT3RLaExaQlJBeUNwdFZydHlrLV84Sm1GM003eENTQTJPc2hLMnFRSXh5VExYSmp5dVpEemFhY0pFUmVpTG0tLXhPb1JQbU16Zk5hNTNiRVBpTWNmaXFJQWJfaHNHaUwwMkU4cWtBUnhWSlJsdyIsCiAgInR4bklEIjogIjEyMzQ1Njc4OTAiLAogICJ2ZXIiOiAiMS4wIgp9\",\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> keymanagervalue = mapper.readValue("{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}}",
				new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> finalValue = mapper.readValue("{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Mockito.when(keyManager.requestData(Mockito.any(), Mockito.any())).thenReturn(keymanagervalue);
		Map<String, Object> decipherValue = (Map<String, Object>) decodeMethod.invoke(kycAuthFilter,
				readValue);
		assertEquals(decipherValue, finalValue);
	}

	@Test
	public void testInValidDecodedRequest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException {		
		Method decodeMethod = KycAuthFilter.class.getDeclaredMethod("decipherRequest", Map.class);
		Map<String, Object> map = new HashMap<>();
		map.put("authRequest", createResponse().get("response"));
		decodeMethod.setAccessible(true);
		try {
			decodeMethod.invoke(kycAuthFilter, map);
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().getClass().equals(IdAuthenticationAppException.class));
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValidEncipherRequest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Map<String, Object> readValue = mapper.readValue("{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:03:28.141+05:30\",\"response\":{\"auth\":{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:03:27.697+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000000000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":null},\"txnID\":\"1234567890\",\"ttl\":\"24\",\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> finalValue = mapper.readValue("{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:03:28.141+05:30\",\"response\":\"eyJhdXRoIjoiZXlKemRHRjBkWE1pT2lKT0lpd2laWEp5SWpwYmV5Smxjbkp2Y2tOdlpHVWlPaUpKUkVFdFFrbEJMVEF3TVNJc0ltVnljbTl5VFdWemMyRm5aU0k2SWtKcGIyMWxkSEpwWXlCa1lYUmhJQzBnWm1kbGNrMXBiaUJrYVdRZ2JtOTBJRzFoZEdOb0luMWRMQ0p5WlhOVWFXMWxJam9pTWpBeE9TMHdNaTB5TlZReE9Ub3dNem95Tnk0Mk9UY3JNRFU2TXpBaUxDSnBibVp2SWpwN0ltbGtWSGx3WlNJNklrUWlMQ0p5WlhGVWFXMWxJam9pTWpBeE9TMHdNaTB5TlZReE5Ub3hOVG95TXk0d01qY3JNRFU2TXpBaUxDSmlhVzlKYm1admN5STZXM3NpWW1sdlZIbHdaU0k2SW1abmNrMXBiaUlzSW1SbGRtbGpaVWx1Wm04aU9uc2laR1YyYVdObFNXUWlPaUl4TWpNeE5ETWlMQ0p0WVd0bElqb2liV0Z1ZEhKaElpd2liVzlrWld3aU9pSnpkR1ZsYkNKOWZWMHNJblZ6WVdkbFJHRjBZU0k2SWpCNE1EQXdNRGd3TURBd01EQXdNREF3TUNKOUxDSjBlRzVKUkNJNklqRXlNelExTmpjNE9UQWlMQ0oyWlhJaU9tNTFiR3dzSW5OMFlYUnBZMVJ2YTJWdUlqb2lOVFV3TlRRek5EQTFNREExTURJeE9EY3dNVFV4TkRReE1qYzBPVFV3TWpNd05EVXdJbjA9Iiwia3ljIjpudWxsfQ==\",\"txnID\":\"1234567890\",\"ttl\":\"24\",\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Method encodeMethod = KycAuthFilter.class.getDeclaredMethod("encipherResponse", Map.class);
		encodeMethod.setAccessible(true);
		Map<String, Object> encipherValue = (Map<String, Object>) encodeMethod.invoke(kycAuthFilter, readValue);
		assertEquals(encipherValue, finalValue);

	}

	@Test
	public void testInValidEncodedRequest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method encodeMethod = KycAuthFilter.class.getDeclaredMethod("encipherResponse", Map.class);
		encodeMethod.setAccessible(true);
		Map<String, Object> map = new HashMap<>();
		map.put("response", "sdfsdfjhds");
		try {
			 encodeMethod.invoke(kycAuthFilter, map);
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().getClass().equals(IdAuthenticationAppException.class));
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testTxnId() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			IOException, NoSuchMethodException, SecurityException {
		Map<String, Object> reqValue = mapper.readValue("{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> resValue = mapper.readValue("{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:11:05.840+05:30\",\"response\":{\"auth\":{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:11:05.566+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000000000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":null},\"txnID\":null,\"ttl\":\"24\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setResponseParams", Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		Map<String, Object> requestBody = createEncodedRequest();
		Map<String, Object> authRequest = (Map<String, Object>) decode(
				(String) createEncodedRequest().get("authRequest"));
		requestBody.replace("authRequest", authRequest);
		Map<String, Object> decodeValue = (Map<String, Object>) txvIdMethod.invoke(kycAuthFilter, reqValue,
				resValue);
		assertNotNull(decodeValue);

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSetResponseParams() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			IOException, NoSuchMethodException, SecurityException {
		Map<String, Object> reqValue = mapper.readValue("{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> resValue = mapper.readValue("{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.666+05:30\",\"response\":{\"auth\":{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.475+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-26T13:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000008000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":{\"identity\":{\"gender\":[{\"language\":\"fra\",\"value\":\"mâle\"}],\"province\":[{\"language\":\"fra\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"fra\",\"value\":\"Casablanca\"}],\"phone\":[{\"language\":null,\"value\":\"9876543210\"}],\"postalCode\":[{\"language\":null,\"value\":\"570004\"}],\"addressLine1\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 1\"}],\"fullName\":[{\"language\":\"fra\",\"value\":\"Ibrahim Ibn Ali\"}],\"addressLine2\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"dateOfBirth\":[{\"language\":null,\"value\":\"1955/04/15\"}],\"addressLine3\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"fra\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"email\":[{\"language\":null,\"value\":\"abc@xyz.com\"}]},\"idvId\":\"XXXXXXXX15\",\"eprint\":null}},\"txnID\":null,\"ttl\":\"24\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setResponseParams", Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) txvIdMethod.invoke(kycAuthFilter, reqValue,
				resValue);
		assertNotNull(decodeValue);

	}
	
	@Test
	public void testInValidDecipherRequest2() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		Method decodeMethod = KycAuthFilter.class.getDeclaredMethod("decipherRequest", Map.class);
		decodeMethod.setAccessible(true);
		Map<String, Object> readValue = mapper.readValue("{\"authRequest\":\"ewogICJhdXRoVHlwZSI6IHsKICAgICJhZGRyZXNzIjogZmFsc2UsCiAgIClvIjogdHJ1ZSwKICAgICJvdHAiOiBmYWxzZSwKICAgICJwZXJzb25hbElkZW50aXR5IjogZmFsc2UsCiAgICAicGluIjogZmFsc2UKICB9LAogICJiaW9JbmZvIjogWwogICAgewogICAgICAiYmlvVHlwZSI6ICJmZ3JNaW4iLAogICAgICAiZGV2aWNlSW5mbyI6IHsKICAgICAgICAiZGV2aWNlSWQiOiAiMTIzMTQzIiwKICAgICAgICAibWFrZSI6ICJtYW50cmEiLAogICAgICAgICJtb2RlbCI6ICJzdGVlbCIKICAgICAgfQogICAgfQogIF0sCiAgImlkIjogIm1vc2lwLmlkZW50aXR5LmF1dGgiLAogICJpZHZJZCI6ICI1NzA2Mjc0OTE1IiwKICAiaWR2SWRUeXBlIjogIkQiLAogICJ0c3BJRCI6ICJzdHJpbmciLAogICJrZXkiOiB7CiAgICAicHVibGljS2V5Q2VydCI6ICJTVFJJTkciLAogICAgInNlc3Npb25LZXkiOiAiU3ltV0c1UkNaNXFPeWJNeHJIRXZEMTNrWTU1Y0c4aUpFam9kczJSVHF0dzh6Zjd1eDVTaEhacEZpdWpaWnVYTzdRNDlmYy01eFBXamh4UFIwTnFpcm9DRXpEZllTWUxpSTFtTkUxTHVjZEhPeHU3NXRnWmZPenJsbXY0ZlU2a3BadzZhcWo0TnJmMHZvNU9BVEVLVFdvUHBfR1RtLU1GSm12UmhuaGk3eHZ3bHJyOEQwZDhjTUxVMEFDcy1QdU94X2NubTBQMGtKTU9WVjZodXhESnlfTHItbzRJZnRVWGZZd3JyZDNDUkNXRzlaWW9sdENqTUZpdGM0Mzc4TWRuc0RJYk5fT0wzb29BTzZ4NVhPNm9JdHozWmZzS1FlT3ZtTG9VanYzRm9VNkVDaHlZWk9aZzB0NGpxWDA1d1pHYjB0dXhuRVJ5YXl3ZDcyLWoza3Z0ZWFBIgogIH0sCiAgInJlcVRpbWUiOiAiMjAxOS0wMi0yNVQxNToxNToyMy4wMjcrMDU6MzAiLAogICJyZXF1ZXN0IjogIkZfU3YwdUVzNkpEMjdVSl9UaXdIRGdEeFlZY2JScWJKUFlYNktXc0E5YXd4RHNpWU41b2VwQ2VINHY3U01rUk90dllJN2M0UXB2V0ZyWWVqQ2hFSURrV3EwSXBSR1RpcXJLTE9LRlJqWVhGSlQtUkJ0UnhTTUJKekw4SUhUMUMtbVRFNmZFbG13UFdHLXZNbDJJUDQzTWNqRWxtNk9sdXhyRDlvSW41X1hmWXhxY1dhc0h1X0pEOHJ3Ui1QdkJqUEdWN1o3WGhGQlBnVmVVWXZuTDVScUdhYzJweDlZSGVQa0hZMlkyZW13NDdyQXV0cVVialRQX0xOU0s2U0xtZHY1MHdyZ0E5Zno3dUUzRVIyRTc0OEdoYkRvRUd3d1JIN0Q5M0pqMUpmamlVTTg0Y25OTUhwZk5Vc01sNDdlTVFIa1NvWklISzVjSHRmc3JDWWVkRkFhd1QtM29YUF9KTE1mbDYwSlNuUFY2UG5jWW1DNF96bHFKSmtad0t5MEhKcnc2TUdRMWFWLUw0VWlZcDg2WVNZRmtjNEYzUE1GYTRLNXBMVXI1Nml1WjJyOVQ4UjVYcXJZcTBpcXdtS1lRVkdlS05GU0t4SWxKRVhsX1ZFQkVFQVBhdmJ5Z2NwOEcxRGJ4V2haWmdNVnRhU183bGYyRXBpMmNLYWt6cUJsdU1nSGpNTUVaMTA2S2VUVXd0TlNjQ2ZfWWJEMDlGNmItcTNlN0s0NFhycUtJMXhIVU5iZU9iR1dlbUh5VlNubWRCNzFQUGdSS0xZaWtObk54QkhrWHBTeUZrNlRfZ01LUXpMeFJ2Tnh0NDByNVdaY00wbUFyMExjOFhiM1d0bm5ieUZqcGxEaDNSMHRqNEFET2J6aEhzelpUUjFkNFpUWlVsekYxVVdNdFlkR0tSZ1VDNWRrWkN1YnowYm9wekhLcWk4cWdtQkhreUxnTUotMDJnWlNJY0p4aU5JbGJlVFlfbzdJdk9ySTNNcFFFc19oMDd3S2ZwS1hTZ3NjMkdCdmlIVmxPQTZvaUI0MUdCOUdLbU9nbHhwZW90MElDX2hncnhCN3NNOHQzcEhuT3RLaExaQlJBeUNwdFZydHlrLV84Sm1GM003eENTQTJPc2hLMnFRSXh5VExYSmp5dVpEemFhY0pFUmVpTG0tLXhPb1JQbU16Zk5hNTNiRVBpTWNmaXFJQWJfaHNHaUwwMkU4cWtBUnhWSlJsdyIsCiAgInR4bklEIjogIjEyMzQ1Njc4OTAiLAogICJ2ZXIiOiAiMS4wIgp9\",\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		try {
			decodeMethod.invoke(kycAuthFilter,	readValue);
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void tesInValidtSetResponseParams() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			IOException, NoSuchMethodException, SecurityException {
		Map<String, Object> reqValue = mapper.readValue("{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.0\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> resValue = mapper.readValue("{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.666+05:30\",\"response\":{\"auth\":{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.475+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-26T13:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000008000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":{\"identity\":{\"gender\":[{\"language\":\"fra\",\"value\":\"mâle\"}],\"province\":[{\"language\":\"fra\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"fra\",\"value\":\"Casablanca\"}],\"phone\":[{\"language\":null,\"value\":\"9876543210\"}],\"postalCode\":[{\"language\":null,\"value\":\"570004\"}],\"addressLine1\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 1\"}],\"fullName\":[{\"language\":\"fra\",\"value\":\"Ibrahim Ibn Ali\"}],\"addressLine2\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"dateOfBirth\":[{\"language\":null,\"value\":\"1955/04/15\"}],\"addressLine3\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"fra\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"email\":[{\"language\":null,\"value\":\"abc@xyz.com\"}]},\"idvId\":\"XXXXXXXX15\",\"eprint\":null}},\"txnID\":null,\"ttl\":\"24\"}",
				new TypeReference<Map<String, Object>>() {
		});
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setResponseParams", Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		try {
			txvIdMethod.invoke(kycAuthFilter, reqValue,	resValue);
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().getClass().equals(IdAuthenticationAppException.class));
		}

	}
	
	@Test
	public void testSign() throws IdAuthenticationAppException {
		assertEquals(true, kycAuthFilter.validateSignature("something", "something".getBytes()));
	}

	private Map<String, Object> decode(String stringToDecode)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper2 = new ObjectMapper();
		return mapper2.readValue(Base64.getDecoder().decode(stringToDecode), new TypeReference<Map<String, Object>>() {
		});
	}

	public Map<String, Object> createEncodedRequest() throws IOException {
		/*KycAuthRequestDTO k = new KycAuthRequestDTO();
		k.setConsentReq(true);
		k.setEKycAuthType(null);
		k.setEPrintReq(true);
		k.setSecLangReq(true);
		k.setId(null);
		// k.setVer(null);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setTxnID("121332");
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setTspID("1234567890");
		authRequestDTO.setTxnID("1234567890");
//		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		k.setAuthRequest(authRequestDTO);

		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		IdentityDTO idDTO3 = new IdentityDTO();
		idInfoDTO3.setLanguage("fre");
		idInfoDTO3.setValue(
				"Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA");
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO3);
		idDTO3.setLeftIndex(idInfoList1);
		RequestDTO r1 = new RequestDTO();
		r1.setIdentity(idDTO3);
		// System.out.println(map1);
		String kycReq = mapper.writeValueAsString(k);

		Map<String, Object> map = (Map<String, Object>) mapper.readValue(kycReq.getBytes(), Map.class);

		String request = mapper.writeValueAsString(reqDTO);
		Map<String, Object> authRequestMap = (Map<String, Object>) map.get("authRequest");
		authRequestMap.put("request", Base64.getEncoder().encodeToString(request.getBytes()));
		map.put("authRequest", Base64.getEncoder().encodeToString(mapper.writeValueAsBytes(authRequestMap)));*/
		Map<String, Object> map = new HashMap<>();
		return map;
	}

	public Map<String, Object> createResponse() throws IOException {
		/*AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setTransactionID("12345");
		//AuthResponseInfo authResponseInfo = new AuthResponseInfo();
		//authResponseDTO.setInfo(authResponseInfo);
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		kycResponseDTO.setAuth(authResponseDTO);
		kycResponseDTO.setKyc(new KycInfo());
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		kycAuthResponseDTO.setTxnID("12345");
		kycAuthResponseDTO.setResponseTime(DateUtils.getUTCCurrentDateTimeString());
		String kycAuthResponse = mapper.writeValueAsString(kycAuthResponseDTO);
		Map<String, Object> map = (Map<String, Object>) mapper.readValue(kycAuthResponse.getBytes(), Map.class);*/
		Map<String, Object> map = new HashMap<>();
		return map;

	}

}
