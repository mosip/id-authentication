package io.mosip.registration.test.util.restclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.aspectj.lang.JoinPoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.ResponseSignatureAdvice;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class })
public class ResponseSignatureAdviceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private ResponseSignatureAdvice responseSignatureAdvice;

	@Mock
	RestTemplate restTemplate;

	@Mock
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	@Mock
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	@Mock
	KeyGenerator keyGenerator;
	
	@Mock
	SignatureUtil signatureUtil;

	@Mock
	private JoinPoint joinPointMock;

	@Mock
	private Object result;

	@Mock
	private Logger LOGGER;

	@Mock
	private PolicySyncDAO policySyncDAO;
	
	@Before
	public void init() throws Exception {
		Map<String,Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.ASYMMETRIC_ALG_NAME, "RSA");

		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
	}

	@Test
	public void responseSignatureTest()
			throws RegBaseCheckedException, URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setIsSignRequired(true);
		requestHTTPDTO.setUri(new URI("/v1/mosip/test"));
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(joinPointMock.getArgs()).thenReturn(args);
		Map<String, Object> mapResponse = new LinkedHashMap<>();
		mapResponse.put("lastSyncTime", "2019-04-23T06:20:28.633Z");
		mapResponse.put("publicKey", null);
		mapResponse.put("issuedAt", null);
		mapResponse.put("expiryAt", null);
		Map<String, Object> linkedMapResponse = new LinkedHashMap<>();
		linkedMapResponse.put("id", null);
		linkedMapResponse.put("version", null);
		linkedMapResponse.put("responsetime", "2019-04-23T06:20:28.660Z");
		linkedMapResponse.put("metadata", null);
		linkedMapResponse.put("response", mapResponse);
		linkedMapResponse.put("errors", null);
		Map<String, Object> linkedMapHeader = new LinkedHashMap<>();
		linkedMapHeader.put("pragma", "no-cache");
		linkedMapHeader.put("response-signature",
				" S6or4K8KD_bqdiDN-UjtyBSI-LPpm800xJF7VKsXIRcnf3z4MV5EbcBGoqc_OcstF6J1FYLTI5uCsonTIj7m4mNnf1H7jOTlZKErjBw0sDSt2PiLSVJdE642SRjD8RXEZGWl_BqGel5PyWfHnBP5Cmmflrtb2oXI8CqEoU7YDwXfcr0wNhy1mtlHpKQx9O82HqhHy59S7iMcBcdIE46rhm7sJkrnOYOU6hwcuGiOYZvbl_y_iOUn5HEZX_41iycQ5PZADDIngF8zJhLOAs1OS9MfJfaTBMtsvKwzfp3NGw6OXoAymYVlykCldCjDOIz6AlM2noKBz0vpc6i8Lxglhg");

		Map<String, Object> linkedMap = new LinkedHashMap<>();
		linkedMap.put(RegistrationConstants.REST_RESPONSE_BODY, linkedMapResponse);
		linkedMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, linkedMapHeader);
		byte[] key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		KeyStore keys = new KeyStore();
		keys.setPublicKey(key);

		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keys);
		Mockito.when(signatureUtil.validateWithPublicKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		Mockito.when(decryptor.asymmetricPublicDecrypt(Mockito.any(), Mockito.any()))
				.thenReturn("rqN-Es-XfO9Ksl7mBJ0jjlWzkhMV1BPk4ShfOOq7QDQ".getBytes());

		responseSignatureAdvice.responseSignatureValidation(joinPointMock, linkedMap);

	}
	
	@Test
	public void responseSignatureTestCaseFail()
			throws RegBaseCheckedException, URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setIsSignRequired(true);
		requestHTTPDTO.setUri(new URI("/v1/mosip/test"));
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(joinPointMock.getArgs()).thenReturn(args);
		Map<String, Object> mapResponse = new LinkedHashMap<>();
		mapResponse.put("lastSyncTime", "2019-04-23T06:20:28.633Z");
		mapResponse.put("publicKey", null);
		mapResponse.put("issuedAt", null);
		mapResponse.put("expiryAt", null);
		Map<String, Object> linkedMapResponse = new LinkedHashMap<>();
		linkedMapResponse.put("id", null);
		linkedMapResponse.put("version", null);
		linkedMapResponse.put("responsetime", "2019-04-23T06:20:28.660Z");
		linkedMapResponse.put("metadata", null);
		linkedMapResponse.put("response", mapResponse);
		linkedMapResponse.put("errors", null);
		Map<String, Object> linkedMapHeader = new LinkedHashMap<>();
		linkedMapHeader.put("pragma", "no-cache");
		linkedMapHeader.put("response-signature",
				" S6or4K8KD_bqdiDN-UjtyBSI-LPpm800xJF7VKsXIRcnf3z4MV5EbcBGoqc_OcstF6J1FYLTI5uCsonTIj7m4mNnf1H7jOTlZKErjBw0sDSt2PiLSVJdE642SRjD8RXEZGWl_BqGel5PyWfHnBP5Cmmflrtb2oXI8CqEoU7YDwXfcr0wNhy1mtlHpKQx9O82HqhHy59S7iMcBcdIE46rhm7sJkrnOYOU6hwcuGiOYZvbl_y_iOUn5HEZX_41iycQ5PZADDIngF8zJhLOAs1OS9MfJfaTBMtsvKwzfp3NGw6OXoAymYVlykCldCjDOIz6AlM2noKBz0vpc6i8Lxglhg");

		Map<String, Object> linkedMap = new LinkedHashMap<>();
		linkedMap.put(RegistrationConstants.REST_RESPONSE_BODY, linkedMapResponse);
		linkedMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, linkedMapHeader);
		byte[] key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		KeyStore keys = new KeyStore();
		keys.setPublicKey(key);

		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keys);
		Mockito.when(signatureUtil.validateWithPublicKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		Mockito.when(decryptor.asymmetricPublicDecrypt(Mockito.any(), Mockito.any()))
				.thenReturn("rqN-Es-XfO9Ksl7mBJ0jjlWzkhMV1BPk4ShfOOq7QDQ".getBytes());

		responseSignatureAdvice.responseSignatureValidation(joinPointMock, linkedMap);

	}

	@Test
	public void responseSignatureTestFalse()
			throws RegBaseCheckedException, URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setIsSignRequired(true);
		requestHTTPDTO.setUri(new URI("/v1/mosip/test"));
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(joinPointMock.getArgs()).thenReturn(args);
		Map<String, Object> mapResponse = new LinkedHashMap<>();
		mapResponse.put("lastSyncTime", "2019-04-23T06:20:28.633Z");
		mapResponse.put("publicKey", null);
		mapResponse.put("issuedAt", null);
		mapResponse.put("expiryAt", null);
		Map<String, Object> linkedMapResponse = new LinkedHashMap<>();
		linkedMapResponse.put("id", null);
		linkedMapResponse.put("version", null);
		linkedMapResponse.put("responsetime", "2019-04-23T06:20:28.660Z");
		linkedMapResponse.put("metadata", null);
		linkedMapResponse.put("response", mapResponse);
		linkedMapResponse.put("errors", null);
		Map<String, Object> linkedMapHeader = new LinkedHashMap<>();
		linkedMapHeader.put("pragma", "no-cache");
		linkedMapHeader.put("response-signature",
				" S6or4K8KD_bqdiDN-UjtyBSI-LPpm800xJF7VKsXIRcnf3z4MV5EbcBGoqc_OcstF6J1FYLTI5uCsonTIj7m4mNnf1H7jOTlZKErjBw0sDSt2PiLSVJdE642SRjD8RXEZGWl_BqGel5PyWfHnBP5Cmmflrtb2oXI8CqEoU7YDwXfcr0wNhy1mtlHpKQx9O82HqhHy59S7iMcBcdIE46rhm7sJkrnOYOU6hwcuGiOYZvbl_y_iOUn5HEZX_41iycQ5PZADDIngF8zJhLOAs1OS9MfJfaTBMtsvKwzfp3NGw6OXoAymYVlykCldCjDOIz6AlM2noKBz0vpc6i8Lxglhg");

		Map<String, Object> linkedMap = new LinkedHashMap<>();
		linkedMap.put(RegistrationConstants.REST_RESPONSE_BODY, linkedMapResponse);
		linkedMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, linkedMapHeader);

		byte[] key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		KeyStore keys = new KeyStore();
		keys.setPublicKey(key);
		Mockito.when(signatureUtil.validateWithPublicKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keys);
		Mockito.when(decryptor.asymmetricPublicDecrypt(Mockito.any(), Mockito.any()))
				.thenReturn("qN-Es-XfO9Ksl7mBJ0jjlWzkhMV1BPk4ShfOOq7QDQ".getBytes());

		responseSignatureAdvice.responseSignatureValidation(joinPointMock, linkedMap);

	}

	@Test
	public void responseSignatureTestNewKey()
			throws RegBaseCheckedException, URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setIsSignRequired(true);
		requestHTTPDTO.setUri(new URI("/v1/mosip/test"));
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(joinPointMock.getArgs()).thenReturn(args);
		Map<String, Object> mapResponse = new LinkedHashMap<>();
		mapResponse.put("lastSyncTime", "2019-04-23T06:20:28.633Z");
		mapResponse.put("publicKey", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB");
		mapResponse.put("issuedAt", null);
		mapResponse.put("expiryAt", null);
		Map<String, Object> linkedMapResponse = new LinkedHashMap<>();
		linkedMapResponse.put("id", null);
		linkedMapResponse.put("version", null);
		linkedMapResponse.put("responsetime", "2019-04-23T06:20:28.660Z");
		linkedMapResponse.put("metadata", null);
		linkedMapResponse.put("response", mapResponse);
		linkedMapResponse.put("errors", null);
		Map<String, Object> linkedMapHeader = new LinkedHashMap<>();
		linkedMapHeader.put("pragma", "no-cache");
		linkedMapHeader.put("response-signature",
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB");

		Map<String, Object> linkedMap = new LinkedHashMap<>();
		linkedMap.put(RegistrationConstants.REST_RESPONSE_BODY, linkedMapResponse);
		linkedMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, linkedMapHeader);

		KeyStore keys = null;
		Mockito.when(signatureUtil.validateWithPublicKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keys);
		Mockito.when(decryptor.asymmetricPublicDecrypt(Mockito.any(), Mockito.any()))
				.thenReturn("qN-Es-XfO9Ksl7mBJ0jjlWzkhMV1BPk4ShfOOq7QDQ".getBytes());

		responseSignatureAdvice.responseSignatureValidation(joinPointMock, linkedMap);

	}

	@Test
	public void responseSignatureTestFail() throws RegBaseCheckedException, URISyntaxException {

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setIsSignRequired(false);
		requestHTTPDTO.setUri(new URI("/v1/mosip/test"));
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(joinPointMock.getArgs()).thenReturn(args);
		Map<String, Object> mapResponse = new LinkedHashMap<>();
		mapResponse.put("lastSyncTime", "2019-04-23T06:20:28.633Z");
		mapResponse.put("publicKey", null);
		mapResponse.put("issuedAt", null);
		mapResponse.put("expiryAt", null);
		Map<String, Object> linkedMapResponse = new LinkedHashMap<>();
		linkedMapResponse.put("id", null);
		linkedMapResponse.put("version", null);
		linkedMapResponse.put("responsetime", "2019-04-23T06:20:28.660Z");
		linkedMapResponse.put("metadata", null);
		linkedMapResponse.put("response", mapResponse);
		linkedMapResponse.put("errors", null);
		Map<String, Object> linkedMapHeader = new LinkedHashMap<>();
		linkedMapHeader.put("pragma", "no-cache");
		linkedMapHeader.put("response-signature",
				" S6or4K8KD_bqdiDN-UjtyBSI-LPpm800xJF7VKsXIRcnf3z4MV5EbcBGoqc_OcstF6J1FYLTI5uCsonTIj7m4mNnf1H7jOTlZKErjBw0sDSt2PiLSVJdE642SRjD8RXEZGWl_BqGel5PyWfHnBP5Cmmflrtb2oXI8CqEoU7YDwXfcr0wNhy1mtlHpKQx9O82HqhHy59S7iMcBcdIE46rhm7sJkrnOYOU6hwcuGiOYZvbl_y_iOUn5HEZX_41iycQ5PZADDIngF8zJhLOAs1OS9MfJfaTBMtsvKwzfp3NGw6OXoAymYVlykCldCjDOIz6AlM2noKBz0vpc6i8Lxglhg");

		Map<String, Object> linkedMap = new LinkedHashMap<>();
		linkedMap.put(RegistrationConstants.REST_RESPONSE_BODY, linkedMapResponse);
		linkedMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, linkedMapHeader);

		byte[] key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		KeyStore keys = new KeyStore();
		keys.setPublicKey(key);

		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keys);
		Mockito.when(decryptor.asymmetricPublicDecrypt(Mockito.any(), Mockito.any()))
				.thenReturn("qN-Es-XfO9Ksl7mBJ0jjlWzkhMV1BPk4ShfOOq7QDQ".getBytes());

		responseSignatureAdvice.responseSignatureValidation(joinPointMock, linkedMap);

	}

	@SuppressWarnings("unchecked")
	@Test(expected = Throwable.class)
	public void responseSignatureTestException() throws RegBaseCheckedException, URISyntaxException {

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setIsSignRequired(true);
		requestHTTPDTO.setUri(new URI("/v1/mosip/test"));
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(joinPointMock.getArgs()).thenReturn(args);
		Map<String, Object> mapResponse = new LinkedHashMap<>();
		mapResponse.put("lastSyncTime", "2019-04-23T06:20:28.633Z");
		mapResponse.put("publicKey", null);
		mapResponse.put("issuedAt", null);
		mapResponse.put("expiryAt", null);
		Map<String, Object> linkedMapResponse = new LinkedHashMap<>();
		linkedMapResponse.put("id", null);
		linkedMapResponse.put("version", null);
		linkedMapResponse.put("responsetime", "2019-04-23T06:20:28.660Z");
		linkedMapResponse.put("metadata", null);
		linkedMapResponse.put("response", mapResponse);
		linkedMapResponse.put("errors", null);
		Map<String, Object> linkedMapHeader = new LinkedHashMap<>();
		linkedMapHeader.put("pragma", "no-cache");
		linkedMapHeader.put("response-signature",
				" S6or4K8KD_bqdiDN-UjtyBSI-LPpm800xJF7VKsXIRcnf3z4MV5EbcBGoqc_OcstF6J1FYLTI5uCsonTIj7m4mNnf1H7jOTlZKErjBw0sDSt2PiLSVJdE642SRjD8RXEZGWl_BqGel5PyWfHnBP5Cmmflrtb2oXI8CqEoU7YDwXfcr0wNhy1mtlHpKQx9O82HqhHy59S7iMcBcdIE46rhm7sJkrnOYOU6hwcuGiOYZvbl_y_iOUn5HEZX_41iycQ5PZADDIngF8zJhLOAs1OS9MfJfaTBMtsvKwzfp3NGw6OXoAymYVlykCldCjDOIz6AlM2noKBz0vpc6i8Lxglhg");

		Map<String, Object> linkedMap = new LinkedHashMap<>();
		linkedMap.put(RegistrationConstants.REST_RESPONSE_BODY, linkedMapResponse);
		linkedMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, linkedMapHeader);

		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenThrow(IOException.class);

		responseSignatureAdvice.responseSignatureValidation(joinPointMock, linkedMap);

	}

}
