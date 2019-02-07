package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.service.integration.KeyManager;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@AutoConfigureMockMvc
public class IdAuthFilterTest {

	@Autowired
	private Environment env;

	@Autowired
	private ObjectMapper mapper;

	IdAuthFilter filter = new IdAuthFilter();

	Map<String, Object> requestBody = new HashMap<>();

	Map<String, Object> responseBody = new HashMap<>();

	@Before
	public void before() {
		ReflectionTestUtils.setField(filter, "mapper", mapper);
		ReflectionTestUtils.setField(filter, "env", env);
	}

	@Test
	public void testSetTxnId() throws IdAuthenticationAppException, ServletException {
		requestBody.put("txnId", null);
		responseBody.put("txnId", "1234");
		assertEquals(responseBody.toString(), filter.setResponseParam(requestBody, responseBody).toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDecodedRequest() throws IdAuthenticationAppException, ServletException, JsonParseException,
			JsonMappingException, IOException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(filter, "keyManager", keyManager);
		requestBody.put("request",
				"ew0KCSJhdXRoVHlwZSI6IHsNCgkJImFkZHJlc3MiOiAidHJ1ZSIsDQoJCSJiaW8iOiAidHJ1ZSIsDQoJCSJmYWNlIjogInRydWUiLA0KCQkiZmluZ2VycHJpbnQiOiAidHJ1ZSIsDQoJCSJmdWxsQWRkcmVzcyI6ICJ0cnVlIiwNCgkJImlyaXMiOiAidHJ1ZSIsDQoJCSJvdHAiOiAidHJ1ZSIsDQoJCSJwZXJzb25hbElkZW50aXR5IjogInRydWUiLA0KCQkicGluIjogInRydWUiDQoJfQ0KfQ==");
		responseBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		Mockito.when(keyManager.requestData(Mockito.any(), Mockito.any())).thenReturn(new ObjectMapper().readValue(
				"{\"authType\":{\"address\":\"true\",\"bio\":\"true\",\"face\":\"true\",\"fingerprint\":\"true\",\"fullAddress\":\"true\",\"iris\":\"true\",\"otp\":\"true\",\"personalIdentity\":\"true\",\"pin\":\"true\"}}"
						.getBytes(),
				Map.class));
		assertEquals(responseBody.toString(), filter.decodedRequest(requestBody).toString());
	}

	@Test
	public void testEncodedResponse() throws IdAuthenticationAppException, ServletException {
		/*requestBody.put("request",
				"e2F1dGhUeXBlPXthZGRyZXNzPXRydWUsIGJpbz10cnVlLCBmYWNlPXRydWUsIGZpbmdlcnByaW50PXRydWUsIGZ1bGxBZGRyZXNzPXRydWUsIGlyaXM9dHJ1ZSwgb3RwPXRydWUsIHBlcnNvbmFsSWRlbnRpdHk9dHJ1ZSwgcGluPXRydWV9fQ==");*/
		requestBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		responseBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		assertEquals(requestBody.toString(), filter.encodedResponse(responseBody).toString());
	}
}
