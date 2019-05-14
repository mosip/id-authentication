package io.mosip.kernel.jsonvalidator.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;
import io.mosip.kernel.core.idobjectvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.core.idobjectvalidator.exception.UnidentifiedJsonException;
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
	IdObjectSchemaValidator jsonValidator;

	@Before
	public void setup() {
		ReflectionTestUtils.setField(jsonValidator, "propertySource", "LOCAL");
		ReflectionTestUtils.setField(jsonValidator, "schemaName", "schemaV1.json");
	}

	@Test
	public void testWhenValidJsonProvided() throws HttpRequestException, IdObjectValidationProcessingException, IOException,
			IdObjectIOException, IdObjectSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		assertEquals(true, jsonValidator.validateIdObject(jsonSchemaNode));
	}

	@Test(expected = NullJsonNodeException.class)
	@Ignore
	public void testForEmptyJsonString() throws IdObjectValidationProcessingException, HttpRequestException,
			IdObjectIOException, IdObjectSchemaIOException, FileIOException {
		String jsonString = "";
		jsonValidator.validateIdObject(jsonString);

	}

	@Test(expected = IdObjectIOException.class)
	@Ignore
	public void testForinvalidJsonString() throws HttpRequestException, IdObjectValidationProcessingException,
			IdObjectIOException, IdObjectSchemaIOException, FileIOException {
		String jsonString = "{";
		jsonValidator.validateIdObject(jsonString);
	}

	@Test(expected = UnidentifiedJsonException.class)
	public void testForUnidentifiedJson() throws HttpRequestException, IdObjectValidationProcessingException,
			IdObjectIOException, IOException, IdObjectSchemaIOException, FileIOException {
		String jsonString = "";
		jsonValidator.validateIdObject(jsonString);
	}

	@Test(expected = FileIOException.class)
	public void testForinvalidSchemaFile() throws HttpRequestException, IdObjectValidationProcessingException,
			IdObjectIOException, IOException, IdObjectSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		ReflectionTestUtils.setField(jsonValidator, "schemaName", "");
		jsonValidator.validateIdObject(jsonString);
	}

}