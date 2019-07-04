package io.mosip.authentication.common.service.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class FilterValidatorTest {

	@Autowired
	Environment env;

	@Autowired
	ObjectMapper mapper;

	
	BaseIDAFilter baseIDAFilter = new BaseIDAFilter() {
		
		@Override
		protected void authenticateRequest(ResettableStreamHttpServletRequest requestWrapper)
				throws IdAuthenticationAppException {
		}
	};
	
	BaseAuthFilter baseAuthFilter = new BaseAuthFilter() {
		
		@Override
		protected void validateDecipheredRequest(ResettableStreamHttpServletRequest requestWrapper,
				Map<String, Object> decipherRequest) throws IdAuthenticationAppException {
		}
	};
	
	@InjectMocks
	IdAuthFilter idAuthFilter;
	
	@Before
	public void setUp() {
		ReflectionTestUtils.setField(baseIDAFilter, "env", env);
		ReflectionTestUtils.setField(idAuthFilter, "env", env);
		ReflectionTestUtils.setField(idAuthFilter, "mapper", mapper);
	}

	@Test
	public void validateIDTest1() throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> requestBody = mapper.readValue(
				"{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auths\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		try {
			ReflectionTestUtils.invokeMethod(baseIDAFilter, "validateId", requestBody, "mosip.ida.api.ids.auth");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MLC-009", error[0].trim());
			assertEquals("Invalid Input Parameter - id", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validateIDTest2() throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> requestBody = mapper.readValue(
				"{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		try {
			ReflectionTestUtils.invokeMethod(baseIDAFilter, "validateId", requestBody, "mosip.ida.api.ids.auth");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MLC-006", error[0].trim());
			assertEquals("Missing Input Parameter - id", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validateIDTest3() throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> requestBody = mapper.readValue(
				"{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		try {
			ReflectionTestUtils.invokeMethod(baseIDAFilter, "validateId", requestBody, "mosip.ida.api.ids.auth");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MLC-006", error[0].trim());
			assertEquals("Missing Input Parameter - id", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validateLicenseKeyMISPMappingTest1() {
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "licenseKeyMISPMapping", "135898653");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-008", error[0].trim());
			assertEquals("License key of MISP has expired", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validateLicenseKeyMISPMappingTest2() {
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "licenseKeyMISPMapping", "635899234");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-017", error[0].trim());
			assertEquals("License key of MISP is blocked", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validateLicenseKeyMISPMappingTest3() {
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "licenseKeyMISPMapping", "123456789");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-007", error[0].trim());
			assertEquals("License key does not belong to a registered MISP", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validPartnerIdTest1() {
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "validPartnerId", "123456789");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-009", error[0].trim());
			assertEquals("Partner is not registered", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validPartnerIdTest2() {
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "validPartnerId", "18248239994");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-014", error[0].trim());
			assertEquals("Partner is not assigned with any policy", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validPartnerIdTest3() {
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "validPartnerId", "1873293764");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-012", error[0].trim());
			assertEquals("Partner is deactivated", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validMISPPartnerMappingTest1() {
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "validMISPPartnerMapping", "1873293764", "12344544");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-010", error[0].trim());
			assertEquals("MISP and Partner not mapped", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validMISPPartnerMappingTest2() {
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "validMISPPartnerMapping", "1873299300", "9870862555");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-010", error[0].trim());
			assertEquals("MISP and Partner not mapped", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void checkAllowedAuthTypeBasedOnPolicyTest1() throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> requestBody = mapper.readValue(
				"{\r\n" + 
				"              \"consentObtained\": true,\r\n" + 
				"              \"id\": \"mosip.identity.auth\",\r\n" + 
				"              \"individualId\": \"2160785027\",\r\n" + 
				"              \"individualIdType\": \"UIN\",\r\n" + 
				"              \"keyIndex\": \"string\",\r\n" + 
				"              \"request\": \"4vT_-QARSKI8JI0lSqRhEqyX_SA0AKZK3tbkfaYQvFK0G7U_3PkK3xNunSzyiYvcUGjmDOfqy6ETbnW1I7tqlMrdjUqLcB9N0Gjo2R-y6XYku7wmnjH2SaYuIDQIeWryHH19l6nl_0sKMpG1dKryvR0OAHv7xudeRuWfEQLYEWO72zukiTHDq6dBA-6wyW6h6tIeYY1mDX4L8C2xRoyR05US2sQxJoVNqOo2o-DC_8rBNELKTxb7tSn4Z7MIYXymUQ5ASh2py_5m7TH55eGjo6eeZ2xiPQeFpgfXQH4HOpSxEaFuo2qGk9Pg40DneItQhyoK2X7-1j6m3KX3ybj_GcdinoxtI87E6w1RXg_cLByyjMArrZdU4uMt_9weShHV9vS17RjN7pVx_rkne_m4jGHB527T-Qa3YovXBrrC7yqzyc_TAPwCE4ERPWf7KHxdcgpw_XJXYETtC_cZRouTzFRgAKmq2JdxKZMUH7C0-RuCbTR5WfnpAK12VV5xs658QvKxYgkHv5X0MkTKWf-uE53Bto-f6glODH0-GVm7Pln5Ra1xXzti-e5yrkmslX--PPwUr2F6_3fAU-R05Xx1kKA8zull3Ne_npX2OpR-b2AKMYFXk9bBXG3O-8eF4A5emo0T4oKAYw4js4wITaB1qSxOrKQs3MTlbKC7Rv3TlsLspWozx8L4jQUjkrwiD4zSY7kjyiq5O-3UqKt1JeNMc1n9wYxOMOZDJETfa9nZEDuq-85EeEosjoTDChjtAUTEdhtuVNhsY9C66dQxA18kzIdYCMDrgU9Y-y9-LtBXF7WsnivqCudak4O4lX1aDwAquYnAhis9wMP8X0qbApkf1A\",\r\n" + 
				"              \"requestHMAC\": \"4RVZ4D0901hTYReM84SMoHQKCXt3_Jj8wCMOWTC-sBMEg_s4G-gKvPAHTlPBwVvO6XtW8GNp8rJj5voaYqqKb6x7MLYbDRbKGE31wt13h48iVP2V3kZptJJ_GR-7KGK9\",\r\n" + 
				"              \"requestSessionKey\": \"s3GiB3rPYGs6lnMggGIQe4Q6G9w_hk35f9WVEA_cA8I10FhTNi3RosyJwjeMRWrWOadZnSJbKRczA6h3lYxWPU2L_Yq9vt__IsH1g5fgDgWQ5DYLgZiBGAtw9nTIeMd9LKuaLn779F_xlbg_rL7nHYA5R-QUEqZcIiAJPlvBfV-69N1PXozZ2zzpru0ci7pklhLec_N7IbFryK7Y5c62dnR7O-HN--dnEVXwpbEOs9aqwNVYJ3jr1C_gBIhbU_LGGhxjNbm4YSZ49WmQvR8NBNfFUIR1Oobw4RqHUSn0me86fSOm9Aq94VgjS81chaHUPRIYrM60XUXAg3DpVN-zgw\",\r\n" + 
				"              \"requestTime\": \"2019-04-03T06:02:22.250+05:30\",\r\n" + 
				"              \"requestedAuth\": {\r\n" + 
				"                             \"bio\": false,\r\n" + 
				"                             \"demo\": true,\r\n" + 
				"                             \"otp\": false,\r\n" + 
				"                             \"pin\": false\r\n" + 
				"              },\r\n" + 
				"              \"transactionID\": \"1234567890\",\r\n" + 
				"              \"version\": \"0.9\"\r\n" + 
				"}\r\n" + 
				"",
				new TypeReference<Map<String, Object>>() {
				});
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "checkAllowedAuthTypeBasedOnPolicy","0983222", requestBody);
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-006", error[0].trim());
			assertEquals("DEMO Authentication usage not allowed as per policy", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void checkAllowedAuthTypeBasedOnPolicyTest2() throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> requestBody = mapper.readValue(
				"{\r\n" + 
				"              \"consentObtained\": true,\r\n" + 
				"              \"id\": \"mosip.identity.auth\",\r\n" + 
				"              \"individualId\": \"2160785027\",\r\n" + 
				"              \"individualIdType\": \"UIN\",\r\n" + 
				"              \"keyIndex\": \"string\",\r\n" + 
				"              \"request\":{\r\n" + 
				"	\"timestamp\": \"2019-04-24T09:41:57.086+05:30\",\r\n" + 
				"	\"transactionID\": \"1234567890\",\r\n" + 
				"	\"biometrics\": [{\r\n" + 
				"		\"data\": {\r\n" + 
				"			\"bioType\": \"FID\",\r\n" + 
				"			\"bioSubType\": \"UNKNOWN\",\r\n" + 
				"			\"deviceProviderID\": \"cogent\",\r\n" + 
				"			\"bioValue\": \"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA\"\r\n" + 
				"		}\r\n" + 
				"	}]\r\n" + 
				"},\r\n" + 
				"              \"requestHMAC\": \"4RVZ4D0901hTYReM84SMoHQKCXt3_Jj8wCMOWTC-sBMEg_s4G-gKvPAHTlPBwVvO6XtW8GNp8rJj5voaYqqKb6x7MLYbDRbKGE31wt13h48iVP2V3kZptJJ_GR-7KGK9\",\r\n" + 
				"              \"requestSessionKey\": \"s3GiB3rPYGs6lnMggGIQe4Q6G9w_hk35f9WVEA_cA8I10FhTNi3RosyJwjeMRWrWOadZnSJbKRczA6h3lYxWPU2L_Yq9vt__IsH1g5fgDgWQ5DYLgZiBGAtw9nTIeMd9LKuaLn779F_xlbg_rL7nHYA5R-QUEqZcIiAJPlvBfV-69N1PXozZ2zzpru0ci7pklhLec_N7IbFryK7Y5c62dnR7O-HN--dnEVXwpbEOs9aqwNVYJ3jr1C_gBIhbU_LGGhxjNbm4YSZ49WmQvR8NBNfFUIR1Oobw4RqHUSn0me86fSOm9Aq94VgjS81chaHUPRIYrM60XUXAg3DpVN-zgw\",\r\n" + 
				"              \"requestTime\": \"2019-04-03T06:02:22.250+05:30\",\r\n" + 
				"              \"requestedAuth\": {\r\n" + 
				"                             \"bio\": true,\r\n" + 
				"                             \"demo\": false,\r\n" + 
				"                             \"otp\": false,\r\n" + 
				"                             \"pin\": false\r\n" + 
				"              },\r\n" + 
				"              \"transactionID\": \"1234567890\",\r\n" + 
				"              \"version\": \"0.9\"\r\n" + 
				"}\r\n" + 
				"",
				new TypeReference<Map<String, Object>>() {
				});
		try {
			ReflectionTestUtils.invokeMethod(idAuthFilter, "checkAllowedAuthTypeBasedOnPolicy","92834787293", requestBody);
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-015", error[0].trim());
			assertEquals("otp-authentiation usage is mandatory as per policy", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void validateRequestHMACTest() {
		try {
			ReflectionTestUtils.invokeMethod(baseAuthFilter, "validateRequestHMAC","92834787293", "1234234");
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-016", error[0].trim());
			assertEquals("HMAC Validation failed", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
}
