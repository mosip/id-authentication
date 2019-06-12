package io.mosip.authentication.kyc.service.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.policy.dto.AuthPolicy;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;

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

	@Mock
	EncryptorImpl encryptor;

	byte[] key = { 48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 1, 15, 0, 48,
			-126, 1, 10, 2, -126, 1, 1, 0, -56, 41, -49, 92, 30, -78, 87, 22, -103, -23, -14, 106, -89, 84, -73, 51,
			-69, -10, 75, -88, 94, 23, -106, -67, -4, 53, -91, -74, -64, 101, 70, 113, 100, 14, 67, 22, -27, -121, -45,
			-11, -107, 64, -56, -101, 97, 62, 64, 65, 57, -18, -47, 96, -88, 38, -77, 107, 125, 39, -52, -83, -67, -27,
			-20, -9, 27, -15, 69, 78, 74, -36, -114, 20, -121, -119, -55, 26, -50, -69, 16, -21, 84, 6, 66, 117, -39, 0,
			17, -39, -15, 49, -114, -101, -106, -113, -98, -81, 3, 18, -109, -122, -57, -19, 27, 2, 53, 8, -53, -11,
			-73, -84, 9, 55, -33, 8, -93, 16, -103, -4, 117, -35, -63, 43, -97, -74, 48, 101, -108, 38, -54, 18, -36,
			105, -39, 21, 117, -81, 42, -15, -95, 79, -124, -59, -128, 64, 82, 85, -68, -79, 24, -84, 25, -113, 125,
			-17, -20, -57, 50, -63, -13, -79, -60, 81, -104, 111, -84, 62, 123, -40, 12, -7, 65, -5, 23, 3, -91, -17, 2,
			49, -56, 73, 35, 46, -97, 38, -18, 14, 10, 26, 11, 122, 124, 124, -20, -110, -9, 26, 122, 59, 74, -123, -86,
			97, 0, 48, -14, 65, -50, -49, 40, 90, 65, 127, 75, 110, -76, 127, -41, 80, 6, 30, 61, -4, 27, -63, -100,
			115, -79, -87, 107, 66, 73, -14, 13, -98, -108, 55, 26, 58, -72, -103, -35, 46, -15, 45, 23, 84, 93, 31, 44,
			-112, -41, 95, 22, 14, -114, 15, 2, 3, 1, 0, 1 };

	@Before
	public void before() {
		ReflectionTestUtils.setField(kycAuthFilter, "mapper", mapper);
		ReflectionTestUtils.setField(kycAuthFilter, "env", env);
		ReflectionTestUtils.setField(kycAuthFilter, "keyManager", keyManager);
	}

	@Test
	public void testValidEncipherRequest()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException,
			NoSuchMethodException, SecurityException, InvalidKeySpecException, NoSuchAlgorithmException {
		PublicKey pkey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
		ReflectionTestUtils.setField(kycAuthFilter, "keyManager", keyManager);
		ReflectionTestUtils.setField(kycAuthFilter, "encryptor", encryptor);
		ReflectionTestUtils.setField(kycAuthFilter, "publicKey", pkey);
		Map<String, Object> readValue = mapper.readValue(
				"{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:03:28.141+05:30\",\"response\":{\"auth\":{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:03:27.697+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000000000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":null},\"txnID\":\"1234567890\",\"ttl\":\"24\",\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Mockito.when(encryptor.symmetricEncrypt(new SecretKey() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getFormat() {

				return null;
			}

			@Override
			public byte[] getEncoded() {

				return null;
			}

			@Override
			public String getAlgorithm() {

				return null;
			}
		}, "asdsad".toString().getBytes())).thenReturn("asad".getBytes());
		Mockito.when(encryptor.asymmetricPublicEncrypt(new PublicKey() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getFormat() {

				return null;
			}

			@Override
			public byte[] getEncoded() {

				return null;
			}

			@Override
			public String getAlgorithm() {

				return null;
			}
		}, "adsad".toString().getBytes())).thenReturn("asad".getBytes());
		Method encodeMethod = KycAuthFilter.class.getDeclaredMethod("encipherResponse", Map.class);
		encodeMethod.setAccessible(true);
		encodeMethod.invoke(kycAuthFilter, readValue);

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
		Map<String, Object> reqValue = mapper.readValue(
				"{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Map<String, Object> resValue = mapper.readValue(
				"{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:11:05.840+05:30\",\"response\":{\"auth\":{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:11:05.566+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000000000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":null},\"txnID\":null,\"ttl\":\"24\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setResponseParams", Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) txvIdMethod.invoke(kycAuthFilter, reqValue, resValue);
		assertNotNull(decodeValue);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetResponseParams() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Map<String, Object> reqValue = mapper.readValue(
				"{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Map<String, Object> resValue = mapper.readValue(
				"{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.666+05:30\",\"response\":{\"auth\":{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.475+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-26T13:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000008000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":{\"identity\":{\"gender\":[{\"language\":\"fra\",\"value\":\"mâle\"}],\"province\":[{\"language\":\"fra\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"fra\",\"value\":\"Casablanca\"}],\"phone\":[{\"language\":null,\"value\":\"9876543210\"}],\"postalCode\":[{\"language\":null,\"value\":\"570004\"}],\"addressLine1\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 1\"}],\"fullName\":[{\"language\":\"fra\",\"value\":\"Ibrahim Ibn Ali\"}],\"addressLine2\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"dateOfBirth\":[{\"language\":null,\"value\":\"1955/04/15\"}],\"addressLine3\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"fra\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"email\":[{\"language\":null,\"value\":\"abc@xyz.com\"}]},\"idvId\":\"XXXXXXXX15\",\"eprint\":null}},\"txnID\":null,\"ttl\":\"24\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setResponseParams", Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) txvIdMethod.invoke(kycAuthFilter, reqValue, resValue);
		assertNotNull(decodeValue);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetResponseParams2() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Map<String, Object> reqValue = mapper.readValue(
				"{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Map<String, Object> resValue = mapper.readValue(
				"{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.666+05:30\",\"auth\":{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.475+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-26T13:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000008000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"response\":{\"identity\":{\"gender\":[{\"language\":\"fra\",\"value\":\"mâle\"}],\"province\":[{\"language\":\"fra\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"fra\",\"value\":\"Casablanca\"}],\"phone\":[{\"language\":null,\"value\":\"9876543210\"}],\"postalCode\":[{\"language\":null,\"value\":\"570004\"}],\"addressLine1\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 1\"}],\"fullName\":[{\"language\":\"fra\",\"value\":\"Ibrahim Ibn Ali\"}],\"addressLine2\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"dateOfBirth\":[{\"language\":null,\"value\":\"1955/04/15\"}],\"addressLine3\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"fra\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"email\":[{\"language\":null,\"value\":\"abc@xyz.com\"}]},\"idvId\":\"XXXXXXXX15\",\"eprint\":null},\"txnID\":null,\"ttl\":\"24\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setResponseParams", Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) txvIdMethod.invoke(kycAuthFilter, reqValue, resValue);
		assertNotNull(decodeValue);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetResponseParams3() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException, NoSuchFieldException,
			InvalidKeySpecException, NoSuchAlgorithmException {
		PublicKey pkey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
		ReflectionTestUtils.setField(kycAuthFilter, "keyManager", keyManager);
		ReflectionTestUtils.setField(kycAuthFilter, "encryptor", encryptor);
		ReflectionTestUtils.setField(kycAuthFilter, "publicKey", pkey);
		/*
		 * Class<KycAuthFilter> myClass = (Class<KycAuthFilter>)
		 * kycAuthFilter.getClass(); Field field =
		 * myClass.getDeclaredField("publicKey"); field.setAccessible(true);
		 * field.set("publicKey", pkey);
		 */
		Map<String, Object> reqValue = mapper.readValue(
				"{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Map<String, Object> resValue = mapper.readValue(
				"{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.666+05:30\",\"auth\":{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.475+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-26T13:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000008000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"response\":{\"identity\":{\"gender\":[{\"language\":\"fra\",\"value\":\"mâle\"}],\"province\":[{\"language\":\"fra\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"fra\",\"value\":\"Casablanca\"}],\"phone\":[{\"language\":null,\"value\":\"9876543210\"}],\"postalCode\":[{\"language\":null,\"value\":\"570004\"}],\"addressLine1\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 1\"}],\"fullName\":[{\"language\":\"fra\",\"value\":\"Ibrahim Ibn Ali\"}],\"addressLine2\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"dateOfBirth\":[{\"language\":null,\"value\":\"1955/04/15\"}],\"addressLine3\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"fra\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"email\":[{\"language\":null,\"value\":\"abc@xyz.com\"}]},\"idvId\":\"XXXXXXXX15\",\"eprint\":null},\"txnID\":null,\"ttl\":\"24\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Mockito.when(encryptor.symmetricEncrypt(new SecretKey() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getFormat() {

				return null;
			}

			@Override
			public byte[] getEncoded() {

				return null;
			}

			@Override
			public String getAlgorithm() {

				return null;
			}
		}, resValue.toString().getBytes())).thenReturn("asad".getBytes());
		Mockito.when(encryptor.asymmetricPublicEncrypt(new PublicKey() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getFormat() {

				return null;
			}

			@Override
			public byte[] getEncoded() {

				return null;
			}

			@Override
			public String getAlgorithm() {

				return null;
			}
		}, resValue.toString().getBytes())).thenReturn("asad".getBytes());
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setResponseParams", Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) txvIdMethod.invoke(kycAuthFilter, reqValue, resValue);
		assertNotNull(decodeValue);

	}

	@Test
	public void tesInValidtSetResponseParams() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Map<String, Object> reqValue = mapper.readValue(
				"{\"authRequest\":{\"authType\":{\"address\":false,\"bio\":true,\"otp\":false,\"personalIdentity\":false,\"pin\":false},\"bioInfo\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"id\":\"mosip.identity.auth\",\"idvId\":\"5706274915\",\"idvIdType\":\"D\",\"tspID\":\"string\",\"key\":{\"publicKeyCert\":\"STRING\",\"sessionKey\":\"SymWG5RCZ5qOybMxrHEvD13kY55cG8iJEjods2RTqtw8zf7ux5ShHZpFiujZZuXO7Q49fc-5xPWjhxPR0NqiroCEzDfYSYLiI1mNE1LucdHOxu75tgZfOzrlmv4fU6kpZw6aqj4Nrf0vo5OATEKTWoPp_GTm-MFJmvRhnhi7xvwlrr8D0d8cMLU0ACs-PuOx_cnm0P0kJMOVV6huxDJy_Lr-o4IftUXfYwrrd3CRCWG9ZYoltCjMFitc4378MdnsDIbN_OL3ooAO6x5XO6oItz3ZfsKQeOvmLoUjv3FoU6EChyYZOZg0t4jqX05wZGb0tuxnERyaywd72-j3kvteaA\"},\"reqTime\":\"2019-02-25T15:15:23.0\",\"request\":{\"identity\":{\"leftIndex\":[{\"value\":\"Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA\"}],\"rightIndex\":[{\"value\":\"Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA\"}]}},\"txnID\":\"1234567890\",\"ver\":\"1.0\"},\"consentReq\":true,\"ekycAuthType\":\"F\",\"eprintReq\":false,\"id\":\"mosip.identity.kyc\",\"secLangReq\":false,\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Map<String, Object> resValue = mapper.readValue(
				"{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.666+05:30\",\"response\":{\"auth\":{\"status\":\"Y\",\"err\":[],\"resTime\":\"2019-02-26T14:29:50.475+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-26T13:15:23.027+05:30\",\"matchInfos\":[],\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000008000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":{\"identity\":{\"gender\":[{\"language\":\"fra\",\"value\":\"mâle\"}],\"province\":[{\"language\":\"fra\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"fra\",\"value\":\"Casablanca\"}],\"phone\":[{\"language\":null,\"value\":\"9876543210\"}],\"postalCode\":[{\"language\":null,\"value\":\"570004\"}],\"addressLine1\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 1\"}],\"fullName\":[{\"language\":\"fra\",\"value\":\"Ibrahim Ibn Ali\"}],\"addressLine2\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"dateOfBirth\":[{\"language\":null,\"value\":\"1955/04/15\"}],\"addressLine3\":[{\"language\":\"fra\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"fra\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"email\":[{\"language\":null,\"value\":\"abc@xyz.com\"}]},\"idvId\":\"XXXXXXXX15\",\"eprint\":null}},\"txnID\":null,\"ttl\":\"24\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setResponseParams", Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		try {
			txvIdMethod.invoke(kycAuthFilter, reqValue, resValue);
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().getClass().equals(IdAuthenticationAppException.class));
		}

	}

	@Test
	public void testSign() throws IdAuthenticationAppException {
		assertEquals(true, kycAuthFilter.validateSignature("something", "something".getBytes()));
	}

	@Test
	public void testValidEncipherRequest2()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException,
			NoSuchMethodException, SecurityException, InvalidKeySpecException, NoSuchAlgorithmException {
		PublicKey pkey = null;
		ReflectionTestUtils.setField(kycAuthFilter, "keyManager", keyManager);
		ReflectionTestUtils.setField(kycAuthFilter, "encryptor", encryptor);
		ReflectionTestUtils.setField(kycAuthFilter, "publicKey", pkey);
		Map<String, Object> readValue = mapper.readValue(
				"{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:03:28.141+05:30\",\"response\":{\"auth\":{\"status\":\"N\",\"err\":[{\"errorCode\":\"IDA-BIA-001\",\"errorMessage\":\"Biometric data - fgerMin did not match\"}],\"resTime\":\"2019-02-25T19:03:27.697+05:30\",\"info\":{\"idType\":\"D\",\"reqTime\":\"2019-02-25T15:15:23.027+05:30\",\"bioInfos\":[{\"bioType\":\"fgrMin\",\"deviceInfo\":{\"deviceId\":\"123143\",\"make\":\"mantra\",\"model\":\"steel\"}}],\"usageData\":\"0x0000800000000000\"},\"txnID\":\"1234567890\",\"ver\":null,\"staticToken\":\"550543405005021870151441274950230450\"},\"kyc\":null},\"txnID\":\"1234567890\",\"ttl\":\"24\",\"ver\":\"1.0\"}",
				new TypeReference<Map<String, Object>>() {
				});
		Mockito.when(encryptor.symmetricEncrypt(new SecretKey() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getFormat() {

				return null;
			}

			@Override
			public byte[] getEncoded() {

				return null;
			}

			@Override
			public String getAlgorithm() {

				return null;
			}
		}, "asdsad".toString().getBytes())).thenReturn("asad".getBytes());
		Mockito.when(encryptor.asymmetricPublicEncrypt(new PublicKey() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getFormat() {

				return null;
			}

			@Override
			public byte[] getEncoded() {

				return null;
			}

			@Override
			public String getAlgorithm() {

				return null;
			}
		}, "adsad".toString().getBytes())).thenReturn("asad".getBytes());
		Method encodeMethod = KycAuthFilter.class.getDeclaredMethod("encipherResponse", Map.class);
		encodeMethod.setAccessible(true);
		encodeMethod.invoke(kycAuthFilter, readValue);
	}
	
	@Test
	public void checkAllowedAuthTypeBasedOnPolicyTest() {
		AuthPolicy authPolicy = new AuthPolicy();
		authPolicy.setAuthType("demo");
		authPolicy.setMandatory(true);
		try {
			ReflectionTestUtils.invokeMethod(kycAuthFilter, "checkAllowedAuthTypeBasedOnPolicy", new HashMap<>(), Collections.singletonList(authPolicy));
		} catch (UndeclaredThrowableException e) {
			String detailMessage = e.getUndeclaredThrowable().getMessage();
			String[] error = detailMessage.split("-->");
			assertEquals("IDA-MPA-013", error[0].trim());
			assertEquals("Partner is unauthorised for eKYC", error[1].trim());
			assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
		}
	}
	
	@Test
	public void testEncipherResponse() {
		Map<String,Object> resMap=new HashMap<>();
		resMap.put(IdAuthCommonConstants.RESPONSE, new HashMap<>());
		try {
			Mockito.when(keyManager.encryptData(Mockito.any(), Mockito.any())).thenThrow(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
			kycAuthFilter.encipherResponse(resMap);
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.INVALID_ENCRYPT_EKYC_RESPONSE.getErrorCode(), e.getErrorCode());
			assertEquals(IdAuthenticationErrorConstants.INVALID_ENCRYPT_EKYC_RESPONSE.getErrorMessage(), e.getErrorText());
		}
		
	}

}
