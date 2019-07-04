package io.mosip.registration.processor.core.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;

/**
 * The Class JsonUtilTest.
 * 
 * @author Ranjitha
 * 
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class JsonUtilTest {

	/** The input stream. */
	private InputStream inputStream;

	/** The input string. */
	private String inputString;

	/** The expected. */
	private String expectedResult = "{registrationId=1001, langCode=eng, createdBy=mosip_system}";
	
	private String value;

	
	JSONObject demoJson = new JSONObject();
	/**
	 * Setup.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	@Before
	public void setup() throws IOException, ClassNotFoundException {
		 value = "{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"authentication_bio_CBEFF\"}}}";
		inputString = "{\"registrationId\":\"1001\",\"langCode\":\"eng\",\"createdBy\":\"mosip_system\"}";
		inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Input streamto java object test.
	 *
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	@Test
	public void inputStreamtoJavaObjectTest() throws UnsupportedEncodingException {
		Object result = JsonUtil.inputStreamtoJavaObject(inputStream, Object.class);
		assertEquals("Coversion of input stream to java object. Expected value is Java Object", expectedResult,
				result.toString());

	}

	/**
	 * Input streamto java object failure test.
	 *
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	@Test(expected = UnsupportedEncodingException.class)
	public void inputStreamtoJavaObjectFailureTest() throws UnsupportedEncodingException {
		JsonUtil.inputStreamtoJavaObject(inputStream, null);
	}
	
	
	@Test
	public void testGetJsonValues() throws IOException {

		JSONObject result = JsonUtil.objectMapperReadValue(inputString,JSONObject.class);
		Object result1 =JsonUtil.getJSONValue(result, "registrationId");
		
		
		assertEquals("Coversion of input stream to java object. Expected value is Java Object","1001",
				result1.toString());

	}
	
	

}
