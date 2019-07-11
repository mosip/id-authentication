package io.mosip.kernel.idobjectvalidator.test;

import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.*;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.*;

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
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;

/**
 * @author Manoj SP
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonLoader.class, JsonSchemaFactory.class })
@ActiveProfiles("test")
public class IdObjectValidatorApplicationContextTest {

	MockEnvironment env = new MockEnvironment();

	@InjectMocks
	IdObjectSchemaValidator validator;

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
		ReflectionTestUtils.setField(validator, "propertySource", "APPLICATION_CONTEXT");
		ReflectionTestUtils.setField(validator, "configServerFileStorageURL", "http://1.1.1.1/");
		ReflectionTestUtils.setField(validator, "schemaName", "schemaName");
		env.setProperty(APPLICATION_ID.getValue(), "reg-client");
		env.setProperty(String.format(FIELD_LIST.getValue(), "reg-client", "new-registration"), "firstName");
		ReflectionTestUtils.setField(validator, "env", env);
		ReflectionTestUtils.setField(validator, "mapper", mapper);
	}

	@Test
	public void testSchemaLoader() throws IOException, IdObjectIOException {
		ReflectionTestUtils.invokeMethod(validator, "loadSchema");
	}

	@Test
	public void testSchemaLoaderError() throws Throwable {
		try {
			PowerMockito.when(JsonLoader.fromURL(Mockito.any(URL.class))).thenThrow(new IOException(""));
			ReflectionTestUtils.invokeMethod(validator, "loadSchema");
		} catch (UndeclaredThrowableException e) {
			IdObjectIOException ex = (IdObjectIOException) e.getCause();
			ex.getErrorCode().equals(SCHEMA_IO_EXCEPTION.getErrorCode());
			ex.getErrorText().equals(SCHEMA_IO_EXCEPTION.getMessage());
		}
	}

	@Test
	public void testValidateJson()
			throws IdObjectValidationFailedException, IdObjectIOException, IdObjectIOException, IOException {
		testSchemaLoader();
		JsonLoader.fromString("");
		JsonNode jsonString = JsonLoader.fromResource("/valid-json.json");
		validator.validateIdObject(mapper.readValue(jsonString.toString(), Object.class),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
	}

	@Test
	public void testValidateJsonInvalidJson()
			throws IdObjectValidationFailedException, IdObjectIOException, IdObjectIOException, IOException {
		try {
			testSchemaLoader();
			JsonLoader.fromString("");
			validator.validateIdObject(mapper.readValue("{}", Object.class),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectValidationFailedException e) {
			e.getErrorCode().equals(ID_OBJECT_VALIDATION_FAILED.getErrorCode());
			e.getErrorText().equals(ID_OBJECT_VALIDATION_FAILED.getMessage());
		}
	}

	@Test
	public void testJsonValidationFailure() throws ProcessingException, IdObjectValidationFailedException,
			IdObjectIOException, IdObjectIOException, IOException {
		try {
			JsonLoader.fromString("");
			ReflectionTestUtils.invokeMethod(validator, "loadSchema");
			PowerMockito.mockStatic(JsonSchemaFactory.class);
			JsonSchemaFactory mockFactory = PowerMockito.mock(JsonSchemaFactory.class);
			PowerMockito.when(JsonSchemaFactory.byDefault()).thenReturn(mockFactory);
			PowerMockito.when(mockFactory.getJsonSchema(Mockito.any(JsonNode.class)))
					.thenThrow(new JsonReferenceException(new ProcessingMessage().setMessage("")));
			validator.validateIdObject(mapper.readValue("{}", Object.class),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectIOException e) {
			e.getErrorCode().equals(ID_OBJECT_PARSING_FAILED.getErrorCode());
			e.getErrorText().equals(ID_OBJECT_PARSING_FAILED.getMessage());
		}
	}
}