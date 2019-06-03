package io.mosip.kernel.jsonvalidator.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;

import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.core.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.jsonvalidator.impl.JsonSchemaLoader;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;

/**
 * 
 * @author Swati Raj
 * @author Manoj SP
 * @since 1.0.0
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonLoader.class })
public class JsonValidatorFromConfigServerTest {

	String propertySourceString = "propertySource";
	String configServerFileStorageURLString = "configServerFileStorageURL";

	@InjectMocks
	JsonValidatorImpl jsonValidator;

	@Mock
	JsonSchemaLoader schemaLoader;

	@Before
	public void before() throws IOException {
		MockitoAnnotations.initMocks(this);
		JsonNode schema = JsonLoader.fromResource("/schema.json");
		PowerMockito.mockStatic(JsonLoader.class);
		PowerMockito.when(JsonLoader.fromString(Mockito.any())).thenReturn(new ObjectMapper().createObjectNode());
		PowerMockito.when(JsonLoader.fromURL(Mockito.any())).thenReturn(schema);
		PowerMockito.when(JsonLoader.class.getResource(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(JsonLoader.class.getClassLoader()).thenCallRealMethod();
		PowerMockito.when(JsonLoader.fromResource(Mockito.anyString())).thenCallRealMethod();
		ReflectionTestUtils.setField(jsonValidator, "propertySource", "CONFIG_SERVER");
		ReflectionTestUtils.setField(jsonValidator, "configServerFileStorageURL", "http://1.1.1.1:51000/");
		ReflectionTestUtils.setField(jsonValidator, "schemaName", "schemaName");
	}

	@Test
	public void testWhenValidJsonProvided() throws HttpRequestException, JsonValidationProcessingException, IOException,
			JsonIOException, JsonSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		JsonLoader.fromString("");
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
		JsonLoader.fromString("");
		jsonValidator.validateJson(jsonString);
	}

	@Test(expected = JsonSchemaIOException.class)
	public void testForNullJsonSchemaSyntax() throws HttpRequestException, JsonValidationProcessingException,
			JsonIOException, JsonSchemaIOException, FileIOException, IOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		JsonLoader.fromString("");
		PowerMockito.when(JsonLoader.fromURL(Mockito.any())).thenThrow(new FileNotFoundException(""));
		jsonValidator.validateJson(jsonString);
	}

}