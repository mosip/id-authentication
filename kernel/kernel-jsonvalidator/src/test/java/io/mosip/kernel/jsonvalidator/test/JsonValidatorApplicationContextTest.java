package io.mosip.kernel.jsonvalidator.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.jsonvalidator.impl.JsonSchemaLoader;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;

/**
 * @author Manoj SP
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonLoader.class, JsonSchemaFactory.class })
public class JsonValidatorApplicationContextTest {

	@InjectMocks
	JsonSchemaLoader schemaLoader;

	@InjectMocks
	JsonValidatorImpl jsonValidator;

	@Before
	public void before() throws IOException {
		JsonNode schema = JsonLoader.fromResource("/schema.json");
		PowerMockito.mockStatic(JsonLoader.class);
		PowerMockito.when(JsonLoader.fromString(Mockito.any())).thenReturn(new ObjectMapper().createObjectNode());
		PowerMockito.when(JsonLoader.fromURL(Mockito.any())).thenReturn(schema);
		PowerMockito.when(JsonLoader.class.getResource(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(JsonLoader.class.getClassLoader()).thenCallRealMethod();
		PowerMockito.when(JsonLoader.fromResource(Mockito.anyString())).thenCallRealMethod();
		ReflectionTestUtils.setField(schemaLoader, "configServerFileStorageURL", "http://1.1.1.1:51000/");
		ReflectionTestUtils.setField(schemaLoader, "schemaName", "schemaName");
		ReflectionTestUtils.setField(schemaLoader, "propertySource", "APPLICATION_CONTEXT");
		ReflectionTestUtils.setField(jsonValidator, "propertySource", "APPLICATION_CONTEXT");
		ReflectionTestUtils.setField(jsonValidator, "configServerFileStorageURL", "http://1.1.1.1:51000/");
		ReflectionTestUtils.setField(jsonValidator, "schemaName", "schemaName");
		ReflectionTestUtils.setField(jsonValidator, "schemaLoader", schemaLoader);
	}

	@Test
	public void testSchemaLoader() throws IOException, JsonSchemaIOException {
		JsonLoader.fromString("");
		schemaLoader.loadSchema();
		JsonNode schema = JsonLoader.fromResource("/schema.json");
		assertTrue(schema.equals(schemaLoader.getSchema()));
	}

	@Test(expected = JsonSchemaIOException.class)
	public void testSchemaLoaderError() throws IOException, JsonSchemaIOException {
		ReflectionTestUtils.setField(schemaLoader, "configServerFileStorageURL", "");
		JsonLoader.fromString("");
		schemaLoader.loadSchema();
	}

	@Test
	public void testValidateJson() throws JsonValidationProcessingException, JsonIOException, JsonSchemaIOException,
			FileIOException, IOException {
		testSchemaLoader();
		JsonLoader.fromString("");
		JsonNode jsonString = JsonLoader.fromResource("/valid-json.json");
		jsonValidator.validateJson(jsonString.toString());
	}

	@Test
	public void testValidateJsonInvalidJson() throws JsonValidationProcessingException, JsonIOException,
			JsonSchemaIOException, FileIOException, IOException {
		testSchemaLoader();
		JsonLoader.fromString("");
		jsonValidator.validateJson("{}");
	}

	@Test(expected = JsonValidationProcessingException.class)
	public void testJsonValidationFailure() throws ProcessingException, JsonValidationProcessingException,
			JsonIOException, JsonSchemaIOException, FileIOException, IOException {
		JsonLoader.fromString("");
		schemaLoader.loadSchema();
		PowerMockito.mockStatic(JsonSchemaFactory.class);
		JsonSchemaFactory mockFactory = PowerMockito.mock(JsonSchemaFactory.class);
		PowerMockito.when(JsonSchemaFactory.byDefault()).thenReturn(mockFactory);
		PowerMockito.when(mockFactory.getJsonSchema(Mockito.any(JsonNode.class)))
				.thenThrow(new JsonReferenceException(new ProcessingMessage().setMessage("")));
		jsonValidator.validateJson("{}");
	}
}