package io.mosip.kernel.idobjectvalidator.test;

import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.ID_OBJECT_VALIDATION_FAILED;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.MISSING_INPUT_PARAMETER;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.APPLICATION_ID;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.FIELD_LIST;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
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
 * @author Manoj SP
 * @author Swati Raj
 * @since 1.0.0
 *
 */
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class IdObjectValidatorLocalTest {

	@InjectMocks
	IdObjectSchemaValidator validator;

	@Before
	public void setup() {
		ReflectionTestUtils.setField(validator, "propertySource", "LOCAL");
		ReflectionTestUtils.setField(validator, "schemaName", "schemaV1.json");
		MockEnvironment env = new MockEnvironment();
		env.setProperty(APPLICATION_ID.getValue(), "reg-client");
		env.setProperty(String.format(FIELD_LIST.getValue(), "reg-client", "new-registration"), "firstName");
		ReflectionTestUtils.setField(validator, "env", env);
		ReflectionTestUtils.setField(validator, "mapper", new ObjectMapper());
	}

	@Test
	public void testWhenValidJsonProvided() throws IdObjectValidationFailedException, IOException, IdObjectIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		assertEquals(true,
				validator.validateIdObject(jsonSchemaNode, IdObjectValidatorSupportedOperations.NEW_REGISTRATION));
	}

	@Test
	public void testForEmptyJsonString() throws IdObjectValidationFailedException, IdObjectIOException {
		try {
			validator.validateIdObject("", IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectValidationFailedException e) {
			e.getErrorCode().equals(MISSING_INPUT_PARAMETER.getErrorCode());
			e.getErrorTexts().get(0).equals(String.format(MISSING_INPUT_PARAMETER.getMessage(), "identity/firstName"));
		}

	}

	@Test
	public void testForinvalidSchemaFile() throws IdObjectValidationFailedException, IdObjectIOException, IOException {
		try {
			JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
			String jsonString = jsonSchemaNode.toString();
			ReflectionTestUtils.setField(validator, "schemaName", "");
			validator.validateIdObject(jsonString, IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectIOException e) {
			e.getErrorCode().equals(ID_OBJECT_VALIDATION_FAILED.getErrorCode());
			e.getErrorText().equals(ID_OBJECT_VALIDATION_FAILED.getMessage());
		}
	}

}