package com.test.demo;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.demo.authentication.service.dto.EncryptionRequestDto;
import io.mosip.demo.authentication.service.impl.indauth.controller.OldEncrypt;


/**
 * @author Arun Bose S
 * The Class OldEncryptTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OldEncryptTest {
	
	/** The old encrypt mock. */
	@InjectMocks
	private OldEncrypt oldEncryptMock;
	
	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(oldEncryptMock, "objMapper", objectMapper);
	}
	
	/**
	 * Old encrypt test.
	 *
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws JsonProcessingException the json processing exception
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void oldEncryptTest() throws NoSuchAlgorithmException, JsonProcessingException, InvalidKeySpecException, IOException {
		EncryptionRequestDto encryptionRequest=new EncryptionRequestDto();
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
		encryptionRequest.setIdentityRequest(new ObjectMapper().readValue(testData.getBytes(), Map.class));
		oldEncryptMock.oldEncrypt(encryptionRequest);
		
	}

}
