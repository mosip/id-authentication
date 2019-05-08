package io.mosip.kernel.jsonvalidator.test;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;
import io.mosip.kernel.jsonvalidator.impl.IdObjectSchemaValidator;

/**
 * @author Manoj SP
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonLoader.class, JsonSchemaFactory.class })
@ActiveProfiles("test")
public class IdObjectValidatorApplicationContextTest {

	@InjectMocks
	IdObjectSchemaValidator jsonValidator;
	
	ObjectMapper mapper = new ObjectMapper();

	@Before
	public void before() throws IOException {
		JsonNode schema = JsonLoader.fromResource("/schemaV1.json");
		PowerMockito.mockStatic(JsonLoader.class);
		PowerMockito.when(JsonLoader.fromString(Mockito.any())).thenReturn(new ObjectMapper().createObjectNode());
		PowerMockito.when(JsonLoader.fromURL(Mockito.any(URL.class))).thenReturn(schema);
		PowerMockito.when(JsonLoader.class.getResource(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(JsonLoader.class.getClassLoader()).thenCallRealMethod();
		PowerMockito.when(JsonLoader.fromResource(Mockito.anyString())).thenCallRealMethod();
		ReflectionTestUtils.setField(jsonValidator, "propertySource", "APPLICATION_CONTEXT");
		ReflectionTestUtils.setField(jsonValidator, "configServerFileStorageURL", "http://1.1.1.1:51000/");
		ReflectionTestUtils.setField(jsonValidator, "schemaName", "schemaName");
	}

	@Test
	public void testSchemaLoader() throws IOException, IdObjectSchemaIOException {
		ReflectionTestUtils.invokeMethod(jsonValidator, "loadSchema");
	}
	
	@Test(expected = IdObjectSchemaIOException.class)
	public void testSchemaLoaderError() throws Throwable {
		try {
		PowerMockito.when(JsonLoader.fromURL(Mockito.any(URL.class))).thenThrow(new IOException(""));
		ReflectionTestUtils.invokeMethod(jsonValidator, "loadSchema");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test
	public void testValidateJson() throws IdObjectValidationProcessingException, IdObjectIOException, IdObjectSchemaIOException,
			FileIOException, IOException {
		testSchemaLoader();
		JsonLoader.fromString("");
		JsonNode jsonString = JsonLoader.fromResource("/valid-json.json");
		jsonValidator.validateIdObject(mapper.readValue(jsonString.toString(), Object.class));
	}

	@Test
	public void testValidateJsonInvalidJson() throws IdObjectValidationProcessingException, IdObjectIOException,
			IdObjectSchemaIOException, FileIOException, IOException {
		testSchemaLoader();
		JsonLoader.fromString("");
		jsonValidator.validateIdObject(mapper.readValue("{}", Object.class));
	}

	@Test(expected = IdObjectValidationProcessingException.class)
	public void testJsonValidationFailure() throws ProcessingException, IdObjectValidationProcessingException,
			IdObjectIOException, IdObjectSchemaIOException, FileIOException, IOException {
		JsonLoader.fromString("");
		ReflectionTestUtils.invokeMethod(jsonValidator, "loadSchema");
		PowerMockito.mockStatic(JsonSchemaFactory.class);
		JsonSchemaFactory mockFactory = PowerMockito.mock(JsonSchemaFactory.class);
		PowerMockito.when(JsonSchemaFactory.byDefault()).thenReturn(mockFactory);
		PowerMockito.when(mockFactory.getJsonSchema(Mockito.any(JsonNode.class)))
				.thenThrow(new JsonReferenceException(new ProcessingMessage().setMessage("")));
		jsonValidator.validateIdObject(mapper.readValue("{}", Object.class));
	}
}