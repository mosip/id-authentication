package io.mosip.registration.test.util.restclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.aspectj.lang.JoinPoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.ResponseSignatureAdvice;

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
	private JoinPoint joinPointMock;

	@Mock
	private Object result;

	@Mock
	private Logger LOGGER;

	@Before
	public void initialize() throws IOException, URISyntaxException {

		ReflectionTestUtils.setField(responseSignatureAdvice, "publicKey",
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtCR2L_MwUv4ctfGulWf4ZoWkSyBHbfkVtE_xAmzzIDWHP1V5hGxg8jt8hLtYYFwBNj4l_PTZGkblcVg-IePHilmQiVDptTVVA2PGtwRdud7QL4xox8RXmIf-xa-JmP2E804iVM-Ki8aPf1yuxXNUwLxZsflFww73lc-SGVUHupD8Os0qNZbbJl0BYioNG4WmPMHy3WJ-7jGN0HEV-9E18yf_enR0YewUmUI6Rxxb606-w8iQyWfSJq6UOfFmH5WAn-oTOoTIwg_fBxXuG_FlDoNWs6N5JtI18BMsUQA_GQZJct6TyXcBNUrcBYhZERvPlRGqIOoTl-T2sPJ5ST9eswIDAQAB");
	}

	@Test
	public void responseSignatureTest() throws RegBaseCheckedException, URISyntaxException {

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

		Mockito.when(decryptor.asymmetricPublicDecrypt(Mockito.any(), Mockito.any()))
				.thenReturn("rqN-Es-XfO9Ksl7mBJ0jjlWzkhMV1BPk4ShfOOq7QDQ".getBytes());

		responseSignatureAdvice.responseSignatureValidation(joinPointMock, linkedMap);

	}

	@Test
	public void responseSignatureTestFalse() throws RegBaseCheckedException, URISyntaxException {

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

		Mockito.when(decryptor.asymmetricPublicDecrypt(Mockito.any(), Mockito.any()))
				.thenThrow(JsonProcessingException.class);

		responseSignatureAdvice.responseSignatureValidation(joinPointMock, linkedMap);

	}

}
