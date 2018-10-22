package io.mosip.registration.processor.core.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * The Class JsonUtilTest.
 * 
 * @author Ranjitha
 * 
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class JsonUtilTest {

	/** The json util. */
	@InjectMocks
	JsonUtil jsonUtil = new JsonUtil();

	/** The input stream. */
	private InputStream inputStream;

	/** The input string. */
	private String inputString;

	/** The expected. */
	private String expectedResult = "{registrationId=1001, langCode=eng, createdBy=mosip_system}";

	/**
	 * Setup.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	@Before
	public void setup() throws IOException, ClassNotFoundException {

		inputString = "{\"registrationId\":\"1001\",\"langCode\":\"eng\",\"createdBy\":\"mosip_system\"}";
		inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Input streamto java object test.
	 *
	 * @throws UnsupportedEncodingException the unsupported encoding exception
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
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test(expected = UnsupportedEncodingException.class)
	public void inputStreamtoJavaObjectFailureTest() throws UnsupportedEncodingException {
	 JsonUtil.inputStreamtoJavaObject(inputStream, null);
	}

}
