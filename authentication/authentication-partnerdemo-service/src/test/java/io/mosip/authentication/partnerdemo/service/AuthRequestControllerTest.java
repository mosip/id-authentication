package io.mosip.authentication.partnerdemo.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

import org.json.JSONException;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.partnerdemo.service.controller.AuthRequestController;
import io.mosip.authentication.partnerdemo.service.controller.Encrypt;
import io.mosip.authentication.partnerdemo.service.dto.EncryptionResponseDto;
import io.mosip.kernel.templatemanager.velocity.impl.TemplateManagerImpl;

//
/**
 * The Class AuthRequestControllerTest tests the creation of auth request
 * 
 * @author Arun Bose S
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthRequestControllerTest {
	
	
	private final class ServletInputStreamExtension extends ServletInputStream {
		private final ByteArrayInputStream bais;

		private ServletInputStreamExtension(ByteArrayInputStream bais) {
			this.bais = bais;
		}

		@Override
		public int read() throws IOException {
			return bais.read();
		}

		@Override
		public void setReadListener(ReadListener listener) {
			
		}

		@Override
		public boolean isReady() {
			return bais.available() != 0;
		}

		@Override
		public boolean isFinished() {
			return bais.available() == 0;
		}
	}
	
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private ObjectMapper mapper;
	
	@InjectMocks
	private AuthRequestController authReqController;
	
	@Mock
	private Encrypt encrypt;
	
	@Mock
	private TemplateManagerImpl templateManager;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(authReqController, "environment", environment);
		ReflectionTestUtils.setField(authReqController, "mapper", mapper);
		ReflectionTestUtils.setField(authReqController, "templateManager", templateManager);
		ReflectionTestUtils.setField(authReqController, "templateManager", templateManager);
	}

	/**
	 * Auth controller test.
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IdAuthenticationBusinessException 
	 * @throws IdAuthenticationAppException 
	 * @throws InvalidKeyException 
	 * @throws KeyManagementException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void authControllerTest() throws KeyManagementException, InvalidKeyException, IdAuthenticationAppException, IdAuthenticationBusinessException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, JSONException {
		EncryptionResponseDto encryptionResponse=new EncryptionResponseDto();
		Mockito.when(encrypt.encrypt(Mockito.any(), Mockito.anyBoolean())).thenReturn(encryptionResponse);
		String reqData="{\r\n" + 
				"		\"biometrics\": [{\r\n" + 
				"				\"data\": {\r\n" + 
				"					\"bioSubType\": \"UNKNOWN\",\r\n" + 
				"					\"bioType\": \"IIR\",\r\n" + 
				"					\"bioValue\": \"LEFT\",\r\n" + 
				"					\"deviceCode\": \"cogent\",\r\n" + 
				"					\"deviceProviderID\": \"cogent\",\r\n" + 
				"					\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"					\"transactionID\": \"1234567890\"\r\n" + 
				"				}\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"data\": {\r\n" + 
				"					\"bioSubType\": \"UNKNOWN\",\r\n" + 
				"					\"bioType\": \"IIR\",\r\n" + 
				"					\"bioValue\": \"RIGHT\",\r\n" + 
				"					\"deviceCode\": \"cogent\",\r\n" + 
				"					\"deviceProviderID\": \"cogent\",\r\n" + 
				"					\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"					\"transactionID\": \"1234567890\"\r\n" + 
				"				}\r\n" + 
				"			}\r\n" + 
				"		],\r\n" + 
				"		\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"		\"transactionID\": \"1234567890\"\r\n" + 
				"	}";
		Map<String,Object> reqMap=mapper.readValue(reqData.getBytes(StandardCharsets.UTF_8), Map.class);
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any())).thenReturn(newServletInputStream());
		authReqController.createAuthRequest("1234567890", "UIN", false, false, null, null, reqMap)	;
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void kycControllerTest() throws KeyManagementException, InvalidKeyException, IdAuthenticationAppException, IdAuthenticationBusinessException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, JSONException {
		EncryptionResponseDto encryptionResponse=new EncryptionResponseDto();
		Mockito.when(encrypt.encrypt(Mockito.any(), Mockito.anyBoolean())).thenReturn(encryptionResponse);
		String reqData="{\r\n" + 
				"		\"biometrics\": [{\r\n" + 
				"				\"data\": {\r\n" + 
				"					\"bioSubType\": \"UNKNOWN\",\r\n" + 
				"					\"bioType\": \"IIR\",\r\n" + 
				"					\"bioValue\": \"LEFT\",\r\n" + 
				"					\"deviceCode\": \"cogent\",\r\n" + 
				"					\"deviceProviderID\": \"cogent\",\r\n" + 
				"					\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"					\"transactionID\": \"1234567890\"\r\n" + 
				"				}\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"data\": {\r\n" + 
				"					\"bioSubType\": \"UNKNOWN\",\r\n" + 
				"					\"bioType\": \"IIR\",\r\n" + 
				"					\"bioValue\": \"RIGHT\",\r\n" + 
				"					\"deviceCode\": \"cogent\",\r\n" + 
				"					\"deviceProviderID\": \"cogent\",\r\n" + 
				"					\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"					\"transactionID\": \"1234567890\"\r\n" + 
				"				}\r\n" + 
				"			}\r\n" + 
				"		],\r\n" + 
				"		\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"		\"transactionID\": \"1234567890\"\r\n" + 
				"	}";
		Map<String,Object> reqMap=mapper.readValue(reqData.getBytes(StandardCharsets.UTF_8), Map.class);
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any())).thenReturn(newServletInputStream());
		authReqController.createAuthRequest("1234567890", "UIN", false, false, null, null, reqMap)	;
		
		
	}
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void authControllerAuthTypeTest() throws KeyManagementException, InvalidKeyException, IdAuthenticationAppException, IdAuthenticationBusinessException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, JSONException {
		EncryptionResponseDto encryptionResponse=new EncryptionResponseDto();
		Mockito.when(encrypt.encrypt(Mockito.any(), Mockito.anyBoolean())).thenReturn(encryptionResponse);
		String reqData="{\r\n" + 
				"		\"biometrics\": [{\r\n" + 
				"				\"data\": {\r\n" + 
				"					\"bioSubType\": \"UNKNOWN\",\r\n" + 
				"					\"bioType\": \"IIR\",\r\n" + 
				"					\"bioValue\": \"LEFT\",\r\n" + 
				"					\"deviceCode\": \"cogent\",\r\n" + 
				"					\"deviceProviderID\": \"cogent\",\r\n" + 
				"					\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"					\"transactionID\": \"1234567890\"\r\n" + 
				"				}\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"data\": {\r\n" + 
				"					\"bioSubType\": \"UNKNOWN\",\r\n" + 
				"					\"bioType\": \"IIR\",\r\n" + 
				"					\"bioValue\": \"RIGHT\",\r\n" + 
				"					\"deviceCode\": \"cogent\",\r\n" + 
				"					\"deviceProviderID\": \"cogent\",\r\n" + 
				"					\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"					\"transactionID\": \"1234567890\"\r\n" + 
				"				}\r\n" + 
				"			}\r\n" + 
				"		],\r\n" + 
				"		\"timestamp\": \"2019-03-27T10:01:57.086+05:30\",\r\n" + 
				"		\"transactionID\": \"1234567890\"\r\n" + 
				"	}";
		Map<String,Object> reqMap=mapper.readValue(reqData.getBytes(StandardCharsets.UTF_8), Map.class);
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any())).thenReturn(newServletInputStream());
		authReqController.createAuthRequest("1234567890", "UIN", true, false, "bio,otp,demo,pin", null, reqMap)	;
	}
	
	@Ignore
	@SuppressWarnings("unchecked")
	@Test(expected=IdAuthenticationBusinessException.class)
	public void noRequest() throws KeyManagementException, InvalidKeyException, IdAuthenticationAppException, IdAuthenticationBusinessException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, JSONException {
		EncryptionResponseDto encryptionResponse=new EncryptionResponseDto();
		Mockito.when(encrypt.encrypt(Mockito.any(), Mockito.anyBoolean())).thenReturn(encryptionResponse);
		String reqData="{}";
		Map<String,Object> reqMap=mapper.readValue(reqData.getBytes(StandardCharsets.UTF_8), Map.class);
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any())).thenReturn(newServletInputStream());
		authReqController.createAuthRequest("1234567890", "UIN", false, false, "bio", null, reqMap)	;
	}
	
	
	
	
	
	
	private ServletInputStreamExtension newServletInputStream() {
		String req = "{\r\n" + 
				"  \"consentObtained\": true,\r\n" + 
				"  \"id\": \"mosip.identity.auth\",\r\n" + 
				"  \"individualId\": \"6521768301\",\r\n" + 
				"  \"individualIdType\": \"UIN\",\r\n" + 
				"  \"request\": \"7sBCLgrrSNhB-6vNR_MKtzv0wgjaKzB6HCnf1mx0u0YH0IiUL0PaUDOqaQi1RuN4Ar0yAlhJsIJIW-uxWHNxFEoD6h-r75pzKyBoyefMdpLHboLbljUBrPHoqrJLfv1nkas9RS-fD6M5S4e0D5YbZ5c9toNpb4thjj-QbHkEsOKQsVv7R7g59wMzMYx49NHSWnYnB4Dphsks-EGdCkHYR6IUR_Ys4OB1aZTGwfHrnXw8iWRhkrkv2hJA7QkpA9TGG_I9_dQT3amIWB3cDISlW0tKnaG7EVMApuPkeKO5LaxwKhE6_0RWCCvr9LSzVl6b8l8tZ8zybcRnrNS22nGAxytuwkx_BeEFdqnGrht0Q7y23pMmjn5JPYN3mnfQbuENYXKU5f5LGslUZm48ouqa1_-oPktSIq6K0PrTEKGlSukkmitqBXApuSbS1VJJ6uqhpudMnWB7QyO37XDPfMqsJKQueGbdLObdq_wT81-c5PT3h1QhFBe16N5Sf3qg0AgzRxzZ6T3kNiFIy7CkLZD9B7dKCmuL4nV-ixSyjzNQUALvEbDeatSF2-wjlTVVewtwcYzByCJbet2agYqfG0hdiTR5LbyclO8E67E1k73pJT07OgBihlXb9oIw0ljIZo_9smN_shoJUfxy1f8UUKJVWg\",\r\n" + 
				"  \"requestHMAC\": \"OPDtwdW7bHnQ3d7T8pQURjIyNYBt3hkE8ZbY7H1cBFtufkjYKd9rpcE7w57hZg6xUGsN2QZM8PtbEUjWShjrZIezI7nmGnlPS2lhIE1F6pw4Nl9AJ3oabrN4D7sULeVs\",\r\n" + 
				"  \"requestSessionKey\": \"AmbpO_u3WC83w4emk0f8G4RDdwIhk8KablUfBw6OVy_3a4UrHxx8TE7mpHgRMp-M7V01qAoYguO3XwWKFQfWHTZNaVuXYMkf3biNHWQiPm4UKob3MZjhyLUXaacy0MPFj8OIRYoRcQ5uwtpknzUC6qyjfyh9CdGlvvREyCgjSOFaBalFW3pFPkeZK7J7BfHv0OF1BoncE5Z4ITgUFAwyCAHBp3RrU0Oy6DqJXsm5I2ICuZKqYzE-bCK85Gw2eq-ECllQzmTvE34ILWpAOgmcUwie62rJnNtGJTzFoLCuzOxo7qtJBpi2ida4rzd1_jWVWGwe2cW4l2jGSEtDMve-Og\",\r\n" + 
				"  \"requestTime\": \"2019-03-28T10:01:57.086+05:30\",\r\n" + 
				"  \"requestedAuth\": {\r\n" + 
				"    \"bio\": true,\r\n" + 
				"    \"demo\": false,\r\n" + 
				"    \"otp\": false,\r\n" + 
				"    \"pin\": false\r\n" + 
				"  },\r\n" + 
				"  \"transactionID\": \"1234567890\",\r\n" + 
				"  \"version\": \"0.9\"\r\n" + 
				"}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		return new ServletInputStreamExtension(bais);
	}

}
