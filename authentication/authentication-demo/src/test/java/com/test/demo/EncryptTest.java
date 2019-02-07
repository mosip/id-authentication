package com.test.demo;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.demo.authentication.service.dto.EncryptionRequestDto;
import io.mosip.demo.authentication.service.dto.EncryptionResponseDto;
import io.mosip.demo.authentication.service.impl.indauth.controller.Encrypt;


/**
 * @author Arun Bose S
 * The Class EncryptTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class EncryptTest {
	
	
	
	/** The encrypt mock. */
	@InjectMocks
	private Encrypt encryptMock;
	
	/** The obj mapper. */
	@Autowired
	private ObjectMapper objMapper;
	
	/** The environment. */
	@Autowired
	private Environment environment;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(encryptMock, "encryptURL", environment.getProperty("mosip.kernel.encrypt-url"));
		ReflectionTestUtils.setField(encryptMock, "appID", environment.getProperty("application.id"));
		ReflectionTestUtils.setField(encryptMock, "keySplitter", environment.getProperty("mosip.kernel.data-key-splitter"));
		ReflectionTestUtils.setField(encryptMock, "objMapper", objMapper);
		
	}
	
	//@Mock
	

	/**
	 * Encrypt test.
	 *
	 * @throws KeyManagementException the key management exception
	 * @throws RestClientException the rest client exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JSONException the JSON exception
	 * @throws InvalidKeySpecException the invalid key spec exception
	 */
	@Test
	public void encryptTest() throws KeyManagementException, RestClientException, NoSuchAlgorithmException, IOException, JSONException, InvalidKeySpecException {
		String testData="{\r\n" + 
				"	\"identityRequest\": {\r\n" + 
				"		\"identity\": {\r\n" + 
				"			\"leftEye\": [{\r\n" + 
				"				\"value\": \"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA\"\r\n" + 
				"			}]\r\n" + 
				"		}\r\n" + 
				"	},\r\n" + 
				"	\"tspID\": \"TEMP\"\r\n" + 
				"}";
		//EncryptionResponseDto encryptionResponseDTO=new EncryptionResponseDto();
	 // testData=encryptMock.getEncryptedValue(testData,"TEMP");
		//ReflectionTestUtils.invokeMethod(encryptMock, "split", testData);
	 EncryptionRequestDto encryptionRequestDto=new EncryptionRequestDto();
	 encryptionRequestDto.setTspID("TEMP");
	 encryptionRequestDto.setIdentityRequest(new ObjectMapper().readValue(testData.getBytes(), Map.class));
		encryptMock.encrypt(encryptionRequestDto);
	}
	
	
	/**
	 * Old encrypt test.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 *//*
	@Test
	public void oldEncryptTest() throws JsonParseException, JsonMappingException, IOException   {
		String testData="{\r\n" + 
				"	\"identityRequest\": {\r\n" + 
				"		\"identity\": {\r\n" + 
				"			\"leftEye\": [{\r\n" + 
				"				\"value\": \"Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNUB9AMF0V4CBAKBBPEC0AL68ZIC4AKjNZEBiAJvWXUBPANPWNUDSAK7RUIC2AQIfZEDJAPMxPEByAGwPXYCpARYPZECfAFjoZECGAEv9ZEBEAFmtV0BpAUGNXUC/AUEESUCUAVIEPEC2AVNxPICcALWuZICuALm3ZECNAJqxQ0CUAI3GQ0CXAPghV0BVAKDOZEBfAPqHXUBDAKe/ZIB9AG3xXUDPAIbZUEBcAGYhZECIASgHXYBJAGAnV0DjAR4jG0DKATqJIUCGADGSZEDSAUYGIUAxAD+nV0CXAK+oSUBoALr6Q4CSAOuKXUCiAIvNZEC9AJzQZIBNALbTXUBBAL68V0CeAHDZZECwAHPaZEBRAPwHUIBHAHW2XUDXARAUDUC4AS4HZEDXAS0CQ0CYADL4ZECsAUzuPEBkACgRZAAA\"\r\n" + 
				"			}]\r\n" + 
				"		}\r\n" + 
				"	},\r\n" + 
				"	\"tspID\": \"TEMP\"\r\n" + 
				"}";
		//EncryptionResponseDto encryptionResponseDTO=new EncryptionResponseDto();
	 // testData=encryptMock.getEncryptedValue(testData,"TEMP");
		//ReflectionTestUtils.invokeMethod(encryptMock, "split", testData);
	 EncryptionRequestDto encryptionRequestDto=new EncryptionRequestDto();
	 encryptionRequestDto.setTspID("TEMP");
	 encryptionRequestDto.setIdentityRequest(new ObjectMapper().readValue(testData.getBytes(), Map.class));
	 ReflectionTestUtils.invokeMethod(encryptMock, "oldEncrypt", encryptionRequestDto);
	}
	*/
	/**
	 * Turnoff SSL check test.
	 *
	 * @throws KeyManagementException the key management exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	@Test
	public void turnoffSSLCheckTest() throws KeyManagementException, NoSuchAlgorithmException {
		Encrypt.turnOffSslChecking();
	}
}
