package io.mosip.kernel.jsonvalidator.test;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.core.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;

/**
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class JsonValidatorLocalPropertySourceExceptionTest {
	
	String propertySourceString= "propertySource";
	
	@InjectMocks
	JsonValidatorImpl jsonValidator;

	@Before
	public void setup() {
		InputStream config = getClass().getClassLoader().getResourceAsStream("application-local.properties");
		Properties propObj = new Properties();
		try {
			propObj.load(config);
			String propertySource = propObj.getProperty("mosip.kernel.jsonvalidator.property-source");
			ReflectionTestUtils.setField(jsonValidator, propertySourceString, propertySource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testWhenValidJsonProvided()
			throws HttpRequestException, JsonValidationProcessingException, IOException, JsonIOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "schema.json";
		ValidationReport validationResponse = jsonValidator.validateJson(jsonString, schemaName);
		Boolean isValid =  validationResponse.isValid();
		assertEquals(true,isValid);
	}

	@Test(expected = NullJsonNodeException.class)
	public void testForEmptyJsonString()
			throws JsonValidationProcessingException, HttpRequestException, JsonIOException, JsonSchemaIOException, FileIOException {
		String jsonString = "";
		String schemaName = "schema.json";
		jsonValidator.validateJson(jsonString, schemaName);

	}

	@Test(expected = JsonIOException.class)
	public void testForinvalidJsonString()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		String jsonString = "{";
		String schemaName = "schema.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}

	@Test(expected = UnidentifiedJsonException.class)
	public void testForUnidentifiedJson()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, IOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/invalid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "schema.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}
	@Test(expected = FileIOException.class)
	public void testForinvalidSchemaFile()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, IOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "some-random-schema.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}
}
