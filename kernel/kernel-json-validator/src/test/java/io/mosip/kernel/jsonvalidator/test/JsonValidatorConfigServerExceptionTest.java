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
import io.mosip.kernel.jsonvalidator.dto.JsonValidatorResponseDto;
import io.mosip.kernel.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.jsonvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.jsonvalidator.exception.NullJsonSchemaException;
import io.mosip.kernel.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.jsonvalidator.validator.JsonValidator;

/**
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class JsonValidatorConfigServerExceptionTest {

	@InjectMocks
	JsonValidator jsonValidator;
	
	@Before
	public void setup() {
	    ReflectionTestUtils.setField(jsonValidator, "configServerFileStorageURL", "http://104.211.212.28:51000/*/default/DEV/");
	    ReflectionTestUtils.setField(jsonValidator, "propertySource", "CONFIG_SERVER");
	}
	
	@Test
	public void testWhenValidJsonProvided()
			throws HttpRequestException, JsonValidationProcessingException, IOException, JsonIOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "mosip-identity-json-schema.json";
		JsonValidatorResponseDto validationResponse = jsonValidator.validateJson(jsonString, schemaName);
		Boolean isValid =  validationResponse.isValid();
		assertEquals(isValid, true);
	}

	@Test(expected = NullJsonNodeException.class)
	public void testForEmptyJsonString()
			throws JsonValidationProcessingException, HttpRequestException, JsonIOException, JsonSchemaIOException, FileIOException {
		String jsonString = "";
		String schemaName = "mosip-identity-json-schema.json";
		jsonValidator.validateJson(jsonString, schemaName);

	}

	@Test(expected = JsonIOException.class)
	public void testForinvalidJsonString()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		String jsonString = "{";
		String schemaName = "mosip-identity-json-schema.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}

	@Test(expected = HttpRequestException.class)
	public void testForInvalidSchemaFileName()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, IOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "some-random-schema.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}

	@Test(expected = UnidentifiedJsonException.class)
	public void testForUnidentifiedJson()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, IOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/invalid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "mosip-identity-json-schema.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}
	@Test(expected = NullJsonSchemaException.class)
	public void testForNullJsonSchemaSyntax()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException, IOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "kernel-json-validator-null-schema-for-testing.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}
	@Test(expected = JsonSchemaIOException.class)
	public void testForInvalidJsonSchemaSyntax()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException, IOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "kernel-json-validator-invalid-syntax-schema-for-testing.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}
	

}
