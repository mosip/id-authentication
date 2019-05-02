package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
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
import io.mosip.authentication.common.service.integration.dto.CryptomanagerRequestDto;
import io.mosip.authentication.common.service.integration.dto.CryptomanagerResponseDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.kernel.core.http.ResponseWrapper;
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

	@Autowired
	private ObjectMapper mapper;

	/** The rest request factory. */
	@Mock
	private RestRequestFactory restRequestFactory;

	@Mock
	private RestHelper restHelper;

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
		ResponseWrapper<Map<String, Object>> symmetricKeyResponse = new ResponseWrapper<>();
		Map<String, Object> symKeyMap = new HashMap<>();
		symKeyMap.put("symmetricKey", "-CAj77ZNbtHjmCOSlPUsb4IgqnqHSv0MS5FeLMj2tDM");
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", symKeyMap);
		CryptomanagerResponseDto cryptoResponse = new CryptomanagerResponseDto(Base64.encodeBase64URLSafeString(
				("{\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\"}")
						.getBytes()));
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(responseMap);
		Map<String, Object> decryptedReqMap = keyManager.requestData(reqMap, mapper);
		assertTrue(decryptedReqMap.containsKey("secretKey"));
	}

	/**
	 * Request data mapper test.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException    the ID data validation exception
	 * @throws JsonProcessingException      the json processing exception
	 */

	@Test(expected = IdAuthenticationAppException.class)
	public void requestDataMapperTest()
			throws IdAuthenticationAppException, IDDataValidationException, JsonProcessingException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Map<String, Object> symKeyMap = new HashMap<>();
		symKeyMap.put("symmetricKey", "-CAj77ZNbtHjmCOSlPUsb4IgqnqHSv0MS5FeLMj");
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", symKeyMap);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(responseMap);
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
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(
				new IDDataValidationException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, "publickey expired"));
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
	public void invalidKernelKeyManagerErrorRequest()
			throws IdAuthenticationAppException, IOException, IDDataValidationException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		String kernelErrorMapStr = "{\r\n" + "	\"errors\": [{\r\n" + "			\"errCode\": \"KER-KMS-003\"\r\n"
				+ "		}\r\n" + "\r\n" + "	]\r\n" + "\r\n" + "}";
		RestServiceException restException = new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,
				kernelErrorMapStr, new ObjectMapper().readValue(kernelErrorMapStr.getBytes("UTF-8"), Map.class));
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restException);
		keyManager.requestData(reqMap, mapper);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void invalidKernelErrorRequest()
			throws IdAuthenticationAppException, IOException, IDDataValidationException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		String kernelErrorMapStr = "{\r\n" + "	\"errors\": [{\r\n" + "			\"errCode\": \"KER-KMS-004\"\r\n"
				+ "		}\r\n" + "\r\n" + "	]\r\n" + "\r\n" + "}";
		RestServiceException restException = new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR,
				kernelErrorMapStr, new ObjectMapper().readValue(kernelErrorMapStr.getBytes("UTF-8"), Map.class));
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restException);
		keyManager.requestData(reqMap, mapper);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void requestInvalidDataIOException() throws IDDataValidationException, IdAuthenticationAppException {
		Map<String, Object> reqMap = createRequest();
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Map<String, Object> symKeyMap = new HashMap<>();
		symKeyMap.put("symmetricKey", "-CAj77ZNbtHjmCOSlPUsb4IgqnqHSv0MS5FeLMj");
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("response", symKeyMap);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(responseMap);
		Mockito.when(keyManager.requestData(reqMap, mapper)).thenThrow(IOException.class);
	}

	/*
	 * @Test(expected = IdAuthenticationAppException.class) public void
	 * TestTspIdisNullorEmpty() throws IdAuthenticationAppException { Map<String,
	 * Object> requestBody = new HashMap<>(); keyManager.requestData(requestBody,
	 * mapper); }
	 * 
	 * @Ignore
	 * 
	 * @Test(expected = IdAuthenticationAppException.class) public void
	 * TestTspIdisNull() throws IdAuthenticationAppException { Map<String, Object>
	 * requestBody = new HashMap<>(); requestBody.put("tspID", null);
	 * keyManager.requestData(requestBody, mapper); }
	 */

	/*
	 * @Test(expected = IdAuthenticationAppException.class) public void
	 * TestTspIdisEmpty() throws IdAuthenticationAppException { Map<String, Object>
	 * requestBody = new HashMap<>(); requestBody.put("tspID", "");
	 * keyManager.requestData(requestBody, mapper); }
	 */

	// ====================================================================
	// ********************** Helper Method *******************************
	// ====================================================================

	/**
	 * Creates the request.
	 *
	 * @return the map
	 */
	private Map<String, Object> createRequest() {
		String data = "{\r\n" + "  \"consentObtained\": true,\r\n" + "  \"id\": \"mosip.identity.auth\",\r\n"
				+ "  \"individualId\": \"6892738569\",\r\n" + "  \"individualIdType\": \"UIN\",\r\n"
				+ "  \"keyIndex\": \"string\",\r\n"
				+ "  \"request\": \"iMw7w2duULAj3tgfJvPpKEmZ_KBdD1RruHE-jQlfssfHTl_pv8_6Ik_TlZbmcH2VyGWtC9sCxIYBoblXLZRkZIF4fErFg5VZgomvYCJ-RzQa78izAGkrehxuMfnXQcY3zaDObkSZPzlA7Law6sokohL6frRAnVcDdZ2kXxfjYgYyRR40CIqKtpuUXnEjBQbmiw8AguaBjTgK6r2pTTH9irfkONSvHjMDGbn6aTQFmGbuCJoOvCBR-qf2jaq9BpaAEXpNNTorr4XoS6Aen3hbdqYQHeyGm8yggmKTeiOZHVatEAaT8LVouYqlMVNV65eZKdmDQmZvY2f18aJu5RcU_XUlz0uvITtPkBIBpIbpVx7KGVIoe3iBKf6n9mmm3xjpGHMIipMZJZSdINMQx8t-pBXiAOr2fvrcR6rbvjLq24pzwBq6jkM3Zg1_QKbX9YwouraWKOhW79AZ8ZmVmDtYQr0DhQNypx-kNQrm2K8_BIjzP-Fvm3YhGudZ7yre1I05TwjNLCr-iLcEnOEl_KWAVNpfNLrotkWgwmaUbz2mZjkMbdGy5JtW5a0-yAg6_zZFiFWhCgOUEvaZ_226PZy1dZpElOrP3W-Mvc0ATJCc76e2F86JoNwpOA8SLpyYsd3j\",\r\n"
				+ "  \"requestHMAC\": \"TMXTCgmeuswzn2Ls2mWSz6ZXjiV8EWGmqfZoGHqKJEG0E__oDpBsoGjyEBzEK-GuXw70q8bWrPB7MoUuq2t3D7vV3r9X52B5mMezNH_xzsjrc6ruQrdWg-okg0nBasEs\",\r\n"
				+ "  \"requestSessionKey\": \"rBM6Ekp7xWwyzGgk1E4rgHRFUKUZCU3bAZG_wzRb35HI7tb-280bbTJYTBgik6U_LJIzXhTRmMRt2_sgPB4EmePsS4yC93HpyGF6uJ1yPoA6E--CKqHHaGx1z774moIH_sbB5TJw6fYQSiBGBABKFbrxSdj4FqLhejtIcyIxv5gRLN43Ye-WvIZNioHVOoBxCbRxgPCVESF8AHZK67S-SSy2jk8p-e-47g869C0yp6gYReo4dGIFs-1yD2bNSQC9b1mo-cC528W0iIt5j23PlJdELEj9a2oPI2PqIfU6zFtKEXqqYo_WmvpfA1Wu9r3yuY5MGp_C0F1nD0u_JIRjPw\",\r\n"
				+ "  \"requestTime\": \"2019-04-12T06:00:22.098Z\",\r\n" + "  \"requestedAuth\": {\r\n"
				+ "    \"bio\": true,\r\n" + "    \"demo\": false,\r\n" + "    \"otp\": false,\r\n"
				+ "    \"pin\": false\r\n" + "  },\r\n" + "  \"transactionID\": \"1234567890\",\r\n"
				+ "  \"version\": \"0.9\"\r\n" + "}" + "}";
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
