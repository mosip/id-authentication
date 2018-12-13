package io.mosip.authentication.service.integration;

import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, DecryptorImpl.class})
@WebMvcTest
public class KeyManagerTest {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private DecryptorImpl decryptor;
	
	@Autowired
	private ObjectMapper mapper;
	
	@InjectMocks
	private KeyManager keyManager;
	
	@Ignore
	@Test
	public void requestDataTest() throws IdAuthenticationAppException {
		assertNotEquals(keyManager.requestData(createRequest(), env, decryptor, mapper), createResponse());
		
		
	}
	
	private  Map<String, Object> createRequest(){
		String data ="{\r\n" + 
				"	\"authType\": {\r\n" + 
				"		\"address\": false,\r\n" + 
				"		\"bio\": true,\r\n" + 
				"        \"fullAddress\": false,\r\n" + 
				"		\"otp\": false,\r\n" + 
				"		\"personalIdentity\": false,\r\n" + 
				"		\"pin\": false\r\n" + 
				"	},\r\n" + 
				"	\"bioInfo\": [{\r\n" + 
				"		\"bioType\": \"fgrMin\",\r\n" + 
				"		\"deviceInfo\": {\r\n" + 
				"			\"deviceId\": \"123143\",\r\n" + 
				"			\"make\": \"Mantra\",\r\n" + 
				"			\"model\": \"steel\"\r\n" + 
				"		}\r\n" + 
				"	}],\r\n" + 
				"	\"id\": \"mosip.identity.auth\",\r\n" + 
				"	\"idvId\": \"312480672934\",\r\n" + 
				"	\"idvIdType\": \"D\",\r\n" + 
				"	\"reqTime\": \"2018-12-12T15:44:57.086+05:30\",\r\n" + 
				"	\"muaCode\": \"1234567890\",\r\n" + 
				"	\"tspID\":\"test\",\r\n" + 
				"	\"key\": {\r\n" + 
				"    \"sessionKey\": \"D4vr4nyAiAkSWRJ8Gu2v7NnOxyh3EGIef2RiDQ3I8qKCyNQB0+CJYOrNhKRsHjmapECTb/ZZw8gbMtIka3nvly/2TwczfTY1gYzKEeg5BwiwYe/OljRz7MWYwZp6ow5bvdc3iJlXdybZ6xXQrZLyM6C3f6f53Spyu7dKBn6vmCZbZ16RrE1TrZmAcZdoXdOe5OID9H7VDh7oCVvqI7DOvDVSOc4n4iDOSAkt4aSZDS2Sv+xCIInKBMBb7vcKW0EjHt8rhTOFq7aEOnYcWlhT6Nku8XcRWMDQ32blHnVI3652j5JueBTWZDrSncG4wKfXq3SMuxExKjffu6a6Q4ErSA==\"\r\n" + 
				"	},\r\n" + 
				"	\"request\": \"NMRqSbaHnuHPMd6woJjKpPkgJ5FQrpN2YfLPpScmja+Ext9bXc7XGfi4xonyCAyPGNfcFkSoBQl2PSOzEDGlNwy5EyJZvqYy73itTa2mu8+0IZrGFtIaiBEp/TSSeBT7NXJNyIJBDTfkjtevFLlU43ZoHtG2tewC2MOVsSzTyMjvCciS9TOGN7pHwft0L0zCBIq8DeqtPw4i4slDTZfDnl+p7qq0DcbowaGn7TFXMgYXYGr8UuWSx37EADYhyg5JyDX/xkZLIJDj/DN+WhQfaoZIQ7HticADiiJr0v2NS4iXT9r6HEOo+8DigPUnbPUPK55vkhUqw/wvzZQHcYbAZUREYC3K0DR05FQ3sZRLcoDi+y3+lCEUecbA6DC01aqBJ+ExqFiWtaakXdTc7WhHWzmWgbp+xYreqkloWsRoj7b1oUbuxou206E3IRmQLyCtOIaftWJPrygVQNE5lkKWfcObWzz0f1qbInVwWposvVFwbE9nZtxicud4L8fKl+1XPuCXV66AwT4ar3LWVF4EPrhM2E3QgAkSCzI2UsByT3k=\",\r\n" + 
				"	\"txnID\": \"1234567890\",\r\n" + 
				"	\"reqHmac\": \"string\",\r\n" + 
				"	\"ver\": \"1.0\"\r\n" + 
				"}";
		Map<String, Object> readValue=null;
		try {
			readValue = mapper.readValue(
					data,
					new TypeReference<Map<String, Object>>() {
					});
			readValue.put("request",Base64.getDecoder().decode((String) readValue.get("request")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readValue;
	}
	
	private Map<String, Object> createResponse(){
		String data ="{\\r\\n\\tidentity = {\\r\\n\\t\\tleftIndex = [{\\r\\n\\t\\t\\tvalue = Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT + oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN \\/ QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI + oZECLAG0FZAAA\\r\\n\\t\\t}]\\r\\n\\t}\\r\\n}";
		
		Map<String, Object> readValue=null;
		try {
			readValue = mapper.readValue(
					data,
					new TypeReference<Map<String, Object>>() {
					});
			readValue.put("request",Base64.getDecoder().decode((String) readValue.get("request")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readValue;
	}

}
