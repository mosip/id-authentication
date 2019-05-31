package io.mosip.kernel.idobjectvalidator.test;

import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.ID_OBJECT_VALIDATION_FAILED;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.SCHEMA_IO_EXCEPTION;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.APPLICATION_ID;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.FIELD_LIST;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;

/**
 * 
 * @author Swati Raj
 * @author Manoj SP
 * @since 1.0.0
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonLoader.class })
@ActiveProfiles("test")
public class IdObjectValidatorFromConfigServerTest {

	String propertySourceString = "propertySource";
	String configServerFileStorageURLString = "configServerFileStorageURL";

	@InjectMocks
	IdObjectSchemaValidator idValidator;

	@Before
	public void before() throws IOException {
		MockitoAnnotations.initMocks(this);
		JsonNode schema = JsonLoader.fromResource("/schemaV1.json");
		PowerMockito.mockStatic(JsonLoader.class);
		PowerMockito.when(JsonLoader.fromString(Mockito.any())).thenReturn(new ObjectMapper().createObjectNode());
		PowerMockito.when(JsonLoader.fromURL(Mockito.any())).thenReturn(schema);
		PowerMockito.when(JsonLoader.class.getResource(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(JsonLoader.class.getClassLoader()).thenCallRealMethod();
		PowerMockito.when(JsonLoader.fromResource(Mockito.anyString())).thenCallRealMethod();
		ReflectionTestUtils.setField(idValidator, "propertySource", "CONFIG_SERVER");
		ReflectionTestUtils.setField(idValidator, "configServerFileStorageURL", "http://1.1.1.1:51000/");
		ReflectionTestUtils.setField(idValidator, "schemaName", "schemaName");
		MockEnvironment env = new MockEnvironment();
		env.setProperty(APPLICATION_ID.getValue(), "reg-client");
		env.setProperty(String.format(FIELD_LIST.getValue(), "reg-client", "new-registration"), "firstName");
		ReflectionTestUtils.setField(idValidator, "env", env);
		ReflectionTestUtils.setField(idValidator, "mapper", new ObjectMapper());
	}

	@Test
	public void testWhenValidJsonProvided() throws IdObjectValidationFailedException, IOException, IdObjectIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		JsonLoader.fromString("");
		assertEquals(true,
				idValidator.validateIdObject(jsonSchemaNode, IdObjectValidatorSupportedOperations.NEW_REGISTRATION));
	}

	@Test
	public void testForEmptyJsonString() throws IdObjectValidationFailedException, IdObjectIOException {
		try {
			idValidator.validateIdObject(new ObjectMapper().createObjectNode(),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectValidationFailedException e) {
			e.getErrorCode().equals(ID_OBJECT_VALIDATION_FAILED.getErrorCode());
			e.getErrorText().equals(ID_OBJECT_VALIDATION_FAILED.getMessage());
		}

	}

	@Test
	public void testForNullJsonSchemaSyntax()
			throws IdObjectValidationFailedException, IdObjectIOException, IOException {
		try {
			PowerMockito.when(JsonLoader.fromURL(Mockito.any())).thenThrow(new FileNotFoundException(""));
			idValidator.validateIdObject(new ObjectMapper().createObjectNode(),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectIOException e) {
			e.getErrorCode().equals(SCHEMA_IO_EXCEPTION.getErrorCode());
			e.getErrorText().equals(SCHEMA_IO_EXCEPTION.getMessage());
		}
	}

}