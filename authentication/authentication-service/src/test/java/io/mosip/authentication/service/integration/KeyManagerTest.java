package io.mosip.authentication.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.CryptomanagerRequestDto;
import io.mosip.authentication.service.integration.dto.CryptomanagerResponseDto;
import io.mosip.authentication.service.integration.dto.OTPValidateResponseDTO;
import io.mosip.authentication.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

// 
/**
 * The Class KeyManagerTest which covers KeyManager 
 * 
 * @author M1046368 Arun Bose S
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, DecryptorImpl.class})
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
	
	

	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		//ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(keyManager, "keySplitter", "#KEY_SPLITTER#");
	}
	
	
	
	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {

		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/otpmanager/otps"),
				request -> ServerResponse.status(HttpStatus.OK).body(Mono.just(new CryptomanagerResponseDto()),
						CryptomanagerResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		HttpServer.create(8089).start(adapter);

		System.err.println("started server");

	}
	
	
	
	/**
	 * Request data test.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException the ID data validation exception
	 * @throws JsonProcessingException the json processing exception
	 */
	@Test
	public void requestDataTest() throws IdAuthenticationAppException, IDDataValidationException, JsonProcessingException {
        Map<String,Object> reqMap=createRequest();
        RestRequestDTO restRequestDTO = getRestRequestDTO();
        CryptomanagerResponseDto cryptoResponse=new CryptomanagerResponseDto(Base64.encodeBase64URLSafeString(("{\"data\":\"value\"}").getBytes()));
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(cryptoResponse);
		assertEquals(new String(Base64.decodeBase64(cryptoResponse.getData())), mapper.writeValueAsString(keyManager.requestData(reqMap,mapper)));
		
	}
	
	/**
	 * Request data mapper test.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException the ID data validation exception
	 * @throws JsonProcessingException the json processing exception
	 */
	@Test(expected=IdAuthenticationAppException.class)
	public void requestDataMapperTest() throws IdAuthenticationAppException, IDDataValidationException, JsonProcessingException {
        Map<String,Object> reqMap=createRequest();
        RestRequestDTO restRequestDTO = getRestRequestDTO();
        CryptomanagerResponseDto cryptoResponse=new CryptomanagerResponseDto(Base64.encodeBase64URLSafeString(("dfdfdfgdfgf").getBytes()));
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(cryptoResponse);
		keyManager.requestData(reqMap,mapper);
		
	}
	
	/**
	 * Request invalid data test 1.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Test(expected=IdAuthenticationAppException.class)
	public void requestInvalidDataTest1() throws IDDataValidationException, IdAuthenticationAppException {
	 Map<String,Object> reqMap=createRequest();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IDDataValidationException());
		keyManager.requestData(reqMap,mapper);	
	}
	
	/**
	 * Request invalid data test 2.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected=IdAuthenticationAppException.class)
	public void requestInvalidDataTest2() throws IdAuthenticationAppException, IOException, IDDataValidationException {
		 Map<String,Object> reqMap=createRequest();
	        RestRequestDTO restRequestDTO = getRestRequestDTO();
	        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restRequestDTO);
			Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE));
			keyManager.requestData(reqMap,mapper);
	}
	
	
	
	
	// ====================================================================
	// ********************** Helper Method *******************************
	// ====================================================================
	
	/**
	 * Creates the request.
	 *
	 * @return the map
	 */
	private  Map<String, Object> createRequest(){
		String data ="{\r\n" + 
				"  \"authType\": {\r\n" + 
				"    \"address\": false,\r\n" + 
				"    \"bio\": false,\r\n" + 
				"    \"fullAddress\": false,\r\n" + 
				"    \"otp\": false,\r\n" + 
				"    \"personalIdentity\": true,\r\n" + 
				"    \"pin\": false\r\n" + 
				"  },\r\n" + 
				"  \r\n" + 
				"  \"id\": \"mosip.identity.auth\",\r\n" + 
				"  \"idvId\": \"927463875317\",\r\n" + 
				"  \"idvIdType\": \"D\",\r\n" + 
				"\"tspID\":\"REF01\",\r\n" + 
				"   \"key\": {\r\n" + 
				"    \"sessionKey\": \"D5jp00eDp5UzS4WDRuXuOAwpKFefHxR-oef60z81qdYKyvIRScIKL7ohjp3vlz-Z2BUQ050sLvZGdMOsVfANAyFaM9MXk3tw9ZZIrx2X14aeWzsWNgn9w8RObvcEcJBHktFAy9kKqHKwgisnQ0E5zMUAeREx46rYMHOmH_Av4mlJa5HewDBGGmlRsdFMIXWK7lCPwd78ivkf48_07mGHkMJ5SlwiCXD3XHz7ZyHAuP6vGV5K5k1S_fpgCh-vdWSDExHRSgQkxPP5Lvr6hdok4MQgJQL-Yu-Kc7ISnZSpYBVe6m8Qzdc2VkfBuEMlNleyRjcBw_L6q4rAubfec3P9bw\"\r\n" + 
				"  },\r\n" + 
				"  \"muaCode\": \"1234567890\",\r\n" + 
				" \r\n" + 
				"  \"reqHmac\": \"string\",\r\n" + 
				"  \"reqTime\": \"2018-12-26T12:13:49.105+05:30\",\r\n" + 
				"  \"request\": \"4wbb7RnXJTtqdrjy6ckYilLJkiX_BPokPdzIBR4S7uBAC1bkIa71DbKIiiIPc3Cyi9-06VNwGTBMYpHcPaAKTZQ3218pKHuC7T6_9iRQ1pEJBeKYkIHl9bODY1oFMY6o\",\r\n" + 
				"  \"txnID\": \"1234567890\",\r\n" + 
				"  \"ver\": \"1.0\"\r\n" + 
				"}";
		Map<String, Object> readValue=null;
		try {
			readValue = mapper.readValue(
					data,
					new TypeReference<Map<String, Object>>() {
					});
			readValue.put("request",Base64.decodeBase64( (String) readValue.get("request")));
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
	private Map<String, Object> createResponse(){
		String data ="{\\r\\n\\tidentity = {\\r\\n\\t\\tleftIndex = [{\\r\\n\\t\\t\\tvalue = Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT + oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN \\/ QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI + oZECLAG0FZAAA\\r\\n\\t\\t}]\\r\\n\\t}\\r\\n}";
		
		Map<String, Object> readValue=null;
		try {
			readValue = mapper.readValue(
					data,
					new TypeReference<Map<String, Object>>() {
					});
			readValue.put("request",Base64.decodeBase64((String) readValue.get("request")));
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
			CryptomanagerRequestDto cryptomanagerRqt=new CryptomanagerRequestDto();
			restRequestDTO.setRequestBody(cryptomanagerRqt);
			restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
			restRequestDTO.setTimeout(23);
			return restRequestDTO;
		}

}
