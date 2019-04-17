package io.mosip.authentication.common.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.integration.dto.CryptomanagerRequestDto;
import io.mosip.authentication.common.service.integration.dto.CryptomanagerResponseDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;

// 
/**
 * The Class KeyManagerTest which covers KeyManager
 * 
 * @author M1046368 Arun Bose S
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, DecryptorImpl.class })
@WebMvcTest
public class KeyManagerTest {

	/** The env. */
	@Autowired
	private Environment env;

	/** The decryptor. */
	@Autowired
	private DecryptorImpl decryptor;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The rest helper. */
	@Mock
	private RestHelper restHelper;

	/** The rest request factory. */
	@Mock
	private RestRequestFactory restRequestFactory;

	/** The key manager. */
	@InjectMocks
	private KeyManager keyManager;

	@Autowired
	private Environment environment;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		// ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(keyManager, "keySplitter", "#KEY_SPLITTER#");
		ReflectionTestUtils.setField(keyManager, "environment", environment);
	}

	/**
	 * Request data test.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException    the ID data validation exception
	 * @throws JsonProcessingException      the json processing exception
	 */
	@Ignore
	@Test
	public void requestDataTest()
			throws IdAuthenticationAppException, IDDataValidationException, JsonProcessingException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		CryptomanagerResponseDto cryptoResponse = new CryptomanagerResponseDto(
				Base64.encodeBase64URLSafeString(("{\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\"}").getBytes()));
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(cryptoResponse);
		assertEquals(new String(Base64.decodeBase64(cryptoResponse.getData())),
				mapper.writeValueAsString(keyManager.requestData(reqMap, mapper)));

	}

	/**
	 * Request data mapper test.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException    the ID data validation exception
	 * @throws JsonProcessingException      the json processing exception
	 */
	@Ignore
	@Test(expected = IdAuthenticationAppException.class)
	public void requestDataMapperTest()
			throws IdAuthenticationAppException, IDDataValidationException, JsonProcessingException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		CryptomanagerResponseDto cryptoResponse = new CryptomanagerResponseDto(
				Base64.encodeBase64URLSafeString(("dfdfdfgdfgf").getBytes()));
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(cryptoResponse);
		keyManager.requestData(reqMap, mapper);

	}

	/**
	 * Request invalid data test 1.
	 *
	 * @throws IDDataValidationException    the ID data validation exception
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	
	@Test(expected = IdAuthenticationAppException.class)
	public void requestInvalidDataTest1() throws IDDataValidationException, IdAuthenticationAppException {
		Map<String, Object> reqMap = createRequest();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED,"publickey expired"));
		keyManager.requestData(reqMap, mapper);
	} 

	
	
	/**
	 * Request invalid data test 2.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IOException                  Signals that an I/O exception has
	 *                                      occurred.
	 * @throws IDDataValidationException    the ID data validation exception
	 */
	
	@Test(expected = IdAuthenticationAppException.class)
	public void requestInvalidDataTest2() throws IdAuthenticationAppException, IOException, IDDataValidationException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE));
		keyManager.requestData(reqMap, mapper);
	}
	
	
	@Test(expected = IdAuthenticationAppException.class)
	public void invalidKernelKeyManagerErrorRequest() throws IdAuthenticationAppException, IOException, IDDataValidationException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		String kernelErrorMapStr="{\r\n" + 
				"	\"errors\": [{\r\n" + 
				"			\"errCode\": \"KER-KMS-003\"\r\n" + 
				"		}\r\n" + 
				"\r\n" + 
				"	]\r\n" + 
				"\r\n" + 
				"}";
		RestServiceException restException=new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,kernelErrorMapStr,new ObjectMapper().readValue(kernelErrorMapStr.getBytes("UTF-8"), Map.class));
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(restException);
		keyManager.requestData(reqMap, mapper);
	}
	
	
	@Test(expected = IdAuthenticationAppException.class)
	public void invalidKernelErrorRequest() throws IdAuthenticationAppException, IOException, IDDataValidationException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		String kernelErrorMapStr="{\r\n" + 
				"	\"errors\": [{\r\n" + 
				"			\"errCode\": \"KER-KMS-004\"\r\n" + 
				"		}\r\n" + 
				"\r\n" + 
				"	]\r\n" + 
				"\r\n" + 
				"}";
		RestServiceException restException=new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,kernelErrorMapStr,new ObjectMapper().readValue(kernelErrorMapStr.getBytes("UTF-8"), Map.class));
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(restException);
		keyManager.requestData(reqMap, mapper);
	}
	
	/*@Test(expected = IdAuthenticationAppException.class)
	public void TestTspIdisNullorEmpty() throws IdAuthenticationAppException {
		Map<String, Object> requestBody =
		 new HashMap<>();
		keyManager.requestData(requestBody, mapper);
	}
	
	@Ignore
	@Test(expected = IdAuthenticationAppException.class)
	public void TestTspIdisNull() throws IdAuthenticationAppException {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("tspID", null);
		keyManager.requestData(requestBody, mapper);
	}*/
	
	/*@Test(expected = IdAuthenticationAppException.class)
	public void TestTspIdisEmpty() throws IdAuthenticationAppException {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("tspID", "");
		keyManager.requestData(requestBody, mapper);
	}*/

	// ====================================================================
	// ********************** Helper Method *******************************
	// ====================================================================

	/**
	 * Creates the request.
	 *
	 * @return the map
	 */
	private Map<String, Object> createRequest() {
		String data = "{\r\n" + 
				"	\"id\": \"mosip.identity.auth\",\r\n" + 
				"	\"individualId\": \"2410478395\",\r\n" + 
				"	\"individualIdType\": \"D\",\r\n" + 
				"	\"request\": \"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\r\n" + 
				"	\"requestTime\": \"2019-03-13T10:01:57.086+05:30\",\r\n" + 
				"	\"requestedAuth\": {\r\n" + 
				"		\"bio\": false,\r\n" + 
				"		\"demo\": true,\r\n" + 
				"		\"otp\": false,\r\n" + 
				"		\"pin\": false\r\n" + 
				"	},\r\n" + 
				"	\"requestSessionKey\": \"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\r\n" + 
				"	\"transactionID\": \"1234567890\",\r\n" + 
				"	\"version\": \"1.0\",\r\n" + 
				"	\"partnerId\": \"1873299273\",\r\n" + 
				"	\"licenseKey\": \"1873299273\"\r\n" + 
				"}" + 
				"}";
		Map<String, Object> readValue = null;
		try {
			readValue = mapper.readValue(data, new TypeReference<Map<String, Object>>() {
			});
			readValue.put("request", Base64.decodeBase64((String) readValue.get("request")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readValue;
	}

	/**
	 * Creates the response.
	 *
	 * @return the map
	 */
	private Map<String, Object> createResponse() {
		String data = "{\\r\\n\\tidentity = {\\r\\n\\t\\tleftIndex = [{\\r\\n\\t\\t\\tvalue = Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT + oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN \\/ QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI + oZECLAG0FZAAA\\r\\n\\t\\t}]\\r\\n\\t}\\r\\n}";
		Map<String, Object> readValue = null;
		try {
			readValue = mapper.readValue(data, new TypeReference<Map<String, Object>>() {
			});
			readValue.put("request", Base64.decodeBase64((String) readValue.get("request")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readValue;
	}

	/**
	 * Gets the rest request DTO.
	 *
	 * @return the rest request DTO
	 */
	private RestRequestDTO getRestRequestDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8089/cryptomanager/v1.0/decrypt");
		CryptomanagerRequestDto cryptomanagerRqt = new CryptomanagerRequestDto();
		restRequestDTO.setRequestBody(cryptomanagerRqt);
		restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

}
