package io.mosip.kernel.jsonvalidator.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

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
public class JsonValidatorLocalTest {

	@InjectMocks
	JsonValidatorImpl jsonValidator;

	@Before
	public void setup() {
		ReflectionTestUtils.setField(jsonValidator, "propertySource", "LOCAL");
		ReflectionTestUtils.setField(jsonValidator, "schemaName", "schema.json");
	}

	@Test
	public void testWhenValidJsonProvided() throws HttpRequestException, JsonValidationProcessingException, IOException,
			JsonIOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		ValidationReport validationResponse = jsonValidator.validateJson(jsonString);
		Boolean isValid = validationResponse.isValid();
		assertEquals(true, isValid);
	}

	@Test(expected = NullJsonNodeException.class)
	public void testForEmptyJsonString() throws JsonValidationProcessingException, HttpRequestException,
			JsonIOException, JsonSchemaIOException, FileIOException {
		String jsonString = "";
		jsonValidator.validateJson(jsonString);

	}

	@Test(expected = JsonIOException.class)
	public void testForinvalidJsonString() throws HttpRequestException, JsonValidationProcessingException,
			JsonIOException, JsonSchemaIOException, FileIOException {
		String jsonString = "{";
		jsonValidator.validateJson(jsonString);
	}

	@Test(expected = UnidentifiedJsonException.class)
	public void testForUnidentifiedJson() throws HttpRequestException, JsonValidationProcessingException,
			JsonIOException, IOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/invalid-json.json");
		String jsonString = jsonSchemaNode.toString();
		jsonValidator.validateJson(jsonString);
	}

	@Test(expected = FileIOException.class)
	public void testForinvalidSchemaFile() throws HttpRequestException, JsonValidationProcessingException,
			JsonIOException, IOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		ReflectionTestUtils.setField(jsonValidator, "schemaName", "");
		jsonValidator.validateJson(jsonString);
	}

}