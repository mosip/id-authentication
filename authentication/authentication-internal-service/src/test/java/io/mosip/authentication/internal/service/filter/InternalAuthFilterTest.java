/*package io.mosip.authentication.internal.service.filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

import io.mosip.authentication.common.integration.KeyManager;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthFilterTest {

	@Autowired
	Environment env;

	InternalAuthFilter internalAuthFilter = new InternalAuthFilter();

	Map<String, Object> requestBody = new HashMap<>();

	Map<String, Object> responseBody = new HashMap<>();

	@Autowired
	ObjectMapper mapper;

	@Before
	public void before() {
		ReflectionTestUtils.setField(internalAuthFilter, "mapper", mapper);
		ReflectionTestUtils.setField(internalAuthFilter, "env", env);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValidDecodedRequest()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException,
			NoSuchMethodException, SecurityException, IdAuthenticationAppException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(internalAuthFilter, "keyManager", keyManager);
		requestBody.put("request",
				"ew0KCSJhdXRoVHlwZSI6IHsNCgkJImFkZHJlc3MiOiAidHJ1ZSIsDQoJCSJiaW8iOiAidHJ1ZSIsDQoJCSJmYWNlIjogInRydWUiLA0KCQkiZmluZ2VycHJpbnQiOiAidHJ1ZSIsDQoJCSJmdWxsQWRkcmVzcyI6ICJ0cnVlIiwNCgkJImlyaXMiOiAidHJ1ZSIsDQoJCSJvdHAiOiAidHJ1ZSIsDQoJCSJwZXJzb25hbElkZW50aXR5IjogInRydWUiLA0KCQkicGluIjogInRydWUiDQoJfQ0KfQ==");
		requestBody.put("requestHMAC", "B93ACCB8D7A0B005864F684FB1F53A833BAF547ED4D610C5057DE6B55A4EF76C");
		responseBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		Mockito.when(keyManager.requestData(Mockito.any(), Mockito.any())).thenReturn(new ObjectMapper().readValue(
				"{\"authType\":{\"address\":\"true\",\"bio\":\"true\",\"face\":\"true\",\"fingerprint\":\"true\",\"fullAddress\":\"true\",\"iris\":\"true\",\"otp\":\"true\",\"personalIdentity\":\"true\",\"pin\":\"true\"}}"
						.getBytes(),
				Map.class));
		Map<String, Object> decipherRequest = internalAuthFilter.decipherRequest(requestBody);
		decipherRequest.remove("requestHMAC");
		assertEquals(responseBody.toString(), decipherRequest.toString());

	}

	@Test
	public void testSign() throws IdAuthenticationAppException {
		assertEquals(true, internalAuthFilter.validateSignature("something", "something".getBytes()));
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testInValidDecodedRequest()
			throws IdAuthenticationAppException, JsonParseException, JsonMappingException, IOException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(internalAuthFilter, "keyManager", keyManager);
		requestBody.put("request", 123214214);
		internalAuthFilter.decipherRequest(requestBody);
	}

}
*/